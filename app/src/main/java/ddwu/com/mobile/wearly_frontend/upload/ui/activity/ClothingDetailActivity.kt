package ddwu.com.mobile.wearly_frontend.upload.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.databinding.ActivityClothingDetailBinding
import ddwu.com.mobile.wearly_frontend.upload.data.entity.ClothingDetail

class ClothingDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClothingDetailBinding

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
        val types = listOf("아우터", "상의", "바지", "원피스")

        val spCloset = findViewById<Spinner>(R.id.spCloset)
        val spSection = findViewById<Spinner>(R.id.spSection)
        val spType = findViewById<Spinner>(R.id.spType)
        val btnDelete = findViewById<View>(R.id.btnDelete)

        spCloset.adapter = spinnerAdapter(closets)
        spSection.adapter = spinnerAdapter(sections)
        spType.adapter = spinnerAdapter(types)
        spCloset.setSelection(0)
        spSection.setSelection(0)
        spType.setSelection(0)


        val detail: ClothingDetail? =
            if (android.os.Build.VERSION.SDK_INT >= 33) {
                intent.getParcelableExtra("detail", ClothingDetail::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra("detail")
            }

        binding.ivCloth.setImageResource(detail?.resId ?: R.drawable.cloth_01)
        binding.tvLocationHeader.text = detail?.location
        binding.tvClosetLabel.text = detail?.category


    }
    private fun spinnerAdapter(items: List<String>): ArrayAdapter<String> {
        return ArrayAdapter(this, R.layout.item_spinner, items).apply {
            setDropDownViewResource(R.layout.item_spinner_dropdown)
        }
    }

}