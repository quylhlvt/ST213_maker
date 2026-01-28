package com.catcreator.catmaker.meme.ui.background.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.catcreator.catmaker.meme.base.AbsBaseAdapter
import com.catcreator.catmaker.meme.base.AbsBaseDiffCallBack
import com.catcreator.catmaker.meme.data.model.SelectedModel
import com.catcreator.catmaker.meme.utils.onSingleClick
import com.catcreator.catmaker.meme.R
import com.catcreator.catmaker.meme.databinding.ItemStikerBgBinding

class StikerAdapter :
    AbsBaseAdapter<SelectedModel, ItemStikerBgBinding>(
        R.layout.item_stiker_bg,
        DiffCallBack()
    ) {
    var onClick: ((String) -> Unit)? = null
    override fun bind(
        binding: ItemStikerBgBinding,
        position: Int,
        data: SelectedModel,
        holder: RecyclerView.ViewHolder
    ) {
        binding.imv.onSingleClick {
            onClick?.invoke(data.path)
        }
        Glide.with(binding.root).load(data.path)
            .override(256, 256)
            .encodeQuality(50)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(binding.imv)
    }

    class DiffCallBack :
        AbsBaseDiffCallBack<SelectedModel>() {
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