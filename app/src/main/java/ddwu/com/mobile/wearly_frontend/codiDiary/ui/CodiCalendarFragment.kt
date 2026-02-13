package ddwu.com.mobile.wearly_frontend.codiDiary.ui

import android.Manifest
import android.content.pm.PackageManager
import ddwu.com.mobile.wearly_frontend.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import ddwu.com.mobile.wearly_frontend.BuildConfig
import ddwu.com.mobile.wearly_frontend.TokenManager
import ddwu.com.mobile.wearly_frontend.codiDiary.data.CalendarDateData
import ddwu.com.mobile.wearly_frontend.codiDiary.data.viewmodel.CodiDiaryViewModel
import ddwu.com.mobile.wearly_frontend.codiDiary.data.viewmodel.WeatherViewModel
import ddwu.com.mobile.wearly_frontend.codiDiary.ui.adapter.CalendarAdapter
import ddwu.com.mobile.wearly_frontend.codiDiary.ui.adapter.WeatherAdapter
import ddwu.com.mobile.wearly_frontend.databinding.FragmentCodiCalendarBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CodiCalendarFragment : Fragment() {
    private lateinit var binding: FragmentCodiCalendarBinding

    private var currentYear: Int = LocalDate.now().year
    private var currentMonth: Int = LocalDate.now().monthValue

    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var weatherAdapter: WeatherAdapter

    private val weatherViewModel: WeatherViewModel by activityViewModels()
    private val codiDiaryViewModel : CodiDiaryViewModel by activityViewModels()

    private lateinit var fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 옷 목록 캐시 비우기
        codiDiaryViewModel.clearClothesCache()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCodiCalendarBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 토큰 호출
        val tokenManager = TokenManager(requireContext())
        val token = tokenManager.getToken()


        // 위치 권환 확인 후 주간 날씨 불러오기
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (token != null) {
            fetchLocationAndWeather(token)

        } else {
            Snackbar.make(binding.root, "토큰 없음.", Snackbar.LENGTH_SHORT).show()
        }


        // 달력 어댑터 연결
        calendarAdapter = CalendarAdapter { day, hasRecord ->
            val dayInt = day.toIntOrNull() ?: 1
            val selectedDate = LocalDate.of(currentYear, currentMonth, dayInt)
            val selectedFullDate = "${currentYear}년 ${currentMonth}월 ${day}일"

            // 날짜 범위 확인 (한 달 전 ~ 다음 주)
            if (isWithinRange(selectedDate)) {
                if (hasRecord) {
                    val serverDate = formatToServerDate(selectedFullDate)
                    val token = TokenManager(requireContext()).getToken()

                    if (token != null && serverDate != null) {
                        codiDiaryViewModel.diaryReadData.removeObservers(viewLifecycleOwner)

                        codiDiaryViewModel.clearDiaryReadData()
                        codiDiaryViewModel.fetchDiaryRead(token, serverDate)

                        codiDiaryViewModel.diaryReadData.observe(viewLifecycleOwner) { data ->
                            if (data != null) {
                                codiDiaryViewModel.diaryReadData.removeObservers(viewLifecycleOwner)

                                val bundle = Bundle().apply {
                                    putString("selectedDate", selectedFullDate)
                                }
                                findNavController().navigate(R.id.action_calendar_to_diaryRead, bundle)
                            }
                        }
                    }
                } else {
                    showSelectDialog(selectedFullDate)
                }
            } else {
                Snackbar.make(binding.root, "일기를 작성할 수 없는 날짜입니다.", Snackbar.LENGTH_SHORT).show()
            }
        }
        binding.calendarRv.apply {
            adapter = calendarAdapter
            layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 7)
        }


        // 날씨 어댑터 연결
        weatherAdapter = WeatherAdapter()

        binding.calendarWeatherRv.apply {
            adapter = weatherAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        weatherViewModel.weaklyWeatherData.observe(viewLifecycleOwner) { list ->
            weatherAdapter.submitList(list)
        }


        // 달력 피커
        binding.calendarYearmonthTv.setOnClickListener {
            showMonthPicker()
        }



        // 처음 진입 시 당월 달력 그리기
        binding.calendarYearmonthTv.text = "${currentYear}년 ${currentMonth}월"
        updateCalendar(currentYear, currentMonth)

        codiDiaryViewModel.diaryDateList.observe(viewLifecycleOwner) { dates ->
            if (dates.isNotEmpty()) {
                Log.d("Fragment", "받아온 날짜 개수: ${dates.size}")

                calendarAdapter.setRecordedDates(dates)
            }
        }

    }


    /**
     * 일기 작성 가능한 날짜인지 확인하는 함수
     */
    private fun isWithinRange(selectedDate: LocalDate): Boolean {
        val today = LocalDate.now()
        val startDate = today.minusMonths(1)
        val endDate = today.plusWeeks(1).minusDays(1)
        return !selectedDate.isBefore(startDate) && !selectedDate.isAfter(endDate)
    }


    /**
     * 바텀 시트를 호출하여 캘린더 범위를 변경하는 함수
     */
    private fun showMonthPicker() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.layout_calendar_picker, null)

        val npYear = view.findViewById<NumberPicker>(R.id.calendar_year_picker)
        val npMonth = view.findViewById<NumberPicker>(R.id.calendar_month_picker)
        val btnSubmit = view.findViewById<Button>(R.id.calendar_picker_submit)
        val btnClose = view.findViewById<ImageButton>(R.id.calendar_picker_close_btn)

        npYear.minValue = 2020
        npYear.maxValue = 2030
        npYear.value = currentYear

        npMonth.minValue = 1
        npMonth.maxValue = 12
        npMonth.value = currentMonth

        btnSubmit.setOnClickListener {
            currentYear = npYear.value
            currentMonth = npMonth.value

            binding.calendarYearmonthTv.text = "${currentYear}년 ${currentMonth}월"

            updateCalendar(currentYear, currentMonth)

            dialog.dismiss()
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }


    /**
     * 선택된 년도와 달에 따라 달력 범위를 결정하여 어댑터에게 넘기는 함수 (LocalDate 버전)
     */
    private fun updateCalendar(year: Int, month: Int) {
        val dayList = mutableListOf<CalendarDateData>()
        val firstDayOfMonth = LocalDate.of(year, month, 1)
        val today = LocalDate.now()

        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
        val maxDay = firstDayOfMonth.lengthOfMonth()

        val prevMonthLastDay = firstDayOfMonth.minusMonths(1).lengthOfMonth()
        for (i in firstDayOfWeek - 1 downTo 0) {
            dayList.add(CalendarDateData((prevMonthLastDay - i).toString(), false, false, ""))
        }

        for (i in 1..maxDay) {
            val current = LocalDate.of(year, month, i)
            val isToday = (current == today)
            val formattedDate = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            dayList.add(CalendarDateData(i.toString(), true, isToday, formattedDate))
        }

        if (dayList.size % 7 != 0) {
            val nextMonthRemaining = 7 - (dayList.size % 7)
            for (i in 1..nextMonthRemaining) {
                dayList.add(CalendarDateData(i.toString(), false, false, ""))
            }
        }

        calendarAdapter.submitList(dayList)

        val token = TokenManager(requireContext()).getToken()
        if (token != null) {
            codiDiaryViewModel.fetchDiaryDates(currentYear, currentMonth, token)
        }
    }


    /**
     * 다이얼로그 호출하여 옷 사진 선택 방식을 선택해 다이어리로 넘기는 함수
     */
    private fun showSelectDialog(selectedDate: String) {
        val dialogView = layoutInflater.inflate(R.layout.layout_calendar_select_clothes, null)

        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)

        val alertDialog = builder.create()

        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        alertDialog.show()

        val window = alertDialog.window
        window?.let {
            val layoutParams = it.attributes
            val displayMetrics = resources.displayMetrics
            layoutParams.width = (displayMetrics.widthPixels * 0.8).toInt()
            layoutParams.dimAmount = 0.6f
            it.attributes = layoutParams
            it.addFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }

        dialogView.findViewById<LinearLayout>(R.id.calendar_select_from_category_btn).setOnClickListener {
            val bundle = Bundle().apply {
                putString("selectedDate", selectedDate)
            }
            findNavController().navigate(R.id.action_calendar_to_codi_select, bundle)
            alertDialog.dismiss()
        }

        dialogView.findViewById<LinearLayout>(R.id.calendar_select_from_gallery_btn).setOnClickListener {
            val bundle = Bundle().apply {
                putString("selectedDate", selectedDate)
            }
            findNavController().navigate(R.id.action_calendar_to_diaryWrite, bundle)
            alertDialog.dismiss()
        }
    }

    /**
     * 날씨 권한 여부 확인 후 API 호출하는 함수.
     */
    private fun fetchLocationAndWeather(token: String) {
        // 권한 확인
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    // 에뮬레이터 위치 조회 불가로 인해 서울 정보 넣었습니다. 실제 휴대폰 사용 시 아래 주석 처리된 코드로 바꿔주세요.
                    // weatherViewModel.fetchWeeklyWeather(location.latitude, location.longitude, token)
                    weatherViewModel.fetchWeeklyWeather(37.5665, 126.9780, token)
                } else {
                    // 위치를 못 잡을 경우 기본값 (서울)
                    weatherViewModel.fetchWeeklyWeather(37.5665, 126.9780, BuildConfig.TEST_API_TOKEN)
                }
            }
        } else {
            // 권한이 없을 경우 기본값 (서울)
            weatherViewModel.fetchWeeklyWeather(37.5665, 126.9780, BuildConfig.TEST_API_TOKEN)
        }
    }


    /**
     * 날짜 포맷팅 함수
     *
     * @param selectedFullDate 선택된 날짜
     * @return 포맷팅된 날짜
     */
    private fun formatToServerDate(selectedFullDate: String): String? {
        return selectedFullDate.replace("년 ", "-")
            .replace("월 ", "-")
            .replace("일", "")
            .split("-")
            .let { parts ->
                if (parts.size >= 3) {
                    "${parts[0]}-${parts[1].padStart(2, '0')}-${parts[2].trim().padStart(2, '0')}"
                } else null
            }
    }



    override fun onResume() {
        super.onResume()

        // 토큰 호출
        val token = TokenManager(requireContext()).getToken()

        if (token != null) {
            codiDiaryViewModel.fetchDiaryDates(currentYear, currentMonth, token)
        } else {
            Snackbar.make(binding.root, "토큰 없음.", Snackbar.LENGTH_SHORT).show()
        }

        // 액션바 숨기기
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onPause() {
        super.onPause()
        // 다른 화면으로 나갈 때 다시 보이게 하기
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}