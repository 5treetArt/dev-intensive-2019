package ru.skillbranch.devintensive.ui.custom

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.Context
import android.view.View
import androidx.annotation.ColorInt
import ru.skillbranch.devintensive.utils.Utils


class MaterialDividerItemDecorator(
    val context: Context,
    @ColorInt val dividerColor: Int,
    @ColorInt val backgroundColor: Int
    ): RecyclerView.ItemDecoration(){

    private var dividerPaint: Paint = Paint()
    private var backgroundPaint: Paint = Paint()
    private var layoutOrientation = -1
    private val dividerMargin = Utils.convertDpToPx(MATERIAL_DIVIDER_MARGIN, context)

    companion object {
        val MATERIAL_DIVIDER_MARGIN = 72f
        val DIVIDER_HEIGHT = 1
    }

    init{
        with(dividerPaint){
            color = this@MaterialDividerItemDecorator.dividerColor
            strokeWidth = DIVIDER_HEIGHT.toFloat()
        }
        with(backgroundPaint){
            color = this@MaterialDividerItemDecorator.backgroundColor
            strokeWidth = DIVIDER_HEIGHT.toFloat()
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        if (parent.layoutManager is LinearLayoutManager && layoutOrientation == -1) {
            layoutOrientation = (parent.layoutManager as LinearLayoutManager).orientation
        }

        if (layoutOrientation == LinearLayoutManager.HORIZONTAL) {
            outRect.set(0, 0, DIVIDER_HEIGHT, 0)
        } else {
            outRect.set(0, 0, 0, DIVIDER_HEIGHT)
        }

    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        if (layoutOrientation == LinearLayoutManager.HORIZONTAL) {
            horizontalDivider(c, parent)
        } else {
            verticalDivider(c, parent)
        }
    }

    private fun horizontalDivider(c: Canvas, parent: RecyclerView) {
        val top = parent.paddingTop
        val bottom = parent.height - parent.paddingBottom

        val itemCount = parent.childCount
        for (i in 0 until itemCount) {
            val child = parent.getChildAt(i)
            val params = child
                .layoutParams as RecyclerView.LayoutParams
            val left = child.right + params.rightMargin
            c.drawLine(left.toFloat(), top.toFloat(), left.toFloat(), bottom.toFloat(), dividerPaint)
        }
    }

    private fun verticalDivider(c: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child
                .layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            c.drawLine(left.toFloat(), top.toFloat(), dividerMargin.toFloat(), top.toFloat(), backgroundPaint)
            c.drawLine(left.toFloat() + dividerMargin, top.toFloat(), right.toFloat(), top.toFloat(), dividerPaint)
        }
    }
}