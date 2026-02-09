package ddwu.com.mobile.wearly_frontend.category.ui.adapter

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import ddwu.com.mobile.wearly_frontend.codidiary.data.ClothingItem
import ddwu.com.mobile.wearly_frontend.databinding.ItemClothingRecordBinding

class CategoryClothingAdapter(
    private var items: MutableList<ClothingItem>,
    private val onSelectionChanged: (Int, List<ClothingItem>) -> Unit
) : RecyclerView.Adapter<CategoryClothingAdapter.ClothingViewHolder>() {

    // 1. 변수 위치를 클래스 시작 지점으로 정확히 고정
    private val selectedItems = mutableSetOf<ClothingItem>()

    inner class ClothingViewHolder(val binding: ItemClothingRecordBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothingViewHolder {
        val binding = ItemClothingRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClothingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClothingViewHolder, position: Int) {
        val item = items[position]

        // 이미지 로드 로그 추가 버전
        Glide.with(holder.itemView.context)
            .load(item.image)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.e("GLIDE_ERROR", "로드 실패! 경로: $model / 에러: ${e?.message}")
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable, // ?를 제거하거나 추가하는 등 라이브러리 사양에 맞춤
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d("GLIDE_SUCCESS", "로드 성공: $model")
                    return false
                }
            })
            .into(holder.binding.clothingIv)

        // UI 상태 업데이트
        val isSelected = selectedItems.contains(item)
        holder.binding.selectedOverlay.visibility = if (isSelected) View.VISIBLE else View.GONE
        holder.binding.checkIv.visibility = if (isSelected) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            if (selectedItems.contains(item)) {
                selectedItems.remove(item)
            } else {
                selectedItems.add(item)
            }
            notifyItemChanged(position)
            onSelectionChanged(selectedItems.size, selectedItems.toList())
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<ClothingItem>) {
        this.items.clear()
        this.items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun getSelectedItems(): List<ClothingItem> = selectedItems.toList()
}