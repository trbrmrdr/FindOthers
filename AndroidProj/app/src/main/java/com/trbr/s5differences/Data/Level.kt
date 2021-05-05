package com.trbr.s5differences.Data

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.NonNull
import androidx.core.graphics.get
import com.google.gson.Gson
import com.trbr.s5differences.App
import com.trbr.s5differences.Helper.AppResource
import kotlin.math.roundToInt


class Level(context: Context) : AppResource(context) {

    fun load() {


        ReadAssetFile(context, "levels.json").also { str_data ->
//        context.assets.open("levels.json").also { inputStream ->
            try {
//                val str_data = inputStream.readBytes().toString()
                levels_data = Gson().fromJson(
                    str_data, Array<LevelsData>::class.java
                )
            } catch (ex: Exception) {

            }

        }
    }

    class LevelsData(
        val id: Int = 0,
        val n: Int = 0,
        var jpg_url: String = "",
        var png_url: String = ""
    ) {


        enum class Type {
            Top, Bottom, DIff
        }

        var bmp_top: Bitmap? = null
        var bmp_bottom: Bitmap? = null
        var bmp_ch: Bitmap? = null

        private fun init() {
            bmp_top = getBitmap(jpg_url)

            bmp_ch = getBitmap(png_url)

            val bmOverlay =
                Bitmap.createBitmap(
                    bmp_top!!.width,
                    bmp_top!!.height,
                    bmp_top!!.getConfig()
                )
            val canvas = Canvas(bmOverlay)
            canvas.drawBitmap(bmp_top!!, Matrix(), null)
//                    canvas.drawBitmap(bmp_ch!!, Matrix(), null)
            bmp_bottom = bmOverlay
        }

        fun getImg(type: Type): Bitmap? {
            bmp_top ?: init()
            when (type) {
                Type.Top -> {
                    return bmp_top
                }
                Type.Bottom -> {
                    return bmp_bottom

                }
                Type.DIff -> {
                    return bmp_ch
                }
            }
            return null
        }

        private fun getBitmap(fullPath: String): Bitmap? {
            val bitmap_draw = getDrawableFromAssetFolder(fullPath) as BitmapDrawable
            return bitmap_draw.bitmap
        }


        fun getDrawableFromAssetFolder(fullPath: String): Drawable? {
            var d: Drawable? = null
            try {
                d = Drawable.createFromStream(
                    App.application.getAssets().open(fullPath),
                    null
                );
            } catch (e: Exception) {
                e.printStackTrace();
            }
            return d
        }

        fun check(pos: PointF): Boolean {
            var coun_dif = 0
            for (i in -10..10) {
                for (j in -10..10) {

                    val ch_x = (pos.x + i).roundToInt()
                    val ch_y = (pos.y + j).roundToInt()

                    if (ch_x < 0 || ch_x >= bmp_top!!.width) break
                    if (ch_y < 0 || ch_y >= bmp_top!!.height) break

                    val ch = bmp_ch!!.get(ch_x, ch_y)
                    if (ch != 0)
                    /*val top = bmp_top!!.get(ch_x, ch_y)
                    val bottom = bmp_bottom!!.get(ch_x, ch_y)
                    if (top != bottom)*/
                        coun_dif += 1
                    if (coun_dif > 10) break
                }
            }
            return coun_dif > 3
        }

        fun complete() {
            bmp_top?.also { it.recycle() }
            bmp_bottom?.also { it.recycle() }
            bmp_ch?.also { it.recycle() }

            bmp_top = null
        }


        fun recycle(drawable: Drawable) {
            if (drawable is BitmapDrawable) {
                val bitmapDrawable = drawable as BitmapDrawable
                val bitmap = bitmapDrawable.bitmap
                bitmap.recycle()
            }
        }

    }

    var curr_lvl = 0;


    @NonNull
    var levels_data: Array<LevelsData>? = null
        get() {
            field ?: run {
                try {
                    field =
                        Gson().fromJson(getString("levels_data"), Array<LevelsData>::class.java)
                } catch (ex: java.lang.Exception) {
                }
            }
            return field
        }
        set(value) {
            field = value
            setString("levels_data", Gson().toJson(value))
        }

    fun getImg(type: LevelsData.Type): Bitmap? {
        return levels_data!![curr_lvl].getImg(type)
    }


    fun completeLevel() {
        star_count = star_count + (Math.random() * 10).toInt()

        levels_data!![curr_lvl].complete()

        curr_lvl += 1
        if (curr_lvl >= levels_data!!.size) curr_lvl = 0
    }

    fun check_point(pos: PointF): Boolean {
        return levels_data!![curr_lvl].check(pos)
    }


    var star_count: Int
        get() = getInt("star_count",0)
        set(value) {
            set("star_count", value)
        }

}