package com.trbr.s5differences.Helper

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.trbr.s5differences.App
import com.trbr.s5differences.R

class PermissionVerify(
    val activity: Activity,
    permissions: List<Pair<String, String>>,
    val endCallback: () -> Unit = {}
) {

    constructor(activity: Activity, pair_permission: Pair<String, String>) : this(
        activity,
        listOf(pair_permission)
    )

    class PairPermission(
        val name: String,
        val what: String,
        val request: Int,
        var countRequest: Int = 2
    )

    private val neededPermission: MutableList<PairPermission> = mutableListOf()

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            for (permission in permissions) {
                if (permission.first.contains("android.permission."))
                    neededPermission.add(
                        PairPermission(
                            name = permission.first,
                            what = permission.second,
                            request = neededPermission.size
                        )
                    )
            }
        }
    }

    protected fun createPairPermission(activity: Activity): MutableList<PairPermission>? {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) null else try {
            val list = ArrayList<PairPermission>()
            val requestedPermissions = activity.packageManager.getPackageInfo(
                activity.packageName,
                PackageManager.GET_PERMISSIONS
            ).requestedPermissions
            for (permission in requestedPermissions) {
                if (permission.contains("android.permission.")) {
                    list.add(PairPermission(permission, "", list.size, 1))
                }
            }
            if (list.size > 0) return list
            null
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    var dialog: Dialog? = null

    fun check(): Boolean {
        if (dialog != null) return false

        neededPermission.forEach { permission ->
            permission.apply {
                if (countRequest != 0) {
                    if (!checkPermission(name, request)) return false
                    countRequest = 0
                }
            }
        }
        endCallback()
        return true
    }

    private fun checkPermission(namePermission: String, request: Int): Boolean {
        if (ContextCompat.checkSelfPermission(
                activity,
                namePermission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            /*         when (namePermission) {
                         Manifest.permission.ACCESS_COARSE_LOCATION,
                         Manifest.permission.ACCESS_FINE_LOCATION -> {

                             MaterialAlertDialogBuilder(activity)
                                     .setTitle(R.string.permission_title_al)
                                     .setMessage(R.string.permission_al)
                                     .setPositiveButton(R.string.permission_yes) { _, _ -> ActivityCompat.requestPermissions(activity, arrayOf(namePermission), request) }
                                     .setNegativeButton(R.string.permission_no) { _, _ -> }
                                     .show()

                             return false
                         }
                         Manifest.permission.ACCESS_BACKGROUND_LOCATION -> {

                             MaterialAlertDialogBuilder(activity)
                                     .setTitle(R.string.permission_title_al)
                                     .setMessage(R.string.permission_abl)
                                     .setPositiveButton(R.string.permission_yes) { _, _ -> ActivityCompat.requestPermissions(activity, arrayOf(namePermission), request) }
                                     .setNegativeButton(R.string.permission_no) { _, _ -> }
                                     .show()

                             return false
                         }
                     }
         */
            ActivityCompat.requestPermissions(activity, arrayOf(namePermission), request)
            return false
        }
        return true
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        neededPermission.forEach { pairp ->
            if (requestCode == pairp.request) {
                if (grantResults.size >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pairp.countRequest = 0
                } else {
                    if (pairp.run {
                            if (countRequest > 0) {
                                countRequest--
                                true
                            } else countRequest == -1
                        }
                    ) {
                        if (pairp.countRequest == 0) {
                            dialog = MaterialAlertDialogBuilder(activity)
                                .setMessage(
                                    activity.getString(R.string.msg_permission2).format(pairp.what)
                                )
                                .setPositiveButton(R.string.permission_yes) { _, _ ->
                                    App.activity?.also {
                                        HelperUtils.OpenFlymeSecurityApp(
                                            it
                                        )
                                    }
                                }
                                .setOnDismissListener {
                                    check()
                                    dialog = null
                                }
                                .show()
                            return
                        }
                        dialog = MaterialAlertDialogBuilder(activity)
                            .setMessage(
                                activity.getString(R.string.msg_permission).format(pairp.what)
                            )
                            .setPositiveButton(R.string.permission_yes) { _, _ ->
                                checkPermission(
                                    pairp.name,
                                    pairp.request
                                )
                            }
                            .setNegativeButton(R.string.permission_no) { _, _ -> check() }
                            .setOnDismissListener { dialog = null }
                            .show()
                        return
                    }
                }
                return@forEach
            }
        }
        check()
    }

}