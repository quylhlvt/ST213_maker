package com.catcreator.catmaker.meme.dialog

import android.app.Activity
import android.graphics.Color
import com.catcreator.catmaker.meme.base.BaseDialog
import com.catcreator.catmaker.meme.utils.onSingleClick
import com.catcreator.catmaker.meme.R
import com.catcreator.catmaker.meme.databinding.DialogColorPickerBinding


class ChooseColorDialog(context: Activity) : BaseDialog<DialogColorPickerBinding>(context, false) {
    var onDoneEvent: ((Int) -> Unit) = {}
    private var color = Color.WHITE
    override fun getContentView(): Int = R.layout.dialog_color_picker

    override fun initView() {
        binding.apply {
            colorPickerView.apply {
                hueSliderView = hueSlider
            }
            txtColor.post { txtColor.text = String.format("#%06X", 0xFFFFFF and color) }
        }
    }

    override fun bindView() {
        binding.apply {
            colorPickerView.setOnColorChangedListener { newColor -> color = newColor
                txtColor.post { txtColor.text = String.format("#%06X", 0xFFFFFF and color) } }
            btnClose.onSingleClick { dismiss() }
            btnDone.onSingleClick {
                dismiss()
                onDoneEvent.invoke(color)
            }
        }
    }


}