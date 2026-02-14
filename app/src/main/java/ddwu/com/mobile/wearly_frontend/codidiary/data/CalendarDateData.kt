package ddwu.com.mobile.wearly_frontend.codidiary.data

data class CalendarDateData(
    val day: String,
    val isCurrentMonth: Boolean,
    val isToday: Boolean = false,
    val fullDate: String = ""
)
