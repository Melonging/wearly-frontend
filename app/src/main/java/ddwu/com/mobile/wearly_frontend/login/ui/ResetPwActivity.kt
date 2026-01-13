package ddwu.com.mobile.wearly_frontend.login.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.databinding.ActivityLoginResetPwBinding
import ddwu.com.mobile.wearly_frontend.login.ui.adapter.FindEmailAdapter
import ddwu.com.mobile.wearly_frontend.login.ui.adapter.ResetPwAdapter

class ResetPwActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginResetPwBinding

    // 어댑터 생성
    lateinit var resetPwAdapter: ResetPwAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginResetPwBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 어댑터 설정
        resetPwAdapter = ResetPwAdapter(this)
        binding.resetPwVp.adapter = resetPwAdapter

        // 스와이프 기능 끄기
        binding.resetPwVp.isUserInputEnabled = false




        // 클릭리스너

        // 뒤로가기
        binding.resetPwBackBtn.setOnClickListener {
            val currentPage = binding.resetPwVp.currentItem

            if (currentPage == 0) {
                finish()
            }
            else {
                binding.resetPwVp.setCurrentItem(currentPage - 1, true)
            }
        }
    }

    /**
     * 뷰페이저에 표시할 페이지를 다음 프레그먼트로 넘기기 위한 함수.
     * 각 프레그먼트에서 호출.
     *
     * @param[currentPage] 현재 페이지 수를 나타내는 변수
     */
    fun nextPage() {
        val currentPage = binding.resetPwVp.currentItem

        if (currentPage == 2) {
            finish()
        }
        else {
            binding.resetPwVp.setCurrentItem(currentPage + 1, true)
        }
    }
}