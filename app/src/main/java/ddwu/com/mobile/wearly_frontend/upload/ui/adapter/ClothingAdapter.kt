package ddwu.com.mobile.wearly_frontend.upload.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ddwu.com.mobile.wearly_frontend.databinding.ItemListUploadBinding
import ddwu.com.mobile.wearly_frontend.upload.data.SlotItem
import ddwu.com.mobile.wearly_frontend.upload.network.CameraManager

class ClothingAdapter(private val list: ArrayList<SlotItem>,
                      private val onAddClick: () -> Unit,
                      private val onImageClick: (SlotItem.Image) -> Unit)
    : RecyclerView.Adapter<ClothingAdapter.ItemViewHolder>() {
    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ClothingAdapter.ItemViewHolder {
        val itemBinding = ItemListUploadBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        when (val item = list[position]) {
            is SlotItem.Image -> {
                holder.itemBinding.clothingIv.visibility = View.VISIBLE

                if (item.uri != null) {
                    Glide.with(holder.itemView)
                        .load(item.uri)
                        .centerCrop()
                        .into(holder.itemBinding.clothingIv)
                } else if (item.resId != null) {
                    holder.itemBinding.clothingIv.setImageResource(item.resId)
                }

                holder.itemBinding.root.setOnClickListener {
                    onImageClick(item)
                }
            }

            is SlotItem.Empty -> {
                holder.itemBinding.clothingIv.visibility = View.GONE
            }
        }
    }

    // 이벤트 핸들러 구현
    interface OnItemClickListener {
        fun onItemClick(v: View, position: Int)
    }

    //lateinit var itemClickListener: OnItemClickListener

    fun setItemClickListener(itemClickListener: OnItemClickListener) {
      //  this.itemClickListener = itemClickListener
    }

    inner class ItemViewHolder(val itemBinding: ItemListUploadBinding) : RecyclerView.ViewHolder(itemBinding.root) {

    }
}