package ddwu.com.mobile.wearly_frontend.upload.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.databinding.ActivityLoadingUploadBinding
import ddwu.com.mobile.wearly_frontend.databinding.StepItemBinding
import ddwu.com.mobile.wearly_frontend.upload.data.remote.ApiClient
import ddwu.com.mobile.wearly_frontend.upload.data.util.MultipartUtil
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random

class UploadLoadingActivity : AppCompatActivity() {


    // viewBinding
    private lateinit var binding: ActivityLoadingUploadBinding

    // step

    private lateinit var s1: StepItemBinding
    private lateinit var s2: StepItemBinding
    private lateinit var s3: StepItemBinding

    private var pollingJob: Job? = null

    private companion object {
        private const val TOTAL_MS = 4500L
        private const val STEP_MS = 1500L

        // 폴링 간격 범위
        private const val POLL_MIN_MS = 350L
        private const val POLL_MAX_MS = 2500L

        // 처리중이 길어질 때 backoff
        private const val BACKOFF_MULT = 1.25

        // 랜덤 지터(동시 호출 분산)
        private const val JITTER_MS = 180L
    }

    // 결과
    private val resultDeferred = CompletableDeferred<UploadResult>()

    data class UploadResult(
        val resultText: String,
        val clothingId: Long,
        val imageUrl: String?
    )

    private fun <T> CompletableDeferred<T>.getCompletedOrNull(): T? =
        if (isCompleted && !isCancelled) runCatching { getCompleted() }.getOrNull() else null


    // step_UI

    private val stepTitles = listOf("배경 제거 중", "이미지 분석 중", "카테고리 분류 중")
    private val stepSubs = listOf(
        "AI가 의류 이미지에서 배경을 제거하고 있습니다",
        "의류의 특징과 스타일을 분석하고 있습니다",
        "적합한 카테고리를 찾고 있습니다"
    )
    private val stepLabels = listOf("배경 제거", "이미지 분석", "카테고리 분류")

    private val stepIcons = listOf(
        R.drawable.ic_remove,
        R.drawable.ic_make,
        R.drawable.ic_make
    )

    //  API 결과 저장
    @Volatile private var resultText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoadingUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // step 바인딩

        s1 = StepItemBinding.bind(findViewById(R.id.step1))
        s2 = StepItemBinding.bind(findViewById(R.id.step2))
        s3 = StepItemBinding.bind(findViewById(R.id.step3))

        val photoUriStr = intent.getStringExtra("photoUri")
        val photoUri = photoUriStr?.let(Uri::parse)
        if (photoUri == null) {
            finishWithResult("이미지 URI가 없습니다")
            return
        }

        val sectionId = intent.getIntExtra("sectionId", -1)
        if (sectionId == -1) {
            finishWithResult("sectionId가 없습니다")
            return
        }

        //  초기 UI
        setStepState(s1, 1, stepLabels[0], isActive = true,  isCompleted = false)
        setStepState(s2, 2, stepLabels[1], isActive = false, isCompleted = false)
        setStepState(s3, 3, stepLabels[2], isActive = false, isCompleted = false)


        scaleStep(s1, 1.18f, duration = 0)
        scaleStep(s2, 1.0f, duration = 0)
        scaleStep(s3, 1.0f, duration = 0)

        binding.ivSpinner.apply {
            scaleX = 1f
            scaleY = 1f
            alpha = 1f
            rotation = 0f
            setImageResource(stepIcons[0])
        }

        crossFadeText(binding.tvTitle, stepTitles[0])
        crossFadeText(binding.tvSub, stepSubs[0])

        //  1) 업로드+폴링은 백그라운드로 시작
        startUploadAndPolling(photoUri, sectionId)

        lifecycleScope.launch {
            delay(STEP_MS)
            applyStep(2)

            delay(STEP_MS)
            applyStep(3)

            // 4.5초 지점: 결과 이미 왔으면 즉시 종료
            resultDeferred.getCompletedOrNull()?.let {
                finishWithUploadResult(it)
                return@launch
            }

            val late = withTimeoutOrNull(50_000L) { resultDeferred.await() }
            finishWithUploadResult(late ?: UploadResult("임시 분류 완료(테스트)", -1L, null))
        }
    }

    private fun finishWithUploadResult(res: UploadResult) {
        pollingJob?.cancel()

        val data = Intent().apply {
            putExtra("resultText", res.resultText)
            putExtra("clothingId", res.clothingId)
            putExtra("imageUrl", res.imageUrl ?: "")
        }
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun startUploadAndPolling(photoUri: Uri, sectionId: Int) {
        pollingJob?.cancel()
        pollingJob = lifecycleScope.launch {
            try {
                val api = ApiClient.uploadApi(context = this@UploadLoadingActivity)

                // 1) 업로드 시작
                val startRes = api.startClothingUpload(
                    image = MultipartUtil.uriToMultipart(this@UploadLoadingActivity, photoUri),
                    sectionId = MultipartUtil.textPart(sectionId.toString())
                )

                if (!startRes.success || startRes.data == null) {
                    resultText = "업로드 시작 실패"
                    return@launch
                }

                val jobId = startRes.data.jobId

                // 2) status 폴링 (응답시간 기반)
                var nextDelayMs = 450L
                var processingStreak = 0

                while (isActive) {
                    delay(nextDelayMs)

                    val t0 = SystemClock.elapsedRealtime()
                    val statusRes = api.getClothingUploadStatus(jobId)
                    val rtt = SystemClock.elapsedRealtime() - t0

                    val data = statusRes.data
                    if (data == null) {
                        processingStreak++
                        nextDelayMs = computeNextDelayMs(rtt, processingStreak, nextDelayMs)
                        continue
                    }

                    val status = data.status.lowercase()

                    when (status) {
                        "completed", "success", "done" -> {
                            val clothing = data.result?.clothing

                            val categoryName = clothing?.category_name
                            val res = UploadResult(
                                resultText = categoryName?.let { "${it}로 분류되었습니다" } ?: "분류 완료",
                                clothingId = clothing?.clothing_id ?: -1L,
                                imageUrl = clothing?.image
                            )

                            if (!resultDeferred.isCompleted) resultDeferred.complete(res)
                            return@launch
                        }

                        "failed", "error" -> {
                            val res = UploadResult(
                                resultText = data.error ?: "업로드 실패",
                                clothingId = -1L,
                                imageUrl = null
                            )
                            if (!resultDeferred.isCompleted) resultDeferred.complete(res)
                            withContext(Dispatchers.Main) { finishWithUploadResult(res) }
                            return@launch
                        }

                        else -> {
                            // processing 계속
                            processingStreak++
                            nextDelayMs = computeNextDelayMs(rtt, processingStreak, nextDelayMs)
                        }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("UPLOAD_ERROR", "폴링 예외", e)
                val res = UploadResult("임시 분류 완료(테스트)", -1L, null)
                if (!resultDeferred.isCompleted) resultDeferred.complete(res)
            }
        }
    }

    private fun computeNextDelayMs(
        rttMs: Long,
        processingStreak: Int,
        prevDelayMs: Long
    ): Long {
        val base = (rttMs * 2.2).toLong().coerceIn(POLL_MIN_MS, POLL_MAX_MS)

        val backedOff = (base * Math.pow(BACKOFF_MULT, (processingStreak - 1).toDouble()))
            .toLong()
            .coerceAtMost(POLL_MAX_MS)

        val smooth = (prevDelayMs * 0.35 + backedOff * 0.65).toLong()

        // 지터 추가
        val jitter = Random.nextLong(0, JITTER_MS)

        return (smooth + jitter).coerceIn(POLL_MIN_MS, POLL_MAX_MS)
    }

    private suspend fun applyStep(step: Int) {
        when (step) {
            2 -> {
                // 1) s1: 숫자→체크 디졸브 먼저
                setStepState(s1, 1, stepLabels[0], isActive = true, isCompleted = false)
                dissolveMarkToCheck(s1, 1)         // 숫자 -> ✓
                delay(360)

                // 2) s1: 완료 스타일 적용 (이때 tvMark는 이미 ✓)
                setStepState(s1, 1, stepLabels[0], isActive = false, isCompleted = true)

                // 3) s1: 기본 크기(1.0)로 복귀
                scaleStep(s1, 1.0f, duration = 220)
                delay(220)

                // 4) s2: 커짐 + 활성
                setStepState(s2, 2, stepLabels[1], isActive = true, isCompleted = false)
                scaleStep(s2, 1.18f, duration = 240)

                swapText(binding.tvTitle, stepTitles[1])
                swapText(binding.tvSub, stepSubs[1])
                playCenterSwap(stepIcons[1])
            }

            3 -> {
                // s2: 숫자→체크 먼저
                setStepState(s2, 2, stepLabels[1], isActive = true, isCompleted = false)
                dissolveMarkToCheck(s2, 2)
                delay(360)

                // 완료 스타일
                setStepState(s2, 2, stepLabels[1], isActive = false, isCompleted = true)

                // 기본 크기 복귀
                scaleStep(s2, 1.0f, duration = 220)
                delay(220)

                // s3 커짐 + 활성
                setStepState(s3, 3, stepLabels[2], isActive = true, isCompleted = false)
                scaleStep(s3, 1.18f, duration = 240)

                // s1 완료 유지 + 기본 크기
                setStepState(s1, 1, stepLabels[0], isActive = false, isCompleted = true)
                scaleStep(s1, 1.0f, duration = 0)

                swapText(binding.tvTitle, stepTitles[2])
                swapText(binding.tvSub, stepSubs[2])
                playCenterSwap(stepIcons[2])
            }
        }
    }

    private fun crossFadeText(tv: TextView, newText: String, duration: Long = 220) {
        tv.animate().alpha(0f).setDuration(duration).withEndAction {
            tv.text = newText
            tv.animate().alpha(1f).setDuration(duration).start()
        }.start()
    }

    private fun swapText(tv: TextView, newText: String, fadeInMs: Long = 180) {
        if (tv.text == newText) return
        tv.animate().cancel()

        tv.alpha = 0.0f
        tv.text = newText
        tv.animate().alpha(1f).setDuration(fadeInMs).start()
    }

    private fun dissolveMarkToCheck(step: StepItemBinding, number: Int) {
        val tv = step.tvMark
        tv.animate().cancel()

        // 숫자 상태 보장
        tv.text = number.toString()
        tv.alpha = 1f

        // 1) 사라짐
        tv.animate()
            .alpha(0f)
            .setDuration(160)
            .withEndAction {
                // 2) 체크로 교체 후 다시 나타남
                tv.text = "✓"
                tv.animate().alpha(1f).setDuration(180).start()
            }
            .start()
    }

    private fun scaleStep(
        step: StepItemBinding,
        target: Float,
        duration: Long = 220,
        end: (() -> Unit)? = null
    ) {
        step.stepCircle.animate().cancel()
        step.stepCircle.animate()
            .scaleX(target)
            .scaleY(target)
            .setDuration(duration)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction { end?.invoke() }
            .start()
    }

    private fun playCenterSwap(newIconRes: Int) {
        val iv = binding.ivSpinner
        iv.animate().cancel()
        iv.animate()
            .setInterpolator(DecelerateInterpolator())
            .rotationBy(-140f)
            .scaleX(0.05f).scaleY(0.05f)
            .alpha(0.2f)
            .setDuration(360)
            .withEndAction {
                iv.setImageResource(newIconRes)
                iv.animate()
                    .setInterpolator(DecelerateInterpolator())
                    .rotationBy(140f)
                    .scaleX(1f).scaleY(1f)
                    .alpha(1f)
                    .setDuration(360)
                    .start()
            }
            .start()
    }

    private fun setStepState(
        step: StepItemBinding,
        number: Int,
        labelText: String,
        isActive: Boolean,
        isCompleted: Boolean
    ) {
        step.tvLabel.text = labelText

        when {
            isCompleted -> {
                step.stepCircle.setCardBackgroundColor(Color.parseColor("#D8D3FF"))
                step.tvMark.setTextColor(Color.parseColor("#1B1652"))
                step.tvLabel.setTextColor(Color.parseColor("#1B1652"))
                step.tvMark.text = "✓"
            }

            isActive -> {
                step.stepCircle.setCardBackgroundColor(Color.parseColor("#D8D3FF"))
                step.tvMark.setTextColor(Color.parseColor("#1B1652"))
                step.tvLabel.setTextColor(Color.parseColor("#1B1652"))
                step.tvMark.text = number.toString()
            }

            else -> {
                step.stepCircle.setCardBackgroundColor(Color.parseColor("#EEEEEE"))
                step.tvMark.setTextColor(Color.parseColor("#777777"))
                step.tvLabel.setTextColor(Color.parseColor("#777777"))
                step.tvMark.text = number.toString()
            }
        }
    }

    private fun finishWithResult(msg: String) {
        val data = Intent().apply { putExtra("resultText", msg) }
        setResult(Activity.RESULT_OK, data)
        finish()
    }
}
