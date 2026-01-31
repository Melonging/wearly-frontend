package ddwu.com.mobile.wearly_frontend.upload.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import ddwu.com.mobile.wearly_frontend.databinding.ItemListUploadBinding
import ddwu.com.mobile.wearly_frontend.upload.data.SlotItem
import ddwu.com.mobile.wearly_frontend.upload.network.CameraManager
import ddwu.com.mobile.wearly_frontend.upload.ui.LoadingActivity
import ddwu.com.mobile.wearly_frontend.upload.ui.fragment.UploadFragment

class ClothingAdapter(private val list: ArrayList<SlotItem>,
                      private val onAddClick: () -> Unit)
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
        when(val item = list[position]) {
            is SlotItem.Empty -> {
                holder.itemBinding.btnAddClothing.visibility = View.VISIBLE
                holder.itemBinding.clothingIv.visibility = View.GONE
                holder.itemBinding.btnAddClothing.setOnClickListener {
                    onAddClick()
                }
            }
            is SlotItem.Image -> {
                holder.itemBinding.btnAddClothing.visibility = View.GONE
                holder.itemBinding.clothingIv.visibility = View.VISIBLE
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