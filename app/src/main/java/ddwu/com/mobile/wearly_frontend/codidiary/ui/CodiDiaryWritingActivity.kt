package ddwu.com.mobile.wearly_frontend.codidiary.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.databinding.ActivityCodiDiaryWritingBinding
import java.io.File

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

        //날짜
        val dateText = intent.getStringExtra("formattedDate")
        binding.dateTv.text = dateText

        //뒤로 돌아가기
        binding.backArrowIv.setOnClickListener {
            finish()
        }

        //end액티비티로 이동
        binding.confirmIv.setOnClickListener {
            val intent = Intent(this, CodiDiaryEndActivity::class.java)
            intent.putExtra("formattedDate", dateText)
            startActivity(intent)
        }

        val imagePath = intent.getStringExtra("image_path")
        if (imagePath != null) {
            val imageFile = File(imagePath)
            Glide.with(this)
                .load(imageFile)
                .into(binding.resultImageView)
        }
    }
}