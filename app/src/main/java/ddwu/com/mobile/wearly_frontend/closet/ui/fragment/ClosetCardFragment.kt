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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.closet.ui.adapter.ClosetChipListAdapter
import ddwu.com.mobile.wearly_frontend.closet.data.ClosetItem
import ddwu.com.mobile.wearly_frontend.databinding.FragmentClosetCardBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.navigation.fragment.findNavController
import ddwu.com.mobile.wearly_frontend.closet.data.SectionItem
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.ClosetViewSectionDto
import ddwu.com.mobile.wearly_frontend.upload.data.remote.ApiClient
import ddwu.com.mobile.wearly_frontend.upload.data.remote.closet.ClosetApi
import ddwu.com.mobile.wearly_frontend.upload.ui.activity.UploadActivity
import kotlinx.coroutines.launch
class ClosetCardFragment : Fragment() {
    lateinit var binding : FragmentClosetCardBinding

    /* 빙기: upload에서 사용한 ClosetApi를 활용하겠습니다.*/
    private val closetApi: ClosetApi by lazy { ApiClient.closetApi() }

    private lateinit var closetAdapter: ClosetChipListAdapter

    private var selectedClosetId: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentClosetCardBinding.inflate(inflater, container, false)

        //리사이클러뷰 및 어댑터 초기화
        setupRecyclerView()

        /*
        //Retrofit 서비스 초기화
        initRetrofit()

         */

        //홈화면 API 연결
        fetchClosetList()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getWeekFullDates()

        setupClosetListeners()

        // 기본 옷장 상세
        //fetchClosetDetail(selectedClosetId)


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
            selectedClosetId = selectedCloset.closetId // 빙기: 섹션 옷 조회를 위해 필요합니다.
            fetchClosetDetail(selectedCloset.closetId)

        }

        //리사이클러뷰 연결
        binding.rvClosetChips.apply {
            adapter = closetAdapter
            // 가로 스크롤 설정
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    /*
    private fun initRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:4000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        service = retrofit.create(ClosetService::class.java)
    }

     */

    private fun fetchClosetList() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = closetApi.getClosets()

                val list = response.data.orEmpty()
                    .map { dto -> ClosetItem(closetId = dto.closet_id, closetName = dto.closet_name) }

                if (list.isEmpty()) {
                    Log.e("API_TEST", "옷장 목록이 비어있음")
                    closetAdapter.submitList(listOf(ClosetItem(1, "연결실패-더미1")))
                    return@launch
                }

                closetAdapter.submitList(list)

                selectedClosetId = list.first().closetId
                Log.d("API_TEST", "$selectedClosetId")
                fetchClosetDetail(selectedClosetId!!)

                Log.d("API_TEST", "옷장 목록 로드 성공: ${list.size}개")
            } catch (e: Exception) {
                Log.e("API_TEST", "옷장 목록 통신 실패: ${e.message}")

                closetAdapter.submitList(
                    listOf(
                        ClosetItem(1, "연결실패-더미1"),
                        ClosetItem(2, "연결실패-더미2")
                    )
                )
            }
        }
    }


    private fun fetchClosetDetail(closetId: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = closetApi.getClosetView(closetId)
                val data = response.data ?: return@launch

                val closetName = data.closet.closet_name
                val closetType = data.closet.closet_type
                val sections = data.sections

                applySectionsFromApi(
                    closetId = closetId,
                    closetName = closetName,
                    closetType = closetType,
                    sections = sections
                )


            } catch (e: Exception) {
                Log.e("API_TEST", "옷장 뷰 통신 실패: ${e.message}")
            }
        }
    }


    private fun applySectionsFromApi(
        closetId: Int,
        closetName: String,
        closetType: String,
        sections: List<ClosetViewSectionDto>
    ) {
        selectedClosetId = closetId

        binding.tvHanger1Title.text = ""
        binding.tvHanger2Title.text = ""
        binding.tvDrawer1Title.text = ""
        binding.tvDrawer2Title.text = ""

        binding.btnHanger1.setOnClickListener(null)
        binding.btnHanger2.setOnClickListener(null)
        binding.btnDrawer1.setOnClickListener(null)
        binding.btnDrawer2.setOnClickListener(null)

        binding.btnHanger1.isEnabled = false
        binding.btnHanger2.isEnabled = false
        binding.btnDrawer1.isEnabled = false
        binding.btnDrawer2.isEnabled = false

        val hangers = sections.filter { it.section_type == "행거" }
        val drawers = sections.filter { it.section_type == "서랍" }

        hangers.getOrNull(0)?.let { s ->
            binding.tvHanger1Title.text = s.section_name
            binding.btnHanger1.isEnabled = true
            binding.btnHanger1.setOnClickListener {
                Log.d("NAV", "CLICK hanger1 closetId=$closetId sectionId=${s.section_id} name=${s.section_name}")
                openContainer(closetId, s.section_id, s.section_name, closetName)
            }
        }

        hangers.getOrNull(1)?.let { s ->
            binding.tvHanger2Title.text = s.section_name
            binding.btnHanger2.isEnabled = true
            binding.btnHanger2.setOnClickListener {
                Log.d("NAV", "CLICK hanger1 closetId=$closetId sectionId=${s.section_id} name=${s.section_name}")
                openContainer(closetId, s.section_id, s.section_name, closetName)
            }
        }

        drawers.getOrNull(0)?.let { s ->
            binding.tvDrawer1Title.text = s.section_name
            binding.btnDrawer1.isEnabled = true
            binding.btnDrawer1.setOnClickListener {
                Log.d("NAV", "CLICK hanger1 closetId=$closetId sectionId=${s.section_id} name=${s.section_name}")
                openContainer(closetId, s.section_id, s.section_name, closetName)
            }
        }

        drawers.getOrNull(1)?.let { s ->
            binding.tvDrawer2Title.text = s.section_name
            binding.btnDrawer2.isEnabled = true
            binding.btnDrawer2.setOnClickListener {
                Log.d("NAV", "CLICK hanger1 closetId=$closetId sectionId=${s.section_id} name=${s.section_name}")
                openContainer(closetId, s.section_id, s.section_name, closetName)
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
    private fun openContainer(closetId: Int, sectionId: Int, name: String, closet: String) {
        Log.d("NAV", "openContainer closetId=$closetId sectionId=$sectionId name=$name")

        val intent = Intent(requireContext(), UploadActivity::class.java).apply {
            putExtra("closetId", closetId)
            putExtra("sectionId", sectionId)
            putExtra("containerName", name)
            putExtra("closet", closet)
        }
        startActivity(intent)
    }

    // API 연결이 되지 않더라도 우선 진행

    private fun dummySections(): List<SectionItem> = listOf(
        SectionItem(sectionName = "행거 1", clothesCount = 8),
        SectionItem(sectionName = "행거 2", clothesCount = 8),
        SectionItem(sectionName = "서랍 1", clothesCount = 4),
        SectionItem(sectionName = "서랍 2", clothesCount = 2),
    )


    private fun applySections(closetId: Int, sections: List<SectionItem>) {
        selectedClosetId = closetId

        // 행거1
        if (sections.isNotEmpty()) {
            val s = sections[0]
            binding.tvHanger1Title.text = s.sectionName
            binding.btnHanger1.setOnClickListener { openContainer(selectedClosetId, 1, s.sectionName, "옷장1") } // 더미 sectionId
        }

        // 행거2
        if (sections.size > 1) {
            val s = sections[1]
            binding.tvHanger2Title.text = s.sectionName
            binding.btnHanger2.setOnClickListener { openContainer(selectedClosetId, 2, s.sectionName,"옷장1") }
        }

        // 서랍1
        if (sections.size > 2) {
            val s = sections[2]
            binding.tvDrawer1Title.text = s.sectionName
            binding.btnDrawer1.setOnClickListener { openContainer(selectedClosetId, 3, s.sectionName, "옷장1") }
        }

        // 서랍2
        if (sections.size > 3) {
            val s = sections[3]
            binding.tvDrawer2Title.text = s.sectionName
            binding.btnDrawer2.setOnClickListener { openContainer(selectedClosetId, 4, s.sectionName,"옷장1") }
        }
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
                }
            }
    }
}

