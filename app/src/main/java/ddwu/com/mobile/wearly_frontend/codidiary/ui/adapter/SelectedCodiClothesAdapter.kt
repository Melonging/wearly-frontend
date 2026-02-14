package ddwu.com.mobile.wearly_frontend.codidiary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ddwu.com.mobile.wearly_frontend.codidiary.data.DiaryClothItem
import ddwu.com.mobile.wearly_frontend.databinding.ItemDiaryClothesCategorySelectedBinding

class SelectedCodiClothesAdapter(private val onDeleteClick: (DiaryClothItem) -> Unit) :
    ListAdapter<DiaryClothItem, SelectedCodiClothesAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: ItemDiaryClothesCategorySelectedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DiaryClothItem) {
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