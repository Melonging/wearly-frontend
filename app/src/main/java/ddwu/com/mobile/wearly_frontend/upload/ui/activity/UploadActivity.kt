package ddwu.com.mobile.wearly_frontend.upload.ui.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
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
import ddwu.com.mobile.wearly_frontend.BuildConfig
import ddwu.com.mobile.wearly_frontend.R
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
        ClosetRepository(ApiClient.closetApi())
    }

    private val uploadRepository by lazy {
        UploadRepository(ApiClient.uploadApi())
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
                items.add(0, SlotItem.Image(id = clothingId, imageUrl = imageUrl))
                adapter.notifyItemInserted(0)
                binding.itemRV.scrollToPosition(0)

                if (currentSectionId != -1) {
                    lifecycleScope.launch {
                        kotlinx.coroutines.delay(300L)
                        fetchSectionClothes(currentSectionId)
                    }
                }
            } else {
                if (currentSectionId != -1) {
                    fetchSectionClothes(currentSectionId)
                }
            }
        }

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) openCameraInternal()
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("BASE_URL_CHECK", BuildConfig.BASE_URL)

        // 섹션 전달 받기
        val name = intent.getStringExtra("containerName")
        currentSectionId = intent.getIntExtra("sectionId", -1)
        if (currentSectionId == -1) { finish(); return }

        val layoutManager = GridLayoutManager(this, 3)
        binding.itemRV.layoutManager = layoutManager

        adapter = ClothingAdapter(
            items,
            onAddClick = { openCamera() },
            onImageClick = { imageItem ->
                val clothingId = imageItem.id ?: return@ClothingAdapter

                val intent = Intent(this, ClothingDetailActivity::class.java).apply {
                    putExtra("clothingId", clothingId)
                    putExtra("imageUrl", imageItem.imageUrl)
                }
                startActivity(intent)
            }
        )
        binding.itemRV.adapter = adapter

        fetchSectionClothes(currentSectionId)

        binding.btnAdd.setOnClickListener {
            openCamera()
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }


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


    private fun fetchSectionClothes(sectionId: Int) {
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
                Toast.makeText(this@UploadActivity, "섹션 조회 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }
}