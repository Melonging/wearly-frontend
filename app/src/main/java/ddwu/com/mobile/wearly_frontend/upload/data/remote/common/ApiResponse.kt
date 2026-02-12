package ddwu.com.mobile.wearly_frontend.upload.data.remote.common

data class ApiError(
    val code: String?,
    val message: String?,
    val field: String? = null
)

data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val error: ApiError? = null
)
