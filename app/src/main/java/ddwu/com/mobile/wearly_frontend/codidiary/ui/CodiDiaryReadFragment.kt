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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.TokenManager
import ddwu.com.mobile.wearly_frontend.codidiary.data.CodiDiaryEditRequest
import ddwu.com.mobile.wearly_frontend.codidiary.data.viewmodel.CodiDiaryViewModel
import ddwu.com.mobile.wearly_frontend.databinding.FragmentCodiDiaryReadBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

        // [데이터 관찰] 평면 구조(Flat) 반영
        codiDiaryViewModel.diaryReadData.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                Log.d("CodiDiaryRead", "데이터 수신: date_id=${data.date_id}, is_heart=${data.is_heart}")

                // 1. 제목 및 날짜
                binding.diaryReadTitleTv.text = data.memo ?: "기록 제목"

                // 2. 날씨 아이콘 매핑 (4가지 상태)
                val iconCode = data.weather_icon ?: "01d"
                binding.diaryReadWeatherIcon.setImageResource(getWeatherIcon(iconCode))

                // 3. 온도 표시 (데이터에서 직접 추출)
                val minTemp = data.temp_min.toInt()
                val maxTemp = data.temp_max.toInt()
                binding.diaryReadTempTv.text = "$minTemp° / $maxTemp°"

                // 4. 메모
                binding.diaryReadTv.text = data.memo ?: "작성된 메모가 없습니다."

                // 5. 좋아요 (서버 필드명 is_heart 직접 참조)
                isLikedLocal = data.is_heart
                updateLikeUI(isLikedLocal)

                // 6. 이미지 로드 (현재는 clothes 리스트가 없으므로 메인 image_url 사용)
                if (!data.image_url.isNullOrEmpty()) {
                    loadMainImage(data.image_url)
                }
            } else {
                Log.e("CodiDiaryRead", "데이터가 null입니다. 날짜 형식이 서버와 맞는지 확인하세요.")
            }
        }

        // --- 클릭 리스너 ---

        binding.diaryReadBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        // 수정 화면으로 데이터 전달 (평면 구조에 맞춤)
        binding.diaryReadEditBtn.setOnClickListener {
            val currentData = codiDiaryViewModel.diaryReadData.value
            if (currentData != null) {
                val bundle = Bundle().apply {
                    putInt("dateId", currentData.date_id)
                    putString("memo", currentData.memo)
                    putBoolean("isHeart", currentData.is_heart)
                    putString("wearDate", currentData.wear_date)
                    putDouble("tempMin", currentData.temp_min)
                    putDouble("tempMax", currentData.temp_max)
                    putString("weatherIcon", currentData.weather_icon)
                    putString("imageUrl", currentData.image_url)
                }
                findNavController().navigate(R.id.action_edit_diary, bundle)
            }
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

    // 메인 코디 이미지 로드 (clothes 리스트가 없을 경우 대비)
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