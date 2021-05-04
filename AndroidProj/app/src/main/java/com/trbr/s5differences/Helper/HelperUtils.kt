package com.trbr.s5differences.Helper

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.AnimRes
import androidx.annotation.UiThread
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.trbr.s5differences.BuildConfig
import com.trbr.s5differences.R
import com.trbr.s5differences.S5Application
import java.io.File
import java.io.IOException
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object HelperUtils {
    object Images {

        @UiThread
        public fun Load(view: ImageView, path: String?, width: Int = -1, height: Int = -1): Boolean {
            try {
//                view.setImageBitmap(BitmapFactory.decodeFile(path))
//                view.setImageBitmap(getImage(path, width, height))
                Glide.with(S5Application.application)
                        .load(File(path))
                        .apply(RequestOptions().apply {
                            diskCacheStrategy(DiskCacheStrategy.NONE)
                            skipMemoryCache(true)
                        })
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(true)
                        .into(view)

                return true
            } catch (ex: Exception) {
                view.setImageDrawable(null)
                LogUtils.e("load image " + ex)
            }
            return true
        }

        private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
            val (height: Int, width: Int) = options.run { outHeight to outWidth }
            var inSampleSize = 1
            if (height > reqHeight || width > reqWidth) {
                val halfHeight: Int = height / 2
                val halfWidth: Int = width / 2
                while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                    inSampleSize *= 2
                }
            }

            return inSampleSize
        }

        @Throws(IOException::class)
        fun getImage(path: String, width: Int = -1, height: Int = -1): Bitmap? {
            val options: BitmapFactory.Options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, options)
            if (width == options.outWidth && height == options.outHeight) {
                return BitmapFactory.decodeFile(path, options.apply {
                    inJustDecodeBounds = false
                    inDither = false
                    inSampleSize = 1
                    inScaled = false
                    inPreferredConfig = Bitmap.Config.ARGB_8888
                })
            }
            var srcWidth = options.outWidth
            var srcHeight = options.outHeight
            val newWH = IntArray(2)
            newWH[0] = srcWidth / 2
            newWH[1] = newWH[0] * srcHeight / srcWidth
            var inSampleSize = 1//calculateInSampleSize(options, )1
            while (srcWidth / 2 >= newWH[0]) {
                srcWidth /= 2
                srcHeight /= 2
                inSampleSize *= 2
            }

            //float desiredScale = (float) newWH[0] / srcWidth;
            // Decode with inSampleSize
            options.inJustDecodeBounds = false
            options.inDither = false
            options.inSampleSize = inSampleSize
            options.inScaled = false
//            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            options.inPreferredConfig = Bitmap.Config.RGB_565
            val tmp_bitmap = BitmapFactory.decodeFile(path, options)

            val matrix = Matrix().apply {
                postRotate(
                        when (ExifInterface(path).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                            ExifInterface.ORIENTATION_ROTATE_270 -> 270F
                            ExifInterface.ORIENTATION_ROTATE_180 -> 180F
                            ExifInterface.ORIENTATION_ROTATE_90 -> 90F
                            else -> 0F
                        }
                )
            }

//            val newh: Int = w * sampledSrcBitmap.height / sampledSrcBitmap.width

            var neww = tmp_bitmap.width
            var newh = tmp_bitmap.height
            if (width != -1 && height != -1) {
                if (tmp_bitmap.width > tmp_bitmap.height) {
                    neww = width
                    newh = width * tmp_bitmap.height / tmp_bitmap.width
                } else {
                    neww = height * tmp_bitmap.width / tmp_bitmap.height
                    newh = height
                }

            }

            val r = Bitmap.createScaledBitmap(tmp_bitmap, neww, newh, true)
            return Bitmap.createBitmap(r, 0, 0, neww, newh, matrix, true)
        }

        @Throws(IOException::class)
        private fun rotateImage(path: String): Bitmap? {
            var rotate = 0F
            val exif = ExifInterface(path)
            val orientation: Int = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270F
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180F
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90F
            }
            val matrix = Matrix()
            matrix.postRotate(rotate)

            val bitmap = BitmapFactory.decodeFile(path)
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
    }

    val isPreAndroidO: Boolean
        get() = Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1


    fun Toast(context: Context?, msg: String?) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    fun StartClean(activity: Activity, clazz: Class<*>) {
        val intent = Intent(activity, clazz)
        intent.flags = (
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_NEW_TASK
                        or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                or Intent.FLAG_ACTIVITY_NO_HISTORY
                )
        activity.startActivity(intent)
//        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    fun StartActivity(activity: Activity, clazz: Class<*>) {
        val intent = Intent(activity, clazz)
        /*intent.flags = (
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_NEW_TASK
                        or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                or Intent.FLAG_ACTIVITY_NO_HISTORY
                )*/
        activity.startActivity(intent)
//        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    fun StartAlphaAnimation(v: View, duration: Long, visibility: Int, alphaMax: Float) {
        val alphaAnimation = if (visibility == View.VISIBLE) AlphaAnimation(0f, alphaMax) else AlphaAnimation(alphaMax, 0f)
        alphaAnimation.duration = duration
        alphaAnimation.fillAfter = true
        v.startAnimation(alphaAnimation)
    }

    fun ExpandOrCollapseView(v: View, expand: Boolean) {
        if (expand) {
            v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val targetHeight = v.measuredHeight
            v.layoutParams.height = 1
            v.visibility = View.VISIBLE
            val valueAnimator = ValueAnimator.ofInt(1, targetHeight)
            valueAnimator.addUpdateListener { animation ->
                v.layoutParams.height = animation.animatedValue as Int
                v.requestLayout()
            }
            valueAnimator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    v.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
            valueAnimator.interpolator = AccelerateInterpolator()
            valueAnimator.duration = 500
            valueAnimator.start()
        } else {
            val initialHeight = v.measuredHeight
            val valueAnimator = ValueAnimator.ofInt(initialHeight, 0)
            valueAnimator.interpolator = DecelerateInterpolator()
            valueAnimator.addUpdateListener { animation ->
                v.layoutParams.height = animation.animatedValue as Int
                v.requestLayout()
                if (animation.animatedValue as Int == 0) v.visibility = View.GONE
            }
            valueAnimator.interpolator = DecelerateInterpolator()
            valueAnimator.duration = 500
            valueAnimator.start()
        }
    }

    //_________________________________________________________________________
    var MINUTE = 60 * 1000.toLong()
    var HOUR = 1 * 60 * 60 * 1000.toLong()
    var PATTERN_DATE = "HH:mm:ss dd.MM.yyyy"
    fun CompareDate(date: String, delay: Long): Int {
        if (date.isEmpty()) {
            return -1
        }
        var pares_date: Date? = null
        try {
            pares_date = SimpleDateFormat(PATTERN_DATE).parse(date)
            if (null != pares_date) {
                pares_date = Date(pares_date.time + delay)
                return pares_date.compareTo(Date())
            }
        } catch (e: ParseException) {
            //e.printStackTrace();
        }
        return -1


        //
        //Calendar newDate = Calendar.getInstance();
        //Calendar.getTime()
        //newDate.set(year, monthOfYear, dayOfMonth);
        //String text = Data.formatTimeDocs(newDate.getTime());
    }

    fun NowDate(): String {
        val df: DateFormat = SimpleDateFormat(PATTERN_DATE)
        return df.format(Date())
    }

    fun getClearPhone(str: String): String {
        //if (null == str) return "";
        val _ch = charArrayOf('(', ')', '-'/*, '+'*/)
        var ret = str
        for (ch in _ch) {
            ret = ret.replace(ch.toString() + "", "")
        }
        return ret
    }

    fun AnimView(view: View, show: Boolean,
                 @AnimRes id_show: Int = R.anim.top_sheet_slide_in,
                 @AnimRes id_hide: Int = R.anim.top_sheet_slide_out,
                 context: Context = S5Application.application,
                 check_visible: Boolean = false
    ) {
        var anim: Animation? = null
        if (show) {
            if (check_visible && view.visibility == View.VISIBLE) return
            anim = AnimationUtils.loadAnimation(context, id_show)
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                }

                override fun onAnimationStart(animation: Animation?) {
                    view.visibility = View.VISIBLE
                }
            })

        } else {
            if (check_visible && view.visibility == View.GONE) return
            anim = AnimationUtils.loadAnimation(context, id_hide)

            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    view.visibility = View.GONE
                }

                override fun onAnimationStart(animation: Animation?) {
                    view.visibility = View.VISIBLE
                }
            })
        }
//        anim?.fillAfter = true
        view.startAnimation(anim)

    }


    fun SetListViewHeightBasedOnChildren(listView: ListView, inc_height: Int = 0) {
        val listAdapter = listView.getAdapter() ?: return
        var totalHeight = 0
        for (i in 0 until listAdapter.getCount()) {
            val listItem: View = listAdapter.getView(i, null, listView)
            listItem.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }
        val params: ViewGroup.LayoutParams = listView.getLayoutParams()
        params.height = totalHeight + listView.getDividerHeight() * (listAdapter.getCount() - 1) + inc_height
        listView.setLayoutParams(params)
    }

    fun GetClearPhone(str: String): String? {
        //if (null == str) return "";
        val _ch = charArrayOf('(', ')', '-', '+')
        var ret = str
        for (ch in _ch) {
            ret = ret.replace(ch.toString() + "", "")
        }
        return ret
    }


    fun HideKeyboard(activity: Activity) {
        val imm: InputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }



    fun getPath(context: Context, uri: Uri?): String? {
        uri ?: return null

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId: String = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id: String = DocumentsContract.getDocumentId(uri)
                val contentUri: Uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId: String = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                        split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(context: Context, uri: Uri?, selection: String?,
                      selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
                column
        )
        try {
            cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs,
                    null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            if (cursor != null) cursor.close()
        }
        return null
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    fun OpenFlymeSecurityApp(activity: Activity) {
        val intent = Intent()
        intent.action = ACTION_APPLICATION_DETAILS_SETTINGS
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            intent.data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
            activity.startActivity(intent)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

}