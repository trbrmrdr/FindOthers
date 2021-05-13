package com.trbr.s5differences.Activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.AnimationUtils
import androidx.cardview.widget.CardView
import androidx.core.view.children
import androidx.core.view.get
import com.jsibbold.zoomage.ZoomageView
import com.trbr.s5differences.App
import com.trbr.s5differences.Data.Level
import com.trbr.s5differences.Helper.HelperUtils
import com.trbr.s5differences.Helper.LogUtils
import com.trbr.s5differences.R
import com.trbr.s5differences.extension.BaseActivity
import kotlinx.android.synthetic.main.activity_playing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import om.trbr.s5differences.EMListener
import om.trbr.s5differences.EventManager
import om.trbr.s5differences.EventName
import om.trbr.s5differences.blur


class PlayingActivity : BaseActivity(), View.OnClickListener, EMListener {


    var job: Job? = null
    var scope_ui = CoroutineScope(Dispatchers.Main)

    lateinit var img_top: ZoomageView
    lateinit var img_bottom: ZoomageView
    var lvl_stage = -1
    var err_click = 0

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

        /* var counter = 0
         star_input.children.forEach { star_btn ->
             star_btn.setOnClickListener(this)
             star_btn.id = counter++
         }*/
    }

    override fun onResume() {
        super.onResume()
        lvl_stage = -1
        err_click = 0
        setImage()

        EventManager.registerEvents(
            arrayOf(
                EventName.FIND_DIFFERENCES,
                EventName.FIND_DIFFERENCES_pre,
                EventName.FIND_ERR
            ), eventListener
        )
    }


    override fun onStop() {
        super.onStop()
        EventManager.un_registerEvent(eventListener)
    }

    val eventListener = object : EMListener {
        override fun callback(name: EventName, data: Any?): Boolean {
            if (lvl_stage >= 10) return false
            when (name) {
                EventName.FIND_DIFFERENCES_pre -> {
                    lvl_stage += 1
                    if (lvl_stage >= 4) {
                        img_top.onStop = true
                        img_bottom.onStop = true
                    }
                    star_input.children.elementAt(lvl_stage).apply {
                        background = resources.getDrawable(R.drawable.star, null)

                        startAnimation(
                            AnimationUtils.loadAnimation(
                                App.application,
                                R.anim.star_pulse
                            )
                        )
//                            star_btn.background = resources.getDrawable(R.drawable.star_gray, null)
                    }
                }
                EventName.FIND_DIFFERENCES -> {

                    scope_ui.launch {
                        if (lvl_stage >= 4 && lvl_stage < 10) {
                            lvl_stage = 10
                            WinScreen()
                            return@launch
                        }
                    }
                }
                EventName.FIND_ERR -> {
                    err_click += 1
                    if (err_click >= 3) {
                        err_click = 0
                        LimitTouch()
                    }
                }
            }
            return false
        }
    }

    private fun WinScreen() {
        LogUtils.i("WinScreen")
        val intent = Intent(this@PlayingActivity, WinActivity::class.java)
        startActivity(intent)
        overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        );
    }

    fun setImage(has_next: Boolean = false) {

        img_top.clearPoint()
        img_bottom.clearPoint()


        if (has_next) App.data.completeLevel()

        App.data.also { level ->
            img_top.setImageBitmap(level.getImg(Level.LevelsData.Type.Top)!!)
            img_top.setMiror(img_bottom)

            img_bottom.setImageBitmap(level.getImg(Level.LevelsData.Type.Bottom)!!)
            img_bottom.setMiror(img_top)
            img_bottom.setAnimated(true)
        }
    }

    private fun setSize(img: ZoomageView, parent: ViewGroup) {
        val width_img = (img.drawable as BitmapDrawable).bitmap.width
        val height_img = (img.drawable as BitmapDrawable).bitmap.height

        val aspect = 720F / 580F

        val vto = parent.getViewTreeObserver()
        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    parent.getViewTreeObserver().removeGlobalOnLayoutListener(this)
                } else {
                    parent.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                }

//                   scope_ui.launch {
                val width_contain: Int = parent.getMeasuredWidth()
                //                val height_contain: Int = parent.getMeasuredHeight()
                val pagerParams = parent.getLayoutParams()
                pagerParams.height = (width_contain / aspect).toInt()
                parent.layoutParams = pagerParams
//                   }

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
//        onBackPressed()
//        nextImage()
        LimitTouch()
    }

    private fun LimitTouch() = scope_ui.launch {
        LogUtils.i("LimitTouch")

//        if (block_layout.visibility != View.GONE) {
//            block_layout.visibility = View.GONE
//            return@launch
//        }
        block_layout.visibility = View.GONE

        var returnedBitmap = Bitmap.createBitmap(
            main_constrain_layout.width,
            main_constrain_layout.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnedBitmap)
        main_constrain_layout.draw(canvas)

        returnedBitmap = returnedBitmap.blur(App.application, 25F)
//        returnedBitmap = returnedBitmap.blur(App.application, 25F)
//        returnedBitmap = returnedBitmap.blur(App.application, 25F)

        block_layout.background = BitmapDrawable(App.application.resources, returnedBitmap)

        block_layout.visibility = View.VISIBLE
//        BlurBuilder.blur(this, returnedBitmap, block_layout);


        val timer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                scope_ui.launch {
                    block_timer.text = (millisUntilFinished / 1000 + 1).toString()
                }
            }

            override fun onFinish() {
                scope_ui.launch {
                    block_layout.visibility = View.GONE
                }
            }
        }
        timer.start()

        scope_ui.launch { }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        //        HelperUtils.StartActivity(this, MainActivity::class.java)
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    var active_ad = true
    fun Hint_active(view: View) {
        active_ad = !active_ad
        HelperUtils.AnimView(
            hint_notification, active_ad,
            R.anim.hint_ad_show, R.anim.hint_ad_hide, this
        )

//        val toast =
//            Toast.makeText(applicationContext, "counter = ${n_ac_counter}", Toast.LENGTH_LONG)
//        toast.show()

        img_bottom.hint_active()
    }

    fun GoSetting(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        this.startActivity(intent)
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}