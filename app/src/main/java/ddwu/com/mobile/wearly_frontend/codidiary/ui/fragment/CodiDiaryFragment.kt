package ddwu.com.mobile.wearly_frontend.codidiary.ui.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.category.ui.adapter.CategoryClothingAdapter
import ddwu.com.mobile.wearly_frontend.category.ui.adapter.CategoryGridAdapter
import ddwu.com.mobile.wearly_frontend.codidiary.data.CategoryItem
import ddwu.com.mobile.wearly_frontend.codidiary.data.CategoryResponse
import ddwu.com.mobile.wearly_frontend.codidiary.data.ClothesResponse
import ddwu.com.mobile.wearly_frontend.codidiary.data.ClothingItem
import ddwu.com.mobile.wearly_frontend.codidiary.network.CodiDiaryService
import ddwu.com.mobile.wearly_frontend.codidiary.ui.CodiDiaryWritingActivity
import ddwu.com.mobile.wearly_frontend.databinding.FragmentCodiDraryBinding
import retrofit2.Call
import java.text.SimpleDateFormat
import java.util.Locale
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.collections.forEachIndexed
import android.widget.ImageView

class CodiDraryFragment : Fragment() {

    lateinit var binding: FragmentCodiDraryBinding
    private var serverCategories = listOf<CategoryItem>()
    private lateinit var selectedImageViews: List<ImageView>
    private lateinit var clothingAdapter: CategoryClothingAdapter

    private val TAG = "CODI_DEBUG"
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:4000")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(CodiDiaryService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCodiDraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedImageViews = listOf(
            binding.selectedItem1Iv,
            binding.selectedItem2Iv,
            binding.selectedItem3Iv,
            binding.selectedItem4Iv,
            binding.selectedItem5Iv
        )

        clothingAdapter = CategoryClothingAdapter(mutableListOf()) { count, selectedList ->
            Log.d(TAG, "SelectionChanged: 선택된 아이템 개수 = $count")
            binding.selectedCountTv.text = count.toString() // 개수 업데이트
            updateSelectedPreview(selectedList)            // 미리보기 업데이트
        }

        // 4. 리사이클러뷰 설정
        binding.clothingGridRv.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.clothingGridRv.adapter = clothingAdapter

        // [주의] 아래에 있던 중복된 clothingAdapter 초기화 코드는 삭제했습니다.

        fetchCategories()
        setupDateView()
        setupClickListeners()
    }

    private fun updateSelectedPreview(selectedList: List<ClothingItem>) {
        Log.d(TAG, "updateSelectedPreview: 미리보기 갱신 시작 (아이템 ${selectedList.size}개)")
        selectedImageViews.forEach {
            it.setImageResource(0)
            it.visibility = View.GONE
        }

        selectedList.forEachIndexed { index, item ->
            if (index < selectedImageViews.size) {
                selectedImageViews[index].apply {
                    visibility = View.VISIBLE
                    Glide.with(this)
                        .load(item.image)
                        .into(this)
                }
            }
        }
    }
    private fun setupDateView() {
        val receivedDate = arguments?.getString("selectedDate") ?: "2024-01-01"
        try {
            val incomingFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val resultFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
            val dateObject = incomingFormat.parse(receivedDate)
            binding.dateTv.text = resultFormat.format(dateObject)
        } catch (e: Exception) {
            binding.dateTv.text = receivedDate
        }
    }

    private fun setupClickListeners() {
        binding.backArrowIv.setOnClickListener { parentFragmentManager.popBackStack() }

        binding.confirmIv.setOnClickListener {
            val selectedClothes = clothingAdapter.getSelectedItems()

            if (selectedClothes.isEmpty()) {
                Toast.makeText(context, "코디할 옷을 선택해주세요!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 선택된 아이템들의 비트맵을 리사이클러뷰 뷰홀더에서 직접 추출하여 리스트화
            val bitmaps = mutableListOf<Bitmap>()
            for (i in 0 until clothingAdapter.itemCount) {
                val viewHolder = binding.clothingGridRv.findViewHolderForAdapterPosition(i) as? CategoryClothingAdapter.ClothingViewHolder
                // 선택된 아이템인지 확인 (아이템 객체 비교)
                // 주의: 화면에 보이는 뷰만 가져올 수 있으므로, 실제 서비스에선 Glide 비동기 비트맵 로드가 더 안전하지만
                // 여기서는 직관적인 뷰 추출 방식을 사용합니다.
                if (viewHolder != null && viewHolder.itemView.alpha == 0.5f) {
                    val bitmap = viewToBitmap(viewHolder.binding.clothingIv)
                    bitmaps.add(bitmap)
                }
            }

            val finalBitmap = createCombinedBitmap(bitmaps)
            val imagePath = saveBitmapToFile(finalBitmap)

            if (imagePath != null) {
                val intent = Intent(requireContext(), CodiDiaryWritingActivity::class.java)
                intent.putExtra("image_path", imagePath)
                intent.putExtra("formattedDate", binding.dateTv.text.toString())
                startActivity(intent)
            }
        }

        binding.recentIv.setOnClickListener {
            val bottomSheet = RecentCodiBottomSheet()
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }

        binding.categoryMoreIv.setOnClickListener { binding.categoryExpandContainer.visibility = View.VISIBLE }
        binding.closeBtn.setOnClickListener { binding.categoryExpandContainer.visibility = View.GONE }
    }

    private fun fetchCategories() {
        apiService.getCategories().enqueue(object : Callback<CategoryResponse> {
            override fun onResponse(call: Call<CategoryResponse>, response: Response<CategoryResponse>) {
                if (response.isSuccessful) {
                    Log.d("API_TEST", "카테고리 가져오기 성공: ${response.body()}")
                    serverCategories = response.body()?.data?.categories ?: emptyList()
                    setupCategoryGrid()
                } else {
                    Log.e("API_TEST", "서버 응답 에러 코드: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                Log.e("API_TEST", "연결 실패 이유: ${t.message}")
            }
        })
    }
    private fun setupCategoryGrid() {
        val categoryNames = serverCategories.map { it.name }

        // CategoryGridAdapter 설정
        val gridAdapter = CategoryGridAdapter(categoryNames, "전체") { selectedName ->
            val selectedId = serverCategories.find { it.name == selectedName }?.category_id
            if (selectedId != null) {
                fetchClothes(selectedId) // 옷 목록 불러오기 함수 호출
            }
            binding.categoryExpandContainer.visibility = View.GONE
        }
        binding.categoryGridRv.adapter = gridAdapter
    }

    private fun fetchClothes(categoryId: Int) {
        Log.d(TAG, "API_CALL: 옷 목록 요청 (카테고리 ID: $categoryId)")

        // 더미 데이터 주입 로그
        Log.v(TAG, "DummyData: 서버 응답 전 더미 데이터 세팅")
        val packageName = requireContext().packageName
        val dummyClothes = listOf(
            ClothingItem(101, "android.resource://$packageName/${R.drawable.cloth_01}"),
            ClothingItem(102, "android.resource://$packageName/${R.drawable.cloth_02}"),
            ClothingItem(103, "android.resource://$packageName/${R.drawable.cloth_03}")
        )
        clothingAdapter.updateData(dummyClothes)

        apiService.getClothesByCategory(categoryId).enqueue(object : Callback<ClothesResponse> {
            override fun onResponse(call: Call<ClothesResponse>, response: Response<ClothesResponse>) {
                if (response.isSuccessful) {
                    val clothesList = response.body()?.data?.clothes ?: emptyList()
                    Log.i(TAG, "API_SUCCESS: 옷 ${clothesList.size}개 수신")
                    if (clothesList.isNotEmpty()) {
                        clothingAdapter.updateData(clothesList)
                    } else {
                        Log.w(TAG, "API_CHECK: 서버에 옷 데이터가 없음 (빈 리스트)")
                    }
                } else {
                    Log.e(TAG, "API_ERROR: 옷 응답 실패 (코드: ${response.code()})")
                }
            }
            override fun onFailure(call: Call<ClothesResponse>, t: Throwable) {
                Log.e(TAG, "API_FAIL: 옷 목록 연결 실패 - ${t.message}")
            }
        })
    }

    // 뷰를 비트맵으로 변환하는 헬퍼 함수
    private fun viewToBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun createCombinedBitmap(bitmaps: List<Bitmap>): Bitmap {
        if (bitmaps.isEmpty()) return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

        var width = 0
        var height = 0
        bitmaps.forEach {
            width = maxOf(width, it.width)
            height += it.height
        }

        val combined = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(combined)
        var currentY = 0f
        bitmaps.forEach {
            canvas.drawBitmap(it, 0f, currentY, null)
            currentY += it.height
        }
        return combined
    }

    private fun saveBitmapToFile(bitmap: Bitmap): String? {
        val file = File(requireContext().cacheDir, "combined_${System.currentTimeMillis()}.jpg")
        return try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            file.absolutePath
        } catch (e: IOException) {
            null
        }
    }
}