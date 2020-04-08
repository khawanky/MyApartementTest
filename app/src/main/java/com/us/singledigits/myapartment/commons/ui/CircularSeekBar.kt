package com.us.singledigits.myapartment.commons.ui

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import com.us.singledigits.myapartment.R
import kotlin.math.*

class CircularSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyle) {

    /**
     * Used to scale the dp units to pixels
     */
    private val density = resources.displayMetrics.density
    private val a = attrs?.let { context.obtainStyledAttributes(attrs, R.styleable.CircularSeekBar, defStyle, defStyleRes) }

    /**
     * The Width of the background arc for the CircularSeekBar
     */
    var arcWidth = a.useOrDefault(8 * density) { getDimension(R.styleable.CircularSeekBar_arcWidth, it) }
        set(value) {
            field = value
            arcPaint.strokeWidth = value
            invalidate()
        }

    /**
     * The Maximum value that this CircularSeekBar can be set to
     */
    var progressMin = a.useOrDefault(0F) { getFloat(R.styleable.CircularSeekBar_progressMin, it) }
        set(progress) {
            field = progress.bound(0F, Float.MAX_VALUE)
            primaryProgress = primaryProgress.bound(progressMin, progressMax)
            secondaryProgress = secondaryProgress.bound(progressMin, progressMax)
            invalidate()
        }

    /**
     * The Maximum value that this CircularSeekBar can be set to
     */
    var progressMax = a.useOrDefault(100F) { getFloat(R.styleable.CircularSeekBar_progressMax, it) }
        set(progress) {
            field = progress.bound(0F, Float.MAX_VALUE)
            primaryProgress = primaryProgress.bound(progressMin, progressMax)
            secondaryProgress = secondaryProgress.bound(progressMin, progressMax)
            invalidate()
        }

    /**
     * The primary progress that the CircularSeekBar is set to and all its properties
     */
    var primaryProgress = a.useOrDefault(0F) { getFloat(R.styleable.CircularSeekBar_primaryProgress, it) }
        set(progress) {
            field = progress.bound(progressMin, progressMax)
            primaryProgressUpdated(false)
        }

    var primaryProgressWidth = a.useOrDefault(8 * density) { getDimension(R.styleable.CircularSeekBar_primaryProgressWidth, it) }
        set(value) {
            field = value
            primaryProgressPaint.strokeWidth = value
            invalidate()
        }

    var primaryProgressThumbStyle = a.useOrDefault(0) { getInt(R.styleable.CircularSeekBar_primaryProgressThumbStyle, it) }
        set(style) {
            field = style
            invalidate()
        }

    var primaryProgressThumbDrawable: Drawable? = null
        set(value) {
            val thumbHalfHeight = value?.intrinsicHeight?.div(2) ?: 0
            val thumbHalfWidth = value?.intrinsicWidth?.div(2) ?: 0

            value?.setBounds(-thumbHalfWidth, -thumbHalfHeight, thumbHalfWidth, thumbHalfHeight)

            field = value

            invalidate()
        }

    var primaryProgressThumbLineWidth = a.useOrDefault(4 * density) { getDimension(R.styleable.CircularSeekBar_primaryProgressThumbLineWidth, it) }
        set(value) {
            field = value
            primaryProgressThumbLinePaint.strokeWidth = value
            invalidate()
        }

    var primaryProgressThumbLineLength = a.useOrDefault(30 * density) { getDimension(R.styleable.CircularSeekBar_primaryProgressThumbLineLength, it) }
        set(value) {
            field = value
            invalidate()
        }
    /**
     * The secondary progress that the CircularSeekBar is set to and all its properties
     */
    var secondaryProgress = a.useOrDefault(0F) { getFloat(R.styleable.CircularSeekBar_secondaryProgress, it) }
        set(progress) {
            field = progress.bound(progressMin, progressMax)
            secondaryProgressUpdated(false)
        }

    var secondaryProgressWidth = a.useOrDefault(8 * density) { getDimension(R.styleable.CircularSeekBar_secondaryProgressWidth, it) }
        set(value) {
            field = value
            secondaryProgressPaint.strokeWidth = value
            invalidate()
        }

    var secondaryProgressThumbStyle = a.useOrDefault(1) { getInt(R.styleable.CircularSeekBar_secondaryProgressThumbStyle, it) }
        set(style) {
            field = style
            invalidate()
        }

    var secondaryProgressThumbDrawable: Drawable? = null
        set(value) {
            val thumbHalfHeight = value?.intrinsicHeight?.div(2) ?: 0
            val thumbHalfWidth = value?.intrinsicWidth?.div(2) ?: 0

            value?.setBounds(-thumbHalfWidth, -thumbHalfHeight, thumbHalfWidth, thumbHalfHeight)

            field = value

            invalidate()
        }

    var secondaryProgressThumbLineWidth = a.useOrDefault(4 * density) { getDimension(R.styleable.CircularSeekBar_secondaryProgressThumbLineWidth, it) }
        set(value) {
            field = value
            secondaryProgressThumbLinePaint.strokeWidth = value
            invalidate()
        }

    var secondaryProgressThumbLineLength = a.useOrDefault(30 * density) { getDimension(R.styleable.CircularSeekBar_secondaryProgressThumbLineLength, it) }
        set(value) {
            field = value
            invalidate()
        }

    var showThumbs = a.useOrDefault(true) { getBoolean(R.styleable.CircularSeekBar_showThumbs, it) }
        set(value) {
            field = value
            invalidate()
        }

    /**
     * Switches for raising the pop-up box
     */
    var showLabel = a.useOrDefault(true) { getBoolean(R.styleable.CircularSeekBar_showLabel, it) }
        set(value) {
            field = value
            invalidate()
        }

    var labelTextSize = a.useOrDefault(13 * density) { getDimension(R.styleable.CircularSeekBar_labelTextSize, it) }
        set(value) {
            field = value
            labelPaint.textSize = value
            invalidate()
        }

    // The initial rotational offset -90 means we start at 12 o'clock
    var offsetAngle = a.useOrDefault(-90F) { getFloat(R.styleable.CircularSeekBar_offsetAngle, it) }
        set(angle) {
            field = angle.bound(0F, 360F)
            updateThumbsPositions()
            updateLinesPositions()
            updateLabelPosition()
            invalidate()
        }

    /**
     * The rotation of the CircularSeekBar- 0 is twelve o'clock
     */
    var rotationAngle = a.useOrDefault(0F) { getFloat(R.styleable.CircularSeekBar_rotationAngle, it) }
        set(angle) {
            field = angle.bound(0F, 360F)
            updateThumbsPositions()
            updateLinesPositions()
            updateLabelPosition()
            invalidate()
        }

    /**
     * The Angle to start drawing this Arc from
     */
    var startAngle = a.useOrDefault(225F) { getFloat(R.styleable.CircularSeekBar_startAngle, it) }
        set(angle) {
            field = angle.bound(0F, 360F)
            updateThumbsPositions()
            updateLinesPositions()
            updateLabelPosition()
            invalidate()
        }

    /**
     * The Angle through which to draw the arc (Max is 360)
     */
    var sweepAngle = a.useOrDefault(270F) { getFloat(R.styleable.CircularSeekBar_sweepAngle, it) }
        set(angle) {
            field = angle.bound(0F, 360F)
            updateThumbsPositions()
            updateLinesPositions()
            updateLabelPosition()
            invalidate()
        }

    /**
     * Give the CircularSeekBar rounded edges
     */
    var roundedEdges = a.useOrDefault(true) { getBoolean(R.styleable.CircularSeekBar_roundEdges, it) }
        set(value) {
            if (value) {
                arcPaint.strokeCap = Paint.Cap.ROUND
                primaryProgressPaint.strokeCap = Paint.Cap.ROUND
            } else {
                arcPaint.strokeCap = Paint.Cap.SQUARE
                primaryProgressPaint.strokeCap = Paint.Cap.SQUARE
            }
            field = value
            invalidate()
        }

    /**
     * Is clockwise or not
     */
    var clockwise = a.useOrDefault(true) { getBoolean(R.styleable.CircularSeekBar_clockwise, it) }
        set(value) {
            field = value
            invalidate()
        }

    var onCircularSeekBarChangeListener: OnCircularSeekBarChangeListener? = null
    var progress = primaryProgress
        get() = primaryProgress
        set(value) {
            field = value
            primaryProgress = value
            secondaryProgress = value
        }

    private var arcPaint: Paint = makePaint(
        color = a.useOrDefault(Color.WHITE.withAlpha(0.2F)) { getColor(R.styleable.CircularSeekBar_arcColor, it) },
        width = arcWidth
    )

    private var primaryProgressPaint: Paint = makePaint(
        color = a.useOrDefault(Color.WHITE) { getColor(R.styleable.CircularSeekBar_primaryProgressColor, it) },
        width = primaryProgressWidth
    )

    private var primaryProgressThumbLinePaint: Paint = makePaint(
        color = a.useOrDefault(Color.WHITE) { getColor(R.styleable.CircularSeekBar_primaryProgressThumbLineColor, it) },
        width = primaryProgressThumbLineWidth
    )

    private var secondaryProgressPaint: Paint = makePaint(
        color = a.useOrDefault(Color.BLACK) { getColor(R.styleable.CircularSeekBar_secondaryProgressColor, it) },
        width = secondaryProgressWidth,
        allowRounded = false
    )

    private var secondaryProgressThumbLinePaint: Paint = makePaint(
        color = a.useOrDefault(Color.WHITE) { getColor(R.styleable.CircularSeekBar_secondaryProgressThumbLineColor, it) },
        width = secondaryProgressThumbLineWidth
    )

    private var labelPaint: Paint = makeTextPaint(
        color = a.useOrDefault(Color.WHITE) { getColor(R.styleable.CircularSeekBar_labelColor, it) },
        size = labelTextSize
    )

    // Internal variables
    private var arcDiameter = 0F
    private var arcRadius = 0F
    private val arcRect = RectF()

    private var translateX = 0F
    private var translateY = 0F

    private var primaryProgressSweep = 0F

    private var primaryProgressThumbXPos = 0F
    private var primaryProgressThumbYPos = 0F

    private var primaryProgressLineX1Pos = 0F
    private var primaryProgressLineY1Pos = 0F
    private var primaryProgressLineX2Pos = 0F
    private var primaryProgressLineY2Pos = 0F

    private var secondaryProgressSweep = 0F

    private var secondaryProgressThumbXPos = 0F
    private var secondaryProgressThumbYPos = 0F

    private var secondaryProgressLineX1Pos = 0F
    private var secondaryProgressLineY1Pos = 0F
    private var secondaryProgressLineX2Pos = 0F
    private var secondaryProgressLineY2Pos = 0F

    private var labelXPos = 0F
    private var labelYPos = 0F

    private var touchAngle = 0F

    private var minimumTouchTarget = MIN_TOUCH_TARGET_DP * density

    init {
        primaryProgressThumbDrawable = ContextCompat.getDrawable(getContext(), R.drawable.circular_slider_drawable)
        a?.getDrawable(R.styleable.CircularSeekBar_primaryProgressThumbDrawable)?.let {
            primaryProgressThumbDrawable = it
        }

        secondaryProgressThumbDrawable = ContextCompat.getDrawable(getContext(), R.drawable.circular_slider_drawable)
        a?.getDrawable(R.styleable.CircularSeekBar_secondaryProgressThumbDrawable)?.let {
            secondaryProgressThumbDrawable = it
        }

        a?.recycle()

        primaryProgressUpdated(false)
        secondaryProgressUpdated(false)
    }
    
    fun animatePrimaryTo(primaryUpdated: Float, duration: Long = 2000, interpolator: TimeInterpolator = AccelerateDecelerateInterpolator()) {
        animateTo(primaryProgress, primaryUpdated, duration, interpolator,
            {
                primaryProgress = it.animatedValue as Float
            },
            {
                primaryProgressUpdated(false)
            })
    }

    fun animateSecondaryTo(secondaryUpdated: Float, duration: Long = 2000, interpolator: TimeInterpolator = AccelerateDecelerateInterpolator()) {
        animateTo(secondaryProgress, secondaryUpdated, duration, interpolator,
            {
                secondaryProgress = it.animatedValue as Float
            },
            {
                secondaryProgressUpdated(false)
            })
    }

    private fun animateTo(progress: Float, updated: Float,
                          duration: Long = 2000,
                          interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
                          updateListener: (ValueAnimator) -> Unit = {},
                          onAnimationEnd: (animator: Animator) -> Unit = {}) {
        var updatedProgress = updated
        if (updatedProgress == INVALID_PROGRESS_VALUE) {
            return
        }
        updatedProgress = updatedProgress.bound(progressMin, progressMax)

        val animation = ValueAnimator.ofFloat(progress, updatedProgress)
        animation.addUpdateListener { updateListener(it) }
        animation.addListener(onEnd = { onAnimationEnd(it) })
        animation.duration = duration
        animation.interpolator = interpolator
        animation.start()
    }

    override fun onDraw(canvas: Canvas) {
        if (!clockwise) {
            canvas.scale(-1f, 1f, arcRect.centerX(), arcRect.centerY())
        }

        // Draw the arcs
        val arcStart = startAngle + offsetAngle + rotationAngle
        val arcSweep = sweepAngle
        canvas.drawArc(arcRect, arcStart, arcSweep, false, arcPaint)

        // Primary Progress
        canvas.drawArc(arcRect, arcStart, primaryProgressSweep, false, primaryProgressPaint)

        // Secondary Progress
        val offset = if(showThumbs) 2.times(if(primaryProgress < secondaryProgress) -1 else 1) else 0
        canvas.drawArc(arcRect, arcStart + primaryProgressSweep ,
            secondaryProgressSweep - primaryProgressSweep + offset,
            false, secondaryProgressPaint)

        if (showThumbs && secondaryProgressThumbStyle == 1 && roundToFraction(primaryProgress, 10F) != roundToFraction(secondaryProgress, 10F)) {
            // Draw the secondary line
            canvas.drawLine(secondaryProgressLineX1Pos, secondaryProgressLineY1Pos, secondaryProgressLineX2Pos, secondaryProgressLineY2Pos, secondaryProgressThumbLinePaint)
        }

        if (showThumbs && primaryProgressThumbStyle == 1) {
            // Draw the primary line
            canvas.drawLine(primaryProgressLineX1Pos, primaryProgressLineY1Pos, primaryProgressLineX2Pos, primaryProgressLineY2Pos, primaryProgressThumbLinePaint)
        }

        if (showLabel && roundToFraction(primaryProgress, 10F) != roundToFraction(secondaryProgress, 10F)) {
            canvas.drawText(getLabel(), labelXPos, labelYPos, labelPaint)
        }

        if (showThumbs && secondaryProgressThumbStyle == 0 && roundToFraction(primaryProgress, 10F) != roundToFraction(secondaryProgress, 10F)) {
            // Draw the thumb nail
            canvas.save()
            canvas.translate(translateX - secondaryProgressThumbXPos, translateY - secondaryProgressThumbYPos)
            secondaryProgressThumbDrawable?.draw(canvas)
            canvas.restore()
        }

        if (showThumbs && primaryProgressThumbStyle == 0) {
            // Draw the thumb nail
            canvas.save()
            canvas.translate(translateX - primaryProgressThumbXPos, translateY - primaryProgressThumbYPos)
            primaryProgressThumbDrawable?.draw(canvas)
            canvas.restore()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)

        translateX = width * 0.5F
        translateY = height * 0.5F

        arcDiameter = minOf(width, height) - paddingLeft - 30F - 5F
        arcRadius = arcDiameter / 2F

        val top = height / 2F - arcDiameter / 2F
        val left = width / 2F - arcDiameter / 2F

        arcRect[left, top, left + arcDiameter] = top + arcDiameter

        updateThumbsPositions()
        updateLinesPositions()
        updateLabelPosition()

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isEnabled) {
            this.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    onStartTrackingTouch()
                    updateOnTouch(event)
                }
                MotionEvent.ACTION_MOVE -> updateOnTouch(event)
                MotionEvent.ACTION_UP -> {
                    onStopTrackingTouch()
                    isPressed = false
                    parent.requestDisallowInterceptTouchEvent(false)
                }
                MotionEvent.ACTION_CANCEL -> {
                    onStopTrackingTouch()
                    isPressed = false
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            return true
        }
        return false
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (primaryProgressThumbDrawable?.isStateful == true) {
            primaryProgressThumbDrawable?.state = drawableState
        }
        if (secondaryProgressThumbDrawable?.isStateful == true) {
            secondaryProgressThumbDrawable?.state = drawableState
        }
        invalidate()
    }

    private fun onStartTrackingTouch() {
        onCircularSeekBarChangeListener?.onStartTrackingTouch(this)
    }

    private fun onStopTrackingTouch() {
        onCircularSeekBarChangeListener?.onStopTrackingTouch(this)
    }

    /**
     * Update progress value
     *
     * @param event generated from touch
     */
    private fun updateOnTouch(event: MotionEvent) {
        val ignoreTouch = ignoreTouch(event.x, event.y)
        if (ignoreTouch) {
            return
        }
        isPressed = true
        touchAngle = getTouchDegrees(event.x, event.y)
        val progress = getProgressForAngle(touchAngle)
        onPrimaryProgressRefresh(progress, true)
    }

    /**
     * Get the point of contact
     *
     * @param xPos current x point
     * @param yPos current y point
     * @return if Thumb is touched or not
     */
    private fun ignoreTouch(xPos: Float, yPos: Float): Boolean {
        var ignore = true
        val x = xPos - (translateX - primaryProgressThumbXPos)
        val y = yPos - (translateY - primaryProgressThumbYPos)
        // Check if the touch event is on the Thumb
        val touchRadius = sqrt(x * x + y * y)
        if (touchRadius < max(primaryProgressThumbDrawable?.intrinsicHeight?.toFloat() ?: minimumTouchTarget, minimumTouchTarget)) {
            ignore = false
        }
        return ignore
    }

    /**
     * Convert from cartesian to polar
     *
     * @param xPos current x point
     * @param yPos current y point
     * @return angle of the Thumb w.r.t the arc.
     */
    private fun getTouchDegrees(xPos: Float, yPos: Float): Float {
        var x = xPos - translateX
        val y = yPos - translateY
        //invert the x-coord if we are rotating anti-clockwise
        x = if (clockwise) x else -x
        // convert to arc Angle
        var angle = (atan2(y, x) + Math.PI / 2F - rotationAngle.toRadians()).toDegrees().toFloat()
        angle -= startAngle
        if (angle < 0) {
            angle += 360F
        }
        return angle
    }

    /**
     * Get value for current angle
     *
     * @param angle current angle
     * @return progress for angle
     */
    private fun getProgressForAngle(angle: Float): Float {
        var touchProgress = progressMin + roundToFraction(valuePerDegree() * angle)
        touchProgress = if (touchProgress < progressMin) INVALID_PROGRESS_VALUE else touchProgress
        touchProgress = if (touchProgress > progressMax) INVALID_PROGRESS_VALUE else touchProgress
        return touchProgress
    }

    /**
     * Round the values to set fraction
     *
     * @param x value of progress
     * @return rounded to the nearest precision
     */
    private fun roundToFraction(x: Float, fractionDigits: Float = 100F): Float {
        return round(x * fractionDigits) / fractionDigits
    }

    /**
     * Getter
     *
     * @return value per degree
     */
    private fun valuePerDegree(): Float {
        return (progressMax - progressMin) / sweepAngle
    }

    /**
     * Update thumbs positions
     */
    private fun updateThumbsPositions() {
        val primaryThumbAngle = startAngle + primaryProgressSweep + rotationAngle + 90
        primaryProgressThumbXPos = arcRadius * cos(primaryThumbAngle.toRadians())
        primaryProgressThumbYPos = arcRadius * sin(primaryThumbAngle.toRadians())

        val secondaryThumbAngle = startAngle + secondaryProgressSweep + rotationAngle + 90
        secondaryProgressThumbXPos = arcRadius * cos(secondaryThumbAngle.toRadians())
        secondaryProgressThumbYPos = arcRadius * sin(secondaryThumbAngle.toRadians())
    }

    /**
     * Update lines positions
     */
    private fun updateLinesPositions() {
        val primaryLineAngle = startAngle + primaryProgressSweep + rotationAngle
        primaryProgressLineX1Pos = (arcRect.centerX() + (arcRadius - primaryProgressThumbLineLength / 2F) * sin(primaryLineAngle.toRadians()))
        primaryProgressLineY1Pos = (arcRect.centerY() - (arcRadius - primaryProgressThumbLineLength / 2F) * cos(primaryLineAngle.toRadians()))

        primaryProgressLineX2Pos = (arcRect.centerX() + (arcRadius + primaryProgressThumbLineLength / 2F) * sin(primaryLineAngle.toRadians()))
        primaryProgressLineY2Pos = (arcRect.centerY() - (arcRadius + primaryProgressThumbLineLength / 2F) * cos(primaryLineAngle.toRadians()))

        val secondaryLineAngle = startAngle + secondaryProgressSweep + rotationAngle
        secondaryProgressLineX1Pos = (arcRect.centerX() + (arcRadius - secondaryProgressThumbLineLength / 2F) * sin(secondaryLineAngle.toRadians()))
        secondaryProgressLineY1Pos = (arcRect.centerY() - (arcRadius - secondaryProgressThumbLineLength / 2F) * cos(secondaryLineAngle.toRadians()))

        secondaryProgressLineX2Pos = (arcRect.centerX() + (arcRadius + secondaryProgressThumbLineLength / 2F) * sin(secondaryLineAngle.toRadians()))
        secondaryProgressLineY2Pos = (arcRect.centerY() - (arcRadius + secondaryProgressThumbLineLength / 2F) * cos(secondaryLineAngle.toRadians()))
    }

    /**
     * Update label position
     */
    private fun updateLabelPosition() {
        val labelAngle = startAngle + secondaryProgressSweep + rotationAngle

        val label = getLabel()
        val bounds = Rect()

        labelPaint.getTextBounds(label, 0, label.length, bounds)

        val textWidth = bounds.width()
        val textHeight = bounds.height()

        labelXPos = (arcRect.centerX() + (arcRadius + textWidth + secondaryProgressThumbLineLength / 2F) * sin(labelAngle.toRadians()))
        labelYPos = (arcRect.centerY() - (arcRadius + textHeight + secondaryProgressThumbLineLength / 2F) * cos(labelAngle.toRadians()))
    }

    /**
     * Upon progress refresh from listener
     *
     * @param updated progress value
     * @param fromUser is from user
     */
    private fun onPrimaryProgressRefresh(updated: Float, fromUser: Boolean) {
        var updatedProgress = updated
        if (updatedProgress == INVALID_PROGRESS_VALUE) {
            return
        }
        updatedProgress = updated.bound(progressMin, progressMax)
        primaryProgress = updatedProgress

        primaryProgressUpdated(fromUser)
    }

    /**
     * Progress value updated
     *
     * @param fromUser is from user
     */
    private fun primaryProgressUpdated(fromUser: Boolean) {
        onCircularSeekBarChangeListener?.onProgressChanged(this, primaryProgress, secondaryProgress, fromUser)
        primaryProgressSweep = (primaryProgress - progressMin) / (progressMax - progressMin) * sweepAngle

        updateThumbsPositions()
        updateLinesPositions()
        updateLabelPosition()

        invalidate()
    }

    private fun secondaryProgressUpdated(fromUser: Boolean) {
        onCircularSeekBarChangeListener?.onProgressChanged(this, primaryProgress, secondaryProgress, fromUser)
        secondaryProgressSweep = (secondaryProgress - progressMin) / (progressMax - progressMin) * sweepAngle

        updateThumbsPositions()
        updateLinesPositions()
        updateLabelPosition()

        invalidate()
    }

    private fun getLabel(): String = roundToFraction(secondaryProgress, 10F).toInt().toString()

    private fun makePaint(color: Int, width: Float, allowRounded: Boolean = true) = Paint().apply {
        this.color = color
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = width
        strokeCap = if (roundedEdges && allowRounded) Paint.Cap.ROUND else Paint.Cap.SQUARE
    }

    private fun makeTextPaint(color: Int, size: Float) = Paint().apply {
        this.color = color
        textSize = size
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        style = Paint.Style.FILL
    }

    private fun <T, R> T?.useOrDefault(default: R, usage: T.(R) -> R) = if (this == null) default else usage(default)

    private fun Int.withAlpha(alpha: Float): Int =
        Color.argb(round(Color.alpha(this) * alpha).toInt(), Color.red(this), Color.green(this), Color.blue(this))

    companion object {
        private const val INVALID_PROGRESS_VALUE = -1F
        /**
         * Minimum touch target size in DP. 48dp is the Android design recommendation
         */
        private const val MIN_TOUCH_TARGET_DP = 48F
    }

    interface OnCircularSeekBarChangeListener {
        /**
         * Notification that the progress level has changed. Clients can use the
         * fromUser parameter to distinguish user-initiated changes from those
         * that occurred programmatically.
         *
         * @param circularSeekBar The CircularSeekBar whose progress has changed
         * @param primaryProgress The current primary progress level.
         * @param secondaryProgress The current secondary progress level.
         * @param fromUser        True if the progress change was initiated by the user.
         */
        fun onProgressChanged(circularSeekBar: CircularSeekBar, primaryProgress: Float, secondaryProgress: Float, fromUser: Boolean)

        /**
         * Notification that the user has started a touch gesture. Clients may
         * want to use this to disable advancing the seekbar.
         *
         * @param circularSeekBar The CircularSeekBar in which the touch gesture began
         */
        fun onStartTrackingTouch(circularSeekBar: CircularSeekBar)

        /**
         * Notification that the user has finished a touch gesture. Clients may
         * want to use this to re-enable advancing the CircularSeekBar.
         *
         * @param circularSeekBar The CircularSeekBar in which the touch gesture began
         */
        fun onStopTrackingTouch(circularSeekBar: CircularSeekBar)
    }

}

internal fun <T : Number> T.bound(min: T, max: T) = when {
    toDouble() > max.toDouble() -> max
    toDouble() < min.toDouble() -> min
    else -> this
}

internal fun Double.toRadians(): Double = Math.toRadians(this)
internal fun Double.toDegrees(): Double = Math.toDegrees(this)

internal fun Float.toRadians(): Float = FloatMath.toRadians(this)
internal fun Float.toDegrees(): Float = FloatMath.toDegrees(this)

object FloatMath {
    fun toRadians(angdeg: Float): Float = Math.toRadians(angdeg.toDouble()).toFloat()
    fun toDegrees(angrad: Float): Float = Math.toDegrees(angrad.toDouble()).toFloat()
}