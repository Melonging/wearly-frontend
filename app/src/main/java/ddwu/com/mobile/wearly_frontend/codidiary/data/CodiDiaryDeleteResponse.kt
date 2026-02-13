package ddwu.com.mobile.wearly_frontend.codidiary.data

data class CodiDiaryDeleteResponse(
    val success: Boolean,
    val data: CodiDiaryDeleteResult?,
    val error: String?
)

data class CodiDiaryDeleteResult(
    val message: String
)
