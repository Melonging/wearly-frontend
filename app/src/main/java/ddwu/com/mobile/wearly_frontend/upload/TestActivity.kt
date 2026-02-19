package ddwu.com.mobile.wearly_frontend.upload

import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.databinding.ActivityTestBinding
import ddwu.com.mobile.wearly_frontend.upload.data.remote.ApiClient
import ddwu.com.mobile.wearly_frontend.upload.data.util.MultipartUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TestActivity : AppCompatActivity() {
    lateinit var binding: ActivityTestBinding
    private val ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsImxvZ2luSWQiOiJ0ZXN0dXNlcjEiLCJ0eXBlIjoiYWNjZXNzIiwiaWF0IjoxNzcwNjQ2MTU2LCJleHAiOjE3NzA2NDk3NTZ9.ETGGSF9mmghxXnjfkc0Cl0FQEYKfip836OUpxuVh3-w"
    private var pickedUri: Uri? = null

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        pickedUri = uri
        binding.textView.text = if (uri != null) "이미지 선택됨: $uri\n업로드 시작 버튼 누르셈" else "이미지 선택 취소"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.testUpload.setOnClickListener {
            val uri = pickedUri
            if (uri == null) {
                binding.textView.text = "이미지부터 선택해!"
                return@setOnClickListener
            }
            startUploadAndPoll(uri)
        }

        binding.button2.setOnClickListener {
            pickImage.launch("image/*")
        }
    }

    private fun startUploadAndPoll(imageUri: Uri) {
        lifecycleScope.launch {
            val api = ApiClient.uploadApi(context = this@TestActivity)
            val auth = "Bearer $ACCESS_TOKEN"

            binding.textView.text = "업로드 시작 요청 보내는 중..."

            val startRes = api.startClothingUpload(
                image = MultipartUtil.uriToMultipart(this@TestActivity, imageUri),
                sectionId = MultipartUtil.textPart("1")
            )

            if (!startRes.success || startRes.data == null) {
                binding.textView.text = "업로드 시작 실패: ${startRes.error?.message ?: "unknown"}"
                return@launch
            }

            val jobId = startRes.data.jobId
            binding.textView.text = "jobId=$jobId\nprocessing..."

            while (isActive) {
                delay(1500)

                val st = api.getClothingUploadStatus(jobId)
                if (!st.success || st.data == null) {
                    binding.textView.text = "상태 조회 실패: ${st.error?.message ?: "unknown"}"
                    return@launch
                }

                when (st.data.status) {
                    "processing" -> binding.textView.text = "jobId=$jobId\nprocessing..."
                    "completed" -> {
                        binding.textView.text = "✅ completed!\nresult=${st.data.result}"
                        return@launch
                    }
                    "failed" -> {
                        binding.textView.text = "❌ failed: ${st.data.error}"
                        return@launch
                    }
                    else -> binding.textView.text = "unknown status=${st.data.status}"
                }
            }
        }
    }
}