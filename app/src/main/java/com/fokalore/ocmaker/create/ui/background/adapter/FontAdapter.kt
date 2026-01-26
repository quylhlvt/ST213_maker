package com.fokalore.ocmaker.create.ui.background.adapter

import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.fokalore.ocmaker.create.base.AbsBaseAdapter
import com.fokalore.ocmaker.create.base.AbsBaseDiffCallBack
import com.fokalore.ocmaker.create.data.model.SelectedModel
import com.fokalore.ocmaker.create.utils.onSingleClick
import com.fokalore.ocmaker.create.R
import com.fokalore.ocmaker.create.databinding.ItemFontBinding

class FontAdapter :
    AbsBaseAdapter<SelectedModel, ItemFontBinding>(R.layout.item_font, DiffCallBack()) {
    var onClick: ((Int) -> Unit)? = null
    var posSelect = 0
    override fun bind(
        binding: ItemFontBinding,
        position: Int,
        data: SelectedModel,
        holder: RecyclerView.ViewHolder
    ) {
        binding.imv.onSingleClick {
            onClick?.invoke(position)
        }

        binding.tv.typeface = ResourcesCompat.getFont(binding.root.context, data.color)
       if(data.isSelected){
           binding.imv.setImageResource(R.drawable.imv_font_true)
           binding.tv.setTextColor("#AB7920".toColorInt())
       }else{
           binding.imv.setImageResource(R.drawable.imv_font_false)
           binding.tv.setTextColor("#ffffff".toColorInt())
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