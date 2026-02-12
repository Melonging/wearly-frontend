package ddwu.com.mobile.wearly_frontend.codiDiary.ui

import ddwu.com.mobile.wearly_frontend.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ddwu.com.mobile.wearly_frontend.codiDiary.data.CodiDiaryRecordRequest
import ddwu.com.mobile.wearly_frontend.codiDiary.data.CodiDiaryViewModel
import ddwu.com.mobile.wearly_frontend.databinding.FragmentCodiDiaryBinding

class CodiDiaryWriteFragment: Fragment() {

    private lateinit var binding: FragmentCodiDiaryBinding

    private val codiDiaryWriteViewModel: CodiDiaryViewModel by viewModels()

    private val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsImxvZ2luSWQiOiJ0ZXN0dXNlciIsInR5cGUiOiJhY2Nlc3MiLCJpYXQiOjE3NzA4MzA2OTUsImV4cCI6MTc3MDgzNDI5NX0.yvetEy-ixhgiZhr2N04QzDk7AEpyuN75Wc_3OkQ4Yts"

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


        // --------------- 리스너 ---------------
        // 뒤로가기
        binding.diaryBackBtn.setOnClickListener {
            findNavController().popBackStack(R.id.calendarFragment, false)
        }

        // 다이어리 저장
        binding.diarySubmitBtn.setOnClickListener {
            val title = binding.diaryTitleEt.text.toString()
            val memo = binding.diaryEt.text.toString()

            if (title.isEmpty()) {
                binding.diaryTitleEt.hint = "코디 제목을 입력해주세요!!"
                binding.diaryEt.hint = "오늘 있었던 일을 알려주세요!!"
                return@setOnClickListener
            }

            val request = CodiDiaryRecordRequest(
                wear_date =  binding.diaryDayTv.text.toString(),
                clothes_ids = listOf(1, 2, 5),
                outfit_name = title,
                latitude = 37.5665,
                longitude = 126.9780,
                memo = memo,
                is_heart = false
            )

            codiDiaryWriteViewModel.saveDiary(request)
        }

        codiDiaryWriteViewModel.saveStatus.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(requireContext(), "기록이 저장되었습니다.", Toast.LENGTH_SHORT).show()

                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "저장에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }


        // 좋아요
        binding.diaryLikeBtnToggle.setOnClickListener {
            if(binding.diaryLikeUnselected.isVisible){
                binding.diaryLikeSelected.visibility = View.GONE
            }
            else{
                binding.diaryLikeUnselected.visibility = View.GONE
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