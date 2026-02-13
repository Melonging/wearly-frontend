package ddwu.com.mobile.wearly_frontend.codidiary.ui

import CodiDiaryRecordRequest
import ddwu.com.mobile.wearly_frontend.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import ddwu.com.mobile.wearly_frontend.TokenManager
import ddwu.com.mobile.wearly_frontend.codidiary.data.viewmodel.CodiDiaryViewModel
import ddwu.com.mobile.wearly_frontend.databinding.FragmentCodiDiaryBinding

class CodiDiaryWriteFragment: Fragment() {

    private lateinit var binding: FragmentCodiDiaryBinding

    private val codiDiaryWriteViewModel: CodiDiaryViewModel by viewModels()

    private var isLiked = false

    // 카테고리별 배치 좌표 맵 (중복 제거 및 가독성을 위해 상단 배치)
    private val categoryLayoutMap = mapOf(
        "아우터" to Triple(0.25f, 0.25f, 1),
        "상의" to Triple(0.65f, 0.35f, 2),
        "바지" to Triple(0.75f, 0.75f, 5),
        "원피스" to Triple(0.75f, 0.75f, 5),
        "스커트" to Triple(0.75f, 0.75f, 5),
        "신발" to Triple(0.25f, 0.85f, 3),
        "가방" to Triple(0.85f, 0.50f, 6),
        "모자" to Triple(0.15f, 0.15f, 4),
        "액세서리" to Triple(0.50f, 0.50f, 7)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCodiDiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val selectedDate = arguments?.getString("selectedDate")
        binding.diaryDayTv.setText(selectedDate)

        val serverDate = selectedDate?.replace("년 ", "-")
            ?.replace("월 ", "-")
            ?.replace("일", "")
            ?.split("-")
            ?.let { parts ->
                val y = parts[0]
                val m = parts[1].padStart(2, '0')
                val d = parts[2].trim().padStart(2, '0')
                "$y-$m-$d"
            } ?: ""

        val iconCode = arguments?.getInt("weatherIcon") ?: 0
        binding.diaryWeatherIcon.setImageResource(getWeatherDrawable(iconCode))

        val temperature = arguments?.getString("temperature") ?: ""
        binding.diaryTempTv.setText(temperature)

        val temps = temperature.replace("°", "").split("/")
        val minTemp = temps.getOrNull(0)?.toIntOrNull() ?: 0
        val maxTemp = temps.getOrNull(1)?.toIntOrNull() ?: 0

        val selectedIds = arguments?.getIntArray("selectedClothIds") ?: intArrayOf(0)

        // 이전 프래그먼트에서 넘겨준 카테고리 및 이미지 정보 받기
        val selectedCategories = arguments?.getStringArray("selectedClothCategories")
        val selectedImages = arguments?.getStringArray("selectedClothImages")

        Log.d("DiaryWrite", "선택된 옷 ID들: ${selectedIds?.contentToString()}")

        // 이미지 배치 함수 호출
        if (selectedCategories != null && selectedImages != null) {
            displaySelectedOutfits(selectedCategories, selectedImages)
        }


        // --------------- 리스너 ---------------

        // 뒤로가기
        binding.diaryBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        // 다이어리 저장
        binding.diarySubmitBtn.setOnClickListener {
            val title = binding.diaryTitleEt.text.toString()
            val diary = binding.diaryEt.text.toString()

            if (title.isEmpty()) {
                binding.diaryTitleEt.hint = "제목을 입력해주세요!!"
                binding.diaryEt.hint = "일기를 입력해주세요!!"

                return@setOnClickListener
            }
            if (selectedDate.isNullOrEmpty()) {
                findNavController().popBackStack()
                Snackbar.make(binding.root, "오류가 발생했습니다. 다시 시도해주세요.", Snackbar.LENGTH_SHORT).show()
            }


            val request = CodiDiaryRecordRequest(
                wear_date = serverDate,
                clothes_ids = selectedIds.toList(),
                outfit_name = title,
                temp_min = minTemp,
                temp_max = maxTemp,
                weather_icon = iconCode.toString(),
                memo = diary,
                is_heart = isLiked
            )

            val token = TokenManager(requireContext()).getToken()

            if (!token.isNullOrEmpty()){
                codiDiaryWriteViewModel.saveRecord(token, isWeatherLog = true, request = request)
            } else {
            }
        }

        codiDiaryWriteViewModel.saveStatus.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Snackbar.make(binding.root, "코디 일기가 저장되었습니다.", Snackbar.LENGTH_SHORT).show()
                findNavController().popBackStack(R.id.calendarFragment, false)

            } else {
                Snackbar.make(binding.root, "저장에 실패했습니다. 다시 시도해주세요.", Snackbar.LENGTH_SHORT).show()
            }
        }


        // 좋아요
        binding.diaryLikeBtnToggle.setOnClickListener {
            isLiked = !isLiked

            if (isLiked){
                binding.diaryLikeSelected.visibility = View.VISIBLE
                binding.diaryLikeUnselected.visibility = View.GONE
            }
            else {
                binding.diaryLikeUnselected.visibility = View.VISIBLE
                binding.diaryLikeSelected.visibility = View.GONE
            }
        }
    }

    /**
     * 선택된 이미지들을 FrameLayout에 배치하는 함수
     */
    private fun displaySelectedOutfits(categories: Array<String>, images: Array<String>) {
        val container = binding.diaryClothesFrame
        container.removeAllViews()

        container.post {
            val parentWidth = container.width
            val parentHeight = container.height
            val viewSize = (parentWidth * 0.4f).toInt()

            for (i in categories.indices) {
                val categoryName = categories[i]
                val imageUrl = images[i]

                val layoutInfo = categoryLayoutMap[categoryName] ?: Triple(0.50f, 0.50f, 7)
                val (xRatio, yRatio, zIndex) = layoutInfo

                val imageView = ImageView(requireContext()).apply {
                    layoutParams = FrameLayout.LayoutParams(viewSize, viewSize)
                    scaleType = ImageView.ScaleType.CENTER_INSIDE
                    z = zIndex.toFloat()
                }

                imageView.x = (parentWidth * xRatio) - (viewSize / 2f)
                imageView.y = (parentHeight * yRatio) - (viewSize / 2f)

                Glide.with(this)
                    .load(imageUrl)
                    .apply(
                        RequestOptions()
                            .placeholder(R.color.box_gray)
                            .error(R.drawable.cloth_01)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                    )
                    .into(imageView)

                container.addView(imageView)
            }
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

        // 액션바 숨기기
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onPause() {
        super.onPause()
        // 다른 화면으로 나갈 때 다시 보이게 하기
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}