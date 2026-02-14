package ddwu.com.mobile.wearly_frontend.codidiary.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ddwu.com.mobile.wearly_frontend.codidiary.data.DiaryClothItem
import ddwu.com.mobile.wearly_frontend.databinding.ItemDiaryCategoryClothesBinding

class CodiClothesAdapter(
    private val onClothClick: (DiaryClothItem) -> Unit
) : ListAdapter<DiaryClothItem, CodiClothesAdapter.ViewHolder>(DiffCallback) {

    private var selectedIds = setOf<Int>()

    fun updateSelectedIds(newIds: Set<Int>) {
        selectedIds = newIds
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemDiaryCategoryClothesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DiaryClothItem) {
            Glide.with(binding.root.context).load(item.image).into(binding.diaryCategoryUnselectedClothesIv)

            val isSelected = selectedIds.contains(item.clothing_id)
            updateUI(isSelected)

            binding.root.setOnClickListener {
                onClothClick(item)
            }
        }

        private fun updateUI(isSelected: Boolean) {
            val visibility = if (isSelected) View.VISIBLE else View.GONE
            binding.diaryCategorySelectedClothesFilter.visibility = visibility
            binding.diaryCategorySelectedClothesCloseBtn.visibility = visibility
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDiaryCategoryClothesBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<DiaryClothItem>() {
            override fun areItemsTheSame(oldItem: DiaryClothItem, newItem: DiaryClothItem): Boolean {
                return oldItem.clothing_id == newItem.clothing_id
            }

            override fun areContentsTheSame(oldItem: DiaryClothItem, newItem: DiaryClothItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}