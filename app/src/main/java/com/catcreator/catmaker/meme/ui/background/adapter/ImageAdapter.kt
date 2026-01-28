package com.catcreator.catmaker.meme.ui.background.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.catcreator.catmaker.meme.base.AbsBaseAdapter
import com.catcreator.catmaker.meme.base.AbsBaseDiffCallBack
import com.catcreator.catmaker.meme.data.model.SelectedModel
import com.catcreator.catmaker.meme.utils.hide
import com.catcreator.catmaker.meme.utils.onSingleClick
import com.catcreator.catmaker.meme.utils.show
import com.catcreator.catmaker.meme.R
import com.catcreator.catmaker.meme.databinding.ItemImageBinding

class ImageAdapter :
    AbsBaseAdapter<SelectedModel, ItemImageBinding>(R.layout.item_image, DiffCallBack()) {
    var onClick: ((Int) -> Unit)? = null
    var posSelect = -1
    override fun bind(
        binding: ItemImageBinding,
        position: Int,
        data: SelectedModel,
        holder: RecyclerView.ViewHolder
    ) {
        binding.tvAddImage.isSelected = true
        binding.imvImage.onSingleClick {
            onClick?.invoke(position)
        }

        Glide.with(binding.root).load(data.path).encodeQuality(70)
            .dontAnimate()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(binding.imvImage)
        if (position == 0) {
            binding.lnlAddItem.show()
        } else {
            binding.lnlAddItem.hide()
        }
        if (data.isSelected) {
            binding.vFocus.show()

        } else {
            binding.vFocus.hide()

        }
    }

    class DiffCallBack : AbsBaseDiffCallBack<SelectedModel>() {
        override fun itemsTheSame(
            oldItem: SelectedModel,
            newItem: SelectedModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun contentsTheSame(
            oldItem: SelectedModel,
            newItem: SelectedModel
        ): Boolean {
            return oldItem.path != newItem.path || oldItem.isSelected != newItem.isSelected
        }

    }
}