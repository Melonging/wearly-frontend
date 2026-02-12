package ddwu.com.mobile.wearly_frontend.codiDiary.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.databinding.FragmentCodiDiaryReadBinding

class CodiDiaryReadFragment: Fragment() {

    private lateinit var binding: FragmentCodiDiaryReadBinding

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

//        val selectedDate = arguments?.getString("selectedDate")
//        binding.diaryDayTv.setText(selectedDate)
//
//
//        // --------------- 리스너 ---------------
//
//        // 뒤로가기
//        binding.diaryBackBtn.setOnClickListener {
//            findNavController().navigate(R.id.action_return_to_calendar)
//        }
//
//        // 다이어리 저장
//        binding.diarySubmitBtn.setOnClickListener {
//            findNavController().navigate(R.id.action_submit_diary)
//        }
//
//        // 좋아요
//        binding.diaryLikeBtnToggle.setOnClickListener {
//            if(binding.diaryLikeUnselected.isVisible){
//                binding.diaryLikeSelected.visibility = View.GONE
//            }
//            else{
//                binding.diaryLikeUnselected.visibility = View.GONE
//            }
//        }
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
