package com.catcreator.catmaker.meme.ui.background.adapter

import androidx.recyclerview.widget.RecyclerView
import com.catcreator.catmaker.meme.base.AbsBaseAdapter
import com.catcreator.catmaker.meme.base.AbsBaseDiffCallBack
import com.catcreator.catmaker.meme.data.model.SelectedModel
import com.catcreator.catmaker.meme.utils.hide
import com.catcreator.catmaker.meme.utils.onSingleClick
import com.catcreator.catmaker.meme.utils.show
import com.catcreator.catmaker.meme.R
import com.catcreator.catmaker.meme.databinding.ItemColorEdtBinding

class ColorTextAdapter :
    AbsBaseAdapter<SelectedModel, ItemColorEdtBinding>(R.layout.item_color_edt, DiffCallBack()) {
    var onClick: ((Int) -> Unit)? = null
    var posSelect = 1
    override fun bind(
        binding: ItemColorEdtBinding,
        position: Int,
        data: SelectedModel,
        holder: RecyclerView.ViewHolder
    ) {
        binding.imvColor.onSingleClick {

            onClick?.invoke(position)
        }
        binding.imvColor.setBackgroundColor(data.color)
        if(position == 0){
            binding.btnAddColor.show()
        }else{
            binding.btnAddColor.hide()
        }
        if(data.isSelected){
            binding.apply {
                vFocus.show()
                cardColor.cardElevation=  3f
            }
        }else{
            binding.apply {
                vFocus.hide()
                cardColor.cardElevation=  0f
            }
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