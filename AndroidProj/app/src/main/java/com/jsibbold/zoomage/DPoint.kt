package com.jsibbold.zoomage

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.*
import android.view.View
import com.trbr.s5differences.App
import com.trbr.s5differences.R
import om.trbr.s5differences.EventManager
import om.trbr.s5differences.EventName
import kotlin.math.roundToInt

class DPoint(val point: PointF) {

    var paint_no = Paint().apply {
        color = Color.RED
        strokeWidth = 3F
        style = Paint.Style.FILL

        colorFilter = PorterDuffColorFilter(
            Color.RED,
            PorterDuff.Mode.SRC_IN
        )
    }

    var paint_yes = Paint().apply {
        color = Color.RED
        strokeWidth = 4F
        style = Paint.Style.STROKE
        alpha = 0
    }

    val radius = 30F
    val DURATION = 800
    var img: Bitmap? = null
    var has_removed = false
    var success: Boolean = false

    init {
        success = App.data.check_point(point)

        if (success) {
            EventManager.sendEvent(EventName.FIND_DIFFERENCES_pre)
        } else {

            img = BitmapFactory.decodeResource(App.application.resources, R.drawable.xmark);
//                img = ContextCompat.getDrawable(App.application, R.drawable.xmark)


//                val paint = Paint().apply {
//                    colorFilter = PorterDuffColorFilter(
//                        Color.RED,
//                        PorterDuff.Mode.SRC_IN
//                    )
//                }
//
//                val canvas = Canvas(img!!)
//                canvas.drawBitmap(img!!, 0F, 0F, paint)
        }

    }

    fun animate(parent: View) {
        val animator = ValueAnimator.ofFloat(0F, 255F)
        animator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator) {

                val anim_val = animation.animatedValue as Float
//                    LogUtils.i("anim ${anim_val}")
                if (success)
                    paint_yes.alpha = anim_val.toInt()
                else
                    paint_no.alpha = 255 - anim_val.toInt()

                parent.invalidate()
            }
        })

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                if (success)
                    EventManager.sendEvent(EventName.FIND_DIFFERENCES)
                else {
                    EventManager.sendEvent(EventName.FIND_ERR)
                    has_removed = true
                }
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {

            }
        })
        animator.duration = DURATION.toLong()
        animator.start()
    }

    fun draw(canvas: Canvas) {
//            val dst = floatArrayOf(point.x, point.y)
//            Matrix().apply {}.mapPoints(dst)
//            canvas.drawCircle(dst[0], dst[1], 15F, paint_r)
        if (success) {
            canvas.drawCircle(point.x, point.y, radius, paint_yes)
        } else {
            canvas.drawBitmap(
                img!!,
                Rect(0, 0, img!!.width, img!!.height),
                Rect(
                    (point.x - radius).roundToInt(),
                    (point.y - radius).roundToInt(),
                    (point.x + radius).roundToInt(),
                    (point.y + radius).roundToInt()
                ), paint_no
            )
        }
    }
}