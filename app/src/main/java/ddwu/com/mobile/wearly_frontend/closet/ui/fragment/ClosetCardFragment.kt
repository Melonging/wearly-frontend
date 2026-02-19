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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.activityViewModels
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
import com.bumptech.glide.Glide
import ddwu.com.mobile.wearly_frontend.TokenManager
import ddwu.com.mobile.wearly_frontend.closet.data.CreateClosetRequest
import ddwu.com.mobile.wearly_frontend.closet.data.SectionDetail
import ddwu.com.mobile.wearly_frontend.closet.data.UpdateClosetNameRequest
import ddwu.com.mobile.wearly_frontend.upload.data.remote.ApiClient
import ddwu.com.mobile.wearly_frontend.upload.ui.activity.UploadActivity
import kotlinx.coroutines.launch
import ddwu.com.mobile.wearly_frontend.codidiary.data.viewmodel.WeatherViewModel
import ddwu.com.mobile.wearly_frontend.data.CodiRecord
import kotlin.coroutines.cancellation.CancellationException
import kotlin.getValue
class ClosetCardFragment : Fragment() {
    lateinit var binding: FragmentClosetCardBinding
    private val weatherViewModel: WeatherViewModel by activityViewModels()
    private lateinit var closetAdapter: ClosetChipListAdapter

    private var selectedClosetId: Int = 1
    private var selectedClosetName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentClosetCardBinding.inflate(inflater, container, false)
        setupRecyclerView()  //리사이클러뷰 및 어댑터 초기화
        fetchClosetList() //홈화면 API 연결
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getWeekFullDates() //날짜
        getTodayWeatherFromWeekly()   //날씨
        //renderWeeklySlots(records)
        setupClosetListeners()
    }

    /**********************************************
     Setup Methods
    **********************************************/
    private fun setupRecyclerView() {
        closetAdapter = ClosetChipListAdapter { selectedCloset ->
            updateSelectedCloset(selectedCloset.closetId, selectedCloset.closetName)
        }

        binding.rvClosetChips.apply {
            adapter = closetAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupClosetListeners() {
        // 옷장 추가
        binding.addIconIv.setOnClickListener {
            showAddClosetDialog()
        }

        // 옷장 편집/삭제
        binding.moreOptionsIv.setOnClickListener {
            showEditDeletePopup(it)
        }
    }

    /*********************************************
     Dialog & Popup Methods
    ***********************************************/
    private fun showAddClosetDialog() {
        val dialog = PlusClosetDialogFragment().apply {
            currentType = PlusClosetDialogFragment.WardrobeType.CLOSET
            listener = object : PlusClosetDialogFragment.OnWardrobeEditedListener {
                override fun onWardrobeEdited(
                    type: PlusClosetDialogFragment.WardrobeType,
                    name: String
                ) {
                    addNewCloset(type, name)
                }
            }
        }
        dialog.show(childFragmentManager, "PlusWardrobeDialog")
    }

    private fun showEditDeletePopup(anchorView: View) {
        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.layout_edit_popup, null)

        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.showAsDropDown(anchorView)

        // 편집 버튼
        popupView.findViewById<View>(R.id.btn_edit).setOnClickListener {
            showEditClosetDialog()
            popupWindow.dismiss()
        }

        // 삭제 버튼
        popupView.findViewById<View>(R.id.btn_delete).setOnClickListener {
            deleteCloset()
            popupWindow.dismiss()
        }
    }

    private fun showEditClosetDialog() {
        val dialog = EditClosetDialogFragment().apply {
            currentType = EditClosetDialogFragment.WardrobeType.CLOSET
            currentName = selectedClosetName
            listener = object : EditClosetDialogFragment.OnWardrobeEditedListener {
                override fun onWardrobeEdited(
                    type: EditClosetDialogFragment.WardrobeType,
                    name: String
                ) {
                    updateClosetName(type,name)
                }
            }
        }
        dialog.show(childFragmentManager, "EditWardrobeDialog")
    }

    /***********************************************
    API Methods
    ***********************************************/
    private fun addNewCloset(type: PlusClosetDialogFragment.WardrobeType, name: String) {
        val templateId = when (type) {
            PlusClosetDialogFragment.WardrobeType.CLOSET -> 1
            PlusClosetDialogFragment.WardrobeType.DRAWER -> 2
            PlusClosetDialogFragment.WardrobeType.SHOES -> 3
        }

        lifecycleScope.launch {
            try {
                val apiService = ApiClient.closetApi(requireContext())
                val response = apiService.setNewCloset(CreateClosetRequest(templateId, name))

                if (response.success && response.data != null) {
                    val newClosetId = response.data.closetId

                    selectedClosetId = newClosetId
                    selectedClosetName = name

                    fetchClosetList()
                    Log.d("API_TEST", "옷장 추가 성공: $name (ID: $newClosetId)")
                } else {
                    Log.e("API_TEST", "옷장 추가 실패: ${response.message}")
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("API_TEST", "옷장 추가 에러: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun updateClosetName(type: EditClosetDialogFragment.WardrobeType, name: String) {
        // 1. 타입을 숫자로 변환
        val templateId = when (type) {
            EditClosetDialogFragment.WardrobeType.CLOSET -> 1
            EditClosetDialogFragment.WardrobeType.DRAWER -> 2
            EditClosetDialogFragment.WardrobeType.SHOES -> 3
        }

        lifecycleScope.launch {
            try {
                val apiService = ApiClient.closetApi(requireContext())
                // 2. 이름과 templateId를 함께 전송
                val response = apiService.updateClosetName(
                    selectedClosetId,
                    UpdateClosetNameRequest(name)
                )

                if (response.success) { // 👈 if문 괄호와 조건 확인
                    selectedClosetName = name
                    fetchClosetList() // 목록 새로고침 (이때 바뀐 타입으로 조회됨)
                    Log.d("API_TEST", "수정 성공: $name, 타입ID: $templateId")
                } else {
                    Log.e("API_TEST", "수정 실패: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("API_TEST", "수정 에러: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun deleteCloset() {
        lifecycleScope.launch {
            try {
                val apiService = ApiClient.closetApi(requireContext())
                val response = apiService.deleteCloset(selectedClosetId)

                if (response.success) {
                    fetchClosetListAfterDelete()
                    Log.d("API_TEST", "옷장 삭제 성공")
                } else {
                    Log.e("API_TEST", "옷장 삭제 실패: ${response.message}")
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("API_TEST", "옷장 삭제 에러: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun fetchClosetList() {
        val apiService = ApiClient.closetApi(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = apiService.getHomeClosetList()

                if (response.success && response.data != null) {
                    val newList = response.data.map { dto ->
                        ClosetItem(
                            closetId = dto.closetId,
                            closetName = dto.closetName,
                            isSelected = (dto.closetId == selectedClosetId)
                        )
                    }

                    if (newList.isNotEmpty()) {
                        val finalList = if (!newList.any { it.isSelected }) {
                            selectedClosetId = newList.first().closetId
                            selectedClosetName = newList.first().closetName
                            newList.mapIndexed { index, item ->
                                if (index == 0) item.copy(isSelected = true) else item
                            }
                        } else {
                            newList.find { it.isSelected }?.let {
                                selectedClosetName = it.closetName
                            }
                            newList
                        }

                        closetAdapter.submitList(null)
                        closetAdapter.submitList(finalList) {
                            fetchClosetDetail(selectedClosetId)
                        }

                        Log.d("API_TEST", "옷장 목록: ${finalList.size}개, 선택: $selectedClosetId ($selectedClosetName)")
                    } else {
                        closetAdapter.submitList(emptyList())
                        clearClosetView()
                    }
                } else {
                    Log.e("API_TEST", "옷장 목록 조회 실패: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("API_TEST", "옷장 목록 조회 에러: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun fetchClosetListAfterDelete() {
        val apiService = ApiClient.closetApi(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = apiService.getHomeClosetList()

                if (response.success && response.data != null) {
                    val newList = response.data.map { dto ->
                        ClosetItem(
                            closetId = dto.closetId,
                            closetName = dto.closetName,
                            isSelected = false
                        )
                    }

                    if (newList.isNotEmpty()) {
                        selectedClosetId = newList.first().closetId
                        selectedClosetName = newList.first().closetName

                        val finalList = newList.mapIndexed { index, item ->
                            if (index == 0) item.copy(isSelected = true) else item
                        }

                        closetAdapter.submitList(null)
                        closetAdapter.submitList(finalList) {
                            fetchClosetDetail(selectedClosetId)
                        }
                    } else {
                        closetAdapter.submitList(emptyList())
                        clearClosetView()
                    }
                } else {
                    Log.e("API_TEST", "삭제 후 목록 조회 실패")
                }
            } catch (e: Exception) {
                Log.e("API_TEST", "삭제 후 목록 조회 에러: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun fetchClosetDetail(closetId: Int) {
        val apiService = ApiClient.closetApi(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = apiService.getClosetView(closetId)
                val data = response.data

                if (data != null) {
                    val closetName = data.closet.closetName
                    val sections = data.sections
                    val closetType  = data.closet.closetType

                    updateClosetUI(closetType, sections, closetId, closetName)
                } else {
                    Log.e("API_TEST", "옷장 상세 데이터 없음")
                    clearClosetView()
                }
            } catch (e: Exception) {
                Log.e("API_TEST", "옷장 상세 조회 에러: ${e.message}")
                e.printStackTrace()
                clearClosetView()
            }
        }
    }

    /***********************************************
     Selection Methods
    ***********************************************/
    private fun updateSelectedCloset(closetId: Int, closetName: String) {
        selectedClosetId = closetId
        selectedClosetName = closetName

        val updatedList = closetAdapter.currentList.map { item ->
            item.copy(isSelected = item.closetId == closetId)
        }

        closetAdapter.submitList(null)
        closetAdapter.submitList(updatedList) {
            fetchClosetDetail(closetId)
        }

        Log.d("API_TEST", "옷장 선택: $closetId ($closetName)")
    }

    /***********************************************
     UI Update Methods
    ***********************************************/

    private fun updateClosetUI(
        type: String,
        sections: List<SectionDetail>,
        closetId: Int,
        closetName: String
    ) {
        binding.layoutHangerGroup.visibility = View.GONE
        binding.layoutDrawerGroup.visibility = View.GONE
        binding.layoutShoesGroup.visibility = View.GONE

        when (type) {
            "기본 옷장" -> {
                binding.layoutHangerGroup.visibility = View.VISIBLE
                setupMixedClosetUI(sections, closetId, closetName)
            }
            "서랍장" -> {
                binding.layoutDrawerGroup.visibility = View.VISIBLE
                setupDrawerUI(sections, closetId, closetName)
            }
            "신발장" -> {
                binding.layoutShoesGroup.visibility = View.VISIBLE
                setupShoesUI(sections, closetId, closetName)
            }
            else -> {
                Log.w("API_TEST", "알 수 없는 옷장 타입: $type")
                clearClosetView()
            }
        }
    }

    private fun setupMixedClosetUI(
        sections: List<SectionDetail>,
        closetId: Int,
        closetName: String
    ) {
        // 행거 뷰들
        val hangerPairs = listOf(
            Pair(binding.tvHanger1Title, binding.btnHanger1),
            Pair(binding.tvHanger2Title, binding.btnHanger2)
        )

        // 서랍 뷰들
        val drawerPairs = listOf(
            Pair(binding.tvDrawer11Title, binding.btnDrawer11),
            Pair(binding.tvDrawer12Title, binding.btnDrawer12)
        )

        hangerPairs.forEach { (titleView, buttonView) ->
            titleView.text = "비어 있음"
            buttonView.isEnabled = false
            buttonView.setOnClickListener(null)
        }

        drawerPairs.forEach { (titleView, buttonView) ->
            titleView.text = "비어 있음"
            buttonView.isEnabled = false
            buttonView.setOnClickListener(null)
        }

        val hangers = sections.filter { it.sectionType == "행거" }
        val drawers = sections.filter { it.sectionType == "서랍" }

        Log.d("API_TEST", "행거: ${hangers.size}개, 서랍: ${drawers.size}개")

        hangers.forEachIndexed { index, section ->
            if (index < hangerPairs.size) {
                val (titleView, buttonView) = hangerPairs[index]
                titleView.text = section.sectionName
                buttonView.isEnabled = true
                buttonView.setOnClickListener {
                    openContainer(closetId, section.sectionId, section.sectionName, closetName)
                }
            }
        }

        drawers.forEachIndexed { index, section ->
            if (index < drawerPairs.size) {
                val (titleView, buttonView) = drawerPairs[index]
                titleView.text = section.sectionName
                buttonView.isEnabled = true
                buttonView.setOnClickListener {
                    openContainer(closetId, section.sectionId, section.sectionName, closetName)
                }
            }
        }
    }

    private fun setupDrawerUI(
        sections: List<SectionDetail>,
        closetId: Int,
        closetName: String
    ) {
        val drawerPairs = listOf(
            Pair(binding.tvDrawer1Title, binding.btnDrawer1),
            Pair(binding.tvDrawer2Title, binding.btnDrawer2),
            Pair(binding.tvDrawer3Title, binding.btnDrawer3),
            Pair(binding.tvDrawer4Title, binding.btnDrawer4)
        )

        drawerPairs.forEach { (titleView, buttonView) ->
            titleView.text = "비어 있음"
            buttonView.isEnabled = false
            buttonView.setOnClickListener(null)
        }

        sections.forEachIndexed { index, section ->
            if (index < drawerPairs.size) {
                val (titleView, buttonView) = drawerPairs[index]
                titleView.text = section.sectionName
                buttonView.isEnabled = true
                buttonView.setOnClickListener {
                    openContainer(closetId, section.sectionId, section.sectionName, closetName)
                }
            }
        }
    }

    private fun setupShoesUI(
        sections: List<SectionDetail>,
        closetId: Int,
        closetName: String
    ) {
        val shelfPairs = listOf(
            Pair(binding.tvShoes1Title, binding.btnShoes1),
            Pair(binding.tvShoes2Title, binding.btnShoes2),
            Pair(binding.tvShoes3Title, binding.btnShoes3),
            Pair(binding.tvShoes4Title, binding.btnShoes4),
            Pair(binding.tvShoes5Title, binding.btnShoes5),
            Pair(binding.tvShoes6Title, binding.btnShoes6)
        )

        shelfPairs.forEach { (titleView, buttonView) ->
            titleView.text = "비어 있음"
            buttonView.isEnabled = false
            buttonView.setOnClickListener(null)
        }

        sections.forEachIndexed { index, section ->
            if (index < shelfPairs.size) {
                val (titleView, buttonView) = shelfPairs[index]
                titleView.text = section.sectionName
                buttonView.isEnabled = true
                buttonView.setOnClickListener {
                    openContainer(closetId, section.sectionId, section.sectionName, closetName)
                }
            }
        }
    }

    private fun clearClosetView() {
        // 기본 옷장 뷰 초기화
        binding.tvHanger1Title.text = "빈 옷장"
        binding.tvHanger2Title.text = "빈 옷장"
        binding.tvDrawer1Title.text = "빈 옷장"
        binding.tvDrawer2Title.text = "빈 옷장"

        binding.btnHanger1.isEnabled = false
        binding.btnHanger2.isEnabled = false
        binding.btnDrawer1.isEnabled = false
        binding.btnDrawer2.isEnabled = false
    }

    /***********************************************
     Navigation Methods
    ************************************************/

    private fun openContainer(closetId: Int, sectionId: Int, name: String, closet: String) {
        Log.d("NAV", "섹션 열기: closetId=$closetId, sectionId=$sectionId, name=$name")

        val intent = Intent(requireContext(), UploadActivity::class.java).apply {
            putExtra("closetId", closetId)
            putExtra("sectionId", sectionId)
            putExtra("containerName", name)
            putExtra("closet", closet)
        }
        startActivity(intent)
    }

    /*********************************************
     Calendar & Weather Methods
    **********************************************/

    private fun getWeekFullDates() {
        val calendar = Calendar.getInstance()
        val today = Date()
        val todayDateString = formatDate(today)

        val fullDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())

        binding.tvCalendarTitle.text = todayFormat.format(today)

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

            if (dateText == todayDateString) {
                dateViewBg[i].setBackgroundResource(R.drawable.bg_closet_date_selected)
                dateViews[i].setTextColor(Color.WHITE)
            } else {
                dateViewBg[i].setBackgroundResource(R.drawable.bg_closet_date_unselected)
                dateViews[i].setTextColor(Color.parseColor("#666666"))
            }

            dateViewBg[i].setOnClickListener {
                val bundle = Bundle().apply {
                    putString("selectedDate", fullDateString)
                }
                findNavController().navigate(
                    R.id.action_homeFragment_to_codiDiaryFragment,
                    bundle
                )
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    private fun getTodayWeatherFromWeekly() {
        val token = TokenManager.getToken() ?: ""
        val todayFormatted = getTodayFormattedAsMonthDay()

        weatherViewModel.fetchWeeklyWeather(37.5665, 126.9780, token)

        weatherViewModel.weaklyWeatherData.observe(viewLifecycleOwner) { weeklyList ->
            if (weeklyList.isNullOrEmpty()) {
                binding.tvWeatherInfo.text = "날씨 정보 없음"
                return@observe
            }

            val todayWeather = weeklyList.find { it.date == todayFormatted }

            if (todayWeather != null) {
                binding.tvWeatherInfo.text = formatTemp(todayWeather.temperature)
            } else {
                val fallback = weeklyList.first()
                binding.tvWeatherInfo.text = formatTemp(fallback.temperature)
            }
        }
    }

    private fun getTodayFormattedAsMonthDay(): String {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return "$month/$day"
    }

    private fun formatDate(date: Date): String {
        val dayFormat = SimpleDateFormat("d", Locale.getDefault())
        return dayFormat.format(date)
    }

    private fun formatTemp(temp: String): String {
        return if (temp.contains("/")) {
            val parts = temp.split("/")
            "최고: ${parts[0].trim()} / 최저: ${parts[1].trim()}"
        } else {
            temp
        }
    }

//
//    /**
//     * 1. 데이터 로직: 이번 주 7일치 Date 리스트 생성
//     */
    private fun getCurrentWeekDates(): List<Date> {
        val calendar = Calendar.getInstance()
        val dates = mutableListOf<Date>()

        // 이번 주의 시작일(일요일)로 이동
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        calendar.add(Calendar.DAY_OF_MONTH, -(currentDayOfWeek - 1))

        // 일요일부터 7일치 추가
        for (i in 0 until 7) {
            dates.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return dates
    }
//
//    /**
//     * 2. UI 로직: 생성된 날짜와 서버 기록(records)을 매칭하여 렌더링
//     */
//    private fun renderWeeklySlots(records: List<CodiRecord>) {
//        val container = binding.daySlotsLayout
//        container.removeAllViews() // 기존 슬롯 초기화
//
//        val weekDates = getCurrentWeekDates()
//        val inflater = LayoutInflater.from(requireContext())
//
//        // 날짜 포맷 정의
//        val fullDateSdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        val dayOnlySdf = SimpleDateFormat("d", Locale.getDefault())
//
//        // 오늘 날짜 확인 (하이라이트용 선택사항)
//        val todayStr = fullDateSdf.format(Date())
//
//        weekDates.forEach { date ->
//            // 슬롯 뷰 인플레이트
//            val slotView = inflater.inflate(R.layout.item_day_slot, container, false)
//
//            val tvDate = slotView.findViewById<TextView>(R.id.tv_slot_date)
//            val ivImage = slotView.findViewById<ImageView>(R.id.iv_slot_image)
//            val viewDot = slotView.findViewById<View>(R.id.view_today_dot)
//
//            val dateStr = fullDateSdf.format(date)
//            val dayOnly = dayOnlySdf.format(date)
//
//            // 날짜 텍스트 세팅
//            tvDate.text = dayOnly
//
//            // 오늘 날짜인 경우 텍스트 색상 변경 등 처리 (옵션)
//            if (dateStr == todayStr) {
//                tvDate.setTextColor(resources.getColor(R.color.main_color, null))
//            }
//
//            // 💡 서버 데이터와 날짜 매칭
//            val recordForDay = records.find { it.date == dateStr }
//
//            if (recordForDay != null) {
//                ivImage.visibility = View.VISIBLE
//                // Glide 사용하여 코디 이미지 로드
//                Glide.with(this)
//                    .load(recordForDay.imageUrl)
//                    .circleCrop() // 원형으로 보여줄 경우
//                    .into(ivImage)
//            } else {
//                ivImage.visibility = View.GONE
//                // 기록이 없을 때 보여줄 기본 아이콘이나 배경 설정 가능
//            }
//
//            // 슬롯 클릭 이벤트
//            slotView.setOnClickListener {
//                // 상세 페이지로 이동하거나 해당 날짜 기록 보여주기
//                navigateToDateDetail(dateStr)
//            }
//
//            container.addView(slotView)
//        }
//    }

    private fun renderWeeklySlots(records: List<CodiRecord>) {
        val container = binding.daySlotsLayout
        container.removeAllViews() // 기존 슬롯(또는 FrameLayout 내부) 초기화

        // 1. 공통 데이터 준비
        val weekDates = getCurrentWeekDates() // 아까 만든 7일치 리스트 생성 함수
        val inflater = LayoutInflater.from(requireContext())

        val fullDateSdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // 서버 비교용
        val dayOnlySdf = SimpleDateFormat("d", Locale.getDefault())         // 화면 표시용 (1, 2, 3...)
        val titleFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault()) // 상단 타이틀용

        // 2. 상단 캘린더 타이틀 업데이트 (오늘 날짜 기준)
        binding.tvCalendarTitle.text = titleFormat.format(Date())
        val todayStr = fullDateSdf.format(Date())

        // 3. 7일치 슬롯 생성 시작
        weekDates.forEach { date ->
            val dateStr = fullDateSdf.format(date)
            val dayOnly = dayOnlySdf.format(date)

            // 슬롯 뷰(item_day_slot.xml) 인플레이트
            val slotView = inflater.inflate(R.layout.item_day_slot, container, false)

            val tvDate = slotView.findViewById<TextView>(R.id.tv_slot_date)
            val ivImage = slotView.findViewById<ImageView>(R.id.iv_slot_image)
            val slotContainer = slotView.findViewById<View>(R.id.day_slots_layout) // 슬롯 전체 배경

            // 날짜 텍스트 설정
            tvDate.text = dayOnly

            // 오늘 날짜 하이라이트 (기존 getWeekFullDates의 배경 변경 로직 통합)
            if (dateStr == todayStr) {
                slotContainer?.setBackgroundResource(R.drawable.bg_closet_date_selected)
                tvDate.setTextColor(Color.WHITE)
            } else {
                slotContainer?.setBackgroundResource(R.drawable.bg_closet_date_unselected)
                tvDate.setTextColor(Color.parseColor("#666666"))
            }

            // 4. 💡 서버 데이터(records) 매칭
            val recordForDay = records.find { it.date == dateStr }

            if (recordForDay != null) {
                ivImage.visibility = View.VISIBLE
                Glide.with(this)
                    .load(recordForDay.imageUrl)
                    .circleCrop()
                    .into(ivImage)
            } else {
                ivImage.visibility = View.GONE
            }

            // 5. 클릭 이벤트 (Navigation 로직 통합)
            slotView.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("selectedDate", dateStr)
                }
                findNavController().navigate(
                    R.id.action_homeFragment_to_codiDiaryFragment,
                    bundle
                )
            }

            container.addView(slotView)
        }
    }
    companion object {
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
