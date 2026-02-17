package ddwu.com.mobile.wearly_frontend.codidiary.data

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: T? = null,
    @SerializedName("error") val error: Any? = null,     // string일 수도, object일 수도
    @SerializedName("message") val message: String? = null
)
