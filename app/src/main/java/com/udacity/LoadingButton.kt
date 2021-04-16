package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.withStyledAttributes
import kotlin.math.abs
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0

    private var animState = false

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
                animState = true
            }
            ButtonState.Loading -> {
                loadingAnim(10000)
            }
            ButtonState.Completed -> {
                loadingAnim(1000)
            }
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = context.resources.getDimension(R.dimen.default_text_size)
    }

    private val cornerRadius = context.resources.getDimension(R.dimen.round_corner)
    private var colorBackground = 0
    private var colorLoading = 0
    private var colorCircle = 0
    private var buttonText: String? = ""
    private var buttonLoadingText: String? = ""
    private var mainText: String? = ""
    private var textX = 0f
    private var textY = 0f
    private var circleLeft = 0f
    private var circleTop = 0f
    private var circleRight = 0f
    private var circleBottom = 0f

    private var scaleXRect: Float = 0f
    private var angleCircle = 0f

    private var valueAnimator: ValueAnimator? = null
    private var animCancel = false

    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            colorBackground = getColor(R.styleable.LoadingButton_colorBackground, 0)
            colorLoading = getColor(R.styleable.LoadingButton_colorLoading, 0)
            colorCircle = getColor(R.styleable.LoadingButton_colorCircle, 0)
            buttonText = getString(R.styleable.LoadingButton_text)
            buttonLoadingText = getString(R.styleable.LoadingButton_textWhileLoading)
        }
        mainText = buttonText
    }

    private fun loadingAnim(time: Long) {
        if (valueAnimator?.isRunning == true)
            valueAnimator?.cancel()
        valueAnimator = ValueAnimator.ofFloat(scaleXRect, widthSize.toFloat())
        valueAnimator?.duration = time
        valueAnimator?.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator?.addUpdateListener { animation ->
            val currentValue = animation.animatedValue as Float
            scaleXRect = currentValue
            angleCircle = (currentValue / widthSize.toFloat()) * 360f
            invalidate()
            if (animation.currentPlayTime in 8001..8999)
                valueAnimator?.pause()
        }
        valueAnimator?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                super.onAnimationStart(animation, isReverse)
                mainText = buttonLoadingText
                invalidate()
            }

            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                if (!animCancel) {
                    scaleXRect = 0f
                    angleCircle = 0f
                    valueAnimator = null
                    mainText = buttonText
                    animState = false
                } else {
                    animCancel = false
                }
                invalidate()
            }

            override fun onAnimationCancel(animation: Animator?) {
                super.onAnimationCancel(animation)
                animCancel = true
                invalidate()
            }
        })
        valueAnimator?.start()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = colorBackground
        canvas?.drawRoundRect(
            0f,
            0f,
            widthSize.toFloat(),
            heightSize.toFloat(),
            cornerRadius,
            cornerRadius,
            paint
        )

        if (animState) {
            paint.color = colorLoading
            canvas?.drawRoundRect(
                0f,
                0f,
                scaleXRect,
                heightSize.toFloat(),
                cornerRadius,
                cornerRadius,
                paint
            )
            paint.color = colorCircle
            canvas?.drawArc(
                circleLeft,
                circleTop,
                circleRight,
                circleBottom,
                0f,
                angleCircle,
                true,
                paint
            )
        }
        paint.color = Color.WHITE
        canvas?.drawText(mainText!!, textX, textY, paint)
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
        val metrics = paint.fontMetrics
        val absHeightTx = abs(metrics.descent - metrics.ascent)
        val textHeight = absHeightTx / 2 - metrics.descent
        textX = (widthSize / 2).toFloat()
        textY = (heightSize / 2).toFloat() + textHeight
        val textWidth = paint.measureText(buttonLoadingText)
        circleLeft = widthSize / 2 + textWidth / 2
        circleTop = heightSize / 2 - absHeightTx / 2
        circleRight = circleLeft + absHeightTx
        circleBottom = circleTop + absHeightTx
        setMeasuredDimension(w, h)
    }

}