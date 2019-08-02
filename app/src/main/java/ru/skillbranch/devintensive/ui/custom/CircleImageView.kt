package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import ru.skillbranch.devintensive.R

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
    private var cv_borderWidth = DEFAULT_BORDER_WIDTH

    init {
        if(attrs != null){
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            cv_borderColor = a.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            cv_borderWidth = a.getDimension(R.styleable.CircleImageView_cv_borderWidth, DEFAULT_BORDER_WIDTH)
            a.recycle()
        }
    }

    @Dimension fun getBorderWidth(): Int{
        TODO("implement me")
    }

    fun setBorderWidth(@Dimension dp: Int){
        TODO("implement me")
    }

    fun getBorderColor():Int{
        TODO("implement me")
    }

    fun setBorderColor(hex:String){
        TODO("implement me")
    }

    fun setBorderColor(@ColorRes colorId: Int){
        TODO("implement me")
    }
}