package ddwu.com.mobile.wearly_frontend.records.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.databinding.ItemRecordBinding
import ddwu.com.mobile.wearly_frontend.records.data.model.WearRecordItemUi

class RecordsAdapter(
    private val list: ArrayList<WearRecordItemUi>,
    private val onItemClick: ((WearRecordItemUi) -> Unit)? = null,
    private val onHeartClick: ((WearRecordItemUi) -> Unit)? = null
) : RecyclerView.Adapter<RecordsAdapter.ItemViewHolder>() {

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun submit(newList: List<WearRecordItemUi>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    fun updateHeart(id: Long, newValue: Boolean) {
        val idx = list.indexOfFirst { it.id == id }
        if (idx == -1) return
        val old = list[idx]
        list[idx] = old.copy(isHeart = newValue)
        notifyItemChanged(idx)
    }

    fun getItemById(id: Long): WearRecordItemUi? =
        list.firstOrNull { it.id == id }

    inner class ItemViewHolder(private val binding: ItemRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WearRecordItemUi) = with(binding) {
            tvTitle.text = item.title
            tvDate.text = formatDate(item.dateText)
            tvTemp.text = item.tempText ?: ""

            if (item.thumbUrl.isNullOrBlank()) {
                ivThumb.setImageDrawable(null)
            } else {
                Glide.with(root)
                    .load(item.thumbUrl)
                    .centerCrop()
                    .into(ivThumb)
            }

            if (!item.iconCode.isNullOrBlank()) {
                val iconUrl = "https://openweathermap.org/img/wn/${item.iconCode}@2x.png"
                Glide.with(root).load(iconUrl).into(ivWeather)
            }

            btnLike.setImageResource(
                if (item.isHeart) R.drawable.ic_records_heart_selected
                else R.drawable.ic_records_heart
            )

            root.setOnClickListener { onItemClick?.invoke(item) }
            btnLike.setOnClickListener { onHeartClick?.invoke(item) }
        }
    }
}


private fun formatDate(raw: String): String {
    return try {
        val local = java.time.LocalDate.parse(raw)
        val day = when (local.dayOfWeek) {
            java.time.DayOfWeek.MONDAY -> "월"
            java.time.DayOfWeek.TUESDAY -> "화"
            java.time.DayOfWeek.WEDNESDAY -> "수"
            java.time.DayOfWeek.THURSDAY -> "목"
            java.time.DayOfWeek.FRIDAY -> "금"
            java.time.DayOfWeek.SATURDAY -> "토"
            java.time.DayOfWeek.SUNDAY -> "일"
        }
        "${local.year}.${"%02d".format(local.monthValue)}.${"%02d".format(local.dayOfMonth)} ($day)"
    } catch (e: Exception) {
        raw
    }
}
