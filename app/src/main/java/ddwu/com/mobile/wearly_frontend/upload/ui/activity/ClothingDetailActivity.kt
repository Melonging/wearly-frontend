package ddwu.com.mobile.wearly_frontend.upload.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.databinding.ActivityClothingDetailBinding
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.Category
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.ClosetDto
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.ClothingUpdateRequestDto
import ddwu.com.mobile.wearly_frontend.upload.data.remote.ApiClient
import ddwu.com.mobile.wearly_frontend.upload.data.repository.ClosetRepository
import ddwu.com.mobile.wearly_frontend.upload.data.repository.OutfitRepository
import kotlinx.coroutines.launch

class ClothingDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClothingDetailBinding

    private val closetRepository by lazy {
        ClosetRepository(ApiClient.closetApi())
    }

    private val outfitRepository by lazy {
        OutfitRepository(ApiClient.closetApi())
    }

    private var clothingId: Long = -1L
    private var currentSectionId: Long = -1L
    private var currentCategoryId: Long = -1L

    private var closets: List<ClosetDto> = emptyList()
    private var categories: List<Category> = emptyList()

    data class Section(val sectionId: Int, val name: String)

    private val sections = listOf(
        Section(1, "행거 1"),
        Section(2, "행거 2"),
        Section(3, "서랍 1"),
        Section(4, "서랍 2"),
    )

    // 수정 API는 바뀐 필드만 호출
    private var isBinding = true
    private var lastClosetId: Int? = null
    private var lastSectionId: Int? = null
    private var lastCategoryId: Int? = null

    private var updateJob: kotlinx.coroutines.Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        android.util.Log.d("DETAIL", "onCreate() called")
        binding = ActivityClothingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clothingId = intent.getLongExtra("clothingId", -1L)
        Log.d("DETAIL", "received clothingId=$clothingId")

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toolbar.setNavigationOnClickListener { finish() }

        binding.spType.setOnItemClickListener { _, _, pos, _ ->
            if (isBinding) return@setOnItemClickListener
            val newId = categories[pos].category_id
            if (newId == lastCategoryId) return@setOnItemClickListener

            lifecycleScope.launch {
                runCatching {
                    closetRepository.updateClothing(
                        clothingId,
                        ClothingUpdateRequestDto(categoryId = newId)
                    )
                    lastCategoryId = newId
                    setResult(RESULT_OK)
                }
            }
        }

        binding.spSection.setOnItemClickListener { _, _, pos, _ ->
            if (isBinding) return@setOnItemClickListener
            val newId = sections[pos].sectionId
            if (newId == lastSectionId) return@setOnItemClickListener

            lifecycleScope.launch {
                runCatching {
                    closetRepository.updateClothing(
                        clothingId,
                        ClothingUpdateRequestDto(sectionId = newId)
                    )
                    lastSectionId = newId
                    setResult(RESULT_OK)
                }
            }
        }

        binding.spCloset.setOnItemClickListener { _, _, pos, _ ->
            if (isBinding) return@setOnItemClickListener
            val newId = closets[pos].closet_id
            if (newId == lastClosetId) return@setOnItemClickListener

            lifecycleScope.launch {
                runCatching {
                    closetRepository.updateClothing(
                        clothingId,
                        ClothingUpdateRequestDto(closetId = newId)
                    )
                    lastClosetId = newId
                    setResult(RESULT_OK)
                }
            }
        }

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
                Log.d("DETAIL", "fetch detail start")
                val detail = closetRepository.fetchClothesDetail(clothingId)
                Log.d("DETAIL", "detail ok section=${detail.section_id} cat=${detail.category_id}")

                Log.d("DETAIL", "fetch closets start")
                closets = closetRepository.fetchClosets()
                Log.d("DETAIL", "closets size=${closets.size}")

                val closetNames = closets.map { it.closet_name }
                binding.spCloset.setAdapter(spinnerAdapter(closetNames))
                Log.d("DETAIL", "spCloset adapter set: $closetNames")

                Log.d("DETAIL", "fetch categories start")
                categories = outfitRepository.fetchCategories()
                Log.d("DETAIL", "categories size=${categories.size}")

                val categoryNames = categories.map { it.name }
                binding.spType.setAdapter(spinnerAdapter(categoryNames))
                Log.d("DETAIL", "spType adapter set: $categoryNames")

                val sectionNames = sections.map { it.name }
                binding.spSection.setAdapter(spinnerAdapter(sectionNames))
                Log.d("DETAIL", "spSection adapter set: $sectionNames")

                // 마지막에만
                isBinding = false
                Log.d("DETAIL", "binding done")

            } catch (e: Exception) {
                Log.e("DETAIL", "load failed", e)
            }
        }


        // 옷 삭제
        binding.btnDelete.setOnClickListener {
            lifecycleScope.launch {
                runCatching {
                    closetRepository.deleteClothing(clothingId)
                    setResult(RESULT_OK)
                    finish()
                }.onFailure {
                    // Toast.makeText(this@ClothingDetailActivity, "삭제 실패", Toast.LENGTH_SHORT).show()
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