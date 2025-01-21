package com.caneryilmaz.apps.luckywheel.ui

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.FloatRange
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isGone
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.toBitmap
import com.caneryilmaz.apps.luckywheel.R
import com.caneryilmaz.apps.luckywheel.constant.ArrowPosition
import com.caneryilmaz.apps.luckywheel.constant.RotationDirection
import com.caneryilmaz.apps.luckywheel.constant.RotationSpeed
import com.caneryilmaz.apps.luckywheel.constant.RotationStatus
import com.caneryilmaz.apps.luckywheel.constant.TextOrientation
import com.caneryilmaz.apps.luckywheel.data.WheelData
import com.caneryilmaz.apps.luckywheel.listener.RotationCompleteListener
import com.caneryilmaz.apps.luckywheel.listener.RotationStatusListener
import com.caneryilmaz.apps.luckywheel.listener.WheelViewListener
import kotlin.math.abs


class LuckyWheelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), View.OnTouchListener, WheelViewListener {

    private var wheelView: WheelView
    private var wheelTopArrow: AppCompatImageView
    private var wheelCenterArrow: AppCompatImageView
    private var wheelCenterImage: AppCompatImageView
    private var wheelCenterTextView: AppCompatTextView

    private var arrowPosition: ArrowPosition = ArrowPosition.TOP

    private var arrowAnimStatus: Boolean = true
    private var arrowLeftSwingAnimator: ObjectAnimator? = null
    private var arrowRightSwingAnimator: ObjectAnimator? = null
    private var arrowSwingDistance: Float = 10F
    private var arrowSwingDuration: Int = 50
    private var arrowSwingSlowdownMultiplier: Float = 0.1F

    private var rotationStatus: RotationStatus = RotationStatus.IDLE

    private var rotationViaSwipe: Boolean = false

    private var target: Int = 0
    private var rotateRandomTarget: Boolean = false
    private var randomTargets: IntArray = intArrayOf()

    private var swipeDistance: Int = 100

    private var swipeX1: Float = 0F
    private var swipeX2: Float = 0F
    private var swipeY1: Float = 0F
    private var swipeY2: Float = 0F
    private var swipeDx: Float = 0F
    private var swipeDy: Float = 0F

    private var rotationCompleteListener: RotationCompleteListener? = null
    private var rotationStatusListener: RotationStatusListener? = null

    init {
        inflate(context, R.layout.lucky_wheel_layout, this)

        wheelTopArrow = findViewById(R.id.ivTopArrow)
        wheelCenterArrow = findViewById(R.id.ivCenterArrow)
        wheelCenterImage = findViewById(R.id.ivCenterImage)
        wheelCenterTextView = findViewById(R.id.ivCenterText)
        wheelView = findViewById(R.id.wheelView)

        wheelView.setOnTouchListener(this)

        setAttrsField(attrs)

        setWheelViewListener()
    }

    private fun setAttrsField(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LuckyWheelView)

        typedArray.getInt(R.styleable.LuckyWheelView_arrowPosition, 1).let { arrowPosition ->
            when (arrowPosition) {
                2 -> {
                    setArrowPosition(arrowPosition = ArrowPosition.CENTER)
                }
                else -> {
                    setArrowPosition(arrowPosition = ArrowPosition.TOP)
                }
            }
        }

        typedArray.getInt(R.styleable.LuckyWheelView_arrowSwingDuration, 50).let { arrowSwingDuration ->
            setArrowSwingDuration(arrowSwingDuration = arrowSwingDuration)
        }

        typedArray.getFloat(R.styleable.LuckyWheelView_arrowSwingDistance, 10F).let { arrowSwingDistance ->
            setArrowSwingDistance(arrowSwingDistance = arrowSwingDistance)
        }

        typedArray.getFloat(R.styleable.LuckyWheelView_arrowSwingSlowdownMultiplier, 0.1F).let { arrowSwingSlowdownMultiplier ->
            setArrowSwingSlowdownMultiplier(arrowSwingSlowdownMultiplier = arrowSwingSlowdownMultiplier)
        }

        typedArray.getBoolean(R.styleable.LuckyWheelView_arrowAnimationEnable, true).let { arrowAnimationEnable ->
            setArrowAnimationStatus(arrowAnimStatus = arrowAnimationEnable)
        }

        typedArray.getDrawable(R.styleable.LuckyWheelView_wheelTopArrow)?.let { drawable ->
            setWheelTopArrow(wheelArrowDrawable = drawable)
        }

        typedArray.getDimension(R.styleable.LuckyWheelView_wheelTopArrowWidth, resources.getDimension(R.dimen.dp48)).let { arrowWidth ->
            setWheelTopArrowWidth(width = arrowWidth)
        }

        typedArray.getDimension(R.styleable.LuckyWheelView_wheelTopArrowHeight, resources.getDimension(R.dimen.dp48)).let { arrowHeight ->
            setWheelTopArrowHeight(height = arrowHeight)
        }

        typedArray.getColor(R.styleable.LuckyWheelView_wheelTopArrowColor, Color.TRANSPARENT).let { color ->
            setWheelTopArrowColor(wheelTopArrowColor = color)
        }

        typedArray.getDimension(R.styleable.LuckyWheelView_wheelTopArrowMargin, resources.getDimension(R.dimen.dp0)).let { margin ->
            setWheelTopArrowMarginBottom(margin = margin)
        }

        typedArray.getDrawable(R.styleable.LuckyWheelView_wheelCenterImage)?.let { drawable ->
            setWheelCenterImage(wheelCenterImageDrawable = drawable)
        }

        typedArray.getDimension(R.styleable.LuckyWheelView_wheelCenterImageWidth, resources.getDimension(R.dimen.dp30)).let { arrowWidth ->
            setWheelCenterImageWidth(width = arrowWidth)
        }

        typedArray.getDimension(R.styleable.LuckyWheelView_wheelCenterImageHeight, resources.getDimension(R.dimen.dp30)).let { arrowHeight ->
            setWheelCenterImageHeight(height = arrowHeight)
        }

        typedArray.getDrawable(R.styleable.LuckyWheelView_wheelCenterArrow)?.let { drawable ->
            setWheelCenterArrow(wheelArrowDrawable = drawable)
        }

        typedArray.getDimension(R.styleable.LuckyWheelView_wheelCenterArrowWidth, resources.getDimension(R.dimen.dp30)).let { arrowWidth ->
            setWheelCenterArrowWidth(width = arrowWidth)
        }

        typedArray.getDimension(R.styleable.LuckyWheelView_wheelCenterArrowHeight, resources.getDimension(R.dimen.dp30)).let { arrowHeight ->
            setWheelCenterArrowHeight(height = arrowHeight)
        }

        typedArray.getColor(R.styleable.LuckyWheelView_wheelCenterArrowColor, Color.TRANSPARENT).let { color ->
            setWheelCenterArrowColor(wheelCenterArrowColor = color)
        }

        typedArray.getDimension(R.styleable.LuckyWheelView_wheelCenterArrowMarginTop, 0F).let { marginTop ->
            setWheelCenterArrowMarginTop(marginTop = marginTop)
        }

        typedArray.getDimension(R.styleable.LuckyWheelView_wheelCenterArrowMarginBottom, 0F).let { marginBottom ->
            setWheelCenterArrowMarginBottom(marginBottom = marginBottom)
        }

        typedArray.getString(R.styleable.LuckyWheelView_wheelCenterText)?.let { centerText ->
            setWheelCenterText(wheelCenterText = centerText)
        }

        typedArray.getColor(R.styleable.LuckyWheelView_wheelCenterTextColor, Color.BLACK).let { color ->
            setWheelCenterTextColor(wheelCenterTextColor = intArrayOf(color))
        }

        typedArray.getDimension(R.styleable.LuckyWheelView_wheelCenterTextSize, resources.getDimension(R.dimen.sp16)).let { textSize ->
            setWheelCenterTextSize(wheelCenterTextSize = textSize)
        }

        typedArray.getFont(R.styleable.LuckyWheelView_wheelCenterTextFont)?.let { font ->
            setWheelCenterTextFont(typeface = font)
        }

        typedArray.getInt(R.styleable.LuckyWheelView_rotateDirection, 1).let { rotationDirection ->
            when (rotationDirection) {
                2 -> {
                    setRotateDirection(rotationDirection = RotationDirection.COUNTER_CLOCKWISE)
                }
                else -> {
                    setRotateDirection(rotationDirection = RotationDirection.CLOCKWISE)
                }
            }
        }

        typedArray.getBoolean(R.styleable.LuckyWheelView_rotationViaSwipe, false).let { rotationViaSwipeEnable ->
            setRotationViaSwipe(rotationViaSwipe = rotationViaSwipeEnable)
        }

        typedArray.getInt(R.styleable.LuckyWheelView_swipeDistance, 100).let { swipeDistance ->
            setSwipeDistance(swipeDistance = swipeDistance)
        }

        typedArray.getBoolean(R.styleable.LuckyWheelView_stopCenterOfItem, false).let { stopCenterOfItemEnable ->
            stopCenterOfItem(stopCenterOfItem = stopCenterOfItemEnable)
        }

        typedArray.getInt(R.styleable.LuckyWheelView_rotateTime, 5000).let { rotateTime ->
            setRotateTime(rotateTime = rotateTime.toLong())
        }

        typedArray.getInt(R.styleable.LuckyWheelView_rotateSpeed, 10).let { rotateSpeed ->
            when (rotateSpeed) {
                15 -> {
                    setRotateSpeed(rotateSpeed = RotationSpeed.FAST)
                }
                5 -> {
                    setRotateSpeed(rotateSpeed = RotationSpeed.SLOW)
                }
                else -> {
                    setRotateSpeed(rotateSpeed = RotationSpeed.NORMAL)
                }
            }
        }

        typedArray.getFloat(R.styleable.LuckyWheelView_rotateSpeedMultiplier, 1F).let { rotateSpeedMultiplier ->
            setRotateSpeedMultiplier(rotateSpeedMultiplier = rotateSpeedMultiplier)
        }

        typedArray.getBoolean(R.styleable.LuckyWheelView_drawWheelStroke, false).let { drawWheelStroke ->
            drawWheelStroke(drawWheelStroke = drawWheelStroke)
        }

        typedArray.getColor(R.styleable.LuckyWheelView_wheelStrokeColor, Color.BLACK).let { wheelStrokeColor ->
            setWheelStrokeColor(wheelStrokeColor = intArrayOf(wheelStrokeColor))
        }

        typedArray.getDimensionPixelSize(R.styleable.LuckyWheelView_wheelStrokeThickness, resources.getDimensionPixelSize(R.dimen.dp4)).let { wheelStrokeThickness ->
            setWheelStrokeThickness(wheelStrokeThickness = wheelStrokeThickness.toFloat())
        }

        typedArray.getBoolean(R.styleable.LuckyWheelView_drawItemSeparator, false).let { drawItemSeparator ->
            drawItemSeparator(drawItemSeparator = drawItemSeparator)
        }

        typedArray.getColor(R.styleable.LuckyWheelView_wheelItemSeparatorColor, Color.BLACK).let { wheelItemSeparatorColor ->
            setWheelItemSeparatorColor(wheelItemSeparatorColor = intArrayOf(wheelItemSeparatorColor))
        }

        typedArray.getDimensionPixelSize(R.styleable.LuckyWheelView_itemSeparatorThickness, resources.getDimensionPixelSize(R.dimen.dp2)).let { itemSeparatorThickness ->
            setItemSeparatorThickness(itemSeparatorThickness = itemSeparatorThickness.toFloat())
        }

        typedArray.getBoolean(R.styleable.LuckyWheelView_drawCenterPoint, false).let { drawCenterPoint ->
            drawCenterPoint(drawCenterPoint = drawCenterPoint)
        }

        typedArray.getColor(R.styleable.LuckyWheelView_centerPointColor, Color.WHITE).let { centerPointColor ->
            setCenterPointColor(centerPointColor = centerPointColor)
        }

        typedArray.getDimensionPixelSize(R.styleable.LuckyWheelView_centerPointRadius, resources.getDimensionPixelSize(R.dimen.dp20)).let { centerPointRadius ->
            setCenterPointRadius(centerPointRadius = centerPointRadius.toFloat())
        }

        typedArray.getBoolean(R.styleable.LuckyWheelView_drawCornerPoints, false).let { drawCornerPoints ->
            drawCornerPoints(drawCornerPoints = drawCornerPoints)
        }

        typedArray.getInt(R.styleable.LuckyWheelView_cornerPointsEachSlice, 1).let { cornerPointsEachSlice ->
            setCornerPointsEachSlice(cornerPointsEachSlice = cornerPointsEachSlice)
        }

        typedArray.getColor(R.styleable.LuckyWheelView_cornerPointsColor, -1).let { cornerPointsColor ->
            if (cornerPointsColor == -1) {
                setCornerPointsColor(cornerPointsColor = intArrayOf())
            } else {
                setCornerPointsColor(cornerPointsColor = intArrayOf(cornerPointsColor))
            }
        }

        typedArray.getBoolean(R.styleable.LuckyWheelView_useRandomCornerPointsColor, true).let { useRandomCornerPointsColor ->
            setUseRandomCornerPointsColor(useRandomCornerPointsColor = useRandomCornerPointsColor)
        }

        typedArray.getBoolean(R.styleable.LuckyWheelView_useCornerPointsGlowEffect, true).let { useCornerPointsGlowEffect ->
            setUseCornerPointsGlowEffect(useCornerPointsGlowEffect = useCornerPointsGlowEffect)
        }

        typedArray.getInt(R.styleable.LuckyWheelView_cornerPointsColorChangeSpeedMs, 500).let { cornerPointsColorChangeSpeedMs ->
            setCornerPointsColorChangeSpeedMs(cornerPointsColorChangeSpeedMs = cornerPointsColorChangeSpeedMs)
        }

        typedArray.getDimensionPixelSize(R.styleable.LuckyWheelView_cornerPointsRadius, resources.getDimensionPixelSize(R.dimen.dp4)).let { cornerPointsRadius ->
            setCornerPointsRadius(cornerPointsRadius = cornerPointsRadius.toFloat())
        }

        typedArray.getInt(R.styleable.LuckyWheelView_textOrientation, 1).let { textOrientation ->
            when (textOrientation) {
                2 -> {
                    setTextOrientation(textOrientation = TextOrientation.VERTICAL)
                }
                else -> {
                    setTextOrientation(textOrientation = TextOrientation.HORIZONTAL)
                }
            }
        }

        typedArray.getDimensionPixelSize(R.styleable.LuckyWheelView_textPadding, resources.getDimensionPixelSize(R.dimen.dp4)).let { textPadding ->
            setTextPadding(textPadding = textPadding.toFloat())
        }

        typedArray.getDimensionPixelSize(R.styleable.LuckyWheelView_textSize, resources.getDimensionPixelSize(R.dimen.sp16)).let { textSize ->
            setTextSize(textSize = textSize)
        }

        typedArray.getFloat(R.styleable.LuckyWheelView_letterSpacing, 0.1F).let { letterSpacing ->
            setTextLetterSpacing(letterSpacing = letterSpacing)
        }

        typedArray.getFont(R.styleable.LuckyWheelView_textFont)?.let { font ->
            setTextFont(typeface = font)
        }

        typedArray.getFloat(R.styleable.LuckyWheelView_iconSizeMultiplier, 1.0F).let { sizeMultiplier ->
            setIconSizeMultiplier(sizeMultiplier = sizeMultiplier)
        }

        typedArray.getFloat(R.styleable.LuckyWheelView_iconPosition, 0.5F).let { iconPositionFraction ->
            setIconPosition(iconPositionFraction = iconPositionFraction)
        }

        typedArray.recycle()
    }


    private fun Float.getDpValue(): Int {
        return (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
    }

    private fun Float.getDpValueFloat(): Float {
        return (this * Resources.getSystem().displayMetrics.density + 0.5f)
    }

    /**
     * @param arrowPosition is wheel arrow position [ArrowPosition.TOP] or [ArrowPosition.CENTER]
     */
    fun setArrowPosition(arrowPosition: ArrowPosition) {
        this.arrowPosition = arrowPosition

        when (arrowPosition) {
            ArrowPosition.TOP -> {
                wheelCenterArrow.visibility = GONE

                wheelTopArrow.visibility = VISIBLE
            }
            ArrowPosition.CENTER -> {
                wheelCenterArrow.visibility = VISIBLE

                wheelTopArrow.visibility = GONE
            }
        }
    }

    /**
     * @param arrowAnimStatus is enable or disable arrow swing animation,
     * default value `true`
     */
    fun setArrowAnimationStatus(arrowAnimStatus: Boolean) {
        this.arrowAnimStatus = arrowAnimStatus
    }

    /**
     * @param arrowSwingDuration is single arrow swing animation duration, default value `50ms`
     */
    fun setArrowSwingDuration(arrowSwingDuration: Int) {
        this.arrowSwingDuration = arrowSwingDuration
    }

    /**
     * @param arrowSwingDistance is arrow right and left swing distance, default value `10F`
     */
    fun setArrowSwingDistance(arrowSwingDistance: Float) {
        this.arrowSwingDistance = arrowSwingDistance
    }

    /**
     * @param arrowSwingSlowdownMultiplier
     * * is arrow swing animation duration slowdown speed
     * - The smaller the value, the later it slows down
     * - The larger the value, the faster it slows down
     * - default value `0.1F`
     */
    fun setArrowSwingSlowdownMultiplier(arrowSwingSlowdownMultiplier: Float) {
        this.arrowSwingSlowdownMultiplier = arrowSwingSlowdownMultiplier
    }


    fun setWheelTopArrow(wheelArrowId: Int, width: Float, height: Float, wheelTopArrowColor: Int, margin: Float) {
        setWheelTopArrow(wheelArrowId = wheelArrowId)
        setWheelTopArrowSize(width = width, height = height)
        setWheelTopArrowColor(wheelTopArrowColor = wheelTopArrowColor)
        setWheelTopArrowMargin(margin = margin)
    }

    fun setWheelTopArrow(wheelArrowDrawable: Drawable, width: Float, height: Float, wheelTopArrowColor: Int, margin: Float) {
        setWheelTopArrow(wheelArrowDrawable = wheelArrowDrawable)
        setWheelTopArrowSize(width = width, height = height)
        setWheelTopArrowColor(wheelTopArrowColor = wheelTopArrowColor)
        setWheelTopArrowMargin(margin = margin)
    }

    /**
     * @param wheelArrowId is wheel top arrow drawable resource id
     */
    fun setWheelTopArrow(wheelArrowId: Int) {
        wheelTopArrow.setImageResource(wheelArrowId)
    }

    /**
     * @param wheelArrowDrawable is wheel top arrow drawable resource
     */
    fun setWheelTopArrow(wheelArrowDrawable: Drawable) {
        wheelTopArrow.setImageDrawable(wheelArrowDrawable)
    }

    /**
     * @param width is width of wheel top arrow image, default value `48dp`
     * @param height is height of wheel top arrow image, default value `48dp`
     */
    fun setWheelTopArrowSize(width: Float, height: Float) {
        setWheelTopArrowWidth(width.getDpValueFloat())
        setWheelTopArrowHeight(height.getDpValueFloat())
    }

    /**
     * @param width is width of wheel top arrow image, default value `48dp`
     */
    private fun setWheelTopArrowWidth(width: Float) {
        wheelTopArrow.layoutParams.width = width.toInt()
    }

    /**
     * @param height is height of wheel top arrow image, default value `48dp`
     */
    private fun setWheelTopArrowHeight(height: Float) {
        wheelTopArrow.layoutParams.height = height.toInt()
    }

    /**
     * @param wheelTopArrowColor is wheel top arrow tint color
     */
    fun setWheelTopArrowColor(wheelTopArrowColor: Int) {
        wheelTopArrow.setColorFilter(wheelTopArrowColor)
    }

    /**
     * @param margin
     * * is wheel top arrow margin from bottom
     * - if value is positive then arrow moving up
     * - if value is negative then arrow moving down
     * - default value `0dp`
     */
    fun setWheelTopArrowMargin(margin: Float) {
        val marginDp = margin.getDpValue()

        val params: MarginLayoutParams = wheelTopArrow.layoutParams as MarginLayoutParams
        params.bottomMargin = -marginDp
    }

    /**
     * @param margin
     * * is wheel top arrow margin from bottom
     * - if value is positive then arrow moving up
     * - if value is negative then arrow moving down
     * - default value `0dp`
     * - this function for attrs calling
     */
    private fun setWheelTopArrowMarginBottom(margin: Float) {
        val params: MarginLayoutParams = wheelTopArrow.layoutParams as MarginLayoutParams
        params.bottomMargin = -margin.toInt()
    }


    fun setWheelCenterImage(wheelCenterImageId: Int, width: Float, height: Float) {
        setWheelCenterImage(wheelCenterImageId = wheelCenterImageId)
        setWheelCenterImageSize(width = width, height = height)
    }

    fun setWheelCenterImage(wheelCenterImageDrawable: Drawable, width: Float, height: Float) {
        setWheelCenterImage(wheelCenterImageDrawable = wheelCenterImageDrawable)
        setWheelCenterImageSize(width = width, height = height)
    }

    /**
     * @param wheelCenterImageId is wheel center image drawable resource id
     */
    fun setWheelCenterImage(wheelCenterImageId: Int) {
        wheelCenterImage.isGone = false
        wheelCenterImage.setImageResource(wheelCenterImageId)
    }

    /**
     * @param wheelCenterImageDrawable is wheel center image drawable resource
     */
    fun setWheelCenterImage(wheelCenterImageDrawable: Drawable) {
        wheelCenterImage.isGone = false
        wheelCenterImage.setImageDrawable(wheelCenterImageDrawable)
    }

    /**
     * @param width is width of wheel center image, default value `30dp`
     * @param height is height of wheel center image, default value `30dp`
     */
    fun setWheelCenterImageSize(width: Float, height: Float) {
        setWheelCenterImageWidth(width.getDpValueFloat())
        setWheelCenterImageHeight(height.getDpValueFloat())
    }

    /**
     * @param width is width of wheel center image, default value `30dp`
     */
    private fun setWheelCenterImageWidth(width: Float) {
        wheelCenterImage.layoutParams.width = width.toInt()
    }

    /**
     * @param height is height of wheel center image, default value `30dp`
     */
    private fun setWheelCenterImageHeight(height: Float) {
        wheelCenterImage.layoutParams.height = height.toInt()
    }


    fun setWheelCenterArrow(wheelArrowId: Int, width: Float, height: Float, wheelCenterArrowColor: Int, marginTop: Float, marginBottom: Float) {
        setWheelCenterArrow(wheelArrowId = wheelArrowId)
        setWheelCenterArrowSize(width = width, height = height)
        setWheelCenterArrowColor(wheelCenterArrowColor = wheelCenterArrowColor)
        setWheelCenterArrowMargin(marginTop = marginTop, marginBottom = marginBottom)
    }

    fun setWheelCenterArrow(wheelArrowDrawable: Drawable, width: Float, height: Float, wheelCenterArrowColor: Int, marginTop: Float, marginBottom: Float) {
        setWheelCenterArrow(wheelArrowDrawable = wheelArrowDrawable)
        setWheelCenterArrowSize(width = width, height = height)
        setWheelCenterArrowColor(wheelCenterArrowColor = wheelCenterArrowColor)
        setWheelCenterArrowMargin(marginTop = marginTop, marginBottom = marginBottom)
    }

    /**
     * @param wheelArrowId is wheel center arrow drawable resource id
     */
    fun setWheelCenterArrow(wheelArrowId: Int) {
        wheelCenterArrow.setImageResource(wheelArrowId)
    }

    /**
     * @param wheelArrowDrawable is wheel top arrow drawable resource
     */
    fun setWheelCenterArrow(wheelArrowDrawable: Drawable) {
        wheelCenterArrow.setImageDrawable(wheelArrowDrawable)
    }

    /**
     * @param width is width of wheel center arrow image, default value `30dp`
     * @param height is height of wheel center arrow image, default value `30dp`
     */
    fun setWheelCenterArrowSize(width: Float, height: Float) {
        setWheelCenterArrowWidth(width.getDpValueFloat())
        setWheelCenterArrowHeight(height.getDpValueFloat())
    }

    /**
     * @param width is width of wheel center arrow image, default value `30dp`
     */
    private fun setWheelCenterArrowWidth(width: Float) {
        wheelCenterArrow.layoutParams.width = width.toInt()
    }

    /**
     * @param height is height of wheel center arrow image, default value `30dp`
     */
    private fun setWheelCenterArrowHeight(height: Float) {
        wheelCenterArrow.layoutParams.height = height.toInt()
    }

    /**
     * @param wheelCenterArrowColor is wheel center arrow tint color
     */
    fun setWheelCenterArrowColor(wheelCenterArrowColor: Int) {
        wheelCenterArrow.setColorFilter(wheelCenterArrowColor)
    }

    /**
     * @param marginTop
     * * is wheel center arrow margin from top
     * - if value is positive then arrow moving down
     * - if value is negative then arrow moving up
     * - default value `0dp`
     * @param marginBottom
     * * is wheel center arrow margin from bottom
     * - if value is positive then arrow moving up
     * - if value is negative then arrow moving down
     * - default value `0dp`
     */
    fun setWheelCenterArrowMargin(marginTop: Float, marginBottom: Float) {
        val marginTopDp = marginTop.getDpValue()
        val marginBottomDp = marginBottom.getDpValue()

        val params: MarginLayoutParams = wheelCenterArrow.layoutParams as MarginLayoutParams
        params.bottomMargin = marginBottomDp
        params.topMargin = marginTopDp
    }

    /**
     * @param marginTop
     * * is wheel center arrow margin from top
     * - if value is positive then arrow moving down
     * - if value is negative then arrow moving up
     * - default value `0dp`
     * - this function for attrs calling
     */
    private fun setWheelCenterArrowMarginTop(marginTop: Float) {
        val marginTopDp = marginTop.getDpValue()

        val params: MarginLayoutParams = wheelCenterArrow.layoutParams as MarginLayoutParams
        params.topMargin = marginTopDp
    }

    /**
     * @param marginBottom
     * * is wheel center arrow margin from bottom
     * - if value is positive then arrow moving up
     * - if value is negative then arrow moving down
     * - default value `0dp`
     * - this function for attrs calling
     */
    private fun setWheelCenterArrowMarginBottom(marginBottom: Float) {
        val marginBottomDp = marginBottom.getDpValue()

        val params: MarginLayoutParams = wheelCenterArrow.layoutParams as MarginLayoutParams
        params.bottomMargin = marginBottomDp
    }

    fun setWheelCenterText(wheelCenterText: String, wheelCenterTextColor: IntArray, wheelCenterTextSize: Int, fontResourceId: Int) {
        setWheelCenterText(wheelCenterText = wheelCenterText)
        setWheelCenterTextColor(wheelCenterTextColor = wheelCenterTextColor)
        setWheelCenterTextSize(wheelCenterTextSize = wheelCenterTextSize)
        setWheelCenterTextFont(fontResourceId = fontResourceId)
    }

    fun setWheelCenterText(wheelCenterText: String, wheelCenterTextColor: IntArray, wheelCenterTextSize: Int, typeface: Typeface) {
        setWheelCenterText(wheelCenterText = wheelCenterText)
        setWheelCenterTextColor(wheelCenterTextColor = wheelCenterTextColor)
        setWheelCenterTextSize(wheelCenterTextSize = wheelCenterTextSize)
        setWheelCenterTextFont(typeface = typeface)
    }

    /**
     * @param wheelCenterText is center text value
     */
    fun setWheelCenterText(wheelCenterText: String) {
        wheelCenterTextView.visibility = VISIBLE
        wheelCenterTextView.text = wheelCenterText
    }

    /**
     * @param wheelCenterTextColor
     * * is color of center text
     * - if [wheelCenterTextColor] size = 1 then gradient text color disable and text color will be value of `wheelCenterTextColor[0]`
     * - if [wheelCenterTextColor] size > 1 then gradient text color enable
     * - if [wheelCenterTextColor] is empty then gradient text color disable and text color will be [Color.BLACK]
     */
    fun setWheelCenterTextColor(wheelCenterTextColor: IntArray) {

        val textWidth =  wheelCenterTextView.paint.measureText(wheelCenterTextView.text.toString())

        val centerTextShader = if (wheelCenterTextColor.size == 1) {
            LinearGradient(
                0F,
                0F,
                textWidth,
                wheelCenterTextView.textSize,
                intArrayOf(wheelCenterTextColor[0], wheelCenterTextColor[0]),
                null,
                Shader.TileMode.CLAMP
            )
        } else if (wheelCenterTextColor.isEmpty()) {
            LinearGradient(
                0F,
                0F,
                textWidth,
                wheelCenterTextView.textSize,
                intArrayOf(Color.BLACK, Color.BLACK),
                null,
                Shader.TileMode.CLAMP
            )
        } else {
            LinearGradient(
                0F,
                0F,
                textWidth,
                wheelCenterTextView.textSize,
                wheelCenterTextColor,
                null,
                Shader.TileMode.CLAMP
            )
        }
        wheelCenterTextView.paint.shader = centerTextShader
    }

    /**
     * @param wheelCenterTextSize is size of center text, default value `16sp`
     */
    fun setWheelCenterTextSize(wheelCenterTextSize: Int) {
        wheelCenterTextView.textSize = wheelCenterTextSize.toFloat()
    }

    /**
     * @param wheelCenterTextSize is size of center text, default value `16sp`
     */
    private fun setWheelCenterTextSize(wheelCenterTextSize: Float) {
        wheelCenterTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, wheelCenterTextSize)
    }

    /**
     * @param fontResourceId is custom font resource id of center text
     * */
    fun setWheelCenterTextFont(fontResourceId: Int) {
        val typeface = ResourcesCompat.getFont(context, fontResourceId)
        wheelCenterTextView.typeface = typeface!!
    }

    /**
     * @param typeface is custom font typeface of center text
     */
    fun setWheelCenterTextFont(typeface: Typeface) {
        wheelCenterTextView.typeface = typeface
    }


    /**
     * @param wheelData is an ArrayList of [WheelData], check info for [WheelData] description
     */
    fun setWheelData(wheelData: ArrayList<WheelData>) {
        val urlIconPairs = ArrayList<Pair<Int, WheelData>>()

        wheelData.forEachIndexed { index, item ->
            item.iconURL?.let {
                urlIconPairs.add(Pair(index, item))
            }
        }

        if (urlIconPairs.isEmpty()) {
            wheelView.setWheelData(wheelData = wheelData)
        } else {
            val itemList = ArrayList<WheelData>()
            itemList.addAll(wheelData)

            wheelView.setWheelData(wheelData = itemList)

            urlIconPairs.forEach { pair ->
                val request = ImageRequest.Builder(context)
                    .data(pair.second.iconURL)
                    .target(
                        onSuccess = { result ->
                            val newItem = WheelData(
                                text = pair.second.text,
                                textColor = pair.second.textColor,
                                backgroundColor = pair.second.backgroundColor,
                                textFontTypeface = pair.second.textFontTypeface,
                                icon = result.toBitmap()
                            )
                            itemList[pair.first] = newItem
                            wheelView.setWheelData(wheelData = itemList)
                        },
                        onError = { error ->
                            val newItem = WheelData(
                                text = pair.second.text,
                                textColor = pair.second.textColor,
                                backgroundColor = pair.second.backgroundColor,
                                textFontTypeface = pair.second.textFontTypeface
                            )
                            itemList[pair.first] = newItem
                            wheelView.setWheelData(wheelData = itemList)
                        }).build()

                context.imageLoader.enqueue(request)
            }
        }
    }


    /**
     * @param target
     * * is index of the item to win
     * - [target] must be between 0 and wheelData last index (exclusive)
     * - also if target a negative number then target throw [IllegalArgumentException]
     * - also if target bigger than given array list last index then throw [IndexOutOfBoundsException]
    */
    fun setTarget(target: Int) {
        this.target = target
    }


    fun setRotateRandomTarget(rotateRandomTarget: Boolean, randomTargets: IntArray) {
        setRotateRandomTarget(rotateRandomTarget)
        setRandomTargets(randomTargets)
    }

    /**
     * @param rotateRandomTarget is enable or disable rotate to random target, default value `false`
     */
    fun setRotateRandomTarget(rotateRandomTarget: Boolean) {
        this.rotateRandomTarget = rotateRandomTarget
    }

    /**
     * @param randomTargets is array of win index
     * - if [rotateRandomTarget] is `true` and [randomTargets] is empty then win index will be randomly between `0` and `wheelData.latsIndex`
     * - if [rotateRandomTarget] is `true` and [randomTargets] is not empty then win index will be randomly one of members of [randomTargets] array
     */
    fun setRandomTargets(randomTargets: IntArray) {
        this.randomTargets = randomTargets
    }

    /**
     * @param rotationDirection is wheel rotate direction [RotationDirection.CLOCKWISE], [RotationDirection.COUNTER_CLOCKWISE], default value [RotationDirection.CLOCKWISE]
     */
    fun setRotateDirection(rotationDirection: RotationDirection) {
        wheelView.setRotateDirection(rotationDirection = rotationDirection)
    }


    fun setRotationViaSwipe(rotationViaSwipe: Boolean, swipeDistance: Int) {
        setRotationViaSwipe(rotationViaSwipe = rotationViaSwipe)
        setSwipeDistance(swipeDistance = swipeDistance)
    }

    /**
     * @param rotationViaSwipe is enable or disable start wheel rotate via swipe down, default value `false`
     */
    fun setRotationViaSwipe(rotationViaSwipe: Boolean) {
        this.rotationViaSwipe = rotationViaSwipe
    }

    /**
     * @param swipeDistance is swipe distance to start rotate wheel, default value `100`
     */
    fun setSwipeDistance(swipeDistance: Int) {
        this.swipeDistance = swipeDistance
    }

    /**
     * @param stopCenterOfItem
     * * default value `false`
     * * if `true` the arrow points to the center of the slice
     * - if `false` the arrow points to a random point on the slice.
     */
    fun stopCenterOfItem(stopCenterOfItem: Boolean) {
        wheelView.stopCenterOfItem(stopCenterOfItem = stopCenterOfItem)
    }


    fun setRotateTime(rotateTime: Long, rotateSpeed: RotationSpeed, rotateSpeedMultiplier: Float) {
        wheelView.setRotateTime(rotateTime = rotateTime, rotateSpeed = rotateSpeed, rotateSpeedMultiplier = rotateSpeedMultiplier)
    }

    /**
     * @param rotateTime is wheel rotate duration, default value `5000ms`
     */
    fun setRotateTime(rotateTime: Long) {
        wheelView.setRotateTime(rotateTime = rotateTime)
    }

    /**
     * @param rotateSpeed is wheel rotate speed [RotationSpeed.FAST], [RotationSpeed.NORMAL] or [RotationSpeed.SLOW], default value [RotationSpeed.NORMAL]
     */
    fun setRotateSpeed(rotateSpeed: RotationSpeed) {
        wheelView.setRotateSpeed(rotateSpeed = rotateSpeed)
    }

    /**
     * @param rotateSpeedMultiplier is wheel rotate speed multiplier, default value `1F`
     */
    fun setRotateSpeedMultiplier(rotateSpeedMultiplier: Float) {
        wheelView.setRotateSpeedMultiplier(rotateSpeedMultiplier = rotateSpeedMultiplier)
    }


    fun drawWheelStroke(drawWheelStroke: Boolean, wheelStrokeColor: IntArray, wheelStrokeThickness: Float) {
        drawWheelStroke(drawWheelStroke = drawWheelStroke)
        setWheelStrokeColor(wheelStrokeColor = wheelStrokeColor)
        setWheelStrokeThickness(wheelStrokeThickness = wheelStrokeThickness)
    }

    /**
     * @param drawWheelStroke is enable or disable wheel corner stroke drawing, default value `false`
     */
    fun drawWheelStroke(drawWheelStroke: Boolean) {
        wheelView.drawWheelStroke(drawWheelStroke = drawWheelStroke)
    }

    /**
     * @param wheelStrokeColor
     * * * is color of stroke line
     *  * - if [wheelStrokeColor] size = 1 then gradient stroke color disable and stroke color will be value of `wheelStrokeColor[0]`
     *  * - if [wheelStrokeColor] size > 1 then gradient stroke color enable
     *  * - if [wheelStrokeColor] is empty then gradient stroke color disable and stroke color will be [Color.BLACK]
     */
    fun setWheelStrokeColor(wheelStrokeColor: IntArray) {
        wheelView.setWheelStrokeColor(wheelStrokeColor = wheelStrokeColor)
    }

    /**
     * @param wheelStrokeThickness is thickness of item stroke circle, default value `4dp`
     */
    fun setWheelStrokeThickness(wheelStrokeThickness: Float) {
        wheelView.setWheelStrokeThickness(wheelStrokeThickness = wheelStrokeThickness)
    }


    fun drawItemSeparator(drawItemSeparator: Boolean, wheelItemSeparatorColor: IntArray, itemSeparatorThickness: Float) {
        wheelView.drawItemSeparator(drawItemSeparator = drawItemSeparator, wheelItemSeparatorColor = wheelItemSeparatorColor, itemSeparatorThickness = itemSeparatorThickness)
    }

    /**
     * @param drawItemSeparator is enable or disable wheel item separator drawing, default value `false`
     */
    fun drawItemSeparator(drawItemSeparator: Boolean) {
        wheelView.drawItemSeparator(drawItemSeparator = drawItemSeparator)
    }

    /**
     * @param wheelItemSeparatorColor
     * * is color of item separator line
     * - if [wheelItemSeparatorColor] size = 1 then gradient separator color disable and separator color will be value of `wheelItemSeparatorColor[0]`
     * - if [wheelItemSeparatorColor] size > 1 then gradient separator color enable
     * - if [wheelItemSeparatorColor] is empty then gradient separator color disable and separator color will be [Color.BLACK]
     */
    fun setWheelItemSeparatorColor(wheelItemSeparatorColor: IntArray) {
        wheelView.setWheelItemSeparatorColor(wheelItemSeparatorColor = wheelItemSeparatorColor)
    }

    /**
     * @param itemSeparatorThickness is thickness of item separator line, default value `2dp`
     */
    fun setItemSeparatorThickness(itemSeparatorThickness: Float) {
        wheelView.setItemSeparatorThickness(itemSeparatorThickness = itemSeparatorThickness)
    }


    fun drawCenterPoint(drawCenterPoint: Boolean, centerPointColor: Int, centerPointRadius: Float) {
        wheelView.drawCenterPoint(drawCenterPoint  = drawCenterPoint, centerPointColor = centerPointColor, centerPointRadius = centerPointRadius)
    }

    /**
     * @param drawCenterPoint is enable or disable center point drawing, default value `false`
     */
    fun drawCenterPoint(drawCenterPoint: Boolean) {
        wheelView.drawCenterPoint(drawCenterPoint = drawCenterPoint)
    }

    /**
     * @param centerPointColor is color of center point, default value [Color.WHITE]
     */
    fun setCenterPointColor(centerPointColor: Int) {
        wheelView.setCenterPointColor(centerPointColor = centerPointColor)
    }

    /**
     * @param centerPointRadius is radius of center point,  default value `20dp`
     */
    fun setCenterPointRadius(centerPointRadius: Float) {
        wheelView.setCenterPointRadius(centerPointRadius = centerPointRadius)
    }


    fun drawCornerPoints(drawCornerPoints: Boolean, cornerPointsEachSlice: Int, useRandomCornerPointsColor: Boolean, useCornerPointsGlowEffect: Boolean, cornerPointsColorChangeSpeedMs: Int, cornerPointsColor: IntArray, cornerPointsRadius: Float) {
        wheelView.drawCornerPoints(drawCornerPoints, cornerPointsEachSlice, useRandomCornerPointsColor, useCornerPointsGlowEffect, cornerPointsColorChangeSpeedMs, cornerPointsColor, cornerPointsRadius)
    }

    /**
     * @param drawCornerPoints is enable or disable corner points drawing, default value `false`
     */
    fun drawCornerPoints(drawCornerPoints: Boolean) {
        wheelView.drawCornerPoints(drawCornerPoints = drawCornerPoints)
    }

    /**
     * @param cornerPointsEachSlice is count of point in a slice,  default value `1`
     */
    fun setCornerPointsEachSlice(cornerPointsEachSlice: Int) {
        wheelView.setCornerPointsEachSlice(cornerPointsEachSlice = cornerPointsEachSlice)
    }

    /**
     * @param useRandomCornerPointsColor is enable or disable random corner points colors,  default value `true`
     */
    fun setUseRandomCornerPointsColor(useRandomCornerPointsColor: Boolean) {
        wheelView.setUseRandomCornerPointsColor(useRandomCornerPointsColor = useRandomCornerPointsColor)
    }

    /**
     * @param useCornerPointsGlowEffect is enable or disable corner points glow effect, default value `true`
     */
    fun setUseCornerPointsGlowEffect(useCornerPointsGlowEffect: Boolean) {
        wheelView.setUseCornerPointsGlowEffect(useCornerPointsGlowEffect = useCornerPointsGlowEffect)
    }

    /**
     * @param cornerPointsColorChangeSpeedMs is corner points color change duration, default value `500ms`
     */
    fun setCornerPointsColorChangeSpeedMs(cornerPointsColorChangeSpeedMs: Int) {
        wheelView.setCornerPointsColorChangeSpeedMs(cornerPointsColorChangeSpeedMs = cornerPointsColorChangeSpeedMs)
    }

    /**
     * @param cornerPointsColor
     * * is colors of corner points
     * - if [cornerPointsColor] is empty and [setUseRandomCornerPointsColor] is `false` then corner colors will be randomly
     * - if [cornerPointsColor] is not empty and [setUseRandomCornerPointsColor] is `true` then corner colors will be randomly
     */
    fun setCornerPointsColor(cornerPointsColor: IntArray) {
        wheelView.setCornerPointsColor(cornerPointsColor = cornerPointsColor)
    }

    /**
     * @param cornerPointsRadius is radius of corner point, default value `4dp`
     */
    fun setCornerPointsRadius(cornerPointsRadius: Float) {
        wheelView.setCornerPointsRadius(cornerPointsRadius = cornerPointsRadius)
    }


    fun setWheelItemText(textOrientation: TextOrientation, textPadding: Float, textSize: Int, letterSpacing: Float, fontResourceId: Int) {
        val textSizeSp: Float = textSize * resources.displayMetrics.scaledDensity
        val typeface = ResourcesCompat.getFont(context, fontResourceId)

        wheelView.setWheelItemText(textOrientation = textOrientation, textPadding = textPadding, textSize = textSizeSp, letterSpacing =  letterSpacing, typeface = typeface!!)
    }

    fun setWheelItemText(textOrientation: TextOrientation, textPadding: Float, textSize: Int, letterSpacing: Float, typeface: Typeface) {
        val textSizeSp: Float = textSize * resources.displayMetrics.scaledDensity

        wheelView.setWheelItemText(textOrientation = textOrientation, textPadding = textPadding, textSize = textSizeSp, letterSpacing =  letterSpacing, typeface = typeface)
    }

    /**
     * @param textOrientation is text orientation of wheel items [TextOrientation.HORIZONTAL] or [TextOrientation.VERTICAL], default value [TextOrientation.HORIZONTAL]
     */
    fun setTextOrientation(textOrientation: TextOrientation) {
        wheelView.setTextOrientation(textOrientation = textOrientation)
    }

    /**
     * @param textPadding is text padding from wheel corner, default value `4dp`
     */
    fun setTextPadding(textPadding: Float) {
        wheelView.setTextPadding(textPadding = textPadding)
    }

    /**
     * @param textSize is text size of wheel items, default value `16sp`
     */
    fun setTextSize(textSize: Int) {
        wheelView.setTextSize(textSize = textSize.toFloat())
    }

    /**
     * @param letterSpacing
     * * is letter spacing of wheel items text
     * - letterSpacing must be in range `0.0F` - `1.0F`
     * - letterSpacing is not in range then letter spacing be `0.1F`
     * - default value `0.1F`
     */
    fun setTextLetterSpacing(@FloatRange(from = 0.0, to = 1.0) letterSpacing: Float) {
        if (letterSpacing in 0.0F..1.0F) {
            wheelView.setTextLetterSpacing(letterSpacing = letterSpacing)
        } else {
            wheelView.setTextLetterSpacing(letterSpacing = 1.0F)
        }
    }

    /**
     * @param fontResourceId is custom font resource id of wheel items text
     */
    fun setTextFont(fontResourceId: Int) {
        val typeface = ResourcesCompat.getFont(context, fontResourceId)
        wheelView.setTextFont(typeface = typeface!!)
    }

    /**
     * @param typeface is custom font typeface of wheel items text
     */
    fun setTextFont(typeface: Typeface) {
        wheelView.setTextFont(typeface = typeface)
    }

    /**
     * @param sizeMultiplier is item icon size multiplier value, default value `1.0F` and default icon size `36dp`
     */
    fun setIconSizeMultiplier(sizeMultiplier: Float) {
        wheelView.setIconSizeMultiplier(sizeMultiplier = sizeMultiplier)
    }

    /**
     * @param iconPositionFraction
     * * is icon vertical position fraction in wheel slice
     * - The smaller the value, the closer to the center
     * - The larger the value, the closer to the corners
     * - default value `0.5F`
     */
    fun setIconPosition(@FloatRange(from = 0.1, to = 0.9) iconPositionFraction: Float) {
        wheelView.setIconPositionFraction(iconPositionFraction = iconPositionFraction)
    }


    fun setListeners(rotationCompleteListener: RotationCompleteListener, rotationStatusListener: RotationStatusListener) {
        setRotationCompleteListener(rotationCompleteListener = rotationCompleteListener)
        setRotationStatusListener(rotationStatusListener = rotationStatusListener)
    }

    /**
     * @param rotationCompleteListener is invoke when wheel stop. return `wheelData[target]` value
     */
    fun setRotationCompleteListener(rotationCompleteListener: RotationCompleteListener) {
        this.rotationCompleteListener = rotationCompleteListener
    }

    /**
     * @param rotationStatusListener is invoke when wheel rotation status change. return current wheel [RotationStatus] value
     */
    fun setRotationStatusListener(rotationStatusListener: RotationStatusListener) {
        this.rotationStatusListener = rotationStatusListener
    }

    /**
     * this function set wheel view listener to wheel view
     * also if this function don't call then wheel view is not notify user
     * this function is not for user
     */
    private fun setWheelViewListener() {
        wheelView.setWheelViewListener(wheelViewListener = this)
    }

    /**
     * this function rotate wheel to given target or random target
     */
    fun rotateWheel() {
        if (rotationStatus == RotationStatus.IDLE || rotationStatus == RotationStatus.COMPLETED) {
            wheelView.resetWheel()

            if (arrowAnimStatus) {
                startArrowAnimation()
            }

            if (rotateRandomTarget) {
                val randomTargetFromArray = randomTargets.randomOrNull()
                if (randomTargetFromArray == null) {
                    wheelView.rotateWheelRandomTarget()
                } else {
                    wheelView.rotateWheelToTarget(target = randomTargetFromArray)
                }
            } else {
                wheelView.rotateWheelToTarget(target = target)
            }
        }
    }

    /**
     * this function start swing animation to selected arrow position
     * this function is not for user
     */
    private fun startArrowAnimation() {
        when (arrowPosition) {
            ArrowPosition.TOP -> {
                arrowRightSwingAnimator = ObjectAnimator.ofFloat(wheelTopArrow, "rotation", -arrowSwingDistance, arrowSwingDistance)
                arrowLeftSwingAnimator = ObjectAnimator.ofFloat(wheelTopArrow, "rotation", -arrowSwingDistance, arrowSwingDistance)
            }
            ArrowPosition.CENTER -> {
                arrowRightSwingAnimator = ObjectAnimator.ofFloat(wheelCenterArrow, "rotation", -arrowSwingDistance, arrowSwingDistance)
                arrowLeftSwingAnimator = ObjectAnimator.ofFloat(wheelCenterArrow, "rotation", -arrowSwingDistance, arrowSwingDistance)
            }
        }

        startRightSwing(arrowSwingDuration.toLong())
    }

    /**
     * this function start swing left animation to selected arrow position
     * this function is not for user
     */
    private fun startLeftSwing(duration: Long) {
        arrowLeftSwingAnimator!!.removeAllListeners()
        arrowLeftSwingAnimator!!.duration = duration
        arrowLeftSwingAnimator!!.interpolator = null
        arrowLeftSwingAnimator!!.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                if (rotationStatus == RotationStatus.ROTATING) {
                    val slowdownDuration = ((duration * arrowSwingSlowdownMultiplier) + duration).toLong()
                    startRightSwing(slowdownDuration)
                }
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
        arrowLeftSwingAnimator!!.start()
    }

    /**
     * this function start swing right animation to selected arrow position
     * this function is not for user
     */
    private fun startRightSwing(duration: Long) {
        arrowRightSwingAnimator!!.removeAllListeners()
        arrowRightSwingAnimator!!.duration = duration
        arrowRightSwingAnimator!!.interpolator = null
        arrowRightSwingAnimator!!.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                if (rotationStatus == RotationStatus.ROTATING) {
                    val slowdownDuration = ((duration * arrowSwingSlowdownMultiplier) + duration).toLong()
                    startLeftSwing(slowdownDuration)
                }
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
        arrowRightSwingAnimator!!.start()
    }

    /**
     * this function clear swing animation from arrows
     * also resets arrows position
     * this function is not for user
     */
    private fun clearArrowAnimation() {
        arrowRightSwingAnimator!!.cancel()
        arrowLeftSwingAnimator!!.cancel()

        val arrowCenterPositionAnimator = when (arrowPosition) {
            ArrowPosition.TOP -> {
                ObjectAnimator.ofFloat(wheelTopArrow, "rotation", 0F, 0F)
            }
            ArrowPosition.CENTER -> {
                ObjectAnimator.ofFloat(wheelCenterArrow, "rotation", 0F, 0F)
            }
        }
        arrowCenterPositionAnimator.duration = 10
        arrowCenterPositionAnimator.start()
        arrowCenterPositionAnimator.cancel()
        arrowCenterPositionAnimator.removeAllListeners()
    }

    override fun onRotationComplete(wheelData: WheelData) {
        rotationCompleteListener?.onRotationComplete(wheelData = wheelData)

        clearArrowAnimation()
    }

    override fun onRotationStatus(rotationStatus: RotationStatus) {
        this.rotationStatus = rotationStatus

        rotationStatusListener?.onRotationStatus(rotationStatus = rotationStatus)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        val canRotate = when (rotationStatus) {
            RotationStatus.IDLE -> {
                true
            }
            RotationStatus.ROTATING -> {
                false
            }
            RotationStatus.COMPLETED -> {
                true
            }
            RotationStatus.CANCELED -> {
                true
            }
        }

        if (rotationViaSwipe && canRotate) {
            event?.let {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        swipeX1 = it.x
                        swipeY1 = it.y
                    }
                    MotionEvent.ACTION_UP -> {
                        swipeX2 = it.x
                        swipeY2 = it.y

                        swipeDx = swipeX2 - swipeX1
                        swipeDy = swipeY2 - swipeY1

                        if (abs(swipeDx) > abs(swipeDy)) {

                            if (swipeDx < 0 && abs(swipeDx) > swipeDistance) {
                                rotateWheel()
                            }
                        } else {
                            if (swipeDy > 0 && abs(swipeDy) > swipeDistance) {
                                rotateWheel()
                            }
                        }
                    }
                }

            }
        }

        return true
    }
}