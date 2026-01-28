package com.catcreator.catmaker.meme.dialog

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.catcreator.catmaker.meme.base.BaseDialog
import com.catcreator.catmaker.meme.utils.inhide
import com.catcreator.catmaker.meme.utils.onSingleClick
import com.catcreator.catmaker.meme.utils.showKeyboard
import com.catcreator.catmaker.meme.utils.viewToBitmap
import com.catcreator.catmaker.meme.R
import com.catcreator.catmaker.meme.databinding.DialogSpeechBinding

class DialogSpeech(context: Activity, val path: String) : BaseDialog<DialogSpeechBinding>(context, false) {
    var onDoneClick: ((Bitmap?) -> Unit) = { }
    override fun getContentView(): Int = R.layout.dialog_speech

    override fun initView() {
        setupWindow()
        binding.apply {
            edtSpeech.isFocusableInTouchMode = true
            edtSpeech.isFocusable = true
            edtSpeech.requestFocus()
            Glide.with(context).load(path).into(binding.imvBubble)
            edtSpeech.postDelayed({ context.showKeyboard(edtSpeech) },300)
        }

    }

    override fun bindView() {
        binding.apply {
            edtSpeech.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                    handleDone()
                    true
                } else {
                    false
                }
            }

            layoutRoot.onSingleClick {
                handleDone()
            }

            edtSpeech.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    binding.tvGetText.text = p0.toString()
                }

                override fun afterTextChanged(p0: Editable?) {}
            })
        }
    }
    private fun setupWindow() {
        window?.apply {
            setGravity(Gravity.CENTER)

            val width = if (true) WindowManager.LayoutParams.MATCH_PARENT
            else WindowManager.LayoutParams.WRAP_CONTENT
            val height = if (true) WindowManager.LayoutParams.MATCH_PARENT
            else WindowManager.LayoutParams.WRAP_CONTENT
            setLayout(width, height)

            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }
    fun handleDone(){
        binding.apply {
            edtSpeech.clearFocus()
            edtSpeech.inhide()
            tvGetText.isVisible = !TextUtils.isEmpty(edtSpeech.text.toString().trim())
            val bitmap = viewToBitmap(layoutBubble)
            onDoneClick.invoke(bitmap)
            dismiss()
        }
    }

}