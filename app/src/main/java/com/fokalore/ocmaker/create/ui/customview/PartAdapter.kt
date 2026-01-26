package com.fokalore.ocmaker.create.ui.customview

import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.fokalore.ocmaker.create.base.AbsBaseAdapter
import com.fokalore.ocmaker.create.base.AbsBaseDiffCallBack
import com.fokalore.ocmaker.create.utils.onClickCustom
import com.fokalore.ocmaker.create.R
import com.fokalore.ocmaker.create.databinding.ItemPartBinding

class PartAdapter : AbsBaseAdapter<String, ItemPartBinding>(R.layout.item_part, PathDiff()) {
    var onClick: ((Int,String) -> Unit)? = null
    var posPath = 0
    //    var checkOnline = false
    fun setPos(pos: Int) {
        posPath = pos
    }

    class PathDiff : AbsBaseDiffCallBack<String>() {
        override fun itemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun contentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem != newItem
        }

    }

    override fun bind(
        binding: ItemPartBinding,
        position: Int,
        data: String,
        holder: RecyclerView.ViewHolder
    ) {
        binding.apply {
            materialCard.strokeColor= if (posPath == position) ContextCompat.getColor(binding.root.context,R.color.app_color4)else ContextCompat.getColor(binding.root.context,R.color.app_color3)
        }
        Glide.with(binding.imv).clear(binding.imv)
        // 🔴 BẮT BUỘC: scaleType cố định
        binding.imv.scaleType = ImageView.ScaleType.CENTER_INSIDE
        // reset padding (KHÔNG dùng margin)
        when (data) {
            "none" -> {
                loadImage(binding, R.drawable.ic_none)
            }
            "dice" -> {
                loadImage(binding, R.drawable.ic_random_layer)
            }
            else -> {
                loadImage(binding, data)
            }
        }
        binding.root.onClickCustom {
            onClick?.invoke(position,data)
        }
    }
    private fun loadImage(binding: ItemPartBinding, data: Any) {
        Glide.with(binding.imv)
            .load(data)
            .encodeQuality(90)
            .override(256)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(binding.imv)
    }
}