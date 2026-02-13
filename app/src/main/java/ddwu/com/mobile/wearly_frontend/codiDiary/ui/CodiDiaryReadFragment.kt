package ddwu.com.mobile.wearly_frontend.codiDiary.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import ddwu.com.mobile.wearly_frontend.BuildConfig
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.TokenManager
import ddwu.com.mobile.wearly_frontend.codiDiary.data.CodiDiaryReadCloth
import ddwu.com.mobile.wearly_frontend.codiDiary.data.viewmodel.CodiDiaryViewModel
import ddwu.com.mobile.wearly_frontend.databinding.FragmentCodiDiaryReadBinding

class CodiDiaryReadFragment : Fragment() {

    private lateinit var binding: FragmentCodiDiaryReadBinding

    private val codiDiaryViewModel: CodiDiaryViewModel by activityViewModels()

    private var isLiked = false

    val categoryLayoutMap = mapOf(
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
        binding = FragmentCodiDiaryReadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.diaryReadTv.movementMethod = android.text.method.ScrollingMovementMethod()

        val selectedDate = arguments?.getString("selectedDate")
        binding.diaryReadDayTv.text = selectedDate

        codiDiaryViewModel.diaryReadData.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                binding.diaryReadTitleTv.text = data.outfit?.outfit_name ?: "제목이 없습니다."

                val iconCode = data.weather?.weather_icon?.toIntOrNull() ?: 0
                val weatherResId = when (iconCode) {
                    0 -> R.drawable.ic_weather_sunny
                    1 -> R.drawable.ic_weather_cloudy
                    2 -> R.drawable.ic_weather_rainy
                    3 -> R.drawable.ic_weather_snowy
                    else -> R.drawable.ic_weather_sunny
                }
                binding.diaryReadWeatherIcon.setImageResource(weatherResId)
                binding.diaryReadTempTv.text = "${data.weather?.temp_min?.toInt()}° / ${data.weather?.temp_max?.toInt()}°"

                binding.diaryReadTv.text = data.memo ?: "작성된 메모가 없습니다."

                val isHeart = data.outfit?.is_heart ?: false
                binding.diaryReadLikeSelected.isVisible = isHeart
                binding.diaryReadLikeUnselected.isVisible = !isHeart

                displayOutfits(data.outfit?.clothes)
            } else {
                Snackbar.make(binding.root, "착용 기록을 불러올 수 없습니다.", Snackbar.LENGTH_SHORT).show()
            }
        }

        // --------------- 리스너 ---------------

        // 뒤로가기
        binding.diaryReadBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        // 일기 편집
        binding.diaryReadEditBtn.setOnClickListener {
            val currentData = codiDiaryViewModel.diaryReadData.value
            if (currentData != null) {
                val bundle = Bundle().apply {
                    putInt("dateId", currentData.date_id)
                    putString("outfitName", currentData.outfit?.outfit_name)
                    putString("memo", currentData.memo)
                    putBoolean("isHeart", currentData.outfit?.is_heart ?: false)
                    putString("wearDate", currentData.wear_date)
                    putDouble("tempMin", currentData.weather?.temp_min ?: 0.0)
                    putDouble("tempMax", currentData.weather?.temp_max ?: 0.0)
                    putString("weatherIcon", currentData.weather?.weather_icon)

                    val clothes = currentData.outfit?.clothes ?: emptyList()
                    putStringArray("selectedClothCategories", clothes.map { it.category_name }.toTypedArray())
                    putStringArray("selectedClothImages", clothes.map { it.image }.toTypedArray())
                }
                findNavController().navigate(R.id.action_edit_diary, bundle)
            }
        }


        // 일기 삭제
        binding.diaryReadDeleteBtn.setOnClickListener {
            val dateId = codiDiaryViewModel.diaryReadData.value?.date_id

            val token = TokenManager(requireContext()).getToken()
            if (token != null && dateId != null) {
                codiDiaryViewModel.deleteRecord(token, dateId)
            }
        }

        codiDiaryViewModel.deleteStatus.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess && codiDiaryViewModel.diaryReadData.value == null) {

                codiDiaryViewModel.resetDeleteStatus()

                Snackbar.make(binding.root, "기록이 삭제되었습니다.", Snackbar.LENGTH_SHORT).show()
                findNavController().popBackStack(R.id.calendarFragment, false)
            }
        }


        // 좋아요 토글
        binding.diaryReadLikeBtnToggle.setOnClickListener {
            isLiked = !isLiked

            if (isLiked){
                binding.diaryReadLikeSelected.visibility = View.VISIBLE
                binding.diaryReadLikeUnselected.visibility = View.GONE
            }
            else {
                binding.diaryReadLikeUnselected.visibility = View.VISIBLE
                binding.diaryReadLikeSelected.visibility = View.GONE
            }
        }


    }

    /**
     * 이미지 배치 함수
     *
     */
    private fun displayOutfits(clothes: List<CodiDiaryReadCloth>?) { // ClothDetail은 수민님의 옷 정보 DTO
        if (clothes.isNullOrEmpty()) return

        val container = binding.diaryReadClothesFrame
        container.removeAllViews()

        container.post {
            // 프래그먼트가 Context에 붙어 있는지 확인 (이탈 시 에러 방지)
            if (!isAdded || context == null) return@post

            val parentWidth = container.width
            val parentHeight = container.height

            val viewSize = (parentWidth * 0.4f).toInt()

            clothes.forEach { cloth ->
                val layoutInfo = categoryLayoutMap[cloth.category_name] ?: Triple(0.50f, 0.50f, 7)
                val (xRatio, yRatio, zIndex) = layoutInfo

                // requireContext() 대신 안전한 context 사용
                val imageView = ImageView(context).apply {
                    layoutParams = FrameLayout.LayoutParams(viewSize, viewSize)
                    scaleType = ImageView.ScaleType.CENTER_INSIDE
                    z = zIndex.toFloat()
                }

                imageView.x = (parentWidth * xRatio) - (viewSize / 2f)
                imageView.y = (parentHeight * yRatio) - (viewSize / 2f)

                Glide.with(this)
                    .load(cloth.image)
                    .apply(
                        RequestOptions()
                            .placeholder(R.color.box_gray)
                            .error(R.drawable.cloth_01)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                    )
                    .into(imageView)

                container.addView(imageView)
            }
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