package ddwu.com.mobile.wearly_frontend.closet.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.closet.data.ClosetItem

class ClosetChipListAdapter (private val onItemClick: (ClosetItem) -> Unit) :
    ListAdapter<ClosetItem, ClosetChipListAdapter.ClosetViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClosetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_closet_chip_list, parent, false)
        return ClosetViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClosetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ClosetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvName: TextView = view.findViewById(R.id.tv_closet_name)

        fun bind(item: ClosetItem) {
            tvName.text = item.closetName

            if (item.isSelected) {
                tvName.setBackgroundResource(R.drawable.bg_closet_chip_scroll_on)
                tvName.setTextColor(Color.WHITE)
            } else {
                tvName.setBackgroundResource(R.drawable.bg_closet_chip_scroll_off)
                tvName.setTextColor(Color.BLACK)
            }

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<ClosetItem>() {
        override fun areItemsTheSame(oldItem: ClosetItem, newItem: ClosetItem) =
            oldItem.closetId == newItem.closetId

        override fun areContentsTheSame(oldItem: ClosetItem, newItem: ClosetItem) =
            oldItem == newItem
    }
}