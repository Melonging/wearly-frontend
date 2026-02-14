package ddwu.com.mobile.wearly_frontend.upload.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.databinding.ActivityClothingDetailBinding
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.Category
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.ClosetDto
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.ClosetViewSectionDto
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.ClothingUpdateRequestDto
import ddwu.com.mobile.wearly_frontend.upload.data.remote.ApiClient
import ddwu.com.mobile.wearly_frontend.upload.data.repository.ClosetRepository
import ddwu.com.mobile.wearly_frontend.upload.data.repository.OutfitRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import android.widget.ArrayAdapter

class ClothingDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClothingDetailBinding

    private val closetRepository by lazy { ClosetRepository(ApiClient.closetApi()) }
    private val outfitRepository by lazy { OutfitRepository(ApiClient.closetApi()) }

    private var changed = false
    private var clothingId: Long = -1L
    private var closetId: Int = -1

    private var closets: List<ClosetDto> = emptyList()
    private var closetSections: List<ClosetViewSectionDto> = emptyList()
    private var categories: List<Category> = emptyList()

    private var lastClosetId: Int = -1
    private var lastSectionId: Int = -1
    private var lastCategoryId: Int = -1
    private var lastClosetName: String = ""


    private var isBinding = true

    // 느릴 때 레이스 방지용
    private var loadSeq = 0
    private var updateJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)

        binding = ActivityClothingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clothingId = intent.getLongExtra("clothingId", -1L)
        if (clothingId == -1L) {
            finish(); return
        }

        closetId = intent.getIntExtra("closetId", -1)
        Log.d("DETAIL", "intent closetId=$closetId")

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener {
            if (changed) setResult(RESULT_OK)
            finish()
        }

        // 이미지 프리뷰
        val previewUrl = intent.getStringExtra("imageUrl")
        if (!previewUrl.isNullOrBlank()) Glide.with(this).load(previewUrl).into(binding.ivCloth)
        else binding.ivCloth.setImageResource(R.drawable.cloth_01)

        // ===== 드롭다운 리스너들 =====
        binding.spType.setOnItemClickListener { _, _, pos, _ ->
            if (isBinding) return@setOnItemClickListener
            val newId = categories.getOrNull(pos)?.category_id ?: return@setOnItemClickListener
            if (newId == lastCategoryId) return@setOnItemClickListener
            runUpdate(ClothingUpdateRequestDto(categoryId = newId)) {
                lastCategoryId = newId
                changed = true
                setResult(RESULT_OK)
            }
        }

        binding.spSection.setOnItemClickListener { _, _, pos, _ ->
            if (isBinding) return@setOnItemClickListener
            val newId = closetSections.getOrNull(pos)?.section_id ?: return@setOnItemClickListener
            if (newId == lastSectionId) return@setOnItemClickListener
            runUpdate(ClothingUpdateRequestDto(sectionId = newId)) {
                lastSectionId = newId
                changed = true
                setResult(RESULT_OK)
            }
        }

        binding.spCloset.setOnItemClickListener { _, _, pos, _ ->
            if (isBinding) return@setOnItemClickListener
            val newCloset = closets.getOrNull(pos) ?: return@setOnItemClickListener
            if (newCloset.closet_id == lastClosetId) return@setOnItemClickListener

            updateJob?.cancel()
            updateJob = lifecycleScope.launch {
                try {
                    isBinding = true

                    closetRepository.updateClothing(
                        clothingId,
                        ClothingUpdateRequestDto(closetId = newCloset.closet_id)
                    )
                    lastClosetId = newCloset.closet_id
                    lastClosetName = newCloset.closet_name
                    changed = true
                    setResult(RESULT_OK)

                    // 섹션 목록 재조회 (옷장 뷰로!)
                    val view = closetRepository.fetchClosetView(newCloset.closet_id)
                    closetSections = view.sections
                    binding.spSection.setAdapter(spinnerAdapter(closetSections.map { it.section_name }))

                    val firstSection = closetSections.firstOrNull()
                    if (firstSection != null) {
                        binding.spSection.setText(firstSection.section_name, false)
                        lastSectionId = firstSection.section_id

                        closetRepository.updateClothing(
                            clothingId,
                            ClothingUpdateRequestDto(sectionId = firstSection.section_id)
                        )
                    } else {
                        binding.spSection.setText("", false)
                    }

                } catch (e: Exception) {
                    Log.e("DETAIL", "update closet failed", e)
                    Toast.makeText(this@ClothingDetailActivity, "옷장 변경 실패", Toast.LENGTH_SHORT).show()

                } finally {
                    isBinding = false
                }
            }
        }

        // ===== 초기 로드 =====
        loadAllAndBind()

        // 삭제
        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("옷 삭제")
                .setMessage("정말 이 옷을 삭제하시겠습니까?")
                .setPositiveButton("삭제") { _, _ ->
                    lifecycleScope.launch {
                        runCatching {
                            closetRepository.deleteClothing(clothingId)
                            changed = true
                            setResult(RESULT_OK)
                            finish()
                        }.onFailure {
                            Toast.makeText(this@ClothingDetailActivity, "삭제 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("취소", null)
                .show()
        }
    }

    private fun loadAllAndBind() {
        val mySeq = ++loadSeq

        lifecycleScope.launch {
            try {
                isBinding = true

                val detail = closetRepository.fetchClothesDetail(clothingId)
                if (mySeq != loadSeq) return@launch

                lastClosetId = closetId.toInt()
                lastSectionId = detail.section_id ?: -1
                lastCategoryId = detail.category_id?.toInt() ?: -1
                // 옷장 목록
                closets = closetRepository.fetchClosets()
                binding.spCloset.setAdapter(spinnerAdapter(closets.map { it.closet_name }))

                // 카테고리 목록
                categories = outfitRepository.fetchCategories()
                binding.spType.setAdapter(spinnerAdapter(categories.map { it.name }))

                // 섹션 목록(옷장 뷰로만)
                if (lastClosetId == -1) throw IllegalStateException("closetId missing")
                val view = closetRepository.fetchClosetView(lastClosetId)
                closetSections = view.sections
                binding.spSection.setAdapter(spinnerAdapter(closetSections.map { it.section_name }))

                // 초기 표시 (id -> name)
                closets.indexOfFirst { it.closet_id == lastClosetId }.takeIf { it >= 0 }?.let { idx ->
                    binding.spCloset.setText(closets[idx].closet_name, false)
                    lastClosetName = closets[idx].closet_name
                }

                categories.indexOfFirst { it.category_id == lastCategoryId }.takeIf { it >= 0 }?.let { idx ->
                    binding.spType.setText(categories[idx].name, false)
                }

                closetSections.indexOfFirst { it.section_id == lastSectionId }.takeIf { it >= 0 }?.let { idx ->
                    binding.spSection.setText(closetSections[idx].section_name, false)
                }

            } catch (e: Exception) {
                Log.e("DETAIL", "load failed", e)
                Toast.makeText(this@ClothingDetailActivity, "불러오기 실패", Toast.LENGTH_SHORT).show()
            } finally {
                isBinding = false
            }
        }
    }

    private fun runUpdate(req: ClothingUpdateRequestDto, onSuccess: () -> Unit) {
        updateJob?.cancel()
        updateJob = lifecycleScope.launch {
            runCatching {
                closetRepository.updateClothing(clothingId, req)
            }.onSuccess {
                onSuccess()
            }.onFailure {
                Log.e("DETAIL", "update failed: $req", it)
                Toast.makeText(this@ClothingDetailActivity, "변경 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun spinnerAdapter(items: List<String>): ArrayAdapter<String> {
        return ArrayAdapter(this, R.layout.item_spinner, items).apply {
            setDropDownViewResource(R.layout.item_spinner_dropdown)
        }
    }
}
