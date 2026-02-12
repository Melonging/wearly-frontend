package ddwu.com.mobile.wearly_frontend.upload.ui.activity

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.databinding.ActivityClothingDetailBinding
import ddwu.com.mobile.wearly_frontend.upload.data.remote.ApiClient
import ddwu.com.mobile.wearly_frontend.upload.data.repository.ClosetRepository
import kotlinx.coroutines.launch

class ClothingDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClothingDetailBinding

    private val repository by lazy {
        ClosetRepository(ApiClient.closetApi())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)

        binding = ActivityClothingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toolbar.setNavigationOnClickListener { finish() }

        val closets = listOf("옷장 1", "옷장 2", "옷장 3")
        val sections = listOf("서랍 1", "행거 1", "행거 2", "서랍 2")
        val types = listOf("상의", "바지", "원피스/스커트", "가방", "모자", "신발", "기타 액세서리")

        val spCloset = binding.spCloset
        val spSection = binding.spSection
        val spType = binding.spType
        val btnDelete = binding.btnDelete

        spCloset.setAdapter(spinnerAdapter(closets))
        spSection.setAdapter(spinnerAdapter(sections))
        spType.setAdapter(spinnerAdapter(types))

        spCloset.setText(closets[0], false)
        spSection.setText(sections[0], false)
        spType.setText(types[0], false)


        val clothingId = intent.getLongExtra("clothingId", -1L)
        if (clothingId == -1L) {
            finish()
            return
        }

        val previewUrl = intent.getStringExtra("imageUrl")
        if (!previewUrl.isNullOrBlank()) {
            Glide.with(this).load(previewUrl).into(binding.ivCloth)
        } else {
            binding.ivCloth.setImageResource(R.drawable.cloth_01)
        }

        lifecycleScope.launch {
            try {
                val detail = repository.fetchClothesDetail(clothingId)
                Glide.with(this@ClothingDetailActivity).load(detail.image).into(binding.ivCloth)
            } catch (e: Exception) {
                if (previewUrl.isNullOrBlank()) {
                    binding.ivCloth.setImageResource(R.drawable.cloth_01)
                }
            }
        }


    }
    private fun spinnerAdapter(items: List<String>): ArrayAdapter<String> {
        return ArrayAdapter(this, R.layout.item_spinner, items).apply {
            setDropDownViewResource(R.layout.item_spinner_dropdown)
        }
    }

}