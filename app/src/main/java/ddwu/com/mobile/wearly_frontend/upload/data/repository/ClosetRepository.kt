package ddwu.com.mobile.wearly_frontend.upload.data.repository

import android.util.Log
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.Category
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.ClosetDto
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.ClosetViewDataDto
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.ClothesDetailDto
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.ClothesDetailInnerDto
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.ClothingUpdateRequestDto
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.ClothingUpdateResponseDto
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
            val msg = when (val err = res.error) {
                null -> res.message ?: "상세 조회 실패"
                is String -> err
                else -> err.toString()
            }
            throw IllegalStateException(msg)
        }

        return res.data
    }


    suspend fun fetchClosetView(closetId: Int): ClosetViewDataDto {
        val res = closetApi.getClosetView(closetId)

        if (!res.success || res.data == null) {
            throw IllegalStateException("옷장 뷰 조회 실패")
        }

        return res.data
    }



    suspend fun fetchCategories(): List<Category> {
        val res = closetApi.getCategories()
        return if (res.success) {
            res.data?.categories ?: emptyList()
        } else {
            emptyList()
        }
    }

    suspend fun fetchClosets(): List<ClosetDto> {
        val res = closetApi.getClosets()
        if (!res.success) return emptyList()
        return res.data ?: emptyList()
    }

    // 옷 정보 수정
    suspend fun updateClothing(
        clothingId: Long,
        req: ClothingUpdateRequestDto
    ): ClothingUpdateResponseDto {
        // “업데이트할 필드가 없음” 방지
        if (req.closetId == null && req.sectionId == null && req.categoryId == null) {
            throw IllegalArgumentException("업데이트할 필드가 없습니다.")
        }

        val res = closetApi.updateClothing(clothingId, req)
        if (!res.success) throw RuntimeException(res.message ?: "의류 수정 실패")
        return res
    }

    // 옷 삭제
    suspend fun deleteClothing(clothingId: Long) {
        val res = closetApi.deleteClothing(clothingId)
        if (!res.success) throw RuntimeException(res.message ?: "삭제 실패")
    }

}

