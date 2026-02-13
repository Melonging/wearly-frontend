package ddwu.com.mobile.wearly_frontend.codiDiary.ui.adapter

import ddwu.com.mobile.wearly_frontend.R
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ddwu.com.mobile.wearly_frontend.codiDiary.data.CalendarDateData
import ddwu.com.mobile.wearly_frontend.databinding.ItemCalendarBinding

class CalendarAdapter(private val onClick: (String, Boolean) -> Unit) :
    RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private var items = listOf<CalendarDateData>()
    private var recordedDates = listOf<String>()

    fun submitList(list: List<CalendarDateData>) {
        items = list
        notifyDataSetChanged()
    }

    fun setRecordedDates(dates: List<String>) {
        this.recordedDates = dates
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = ItemCalendarBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val item = items[position]
        val context = holder.binding.root.context

        val isClickable = checkIfDateIsInRange(item)

        holder.binding.itemCalendarDateTv.text = item.day

        // 이번 달 여부에 따른 텍스트 색상 처리
        if (item.isCurrentMonth) {
            holder.binding.itemCalendarDateTv.setTextColor(Color.BLACK)
        } else {
            holder.binding.itemCalendarDateTv.setTextColor(
                ContextCompat.getColor(context, R.color.text_gray)
            )
        }

        // 오늘 날짜 표시 처리
        if (item.isToday && item.isCurrentMonth) {
            holder.binding.itemCalendarDateTv.isSelected = true
            holder.binding.itemCalendarDateTv.setTextColor(Color.WHITE)
        } else {
            holder.binding.itemCalendarDateTv.isSelected = false
            if (item.isCurrentMonth) {
                holder.binding.itemCalendarDateTv.setTextColor(Color.BLACK)
            }
        }

        // 기록이 있는 날짜에 UI 상태 표시 (Selected 상태 활용)
        if (item.isCurrentMonth && recordedDates.contains(item.fullDate)) {
            holder.binding.itemCalendarDateLayout.isSelected = true
        } else {
            holder.binding.itemCalendarDateLayout.isSelected = false
        }

        holder.binding.root.setOnClickListener {
            if (item.isCurrentMonth) {
                val hasRecord = holder.binding.itemCalendarDateLayout.isSelected
                onClick(item.day, hasRecord)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    class CalendarViewHolder(val binding: ItemCalendarBinding) :
        RecyclerView.ViewHolder(binding.root)

    /**
     * 날짜가 (오늘 - 1달) ~ (오늘 + 6일) 사이인지 확인하는 함수
     */
    private fun checkIfDateIsInRange(item: CalendarDateData): Boolean {
        if (item.fullDate.isEmpty()) return false

        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.KOREA)
        return try {
            val itemDate = sdf.parse(item.fullDate) ?: return false

            val today = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }

            val startDate = (today.clone() as java.util.Calendar).apply {
                add(java.util.Calendar.MONTH, -1)
            }.time

            val endDate = (today.clone() as java.util.Calendar).apply {
                add(java.util.Calendar.DATE, 6)
            }.time

            !itemDate.before(startDate) && !itemDate.after(endDate)
        } catch (e: Exception) {
            false
        }
    }
}