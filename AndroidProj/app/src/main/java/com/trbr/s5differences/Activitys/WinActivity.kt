package com.trbr.s5differences.Activitys

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.trbr.s5differences.R
import com.trbr.s5differences.extension.BaseActivity

class WinActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_win)
    }

    fun Next(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }
}