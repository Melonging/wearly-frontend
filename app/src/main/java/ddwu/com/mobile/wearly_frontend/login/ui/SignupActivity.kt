package ddwu.com.mobile.wearly_frontend.login.ui

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ddwu.com.mobile.wearly_frontend.databinding.ActivityLoginSignupBinding
import ddwu.com.mobile.wearly_frontend.login.data.AuthViewModel
import ddwu.com.mobile.wearly_frontend.login.ui.adapter.SignupAdapter

class SignupActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginSignupBinding

    private val sharedViewModel: AuthViewModel by viewModels()

    // 어댑터 생성
    lateinit var signupAdapter: SignupAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginSignupBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 어댑터 설정
        signupAdapter = SignupAdapter(this)
        binding.signupVp.adapter = signupAdapter

        // 스와이프 기능 끄기
        binding.signupVp.isUserInputEnabled = false


        // --------------- 클릭리스너 ---------------

        // 뒤로가기
        binding.signupBackBtn.setOnClickListener {
            /*  한 페이지 씩 뒤로가기
            val currentPage = binding.signupVp.currentItem

            if (currentPage == 0) {
                finish()
            } else {
                binding.signupVp.setCurrentItem(currentPage - 1, true)
            }
            */
            finish()
        }
    }


    /**
     * 뷰페이저에 표시할 페이지를 다음 프레그먼트로 넘기기 위한 함수.
     * 각 프레그먼트에서 호출.
     *
     * @param[currentPage] 현재 페이지 수를 나타내는 변수
     */
    fun nextPage() {
        val currentPage = binding.signupVp.currentItem

        if (currentPage == 1) {
            finish()
        } else {
            binding.signupVp.setCurrentItem(currentPage + 1, true)
        }
    }


    /**
     * 빈 화면 터치 시 포커스 해제를 위한 함수.
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)

                // 터치 지점이 현재 포커스된 EditText 밖이라면?
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus() // 포커스 해제

                    // 키보드 숨기기
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}