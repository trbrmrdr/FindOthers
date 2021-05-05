package com.trbr.s5differences.Activities

import android.app.job.JobParameters
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.appcompat.view.menu.MenuAdapter
import com.trbr.s5differences.App
import com.trbr.s5differences.Data.Level
import com.trbr.s5differences.Helper.HelperUtils
import com.trbr.s5differences.R
import com.trbr.s5differences.extension.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_win.*
import kotlinx.coroutines.*
import kotlin.math.roundToInt


class MainActivity : BaseActivity() {


    var mp: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        prew_img.setImageBitmap(App.data.getImg(Level.LevelsData.Type.Top))

        label_star.text = App.data.star_count.toString()

//        mp = MediaPlayer.create(this, R.raw.soho)

        /*   btn_coins.setOnClickListener {

               mp ?: return@setOnClickListener

               try {
                   if (mp!!.isPlaying) {
                       mp!!.stop()
                       mp!!.release()
   //                    mp = MediaPlayer.create(this, R.raw.sound)
                   }
                   mp!!.start()
               } catch (e: Exception) {
                   e.printStackTrace()
               }

           }*/
    }

    fun Play(view: View) {
//        HelperUtils.StartActivity(this, PlayingActivity::class.java)
        val intent = Intent(this, PlayingActivity::class.java)
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        this.startActivity(intent)
    }

    var job: Job? = null
    val scope_ui = CoroutineScope(Dispatchers.Main)
    fun More_coins(view: View) {

        job?.cancel()
        job = scope_ui.launch {
            var counter = 20 + Math.random() * 6
            var start_coin = label_coins.text.toString().toInt()
            do {
                start_coin = (start_coin + Math.random() * 5).roundToInt()
                label_coins.text = start_coin.toString()
                delay(140)
            } while (counter-- > 0)
        }
    }
}