package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import ru.skillbranch.devintensive.App
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.utils.Utils
import kotlin.math.roundToInt

class AvatarImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr){
    private var avatarSize: Int = 0
    private var rect: Rect = Rect()
    private var pathR: Path = Path()
    private lateinit var paintText: Paint
    private lateinit var paintBorder: Paint
    private var borderWidth: Float = DEFAULT_BORDER_WIDTH
    private var borderColor: Int = DEFAULT_BORDER_COLOR
    private var initials: String? =  null
    private val bgColors = arrayOf(
        "#7BC862",
        "#E17076",
        "#FAA774",
        "#6EC9CB",
        "#65AADD",
        "#A695E7",
        "#EE7AAE"
    )

    companion object {
        private const val DEFAULT_BORDER_WIDTH = 2f
        private const val DEFAULT_BORDER_COLOR = 0xffffff

    }

    init {
        if(attrs != null){
            val a = context.obtainStyledAttributes(attrs, R.styleable.AvatarImageView)
            borderColor = a.getColor(R.styleable.AvatarImageView_aiv_borderColor, DEFAULT_BORDER_COLOR)
            borderWidth = a.getDimensionPixelSize(
                R.styleable.AvatarImageView_aiv_borderWidth,
                convertDpToPx(DEFAULT_BORDER_WIDTH)
            ).toFloat()
            a.recycle()
        }
    }

    fun setInitials(initials: String, salt: String = ""){
        this.initials = initials
        val avatar = getAvatarBitmap(initials, salt)
        setImageBitmap(avatar)
    }

    fun setAvatarDrawable(drawable: Drawable){
        val avatar = getAvatarBitmap(drawable)
        setImageBitmap(avatar)
    }

    private fun getAvatarBitmap(drawable: Drawable): Bitmap {
        val bgColor = context.getColor(R.color.color_gray_dark)
        return BitmapBuilder(layoutParams.width, layoutParams.height)
            .setBackgroundColor(bgColor)
            .setDrawable(drawable)
            .build()
    }

    private fun getAvatarBitmap(text: String, salt: String): Bitmap {

        val bgColor = Color.parseColor(bgColors[(text.hashCode() + salt.hashCode()) % bgColors.size])

        return BitmapBuilder(layoutParams.width, layoutParams.height)
            .setBackgroundColor(bgColor)
            .setText(text)
            .setTextSize(convertSpToPx(context, 12))
            .setTextColor(Color.WHITE)
            .build()
    }

    private fun convertSpToPx(context: Context, sp: Int): Int =
        sp * context.resources.displayMetrics.scaledDensity.roundToInt()


    fun convertPxToDp(px: Int): Int = (px / context.resources.displayMetrics.density).roundToInt()

    fun convertDpToPx( dp: Float): Int = (dp * context.resources.displayMetrics.density).roundToInt()

    fun getBorderWidth(): Int = convertPxToDp(borderWidth.toInt())

    fun setBorderWidth(dp: Int) {
        borderWidth = convertDpToPx(dp.toFloat()).toFloat()
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
        var bitmap = Utils.getBitmapFromDrawable(drawable)

        if (bitmap == null || width == 0 || height == 0)
            return

        bitmap = getScaledBitmap(bitmap, width)
        bitmap = getCenterCroppedBitmap(bitmap, width)
        bitmap = getCircleBitmap(bitmap)

        if (borderWidth > 0)
            bitmap = getStrokedBitmap(bitmap, borderWidth.toInt(), borderColor)

        canvas.drawBitmap(bitmap, 0F, 0F, null)
    }



    private fun getScaledBitmap(bitmap: Bitmap, minSide: Int) : Bitmap =
        if (bitmap.width != minSide || bitmap.height != minSide) {
            val smallest = Math.min(bitmap.width, bitmap.height).toFloat()
            //val factor = smallest / minSide
            Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * minSide / smallest).toInt(),
                (bitmap.height * minSide / smallest).toInt(),
                false)
        } else bitmap

    private fun getCenterCroppedBitmap(bitmap: Bitmap, size: Int): Bitmap =
        Bitmap.createBitmap(bitmap,
            (bitmap.width - size) / 2,
            (bitmap.height - size) / 2,
            size, size)

    private fun getCircleBitmap(bitmap: Bitmap): Bitmap {
        val smallest = Math.min(bitmap.width, bitmap.height)
        val outputBmp = Bitmap.createBitmap(smallest, smallest, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBmp)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG)

        canvas.drawCircle(smallest / 2F, smallest / 2F, smallest / 2F, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, 0F, 0F,  paint)

        return outputBmp
    }

    private fun getStrokedBitmap(squareBmp: Bitmap, strokeWidth: Int, color: Int): Bitmap {
        val inCircle = RectF()
        val strokeStart = strokeWidth / 2F
        val strokeEnd = squareBmp.width - strokeWidth / 2F

        inCircle.set(strokeStart, strokeStart, strokeEnd, strokeEnd)

        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        strokePaint.color = color
        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeWidth = strokeWidth.toFloat()

        val canvas = Canvas(squareBmp)
        canvas.drawOval(inCircle, strokePaint)

        return squareBmp
    }

}