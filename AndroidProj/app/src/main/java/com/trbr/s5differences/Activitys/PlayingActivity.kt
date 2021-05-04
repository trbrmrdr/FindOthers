package com.trbr.s5differences.Activitys

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.children
import androidx.core.view.get
import com.jsibbold.zoomage.ZoomageView
import com.trbr.s5differences.Helper.HelperUtils
import com.trbr.s5differences.R
import com.trbr.s5differences.extension.BaseActivity
import kotlinx.android.synthetic.main.activity_playing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class PlayingActivity : BaseActivity(), View.OnClickListener {


    var job: Job? = null
    var scope_ui = CoroutineScope(Dispatchers.Main)

    lateinit var img_top: ZoomageView
    lateinit var img_bottom: ZoomageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playing)

        (main_top_card as? CardView)?.let { parent ->
            img_top = (parent.get(0) as CardView).get(0) as ZoomageView
            setSize(img_top, parent)
        }

        (main_bottom_card as? CardView)?.let { parent ->
            img_bottom = (parent.get(0) as CardView).get(0) as ZoomageView
            setSize(img_bottom, parent)
        }

        img_top.setMiror(img_bottom)
        img_bottom.setMiror(img_top)

       /* var counter = 0
        star_input.children.forEach { star_btn ->
            star_btn.setOnClickListener(this)
            star_btn.id = counter++
        }*/
    }

    private fun setSize(img: ZoomageView, parent: ViewGroup) {
        val width_img = (img.drawable as BitmapDrawable).bitmap.width
        val height_img = (img.drawable as BitmapDrawable).bitmap.height


        val vto = parent.getViewTreeObserver()
        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    parent.getViewTreeObserver().removeGlobalOnLayoutListener(this)
                } else {
                    parent.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                }
                val width_contain: Int = parent.getMeasuredWidth()
//                val height_contain: Int = parent.getMeasuredHeight()

//                scope_ui.launch {
                val pagerParams = parent.getLayoutParams()
                pagerParams.height =
                    (width_contain / (width_img.toFloat() / height_img.toFloat())).toInt()
                parent.layoutParams = pagerParams
//                }

            }
        })

    }

    override fun onClick(v: View?) {
        v ?: return
        star_input.children.forEach { star_btn ->
            star_btn.setOnClickListener(this)
            if (star_btn.id <= v.id)
                star_btn.background = resources.getDrawable(R.drawable.star, null)
            else
                star_btn.background = resources.getDrawable(R.drawable.star_gray, null)

        }
    }

    fun Back(view: View) {
//        HelperUtils.StartActivity(this, MainActivity::class.java)
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    var active_ad = true
    var counter = 0
    fun Hint_active(view: View) {
        active_ad = !active_ad
        HelperUtils.AnimView(
            hint_notification, active_ad,
            R.anim.hint_ad_show, R.anim.hint_ad_hide, this
        )


        val toast = Toast.makeText(applicationContext, "counter = ${counter}", Toast.LENGTH_LONG)
        toast.show()

        if (counter++ >= 2) {

            val intent = Intent(this, WinActivity::class.java)
            this.startActivity(intent)
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    fun GoSetting(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        this.startActivity(intent)
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}