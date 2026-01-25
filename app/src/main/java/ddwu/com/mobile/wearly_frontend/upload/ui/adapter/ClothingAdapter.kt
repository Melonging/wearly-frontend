package ddwu.com.mobile.wearly_frontend.upload.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.databinding.ItemListUploadBinding
import ddwu.com.mobile.wearly_frontend.upload.data.SlotItem

class ClothingAdapter(val context: Context, val list: ArrayList<SlotItem>)
    : RecyclerView.Adapter<ClothingAdapter.ItemViewHolder>() {
    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ClothingAdapter.ItemViewHolder {
        val itemBinding = ItemListUploadBinding.inflate(LayoutInflater.from(context), parent, false)
        return ItemViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ClothingAdapter.ItemViewHolder, position: Int) {
        when(val item = list[position]) {
            is SlotItem.Empty -> {
                holder.itemBinding.btnAddClothing.visibility = View.VISIBLE
                holder.itemBinding.clothingIv.visibility = View.GONE
                holder.itemBinding.btnAddClothing.setOnClickListener {
                    // 카메라 앱 실행
                }
            }

            is SlotItem.Image -> {
                holder.itemBinding.btnAddClothing.visibility = View.GONE
                holder.itemBinding.clothingIv.visibility = View.VISIBLE

                Glide.with(context)
                    .load(item.path)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.itemBinding.clothingIv)
            }

        }
    }

    // 이벤트 핸들러 구현
    interface OnItemClickListener {
        fun onItemClick(v: View, position: Int)
    }

    lateinit var itemClickListener: OnItemClickListener

    fun setItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    inner class ItemViewHolder(val itemBinding: ItemListUploadBinding) : RecyclerView.ViewHolder(itemBinding.root) {

    }
}