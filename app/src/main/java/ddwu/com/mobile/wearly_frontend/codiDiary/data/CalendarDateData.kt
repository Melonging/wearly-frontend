package ddwu.com.mobile.wearly_frontend.codiDiary.data

data class CalendarDateData(
    val day: String,
    val isCurrentMonth: Boolean,
    val isToday: Boolean = false
)
