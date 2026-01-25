package ddwu.com.mobile.wearly_frontend.login.ui

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ddwu.com.mobile.wearly_frontend.MainActivity
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.databinding.ActivityLoginSignupBinding

class SignupActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginSignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginSignupBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // (디자인) 회원가입 화면 "로그인" 밑줄
        binding.signupLoginTv.paintFlags = binding.signupLoginTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG




        // 클릭리스너

        // 회원가입 -> 로그인
        binding.signupSubmitBtn.setOnClickListener {
            finish()
        }

        // 로그인
        binding.signupLoginTv.setOnClickListener {
            finish()
        }

        // 뒤로가기
        binding.signupBackBtn.setOnClickListener {
            finish()
        }


    }
}