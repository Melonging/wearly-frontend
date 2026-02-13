package ddwu.com.mobile.wearly_frontend.login.ui

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import ddwu.com.mobile.wearly_frontend.MainActivity
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.TokenManager
import ddwu.com.mobile.wearly_frontend.databinding.ActivityLoginBinding
import ddwu.com.mobile.wearly_frontend.login.data.AuthViewModel
import kotlin.getValue

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    private val sharedViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Splash Screen 실행
        val splashScreen = installSplashScreen()

        // Splash Screen 강제 연장을 위한 변수
        var keepSplashScreen = true

        splashScreen.setKeepOnScreenCondition { keepSplashScreen }

        // 2초(2000ms) 후에 Splash Screen 종료
        Handler(Looper.getMainLooper()).postDelayed({
            keepSplashScreen = false
        }, 2000)


        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // (디자인) 로그인 화면 "회원가입" 밑줄
        binding.loginSignupTv.paintFlags = binding.loginSignupTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG



        // --------------- 이벤트 리스너 ---------------


        // 아이디 유효성 검사 -> 뷰모델에 저장
//        binding.loginIdEt.setOnFocusChangeListener { view, hasFocus ->
//            if (!hasFocus) {
//                val v = view as EditText
//                val errorText = binding.loginIdWrongTv
//                val input = v.text.toString()
//
//                when {
//                    // 아이디가 이메일 형식이 아닐 때
//                    !android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches() -> {
//                        errorText.text = "올바르지 않은 형식입니다. 다시 입력해주세요."
//                        errorText.visibility = View.VISIBLE
//                        v.isSelected = true
//                    }
//
//                    else -> {
//                        errorText.visibility = View.GONE
//                        v.isSelected = false
//                        sharedViewModel.userId.value = input
//                    }
//                }
//            }
//        }

        // 비밀번호 유효성 검사 -> 뷰모델에 저장
        binding.loginPwEt.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                val v = view as EditText
                val errorText = binding.loginPwWrongTv
                val input = v.text.toString()

                when {
                    // 비밀번호가 8자 미만일 때
                    input.length < 8 -> {
                        errorText.text = "8자 이상 입력해주세요."
                        errorText.visibility = View.VISIBLE
                        v.isSelected = true
                    }

                    else -> {
                        errorText.visibility = View.GONE
                        v.isSelected = false
                        sharedViewModel.userPassword.value = input
                    }
                }
            }

        }


        // 로그인
        binding.loginSubmitBtn.setOnClickListener {
            if(binding.loginIdEt.text.toString().isEmpty()) {
                binding.loginIdWrongTv.text = "아이디를 입력해주세요."
                binding.loginIdWrongTv.visibility = View.VISIBLE
                binding.loginIdEt.isSelected = true
            }
            if(binding.loginPwEt.text.toString().isEmpty()) {
                binding.loginPwWrongTv.text = "비밀번호를 입력해주세요."
                binding.loginPwWrongTv.visibility = View.VISIBLE
                binding.loginPwEt.isSelected = true
            }

            sharedViewModel.userId.value = binding.loginIdEt.text.toString()


            if (sharedViewModel.userId.value.isNullOrEmpty() or sharedViewModel.userPassword.value.isNullOrEmpty()) {
                return@setOnClickListener
            }

            val tm = TokenManager(this)
            sharedViewModel.requestLogin(tm)
        }

        sharedViewModel.isLoginSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

                finish()
            }
        }
        sharedViewModel.errorMessage.observe(this) { message ->
            if (!message.isNullOrEmpty()){
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                sharedViewModel.errorMessage.value = null
            }
        }






        // 회원가입 화면으로 이동
        binding.loginSignupTv.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        // 이메일 찾기 화면으로 이동
        binding.loginFindEmailTv.setOnClickListener {
            val intent = Intent(this, FindEmailActivity::class.java)
            startActivity(intent)
        }

        // 비밀번호 재설정 화면으로 이동
        binding.loginResetPwTv.setOnClickListener {
            val intent = Intent(this, ResetPwActivity::class.java)
            startActivity(intent)
        }
    }


    /**
     * 빈 화면 터치 시 포커스 해제를 위한 함수
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