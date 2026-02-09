package ddwu.com.mobile.wearly_frontend.codidiary.ui.fragment


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.category.ui.adapter.CategoryClothingAdapter
import ddwu.com.mobile.wearly_frontend.category.ui.adapter.CategoryGridAdapter
import ddwu.com.mobile.wearly_frontend.codidiary.ui.CodiDiaryWritingActivity
import ddwu.com.mobile.wearly_frontend.databinding.FragmentCodiDraryBinding
import ddwu.com.mobile.wearly_frontend.upload.data.entity.ClothingDetail
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
/**
 * A simple [Fragment] subclass.
 * Use the [CodiDraryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CodiDraryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding : FragmentCodiDraryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCodiDraryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //오늘 날짜 받아오기
        val titleFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())
        binding.dateTv.text = titleFormat.format(Date())

        //뒤로 가기
        binding.backArrowIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        //저장하기
        binding.confirmIv.setOnClickListener {
            val intent = Intent(requireContext(), CodiDiaryWritingActivity::class.java)
            startActivity(intent)
        }

        //최근코디
        binding.recentIv.setOnClickListener {
            val bottomSheet = RecentCodiBottomSheet()
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }


        // 화살표 클릭 시 펼치기
        binding.categoryMoreIv.setOnClickListener {
            binding.categoryExpandContainer.visibility = View.VISIBLE
        }

        // 닫기 클릭 시 접기
        binding.closeBtn.setOnClickListener {
            binding.categoryExpandContainer.visibility = View.GONE
        }


        //카테고리분류 어댑터
        val testClothes = listOf(
            ClothingDetail(uri = null, resId = R.drawable.cloth_01, category = "아우터", recommendedTemp = 10, location = "옷장 A"),
            ClothingDetail(uri = null, resId = R.drawable.cloth_02, category = "아우터", recommendedTemp = 20, location = "옷장 B"),
            ClothingDetail(uri = null, resId = R.drawable.cloth_03, category = "아우터", recommendedTemp = 15, location = "옷장 A")
        )

        val clothingAdapter = CategoryClothingAdapter(testClothes)
        binding.clothingGridRv.adapter = clothingAdapter

        val categoryList = listOf("전체", "아우터", "상의", "바지", "치마", "속옷/홈웨어",
            "가방", "모자", "신발", "주얼리", "패션 소품")

        val gridAdapter = CategoryGridAdapter(categoryList, "전체") { selectedCategory ->
            clothingAdapter.filterByCategory(selectedCategory)
            binding.categoryExpandContainer.visibility = View.GONE
        }
        binding.categoryGridRv.adapter = gridAdapter

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CodiDraryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CodiDraryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}