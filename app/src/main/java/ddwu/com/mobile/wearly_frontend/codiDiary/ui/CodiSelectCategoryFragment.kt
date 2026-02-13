package ddwu.com.mobile.wearly_frontend.codiDiary.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.TokenManager
import ddwu.com.mobile.wearly_frontend.codiDiary.data.ClothItem
import ddwu.com.mobile.wearly_frontend.codiDiary.data.viewmodel.CodiDiaryViewModel
import ddwu.com.mobile.wearly_frontend.codiDiary.data.viewmodel.WeatherViewModel
import ddwu.com.mobile.wearly_frontend.codiDiary.ui.adapter.CodiClothesAdapter
import ddwu.com.mobile.wearly_frontend.codiDiary.ui.adapter.SelectedCodiClothesAdapter
import ddwu.com.mobile.wearly_frontend.databinding.FragmentCodiSelectFromCategoryBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CodiSelectCategoryFragment : Fragment() {

    private lateinit var binding: FragmentCodiSelectFromCategoryBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val weatherViewModel: WeatherViewModel by activityViewModels()
    private val codiDiaryViewModel : CodiDiaryViewModel by activityViewModels()

    private lateinit var codiClothesAdapter: CodiClothesAdapter

    private val selectedItems = mutableListOf<ClothItem>()
    private lateinit var selectedAdapter: SelectedCodiClothesAdapter

    private var currentIconCode: Int = 0
    private var currentTemp: String = "0°/0°"

    // 현재 선택된 카테고리 이름을 저장할 변수
    private var currentCategoryName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCodiSelectFromCategoryBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectedDateStr = arguments?.getString("selectedDate")
        binding.diarySelectCategoryDateTv.text = selectedDateStr

        val token = TokenManager(requireContext()).getToken() ?: ""

        // 어댑터 초기화 및 연결
        setupAdapters()


        // 카테고리 로드 및 탭 설정
        codiDiaryViewModel.fetchCategories(token)
        codiDiaryViewModel.categoryList.observe(viewLifecycleOwner) { categories ->
            if (!categories.isNullOrEmpty()) {
                binding.diarySelectCategoryTb.removeAllTabs()
                for (category in categories) {
                    val tab = binding.diarySelectCategoryTb.newTab()
                    tab.text = category.name
                    tab.tag = category.category_id
                    binding.diarySelectCategoryTb.addTab(tab)
                }

                // 초기 카테고리 이름 세팅
                currentCategoryName = categories[0].name
                codiDiaryViewModel.fetchClothes(token, categories[0].category_id)
            }
        }

        binding.diarySelectCategoryTb.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                val categoryId = tab?.tag as? Int
                currentCategoryName = tab?.text.toString() // 탭 변경 시 카테고리 이름 갱신
                if (categoryId != null) {
                    codiDiaryViewModel.fetchClothes(token, categoryId)
                }
            }
            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                val categoryId = tab?.tag as? Int
                if (categoryId != null) {
                    codiDiaryViewModel.fetchClothes(token, categoryId)
                }
            }
        })


        // 옷 목록 관찰
        codiDiaryViewModel.clothesList.observe(viewLifecycleOwner) { clothes ->
            if (clothes != null) {
                codiClothesAdapter.submitList(clothes)
                val ids = selectedItems.map { it.clothing_id }.toSet()
                codiClothesAdapter.updateSelectedIds(ids)
            }
        }

        // 날씨 기록
        if (selectedDateStr != null) {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일")
            val selectedDate = LocalDate.parse(selectedDateStr, inputFormatter)
            val formattedDateForApi = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val today = LocalDate.now()

            if (selectedDate.isBefore(today)) {
                // 과거 날씨 호출
                fetchPastWeatherWithLocation(formattedDateForApi)

                weatherViewModel.pastWeatherData.observe(viewLifecycleOwner) { pastWeather ->
                    if (pastWeather != null) {
                        currentIconCode = pastWeather.weatherIcon
                        currentTemp = pastWeather.temperature
                        binding.diarySelectCategoryWeatherIcon.setImageResource(getWeatherDrawable(pastWeather.weatherIcon))
                        binding.diarySelectCategoryTemperatureTv.text = pastWeather.temperature
                    }
                }
            } else {
                // 현재, 미래 날씨
                val searchDate = "${selectedDate.monthValue}/${selectedDate.dayOfMonth}"

                weatherViewModel.weaklyWeatherData.observe(viewLifecycleOwner) { list ->
                    val dayWeather = list.find { it.date == searchDate }
                    dayWeather?.let {
                        currentIconCode = it.weatherIcon
                        currentTemp = it.temperature
                        binding.diarySelectCategoryWeatherIcon.setImageResource(getWeatherDrawable(currentIconCode))
                        binding.diarySelectCategoryTemperatureTv.text = currentTemp
                    }
                }
            }
        }

        // 버튼 리스너
        binding.diarySelectCategoryBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.diarySelectCategorySubmitBtn.setOnClickListener {
            val bundle = Bundle().apply {
                putString("selectedDate", selectedDateStr)
                putInt("weatherIcon", currentIconCode)
                putString("temperature", currentTemp)
                putIntArray("selectedClothIds", selectedItems.map { it.clothing_id }.toIntArray())
                // 수정된 ClothItem의 category_name 필드를 사용
                putStringArray("selectedClothCategories", selectedItems.map { it.category_name ?: "기타" }.toTypedArray())
                // 배치에 꼭 필요한 이미지 URL 리스트 추가
                putStringArray("selectedClothImages", selectedItems.map { it.image }.toTypedArray())
            }
            findNavController().navigate(R.id.action_codi_select_to_diaryWrite, bundle)
        }
    }

    private fun setupAdapters() {
        // 선택된 옷 어댑터
        selectedAdapter = SelectedCodiClothesAdapter { cloth ->
            selectedItems.removeAll { it.clothing_id == cloth.clothing_id }
            syncSelectionState()
        }
        binding.diarySelectCategorySelectedRv.adapter = selectedAdapter

        // 카테고리 옷 어댑터
        codiClothesAdapter = CodiClothesAdapter { cloth ->
            val isAlreadySelected = selectedItems.any { it.clothing_id == cloth.clothing_id }
            if (isAlreadySelected) {
                selectedItems.removeAll { it.clothing_id == cloth.clothing_id }
            } else {
                // 핵심: 옷을 추가할 때 현재 선택된 탭의 카테고리 이름을 주입함
                cloth.category_name = currentCategoryName
                selectedItems.add(cloth)
            }
            syncSelectionState()
        }
        binding.diarySelectCategoryRv.adapter = codiClothesAdapter
    }

    // 선택된 옷과 카테고리 옷 목록 동기화
    private fun syncSelectionState() {
        selectedAdapter.submitList(selectedItems.toList())
        binding.diarySelectCategorySelectedItemsTv.text = "${selectedItems.size}개"

        val ids = selectedItems.map { it.clothing_id }.toSet()
        codiClothesAdapter.updateSelectedIds(ids)
    }

    /**
     * 현재 위치 좌표를 가져와서 ViewModel의 과거 날씨 API 호출
     */
    private fun fetchPastWeatherWithLocation(date: String) {
        val token = TokenManager(requireContext()).getToken() ?: ""

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    weatherViewModel.fetchPastWeather(37.5665, 126.9780, date, token)
                } else {
                    weatherViewModel.fetchPastWeather(37.5665, 126.9780, date, token)
                }
            }
        } else {
            weatherViewModel.fetchPastWeather(37.5665, 126.9780, date, token)
        }
    }

    /**
     * 날씨 코드를 아이콘으로 변환
     */
    private fun getWeatherDrawable(iconCode: Int): Int {
        return when (iconCode) {
            0 -> R.drawable.ic_weather_sunny
            1 -> R.drawable.ic_weather_cloudy
            2 -> R.drawable.ic_weather_rainy
            3 -> R.drawable.ic_weather_snowy
            else -> R.drawable.ic_weather_sunny
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onPause() {
        super.onPause()
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}