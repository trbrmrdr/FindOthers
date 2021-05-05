package com.trbr.s5differences.Activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import com.trbr.s5differences.App
import com.trbr.s5differences.Data.Level
import com.trbr.s5differences.R
import com.trbr.s5differences.extension.BaseActivity
import kotlinx.android.synthetic.main.activity_win.*


class WinActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_win)


        val bitmap = App.data.getImg(Level.LevelsData.Type.Bottom)

        win_image_view.setImageBitmap(
            bitmap!!.copy(bitmap.getConfig(), true)
        )


        App.data.completeLevel()
    }

    fun Next(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    fun BadLevel(view: View) {}
    fun GoodLevel(view: View) {}
    fun Share(view: View) {}
}