package ddwu.com.mobile.wearly_frontend.closet.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.databinding.ItemClothesInClosetBinding
import ddwu.com.mobile.wearly_frontend.upload.data.SlotItem
class ClothesInClosetAdapter(val context: Context, var list: MutableList<SlotItem>)
    : RecyclerView.Adapter<ClothesInClosetAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: ItemClothesInClosetBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemClothesInClosetBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClothesInClosetAdapter.ItemViewHolder, position: Int) {
        val item = list[position]

        /*
        // item이 Image 타입일 때만
        if (item is SlotItem.Image) {
            val model = item.uri ?: item.resId

            Glide.with(context)
                .load(model)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.binding.clothesIv)
        } else if (item is SlotItem.Empty) {
            holder.binding.clothesIv.setImageResource(R.drawable.ic_launcher_background)
        }


         */
    }

    // 데이터 갱신 기능 수정
    fun updateData(newList: List<SlotItem>) {
        this.list.clear()
        this.list.addAll(newList)
        notifyDataSetChanged()
    }
}

