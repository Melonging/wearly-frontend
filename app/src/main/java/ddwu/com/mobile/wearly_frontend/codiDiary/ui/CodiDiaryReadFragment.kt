package ddwu.com.mobile.wearly_frontend.codiDiary.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import ddwu.com.mobile.wearly_frontend.BuildConfig
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.codiDiary.data.CodiDiaryReadCloth
import ddwu.com.mobile.wearly_frontend.codiDiary.data.CodiDiaryViewModel
import ddwu.com.mobile.wearly_frontend.databinding.FragmentCodiDiaryReadBinding

class CodiDiaryReadFragment : Fragment() {

    private lateinit var binding: FragmentCodiDiaryReadBinding
    private val codiDiaryViewModel: CodiDiaryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCodiDiaryReadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectedDate = arguments?.getString("selectedDate")
        binding.diaryReadDayTv.text = selectedDate

        if (selectedDate != null) {
            codiDiaryViewModel.fetchDiaryRead(selectedDate, BuildConfig.TEST_API_TOKEN)
        }

        codiDiaryViewModel.diaryReadData.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                binding.diaryReadDayTv.text = data.wear_date

                binding.diaryReadTitleTv.text = data.outfit?.outfit_name ?: "제목이 없습니다."

                val iconCode = data.weather?.weather_icon?.toIntOrNull() ?: 0
                val weatherResId = when (iconCode) {
                    0 -> R.drawable.img_weather_sunny
                    1 -> R.drawable.img_weather_cloudy
                    2 -> R.drawable.img_weather_rainy
                    3 -> R.drawable.img_weather_snowy
                    else -> R.drawable.img_weather_sunny
                }
                binding.diaryReadWeatherIcon.setImageResource(weatherResId)
                binding.diaryReadTempTv.text = "${data.weather?.temp_min?.toInt()}° / ${data.weather?.temp_max?.toInt()}°"

                binding.diaryReadTv.text = data.memo ?: "작성된 메모가 없습니다."

                val isHeart = data.outfit?.is_heart ?: false
                binding.diaryReadLikeSelected.isVisible = isHeart
                binding.diaryReadLikeUnselected.isVisible = !isHeart
            } else {
                Toast.makeText(requireContext(), "착용 기록을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // --------------- 리스너 ---------------

        // 뒤로가기
        binding.diaryReadBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        // 좋아요 토글 (로컬 UI 변경 예시 - 서버 연동 필요 시 추가 작업)
        binding.diaryReadLikeBtnToggle.setOnClickListener {
            val isCurrentlySelected = binding.diaryReadLikeSelected.isVisible
            binding.diaryReadLikeSelected.isVisible = !isCurrentlySelected
            binding.diaryReadLikeUnselected.isVisible = isCurrentlySelected
        }


    }

    override fun onResume() {
        super.onResume()

        // 액션바 숨기기
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onPause() {
        super.onPause()
        // 다른 화면으로 나갈 때 다시 보이게 하기
        (activity as AppCompatActivity).supportActionBar?.show()
    }

}