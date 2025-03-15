package com.tera.custom_slider

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import kotlin.math.max

typealias OnChangeListener = (value: Float) -> Unit

class SliderCustom(
    context: Context,
    attrs: AttributeSet?,
    defStyleRes: Int
) : View(context, attrs, defStyleRes) {

    constructor(context: Context, attributesSet: AttributeSet?) :
            this(context, attributesSet, 0)

    constructor(context: Context) : this(context, null)

    companion object {
        const val COLOR_ACTIVE = -10655316
        const val COLOR_INACTIVE = -4933891
        const val COLOR_END = Color.RED

        const val COLOR_THUMB = -10655316 // Палец
        const val COLOR_STROKE = -16513163 // Обводка
        const val COLOR_DOT = -10655316
        const val COLOR_HALO = -7899727 // Ореол
        const val ALPHA_HALO = 80
        const val RADIUS_DOT = 5f
    }

    private val paintA = Paint()
    private val paintN = Paint()
    private val paintWave = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintThumb = Paint()
    private val paintDot = Paint()
    private val paintStroke = Paint()
    private val paintHalo = Paint()
    private val path = Path()

    private var listener: OnChangeListener? = null

    private var mHInd = 0f       // Высота индикатора
    private var mCenterY = 0f    // Центр по Y
    private var mTrackMax = 0f   // Длина всей дорожки
    private var mLenActive = 0f  // Длина дорожки значения
    private var mRatio = 0f      // Коэффициент по Х

    private var mIconSize = 100f   // Размер иконки
    private var mIconRad = mIconSize / 2

    private var mTrackRadius = 0f // Радиус дорожки
    private var mTrackTop = 0f    // Верх дорожки
    private var mTrackBottom = 0f // Низ дорожки

    private var mThumbX = 0f
    private var mThumbY = 0f
    private var mThumbRadStroke = 0f // Радиус обводки
    private var mWidthStroke = 0f   // Ширина обводки
    private var mHeightStroke = 0f  // Высота обводки
    private var mRectRadius = 0f    // Радиус прямоугольного пальца

    // Halo
    private var mHaloStroke = 25f // Толщина Halo
    private var mHaloRadius = 0f
    private var mHaloWidth = 0f
    private var mHaloHeight = 0f
    private var mHaloRadiusR = 0f
    private var mHaloMax = 0f
    private var mHaloColor = COLOR_HALO
    private var mAlpha = ALPHA_HALO
    private var mRatioHW = 0f // Отношение высоты к ширине
    private var mDiff = 0f

    // Отступ от начала по Х
    private var mOffset = 55f + mHaloStroke / 2

    private var keyHalo = false
    private var keyDown = false

    private var mColorStart = 0
    private var mColorEnd = 0

    // Атрибуты
    private var mMax = 0f
    private var mMin = 0f
    private var mValue = 0f

    private var mThumbRadius = 0f  // Радиус пальца
    private var mThumbWidth = 0f   // Ширина пальца
    private var mThumbHeight = 0f  // Высота пальца
    private var mThumbColor = 0
    private var mThumbStroke = 0f  // Толщина обводки
    private var mThumbStrokeColor = 0
    private var mThumbStyle = 0
    private var mThumbIcon = 0      // Иконка
    private var mIconColor = 0
    private var mShowHalo = false // Показать Halo

    private var mTrackHeight = 0f // Толщина дорожки
    private var mActiveColor = 0
    private var mInactiveColor = 0
    private var mTrackStyle = 0
    private var mChangeColor = false
    private var mEndColor = 0

    private var mWaveCount = 0  // Число периодов
    private var mSweep = 0      // Амплитуда
    private var mWaveWidth = 5f // Толщина волны

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SliderCustom)

        mMax = a.getFloat(R.styleable.SliderCustom_valueMax, 100f)
        mMin = a.getFloat(R.styleable.SliderCustom_valueMin, 0f)
        mValue = a.getFloat(R.styleable.SliderCustom_value, 0f)

        mThumbRadius = a.getDimensionPixelSize(R.styleable.SliderCustom_thumbRadius, 25).toFloat()
        mThumbWidth = a.getDimensionPixelSize(R.styleable.SliderCustom_thumbWidth, 40).toFloat()
        mThumbHeight = a.getDimensionPixelSize(R.styleable.SliderCustom_thumbHeight, 70).toFloat()
        mThumbColor = a.getColor(R.styleable.SliderCustom_thumbColor, COLOR_THUMB)
        mThumbStroke = a.getDimensionPixelSize(R.styleable.SliderCustom_thumbStroke, 0).toFloat()
        mThumbStrokeColor = a.getColor(R.styleable.SliderCustom_thumbStrokeColor, COLOR_STROKE)
        mThumbStyle = a.getInt(R.styleable.SliderCustom_thumbStyle, 0)
        mThumbIcon = a.getResourceId(R.styleable.SliderCustom_thumbIcon, 0)
        mIconColor = a.getColor(R.styleable.SliderCustom_thumbIconColor, -1)
        mShowHalo = a.getBoolean(R.styleable.SliderCustom_thumbShowHalo, true)

        mTrackHeight = a.getDimensionPixelSize(R.styleable.SliderCustom_trackHeight, 20).toFloat()
        mTrackStyle = a.getInt(R.styleable.SliderCustom_trackStyle, 0)
        mActiveColor = a.getColor(R.styleable.SliderCustom_trackActiveColor, COLOR_ACTIVE)
        mInactiveColor = a.getColor(R.styleable.SliderCustom_trackInactiveColor, COLOR_INACTIVE)
        mChangeColor = a.getBoolean(R.styleable.SliderCustom_trackChangeColor, false)
        mEndColor = a.getColor(R.styleable.SliderCustom_trackEndColor, COLOR_END)

        mWaveCount = a.getInt(R.styleable.SliderCustom_waveCount, 10)
        mSweep = a.getDimensionPixelSize(R.styleable.SliderCustom_waveSweep, 35)
        mWaveWidth = a.getDimensionPixelSize(R.styleable.SliderCustom_waveWidth, 8).toFloat()
        a.recycle()

        if (mValue > mMax) mValue = mMax
        if (mValue < mMin) mValue = mMin

        initPaint()
        initParams()
    }

    private fun initParams() {
        if (mWaveCount < 1) mWaveCount = 1

        // Отступы и размеры
        if (mThumbIcon != 0) {
            mThumbX = mIconSize / 2f
            mThumbY = mIconSize
        } else if (mThumbStyle == 1) {

            mThumbX = mThumbWidth / 2f
            mThumbY = mThumbHeight

        } else {
            mThumbX = mThumbRadius
            mThumbY = mThumbRadius * 2
            mHaloRadius = mThumbRadius
        }

        mHInd = mCenterY + mTrackHeight + 8f  // Высота индикатора
        mCenterY = mTrackHeight + 8

        mHaloWidth = mThumbWidth
        mHaloHeight = mThumbHeight
        mHaloRadiusR = mHaloWidth / 2

        mRatioHW = mHaloHeight / mHaloWidth

        mHaloMax = mThumbX + mHaloStroke

        if (mTrackStyle == 0)
            mSweep = 0

        if (!mShowHalo || mThumbIcon != 0) {
            keyHalo = false
            mHaloStroke = 0f
        } else
            keyHalo = true

        if (mThumbWidth > mThumbHeight) {
            mOffset = mThumbHeight + mHaloStroke / 2
            mThumbY = mThumbWidth
        }

        mTrackRadius = mTrackHeight / 2
        mThumbRadStroke = mThumbX - mThumbStroke / 2

        mRectRadius = mThumbWidth / 2
        mWidthStroke = mThumbWidth - mThumbStroke / 2
        mHeightStroke = mThumbHeight - mThumbStroke / 2

        mDiff = mMax - mMin
        mColorStart = mActiveColor
        mColorEnd = mEndColor
    }

    private fun initPaint() {
        paintA.color = mActiveColor
        paintN.color = mInactiveColor

        paintWave.color = mActiveColor
        paintWave.style = Paint.Style.STROKE
        paintWave.strokeWidth = mWaveWidth

        paintThumb.color = mThumbColor
        paintDot.color = COLOR_DOT

        paintStroke.color = mThumbStrokeColor
        paintStroke.style = Paint.Style.STROKE
        paintStroke.strokeWidth = mThumbStroke

        paintHalo.color = mHaloColor
        paintHalo.alpha = mAlpha
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (keyHalo) {
                    mHaloRadius = mThumbRadius
                    mHaloWidth = mThumbWidth
                    mHaloHeight = mThumbHeight
                    mAlpha = ALPHA_HALO
                    paintHalo.alpha = mAlpha
                    keyDown = true
                    startAnim()
                }
                changeValue(event.x)
                if (mChangeColor)
                    setColorTrack(mValue)
            }

            MotionEvent.ACTION_MOVE -> {
                changeValue(event.x)
                if (mChangeColor)
                    setColorTrack(mValue)
            }

            MotionEvent.ACTION_UP -> {
                if (keyHalo) {
                    mHaloRadius = mHaloMax
                    mHaloWidth = mHaloMax * 2
                    mHaloHeight = mThumbHeight + mHaloStroke * 2
                    mHaloRadiusR = mHaloWidth / 2
                    keyDown = false
                    startAnim()
                }
            }
        }
        return true
    }

    // Изменение значения
    private fun changeValue(eventX: Float) {
        val xc = mOffset + mLenActive
        val dx = eventX - xc
        mLenActive += dx

        var value = mLenActive / mRatio

        if (value < 0) value = 0f
        if (value > mMax - mMin) value = mMax - mMin

        mValue = value + mMin
        invalidate()
        listener?.invoke(mValue)
    }

    // Установка цвета
    private fun setColorTrack(value: Float) {
        val ratio = (value - mMin) / mDiff

        mActiveColor = ColorUtils.blendARGB(mColorStart, mColorEnd, ratio)
        paintA.color = mActiveColor
        paintWave.color = mActiveColor
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mLenActive = (mValue - mMin) * mRatio

        // Активная линия
        if (mTrackStyle == 1 || mTrackStyle == 2 || mTrackStyle == 3) // Волны
            drawWave(canvas)
        else
            drawTracActive(canvas)

        // Не активная линия
        drawTracInactive(canvas)

        // Палец
        if (mThumbIcon != 0)
            drawThumbIcon(canvas)
        else if (mThumbStyle == 1) {
            drawHaloR(canvas)
            drawThumbRound(canvas)
        } else {
            drawHaloC(canvas)
            drawThumb(canvas)
        }
    }

    // Левая активная линия
    private fun drawTracActive(canvas: Canvas) {
        val y1 = mTrackTop
        val y2 = mTrackBottom
        val x1 = mOffset
        val x2 = mOffset + mLenActive
        val r = mTrackRadius

        // Обрезка
        if (mThumbIcon != 0) {
            val rect = getRectIconL()
            canvas.save()
            canvas.clipRect(rect)
            canvas.drawRoundRect(x1, y1, x2, y2, r, r, paintA)
            //canvas.drawRect(rect, paintRect)
            canvas.restore()
        } else
            canvas.drawRoundRect(x1, y1, x2, y2, r, r, paintA)
    }

    // Область видимости активной дорожки
    private fun getRectIconL(): RectF {
        val x1 = mOffset
        val y1 = 0f
        val x2 = mOffset + mLenActive - mIconRad
        val y2 = height.toFloat()
        val rect = RectF(x1, y1, x2, y2)
        return rect
    }

    // Правая не активная линия
    private fun drawTracInactive(canvas: Canvas) {
        val y1 = mTrackTop
        val y2 = mTrackBottom
        val x1 = mOffset + mLenActive
        val x2 = mOffset + mTrackMax
        val r = mTrackRadius

        // Обрезка
        if (mThumbIcon != 0) {
            val rect = getRectIconR()
            canvas.save()
            canvas.clipRect(rect)
            canvas.drawRoundRect(x1, y1, x2, y2, r, r, paintN)
            //canvas.drawRect(rect, paintRect)
            canvas.restore()
        } else
            canvas.drawRoundRect(x1, y1, x2, y2, r, r, paintN)

        // Точка
        val rad = RADIUS_DOT
        val x = mOffset + mTrackMax - r
        val y = mCenterY
        canvas.drawCircle(x, y, rad, paintDot)
    }

    // Область видимости не активной дорожки
    private fun getRectIconR(): RectF {
        val x1 = mOffset + mLenActive + mIconRad
        val y1 = 0f
        val x2 = mOffset + mTrackMax
        val y2 = height.toFloat()
        val rect = RectF(x1, y1, x2, y2)
        return rect
    }

    // Палец круглый
    private fun drawThumb(canvas: Canvas) {

        val xc = mOffset + mLenActive
        val yc = mCenterY
        canvas.drawCircle(xc, yc, mThumbX, paintThumb)

        // Обводка
        val r = mThumbRadStroke
        canvas.drawCircle(xc, yc, r, paintStroke)
    }

    // Палец прямоугольный
    private fun drawThumbRound(canvas: Canvas) {
        var w = mThumbWidth
        var h = mThumbHeight
        val r = mRectRadius

        var rect = getRectThumb(w, h)
        canvas.drawRoundRect(rect, r, r, paintThumb)

        // Обводка
        w = mWidthStroke
        h = mHeightStroke

        rect = getRectThumb(w, h)
        canvas.drawRoundRect(rect, r, r, paintStroke)
    }

    // Гало круглого пальца
    private fun drawHaloC(canvas: Canvas) {
        val xc = mOffset + mLenActive
        val yc = mCenterY
        val r = mHaloRadius
        canvas.drawCircle(xc, yc, r, paintHalo)
    }

    // Гало прямоугольного пальца
    private fun drawHaloR(canvas: Canvas) {
        val w = mHaloWidth
        val h = mHaloHeight
        val r = mHaloRadiusR

        val rect = getRectThumb(w, h)
        canvas.drawRoundRect(rect, r, r, paintHalo)
    }

    // Анимация
    private fun startAnim() {
        val size1: Float
        val size2: Float

        if (keyDown) {
            size1 = 0f
            size2 = mHaloStroke
        } else {
            size1 = mAlpha.toFloat()
            size2 = 0f
        }

        val r = mHaloRadius
        val w = mHaloWidth
        val h = mHaloHeight

        val anim = ValueAnimator.ofFloat(size1, size2)
        anim.duration = 200
        anim.addUpdateListener { valueAnimator ->
            val dSize = valueAnimator.animatedValue as Float
            val k = dSize * 2
            if (keyDown) {
                if (mThumbStyle == 0)
                    mHaloRadius = r + dSize
                else {
                    mHaloWidth = w + k
                    mHaloHeight = h + k
                    mHaloRadiusR = mHaloWidth / 2
                }
            } else {
                mAlpha = dSize.toInt()
                paintHalo.alpha = mAlpha
            }
            invalidate()
        }
        anim.start()
    }

    // Rect пальца
    private fun getRectThumb(w: Float, h: Float): RectF {
        val xc = mOffset + mLenActive
        val yc = mCenterY
        val x1 = xc - w / 2
        val y1 = yc - h / 2
        val x2 = xc + w / 2
        val y2 = yc + h / 2
        val rect = RectF(x1, y1, x2, y2)
        return rect
    }

    // Палец с картинкой
    private fun drawThumbIcon(canvas: Canvas) {
        val drawable = ContextCompat.getDrawable(context, mThumbIcon) as Drawable

        // Установить цвет
        if (mIconColor != -1) {
            DrawableCompat.setTint(drawable, mIconColor)
        }

        val xc = mOffset + mLenActive
        val yc = mCenterY
        val w = mIconSize
        val h = mIconSize

        val x1 = xc - w / 2
        val x2 = x1 + w
        val y1 = yc - h / 2
        val y2 = y1 + h

        drawable.setBounds(x1.toInt(), y1.toInt(), x2.toInt(), y2.toInt())
        drawable.draw(canvas)
    }

    // Волна
    private fun drawWave(canvas: Canvas) {
        path.reset()

        val r = mTrackRadius    // Радиус точки
        val lenInd = mTrackMax  // Длина индикатора
        val lenValue = mLenActive - mThumbX - r

        val period = lenValue / mWaveCount
        val dx = period / 4

        var dy = mSweep.toFloat() // Амплитуда

        if (mTrackStyle == 2) {
            val k = dy / lenInd
            dy -= (lenValue * k)
        }

        if (mTrackStyle == 3) {
            val k = (mLenActive / mTrackMax) * 0.1f
            dy *= k
        }

        val yc = mCenterY
        val y1 = yc
        var y2 = yc - dy
        val y3 = yc
        var y4 = yc + dy
        val y5 = yc

        val x0 = mOffset + r

        var rect = RectF()

        if (mThumbIcon != 0) {
            rect = getRectIconL()
        }

        canvas.save()
        for (i in 0..<mWaveCount) {
            val x1 = x0 + period * i
            val x2 = x1 + dx // Левый пик
            val x3 = x2 + dx // Центр
            val x4 = x3 + dx // Правый пик
            val x5 = x4 + dx // Правый край

            if (mTrackStyle == 3) {
                y2 = yc - dy * i // Левый пик
                y4 = yc + dy * i // Правый пик
            }

            path.moveTo(x1, y1)
            path.quadTo(x2, y2, x3, y3)
            canvas.drawPath(path, paintWave)

            path.moveTo(x3, y3)
            path.quadTo(x4, y4, x5, y5)

            if (mThumbIcon != 0) {
                canvas.clipRect(rect)
                canvas.drawPath(path, paintWave)
            } else
                canvas.drawPath(path, paintWave)
        }
        canvas.restore()

        // Точка
        val xc1 = x0
        canvas.drawCircle(xc1, yc, r, paintA)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val minWidth = suggestedMinimumWidth
        val minHeight = suggestedMinimumHeight

        val screen = resources.displayMetrics
        val w = screen.widthPixels  // Разрешение экрана

        val wInd = w
        val hInd =
            mHInd.toInt() + (mSweep * 0.1).toInt() + mThumbY.toInt() + (mHaloStroke * 2).toInt()

        val desiredWidth = max(minWidth, wInd)
        val desiredHeight = max(minHeight, hInd)

        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mTrackMax = w.toFloat() - (mOffset * 2).toInt()
        mCenterY = h / 2f

        mTrackTop = mCenterY - mTrackRadius
        mTrackBottom = mCenterY + mTrackRadius
        mRatio = mTrackMax / (mMax - mMin)
    }

    fun setOnChangeListener(listener: OnChangeListener){
        this.listener = listener
    }

    var valueMax: Float
        get() = mMax
        set(value) {
            mMax = value
        }

    var value: Float
        get() = mValue
        set(value) {
            controlMax(value)
            if (mChangeColor)
                setColorTrack(value)
        }

    private fun controlMax(value: Float) {
        if (value > mMax) mValue = mMax
        else mValue = value
        invalidate()
    }

}