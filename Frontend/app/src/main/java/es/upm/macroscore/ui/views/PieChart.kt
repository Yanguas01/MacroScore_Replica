package es.upm.macroscore.presentation.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import es.upm.macroscore.R

class PieChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var progressColor: Int = context.getColor(R.color.proteinChart)
    private var maxProgress: Float = 100f
    private var progress: Float = 0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeCap = Paint.Cap.ROUND
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
    }
    private val rect = RectF()

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.PieChart, 0, 0).apply {
            try {
                progressColor = getColor(R.styleable.PieChart_progressColor, this@PieChart.progressColor)
                maxProgress = getFloat(R.styleable.PieChart_maxProgress, this@PieChart.maxProgress)
                progress = getFloat(R.styleable.PieChart_progress, this@PieChart.progress)

                paint.color = progressColor
            } finally {
                recycle()
            }
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = width / 2f
        val cy = height / 2f
        val radius = cx.coerceAtMost(cy)
        rect.set(cx - radius, cy - radius, cx + radius, cy + radius)

        val completeTurns = (progress / maxProgress).toInt()

        for (i in 0 until completeTurns) {
            canvas.drawArc(rect, -90f, 360f, true, paint)
        }

        val additionalProgress = progress % maxProgress
        val angle = 360f * additionalProgress / maxProgress
        canvas.drawArc(rect, -90f, angle, true, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        val size = width.coerceAtMost(height)

        setMeasuredDimension(size, size)
    }

    fun setProgress(progress: Float) {
        this.progress = progress
        invalidate()
    }

    fun getProgress(): Float {
        return this.progress
    }
}