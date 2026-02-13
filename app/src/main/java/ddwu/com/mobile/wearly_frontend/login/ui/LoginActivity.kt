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
import android.widget.TextView
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

        // 테스트 계정 자동 로그인
        binding.loginIdEt.setText("test2@email.com")
        binding.loginPwEt.setText("password1234")




        // --------------- 이벤트 리스너 ---------------


        // 아이디 유효성 검사 호출
        binding.loginIdEt.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                validateId(binding.loginIdEt, binding.loginIdWrongTv)
            }
        }

        // 비밀번호 유효성 검사 호출
        binding.loginPwEt.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
               validatePw(binding.loginPwEt, binding.loginPwWrongTv)
            }
        }

        // 로그인
        binding.loginSubmitBtn.setOnClickListener {
            validateId(binding.loginIdEt, binding.loginIdWrongTv)
            validatePw(binding.loginPwEt, binding.loginPwWrongTv)

            if (sharedViewModel.userId.value.isNullOrEmpty() or sharedViewModel.userPassword.value.isNullOrEmpty()) {
                return@setOnClickListener
            }

            val tm = TokenManager(this)
            sharedViewModel.requestLogin(tm)

            Log.d("token", tm.toString())
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
     * 아이디 유효성 검사를 위한 함수
     */
    private fun validateId(editText: EditText, errorTextView: TextView) {
        val input = editText.text.toString()

        when {
            // 비어있는 경우
            input.isEmpty() -> {
                errorTextView.text = "아이디를 입력해주세요."
                errorTextView.visibility = View.VISIBLE
                editText.isSelected = true
            }
            // 이메일 형식이 아닐 때
            !android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches() -> {
                errorTextView.text = "올바르지 않은 형식입니다. 다시 입력해주세요."
                errorTextView.visibility = View.VISIBLE
                editText.isSelected = true
            }
            // 통과
            else -> {
                errorTextView.visibility = View.GONE
                editText.isSelected = false
                sharedViewModel.userId.value = input // 뷰모델에 저장
            }
        }
    }

    /**
     * 비밀번호 유효성 검사를 위한 함수
     */
    private fun validatePw(editText: EditText, errorTextView: TextView) {
        val input = editText.text.toString()

        when {
            // 비어있는 경우
            input.isEmpty() -> {
                errorTextView.text = "비밀번호를 입력해주세요."
                errorTextView.visibility = View.VISIBLE
                editText.isSelected = true
            }
            // 비밀번호가 8자 미만일 때
            input.length < 8 -> {
                errorTextView.text = "8자 이상 입력해주세요."
                errorTextView.visibility = View.VISIBLE
                editText.isSelected = true
            }
            // 통과
            else -> {
                errorTextView.visibility = View.GONE
                editText.isSelected = false
                sharedViewModel.userPassword.value = input // 뷰모델에 저장
            }
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

                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()

                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}