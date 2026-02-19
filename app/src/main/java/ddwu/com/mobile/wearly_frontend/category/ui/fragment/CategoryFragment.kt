package ddwu.com.mobile.wearly_frontend.category.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import android.graphics.Color
import android.graphics.Typeface
import ddwu.com.mobile.wearly_frontend.category.data.ClothingItem
import ddwu.com.mobile.wearly_frontend.category.data.dto.CategoryDto
import ddwu.com.mobile.wearly_frontend.category.ui.adapter.CategoryClothingAdapter
import ddwu.com.mobile.wearly_frontend.category.ui.adapter.CategoryGridAdapter
import ddwu.com.mobile.wearly_frontend.category.ui.adapter.CategoryUi
import ddwu.com.mobile.wearly_frontend.databinding.FragmentCategoryBinding
import ddwu.com.mobile.wearly_frontend.upload.data.remote.ApiClient
import kotlinx.coroutines.launch

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private var selectedCategory: String = "전체"
    private lateinit var topTabs: List<TextView>


    private lateinit var categoryGridAdapter: CategoryGridAdapter
    private lateinit var clothingAdapter: CategoryClothingAdapter

    private var categories: List<CategoryDto> = emptyList()
    private var selectedCategoryId: Long? = null


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

        binding.categoryMoreIv.setOnClickListener {
            binding.categoryExpandContainer.visibility = View.VISIBLE
        }
        binding.closeBtn.setOnClickListener {
            binding.categoryExpandContainer.visibility = View.GONE
        }

        categoryGridAdapter = CategoryGridAdapter(
            categoryList = emptyList(),
            selectedCategoryId = null
        ) { catUi ->
            binding.categoryExpandContainer.visibility = View.GONE
            selectedCategoryId = catUi.id
            fetchClothes(selectedCategoryId)

            val match = topTabs.firstOrNull { it.text.toString() == catUi.name }
            if (catUi.id == null) selectTopTab(binding.categoryAllTv)
            else if (match != null) selectTopTab(match)
        }
        binding.categoryGridRv.adapter = categoryGridAdapter

        clothingAdapter = CategoryClothingAdapter(
            items = mutableListOf(),
            onSelectionChanged = { _, _ -> }
        )
        binding.clothingGridRv.adapter = clothingAdapter
        initTopTabs()
        bindTopTabClicks()


        fetchCategoriesAndInit()
    }

    private fun initTopTabs() {
        topTabs = listOf(
            binding.categoryAllTv,
            binding.categoryOuterTv,
            binding.categoryTopsTv,
            binding.categoryPantsTv,
            binding.categorySkirtTv,
            binding.categoryUnderwearTv
        )

        selectTopTab(binding.categoryAllTv)
    }

    private fun selectTopTab(selected: TextView) {
        topTabs.forEach { tv ->
            tv.setTextColor(Color.parseColor("#666666"))
            tv.setTypeface(null, Typeface.NORMAL)
        }
        selected.setTextColor(Color.parseColor("#111111"))
        selected.setTypeface(null, Typeface.BOLD)
    }

    private fun findCategoryIdByName(name: String): Long? {
        return categories.firstOrNull { it.name == name }?.category_id
    }


    private fun bindTopTabClicks() {
        binding.categoryAllTv.setOnClickListener {
            selectTopTab(binding.categoryAllTv)
            selectedCategoryId = null
            fetchClothes(null)
        }

        fun bind(tv: TextView, categoryName: String) {
            tv.setOnClickListener {
                selectTopTab(tv)
                val id = findCategoryIdByName(categoryName)
                selectedCategoryId = id
                fetchClothes(id) // id가 null이면 fetchClothes(null) -> 전체로 fallback
            }
        }

        bind(binding.categoryOuterTv, "아우터")
        bind(binding.categoryTopsTv, "상의")
        bind(binding.categoryPantsTv, "바지")
        bind(binding.categorySkirtTv, "스커트/원피스")
        bind(binding.categoryUnderwearTv, "신발")
    }

    private fun fetchCategoriesAndInit() {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                val res = ApiClient.categoryApi().getCategories()
                if (!res.success) error(res.error?.message ?: "카테고리 조회 실패")

                categories = res.data?.categories.orEmpty()

                // "전체" + 서버 카테고리들
                val uiList = listOf(CategoryUi(null, "전체")) +
                        categories.map { CategoryUi(it.category_id, it.name) }

                categoryGridAdapter.submitList(uiList)

                // 기본 전체 선택 + 옷 불러오기
                selectedCategoryId = null
                fetchClothes(null)

            }.onFailure {
                // 토스트/로그
                // Toast.makeText(requireContext(), it.message ?: "오류", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchClothes(categoryId: Long?) {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching {
                val res = if (categoryId == null) {
                    ApiClient.categoryApi().getAllClothes()
                } else {
                    ApiClient.categoryApi().getClothesByCategory(categoryId)
                }

                if (!res.success) error(res.error?.message ?: "옷 조회 실패")

                val list = res.data?.clothes.orEmpty().map { dto ->
                    ClothingItem(dto.clothing_id, dto.image)
                }

                clothingAdapter.updateData(list)

            }.onFailure {
                // Toast/로그
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

