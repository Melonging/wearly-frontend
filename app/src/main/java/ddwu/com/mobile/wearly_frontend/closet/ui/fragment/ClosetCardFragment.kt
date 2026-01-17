package ddwu.com.mobile.wearly_frontend.closet.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import ddwu.com.mobile.wearly_frontend.data.CodiRecord
import ddwu.com.mobile.wearly_frontend.databinding.FragmentClosetCardBinding
import ddwu.com.mobile.wearly_frontend.closet.ui.adapter.CodiRecordAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
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

        // 오늘 날짜 가져오기 및 리스트 생성
        val codiList = mutableListOf<CodiRecord>()
        val sdf = SimpleDateFormat("MM/dd (E)", Locale.KOREAN)
        val calendar = Calendar.getInstance()

        // 오늘부터 7일간의 날짜를 리스트에 담기
        for (i in 0 until 7) {
            val dateStr = sdf.format(calendar.time)
            codiList.add(CodiRecord(dateStr))
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        //리사이클러뷰 연결
        val recyclerView = binding.codiRecordRecyclerview
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = CodiRecordAdapter(codiList)

        //더보기 누르면 페이지 이동
        binding.viewMoreTv.setOnClickListener {
            // 페이지 이동 로직 구현
        }

        return binding.root
    }

    companion object {
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