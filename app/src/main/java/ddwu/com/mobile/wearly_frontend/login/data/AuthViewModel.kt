package ddwu.com.mobile.wearly_frontend.login.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel: ViewModel() {
    var userId = MutableLiveData<String>("")
    var userPassword = MutableLiveData<String>("")
    var gender = MutableLiveData<String>("") // 남성 | 여성 | 선택안함
    var userName = MutableLiveData<String>("")
    var birthDate = MutableLiveData<String>("")
    var email = MutableLiveData<String>("")
    var phoneNumber = MutableLiveData<String>("")


    // 인증 상태 관리
    val isEmailSent = MutableLiveData<Boolean>(false)
    val isEmailVerified = MutableLiveData<Boolean>(false)
    val errorMessage = MutableLiveData<String>("")

    // 회원가입
    val isSignUpSuccess = MutableLiveData<Boolean>(false)


    // 로그인
    val isLoginSuccess = MutableLiveData<Boolean>(false)



    fun sendEmailCode() {
        val emailValue = email.value ?: ""
        if (emailValue.isEmpty()) return

        val request = mapOf("email" to emailValue)

        Log.d("AuthVM", "테스트용 더미 log: ${emailValue}로 발송")

        RetrofitClient.authService.sendEmailCode(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    isEmailSent.value = true
                    Log.d("AuthVM", "코드 발송 성공: ${response.body()?.message}")
                } else {
                    isEmailSent.value = false
                    errorMessage.value = response.body()?.message ?: "발송 실패"
                }
            }
            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                isEmailSent.value = false
                errorMessage.value = "네트워크 연결 확인이 필요합니다."
            }
        })
    }

    fun verifyCode(code: String) {
        val emailValue = userId.value ?: ""
        if (emailValue.isEmpty() || code.isEmpty()) return

        val request = mapOf("email" to emailValue, "code" to code)

        Log.d("AuthVM", "테스트용 더미 log: '${code}' 인증")
        // 테스트용 임시
        requestSignup()

        RetrofitClient.authService.verifyEmailCode(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    isEmailVerified.value = true
                    Log.d("AuthVM", "인증 성공")

                    requestSignup()
                } else {
                    isEmailVerified.value = false
                    errorMessage.value = response.body()?.message ?: "인증번호가 일치하지 않습니다."
                }
            }
            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                isEmailVerified.value = false
                errorMessage.value = "네트워크 에러가 발생했습니다."
            }
        })
    }


    fun requestSignup() {
        val signupRequest = SignupRequest(
            userid = userId.value ?: "",
            userPassword = userPassword.value ?: "",
            userName = userName.value ?: "",
            birthDate = birthDate.value ?: "",
            gender = gender.value ?: "",
            email = email.value ?: "",
            phoneNumber = phoneNumber.value ?: ""
        )

        Log.d("AuthVM", "테스트용 더미 log: '$signupRequest' 요청")
        // 테스트용 임시
        isSignUpSuccess.value = true

        RetrofitClient.signupService.signUp(signupRequest).enqueue(object : Callback<SignupResponse> {

            override fun onResponse(call: Call<SignupResponse>, response: Response<SignupResponse>) {
                if (response.isSuccessful) {
                    // 성공 시 서버 응답 바디 확인
                    val result = response.body()
                    Log.d("AuthVM", "회원가입 성공: $result")
                    isSignUpSuccess.value = true
                } else {
                    // 서버 에러 (예: 400 Bad Request, 409 Conflict 등)
                    Log.e("AuthVM", "가입 거부됨. 코드: ${response.code()}")
                    errorMessage.value = "이미 사용 중인 아이디거나 형식이 맞지 않습니다."
                }
            }

            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                errorMessage.value = "서버 연결에 실패했습니다."
            }
        })
    }


    fun requestLogin() {
        val loginData = LoginRequest(
            loginId = userId.value ?: "",
            password = userPassword.value ?: ""
        )

        Log.d("AuthVM", "테스트용 더미 log: '${loginData.loginId}' 로그인 요청")
        // 테스트용 임시
        isLoginSuccess.value = true

        RetrofitClient.signupService.login(loginData).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val body = response.body()
                    Log.d("LoginVM", "로그인 성공 토큰: ${body?.accessToken}")

                    isLoginSuccess.value = true
                } else {
                    Log.e("LoginVM", "로그인 거부: ${response.code()}")
                    errorMessage.value = "아이디 또는 비밀번호를 확인해주세요."
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginVM", "서버 연결 실패: ${t.message}")
                errorMessage.value = "네트워크 상태를 확인해주세요."
            }
        })
    }
}
