package ddwu.com.mobile.wearly_frontend.records.data.repository

import ddwu.com.mobile.wearly_frontend.records.data.dto.UpdateWearRecordRequest
import ddwu.com.mobile.wearly_frontend.records.data.mapper.toUi
import ddwu.com.mobile.wearly_frontend.records.data.model.WearRecordItemUi
import ddwu.com.mobile.wearly_frontend.records.data.remote.RecordsApi

class WearRecordRepository(
    private val api: RecordsApi
) {
    suspend fun fetchWearRecords(
        year: Int? = null,
        month: Int? = null,
        date: String? = null,
        isHeart: Boolean? = null
    ): List<WearRecordItemUi> {
        val res = api.getWearRecords(
            year = year,
            month = month,
            date = date,
            isHeart = isHeart?.toString()
        )
        if (!res.success) return emptyList()
        return res.data.orEmpty().map { it.toUi() }
    }

    suspend fun updateHeart(dateId: Long, newValue: Boolean): Boolean {
        val res = api.updateWearRecord(
            dateId = dateId,
            body = UpdateWearRecordRequest(is_heart = newValue)
        )
        return res.success
    }
}
