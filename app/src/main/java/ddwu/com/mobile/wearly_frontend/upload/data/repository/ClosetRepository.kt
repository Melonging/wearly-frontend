package ddwu.com.mobile.wearly_frontend.upload.data.repository

import android.util.Log
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.ClothesDetailDto
import ddwu.com.mobile.wearly_frontend.upload.data.remote.closet.ClosetApi

class ClosetRepository(
    private val closetApi: ClosetApi
) {
    suspend fun fetchSectionClothes(sectionId: Int): List<ClothesDetailDto> {
        val res = closetApi.getSectionClothes(sectionId)

        Log.d("API_RESPONSE", res.toString())
        if (!res.success || res.data == null) {
            val msg = when (val err = res.error) {
                null -> "섹션 조회 실패"
                is String -> err
                else -> err.toString()
            }
            throw java.lang.IllegalStateException(msg)
        }

        return res.data.clothes
    }

    suspend fun fetchClothesDetail(clothingId: Long): ClothesDetailDto {
        val res = closetApi.getClothesDetail(clothingId)

        if (!res.success || res.data == null) {
            throw RuntimeException("API 실패")
        }

        val inner = res.data

        if (!inner.success || inner.data == null) {
            throw RuntimeException("상세 데이터 없음")
        }

        return inner.data
    }

}

