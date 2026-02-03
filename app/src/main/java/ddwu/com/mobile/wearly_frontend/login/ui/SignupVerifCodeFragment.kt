package ddwu.com.mobile.wearly_frontend.login.ui

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        // (디자인) "다시 보내기" 밑줄
        binding.signupVcRetryTv.paintFlags = binding.signupVcRetryTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG


        // 클릭리스너

        // Activity nextPage() 호출 요청
        binding.signupVcSubmitBtn.setOnClickListener {
            val inputCode = binding.signupVcEt.text.toString()
            sharedViewModel.verifyCode(inputCode)
        }



        // 로그인 화면으로 이동
        sharedViewModel.isSignUpSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                (activity as? SignupActivity)?.nextPage()
            }
        }

    }
}