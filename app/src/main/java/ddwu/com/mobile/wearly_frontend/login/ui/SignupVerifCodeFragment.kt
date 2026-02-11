package ddwu.com.mobile.wearly_frontend.login.ui

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ddwu.com.mobile.wearly_frontend.databinding.FragmentSignupVerifCodeBinding
import ddwu.com.mobile.wearly_frontend.login.data.AuthViewModel
import kotlin.getValue

class SignupVerifCodeFragment: Fragment() {

    lateinit var binding: FragmentSignupVerifCodeBinding

    private val sharedViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupVerifCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 인증코드 요청
        sharedViewModel.sendEmailCode()

        sharedViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()){
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                sharedViewModel.errorMessage.value = null
            }
        }

        // (디자인) "다시 보내기" 밑줄
        binding.signupVcRetryTv.paintFlags = binding.signupVcRetryTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG




        // -------------- 클릭리스너 --------------

        // 개인정보 처리방침 약관 동의
        binding.signupAgreementLayout.setOnClickListener {
            val currentStatus = sharedViewModel.isTermsChecked.value ?: false
            val nextStatus = !currentStatus

            sharedViewModel.isTermsChecked.value = nextStatus
            binding.signupAgreementCheckbox.isSelected = nextStatus
        }

        // 개인정보 처리방침 상세 화면으로 이동
        binding.signupAgreementDetailIb.setOnClickListener {
            val intent = Intent(requireContext(), SignupAgreementActivity::class.java)
            startActivity(intent)
        }

        // 인증번호 유효성 검사 -> 뷰모델에 저장
        val codeLengthFilter = InputFilter.LengthFilter(6)
        binding.signupVcEt.filters = arrayOf(codeLengthFilter)
        binding.signupVcEt.setOnFocusChangeListener { view, hasFocus ->
            if(!hasFocus) {
                val v = view as EditText

                if (v.text.toString().length < 6) {
                    binding.signupVcWrongTv.text = "6자리의 인증번호를 확인해주세요."
                    binding.signupVcWrongTv.visibility = View.VISIBLE
                    v.isSelected = true
                }
                else {
                    binding.signupVcWrongTv.visibility = View.GONE
                    v.isSelected = false

                    sharedViewModel.verifCode.value = v.text.toString()
                }
            }
        }


        // Activity nextPage() 호출 요청
        binding.signupVcSubmitBtn.setOnClickListener {
            if (binding.signupVcEt.text.toString().isEmpty()) {
                binding.signupVcWrongTv.text = "인증번호를 입력해주세요."
                binding.signupVcWrongTv.visibility = View.VISIBLE
                binding.signupVcEt.isSelected = true
            }

            if (sharedViewModel.verifCode.value.isNullOrEmpty() && sharedViewModel.isTermsChecked.value != false) {
                //sharedViewModel.verifyCode(sharedViewModel.verifCode.value)
            }
        }


        // 로그인 화면으로 이동
        sharedViewModel.isSignUpSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                (activity as? SignupActivity)?.nextPage()
            }
        }

    }
}