package com.caneryilmaz.apps.luckywheel.ui

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.annotation.RequiresPermission
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isGone
import com.caneryilmaz.apps.luckywheel.R
import com.caneryilmaz.apps.luckywheel.constant.ArrowPosition
import com.caneryilmaz.apps.luckywheel.constant.RotationSpeed
import com.caneryilmaz.apps.luckywheel.constant.RotationStatus
import com.caneryilmaz.apps.luckywheel.constant.TextOrientation
import com.caneryilmaz.apps.luckywheel.data.WheelData
import com.caneryilmaz.apps.luckywheel.listener.RotationStatusListener
import com.caneryilmaz.apps.luckywheel.listener.TargetReachListener
import com.caneryilmaz.apps.luckywheel.listener.WheelViewListener
import kotlin.math.abs


class LuckyWheelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), View.OnTouchListener, WheelViewListener {

    private var rootLayout: RelativeLayout
    private var wheelView: WheelView
    private var wheelTopArrow: AppCompatImageView
    private var wheelCenterArrow: AppCompatImageView
    private var wheelCenterImage: AppCompatImageView
    private var wheelCenterTextView: AppCompatTextView

    private var arrowPosition: Int = ArrowPosition.TOP

    private var arrowAnimStatus: Boolean = true
    private var arrowLeftSwingAnimator: ObjectAnimator? = null
    private var arrowRightSwingAnimator: ObjectAnimator? = null
    private var arrowSwingDistance: Int = 10
    private var arrowSwingDuration: Int = 50
    private var arrowSwingSlowdownMultiplier: Float = 0.1F

    private var rotationStatus: Int = RotationStatus.IDLE

    private var rotationViaSwipe: Boolean = false

    private var target: Int = 0
    private var rotateRandomTarget: Boolean = false

    private var swipeDistance: Int = 100

    private var swipeX1: Float = 0F
    private var swipeX2: Float = 0F
    private var swipeY1: Float = 0F
    private var swipeY2: Float = 0F
    private var swipeDx: Float = 0F
    private var swipeDy: Float = 0F

    private var targetReachListener: TargetReachListener? = null
    private var rotationStatusListener: RotationStatusListener? = null

    private var vibrationEnabled: Boolean = false
    private var vibrator: Vibrator? = null
    /**
     * Start with delay 200 milliseconds
     * Vibrate for 500 milliseconds
     * Sleep for 250 milliseconds
     */
    private var vibratePattern: LongArray = longArrayOf(200, 500, 250)

    init {
        inflate(context, R.layout.lucky_wheel_layout, this)

        rootLayout = findViewById(R.id.rootLayout)
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

        typedArray.getColor(R.styleable.LuckyWheelView_rootLayoutBackgroundColor, Color.TRANSPARENT).let { color ->
            setRootLayoutBackgroundColor(rootLayoutColor = color)
        }

        typedArray.getDrawable(R.styleable.LuckyWheelView_rootLayoutBackgroundDrawable)?.let { drawable ->
            setRootLayoutBackgroundDrawable(rootLayoutDrawable = drawable)
        }

        typedArray.getDimension(R.styleable.LuckyWheelView_rootLayoutPadding, 5.getDpValue()).let { rootPadding ->

            val rootLayoutPaddingLeft = typedArray.getDimension(R.styleable.LuckyWheelView_rootLayoutPaddingLeft, rootPadding).let { rootPaddingLeft ->
                if (rootPaddingLeft != rootPadding) {
                    rootPaddingLeft
                } else {
                    rootPadding
                }
            }
            val rootLayoutPaddingRight = typedArray.getDimension(R.styleable.LuckyWheelView_rootLayoutPaddingRight, rootPadding).let { rootPaddingRight ->
                if (rootPaddingRight != rootPadding) {
                    rootPaddingRight
                } else {
                    rootPadding
                }
            }
            val rootLayoutPaddingTop = typedArray.getDimension(R.styleable.LuckyWheelView_rootLayoutPaddingTop, rootPadding).let { rootPaddingTop ->
                if (rootPaddingTop != rootPadding) {
                    rootPaddingTop
                } else {
                    rootPadding
                }
            }
            val rootLayoutPaddingBottom = typedArray.getDimension(R.styleable.LuckyWheelView_rootLayoutPaddingBottom, rootPadding).let { rootPaddingBottom ->
                if (rootPaddingBottom != rootPadding) {
                    rootPaddingBottom
                } else {
                    rootPadding
                }
            }

            setRootLayoutPadding(
                left = rootLayoutPaddingLeft,
                top = rootLayoutPaddingTop,
                right = rootLayoutPaddingRight,
                bottom = rootLayoutPaddingBottom
            )
        }

        typedArray.getInt(R.styleable.LuckyWheelView_arrowPosition, 1).let { arrowPosition ->
            setArrowPosition(arrowPosition = arrowPosition)
        }

        typedArray.getInt(R.styleable.LuckyWheelView_arrowSwingDuration, 50).let { arrowSwingDuration ->
            setArrowSwingDuration(arrowSwingDuration = arrowSwingDuration)
        }

        typedArray.getInt(R.styleable.LuckyWheelView_arrowSwingDistance, 10).let { arrowSwingDistance ->
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

        typedArray.getDimension(R.styleable.LuckyWheelView_wheelTopArrowWidth, 48.getDpValue()).let { arrowWidth ->
            setWheelTopArrowWidth(width = arrowWidth)
        }

        typedArray.getDimension(R.styleable.LuckyWheelView_wheelTopArrowHeight, 48.getDpValue()).let { arrowHeight ->
            setWheelTopArrowHeight(height = arrowHeight)
        }

        typedArray.getColor(R.styleable.LuckyWheelView_wheelTopArrowColor, Color.TRANSPARENT).let { color ->
            setWheelTopArrowColor(wheelTopArrowColor = color)
        }

        typedArray.getDimension(R.styleable.LuckyWheelView_wheelTopArrowMargin, 0.getDpValue()).let { margin ->
            setWheelTopArrowMarginBottom(margin = margin)
        }

        typedArray.getDrawable(R.styleable.LuckyWheelView_wheelCenterImage)?.let { drawable ->
            setWheelCenterImage(wheelCenterImageDrawable = drawable)
        }

        typedArray.getDimension(R.styleable.LuckyWheelView_wheelCenterImageWidth, 30.getDpValue()).let { arrowWidth ->
            setWheelCenterImageWidth(width = arrowWidth)
        }

        typedArray.getDimension(R.styleable.LuckyWheelView_wheelCenterImageHeight, 30.getDpValue()).let { arrowHeight ->
            setWheelCenterImageHeight(height = arrowHeight)
        }

        typedArray.getDrawable(R.styleable.LuckyWheelView_wheelCenterArrow)?.let { drawable ->
            setWheelCenterArrow(wheelArrowDrawable = drawable)
        }

        typedArray.getDimension(R.styleable.LuckyWheelView_wheelCenterArrowWidth, 30.getDpValue()).let { arrowWidth ->
            setWheelCenterArrowWidth(width = arrowWidth)
        }

        typedArray.getDimension(R.styleable.LuckyWheelView_wheelCenterArrowHeight, 30.getDpValue()).let { arrowHeight ->
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

        typedArray.getString(R.styleable.LuckyWheelView_wheelCenterText)?.let { centertText ->
            setWheelCenterText(wheelCenterText = centertText)
        }

        typedArray.getColor(R.styleable.LuckyWheelView_wheelCenterTextColor, Color.BLACK).let { color ->
            setWheelCenterTextColor(wheelCenterTextColor = color)
        }

        typedArray.getDimensionPixelSize(R.styleable.LuckyWheelView_wheelCenterTextSize, 16).let { textSize ->
            if (textSize == 16) {
                setWheelCenterTextSize(wheelCenterTextSize = 16)
            } else {
                setWheelCenterTextSize(wheelCenterTextSize = textSize.toFloat())
            }
        }

        typedArray.getFont(R.styleable.LuckyWheelView_wheelCenterTextFont)?.let { font ->
            setWheelCenterTextFont(typeface = font)
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

        typedArray.getInt(R.styleable.LuckyWheelView_rotateSpeed, RotationSpeed.NORMAL).let { rotateSpeed ->
            setRotateSpeed(rotateSpeed = rotateSpeed)
        }

        typedArray.getFloat(R.styleable.LuckyWheelView_rotateSpeedMultiplier, 1F).let { rotateSpeedMultiplier ->
            setRotateSpeedMultiplier(rotateSpeedMultiplier = rotateSpeedMultiplier)
        }

        typedArray.getColor(R.styleable.LuckyWheelView_wheelColor, Color.WHITE).let { wheelColor ->
            setWheelColor(wheelColor = wheelColor)
        }

        typedArray.getDimension(R.styleable.LuckyWheelView_wheelPadding, 2F).let { wheelPadding ->
            setWheelPadding(padding = wheelPadding)
        }

        typedArray.getBoolean(R.styleable.LuckyWheelView_drawItemSeparator, false).let { drawItemSeparator ->
            drawItemSeparator(drawItemSeparator = drawItemSeparator)
        }

        typedArray.getColor(R.styleable.LuckyWheelView_wheelItemSeparatorColor, Color.BLACK).let { wheelItemSeparatorColor ->
            setWheelItemSeparatorColor(wheelItemSeparatorColor = wheelItemSeparatorColor)
        }

        typedArray.getFloat(R.styleable.LuckyWheelView_itemSeparatorThickness, 1F).let { itemSeparatorThickness ->
            setItemSeparatorThickness(itemSeparatorThickness = itemSeparatorThickness)
        }

        typedArray.getBoolean(R.styleable.LuckyWheelView_drawCenterPoint, false).let { drawCenterPoint ->
            drawCenterPoint(drawCenterPoint = drawCenterPoint)
        }

        typedArray.getColor(R.styleable.LuckyWheelView_centerPointColor, Color.WHITE).let { centerPointColor ->
            setCenterPointColor(centerPointColor = centerPointColor)
        }

        typedArray.getFloat(R.styleable.LuckyWheelView_centerPointRadius, 30F).let { centerPointRadius ->
            setCenterPointRadius(centerPointRadius = centerPointRadius)
        }

        typedArray.getInt(R.styleable.LuckyWheelView_textOrientation, TextOrientation.HORIZONTAL).let { textOrientation ->
            setTextOrientation(textOrientation = textOrientation)
        }

        typedArray.getInt(R.styleable.LuckyWheelView_textPadding, 20).let { textPadding ->
            setTextPadding(textPadding = textPadding)
        }

        typedArray.getInt(R.styleable.LuckyWheelView_textSize, 16).let { textSize ->
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

        typedArray.getFloat(R.styleable.LuckyWheelView_iconPosition, 2.0F).let { position ->
            setIconPosition(position = position)
        }

        typedArray.getBoolean(R.styleable.LuckyWheelView_enableVibration, false).let { enableVibration ->
            if (enableVibration) {
                this.vibrationEnabled = true
                enableVibration()
            } else {
                this.vibrationEnabled = false
            }
        }

        typedArray.recycle()
    }

    fun Int.getDpValue(): Float {
        return (this * Resources.getSystem().displayMetrics.density + 0.5f)
    }

    fun Float.getDpValue(): Int {
        return (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
    }

    fun Float.getDpValueFloat(): Float {
        return (this * Resources.getSystem().displayMetrics.density + 0.5f)
    }


    /**
     * this function set root layout color
     * also if this function don't call then root layout be transparent(default)
     */
    fun setRootLayoutBackgroundColor(rootLayoutColor: Int) {
        rootLayout.setBackgroundColor(rootLayoutColor)
    }

    /**
     * this functions set root layout drawable
     * default is null
     */
    fun setRootLayoutBackgroundDrawable(rootLayoutDrawableId: Int) {
        rootLayout.setBackgroundResource(rootLayoutDrawableId)
    }

    /**
     * this functions set root layout drawable
     * default is null
     */
    fun setRootLayoutBackgroundDrawable(rootLayoutDrawable: Drawable) {
        rootLayout.background = rootLayoutDrawable
    }

    /**
     * this function set root layout padding
     * also if this function don't call then root layout padding be 5dp(default)
     */
    fun setRootLayoutPadding(padding: Float) {
        val paddingDp = padding.getDpValue()
        rootLayout.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
    }

    /**
     * this function set root layout padding
     * also if this function don't call then root layout padding be 5dp(default)
     */
    fun setRootLayoutPadding(left: Float, top: Float, right: Float, bottom: Float) {
        val paddingLeft = left.getDpValue()
        val paddingTop = top.getDpValue()
        val paddingRight = right.getDpValue()
        val paddingBottom = bottom.getDpValue()

        rootLayout.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
    }

    /**
     * this function set wheel arrow position
     * @see ArrowPosition
     */
    fun setArrowPosition(arrowPosition: Int) {
        this.arrowPosition = arrowPosition

        when (arrowPosition) {
            ArrowPosition.TOP -> {
                wheelCenterArrow.visibility = View.GONE

                wheelTopArrow.visibility = View.VISIBLE
            }
            ArrowPosition.CENTER -> {
                wheelCenterArrow.visibility = View.VISIBLE

                wheelTopArrow.visibility = View.GONE
            }
            else -> {
                wheelCenterArrow.visibility = View.GONE

                wheelTopArrow.visibility = View.VISIBLE
            }
        }
    }

    /**
     * this function set wheel arrow animation status enable or disable
     * also if this function don't call then arrow animation status be true(default)
     */
    fun setArrowAnimationStatus(arrowAnimStatus: Boolean) {
        this.arrowAnimStatus = arrowAnimStatus
    }

    /**
     * this function set wheel arrow swing animation duration
     * also if this function don't call then arrow swing animation duration be 50ms(default)
     */
    fun setArrowSwingDuration(arrowSwingDuration: Int) {
        this.arrowSwingDuration = arrowSwingDuration
    }

    /**
     * this function set wheel arrow swing animation distance
     * also if this function don't call then arrow swing animation distance be 10F(default)
     */
    fun setArrowSwingDistance(arrowSwingDistance: Int) {
        this.arrowSwingDistance = arrowSwingDistance
    }

    /**
     * this function set wheel arrow swing animation slowdown multiplier
     * also if this function don't call then arrow swing animation slowdown multiplier be 0.1F(default)
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
     * this function set wheel top arrow resource
     */
    fun setWheelTopArrow(wheelArrowId: Int) {
        wheelTopArrow.setImageResource(wheelArrowId)
    }

    /**
     * this function set wheel top arrow drawable
     */
    fun setWheelTopArrow(wheelArrowDrawable: Drawable) {
        wheelTopArrow.setImageDrawable(wheelArrowDrawable)
    }

    /**
     * this function set wheel top arrow size with DP value
     */
    fun setWheelTopArrowSize(width: Float, height: Float) {
        setWheelTopArrowWidth(width.getDpValueFloat())
        setWheelTopArrowHeight(height.getDpValueFloat())
    }

    /**
     * this function set wheel top arrow width with DP value
     */
    private fun setWheelTopArrowWidth(width: Float) {
        wheelTopArrow.layoutParams.width = width.toInt()
    }

    /**
     * this function set wheel top arrow height with DP value
     */
    private fun setWheelTopArrowHeight(height: Float) {
        wheelTopArrow.layoutParams.height = height.toInt()
    }

    /**
     * this function set wheel top arrow color
     */
    fun setWheelTopArrowColor(wheelTopArrowColor: Int) {
        wheelTopArrow.setColorFilter(wheelTopArrowColor)
    }

    /**
     * this function set wheel top arrow margin
     * also if this function don't call then wheel top arrow margin be 0dp(default)
     */
    fun setWheelTopArrowMargin(margin: Float) {
        val marginDp = margin.getDpValue()

        val params: MarginLayoutParams = wheelTopArrow.layoutParams as MarginLayoutParams
        params.bottomMargin = -marginDp
    }

    /**
     * this function set wheel top arrow margin
     * also if this function don't call then wheel top arrow margin be 0dp(default)
     * this function for attrs calling
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
     * this function set wheel center image resource
     */
    fun setWheelCenterImage(wheelCenterImageId: Int) {
        wheelCenterImage.isGone = false
        wheelCenterImage.setImageResource(wheelCenterImageId)
    }

    /**
     * this function set wheel center image drawable
     */
    fun setWheelCenterImage(wheelCenterImageDrawable: Drawable) {
        wheelCenterImage.isGone = false
        wheelCenterImage.setImageDrawable(wheelCenterImageDrawable)
    }

    /**
     * this function set wheel center image size with DP value
     */
    fun setWheelCenterImageSize(width: Float, height: Float) {
        setWheelCenterImageWidth(width.getDpValueFloat())
        setWheelCenterImageHeight(height.getDpValueFloat())
    }

    /**
     * this function set wheel center image width with DP value
     */
    private fun setWheelCenterImageWidth(width: Float) {
        wheelCenterImage.layoutParams.width = width.toInt()
    }

    /**
     * this function set wheel center image height with DP value
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
     * this function set wheel center arrow resource
     */
    fun setWheelCenterArrow(wheelArrowId: Int) {
        wheelCenterArrow.setImageResource(wheelArrowId)
    }

    /**
     * this function set wheel center arrow drawable
     */
    fun setWheelCenterArrow(wheelArrowDrawable: Drawable) {
        wheelCenterArrow.setImageDrawable(wheelArrowDrawable)
    }

    /**
     * this function set wheel center arrow size with DP value
     */
    fun setWheelCenterArrowSize(width: Float, height: Float) {
        setWheelCenterArrowWidth(width.getDpValueFloat())
        setWheelCenterArrowHeight(height.getDpValueFloat())
    }

    /**
     * this function set wheel center arrow width with DP value
     */
    private fun setWheelCenterArrowWidth(width: Float) {
        wheelCenterArrow.layoutParams.width = width.toInt()
    }

    /**
     * this function set wheel center arrow height with DP value
     */
    private fun setWheelCenterArrowHeight(height: Float) {
        wheelCenterArrow.layoutParams.height = height.toInt()
    }

    /**
     * this function set wheel center arrow color
     */
    fun setWheelCenterArrowColor(wheelCenterArrowColor: Int) {
        wheelCenterArrow.setColorFilter(wheelCenterArrowColor)
    }

    /**
     * this function set wheel center arrow margin
     * also if this function don't call then wheel center arrow margin be 0dp(default)
     */
    fun setWheelCenterArrowMargin(marginTop: Float, marginBottom: Float) {
        val marginTopDp = marginTop.getDpValue()
        val marginBottomDp = marginBottom.getDpValue()

        val params: MarginLayoutParams = wheelCenterArrow.layoutParams as MarginLayoutParams
        params.bottomMargin = marginBottomDp
        params.topMargin = marginTopDp
    }

    /**
     * this function set wheel center arrow top margin
     * also if this function don't call then wheel center arrow top margin be 0dp(default)
     */
    private fun setWheelCenterArrowMarginTop(marginTop: Float) {
        val marginTopDp = marginTop.getDpValue()

        val params: MarginLayoutParams = wheelCenterArrow.layoutParams as MarginLayoutParams
        params.topMargin = marginTopDp
    }

    /**
     * this function set wheel center arrow bottom margin
     * also if this function don't call then wheel center arrow bottom margin be 0dp(default)
     */
    private fun setWheelCenterArrowMarginBottom(marginBottom: Float) {
        val marginBottomDp = marginBottom.getDpValue()

        val params: MarginLayoutParams = wheelCenterArrow.layoutParams as MarginLayoutParams
        params.bottomMargin = marginBottomDp
    }

    fun setWheelCenterText(wheelCenterText: String, wheelCenterTextColor: Int, wheelCenterTextSize: Int, fontResourceId: Int) {
        setWheelCenterText(wheelCenterText = wheelCenterText)
        setWheelCenterTextColor(wheelCenterTextColor = wheelCenterTextColor)
        setWheelCenterTextSize(wheelCenterTextSize = wheelCenterTextSize)
        setWheelCenterTextFont(fontResourceId = fontResourceId)
    }

    fun setWheelCenterText(wheelCenterText: String, wheelCenterTextColor: Int, wheelCenterTextSize: Int, typeface: Typeface) {
        setWheelCenterText(wheelCenterText = wheelCenterText)
        setWheelCenterTextColor(wheelCenterTextColor = wheelCenterTextColor)
        setWheelCenterTextSize(wheelCenterTextSize = wheelCenterTextSize)
        setWheelCenterTextFont(typeface = typeface)
    }

    /**
     * this function set wheel center text
     * also if this function don't call then  wheel center text gone(default)
     */
    fun setWheelCenterText(wheelCenterText: String) {
        wheelCenterTextView.visibility = View.VISIBLE
        wheelCenterTextView.text = wheelCenterText
    }

    /**
     * this function set wheel center text color
     */
    fun setWheelCenterTextColor(wheelCenterTextColor: Int) {
        wheelCenterTextView.setTextColor(wheelCenterTextColor)
    }

    /**
     * this function set wheel center text size with SP value
     * also if this function don't call then text size be 16sp(default)
     */
    fun setWheelCenterTextSize(wheelCenterTextSize: Int) {
        wheelCenterTextView.textSize = wheelCenterTextSize.toFloat()
    }

    /**
     * this function set wheel center text size with SP value
     * also if this function don't call then text size be 16sp(default)
     */
    private fun setWheelCenterTextSize(wheelCenterTextSize: Float) {
        wheelCenterTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, wheelCenterTextSize)
    }

    /**
     * this function set wheel center text font family
     * also if this function don't call then text font family be Sans Serif(default)
     */
    fun setWheelCenterTextFont(fontResourceId: Int) {
        val typeface = ResourcesCompat.getFont(context, fontResourceId)
        wheelCenterTextView.typeface = typeface!!
    }

    /**
     * this function set wheel center text font family
     * also if this function don't call then text font family be Sans Serif(default)
     */
    fun setWheelCenterTextFont(typeface: Typeface) {
        wheelCenterTextView.typeface = typeface
    }


    /**
     * this function set list of wheel data
     * also if this function don't call or given list is empty then wheel view is draw only a circle
     * @see WheelData
     */
    fun setWheelData(wheelData: ArrayList<WheelData>) {
        wheelView.setWheelData(wheelData = wheelData)
    }

    /**
     * @see setTarget
     * @see setRotateRandomTarget
     */
    fun setTarget(target: Int, rotateRandomTarget: Boolean) {
        setTarget(target = target)
        setRotateRandomTarget(rotateRandomTarget = rotateRandomTarget)
    }

    /**
     * this function rotate wheel to given index
     * @param target is index of the item to win
     * also if target a negative number then target throw IndexOutOfBoundsException
     * also if target bigger than wheel data size then throw IndexOutOfBoundsException
     */
    fun setTarget(target: Int) {
        this.target = target
    }

    /**
     * this function set rotate with random target
     */
    fun setRotateRandomTarget(rotateRandomTarget: Boolean) {
        this.rotateRandomTarget = rotateRandomTarget
    }

    /**
     * @see setRotationViaSwipe
     * @see setSwipeDistance
     */
    fun setRotationViaSwipe(rotationViaSwipe: Boolean, swipeDistance: Int) {
        setRotationViaSwipe(rotationViaSwipe = rotationViaSwipe)
        setSwipeDistance(swipeDistance = swipeDistance)
    }

    /**
     * this function set user can rotate wheel via swipe
     * also if this function don't call then user can't rotate wheel via swipe
     * default false
     */
    fun setRotationViaSwipe(rotationViaSwipe: Boolean) {
        this.rotationViaSwipe = rotationViaSwipe
    }

    /**
     * this function set swipe trigger distance
     * also if this function don't call then swipe trigger distance be 100F(default)
     */
    fun setSwipeDistance(swipeDistance: Int) {
        this.swipeDistance = swipeDistance
    }

    /**
     * this function set rotation stop at center of item
     * also if this function don't call then stop center of item be false(default)
     */
    fun stopCenterOfItem(stopCenterOfItem: Boolean) {
        wheelView.stopCenterOfItem(stopCenterOfItem = stopCenterOfItem)
    }


    fun setRotateTime(rotateTime: Long, rotateSpeed: Int, rotateSpeedMultiplier: Float) {
        wheelView.setRotateTime(rotateTime = rotateTime, rotateSpeed = rotateSpeed, rotateSpeedMultiplier = rotateSpeedMultiplier)
    }

    /**
     * this function set wheel rotate time
     * also if this function don't call then wheel rotateTime be 5000ms(default)
     */
    fun setRotateTime(rotateTime: Long) {
        wheelView.setRotateTime(rotateTime = rotateTime)
    }

    /**
     * this function set wheel rotate base speed
     * also if this function don't call then wheel rotateSpeed be normal(default)
     * @see RotationSpeed
     */
    fun setRotateSpeed(rotateSpeed: Int) {
        wheelView.setRotateSpeed(rotateSpeed = rotateSpeed)
    }

    /**
     * this function set wheel rotate speed multiplier
     * also if this function don't call then wheel rotateSpeedMultiplier be 1F(default)
     */
    fun setRotateSpeedMultiplier(rotateSpeedMultiplier: Float) {
        wheelView.setRotateSpeedMultiplier(rotateSpeedMultiplier = rotateSpeedMultiplier)
    }

    /**
     * this function set wheel color
     * also if this function don't call then wheel color be white(default)
     */
    fun setWheelColor(wheelColor: Int) {
        wheelView.setWheelColor(wheelColor = wheelColor)
    }

    /**
     * this function set wheel padding
     * also if this function don't call then wheel padding be 2dp(default)
     */
    fun setWheelPadding(padding: Float) {
        val paddingDp = padding.getDpValue()
        wheelView.setWheelPadding(padding = paddingDp)
    }


    fun drawItemSeparator(drawItemSeparator: Boolean, wheelItemSeparatorColor: Int, itemSeparatorThickness: Float) {
        wheelView.drawItemSeparator(drawItemSeparator = drawItemSeparator, wheelItemSeparatorColor = wheelItemSeparatorColor, itemSeparatorThickness = itemSeparatorThickness)
    }

    /**
     * this function set item separator visibility status
     * also if this function don't call then item separator don't draw(default)
     */
    fun drawItemSeparator(drawItemSeparator: Boolean) {
        wheelView.drawItemSeparator(drawItemSeparator = drawItemSeparator)
    }

    /**
     * this function set wheel item separator color
     * also if this function don't call then wheel color be black(default)
     */
    fun setWheelItemSeparatorColor(wheelItemSeparatorColor: Int) {
        wheelView.setWheelItemSeparatorColor(wheelItemSeparatorColor = wheelItemSeparatorColor)
    }

    /**
     * this function set item separator thickness
     * also if this function don't call then item separator thickness be 1F(default)
     */
    fun setItemSeparatorThickness(itemSeparatorThickness: Float) {
        wheelView.setItemSeparatorThickness(itemSeparatorThickness = itemSeparatorThickness)
    }


    fun drawCenterPoint(drawCenterPoint: Boolean, centerPointColor: Int, centerPointRadius: Float) {
        wheelView.drawCenterPoint(drawCenterPoint  = drawCenterPoint, centerPointColor = centerPointColor, centerPointRadius = centerPointRadius)
    }

    /**
     * this function set center point visibility status
     * also if this function don't call then center point don't draw(default)
     */
    fun drawCenterPoint(drawCenterPoint: Boolean) {
        wheelView.drawCenterPoint(drawCenterPoint = drawCenterPoint)
    }

    /**
     * this function set center point color
     * also if this function don't call then center point color be white(default)
     */
    fun setCenterPointColor(centerPointColor: Int) {
        wheelView.setCenterPointColor(centerPointColor = centerPointColor)
    }

    /**
     * this function set center point radius
     * also if this function don't call then center point radius be 30F(default)
     */
    fun setCenterPointRadius(centerPointRadius: Float) {
        wheelView.setCenterPointRadius(centerPointRadius = centerPointRadius)
    }


    fun setWheelItemText(textOrientation: Int, textPadding: Int, textSize: Int, letterSpacing: Float, fontResourceId: Int) {
        val paddingDp = (textPadding * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
        val textSizeSp: Float = textSize * resources.displayMetrics.scaledDensity
        val typeface = ResourcesCompat.getFont(context, fontResourceId)

        wheelView.setWheelItemText(textOrientation = textOrientation, textPadding = paddingDp, textSize = textSizeSp, letterSpacing =  letterSpacing, typeface = typeface!!)
    }

    fun setWheelItemText(textOrientation: Int, textPadding: Int, textSize: Int, letterSpacing: Float, typeface: Typeface) {
        val paddingDp = (textPadding * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
        val textSizeSp: Float = textSize * resources.displayMetrics.scaledDensity

        wheelView.setWheelItemText(textOrientation = textOrientation, textPadding = paddingDp, textSize = textSizeSp, letterSpacing =  letterSpacing, typeface = typeface)
    }

    /**
     * this function set item text orientation
     * also if this function don't call then text orientation be horizontal(default)
     * @see TextOrientation
     */
    fun setTextOrientation(textOrientation: Int) {
        wheelView.setTextOrientation(textOrientation = textOrientation)
    }

    /**
     * this function set item text padding bottom
     * also if this function don't call then text padding be 20dp(default)
     */
    fun setTextPadding(textPadding: Int) {
        val paddingDp = textPadding.getDpValue().toInt()
        wheelView.setTextPadding(textPadding = paddingDp)
    }

    /**
     * this function set item text size with SP value
     * also if this function don't call then text size be 16sp(default)
     */
    fun setTextSize(textSize: Int) {
        val textSizeSp: Float = textSize * resources.displayMetrics.scaledDensity
        wheelView.setTextSize(textSize = textSizeSp)
    }

    /**
     * this function set item text letter spacing
     * @param letterSpacing must be in range 0.0F - 1.0F
     * @param letterSpacing is not in range then letter spacing be 1.0F
     * also if this function don't call then text letter spacing be 0.1F(default)
     */
    fun setTextLetterSpacing(letterSpacing: Float) {
        if (letterSpacing in 0.0F..1.0F) {
            wheelView.setTextLetterSpacing(letterSpacing = letterSpacing)
        } else {
            wheelView.setTextLetterSpacing(letterSpacing = 1.0F)
        }
    }

    /**
     * this function set item text font family
     * also if this function don't call then text font family be Sans Serif(default)
     */
    fun setTextFont(fontResourceId: Int) {
        val typeface = ResourcesCompat.getFont(context, fontResourceId)
        wheelView.setTextFont(typeface = typeface!!)
    }

    /**
     * this function set item text font family
     * also if this function don't call then text font family be Sans Serif(default)
     */
    fun setTextFont(typeface: Typeface) {
        wheelView.setTextFont(typeface = typeface)
    }

    /**
     * this function set item icon size multiplier
     * also if this function don't call then icon size multiplier be 1.0F(default)
     */
    fun setIconSizeMultiplier(sizeMultiplier: Float) {
        wheelView.setIconSizeMultiplier(sizeMultiplier = sizeMultiplier)
    }

    /**
     * this function set item icon position
     * also if this function don't call then icon position be 2.0F(default)
     */
    fun setIconPosition(position: Float) {
        wheelView.setIconPosition(position = position)
    }


    /**
     * this function set vibration enable or disable when wheel rotation stop
     * also if this function don't call then vibration when wheel rotation stop default false
     * also if VIBRATE permission not granted then vibration when wheel rotation stop default false
     * also if device has not vibrator then vibration when wheel rotation stop default false
     * @see checkVibrateService
     */
    @RequiresPermission(value = android.Manifest.permission.VIBRATE)
    fun enableVibration() {
        if (context.checkSelfPermission(android.Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
            this.vibrationEnabled = true
            checkVibrateService()
        } else {
            this.vibrationEnabled = false
        }
    }

    private fun checkVibrateService() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (vibrator?.hasVibrator() == true) {
            this.vibrationEnabled = true
        } else {
            this.vibrationEnabled = false
        }
    }

    /**
     * this function set vibration pattern
     * also if this function don't call then vibration pattern default longArrayOf(200, 500, 250)
     * @see vibratePattern
     */
    fun setVibratePattern(pattern: LongArray) {
        this.vibratePattern = pattern
    }


    fun setListeners(targetReachListener: TargetReachListener, rotationStatusListener: RotationStatusListener) {
        setTargetReachListener(targetReachListener = targetReachListener)
        setRotationStatusListener(rotationStatusListener = rotationStatusListener)
    }

    /**
     * this function set target reach listener to wheel view
     * also if this function don't call then lucky wheel view is not notify user for target reach
     * @see TargetReachListener
     */
    fun setTargetReachListener(targetReachListener: TargetReachListener) {
        this.targetReachListener = targetReachListener
    }

    /**
     * this function set rotation status listener to wheel view
     * also if this function don't call then lucky wheel view is not notify user for rotation status
     * @see RotationStatusListener
     */
    fun setRotationStatusListener(rotationStatusListener: RotationStatusListener) {
        this.rotationStatusListener = rotationStatusListener
    }

    /**
     * this function set wheel view listener to wheel view
     * also if this function don't call then wheel view is not notify user
     * @see WheelViewListener
     * this function is not for user
     */
    private fun setWheelViewListener() {
        wheelView.setWheelViewListener(wheelViewListener = this)
    }

    /**
     * this function rotate wheel to given target
     * or
     * rotate wheel to random index
     * @see rotateWheelRandomTarget
     * @see rotateWheelToTarget
     * also target is default 0
     */
    fun rotateWheel() {
        if (rotationStatus == RotationStatus.IDLE || rotationStatus == RotationStatus.COMPLETED) {
            wheelView.resetWheel()

            if (arrowAnimStatus) {
                startArrowAnimation()
            }

            if (rotateRandomTarget) {
                wheelView.rotateWheelRandomTarget()
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
                arrowRightSwingAnimator = ObjectAnimator.ofFloat(wheelTopArrow, "rotation", -arrowSwingDistance.toFloat(), arrowSwingDistance.toFloat())
                arrowLeftSwingAnimator = ObjectAnimator.ofFloat(wheelTopArrow, "rotation", -arrowSwingDistance.toFloat(), arrowSwingDistance.toFloat())
            }
            ArrowPosition.CENTER -> {
                arrowRightSwingAnimator = ObjectAnimator.ofFloat(wheelCenterArrow, "rotation", -arrowSwingDistance.toFloat(), arrowSwingDistance.toFloat())
                arrowLeftSwingAnimator = ObjectAnimator.ofFloat(wheelCenterArrow, "rotation", -arrowSwingDistance.toFloat(), arrowSwingDistance.toFloat())
            }
            else -> {
                arrowRightSwingAnimator = ObjectAnimator.ofFloat(wheelTopArrow, "rotation", -arrowSwingDistance.toFloat(), arrowSwingDistance.toFloat())
                arrowLeftSwingAnimator = ObjectAnimator.ofFloat(wheelTopArrow, "rotation",  -arrowSwingDistance.toFloat(), arrowSwingDistance.toFloat())
            }
        }

        startRightSwing(arrowSwingDuration.toLong())
    }

    /**
     * this function start swing left animation to selected arrow position
     * this function is not for user
     * @see startRightSwing
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
     * @see startLeftSwing
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
            else -> {
                ObjectAnimator.ofFloat(wheelTopArrow, "rotation", 0F, 0F)
            }
        }
        arrowCenterPositionAnimator.duration = 10
        arrowCenterPositionAnimator.start()
        arrowCenterPositionAnimator.cancel()
        arrowCenterPositionAnimator.removeAllListeners()
    }

    /**
     * this function vibrate device with given vibrate pattern or default vibrate pattern
     * this function is not for user
     * @see setVibratePattern
     */
    @RequiresPermission(value = android.Manifest.permission.VIBRATE)
    private fun vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            vibrator?.vibrate(VibrationEffect.createWaveform(vibratePattern, -1))
        } else {
            vibrator?.vibrate(vibratePattern, -1)
        }
    }

    override fun onRotationComplete(wheelData: WheelData) {
        targetReachListener?.onTargetReached(wheelData = wheelData)

        clearArrowAnimation()

        if (context.checkSelfPermission(android.Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
            vibrate()
        }
    }

    override fun onRotationStatus(rotationStatus: Int) {
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
            else -> {
                false
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