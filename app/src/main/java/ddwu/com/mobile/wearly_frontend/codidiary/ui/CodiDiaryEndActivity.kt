package ddwu.com.mobile.wearly_frontend.codidiary.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.databinding.ActivityCodiDiaryEndBinding

class CodiDiaryEndActivity : AppCompatActivity() {
    lateinit var binding: ActivityCodiDiaryEndBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCodiDiaryEndBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //코디일기작성날짜 받아오기
        //binding.dateTv.text = intent.getStringExtra("date")

        binding.backArrowIv.setOnClickListener {
            finish()
        }

        binding.editIv.setOnClickListener {
            //writing화면으로 이동
        }

        binding.deleteIv.setOnClickListener {
            //삭제
        }

        binding.shareIv.setOnClickListener {
            //공유
        }

    }
}