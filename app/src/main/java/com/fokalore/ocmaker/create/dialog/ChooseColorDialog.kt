package com.fokalore.ocmaker.create.dialog

import android.app.Activity
import android.graphics.Color
import com.fokalore.ocmaker.create.base.BaseDialog
import com.fokalore.ocmaker.create.utils.onSingleClick
import com.fokalore.ocmaker.create.R
import com.fokalore.ocmaker.create.databinding.DialogColorPickerBinding


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