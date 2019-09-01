package ru.skillbranch.devintensive.ui.custom

import android.graphics.*
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.Dimension.PX
import ru.skillbranch.devintensive.utils.Utils


class BitmapBuilder(val width: Int, val height: Int) {

    private var bitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    private var text: String = ""
    private var textSize: Int = 0
    private var drawable: Drawable? = null
    @ColorInt private var textColor: Int = -0x1000000
    @ColorInt private var bgColor: Int = -0x1

    fun setBackgroundColor(@ColorInt color: Int) = this.apply { bgColor = color }
    fun setTextColor(@ColorInt color: Int) = this.apply { textColor = color }
    fun setText(text: String) = this.apply { this.text = text }
    fun setTextSize(@Dimension(unit = PX) size: Int) = this.apply { textSize = size }
    fun setDrawable(imageDrawable: Drawable?) = this.apply { drawable = imageDrawable }

    fun build(): Bitmap {
        val canvas = Canvas(bitmap)
        canvas.drawColor(bgColor)

        if (drawable != null)
            drawImage(canvas)

        if (text.isNotEmpty())
            drawText(canvas)

        return bitmap
    }

    private fun drawImage(canvas: Canvas) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        canvas.drawBitmap(
            Utils.getBitmapFromDrawable(drawable)!!,
            width.toFloat(),
            height.toFloat(),
            paint
        )
    }

    private fun drawText(canvas: Canvas) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = textSize.toFloat()
        paint.color = textColor
        paint.textAlign = Paint.Align.CENTER

        val textBounds = Rect()
        paint.getTextBounds(text, 0, text.length, textBounds)

        val backgroundBounds = RectF()
        backgroundBounds.set(0f, 0f, width.toFloat(), height.toFloat())

        val textBottom = backgroundBounds.centerY() - textBounds.exactCenterY()
        canvas.drawText(text, backgroundBounds.centerX(), textBottom, paint)
    }
}