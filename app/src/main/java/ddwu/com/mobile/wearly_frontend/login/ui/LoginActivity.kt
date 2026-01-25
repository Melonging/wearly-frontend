package ddwu.com.mobile.wearly_frontend.login.ui

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ddwu.com.mobile.wearly_frontend.MainActivity
import ddwu.com.mobile.wearly_frontend.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
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







        // 클릭리스너

        // 로그인 -> 홈 화면
        binding.loginSubmitBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // 회원가입
        binding.loginSignupTv.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        // 이메일 찾기
        binding.loginFindEmailTv.setOnClickListener {
            val intent = Intent(this, FindEmailActivity::class.java)
            startActivity(intent)
        }

        // 비밀번호 재설정
        binding.loginResetPwTv.setOnClickListener {
            val intent = Intent(this, ResetPwActivity::class.java)
            startActivity(intent)
        }






    }
}