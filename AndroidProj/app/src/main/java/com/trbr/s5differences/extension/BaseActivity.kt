package com.trbr.s5differences.extension

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Build.VERSION_CODES.JELLY_BEAN
import android.view.View
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.trbr.s5differences.S5Application

open class BaseActivity : AppCompatActivity() {

    init {
        S5Application.activity = this
    }

    override fun setContentView(@LayoutRes layoutResID: Int) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT



        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }

        /*
        // the status bar.
        if (Build.VERSION.SDK_INT < JELLY_BEAN) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        } else {

            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            actionBar?.hide()
        }*/
        supportActionBar?.hide()

//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        super.setContentView(layoutResID)


        /*window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }*/


//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);


//        toolbar?.setNavigationOnClickListener { onBackPressed() }

//        if (layoutResID != R.layout.activity_splash) {
//            if (!S5Application.data.privacy) DlgPrivacy(this)
//        }
    }

    override fun onResume() {
        super.onResume()
        S5Application.activity = this
    }

    override fun onDestroy() {
        if (S5Application.activity == this) {
            S5Application.activity = null
        }
        super.onDestroy()
    }
}