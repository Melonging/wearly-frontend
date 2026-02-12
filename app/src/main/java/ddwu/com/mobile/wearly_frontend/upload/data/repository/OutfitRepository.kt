package ddwu.com.mobile.wearly_frontend.upload.data.repository

import ddwu.com.mobile.wearly_frontend.category.data.CategoryDto
import ddwu.com.mobile.wearly_frontend.upload.data.model.closet.Category
import ddwu.com.mobile.wearly_frontend.upload.data.remote.closet.ClosetApi

class OutfitRepository(
    private val api: ClosetApi
) {

    suspend fun fetchCategories(): List<Category> {
        val res = api.getCategories()
        if (!res.success) return emptyList()
        return (res.data?.categories ?: emptyList()) as List<Category>
    }
}
