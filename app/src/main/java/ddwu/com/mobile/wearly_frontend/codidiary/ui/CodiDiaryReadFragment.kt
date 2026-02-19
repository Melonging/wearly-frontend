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
import ddwu.com.mobile.wearly_frontend.codidiary.data.DiaryClothItem
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
                val clothes = data.outfit?.clothes
                val hasLayout = clothes?.any { it.layout != null } == true

                when {
                    clothes != null && clothes.isNotEmpty() && hasLayout -> {
                        displayClothesWithLayout( binding.diaryReadClothesFrame,
                            clothes)
                    }

                    !data.image_url.isNullOrEmpty() -> {
                        loadMainImage(data.image_url)
                    }

                    else -> {
                        val fallback = clothes?.firstOrNull()?.image
                        if (!fallback.isNullOrEmpty()) loadMainImage(fallback)
                        else binding.diaryReadClothesFrame.removeAllViews()
                    }
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


    /*

    data class Pos(val x: Float, val y: Float, val scale: Float, val z: Float)

    private fun basePos(category: String?, hasOuter: Boolean): Pos {
        return if (hasOuter) {
            //  아우터 있는 날
            when (category) {
                "아우터" -> Pos(0.35f, 0.35f, 1.15f, 6f)
                "상의"   -> Pos(0.62f, 0.35f, 1.65f, 5f)
                "바지"   -> Pos(0.70f, 0.60f, 1.65f, 4f)
                "스커트/원피스" -> Pos(0.70f, 0.60f, 1.65f, 4f)
                "신발"   -> Pos(0.35f, 0.69f, 1.25f, 7f)
                "가방"   -> Pos(0.78f, 0.55f, 1.00f, 7f)
                else     -> Pos(0.50f, 0.50f, 1.25f, 1f)
            }
        } else {
            //  아우터 없는 날
            when (category) {
                "상의"   -> Pos(0.40f, 0.35f, 1.45f, 4f)
                "바지"   -> Pos(0.70f, 0.60f, 1.75f, 5f)
                "스커트/원피스" -> Pos(0.70f, 0.60f, 1.65f, 4f)
                "신발"   -> Pos(0.40f, 0.72f, 1.25f, 7f)
                "가방"   -> Pos(0.78f, 0.55f, 1.00f, 6f)
                else     -> Pos(0.50f, 0.50f, 1.25f, 1f)
            }
        }
    }


    private fun displayClothesWithLayout(clothes: List<DiaryClothItem>) {

        val container = binding.diaryReadClothesFrame
        container.removeAllViews()

        container.post {

            val pw = container.width.toFloat()
            val ph = container.height.toFloat()
            if (pw == 0f || ph == 0f) return@post

            val stage = minOf(pw, ph)
            val left = (pw - stage) / 2f
            val top  = (ph - stage) / 2f

            val hasOuter = clothes.any { it.category_name == "아우터" }

            clothes.forEach { cloth ->

                val pos = basePos(cloth.category_name, hasOuter)

                val size = (stage * 0.45f * pos.scale).toInt()

                val iv = ImageView(requireContext()).apply {
                    layoutParams = FrameLayout.LayoutParams(size, size)
                    scaleType = ImageView.ScaleType.FIT_CENTER

                    z = pos.z
                }

                val x = left + stage * pos.x - size / 2f
                val y = top  + stage * pos.y - size / 2f

                iv.x = x
                iv.y = y

                Glide.with(this)
                    .load(cloth.image)
                    .into(iv)

                container.addView(iv)
            }
        }
    }

     */


    private fun scaleFor(cat: String): Float = when (cat) {
        "아우터" -> 0.65f
        "상의" -> 0.65f
        "바지", "원피스", "스커트" -> 0.65f
        "신발" -> 0.50f
        "가방" -> 0.40f
        else -> 0.5f
    }




    private fun displayClothesWithLayout(
        container: FrameLayout,
        items: List<DiaryClothItem>
    ) {
        container.removeAllViews()

        container.post {
            val pw = container.width.toFloat()
            val ph = container.height.toFloat()
            if (pw <= 0f || ph <= 0f) return@post

            val stage = minOf(pw, ph)
            val left = (pw - stage) / 2f
            val top = (ph - stage) / 2f

            val sorted = items
                .mapNotNull { c -> c.layout?.let { l -> c to l } }
                .sortedBy { it.second.z_index }

            sorted.forEach { (cloth, l) ->

                val scale = scaleFor(cloth.category_name ?: "")
                val size = (stage * scale).toInt()

                val iv = ImageView(container.context).apply {
                    layoutParams = FrameLayout.LayoutParams(size, size)
                    scaleType = ImageView.ScaleType.FIT_CENTER
                    z = l.z_index.toFloat()
                }

                val cx = left + stage * l.x_ratio.toFloat()
                val cy = top + stage * l.y_ratio.toFloat()

                iv.x = cx - size / 2f
                iv.y = cy - size / 2f

                Glide.with(container).load(cloth.image).into(iv)
                container.addView(iv)
            }
        }
    }



    /*

    private fun displayClothesWithLayout(clothes: List<DiaryClothItem>) {

        val container = binding.diaryReadClothesFrame
        container.removeAllViews()

        container.post {

            val pw = container.width.toFloat()
            val ph = container.height.toFloat()

            if (pw == 0f || ph == 0f) return@post

            clothes
                .filter { it.layout != null }
                .sortedBy { it.layout!!.z_index }
                .forEach { cloth ->

                    val layout = cloth.layout!!

                    val size = (pw * 0.4f).toInt() // 일단 고정

                    val iv = ImageView(requireContext()).apply {
                        layoutParams = FrameLayout.LayoutParams(size, size)
                        scaleType = ImageView.ScaleType.FIT_CENTER
                        z = layout.z_index.toFloat()
                    }

                    val cx = pw * layout.x_ratio.toFloat()
                    val cy = ph * layout.y_ratio.toFloat()

                    iv.x = cx - size / 2f
                    iv.y = cy - size / 2f

                    Glide.with(this)
                        .load(cloth.image)
                        .into(iv)

                    container.addView(iv)
                }
        }
    }

     */









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
