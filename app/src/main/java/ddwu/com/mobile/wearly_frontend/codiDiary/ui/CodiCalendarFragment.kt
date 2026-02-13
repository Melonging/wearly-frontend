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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import ddwu.com.mobile.wearly_frontend.BuildConfig
import ddwu.com.mobile.wearly_frontend.codiDiary.data.CalendarDateData
import ddwu.com.mobile.wearly_frontend.codiDiary.data.CodiDiaryViewModel
import ddwu.com.mobile.wearly_frontend.codiDiary.data.WeaklyWeatherViewModel
import ddwu.com.mobile.wearly_frontend.codiDiary.ui.adapter.CalendarAdapter
import ddwu.com.mobile.wearly_frontend.codiDiary.ui.adapter.WeatherAdapter
import ddwu.com.mobile.wearly_frontend.databinding.FragmentCodiCalendarBinding
import java.util.Calendar

class CodiCalendarFragment : Fragment() {
    private lateinit var binding: FragmentCodiCalendarBinding

    private var currentYear: Int = Calendar.getInstance().get(Calendar.YEAR)
    private var currentMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1

    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var weatherAdapter: WeatherAdapter

    private val weaklyWeatherViewModel: WeaklyWeatherViewModel by viewModels()
    private val codiDiaryViewModel : CodiDiaryViewModel by viewModels()

    private lateinit var fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient

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


        // 위치 권환 확인
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fetchLocationAndWeather()

        binding.calendarDayLayout.setOnClickListener {
            findNavController().navigate(R.id.action_calendar_to_diaryWrite)
        }

        calendarAdapter = CalendarAdapter { day, hasRecord ->
            val selectedFullDate = "${currentYear}년 ${currentMonth}월 ${day}일"

            if (hasRecord) {
                val bundle = Bundle().apply {
                    putString("selectedDate", selectedFullDate)
                }
                findNavController().navigate(R.id.action_calendar_to_diaryRead, bundle)
            } else {
                showSelectDialog(selectedFullDate)
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

        weaklyWeatherViewModel.weaklyWeatherData.observe(viewLifecycleOwner) { list ->
            weatherAdapter.submitList(list)
        }

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
     * 선택된 년도와 달에 따라 달력 범위를 결정하여 어댑터에게 넘기는 함수
     */
    private fun updateCalendar(year: Int, month: Int) {
        val dayList = mutableListOf<CalendarDateData>()

        val today = Calendar.getInstance()
        val tYear = today.get(Calendar.YEAR)
        val tMonth = today.get(Calendar.MONTH) + 1
        val tDay = today.get(Calendar.DATE)

        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
        val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val prevCalendar = calendar.clone() as Calendar
        prevCalendar.add(Calendar.MONTH, -1)
        val prevMaxDay = prevCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in firstDayOfWeek - 1 downTo 0) {
            dayList.add(CalendarDateData((prevMaxDay - i).toString(), false, false, ""))
        }

        for (i in 1..maxDay) {
            val isToday = (year == tYear && month == tMonth && i == tDay)
            val formattedDate = String.format("%04d-%02d-%02d", year, month, i)

            dayList.add(CalendarDateData(i.toString(), true, isToday, formattedDate))
        }

        if (dayList.size % 7 != 0) {
            val nextMonthRemaining = 7 - (dayList.size % 7)
            for (i in 1..nextMonthRemaining) {
                dayList.add(CalendarDateData(i.toString(), false, false, ""))
            }
        }

        calendarAdapter.submitList(dayList)
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
            moveToDiaryWrite(selectedDate, "category")
            alertDialog.dismiss()
        }

        dialogView.findViewById<LinearLayout>(R.id.calendar_select_from_gallery_btn).setOnClickListener {
            moveToDiaryWrite(selectedDate, "gallery")
            alertDialog.dismiss()
        }
    }

    private fun moveToDiaryWrite(date: String, mode: String) {
        val bundle = Bundle().apply {
            putString("selectedDate", date)
            putString("selectMode", mode)
        }
        findNavController().navigate(R.id.action_calendar_to_diaryWrite, bundle)
    }

    /**
     * 날씨 권한 여부 확인 후 API 호출하는 함수.
     */
    private fun fetchLocationAndWeather() {
        // 권한이 있는지 확인
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    // weaklyWeatherViewModel.fetchWeeklyWeather(location.latitude, location.longitude, token)
                    weaklyWeatherViewModel.fetchWeeklyWeather(37.5665, 126.9780, BuildConfig.TEST_API_TOKEN)
                } else {
                    // 위치를 못 잡을 경우 기본값 (서울)
                    weaklyWeatherViewModel.fetchWeeklyWeather(37.5665, 126.9780, BuildConfig.TEST_API_TOKEN)
                }
            }
        } else {
            // 권한이 없을 경우 기본값 (서울)
            weaklyWeatherViewModel.fetchWeeklyWeather(37.5665, 126.9780, BuildConfig.TEST_API_TOKEN)
        }
    }



    override fun onResume() {
        super.onResume()
        codiDiaryViewModel.fetchDiaryDates(currentYear, currentMonth, BuildConfig.TEST_API_TOKEN)

        // 액션바 숨기기
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onPause() {
        super.onPause()
        // 다른 화면으로 나갈 때 다시 보이게 하기
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}