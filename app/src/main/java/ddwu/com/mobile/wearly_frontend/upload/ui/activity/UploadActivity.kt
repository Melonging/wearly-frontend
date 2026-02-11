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
import ddwu.com.mobile.wearly_frontend.upload.data.model.ClothesDetailDto
import ddwu.com.mobile.wearly_frontend.upload.data.remote.ApiClient
import ddwu.com.mobile.wearly_frontend.upload.data.repository.ClosetRepository
import ddwu.com.mobile.wearly_frontend.upload.network.FileUtil
import ddwu.com.mobile.wearly_frontend.upload.ui.adapter.ClothingAdapter
import kotlinx.coroutines.launch

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private val items = ArrayList<SlotItem>()
    private var currentPhotoUri: Uri? = null
    private lateinit var adapter: ClothingAdapter

    private val repository by lazy {
        ClosetRepository(ApiClient.closetApi())
    }


    // 카메라 실행용
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                currentPhotoUri?.let { uri -> openLoading(uri) }
            }
        }

    val loadingActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            Log.d("UPLOAD", "loading resultCode=${result.resultCode}")
            Log.d("UPLOAD", "intent data = ${result.data}")
            val resultText = result.data?.getStringExtra("resultText")
            Log.d("UPLOAD", "resultText=$resultText")

            if (result.resultCode == Activity.RESULT_OK && !resultText.isNullOrBlank()) {
                showClassificationPopup(resultText, currentPhotoUri)
            } else {
                Log.d("UPLOAD", "popup skipped (code or text empty)")
            }

            currentPhotoUri?.let { addPhotoToList(it) }
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
        val sectionId = intent.getIntExtra("sectionId", -1)

        if (sectionId == -1) {
            finish()
            return
        }

        Log.d("UPLOAD", "$name  $sectionId")

        val layoutManager = GridLayoutManager(this, 3)
        binding.itemRV.layoutManager = layoutManager

        adapter = ClothingAdapter(
            items,
            onAddClick = { openCamera() },
            onImageClick = { imageItem ->
                val clothesId = imageItem.id ?: return@ClothingAdapter
                val intent = Intent(this, ClothingDetailActivity::class.java)
                intent.putExtra("clothesId", clothesId)
                startActivity(intent)
            }
        )
        binding.itemRV.adapter = adapter

        if (sectionId != -1) {
            fetchSectionClothes(sectionId)
        }

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

    private fun openLoading(uri: Uri) {
        val intent = Intent(this, UploadLoadingActivity::class.java).apply {
            putExtra("photoUri", uri.toString())
        }
        loadingActivityLauncher.launch(intent)
    }

    private fun addPhotoToList(uri: Uri) {
        val lastIndex = items.lastIndex
        if (lastIndex >= 0 && items[lastIndex] is SlotItem.Empty) items.removeAt(lastIndex)
        items.add(SlotItem.Image(uri = uri))
        items.add(SlotItem.Empty)
        adapter.notifyDataSetChanged()
    }

    private fun showClassificationPopup(resultText: String, photoUri: Uri?) {
        val view = layoutInflater.inflate(R.layout.dialog_classification, null)

        val iv = view.findViewById<ImageView>(R.id.ivCloth)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvSub = view.findViewById<TextView>(R.id.tvSub)

        tvTitle.text = resultText
        tvSub.text = "AI가 옷의 종류를 자동으로 분석했어요"

        // 사진 표시 (Glide 있으면 Glide 추천)
        if (photoUri != null) {
            // Glide 사용 시:
            Glide.with(this).load(photoUri).into(iv)

        }

        val dialog = AlertDialog.Builder(this, R.style.ClassificationDialog)
            .setView(view)
            .setCancelable(true)
            .create()

        dialog.show()

        // 2.5초 후 자동 닫기
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            if (dialog.isShowing) dialog.dismiss()
        }, 2500)
    }


    // 더미 데이터
    private fun loadDummySectionClothes(sectionId: Int) {

        val dummyRes = when (sectionId) {
            1 -> listOf(R.drawable.cloth_01, R.drawable.cloth_02)
            2 -> listOf(R.drawable.cloth_03, R.drawable.cloth_01)
            3 -> listOf(R.drawable.cloth_02, R.drawable.cloth_03, R.drawable.cloth_01) // 서랍2
            4 -> listOf(R.drawable.cloth_01)
            else -> listOf(R.drawable.cloth_01, R.drawable.cloth_02, R.drawable.cloth_03)
        }

        items.clear()
        dummyRes.forEachIndexed { idx, resId ->
            items.add(
                SlotItem.Image(
                    id = (sectionId * 100 + idx + 1).toLong(),
                    resId = resId
                )
            )
        }
    }

    private fun fetchSectionClothes(sectionId: Int) {
        val token = BuildConfig.TEST_TOKEN

        lifecycleScope.launch {
            try {
                val clothesList = repository.fetchSectionClothes(sectionId)

                items.clear()
                clothesList.forEach { dto ->
                    items.add(
                        SlotItem.Image(
                            id = dto.clothing_id,
                            imageUrl = dto.image
                        )
                    )
                }
                adapter.notifyDataSetChanged()

                if (items.isEmpty()) {
                    // binding.emptyView.visibility = View.VISIBLE
                }

            } catch (e: Exception) {
                Log.e("SECTION", e.message ?: "섹션 조회 실패")
                Toast.makeText(this@UploadActivity, "섹션 조회 실패", Toast.LENGTH_SHORT).show()
            } finally {
            }
        }
    }



}