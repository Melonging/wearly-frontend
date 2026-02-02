package ddwu.com.mobile.wearly_frontend.closet.ui.fragment

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.PopupWindow
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.material.bottomsheet.BottomSheetDialog
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.databinding.FragmentClosetCardBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ClosetCardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ClosetCardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding : FragmentClosetCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentClosetCardBinding.inflate(inflater, container, false)

        //이번 주 날짜 받아오기
        getWeekFullDates()

        //옷장 추가
        binding.addIconIv.setOnClickListener {
            val dialog = PlusClosetDialogFragment().apply {
                currentType = PlusClosetDialogFragment.WardrobeType.CLOSET
                currentName = "옷장1"
                listener = object : PlusClosetDialogFragment.OnWardrobeEditedListener {
                    override fun onWardrobeEdited(
                        type: PlusClosetDialogFragment.WardrobeType,
                        name: String
                    ) {
                        // TODO: 편집 결과 반영
                    }
                }
            }
            dialog.show(childFragmentManager, "PlusWardrobeDialog")
        }

        //옷장편집
        binding.moreOptionsIv.setOnClickListener {
            val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView = inflater.inflate(R.layout.layout_edit_popup, null)

            //PopupWindow 생성
            val popupWindow = PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
            )

            popupWindow.showAsDropDown(it) //clicked view (moreOptionsIv)

            //편집
            popupView.findViewById<View>(R.id.btn_edit).setOnClickListener {
                val dialog = EditClosetDialogFragment().apply {
                    currentType = EditClosetDialogFragment.WardrobeType.CLOSET
                    currentName = "옷장1"
                    listener = object : EditClosetDialogFragment.OnWardrobeEditedListener {
                        override fun onWardrobeEdited(
                            type: EditClosetDialogFragment.WardrobeType,
                            name: String
                        ) {
                            // TODO: 편집 결과 반영
                        }
                    }
                }
                dialog.show(childFragmentManager, "EditWardrobeDialog")
                popupWindow.dismiss()
            }

            //삭제
            popupView.findViewById<View>(R.id.btn_delete).setOnClickListener {
                popupWindow.dismiss()
            }
        }

        //옷장 옷 목록 조회
        binding.btnHanger1.setOnClickListener {

        }

        binding.btnHanger2.setOnClickListener {

        }

        binding.btnDrawer1.setOnClickListener {

        }

        binding.btnDrawer2.setOnClickListener {

        }

        return binding.root
    }

    private fun formatDate(date: Date): String {
        val dayFormat = SimpleDateFormat("d", Locale.getDefault())
        return dayFormat.format(date)
    }

    fun getWeekFullDates() {
        val calendar = Calendar.getInstance()
        val today = Date()

        val titleFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())
        binding.tvCalendarTitle.text = titleFormat.format(today)
        binding.tvWeatherInfo.text = "최고: 2° 최저: -10°" // 실제 데이터 연결?

        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val diffToSunday = -(currentDayOfWeek - 1)
        calendar.add(Calendar.DAY_OF_MONTH, diffToSunday)

        val weekDates = mutableListOf<String>()

        //날짜
        val dateViews = listOf(
            binding.tvDate1, binding.tvDate2, binding.tvDate3,
            binding.tvDate4, binding.tvDate5, binding.tvDate6, binding.tvDate7
        )

        //오늘날짜 표시
        val dateViewBg = listOf(
            binding.dateView1, binding.dateView2, binding.dateView3,
            binding.dateView4, binding.dateView5, binding.dateView6, binding.dateView7
        )

        val todayDateString = formatDate(Date())

        for (i in 0 until 7) {
            val dateString = formatDate(calendar.time)

            dateViews[i].text = dateString

            dateViewBg[i].setBackgroundResource(R.drawable.bg_closet_date_unselected)
            dateViews[i].setTextColor(Color.parseColor("#666666"))

            if (dateString == todayDateString) {
                dateViewBg[i].setBackgroundResource(R.drawable.bg_closet_date_selected)
                dateViews[i].setTextColor(Color.WHITE)
            }

            weekDates.add(dateString)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    companion object{
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ClosetCardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ClosetCardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

