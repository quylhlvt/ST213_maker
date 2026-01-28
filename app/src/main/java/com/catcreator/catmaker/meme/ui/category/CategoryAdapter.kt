package com.catcreator.catmaker.meme.ui.category

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.catcreator.catmaker.meme.base.AbsBaseAdapter
import com.catcreator.catmaker.meme.base.AbsBaseDiffCallBack
import com.catcreator.catmaker.meme.data.model.CustomModel
import com.catcreator.catmaker.meme.utils.onSingleClick
import com.catcreator.catmaker.meme.utils.shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.catcreator.catmaker.meme.R
import com.catcreator.catmaker.meme.databinding.ItemCategoryBinding

class CategoryAdapter : AbsBaseAdapter<CustomModel, ItemCategoryBinding>(
    R.layout.item_category, DiffCallBack()
) {
    var onCLick: ((Int) -> Unit)? = null
    override fun bind(
        binding: ItemCategoryBinding,
        position: Int,
        data: CustomModel,
        holder: RecyclerView.ViewHolder
    ) {
        val shimmerDrawable = ShimmerDrawable().apply {
            setShimmer(shimmer)
        }
        Glide.with(binding.root).load(data.avt).encodeQuality(70).diskCacheStrategy(
            DiskCacheStrategy.AUTOMATIC).placeholder(shimmerDrawable).into(binding.imv)
        binding.imv.onSingleClick {
            onCLick?.invoke(position)
        }
    }

    class DiffCallBack : AbsBaseDiffCallBack<CustomModel>() {
        override fun itemsTheSame(
            oldItem: CustomModel, newItem: CustomModel
        ): Boolean {
            return oldItem.avt == newItem.avt
        }

        override fun contentsTheSame(
            oldItem: CustomModel, newItem: CustomModel
        ): Boolean {
            return oldItem.avt != newItem.avt
        }

    }
}