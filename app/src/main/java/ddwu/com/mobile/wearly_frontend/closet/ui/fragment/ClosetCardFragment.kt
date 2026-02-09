package ddwu.com.mobile.wearly_frontend.closet.ui.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.closet.ui.adapter.ClosetChipListAdapter
import ddwu.com.mobile.wearly_frontend.closet.data.ClosetItem
import ddwu.com.mobile.wearly_frontend.closet.network.ClosetService
import ddwu.com.mobile.wearly_frontend.databinding.FragmentClosetCardBinding
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ClosetCardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding : FragmentClosetCardBinding
    private lateinit var service: ClosetService
    private lateinit var closetAdapter: ClosetChipListAdapter

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
        binding = FragmentClosetCardBinding.inflate(inflater, container, false)

        //리사이클러뷰 및 어댑터 초기화
        setupRecyclerView()

        //Retrofit 서비스 초기화
        initRetrofit()

        //홈화면 API 연결
        fetchClosetList()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getWeekFullDates()

        setupClosetListeners()

        //옷장 옷 목록 조회
        // 빙기: uploadFragment로 넘어갑니다.
        binding.btnHanger1.setOnClickListener { openContainer("HANGER", 1, "행거 1") }
        binding.btnHanger2.setOnClickListener { openContainer("HANGER", 2, "행거 2") }
        binding.btnDrawer1.setOnClickListener { openContainer("DRAWER", 1, "서랍 1") }
        binding.btnDrawer2.setOnClickListener { openContainer("DRAWER", 2, "서랍 2") }
    }

    private fun setupClosetListeners(){

        //옷장 추가
        binding.addIconIv.setOnClickListener {
            val dialog = PlusClosetDialogFragment().apply {
                currentType = PlusClosetDialogFragment.WardrobeType.CLOSET
                currentName = "옷장1"
                listener = object : PlusClosetDialogFragment.OnWardrobeEditedListener {
                    override fun onWardrobeEdited(
                        type: PlusClosetDialogFragment.WardrobeType,
                        name: String
                    ) {
                        // TODO: 편집 결과 반영
                    }
                }
            }
            dialog.show(childFragmentManager, "PlusWardrobeDialog")
        }

        //옷장편집
        binding.moreOptionsIv.setOnClickListener {
            val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView = inflater.inflate(R.layout.layout_edit_popup, null)

            //PopupWindow 생성
            val popupWindow = PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
            )

            popupWindow.showAsDropDown(it) //clicked view (moreOptionsIv)

            //편집
            popupView.findViewById<View>(R.id.btn_edit).setOnClickListener {
                val dialog = EditClosetDialogFragment().apply {
                    currentType = EditClosetDialogFragment.WardrobeType.CLOSET
                    currentName = "옷장1"
                    listener = object : EditClosetDialogFragment.OnWardrobeEditedListener {
                        override fun onWardrobeEdited(
                            type: EditClosetDialogFragment.WardrobeType,
                            name: String
                        ) {
                            // TODO: 편집 결과 반영
                        }
                    }
                }
                dialog.show(childFragmentManager, "EditWardrobeDialog")
                popupWindow.dismiss()
            }

            //삭제
            popupView.findViewById<View>(R.id.btn_delete).setOnClickListener {
                popupWindow.dismiss()
            }
        }
    }
    private fun setupRecyclerView() {
        closetAdapter = ClosetChipListAdapter { selectedCloset ->
            fetchClosetDetail(selectedCloset.closetId)
        }

        //리사이클러뷰 연결
        binding.rvClosetChips.apply {
            adapter = closetAdapter
            // 가로 스크롤 설정
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }
    }
    private fun initRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:4000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(ClosetService::class.java)
    }

    private fun fetchClosetList() {
        //더미 데이터
        val dummyClosets = listOf(
            ClosetItem(closetId = 1, closetName = "옷장1"),
            ClosetItem(closetId = 2, closetName = "서랍1"),
            ClosetItem(closetId = 3, closetName = "서랍2")
        )

        // 어댑터에 데이터 전달
        closetAdapter.submitList(dummyClosets)

        //서버 연동 시 사용될 코드
        val token = "actual_token_here"
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = service.getClosetList(token)

                if (response.success && response.data != null) {
                    // 서버에서 받아온 목록을 어댑터에 반영
                    closetAdapter.submitList(response.data)
                    Log.d("API_TEST", "옷장 목록 로드 성공: ${response.data.size}개")
                } else {
                    Log.e("API_TEST", "서버 에러: ${response.error}")
                }
            } catch (e: Exception) {
                Log.e("API_TEST", "통신 실패: ${e.message}")

                // 테스트를 위해 통신 실패 시에만 더미 데이터 보이기 (선택 사항)
                val dummyClosets = listOf(
                    ClosetItem(closetId = 1, closetName = "연결실패-더미1"),
                    ClosetItem(closetId = 2, closetName = "연결실패-더미2")
                )
                closetAdapter.submitList(dummyClosets)
            }
        }

    }

    private fun fetchClosetDetail(closetId: Int) {
        val token = "actual_token_here"

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = service.getClosetDetail(token, closetId)
                if (response.success && response.data != null) {
                    val detail = response.data
                    // TODO: 받아온 detail(closetType, sections)을 UI에 반영
                    // 예: binding.tvClosetType.text = detail.closetType
                    Log.d("API_TEST", "상세 정보 로드: ${detail.closetType}")
                }
            } catch (e: Exception) {
                Log.e("API_TEST", "상세 조회 실패: ${e.message}")
            }
        }
    }

    private fun formatDate(date: Date): String {
        val dayFormat = SimpleDateFormat("d", Locale.getDefault())
        return dayFormat.format(date)
    }

    fun getWeekFullDates() {
        val calendar = Calendar.getInstance()
        val today = Date()
        val todayDateString = formatDate(today)

        val fullDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        calendar.add(Calendar.DAY_OF_MONTH, -(currentDayOfWeek - 1))


        val dateViews = listOf(
            binding.tvDate1, binding.tvDate2, binding.tvDate3,
            binding.tvDate4, binding.tvDate5, binding.tvDate6, binding.tvDate7
        )
        val dateViewBg = listOf(
            binding.dateView1, binding.dateView2, binding.dateView3,
            binding.dateView4, binding.dateView5, binding.dateView6, binding.dateView7
        )

        for (i in 0 until 7) {
            val currentLoopDate = calendar.time
            val dateText = formatDate(currentLoopDate)
            val fullDateString = fullDateFormat.format(currentLoopDate)


            dateViews[i].text = dateText

            // 오늘 날짜 하이라이트 처리
            if (dateText == todayDateString) {
                dateViewBg[i].setBackgroundResource(R.drawable.bg_closet_date_selected)
                dateViews[i].setTextColor(Color.WHITE)
            } else {
                dateViewBg[i].setBackgroundResource(R.drawable.bg_closet_date_unselected)
                dateViews[i].setTextColor(Color.parseColor("#666666"))
            }

            //각 요일별 클릭 리스너 달기
            dateViewBg[i].setOnClickListener {
                val bundle = Bundle().apply {
                    putString("selectedDate", fullDateString)
                }

                // CodiDiaryFragment로 이동 (Action ID 확인 필수)
                findNavController().navigate(
                    R.id.action_homeFragment_to_codiDiaryFragment,
                    bundle
                )
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    // 빙기: navigate 함수 추가함
    private fun openContainer(type: String, id: Int, name: String) {
        val bundle = Bundle().apply {
            putString("containerType", type)
            putInt("containerId", id)
            putString("containerName", name)
        }

        findNavController().navigate(
            R.id.action_homeFragment_to_uploadFragment,
            bundle
        )
    }

    companion object{
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ClosetCardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ClosetCardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

