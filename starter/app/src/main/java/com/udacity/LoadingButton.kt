package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var defaultBackgroundColor = 0
    private var loadingBackgroundColor = 0
    private var pacmanColor = 0
    private var textColor = 0
    private lateinit var defaultText: String
    private lateinit var loadingText: String
    private lateinit var clickedText: String

    private lateinit var currentText: String

    private var loadingBarWidth = 0f
    private var pacmanAngle = 0f


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val textPaint = TextPaint().apply {
        textAlign = Paint.Align.CENTER
        textSize = resources.getDimension(R.dimen.default_text_size)
    }

    private val textHeight = textPaint.descent() - textPaint.ascent()
    private val textOffset = (textHeight / 2) - textPaint.descent()

    private val valueAnimator = ValueAnimator()

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { property, oldValue, newValue ->
        when (newValue) {
            ButtonState.Clicked -> {
                Log.e("Current clicked", "loading")
                isClickable = false
                currentText = clickedText
                invalidate()
            }
            ButtonState.Loading -> {
                Log.e("Current state", "loading")
                currentText = loadingText
                valueAnimator.duration = 5000
                valueAnimator.setFloatValues(0f, widthSize.toFloat())
                valueAnimator.addUpdateListener { animator ->
                    loadingBarWidth = animator.animatedValue as Float
                    pacmanAngle = loadingBarWidth * 360 / widthSize
                    invalidate()
                }
                valueAnimator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        if (buttonState == ButtonState.Loading) {
                            valueAnimator.start()
                        }
                    }
                })
                valueAnimator.start()
            }
            ButtonState.Completed -> {
                Log.e("Current state", "completed")
                valueAnimator.cancel()
                isClickable = true
                currentText = defaultText
                loadingBarWidth = 0f
                pacmanAngle = 0f
                invalidate()
            }
        }
    }


    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            defaultBackgroundColor =
                getColor(R.styleable.LoadingButton_defaultBackgroundColor, Color.RED)
            loadingBackgroundColor =
                getColor(R.styleable.LoadingButton_loadingBackgroundColor, Color.BLUE)
            pacmanColor = getColor(R.styleable.LoadingButton_pacmanColor, Color.YELLOW)
            textColor = getColor(R.styleable.LoadingButton_textColor, Color.BLUE)
            defaultText = getString(R.styleable.LoadingButton_defaultText).toString()
            loadingText = getString(R.styleable.LoadingButton_loadingText).toString()
            clickedText = getString(R.styleable.LoadingButton_clickedText).toString()
            currentText = defaultText
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawButton(canvas)
        drawLoadingBar(canvas)
        drawButtonText(canvas)
        drawPacman(canvas)
    }

    private fun drawLoadingBar(canvas: Canvas) {
        canvas.save()
        val progressBar = RectF(
            0f, 0f, loadingBarWidth, measuredHeight.toFloat()
        )
        paint.color = loadingBackgroundColor
        canvas.drawRect(progressBar, paint)
    }

    // even though drawCircle is better suited name here drawPacman is more fun
    private fun drawPacman(canvas: Canvas) {
        canvas.save()
        val pacmanRect = RectF(
            3 * measuredWidth.toFloat() / 4 - textOffset * 2,
            measuredHeight.toFloat() / 4 + textOffset / 2,
            3 * measuredWidth.toFloat() / 4 + textHeight - textOffset * 2,
            measuredHeight.toFloat() / 4 + textHeight + textOffset / 2
        )
        paint.color = pacmanColor
        canvas.drawArc(
            pacmanRect, 0f, pacmanAngle, true, paint
        )
    }

    private fun drawButton(canvas: Canvas) {
        canvas.save()
        val downloadButtonRect = RectF(
            0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat()
        )
        paint.color = defaultBackgroundColor
        canvas.drawRect(downloadButtonRect, paint)
    }

    private fun drawButtonText(canvas: Canvas) {
        canvas.save()
        textPaint.color = textColor
        canvas.drawText(
            currentText,
            measuredWidth.toFloat() / 2, measuredHeight.toFloat() / 2 + textOffset, textPaint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}