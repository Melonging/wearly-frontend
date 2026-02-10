package ddwu.com.mobile.wearly_frontend.category.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ddwu.com.mobile.wearly_frontend.databinding.ItemCategoryGridBinding

class CategoryGridAdapter(
    private val categoryList: List<String>,
    private var selectedCategory: String,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<CategoryGridAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.bind(category, category == selectedCategory)
    }

    override fun getItemCount(): Int = categoryList.size

    fun updateSelectedCategory(category: String) {
        val previousSelected = selectedCategory
        selectedCategory = category
        val previousPosition = categoryList.indexOf(previousSelected)
        val newPosition = categoryList.indexOf(category)

        if (previousPosition >= 0) notifyItemChanged(previousPosition)
        if (newPosition >= 0) notifyItemChanged(newPosition)
    }

    inner class CategoryViewHolder(
        private val binding: ItemCategoryGridBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: String, isSelected: Boolean) {
            binding.categoryNameTv.text = category

            if (isSelected) {
                binding.categoryNameTv.setTypeface(null, android.graphics.Typeface.BOLD)
            } else {
                binding.categoryNameTv.setTypeface(null, android.graphics.Typeface.NORMAL)
            }

            binding.root.setOnClickListener {
                updateSelectedCategory(category)
                onItemClick(category)
            }
        }
    }
}

