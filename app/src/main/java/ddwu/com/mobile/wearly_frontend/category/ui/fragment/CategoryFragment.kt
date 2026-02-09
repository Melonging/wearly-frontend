package ddwu.com.mobile.wearly_frontend.category.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.category.ui.adapter.CategoryClothingAdapter
import ddwu.com.mobile.wearly_frontend.category.ui.adapter.CategoryGridAdapter
import ddwu.com.mobile.wearly_frontend.databinding.FragmentCategoryBinding
import ddwu.com.mobile.wearly_frontend.upload.data.entity.ClothingDetail

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private var selectedCategory: String = "전체"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 화살표 클릭 시 펼치기
        binding.categoryMoreIv.setOnClickListener {
            binding.categoryExpandContainer.visibility = View.VISIBLE
        }

        // 닫기 클릭 시 접기
        binding.closeBtn.setOnClickListener {
            binding.categoryExpandContainer.visibility = View.GONE
        }

        // 1. 데이터 및 옷 어댑터 '먼저' 준비 (그래야 아래에서 가져다 씀)
        val testClothes = listOf(
            ClothingDetail(uri = null, resId = R.drawable.cloth_01, category = "아우터", recommendedTemp = 10, location = "옷장 A"),
            ClothingDetail(uri = null, resId = R.drawable.cloth_02, category = "아우터", recommendedTemp = 20, location = "옷장 B"),
            ClothingDetail(uri = null, resId = R.drawable.cloth_03, category = "아우터", recommendedTemp = 15, location = "옷장 A")
        )

//        val clothingAdapter = CategoryClothingAdapter(testClothes)
//        binding.clothingGridRv.adapter = clothingAdapter
//
//        // 2. 카테고리 리스트 및 그리드 어댑터 설정
//        val categoryList = listOf("전체", "아우터", "상의", "바지", "치마", "속옷/홈웨어",
//            "가방", "모자", "신발", "주얼리", "패션 소품")
//
//        val gridAdapter = CategoryGridAdapter(categoryList, "전체") { selectedCategory ->
//            // 에러 수정: CategoryGridAdapter -> clothingAdapter
//            clothingAdapter.filterByCategory(selectedCategory)
//            binding.categoryExpandContainer.visibility = View.GONE
//        }
//        binding.categoryGridRv.adapter = gridAdapter
//
//        // 3. 상단 수평 탭(TextView) 클릭 리스너 설정
//        val categoryViews = mapOf(
//            binding.categoryAllTv to "전체",
//            binding.categoryOuterTv to "아우터",
//            binding.categoryTopsTv to "상의",
//            binding.categoryPantsTv to "바지",
//        )
//
//        categoryViews.forEach { (view, categoryName) ->
//            view.setOnClickListener {
//                clothingAdapter.filterByCategory(categoryName)
//                // 선택 시 그리드 어댑터의 글자 굵기도 동기화해주면 더 좋음
//                gridAdapter.updateSelectedCategory(categoryName)
//            }
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

