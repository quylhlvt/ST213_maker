package com.catcreator.catmaker.meme.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.toColorInt
import com.catcreator.catmaker.meme.utils.DataHelper.dp

class StrokeTextView  @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : AppCompatTextView(context, attrs, defStyle) {

    private var strokeWidth = 4.dp(context).toFloat()
    private var strokeColor = "#FFFFFFFF".toColorInt()

    override fun onDraw(canvas: Canvas) {
        val textColor = currentTextColor

        // Vẽ stroke
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        setTextColor(strokeColor)
        super.onDraw(canvas)

        // Vẽ chữ chính
        paint.style = Paint.Style.FILL
        setTextColor(textColor)
        super.onDraw(canvas)
    }
}