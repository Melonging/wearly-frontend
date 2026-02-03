package ddwu.com.mobile.wearly_frontend.closet.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.closet.ui.adapter.ClothesInClosetAdapter
import ddwu.com.mobile.wearly_frontend.databinding.ActivityCloseListBinding
import ddwu.com.mobile.wearly_frontend.upload.data.SlotItem

class ClothesListActivity : AppCompatActivity() {
    lateinit var binding : ActivityCloseListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCloseListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.closetListTitleTv.text = intent.getStringExtra("CLOSET_NAME")

        binding.backIconIv.setOnClickListener {
            finish()
        }

        binding.btnAddClothes.setOnClickListener {
            //갤러리 열기 또는 사진 촬영
        }

        val clothesList = arrayListOf<SlotItem>(
            SlotItem.Empty,
            SlotItem.Image("https://example.com/cloth1.jpg"),
            SlotItem.Image("https://example.com/cloth2.jpg")
        )

        val adapter = ClothesInClosetAdapter(this, clothesList)
        binding.rvClothesInCloset.adapter = adapter
    }
}