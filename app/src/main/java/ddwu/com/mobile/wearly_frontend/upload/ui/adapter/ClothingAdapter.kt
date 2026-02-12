package ddwu.com.mobile.wearly_frontend.upload.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ddwu.com.mobile.wearly_frontend.R
import ddwu.com.mobile.wearly_frontend.databinding.ItemListUploadBinding
import ddwu.com.mobile.wearly_frontend.upload.data.slot.SlotItem
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
        val b = holder.itemBinding

        holder.itemView.setOnClickListener(null)
        holder.itemView.isEnabled = true
        b.root.isEnabled = true

        b.clothingIv.isClickable = false
        b.clothingIv.isFocusable = false

        when (val item = list[position]) {
            is SlotItem.Image -> {
                b.clothingIv.visibility = View.VISIBLE

                val url = item.imageUrl
                when {
                    !url.isNullOrBlank() && (url.startsWith("http://") || url.startsWith("https://")) -> {
                        Glide.with(holder.itemView).load(url).centerCrop().into(b.clothingIv)
                    }
                    item.uri != null -> {
                        Glide.with(holder.itemView).load(item.uri).centerCrop().into(b.clothingIv)
                    }
                    item.resId != null -> b.clothingIv.setImageResource(item.resId)
                    else -> b.clothingIv.setImageResource(R.drawable.cloth_01)
                }

                holder.itemView.setOnClickListener {
                    val pos = holder.bindingAdapterPosition
                    if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
                    android.util.Log.d("ADAPTER", "CLICK image pos=$pos id=${item.id}")
                    onImageClick(item)
                }
            }

            is SlotItem.Empty -> {
                b.clothingIv.visibility = View.GONE

                holder.itemView.setOnClickListener {
                    val pos = holder.bindingAdapterPosition
                    if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
                    android.util.Log.d("ADAPTER", "CLICK add pos=$pos")
                    onAddClick()
                }
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