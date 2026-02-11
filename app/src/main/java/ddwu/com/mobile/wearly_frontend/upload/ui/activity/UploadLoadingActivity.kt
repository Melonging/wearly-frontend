package ddwu.com.mobile.wearly_frontend.upload.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
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
import kotlin.random.Random

class UploadLoadingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoadingUploadBinding

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

    private val resultDeferred = CompletableDeferred<UploadResult>()

    data class UploadResult(
        val resultText: String,
        val clothingId: Long,
        val imageUrl: String?
    )

    private fun <T> CompletableDeferred<T>.getCompletedOrNull(): T? =
        if (isCompleted && !isCancelled) runCatching { getCompleted() }.getOrNull() else null


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

        s1 = binding.step1
        s2 = binding.step2
        s3 = binding.step3

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
        setStepState(s1, 1, stepLabels[0], isActive = true, isCompleted = false)
        setStepState(s2, 2, stepLabels[1], isActive = false, isCompleted = false)
        setStepState(s3, 3, stepLabels[2], isActive = false, isCompleted = false)
        animateStepFocus(active = s1, inactive1 = s2, inactive2 = s3)

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

            val late = withTimeoutOrNull(8000L) { resultDeferred.await() }
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
                val api = ApiClient.uploadApi()

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
                            withContext(Dispatchers.Main) { finishWithUploadResult(res) }
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
            } catch (e: Exception) {
                val res = UploadResult("임시 분류 완료(테스트)", -1L, null)
                if (!resultDeferred.isCompleted) resultDeferred.complete(res)
                finishWithUploadResult(res)
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

    private fun applyStep(step: Int) {
        when (step) {
            2 -> {
                setStepState(s1, 1, stepLabels[0], isActive = false, isCompleted = true)
                setStepState(s2, 2, stepLabels[1], isActive = true, isCompleted = false)
                animateStepFocus(active = s2, inactive1 = s1, inactive2 = s3)
            }
            3 -> {
                setStepState(s2, 2, stepLabels[1], isActive = false, isCompleted = true)
                setStepState(s3, 3, stepLabels[2], isActive = true, isCompleted = false)
                animateStepFocus(active = s3, inactive1 = s1, inactive2 = s2)
            }
        }

        crossFadeText(binding.tvTitle, stepTitles[step - 1])
        crossFadeText(binding.tvSub, stepSubs[step - 1])
        playCenterSwap(stepIcons[step - 1])
    }

    private fun crossFadeText(tv: TextView, newText: String, duration: Long = 220) {
        tv.animate().alpha(0f).setDuration(duration).withEndAction {
            tv.text = newText
            tv.animate().alpha(1f).setDuration(duration).start()
        }.start()
    }

    private fun playCenterSwap(newIconRes: Int) {
        val iv = binding.ivSpinner
        iv.animate().cancel()
        iv.animate()
            .setInterpolator(DecelerateInterpolator())
            .scaleX(0.05f).scaleY(0.05f)
            .alpha(0.2f)
            .setDuration(180)
            .withEndAction {
                iv.setImageResource(newIconRes)
                iv.animate()
                    .setInterpolator(DecelerateInterpolator())
                    .scaleX(1f).scaleY(1f)
                    .alpha(1f)
                    .setDuration(220)
                    .start()
            }
            .start()
    }

    private fun animateStepFocus(active: StepItemBinding, inactive1: StepItemBinding, inactive2: StepItemBinding) {
        fun scale(step: StepItemBinding, target: Float) {
            step.stepCircle.animate()
                .scaleX(target)
                .scaleY(target)
                .setDuration(220)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }
        scale(active, 1.18f)
        scale(inactive1, 0.92f)
        scale(inactive2, 0.92f)
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
