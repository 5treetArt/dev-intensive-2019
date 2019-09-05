package ru.skillbranch.devintensive.ui.adapters

import android.animation.ArgbEvaluator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.toBytesArray
import ru.skillbranch.devintensive.models.data.ChatItem
import kotlin.math.absoluteValue

class ChatItemTouchHelperCallback(
    val adapter: ChatAdapter,
    val icon: IconType = IconType.ARCHIVE_IN,
    val swipeListener: (ChatItem) -> Unit
) : ItemTouchHelper.Callback() {

    private val bgRect = RectF()
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val iconBounds =  Rect()
    private val evaluator = ArgbEvaluator()
    private val initColor = TypedValue()
    private val finishColor = TypedValue()

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return if(viewHolder is ItemTouchViewHolder &&
                  viewHolder !is ChatAdapter.ArchiveViewHolder) {
            makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.START)
        }else{
            makeFlag(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.START)
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        swipeListener.invoke(adapter.items[viewHolder.adapterPosition])
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE && viewHolder is ItemTouchViewHolder){
            viewHolder.onItemSelected()
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        if (viewHolder is ItemTouchViewHolder) viewHolder.onItemCleared()
        super.clearView(recyclerView, viewHolder)
    }


    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            val itemView = viewHolder.itemView
            drawBackground(canvas, itemView, dX)
            drawIcon(canvas, itemView, dX)
        }
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun drawBackground(canvas: Canvas, itemView: View, dX: Float) {
        with(bgRect){
            left = itemView.right.toFloat() + dX
            top = itemView.top.toFloat()
            right = itemView.right.toFloat()
            bottom = itemView.bottom.toFloat()
        }

        itemView.context.theme.resolveAttribute(R.attr.colorInitialSwipe, initColor, true)
        itemView.context.theme.resolveAttribute(R.attr.colorFinishSwipe, finishColor, true)

        bgPaint.color = evaluator.evaluate(
                dX.absoluteValue / itemView.width.toFloat(),
                initColor.data,
                finishColor.data
            ) as? Int ?: initColor.data

        canvas.drawRect(bgRect, bgPaint)
    }

 /*   private fun interpolateColor(width: Int, dX: Float, initialColor: Int, finishColor: Int): Int {

        val initialColorBytes = initialColor.toBytesArray()
        val initialOpaque = initialColorBytes[3]
        val initialRed = initialColorBytes[2]
        val initialGreen = initialColorBytes[1]
        val initialBlue = initialColorBytes[0]

        val finishColorBytes = finishColor.toBytesArray()
        val finishOpaque = finishColorBytes[3]
        val finishRed = finishColorBytes[2]
        val finishGreen = finishColorBytes[1]
        val finishBlue = finishColorBytes[0]

        val currentOpaque = initialOpaque + (finishOpaque - initialOpaque)*(dX.absoluteValue/width.toFloat()).toInt()
        val currentRed = initialRed + (finishRed - initialRed)*(dX.absoluteValue/width.toFloat()).toInt()
        val currentGreen = initialGreen + (finishGreen - initialGreen)*(dX.absoluteValue/width.toFloat()).toInt()
        val currentBlue = initialBlue + (finishBlue - initialBlue)*(dX.absoluteValue/width.toFloat()).toInt()

        val currentColor =
            currentBlue or
            ((currentGreen shl 8) and 0x0000FF00) or
            ((currentRed shl 16) and 0x00FF0000) or
            ((currentOpaque shl 24).toLong() and 0xFF000000L).toInt()

        return currentColor
    }*/

    private fun drawIcon(canvas: Canvas, itemView: View, dX: Float) {
        val icon = itemView.resources.getDrawable(
            when(icon){
                IconType.ARCHIVE_IN -> R.drawable.ic_archive_black_24dp
                IconType.ARCHIVE_OUT -> R.drawable.ic_unarchive_black_24dp
            },
            itemView.context.theme)
        val iconSize = itemView.resources.getDimensionPixelSize(R.dimen.icon_size)
        val space = itemView.resources.getDimensionPixelSize(R.dimen.spacing_normal_16)

        val margin = (itemView.bottom - itemView.top - iconSize) /2

        with(iconBounds){
            left = itemView.right + dX.toInt() + space
            top = itemView.top + margin
            right = itemView.right + dX.toInt() + iconSize + space
            bottom = itemView.bottom - margin
        }

        icon.bounds = iconBounds
        icon.draw(canvas)
    }
}

interface  ItemTouchViewHolder{
    fun onItemSelected()
    fun onItemCleared()
}

enum class IconType{
    ARCHIVE_IN,
    ARCHIVE_OUT,
}