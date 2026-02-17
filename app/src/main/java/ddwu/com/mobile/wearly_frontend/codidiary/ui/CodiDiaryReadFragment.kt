package ddwu.com.mobile.wearly_frontend.codidiary.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.TokenManager
import ddwu.com.mobile.wearly_frontend.codidiary.data.CodiDiaryEditRequest
import ddwu.com.mobile.wearly_frontend.codidiary.data.viewmodel.CodiDiaryViewModel
import ddwu.com.mobile.wearly_frontend.databinding.FragmentCodiDiaryReadBinding

class CodiDiaryReadFragment : Fragment() {

    private lateinit var binding: FragmentCodiDiaryReadBinding
    private val codiDiaryViewModel: CodiDiaryViewModel by activityViewModels()

    private var isLikedLocal = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCodiDiaryReadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.diaryReadTv.movementMethod = android.text.method.ScrollingMovementMethod()

        // 캘린더에서 넘어온 날짜 표시
        val selectedDate = arguments?.getString("selectedDate")
        binding.diaryReadDayTv.text = selectedDate

        codiDiaryViewModel.diaryReadData.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                Log.d(
                    "CodiDiaryRead",
                    "데이터 수신: date_id=${data.date_id}, is_heart=${data.is_heart}"
                )

                // 1) 제목: outfit_name이 “코디 제목”이고, 갤러리 사진 기록이면 outfit이 null일 수 있음
                binding.diaryReadTitleTv.text =
                    data.outfit?.outfit_name
                        ?: "기록"

                // 2) 날씨 아이콘
                val iconCode = data.weather?.weather_icon ?: "01d"
                binding.diaryReadWeatherIcon.setImageResource(getWeatherIcon(iconCode))

                // 3) 온도 표시 (weather 객체 안)
                val minTemp = data.weather?.temp_min
                val maxTemp = data.weather?.temp_max
                binding.diaryReadTempTv.text =
                    if (minTemp != null && maxTemp != null) {
                        "${minTemp.toInt()}° / ${maxTemp.toInt()}°"
                    } else {
                        "-° / -°"
                    }

                // 4) 메모
                binding.diaryReadTv.text = data.memo ?: "작성된 메모가 없습니다."

                // 5) 좋아요
                isLikedLocal = data.is_heart
                updateLikeUI(isLikedLocal)

                // 6) 이미지 로드: 최상위 image_url 사용(갤러리/코디 공통)
                if (!data.image_url.isNullOrEmpty()) {
                    loadMainImage(data.image_url)
                } else {
                    val fallback = data.outfit?.clothes?.firstOrNull()?.image
                    if (!fallback.isNullOrEmpty()) loadMainImage(fallback)
                }

            } else {
                Log.e("CodiDiaryRead", "데이터가 null입니다. 서버 응답/날짜 파라미터 확인 필요.")
            }
        }

        // --- 클릭 리스너 ---

        binding.diaryReadBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.diaryReadEditBtn.setOnClickListener {
            val currentData = codiDiaryViewModel.diaryReadData.value ?: return@setOnClickListener

            val bundle = Bundle().apply {
                putInt("dateId", currentData.date_id)
                putString("memo", currentData.memo)
                putBoolean("isHeart", currentData.is_heart)
                putString("wearDate", currentData.wear_date)

                putDouble("tempMin", currentData.weather?.temp_min ?: 0.0)
                putDouble("tempMax", currentData.weather?.temp_max ?: 0.0)
                putString("weatherIcon", currentData.weather?.weather_icon)

                putString("imageUrl", currentData.image_url)
                putString("outfitName", currentData.outfit?.outfit_name)
            }

            findNavController().navigate(R.id.action_edit_diary, bundle)
        }

        // 삭제
        binding.diaryReadDeleteBtn.setOnClickListener {
            val dateId = codiDiaryViewModel.diaryReadData.value?.date_id
            val token = TokenManager.getToken()
            if (token != null && dateId != null) {
                codiDiaryViewModel.deleteRecord(token, dateId)
            }
        }

        codiDiaryViewModel.deleteStatus.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess && isAdded) {
                codiDiaryViewModel.resetDeleteStatus()
                findNavController().popBackStack(R.id.calendarFragment, false)
            }
        }

        // 좋아요 토글
        binding.diaryReadLikeBtnToggle.setOnClickListener {
            val currentData = codiDiaryViewModel.diaryReadData.value ?: return@setOnClickListener
            val token = TokenManager.getToken() ?: return@setOnClickListener

            isLikedLocal = !isLikedLocal
            updateLikeUI(isLikedLocal)

            val editRequest = CodiDiaryEditRequest(is_heart = isLikedLocal)
            codiDiaryViewModel.updateRecord(token, currentData.date_id, editRequest)
        }
    }

    // 날씨 아이콘 4개 카테고리 매핑 함수
    private fun getWeatherIcon(iconCode: String): Int {
        return when (iconCode) {
            "01d", "01n" -> R.drawable.ic_weather_sunny
            "02d", "02n", "03d", "04d", "03n", "04n" -> R.drawable.ic_weather_cloudy
            "09d", "10d", "11d", "09n", "10n", "11n" -> R.drawable.ic_weather_rainy
            "13d", "13n" -> R.drawable.ic_weather_snowy
            else -> R.drawable.ic_weather_sunny
        }
    }

    private fun updateLikeUI(isHeart: Boolean) {
        binding.diaryReadLikeSelected.isVisible = isHeart
        binding.diaryReadLikeUnselected.isVisible = !isHeart
    }

    // 메인 코디 이미지 로드
    private fun loadMainImage(url: String) {
        val container = binding.diaryReadClothesFrame
        container.removeAllViews()

        val imageView = ImageView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_INSIDE
        }

        Glide.with(this)
            .load(url)
            .into(imageView)

        container.addView(imageView)
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
