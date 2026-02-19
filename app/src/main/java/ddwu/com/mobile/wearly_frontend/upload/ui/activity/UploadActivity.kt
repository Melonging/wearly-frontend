package ddwu.com.mobile.wearly_frontend.upload.ui.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.TokenManager
import ddwu.com.mobile.wearly_frontend.databinding.ActivityUploadBinding
import ddwu.com.mobile.wearly_frontend.upload.data.slot.SlotItem
import ddwu.com.mobile.wearly_frontend.upload.data.remote.ApiClient
import ddwu.com.mobile.wearly_frontend.upload.data.repository.ClosetRepository
import ddwu.com.mobile.wearly_frontend.upload.data.repository.UploadRepository
import ddwu.com.mobile.wearly_frontend.upload.data.util.MultipartUtil
import ddwu.com.mobile.wearly_frontend.upload.network.FileUtil
import ddwu.com.mobile.wearly_frontend.upload.ui.adapter.ClothingAdapter
import kotlinx.coroutines.launch

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private val items = ArrayList<SlotItem>()
    private var currentPhotoUri: Uri? = null
    private lateinit var adapter: ClothingAdapter

    private var currentSectionId: Int = -1

    private val closetRepository by lazy {
        ClosetRepository(ApiClient.closetApi(context = this@UploadActivity))
    }

    // 카메라 실행용
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                currentPhotoUri?.let { uri ->
                    openLoading(uri, currentSectionId)
                }
            }
        }

    private val loadingActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode != Activity.RESULT_OK) return@registerForActivityResult

            val data = result.data
            val resultText = data?.getStringExtra("resultText")
            val imageUrl = data?.getStringExtra("imageUrl")

            if (!resultText.isNullOrBlank()) {
                showClassificationPopup(
                    resultText = resultText,
                    localPhotoUri = currentPhotoUri,
                    fallbackImageUrl = imageUrl
                )
            }

            val clothingId = data?.getLongExtra("clothingId", -1L) ?: -1L

            if (clothingId != -1L) {
                val safeUrl = imageUrl?.takeIf { it.isNotBlank() }
                items.add(0, SlotItem.Image(id = clothingId, imageUrl = safeUrl))
                adapter.notifyItemInserted(0)
                binding.itemRV.scrollToPosition(0)

                if (currentSectionId != -1) {
                    lifecycleScope.launch {
                        kotlinx.coroutines.delay(300L)
                        fetchSectionClothes()
                    }
                }
            } else {
                if (currentSectionId != -1) {
                    fetchSectionClothes()
                }
            }
        }

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) openCameraInternal()
        }


    private val detailLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                fetchSectionClothes()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 섹션 전달 받기
        val name = intent.getStringExtra("containerName") ?: ""
        binding.uploadTitleTv.text = name

        currentSectionId = intent.getIntExtra("sectionId", -1)
        val closet = intent.getStringExtra("closet") ?: ""
        Log.d("UPLOAD", "받은 containerName=$name closet=$closet sectionId=$currentSectionId")

        val closetId = intent.getIntExtra("closetId", -1)
        if (closetId == -1) {
            Log.e("UPLOAD", "closetId 없음")
            finish()
            return
        }
        if (currentSectionId == -1) { finish(); return }

        val layoutManager = GridLayoutManager(this, 3)
        binding.itemRV.layoutManager = layoutManager

        adapter = ClothingAdapter(
            items,
            onAddClick = { openCamera() },
            onImageClick = { imageItem ->
                val clothingId = imageItem.id ?: return@ClothingAdapter
                detailLauncher.launch(
                    Intent(this, ClothingDetailActivity::class.java).apply {
                        putExtra("section", name)
                        putExtra("closet", closet)
                        putExtra("clothingId", clothingId)
                        putExtra("imageUrl", imageItem.imageUrl)
                        putExtra("closetId", closetId)
                    }
                )
            }
        )

        binding.itemRV.adapter = adapter

        fetchSectionClothes()

        binding.btnAdd.setOnClickListener {
            if (!canClick()) return@setOnClickListener
                openCamera()
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }



    }

    private var lastClickAt = 0L
    private fun canClick(): Boolean {
        val now = SystemClock.elapsedRealtime()
        if (now - lastClickAt < 600) return false
        lastClickAt = now
        return true
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) openCameraInternal()
        else cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
    }

    private fun openCameraInternal() {
        val photoFile = FileUtil.createNewFile(this)
        val uri = FileProvider.getUriForFile(
            this,
            "ddwu.com.mobile.wearly_frontend.fileprovider",
            photoFile
        )
        currentPhotoUri = uri
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, uri)
        }
        cameraLauncher.launch(intent)
    }

    private fun openLoading(uri: Uri, sectionId: Int) {
        val intent = Intent(this, UploadLoadingActivity::class.java).apply {
            putExtra("photoUri", uri.toString())
            putExtra("sectionId", sectionId)
        }
        loadingActivityLauncher.launch(intent)
    }

    private fun showClassificationPopup(
        resultText: String,
        localPhotoUri: Uri?,
        fallbackImageUrl: String?
    ) {
        val view = layoutInflater.inflate(R.layout.dialog_classification, null)
        val iv = view.findViewById<ImageView>(R.id.ivCloth)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvSub = view.findViewById<TextView>(R.id.tvSub)

        tvTitle.text = resultText
        tvSub.text = "AI가 옷의 종류를 자동으로 분석했어요"

        when {
            !fallbackImageUrl.isNullOrBlank() -> Glide.with(this).load(fallbackImageUrl).into(iv) // ✅ 서버 우선
            localPhotoUri != null -> Glide.with(this).load(localPhotoUri).into(iv)
            else -> iv.setImageResource(R.drawable.cloth_01)
        }

        val dialog = AlertDialog.Builder(this, R.style.ClassificationDialog)
            .setView(view)
            .setCancelable(true)
            .create()

        dialog.show()

        android.os.Handler(Looper.getMainLooper()).postDelayed({
            if (dialog.isShowing) dialog.dismiss()
        }, 2500)
    }

    private fun fetchSectionClothes() {
        lifecycleScope.launch {
            try {
                // 2. 수정된 리포지터리 호출 (토큰과 sectionId 전달)
                // 반환 타입: SectionClothesData (그 안에 clothes: List<ClothingItem> 이 있음)
                val sectionData = closetRepository.fetchSectionClothes(currentSectionId)

                items.clear()

                // 3. sectionData 내부의 clothes 리스트를 순회
                sectionData.clothes.forEach { item ->
                    items.add(SlotItem.Image(
                        id = item.clothing_id,
                        imageUrl = item.image
                    ))
                }

                adapter.notifyDataSetChanged()

            } catch (e: Exception) {
                Log.e("SECTION", "섹션 조회 실패: ${e.message}")

                if (isFinishing || isDestroyed) return@launch

                // 401 에러 등이 발생했을 때 메시지 표시
                Toast.makeText(applicationContext, "목록을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**private fun fetchSectionClothes(sectionId: Int) {
        lifecycleScope.launch {
            try {
                val clothesList = closetRepository.fetchSectionClothes(sectionId)

                items.clear()
                clothesList.forEach { dto ->
                    items.add(SlotItem.Image(id = dto.clothing_id, imageUrl = dto.image))
                }

                adapter.notifyDataSetChanged()

            } catch (e: Exception) {
                Log.e("SECTION", e.message ?: "섹션 조회 실패")

                if (isFinishing || isDestroyed) {
                    return@launch
                }

                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }**/
}