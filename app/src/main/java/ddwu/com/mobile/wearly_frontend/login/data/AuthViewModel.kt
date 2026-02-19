package ddwu.com.mobile.wearly_frontend.login.data

import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import ddwu.com.mobile.wearly_frontend.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel: ViewModel() {
    var userId = MutableLiveData<String>()
    var userPassword = MutableLiveData<String>()
    var gender = MutableLiveData<String>("선택안함") // 남성 | 여성 | 선택안함
    var userName = MutableLiveData<String>()
    var birthDate = MutableLiveData<String>()
    var email = MutableLiveData<String>()
    var phoneNumber = MutableLiveData<String>()

    val errorMessage = MutableLiveData<String>()



    // 인증 상태 관리
    val verifCode = MutableLiveData<String>()
    val isEmailSent = MutableLiveData<Boolean>(false)
    val isEmailVerified = MutableLiveData<Boolean>(false)



    // 회원가입
    val isSignUpSuccess = MutableLiveData<Boolean>(false)
    val isTermsChecked = MutableLiveData<Boolean>(false)

    /**
     * 회원가입 시 필요한 정보가 전부 입력되었는지 확인하는 함수
     */
    fun isFormFilled(): Boolean {
        return !userName.value.isNullOrEmpty() &&
                !gender.value.isNullOrEmpty() &&
                !birthDate.value.isNullOrEmpty() &&
                !email.value.isNullOrEmpty() &&
                !userId.value.isNullOrEmpty() &&
                !phoneNumber.value.isNullOrEmpty() &&
                !userPassword.value.isNullOrEmpty()
    }


    // 로그인
    val loginResponse = MutableLiveData<LoginResponse?>()
    val isLoginSuccess = MutableLiveData<Boolean>(false)


    /**
     * 인증번호 전송 요청 함수
     * 프레그먼트의 요청에 따라 뷰모델에 저장된 이메일로 인증번호 전송을 요청한다.
     */
    fun sendEmailCode() {
        val emailData = EmailRequest(
            email = email.value ?: ""
        )

        Log.d("AuthVM", "테스트용 더미 log: ${emailData}로 발송")

        AuthRetrofitClient.authService.sendEmailCode(emailData).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    isEmailSent.value = true

                    Log.d("AuthVM", "코드 발송 성공: ${response.body()?.message}")
                } else {
                    isEmailSent.value = false
                    errorMessage.value = response.body()?.message ?: "인증번호 발송 실패"
                    Log.e("AuthVM", "인증번호 발송 실패: ${response.code()}: ${errorMessage.value}")
                }
            }
            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                isEmailSent.value = false
                errorMessage.value = "서버 연결 실패"

                Log.e("AuthVM", "서버 연결 실패: ${t.message}")
                Log.e("AuthVM", "fail type=${t::class.java.simpleName} msg=${t.message}", t)
            }
        })
    }
    /*

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
*/

    /**
     * 로그인 요청 함수
     * 액티비티의 요청에 따라 뷰모델에 저장된 아이디와 비밀번호로 로그인을 요청한다.
     */
    fun requestLogin(tokenManager: TokenManager) {
        val loginData = LoginRequest(
            loginId = userId.value ?: "",
            password = userPassword.value ?: ""
        )

        AuthRetrofitClient.authService.login(loginData).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val body = response.body()
                    loginResponse.value = body

                    val receivedToken = loginResponse.value?.accessToken

                    if (receivedToken != null) {
                        tokenManager.saveToken(receivedToken)
                        Log.d("AuthVM", "토큰 저장 완료!")
                        isLoginSuccess.value = true
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorData = Gson().fromJson(errorBody, LoginResponse::class.java)

                    val msg = when (response.code()) {
                        // 스낵바 메시지
                        400 -> "형식 오류/누락"
                        401 -> "아이디 또는 비밀번호 불일치"
                        429 -> "시도 너무 많음"
                        500 -> "서버 오류(Internal Server Error)"
                        else ->  errorData.error?.message ?: "로그인 실패"
                    }
                    errorMessage.value = msg

                    Log.e("AuthVM", "로그인 거부: ${response.code()}: ${errorMessage.value}")
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                errorMessage.value = "서버 연결 실패"
                Log.e("AuthVM", "서버 연결 실패")
            }
        })
    }
}
