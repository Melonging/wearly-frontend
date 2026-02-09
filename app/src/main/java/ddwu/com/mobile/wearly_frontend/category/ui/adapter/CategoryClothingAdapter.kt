package ddwu.com.mobile.wearly_frontend.category.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ddwu.com.mobile.wearly_frontend.databinding.ItemClothingRecordBinding
import com.bumptech.glide.Glide

class CategoryClothingAdapter(private val allClothes: List<ClothingDetail>) :
    RecyclerView.Adapter<CategoryClothingAdapter.ClothingViewHolder>() {

    private var displayList: List<ClothingDetail> = allClothes

    inner class ClothingViewHolder(private val binding: ItemClothingRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clothing: ClothingDetail) {
            if (clothing.uri != null) {
                Glide.with(binding.root.context)
                    .load(clothing.uri)
                    .into(binding.clothingIv)
            } else if (clothing.resId != null) {
                binding.clothingIv.setImageResource(clothing.resId)
            }

            binding.root.setOnClickListener {
                // 클릭 이벤트처리
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothingViewHolder {
        val binding = ItemClothingRecordBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ClothingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClothingViewHolder, position: Int) {
        holder.bind(displayList[position])
    }

    override fun getItemCount(): Int = displayList.size

    /**
     * 카테고리에 따른 필터링 로직
     */
    fun filterByCategory(category: String) {
        displayList = if (category == "전체") {
            allClothes
        } else {
            // 정의하신 데이터 클래스의 category 필드와 비교
            allClothes.filter { it.category == category }
        }
        notifyDataSetChanged()
    }
}
