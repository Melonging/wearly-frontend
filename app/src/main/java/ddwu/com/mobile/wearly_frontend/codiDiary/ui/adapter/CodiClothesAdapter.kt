package ddwu.com.mobile.wearly_frontend.codiDiary.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ddwu.com.mobile.wearly_frontend.codiDiary.data.ClothItem
import ddwu.com.mobile.wearly_frontend.databinding.ItemDiaryCategoryClothesBinding

class CodiClothesAdapter(
    private val onClothClick: (ClothItem) -> Unit
) : ListAdapter<ClothItem, CodiClothesAdapter.ViewHolder>(DiffCallback) {

    private var selectedIds = setOf<Int>()

    fun updateSelectedIds(newIds: Set<Int>) {
        selectedIds = newIds
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemDiaryCategoryClothesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ClothItem) {
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
        val DiffCallback = object : DiffUtil.ItemCallback<ClothItem>() {
            override fun areItemsTheSame(oldItem: ClothItem, newItem: ClothItem): Boolean {
                return oldItem.clothing_id == newItem.clothing_id
            }

            override fun areContentsTheSame(oldItem: ClothItem, newItem: ClothItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}