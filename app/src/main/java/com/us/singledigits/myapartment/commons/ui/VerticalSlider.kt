package com.us.singledigits.myapartment.commons.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.us.singledigits.myapartment.R
import kotlinx.android.synthetic.main.activity_lights.view.*

class VerticalSlider : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        val a = context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.VerticalSlider,
            0, 0
        )

        try {
            max = a.getInteger(R.styleable.VerticalSlider_vs_max, max)
            progress = a.getInteger(R.styleable.VerticalSlider_vs_progress, progress)
            cornerRadius = a.getDimension(R.styleable.VerticalSlider_vs_cornerRadius, cornerRadius)
        } finally {
            a.recycle()
        }
    }

    var cornerRadius = dpToPx(10).toFloat()
        set(value) {
            field = value
            invalidate()
        }
    var max: Int = 10
    var progress: Int = 5
        set(value) {
            if (value > max) {
                throw RuntimeException("progress must not be larger than max")
            }
            field = value
            onProgressChangeListener?.onChanged(progress, max)
            progressRect.set(
                0f,
                (1 - calculateProgress()) * measuredHeight,
                measuredWidth.toFloat(),
                measuredHeight.toFloat()
            )
            invalidate()
        }
    var onProgressChangeListener: OnSliderProgressChangeListener? = null

    private val iconWidth = dpToPx(36)
    private val iconRect: RectF = RectF()
    private val layoutRect: RectF = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
    private val layoutPaint = Paint().apply {
        color = Color.parseColor("#6643413A")
        isAntiAlias = true
    }
    private val progressRect: RectF =
        RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
    private val progressPaint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
    }
    private val path = Path()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (measuredHeight > 0 && measuredWidth > 0) {
            layoutRect.set(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
            progressRect.set(
                0f,
                (1 - calculateProgress()) * measuredHeight,
                measuredWidth.toFloat(),
                measuredHeight.toFloat()
            )
            iconRect.set(
                measuredWidth / 2f - iconWidth / 2,
                measuredHeight / 2f - iconWidth / 2,
                measuredWidth / 2f + iconWidth / 2,
                measuredHeight / 2f + iconWidth / 2
            )
            path.addRoundRect(layoutRect, cornerRadius, cornerRadius, Path.Direction.CW)
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.clipPath(path)
        canvas.drawRect(layoutRect, layoutPaint)
        canvas.drawRect(progressRect, progressPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                val y = event.y
                val currentHeight = measuredHeight - y
                val percent = currentHeight / measuredHeight.toFloat()
                progress = when {
                    percent >= 1 -> max
                    percent <= 0 -> 0
                    else -> (max * percent).toInt()
                }
                return true
            }
        }
        return false
    }

    private fun calculateProgress(): Float {
        return progress.toFloat() / max.toFloat()
    }

    interface OnSliderProgressChangeListener {
        fun onChanged(progress: Int, max: Int)
    }

    private fun dpToPx(dp: Int) = (dp * resources.displayMetrics.density).toInt()
}