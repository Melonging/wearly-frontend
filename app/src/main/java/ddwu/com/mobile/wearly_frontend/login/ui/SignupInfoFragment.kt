package ddwu.com.mobile.wearly_frontend.login.ui

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ddwu.com.mobile.wearly_frontend.databinding.FragmentSignupInfoBinding
import ddwu.com.mobile.wearly_frontend.login.data.AuthViewModel

class SignupInfoFragment: Fragment() {
    lateinit var binding: FragmentSignupInfoBinding

    private val sharedViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // (디자인) 회원가입 화면 "로그인" 밑줄
        binding.signupLoginTv.paintFlags = binding.signupLoginTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG


        // 클릭리스너



        // Activity nextPage() 호출 요청
        binding.signupSubmitBtn.setOnClickListener {

            sharedViewModel.userName.value = binding.signupNameEt.text.toString()
            sharedViewModel.birthDate.value = binding.signupBirthEt.text.toString()
            sharedViewModel.userName.value = binding.signupNameEt.text.toString()

            // 성별 선택
            val selectedGender = when (binding.signupGenderGroup.checkedRadioButtonId) {
                binding.signupManBtn.id-> "남성"
                binding.signupWomanBtn.id -> "여성"
                else -> "선택안함"
            }

            sharedViewModel.gender.value = selectedGender

            sharedViewModel.userId.value = binding.signupIdEt.text.toString()
            sharedViewModel.email.value = binding.signupIdEt.text.toString()
            sharedViewModel.phoneNumber.value = binding.signupPhnoEt.text.toString()

            sharedViewModel.userPassword.value = binding.signupPwEt.text.toString()
            
            // 인증번호 전송 함수 호출
            sharedViewModel.sendEmailCode()


            (activity as? SignupActivity)?.nextPage()
        }


        // "로그인"
        binding.signupLoginTv.setOnClickListener {
            activity?.finish()
        }

        // 성별 버튼 체크 해제
        val genderButtonList = listOf(binding.signupManBtn, binding.signupWomanBtn)
        genderButtonList.forEach { button ->
            button.setOnClickListener {
                if (!button.isSelected) {
                    binding.signupGenderGroup.clearCheck()
                    button.isChecked = true
                    button.isSelected = true
                    genderButtonList.filter { it != button }.forEach { it.isSelected = false }
                } else {
                    binding.signupGenderGroup.clearCheck()
                    button.isSelected = false
                }
            }
        }
    }
}