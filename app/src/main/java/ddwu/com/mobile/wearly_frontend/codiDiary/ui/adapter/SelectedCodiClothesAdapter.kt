package ddwu.com.mobile.wearly_frontend.codiDiary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ddwu.com.mobile.wearly_frontend.codiDiary.data.ClothItem
import ddwu.com.mobile.wearly_frontend.databinding.ItemDiaryClothesCategorySelectedBinding

class SelectedCodiClothesAdapter(private val onDeleteClick: (ClothItem) -> Unit) :
    ListAdapter<ClothItem, SelectedCodiClothesAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: ItemDiaryClothesCategorySelectedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ClothItem) {
            Glide.with(binding.root.context)
                .load(item.image)
                .into(binding.diarySelectedClothesIv)

            binding.diarySelectedClothesCloseBtn.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDiaryClothesCategorySelectedBinding.inflate(
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