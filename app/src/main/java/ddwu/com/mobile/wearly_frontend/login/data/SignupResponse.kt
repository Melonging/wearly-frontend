package ddwu.com.mobile.wearly_frontend.login.data

data class SignupResponse(
    val success: Boolean,
    val userid: String,
    val userName: String,
    val error: String?
)
