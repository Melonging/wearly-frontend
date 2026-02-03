package ddwu.com.mobile.wearly_frontend.login.data

data class LoginResponse(
    val success: Boolean,
    val tokenType: String,
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val user: UserInfo,
    val error: String?
)

data class UserInfo(
    val userId: String,
    val userName: String
)
