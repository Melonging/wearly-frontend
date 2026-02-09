package ddwu.com.mobile.wearly_frontend.codidiary.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.databinding.ActivityCodiDiaryWritingBinding

class CodiDiaryWritingActivity : AppCompatActivity() {

    lateinit var binding: ActivityCodiDiaryWritingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCodiDiaryWritingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //뒤로 돌아가기
        binding.backArrowIv.setOnClickListener {
            finish()
        }

        //end액티비티로 이동
        binding.confirmIv.setOnClickListener {
            val intent = Intent(this, CodiDiaryEndActivity::class.java)
            startActivity(intent)
        }

    }
}