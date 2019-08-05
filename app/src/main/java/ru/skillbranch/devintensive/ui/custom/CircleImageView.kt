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
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import ru.skillbranch.devintensive.App
import java.lang.Math.min


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
        private const val DEFAULT_BORDER_COLOR =  Color.WHITE
        private const val DEFAULT_BORDER_WIDTH = 2.0F
    }


    //private var borderColor = DEFAULT_BORDER_COLOR
    ////private var borderWidthInPixels = 0F
    //private var borderWidth = convertDpToPx(DEFAULT_BORDER_WIDTH)

    private var viewWidth: Int = 0
    private var viewHeight: Int = 0
    private var paint: Paint? = null
    private var paintBorder: Paint? = null
    private var shader: BitmapShader? = null

    private var borderColor = DEFAULT_BORDER_COLOR
    private var borderWidth = convertDpToPx(DEFAULT_BORDER_WIDTH)

    init {
        if(attrs != null){
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            borderColor = a.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            //borderWidth = a.getDimension(R.styleable.CircleImageView_cv_borderWidth, DEFAULT_BORDER_WIDTH)
            borderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_cv_borderWidth, convertDpToPx(DEFAULT_BORDER_WIDTH).toInt()).toFloat()
            a.recycle()
        }
        //setup()
    }

    fun convertDpToPx(dp: Float): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)

    fun convertPxToDp(px: Int): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px.toFloat(), context.resources.displayMetrics)

    fun getBorderWidth(): Int = convertPxToDp(borderWidth.toInt()).toInt()

    fun setBorderWidth(dp: Int) {
        borderWidth = convertDpToPx(dp.toFloat())
        this.invalidate()
    }

    fun getBorderColor(): Int = borderColor

    fun setBorderColor(hex: String) {
        borderColor = Color.parseColor(hex)
        this.invalidate()
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        borderColor = ContextCompat.getColor(App.applicationContext(), colorId)
        this.invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        var bitmap = getBitmapFromDrawable() ?: return
        if (width == 0 || height == 0) return

        bitmap = getScaledBitmap(bitmap, width)
        bitmap = getCenterCroppedBitmap(bitmap, width)
        bitmap = getCircleBitmap(bitmap)

        if (borderWidth > 0)
            bitmap = getStrokedBitmap(bitmap, borderWidth.toInt(), borderColor)

        canvas.drawBitmap(bitmap, 0F, 0F, null)
    }

    private fun getStrokedBitmap(squareBmp: Bitmap, strokeWidth: Int, color: Int): Bitmap {
        val inCircle = RectF()
        val strokeStart = strokeWidth / 2F
        val strokeEnd = squareBmp.width - strokeWidth / 2F

        inCircle.set(strokeStart , strokeStart, strokeEnd, strokeEnd)

        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        strokePaint.color = color
        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeWidth = strokeWidth.toFloat()

        val canvas = Canvas(squareBmp)
        canvas.drawOval(inCircle, strokePaint)

        return squareBmp
    }

    private fun getCenterCroppedBitmap(bitmap: Bitmap, size: Int): Bitmap {
        val cropStartX = (bitmap.width - size) / 2
        val cropStartY = (bitmap.height - size) / 2

        return Bitmap.createBitmap(bitmap, cropStartX, cropStartY, size, size)
    }

    private fun getScaledBitmap(bitmap: Bitmap, minSide: Int) : Bitmap {
        return if (bitmap.width != minSide || bitmap.height != minSide) {
            val smallest = min(bitmap.width, bitmap.height).toFloat()
            val factor = smallest / minSide
            Bitmap.createScaledBitmap(bitmap, (bitmap.width / factor).toInt(), (bitmap.height / factor).toInt(), false)
        } else bitmap
    }

    private fun getBitmapFromDrawable(): Bitmap? {
        if (drawable == null)
            return null

        if (drawable is BitmapDrawable)
            return (drawable as BitmapDrawable).bitmap

        return drawable.toBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    }

    private fun getCircleBitmap(bitmap: Bitmap): Bitmap {
        val smallest = min(bitmap.width, bitmap.height)
        val outputBmp = Bitmap.createBitmap(smallest, smallest, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBmp)

        val paint = Paint()

        paint.isAntiAlias = true
        paint.isFilterBitmap = true
        paint.isDither = true
        canvas.drawCircle(smallest / 2F, smallest / 2F, smallest / 2F, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, 0F, 0F,  paint)

        return outputBmp
    }

    //private fun setup() {
    //    paint = Paint()
    //    paint!!.isAntiAlias = true
//
    //    paintBorder = Paint()
    //    paintBorder!!.isAntiAlias = true
    //    paintBorder!!.color = borderColor
    //    setLayerType(View.LAYER_TYPE_SOFTWARE, paintBorder)
    //}
//
    //@Dimension fun getBorderWidth(): Int{
    //    return convertPxToDp(borderWidth).toInt()
    //}


//    fun setBorderWidth(@Dimension dp: Int) {
//        borderWidth = convertDpToPx(dp.toFloat())
//        invalidate()
//    }
//
//    fun getBorderColor():Int {
//        return borderColor
//    }
//
//    @SuppressLint("ResourceType")
//    fun setBorderColor(hex:String){
//        val color = parseColor(hex)
//        setBorderColor(color)
//    }
//
//    fun setBorderColor(@ColorRes colorId: Int){
//        borderColor = ContextCompat.getColor(App.applicationContext(), colorId)
//        invalidate()
//    }
//
//
//    @SuppressLint("DrawAllocation")
//    override fun onDraw(canvas: Canvas) {
//
//        val drawable = drawable ?: return
//
//        if (width == 0 || height == 0) {
//            return
//        }
//
//        // load the bitmap
//        val image = when (drawable) {
//            is BitmapDrawable -> drawable.bitmap
//            else -> drawable.toBitmap()
//        }
//
//        if (image != null) {
//
//            // init shader
//            shader = BitmapShader(
//                Bitmap.createScaledBitmap(image, width, height, false),
//                Shader.TileMode.CLAMP,
//                Shader.TileMode.CLAMP
//            )
//
//            paint!!.shader = shader
//            val circleCenter = viewWidth / 2
//
//            canvas.drawCircle(
//                ((circleCenter + borderWidth).toFloat()),
//                ((circleCenter + borderWidth).toFloat()),
//                (circleCenter + borderWidth).toFloat(),
//                paintBorder!!
//            )
//            canvas.drawCircle(
//                ((circleCenter + borderWidth).toFloat()),
//                ((circleCenter + borderWidth).toFloat()),
//                circleCenter.toFloat(),
//                paint!!)
//        }
//    }
//
//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        val width = measureWidth(widthMeasureSpec)
//        val height = measureHeight(heightMeasureSpec)
//
//        viewWidth = (width - borderWidth * 2).toInt()
//        viewHeight = (height - borderWidth * 2).toInt()
//
//        setMeasuredDimension(width, height)
//    }
//
//    private fun measureWidth(measureSpec: Int): Int =
//        if (MeasureSpec.getMode(measureSpec) == MeasureSpec.EXACTLY) {
//            MeasureSpec.getSize(measureSpec)
//        } else {
//            viewWidth
//        }
//
//    private fun measureHeight(measureSpec: Int): Int =
//        if (MeasureSpec.getMode(measureSpec) == MeasureSpec.EXACTLY) {
//            MeasureSpec.getSize(measureSpec)
//        } else {
//            viewHeight
//        }// + 2  //(beware: ascent is a negative number)
}