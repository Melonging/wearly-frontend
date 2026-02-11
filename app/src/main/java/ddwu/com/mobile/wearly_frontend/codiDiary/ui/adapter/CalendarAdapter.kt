package ddwu.com.mobile.wearly_frontend.codiDiary.ui.adapter

import ddwu.com.mobile.wearly_frontend.R
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ddwu.com.mobile.wearly_frontend.codiDiary.data.CalendarDateData
import ddwu.com.mobile.wearly_frontend.databinding.ItemCalendarBinding

class CalendarAdapter(private val onClick: (String) -> Unit) :
    RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private var items = listOf<CalendarDateData>()

    fun submitList(list: List<CalendarDateData>) {
        items = list
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

        holder.binding.itemCalendarDateTv.text = item.day

        if (item.isCurrentMonth) {
            holder.binding.itemCalendarDateTv.setTextColor(Color.BLACK)
        } else {
            holder.binding.itemCalendarDateTv.setTextColor(
                ContextCompat.getColor(context, R.color.text_gray)
            )
        }

        if (item.isToday && item.isCurrentMonth) {
            holder.binding.itemCalendarDateTv.isSelected = true
            holder.binding.itemCalendarDateTv.setTextColor(Color.WHITE)
        } else {
            holder.binding.itemCalendarDateTv.isSelected = false

            if (item.isCurrentMonth) {
                holder.binding.itemCalendarDateTv.setTextColor(Color.BLACK)
            }
        }

        holder.binding.root.setOnClickListener {
            if (item.isCurrentMonth) {
                onClick(item.day)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    class CalendarViewHolder(val binding: ItemCalendarBinding) :
        RecyclerView.ViewHolder(binding.root)
}