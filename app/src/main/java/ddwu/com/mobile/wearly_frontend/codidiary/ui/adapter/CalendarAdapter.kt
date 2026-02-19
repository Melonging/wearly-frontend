package ddwu.com.mobile.wearly_frontend.codidiary.ui.adapter

import ddwu.com.mobile.wearly_frontend.R
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ddwu.com.mobile.wearly_frontend.codidiary.data.CalendarDateData
import ddwu.com.mobile.wearly_frontend.codidiary.data.CodiDiaryRead
import ddwu.com.mobile.wearly_frontend.databinding.ItemCalendarBinding

class CalendarAdapter(private val onClick: (String, Boolean) -> Unit) :
    RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private var items = listOf<CalendarDateData>()
    private var recordedDates = listOf<String>()
    private var recordedDayRecordMap = mapOf<String, CodiDiaryRead>()

    fun setRecordedDayRecordMap(map: Map<String, CodiDiaryRead>) {
        recordedDayRecordMap = map
        notifyDataSetChanged()
    }


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


        val record = recordedDayRecordMap[item.fullDate]
        val container = holder.binding.itemCalendarStackContainer
        container.removeAllViews()

        holder.binding.itemCalendarMoreBadge.visibility = View.GONE
        holder.binding.itemCalendarMoreBadge.text = ""

        if (item.isCurrentMonth && record != null) {

            if (!record.image_url.isNullOrBlank()) {
                val iv = ImageView(holder.binding.root.context).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
                Glide.with(holder.binding.root).load(record.image_url).centerCrop().into(iv)
                container.addView(iv)
            } else {
                val clothes = record.outfit?.clothes.orEmpty()
                    .sortedBy { it.layout?.z_index ?: 999 }

                val showList = clothes.take(3)
                val more = (clothes.size - showList.size).coerceAtLeast(0)

                container.post {
                    val w = container.width
                    val h = container.height
                    if (w == 0 || h == 0) return@post

                    val size = (minOf(w, h) * 0.72f).toInt().coerceAtLeast(18)

                    val offsets = listOf(
                        Pair(0.08f, 0.05f),
                        Pair(0.22f, 0.16f),
                        Pair(0.36f, 0.28f)
                    )

                    showList.forEachIndexed { idx, cloth ->
                        val (xr, yr) = offsets.getOrElse(idx) { Pair(0.2f, 0.2f) }

                        val iv = ImageView(holder.binding.root.context).apply {
                            layoutParams = FrameLayout.LayoutParams(size, size)
                            scaleType = ImageView.ScaleType.CENTER_CROP
                            x = w * xr
                            y = h * yr
                            z = (idx + 1).toFloat()
                        }

                        Glide.with(holder.binding.root)
                            .load(cloth.image)
                            .centerCrop()
                            .into(iv)

                        container.addView(iv)
                    }

                    if (more > 0) {
                        holder.binding.itemCalendarMoreBadge.visibility = View.VISIBLE
                        holder.binding.itemCalendarMoreBadge.text = "+$more"
                    }
                }
            }
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