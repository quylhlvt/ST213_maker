package com.fokalore.ocmaker.create.ui.background.adapter

import androidx.recyclerview.widget.RecyclerView
import com.fokalore.ocmaker.create.base.AbsBaseAdapter
import com.fokalore.ocmaker.create.base.AbsBaseDiffCallBack
import com.fokalore.ocmaker.create.data.model.SelectedModel
import com.fokalore.ocmaker.create.utils.hide
import com.fokalore.ocmaker.create.utils.onSingleClick
import com.fokalore.ocmaker.create.utils.show
import com.fokalore.ocmaker.create.R
import com.fokalore.ocmaker.create.databinding.ItemColorEdtBinding

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
        binding.bg.onSingleClick {

            onClick?.invoke(position)
        }
        binding.bg.setBackgroundColor(data.color)
        if(position == 0){
            binding.imvPlus.show()
        }else{
            binding.imvPlus.hide()
        }
        if(data.isSelected){
            binding.imv.show()
        }else{
            binding.imv.hide()
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