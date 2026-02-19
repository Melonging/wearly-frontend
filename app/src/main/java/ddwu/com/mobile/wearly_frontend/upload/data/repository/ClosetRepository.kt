package ddwu.com.mobile.wearly_frontend.upload.data.repository

import ddwu.com.mobile.wearly_frontend.closet.data.CategoryItem
import ddwu.com.mobile.wearly_frontend.closet.data.ClosetItem
import ddwu.com.mobile.wearly_frontend.closet.data.ClosetViewData
import ddwu.com.mobile.wearly_frontend.closet.data.ClothingDetail
import ddwu.com.mobile.wearly_frontend.closet.data.CreateClosetRequest
import ddwu.com.mobile.wearly_frontend.closet.data.DeleteClosetData
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.ClothingUpdateRequestDto
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.ClothingUpdateResponseDto
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.SectionClothesData
import ddwu.com.mobile.wearly_frontend.upload.data.remote.closet.ClosetApi

class ClosetRepository(
    private val closetApi: ClosetApi
) {

    //홈 화면 옷장 목록 조회
    suspend fun fetchHomeClosetList(): List<ClosetItem> {
        val res = closetApi.getHomeClosetList()
        if (!res.success || res.data == null) {
            throw IllegalStateException(res.message ?: "옷장 목록 조회 실패")
        }
        return res.data
    }

    //옷장 뷰 상세 조회
    suspend fun fetchClosetView(closetId: Int): ClosetViewData {
        val res = closetApi.getClosetView(closetId)
        if (!res.success || res.data == null) {
            throw IllegalStateException(res.message ?: "옷장 상세 조회 실패")
        }
        return res.data
    }

    //섹션 내 옷 목록 조회
    suspend fun fetchSectionClothes(sectionId: Int): SectionClothesData {
        val res = closetApi.getSectionClothes(sectionId)
        if (!res.success || res.data == null) {
            throw IllegalStateException(res.message ?: "섹션 옷 조회 실패")
        }
        return res.data
    }

    //옷 상세 정보 조회
    suspend fun fetchClothingDetail(clothingId: Int): ClothingDetail {
        val res = closetApi.getClothingDetail(clothingId)
        if (!res.success || res.data == null) {
            throw IllegalStateException(res.message ?: "옷 상세 조회 실패")
        }
        return res.data
    }

    //옷장 추가
    suspend fun createCloset(templateId: Int, name: String) : ClosetItem {
        //Body 객체 생성
        val request = CreateClosetRequest(
            closetTemplateId = templateId,
            closetName = name
        )

        val res = closetApi.setNewCloset(request)

        if (!res.success || res.data == null) {
            throw IllegalStateException(res.message ?: "옷장 생성 실패")
        }
        return res.data
    }

    //옷장 삭제
    suspend fun deleteCloset(closetId: Int): DeleteClosetData {
        val res = closetApi.deleteCloset(closetId)
        if (!res.success || res.data == null) {
            throw IllegalStateException(res.message ?: "옷장 삭제 실패")
        }
        return res.data
    }

    //카테고리
    suspend fun fetchCategories(): List<CategoryItem> {
        val res = closetApi.getCategories()
        if (!res.success || res.data == null) {
            throw IllegalStateException(res.message ?: "카테고리 조회 실패")
        }
        return res.data.categories
    }

    // 옷 정보 수정
    suspend fun updateClothing(
        clothingId: Int,
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
    suspend fun deleteClothing(clothingId: Int) {
        val res = closetApi.deleteClothing(clothingId)
        if (!res.success) throw RuntimeException(res.message ?: "삭제 실패")
    }

//    suspend fun fetchSectionClothes(sectionId: Int): List<ClothesDetailDto> {
//        val res = closetApi.getSectionClothes(sectionId)
//
//        Log.d("API_RESPONSE", res.toString())
//        if (!res.success || res.data == null) {
//            val msg = when (val err = res.error) {
//                null -> "섹션 조회 실패"
//                is String -> err
//                else -> err.toString()
//            }
//            throw java.lang.IllegalStateException(msg)
//        }
//
//        return res.data.clothes
//    }
//
//    suspend fun fetchClothesDetail(clothingId: Long): ClothesDetailDto {
//        val res = closetApi.getClothesDetail(clothingId)
//
//        if (!res.success || res.data == null) {
//            val msg = when (val err = res.error) {
//                null -> res.message ?: "상세 조회 실패"
//                is String -> err
//                else -> err.toString()
//            }
//            throw IllegalStateException(msg)
//        }
//
//        return res.data
//    }
//
//
//    suspend fun fetchClosetView(closetId: Int): ClosetViewDataDto {
//        val res = closetApi.getClosetView(closetId)
//
//        if (!res.success || res.data == null) {
//            throw IllegalStateException("옷장 뷰 조회 실패")
//        }
//
//        return res.data
//    }
//
//
//
//    suspend fun fetchCategories(): List<Category> {
//        val res = closetApi.getCategories()
//        return if (res.success) {
//            res.data?.categories ?: emptyList()
//        } else {
//            emptyList()
//        }
//    }
//
//    suspend fun fetchClosets(): List<ClosetDto> {
//        val res = closetApi.getClosets()
//        if (!res.success) return emptyList()
//        return res.data ?: emptyList()
//    }
//

}
