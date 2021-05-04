package com.trbr.s5differences.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import com.trbr.s5differences.R

class RoundImageView(context: Context, attrs: AttributeSet? = null) :
    androidx.appcompat.widget.AppCompatImageView(context, attrs) {

    private var path: Path? = null
    private lateinit var rect: RectF


    init {
        init(context, attrs)
    }

    var corner_d: Float = 0F
    var corner_p: Float = 0F

    private fun init(context: Context, attrs: AttributeSet? = null) {
        val attributes =
            context.theme.obtainStyledAttributes(attrs, R.styleable.RoundImageView, 0, 0)

        try {
            corner_d = attributes.getDimension(R.styleable.RoundImageView_ri_corner_d, 0F)
            corner_p = attributes.getFloat(R.styleable.RoundImageView_ri_corner_p, -1F)
        } finally {
            attributes.recycle()
        }

//      View.inflate(context, R.layout.average_input, this)
        path = Path()
    }

/*
    override fun invalidate() {
        super.invalidate()

    }

    override fun setBackground(background: Drawable?) {
        super.setBackground(background)
    }
*/

    override fun onDraw(canvas: Canvas) {
        rect = RectF(0F, 0F, this.width.toFloat(), this.height.toFloat())
        if (this.width == 0) return
        if (corner_p > 0)
            path!!.addRoundRect(
                rect,
                this.width * corner_p,
                this.height * corner_p,
                Path.Direction.CW
            )
        else
            path!!.addRoundRect(rect, corner_d, corner_d, Path.Direction.CW)

        canvas.clipPath(path!!)
        super.onDraw(canvas)
    }
}