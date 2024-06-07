package com.caneryilmaz.apps.luckywheel.ui

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.caneryilmaz.apps.luckywheel.constant.ArrowPosition
import com.caneryilmaz.apps.luckywheel.R
import com.caneryilmaz.apps.luckywheel.constant.RotationStatus
import com.caneryilmaz.apps.luckywheel.data.WheelData
import com.caneryilmaz.apps.luckywheel.listener.RotationStatusListener
import com.caneryilmaz.apps.luckywheel.listener.TargetReachListener
import com.caneryilmaz.apps.luckywheel.listener.WheelViewListener
import com.caneryilmaz.apps.luckywheel.utils.AttrsHelper
import kotlin.math.abs

class LuckyWheelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), View.OnTouchListener, WheelViewListener {

    private var rootLayout: RelativeLayout
    private var wheelView: WheelView
    private var wheelTopArrow: AppCompatImageView
    private var wheelCenterArrow: AppCompatImageView
    private var wheelCenterTextView: AppCompatTextView

    private var arrowPosition: Int = ArrowPosition.TOP

    private var arrowAnimStatus: Boolean = true
    private var arrowAnimId: Int = R.anim.shake

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

    init {
        inflate(context, R.layout.lucky_wheel_layout, this)

        rootLayout = findViewById(R.id.rootLayout)
        wheelTopArrow = findViewById(R.id.ivTopArrow)
        wheelCenterArrow = findViewById(R.id.ivCenterArrow)
        wheelCenterTextView = findViewById(R.id.ivCenterText)
        wheelView = findViewById(R.id.wheelView)

        wheelView.setOnTouchListener(this)

        setAttrsField(attrs)

        setWheelViewListener()
    }

    private fun setAttrsField(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LuckyWheelView)

        val attrsHelper = AttrsHelper(typedArray)

        attrsHelper.clear()
    }


    /**
     * this function set root layout color
     * also if this function don't call then root layout be transparent(default)
     */
    fun setRootLayoutBackgroundColor(rootLayoutColor: Int) {
        rootLayout.setBackgroundColor(rootLayoutColor)
    }

    /**
     * this function set root layout drawable
     * default is null
     */
    fun setRootLayoutBackgroundDrawable(rootLayoutDrawableId: Int) {
        rootLayout.setBackgroundResource(rootLayoutDrawableId)
    }

    /**
     * this function set root layout padding
     * also if this function don't call then root layout padding be 5dp(default)
     */
    fun setRootLayoutPadding(padding: Int) {
        val paddingDp = (padding * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
        rootLayout.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
    }

    /**
     * this function set root layout padding
     * also if this function don't call then root layout padding be 5dp(default)
     */
    fun setRootLayoutPadding(left: Int, top: Int, right: Int, bottom: Int) {
        val paddingLeft = (left * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
        val paddingTop = (top * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
        val paddingRight = (right * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
        val paddingBottom = (bottom * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

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
     * this function set wheel arrow animation
     */
    fun setArrowAnimation(animationResourceId: Int) {
        this.arrowAnimId = animationResourceId
    }

    /**
     * this function set wheel arrow animation status enable or disable
     * also if this function don't call then arrow animation status be true(default)
     */
    fun setArrowAnimationStatus(arrowAnimStatus: Boolean) {
        this.arrowAnimStatus = arrowAnimStatus
    }


    fun setWheelTopArrow(wheelArrowId: Int, width: Int, height: Int, wheelTopArrowColor: Int, margin: Int) {
        setWheelTopArrow(wheelArrowId = wheelArrowId)
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
     * this function set wheel top arrow size with DP value
     */
    fun setWheelTopArrowSize(width: Int, height: Int) {
        val widthDp = (width * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
        wheelTopArrow.layoutParams.width = widthDp

        val heightDp = (height * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
        wheelTopArrow.layoutParams.height = heightDp
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
    fun setWheelTopArrowMargin(margin: Int) {
        val marginDp = (margin * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

        val params: MarginLayoutParams = wheelTopArrow.layoutParams as MarginLayoutParams
        params.bottomMargin = -marginDp
    }


    fun setWheelCenterArrow(wheelArrowId: Int, width: Int, height: Int, wheelCenterArrowColor: Int, marginTop: Int, marginBottom: Int) {
        setWheelCenterArrow(wheelArrowId = wheelArrowId)
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
     * this function set wheel center arrow size with DP value
     */
    fun setWheelCenterArrowSize(width: Int, height: Int) {
        val widthDp = (width * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
        wheelCenterArrow.layoutParams.width = widthDp

        val heightDp = (height * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
        wheelCenterArrow.layoutParams.height = heightDp
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
    fun setWheelCenterArrowMargin(marginTop: Int, marginBottom: Int) {
        val marginTopDp = (marginTop * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
        val marginBottomDp = (marginBottom * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

        val params: MarginLayoutParams = wheelCenterArrow.layoutParams as MarginLayoutParams
        params.bottomMargin = marginBottomDp
        params.topMargin = marginTopDp
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
     * also if this function don't call or given list is empty then wheel view is not drawn
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
     * also if this function don't call then wheel rotateSpeedMultiplier be 1(default)
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
    fun setWheelPadding(padding: Int) {
        val paddingDp = (padding * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
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
        val paddingDp = (textPadding * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
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
     * this function start shake animation to selected arrow position
     * this function is not for user
     */
    private fun startArrowAnimation() {
        when (arrowPosition) {
            ArrowPosition.TOP -> {
                wheelTopArrow.startAnimation(AnimationUtils.loadAnimation(context, arrowAnimId))
            }
            ArrowPosition.CENTER -> {
                wheelCenterArrow.startAnimation(AnimationUtils.loadAnimation(context, arrowAnimId))
            }
            else -> {
                wheelTopArrow.startAnimation(AnimationUtils.loadAnimation(context, arrowAnimId))
            }
        }
    }

    /**
     * this function clear shake animation from arrows
     * this function is not for user
     */
    private fun clearArrowAnimation() {
        wheelTopArrow.clearAnimation()
        wheelCenterArrow.clearAnimation()
    }

    override fun onRotationComplete(wheelData: WheelData) {
        targetReachListener?.onTargetReached(wheelData = wheelData)

        clearArrowAnimation()
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