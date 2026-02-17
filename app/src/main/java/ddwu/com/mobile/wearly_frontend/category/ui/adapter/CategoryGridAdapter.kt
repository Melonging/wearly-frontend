package ddwu.com.mobile.wearly_frontend.category.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ddwu.com.mobile.wearly_frontend.databinding.ItemCategoryGridBinding

data class CategoryUi(
    val id: Long?,
    val name: String
)

class CategoryGridAdapter(
    private var categoryList: List<CategoryUi>,
    private var selectedCategoryId: Long?,
    private val onItemClick: (CategoryUi) -> Unit
) : RecyclerView.Adapter<CategoryGridAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryGridBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.bind(category, category.id == selectedCategoryId)
    }

    override fun getItemCount(): Int = categoryList.size

    fun submitList(newList: List<CategoryUi>) {
        categoryList = newList
        notifyDataSetChanged()
    }

    fun updateSelectedCategory(id: Long?) {
        val prev = selectedCategoryId
        selectedCategoryId = id

        val prevPos = categoryList.indexOfFirst { it.id == prev }
        val newPos = categoryList.indexOfFirst { it.id == id }

        if (prevPos >= 0) notifyItemChanged(prevPos)
        if (newPos >= 0) notifyItemChanged(newPos)
    }

    inner class CategoryViewHolder(
        private val binding: ItemCategoryGridBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: CategoryUi, isSelected: Boolean) {
            binding.categoryNameTv.text = category.name
            binding.categoryNameTv.setTypeface(
                null,
                if (isSelected) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL
            )

            binding.root.setOnClickListener {
                updateSelectedCategory(category.id)
                onItemClick(category)
            }
        }
    }
}
