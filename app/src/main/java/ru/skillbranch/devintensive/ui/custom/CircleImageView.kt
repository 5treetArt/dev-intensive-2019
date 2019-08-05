package ru.skillbranch.devintensive.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import ru.skillbranch.devintensive.R
import android.graphics.drawable.BitmapDrawable
import android.graphics.*
import android.graphics.Bitmap
import android.graphics.Shader
import android.graphics.BitmapShader
import android.graphics.Color.parseColor
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.core.graphics.drawable.toBitmap


/*
* CircleImageView
Необходимо реализовать CustomView для скругления установленного Drawable

Реализуй CustomView с названием класса CircleImageView и кастомными xml атрибутами
cv_borderColor (цвет границы (format="color") по умолчанию white)
и cv_borderWidth (ширина границы (format="dimension") по умолчанию 2dp).
CircleImageView должна превращать установленное изображение в круглое изображение с цветной рамкой,
у CircleImageView должны быть реализованы методы

@Dimension getBorderWidth():Int,
setBorderWidth(@Dimension dp:Int),
getBorderColor():Int,
setBorderColor(hex:String),
setBorderColor(@ColorRes colorId: Int).

Используй CircleImageView как ImageView для аватара пользователя (@id/iv_avatar)
*
* */


class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr){
    companion object {
        private const val DEFAULT_BORDER_COLOR = 0xFFFFFF
        private const val DEFAULT_BORDER_WIDTH = 2.0F
    }


    private var cv_borderColor = DEFAULT_BORDER_COLOR
    private var borderWidthInPixels = 0F
    private var cv_borderWidth = DEFAULT_BORDER_WIDTH

    private var viewWidth: Int = 0
    private var viewHeight: Int = 0
    private var paint: Paint? = null
    private var paintBorder: Paint? = null
    private var shader: BitmapShader? = null

    init {
        if(attrs != null){
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            cv_borderColor = a.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            cv_borderWidth = a.getDimension(R.styleable.CircleImageView_cv_borderWidth, DEFAULT_BORDER_WIDTH)
            borderWidthInPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, cv_borderWidth, context.resources.displayMetrics)
            a.recycle()
        }
        setup()
    }

    private fun setup() {
        paint = Paint()
        paint!!.isAntiAlias = true

        paintBorder = Paint()
        paintBorder!!.isAntiAlias = true
        paintBorder!!.color = cv_borderColor
        setLayerType(View.LAYER_TYPE_SOFTWARE, paintBorder)
    }

    @Dimension fun getBorderWidth(): Int{
        return borderWidthInPixels.toInt()
    }

    fun setBorderWidth(@Dimension dp: Int) {
        cv_borderWidth = dp.toFloat()
        borderWidthInPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, cv_borderWidth, context.resources.displayMetrics)
        invalidate()
    }

    fun getBorderColor():Int {
        return cv_borderColor
    }

    @SuppressLint("ResourceType")
    fun setBorderColor(hex:String){
        val color = parseColor(hex)
        setBorderColor(color)
    }

    fun setBorderColor(@ColorRes colorId: Int){
        val paintBorder = paintBorder ?: return
        cv_borderColor = colorId
        paintBorder.color = cv_borderColor
        invalidate()
    }


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {

        val drawable = drawable ?: return

        if (width == 0 || height == 0) {
            return
        }

        // load the bitmap
        val image = when (drawable) {
            is BitmapDrawable -> drawable.bitmap
            else -> drawable.toBitmap()
        }

        if (image != null) {

            // init shader
            shader = BitmapShader(
                Bitmap.createScaledBitmap(image, width, height, false),
                Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP
            )

            paint!!.shader = shader
            val circleCenter = viewWidth / 2

            canvas.drawCircle(
                (circleCenter + cv_borderWidth),
                (circleCenter + cv_borderWidth),
                circleCenter + cv_borderWidth,
                paintBorder!!
            )
            canvas.drawCircle(
                (circleCenter + cv_borderWidth),
                (circleCenter + cv_borderWidth),
                circleCenter.toFloat(),
                paint!!)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = measureWidth(widthMeasureSpec)
        val height = measureHeight(heightMeasureSpec)

        viewWidth = (width - cv_borderWidth * 2).toInt()
        viewHeight = (height - cv_borderWidth * 2).toInt()

        setMeasuredDimension(width, height)
    }

    private fun measureWidth(measureSpec: Int): Int =
        if (MeasureSpec.getMode(measureSpec) == MeasureSpec.EXACTLY) {
            MeasureSpec.getSize(measureSpec)
        } else {
            viewWidth
        }

    private fun measureHeight(measureSpec: Int): Int =
        if (MeasureSpec.getMode(measureSpec) == MeasureSpec.EXACTLY) {
            MeasureSpec.getSize(measureSpec)
        } else {
            viewHeight
        }// + 2  //(beware: ascent is a negative number)
}