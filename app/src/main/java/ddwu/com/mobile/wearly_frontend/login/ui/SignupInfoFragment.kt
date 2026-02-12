package ddwu.com.mobile.wearly_frontend.login.ui

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
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


        // --------------- 이벤트 리스너 ---------------

        // 이름 유효성 검사 -> 뷰모델에 저장
        binding.signupNameEt.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                val v = view as EditText
                val errorText = binding.signupNameWrongTv
                val input = v.text.toString()

                errorText.visibility = View.GONE
                v.isSelected = false
                sharedViewModel.userName.value = input
            }
        }

        // 성별 유효성 검사 -> 뷰모델에 저장
        binding.signupManBtn.setOnClickListener {
            if (binding.signupManBtn.isChecked) {
                binding.signupWomanBtn.isChecked = false

                sharedViewModel.gender.value = "남성"
            }
            else if (!binding.signupWomanBtn.isChecked) {
                sharedViewModel.gender.value = "선택안함"
            }
        }
        binding.signupWomanBtn.setOnClickListener {
            if (binding.signupWomanBtn.isChecked) {
                binding.signupManBtn.isChecked = false

                sharedViewModel.gender.value = "여성"
            }
            else if (!binding.signupManBtn.isChecked) {
                sharedViewModel.gender.value = "선택안함"
            }
        }

        // 생년월일 유효성 검사 -> 뷰모델에 저장
        val birthLengthFilter = InputFilter.LengthFilter(6)
        binding.signupBirthEt.filters = arrayOf(birthLengthFilter)
        binding.signupBirthEt.setOnFocusChangeListener { view, hasFocus ->
            if(!hasFocus) {
                val v = view as EditText
                val input = v.text.toString()
                val sdf = java.text.SimpleDateFormat("yyMMdd", java.util.Locale.KOREA)
                sdf.isLenient = false

                // 생일이 잘못된 형식일 때
                try {
                    val parsedDate = sdf.parse(input)
                    parsedDate != null && parsedDate.before(java.util.Date())
                    binding.signupBirthWrongTv.visibility = View.GONE
                    v.isSelected = false

                    sharedViewModel.birthDate.value = input
                } catch (e: Exception) {
                    binding.signupBirthWrongTv.text = "잘못된 형식입니다. 다시 입력해주세요."
                    binding.signupBirthWrongTv.visibility = View.VISIBLE
                    v.isSelected = true
                }
            }
        }

        //이메일 유효성 검사 -> 뷰모델에 저장
        binding.signupIdEt.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                val v = view as EditText
                val errorText = binding.signupIdWrongTv
                val input = v.text.toString()

                when {
                    // 이메일 형식이 아닐 때
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches() -> {
                        errorText.text = "잘못된 형식입니다. 다시 입력해주세요."
                        errorText.visibility = View.VISIBLE
                        v.isSelected = true
                    }

                    else -> {
                        errorText.visibility = View.GONE
                        v.isSelected = false
                        sharedViewModel.email.value = input
                        sharedViewModel.userId.value = input
                    }
                }
            }
        }

        // 휴대폰 번호 유효성 검사 -> 뷰모델에 저장
        val phnoLengthFilter = InputFilter.LengthFilter(11)
        binding.signupPhnoEt.filters = arrayOf(phnoLengthFilter)
        binding.signupPhnoEt.setOnFocusChangeListener { view, hasFocus ->
            if(!hasFocus) {
                val v = view as EditText
                val errorText = binding.signupPhnoWrongTv
                val input = v.text.toString()

                when {
                    // 형식이 잘못 되었을 때
                    input.length < 11 || !input.startsWith("010") -> {
                        errorText.text = "잘못된 형식입니다. 다시 입력해주세요."
                        errorText.visibility = View.VISIBLE
                        v.isSelected = true
                    }

                    else -> {
                        errorText.visibility = View.GONE
                        v.isSelected = false
                        sharedViewModel.phoneNumber.value = input
                    }

                }
            }

        }

        // 비밀번호 유효성 검사 -> 뷰모델에 저장
        binding.signupPwEt.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                val v = view as EditText
                val errorText = binding.signupPwWrongTv
                val input = v.text.toString()

                when {
                    // 비밀번호가 8자 미만일 때
                    input.length < 8 -> {
                        errorText.text = "8자 이상 입력해주세요."
                        errorText.visibility = View.VISIBLE
                        v.isSelected = true
                    }

                    else -> {
                        errorText.visibility = View.GONE
                        v.isSelected = false
                    }
                }
            }

        }
        binding.signupPwCheckEt.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                val v = view as EditText
                val errorText = binding.signupPwCheckWrongTv
                val input = v.text.toString()

                when {
                    // 비밀번호 일치하지 않을 때
                    !input.equals(binding.signupPwEt.text.toString()) -> {
                        errorText.text = "비밀번호가 일치하지 않습니다."
                        errorText.visibility = View.VISIBLE
                        v.isSelected = true
                    }

                    else -> {
                        errorText.visibility = View.GONE
                        v.isSelected = false
                        sharedViewModel.userPassword.value = input
                    }
                }
            }

        }

        // Activity nextPage(), 인증번호 전송 호출 요청
        binding.signupSubmitBtn.setOnClickListener {
            // 이름이 빈칸일 때
            val nameInput = binding.signupNameEt
            if (nameInput.text.toString().isEmpty()) {
                binding.signupNameWrongTv.text = "이름을 입력해주세요."
                binding.signupNameWrongTv.visibility = View.VISIBLE
                nameInput.isSelected = true
            }

            // 생일이 빈칸일 때
            if (binding.signupBirthEt.text.toString().isEmpty()) {
                binding.signupBirthWrongTv.text = "생년월일을 입력해주세요."
                binding.signupBirthWrongTv.visibility = View.VISIBLE
                binding.signupBirthEt.isSelected = true
            }

            // 이메일이 빈칸일 때
            if (binding.signupIdEt.text.toString().isEmpty()) {
                binding.signupIdWrongTv.text = "이메일을 입력해주세요."
                binding.signupIdWrongTv.visibility = View.VISIBLE
                binding.signupIdEt.isSelected = true
            }

            // 휴대폰 번호가 빈칸일 때
            if (binding.signupPhnoEt.text.toString().isEmpty()) {
                binding.signupPhnoWrongTv.text = "휴대폰 번호를 입력해주세요."
                binding.signupPhnoWrongTv.visibility = View.VISIBLE
                binding.signupPhnoEt.isSelected = true
            }

            // 비밀번호가 빈칸일 때
            if (binding.signupPwEt.text.toString().isEmpty()) {
                binding.signupPwWrongTv.text = "비밀번호를 입력해주세요."
                binding.signupPwWrongTv.visibility = View.VISIBLE
                binding.signupPwEt.isSelected = true
            }
            if (binding.signupPwCheckEt.text.toString().isEmpty()) {
                binding.signupPwCheckWrongTv.text = "비밀번호를 입력해주세요."
                binding.signupPwCheckWrongTv.visibility = View.VISIBLE
                binding.signupPwCheckEt.isSelected = true
            }


            if (!sharedViewModel.isFormFilled()) {
                return@setOnClickListener
            }

            (activity as? SignupActivity)?.nextPage()
        }


        // "로그인"
        binding.signupLoginTv.setOnClickListener {
            activity?.finish()
        }
    }
}