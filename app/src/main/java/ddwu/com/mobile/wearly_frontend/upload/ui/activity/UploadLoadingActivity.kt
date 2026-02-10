package ddwu.com.mobile.wearly_frontend.upload.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.ViewPropertyAnimator
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.databinding.ActivityLoadingUploadBinding
import ddwu.com.mobile.wearly_frontend.databinding.StepItemBinding
import ddwu.com.mobile.wearly_frontend.upload.data.remote.ApiClient
import ddwu.com.mobile.wearly_frontend.upload.data.util.MultipartUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import kotlin.coroutines.resume

class UploadLoadingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoadingUploadBinding
    private lateinit var s1: StepItemBinding
    private lateinit var s2: StepItemBinding
    private lateinit var s3: StepItemBinding

    private val ACCESS_TOKEN = ""
    private val icons = listOf(
        R.drawable.ic_remove,
        R.drawable.ic_make,
        R.drawable.ic_make
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        s1 = binding.step1; s2 = binding.step2; s3 = binding.step3

        val photoUriStr = intent.getStringExtra("photoUri")
        val photoUri = photoUriStr?.let { Uri.parse(it) }
        if (photoUri == null) {
            finishWithResult("이미지 URI가 없습니다")
            return
        }

        // 초기 UI
        setStepState(s1, 1, "배경 제거", isActive = true, isCompleted = false)
        setStepState(s2, 2, "이미지 분석", isActive = false, isCompleted = false)
        setStepState(s3, 3, "카테고리 분류", isActive = false, isCompleted = false)
        animateStepFocus(s1, s2, s3)

        binding.ivSpinner.apply {
            scaleX = 1f
            scaleY = 1f
            alpha = 1f
            rotation = 0f
            setImageResource(icons[0])
        }

        lifecycleScope.launch {
            try {
                val api = ApiClient.createUploadApi {
                    ACCESS_TOKEN
                }

                crossFadeText(binding.tvTitle, "업로드 시작 중")
                crossFadeText(binding.tvSub, "서버에 이미지를 전송하고 있습니다")

                val startRes = api.startClothingUpload(
                    image = MultipartUtil.uriToMultipart(this@UploadLoadingActivity, photoUri),
                    sectionId = MultipartUtil.textPart("1")
                )

                if (!startRes.success || startRes.data == null) {
                    finishWithResult("업로드 시작 실패: ${startRes.error?.message}")
                    return@launch
                }

                val jobId = startRes.data.jobId

                withTimeout(35_000) {
                    val maxTry = 20
                    var tryCount = 0

                    while (isActive && tryCount < maxTry) {
                        delay(1500)
                        tryCount++

                        val statusRes = api.getClothingUploadStatus(jobId)
                        val data = statusRes.data

                        if (data == null) {
                            fallbackResult("data 없음 → 임시")
                            return@withTimeout
                        }

                        when (data.status) {
                            "processing" -> {
                                crossFadeText(binding.tvTitle, "처리 중")
                                crossFadeText(binding.tvSub, "AI가 이미지를 분석하고 있습니다")
                            }

                            "completed" -> {
                                val category = data.result?.clothing?.category
                                finishWithResult(category?.let { "${it}로 분류되었습니다" } ?: "업로드 완료")
                                return@withTimeout
                            }

                            "failed" -> {
                                fallbackResult(data.error ?: "failed")
                                return@withTimeout
                            }
                        }
                    }

                    fallbackResult("시도 횟수 초과 → 임시")
                }
            } catch (e: kotlinx.coroutines.TimeoutCancellationException) {

                fallbackResult("시간 초과(35초) → 임시 분류")
            } catch (e: HttpException) {
            when (e.code()) {
                401, 403 -> fallbackResult("권한 오류(${e.code()}) → 임시")
                429 -> fallbackResult("쿼터/레이트리밋(429) → 임시")
                else -> fallbackResult("서버 오류(${e.code()}) → 임시")
            }
            } catch (e: Exception) {
                fallbackResult("네트워크/기타 오류 → 임시 (${e.message})")
            }

        }

    }

    private fun crossFadeText(tv: TextView, newText: String, duration: Long = 250) {
        tv.animate().alpha(0f).setDuration(duration).withEndAction {
            tv.text = newText
            tv.animate().alpha(1f).setDuration(duration).start()
        }.start()
    }

    private suspend fun playCenterIconSwap(newIconRes: Int, total: Long = 600) {
        val iv = binding.ivSpinner
        iv.animate().cancel()

        val t1 = (total * 0.45f).toLong()
        val t2 = total - t1

        val a1 = iv.animate()
            .setInterpolator(DecelerateInterpolator())
            .scaleX(0f).scaleY(0f)
            .alpha(0f)
            .rotationBy(-180f)
            .setDuration(t1)

        suspendUntilEnd(a1)
        iv.setImageResource(newIconRes)

        val a2 = iv.animate()
            .setInterpolator(DecelerateInterpolator())
            .scaleX(1f).scaleY(1f)
            .alpha(1f)
            .rotationBy(-180f)
            .setDuration(t2)

        suspendUntilEnd(a2)
    }

    private suspend fun suspendUntilEnd(anim: ViewPropertyAnimator) =
        suspendCancellableCoroutine<Unit> { cont ->
            anim.withEndAction { if (cont.isActive) cont.resume(Unit) }
        }

    private fun finishWithResult(resultText: String) {
        android.util.Log.d("UPLOAD", "finishWithResult(): $resultText")
        val data = Intent().apply { putExtra("resultText", resultText) }
        setResult(Activity.RESULT_OK, data)
        finish()
    }


    private fun animateStepFocus(active: StepItemBinding, inactive1: StepItemBinding, inactive2: StepItemBinding) {
        fun scaleCircle(step: StepItemBinding, target: Float) {
            step.stepCircle.animate()
                .scaleX(target)
                .scaleY(target)
                .setDuration(250)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }
        scaleCircle(active, 1.18f)
        scaleCircle(inactive1, 0.92f)
        scaleCircle(inactive2, 0.92f)
    }

    private fun setStepState(
        step: StepItemBinding,
        number: Int,
        labelText: String,
        isActive: Boolean,
        isCompleted: Boolean,
        animateCheck: Boolean = false
    ) {
        step.tvLabel.text = labelText

        when {
            isCompleted -> {
                step.stepCircle.setCardBackgroundColor(Color.parseColor("#D8D3FF"))
                step.tvMark.setTextColor(Color.parseColor("#1B1652"))
                step.tvLabel.setTextColor(Color.parseColor("#1B1652"))
                step.tvMark.text = "✓"
                step.tvMark.alpha = 1f
            }

            isActive -> {
                step.stepCircle.setCardBackgroundColor(Color.parseColor("#D8D3FF"))
                step.tvMark.setTextColor(Color.parseColor("#1B1652"))
                step.tvLabel.setTextColor(Color.parseColor("#1B1652"))
                step.tvMark.text = number.toString()
                step.tvMark.alpha = 1f
            }

            else -> {
                step.stepCircle.setCardBackgroundColor(Color.parseColor("#EEEEEE"))
                step.tvMark.setTextColor(Color.parseColor("#777777"))
                step.tvLabel.setTextColor(Color.parseColor("#777777"))
                step.tvMark.text = number.toString()
                step.tvMark.alpha = 1f
            }
        }
    }

    private fun fallbackResult(reason: String? = null) {
        val msg = "임시 분류 완료(테스트)\n${reason ?: ""}".trim()
        android.util.Log.d("UPLOAD", "fallbackResult(): $msg")
        finishWithResult(msg)
    }


}
