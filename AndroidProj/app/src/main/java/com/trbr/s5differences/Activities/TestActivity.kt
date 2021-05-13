package com.trbr.s5differences.Activities

import android.Manifest
import android.os.Bundle
import com.trbr.s5differences.Analytics
import com.trbr.s5differences.Helper.PermissionVerify
import com.trbr.s5differences.R
import com.trbr.s5differences.extension.BaseActivity
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : BaseActivity() {
    lateinit var permVerify: PermissionVerify
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)


        permVerify = PermissionVerify(
            this,
            Manifest.permission.INTERNET to getString(R.string.ethernet_permission)
        )
        permVerify.check()

        send_event_1.setOnClickListener {
            Analytics.onLike(1, true)
        }

        send_event_2.setOnClickListener {
            Analytics.onLoadAdVideo("lala", "lal")
        }

        send_event_3.setOnClickListener {
            Analytics.onGetDailyReward(4, 4)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permVerify.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}