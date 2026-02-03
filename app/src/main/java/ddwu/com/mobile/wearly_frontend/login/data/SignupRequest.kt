package ddwu.com.mobile.wearly_frontend.login.data

data class SignupRequest(
    val userid: String,
    val userPassword: String,
    val userName: String,
    val gender: String,    // "남성" | "여성" | "선택안함"
    val birthDate: String, // "YYMMDD"
    val email: String,
    val phoneNumber: String
)