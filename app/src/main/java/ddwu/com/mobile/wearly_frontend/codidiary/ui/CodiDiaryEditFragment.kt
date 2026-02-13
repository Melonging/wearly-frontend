package ddwu.com.mobile.wearly_frontend.codidiary.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.TokenManager
import ddwu.com.mobile.wearly_frontend.codidiary.data.CodiDiaryEditRequest
import ddwu.com.mobile.wearly_frontend.codidiary.data.viewmodel.CodiDiaryViewModel
import ddwu.com.mobile.wearly_frontend.databinding.FragmentCodiDiaryBinding

class CodiDiaryEditFragment : Fragment() {

    private lateinit var binding: FragmentCodiDiaryBinding
    private val codiDiaryViewModel: CodiDiaryViewModel by activityViewModels()

    private var isLiked = false
    private var dateId: Int = -1

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

        dateId = arguments?.getInt("dateId") ?: -1
        val outfitName = arguments?.getString("outfitName")
        val memo = arguments?.getString("memo")
        isLiked = arguments?.getBoolean("isHeart") ?: false
        val wearDate = arguments?.getString("wearDate")
        val iconCode = arguments?.getString("weatherIcon")?.toIntOrNull() ?: 0
        val tempMin = arguments?.getDouble("tempMin") ?: 0.0
        val tempMax = arguments?.getDouble("tempMax") ?: 0.0

        val categories = arguments?.getStringArray("selectedClothCategories") ?: arrayOf()
        val images = arguments?.getStringArray("selectedClothImages") ?: arrayOf()



        if (wearDate != null) {
            val dateParts = wearDate.split("-")
            if (dateParts.size == 3) {
                val formattedDate = "${dateParts[0]}년 ${dateParts[1].toInt()}월 ${dateParts[2].toInt()}일"
                binding.diaryDayTv.text = formattedDate
            } else {
                binding.diaryDayTv.text = wearDate
            }
        }
        binding.diaryTitleEt.setText(outfitName)
        binding.diaryEt.setText(memo)
        binding.diaryTempTv.text = "${tempMin.toInt()}° / ${tempMax.toInt()}°"
        binding.diaryWeatherIcon.setImageResource(getWeatherDrawable(iconCode))

        updateLikeUI()

        if (categories.isNotEmpty() && images.isNotEmpty()) {
            displayOutfits(categories, images)
        }

        // --------------- 리스너 ---------------

        // 뒤로가기
        binding.diaryBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        // 좋아요 토글
        binding.diaryLikeBtnToggle.setOnClickListener {
            isLiked = !isLiked
            updateLikeUI()
        }

        // 수정 완료 (PATCH API 호출)
        binding.diarySubmitBtn.setOnClickListener {
            val title = binding.diaryTitleEt.text.toString()
            val content = binding.diaryEt.text.toString()

            if (title.isEmpty()) {
                Snackbar.make(binding.root, "제목을 입력해주세요!", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updateRequest = CodiDiaryEditRequest(
                outfit_name = title,
                memo = content,
                is_heart = isLiked,
                wear_date = wearDate,
                temp_min = tempMin,
                temp_max = tempMax,
                weather_icon = iconCode.toString()
            )

            val token = TokenManager(requireContext()).getToken()
            if (token != null && dateId != -1) {
                codiDiaryViewModel.updateRecord(token, dateId, updateRequest)
            }
        }

        codiDiaryViewModel.updateStatus.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                val token = TokenManager(requireContext()).getToken()
                val wearDate = arguments?.getString("wearDate")

                if (token != null && wearDate != null) {
                    codiDiaryViewModel.fetchDiaryRead(token, wearDate)

                    Snackbar.make(binding.root, "기록이 수정되었습니다.", Snackbar.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            } else {
                Snackbar.make(binding.root, "수정에 실패했습니다.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateLikeUI() {
        if (isLiked) {
            binding.diaryLikeSelected.visibility = View.VISIBLE
            binding.diaryLikeUnselected.visibility = View.GONE
        } else {
            binding.diaryLikeUnselected.visibility = View.VISIBLE
            binding.diaryLikeSelected.visibility = View.GONE
        }
    }


    /**
     * 이미지 배치 함수
     */
    private fun displayOutfits(categories: Array<String>, images: Array<String>) {
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