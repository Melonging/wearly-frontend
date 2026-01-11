package ddwu.com.mobile.wearly_frontend.login.ui

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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



        binding.loginSubmitBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }



        // (디자인) 로그인 화면 "회원가입" 밑줄
        binding.loginSignupTv.paintFlags = binding.loginSignupTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }
}