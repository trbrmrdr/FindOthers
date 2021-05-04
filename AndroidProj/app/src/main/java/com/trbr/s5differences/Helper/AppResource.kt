package com.trbr.s5differences.Helper

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Environment
import android.widget.ImageView
import java.io.*

open class AppResource(val context: Context) {

    // ###########################################################################
    @Throws(IllegalAccessException::class, NoSuchFieldException::class, ClassNotFoundException::class)
    fun getStringId(key: String?): Int {
        return getResourceId(RESOURCE_STRING, key)
    }

    @Throws(IllegalAccessException::class, NoSuchFieldException::class, ClassNotFoundException::class)
    fun getIntegerId(key: String?): Int {
        return getResourceId(RESOURCES_INTEGER, key)
    }

    @Throws(IllegalAccessException::class, NoSuchFieldException::class, ClassNotFoundException::class)
    fun getBooleanId(key: String?): Int {
        return getResourceId(RESOURCES_BOOLEAN, key)
    }

    @Throws(IllegalAccessException::class, NoSuchFieldException::class, ClassNotFoundException::class)
    fun getArrayId(key: String?): Int {
        return getResourceId(RESOURCES_ARRAY, key)
    }

    @Throws(IllegalAccessException::class, NoSuchFieldException::class, ClassNotFoundException::class)
    fun getAnimId(key: String?): Int {
        return getResourceId(RESOURCE_ANIM, key)
    }

    @Throws(IllegalAccessException::class, NoSuchFieldException::class, ClassNotFoundException::class)
    fun getDrawableId(key: String?): Int {
        return getResourceId(RESOURCE_DRAWABLE, key)
    }

    fun getDrawableId(key: String?, defValue: Int): Int {
        val ret: Int
        ret = try {
            getResourceId(RESOURCE_DRAWABLE, key)
        } catch (e: Exception) {
            defValue
        }
        return ret
    }

    @Throws(NoSuchFieldException::class, ClassNotFoundException::class, IllegalArgumentException::class, IllegalAccessException::class)
    fun getResourceId(type: String, key: String?): Int {
        return getResourceId(context, type, key)
    }

    //#################################################################

    fun getRBoolean(key: String?, defValue: Boolean = false): Boolean {
        val ret: Boolean = try {
            val resId = getBooleanId(key)
            context.resources.getBoolean(resId)
        } catch (e: Exception) {
            defValue
        }
        return ret
    }

    fun getRInteger(key: String, defValue: Int = 0): Int {
        val ret = try {
            val resId = getIntegerId(key)
            context.resources.getInteger(resId)
        } catch (e: Exception) {
            defValue
        }
        return ret
    }

    fun getRString(key: String, defValue: String = ""): String {
        val ret = try {
            val resId = getStringId(key)
            context.resources.getString(resId)
        } catch (e: Exception) {
            //Log.e(TAG, "Can't get resource \"" + key + "\"\n", e);
            defValue
        }
        return ret
    }

    // ###########################################################################
    private val NAME_DB = "waadsu_0.2"

    //return mContext.getSharedPreferences(NAME_DB, Context.MODE_PRIVATE);
    private val sharedPreferences: SharedPreferences
    private val editor: SharedPreferences.Editor

    fun clear() {
        editor.clear()
        editor.commit()
    }

    fun set(key: String, value: Any) {
        when (value) {
            is Boolean -> editor.putBoolean(key, value)
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Long -> editor.putLong(key, value)
        }

        editor.commit()
    }

    fun setBool(key: String, value: Boolean) {
        editor.putBoolean(key, value)
        editor.commit()
    }

    fun getBool(key: String, defValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defValue)
    }

    fun setString(name: String?, str: String?) {
        editor.putString(name, str)
        editor.commit()
    }

    fun getString(name: String?, defValue: String? = null): String? {
        return sharedPreferences.getString(name, defValue)
    }

    fun rm(name: String) {
        editor.remove(name)
        editor.commit()
    }

    fun isEmpty(name: String?): Boolean {
        return !sharedPreferences.contains(name)
    }

    fun setInt(name: String, value: Int = 0) {
        editor.putInt(name, value)
        editor.commit()
    }

    fun setFloat(name: String, value: Float = 0F) {
        editor.putFloat(name, value)
        editor.commit()
    }

    fun setLong(name: String, value: Long = 0) {
        editor.putLong(name, value)
        editor.commit()
    }

    fun getInt(name: String, defValue: Int = -1): Int {
        return sharedPreferences.getInt(name, defValue)
    }

    fun getLong(name: String, defValue: Long = -1): Long {
        return sharedPreferences.getLong(name, defValue)
    }

    fun getFloat(name: String, defValue: Float = 0F): Float {
        return sharedPreferences.getFloat(name, defValue)
    }

    /*  fun getStringArray(name: String): Array<String> {
          val set = sharedPreferences.getStringSet(name, null) ?: return arrayOf()
          return set.toTypedArray()
      }*/

    /*fun addInArray(key: String, value: Int) {
        var set = sharedPreferences.getStringSet(key, null) ?: HashSet()
        set.add(value.toString())
        editor.putStringSet(key, set)
        editor.commit()
    }

    fun rmInArray(name: String, value: Int) {
        val set = sharedPreferences.getStringSet(name, null) ?: return
        set.remove(value.toString())
        editor.putStringSet(name, set)
        val ret = editor.commit()
        return
    }*/

    //_____________________________________________________________________________

    fun sizeArray(key: String): Int {
        return sharedPreferences.getInt("${key}_size", 0)
    }

    fun pullFromArray(key: String, clear: Boolean = true): String? {
        var size = sharedPreferences.getInt("${key}_size", 0)
        do {
            if (size == 0) return null

            val ret = sharedPreferences.getString("${key}_${size - 1}", null)
            if (ret == null || clear) {
                editor.putInt("${key}_size", size - 1)
                editor.remove("${key}_${size - 1}")
                editor.commit()

                if (ret != null && clear)
                    return ret

                //ToDo remove
                if (ret == null && clear) {
                    size = size
                }
                size -= 1
                continue
            }
            return ret
        } while (true)
    }

    fun getFromArray(key: String, pos: Int = -1): String? {
        var t_pos = pos
        val size = sharedPreferences.getInt("${key}_size", 0)
        if (t_pos == -1) t_pos = size - 1
        if (size == 0 || t_pos >= size) return null
        val ret = sharedPreferences.getString("${key}_${t_pos}", null)
        return ret
    }

    //ToDo - надо пересчитать индексы есл иудаляем из середины
    /*fun rmInArray(key: String, pos: Int = -1): Boolean {
        var t_pos = pos
        val size = sharedPreferences.getInt("${key}_size", 0)
        if (t_pos == -1) t_pos = size - 1
        if (size == 0 || t_pos >= size) return false
        editor.putInt("${key}_size", size - 1)
        editor.remove("${key}_${t_pos}")
        return editor.commit()
    }*/

    fun addInArray(key: String, value: String): Int {
        return addInArray(key, arrayOf<String?>(value))
    }

    fun addInArray(key: String, new_arr: Array<String?>): Int {
        val old = pullAllArray(key, false).toMutableList()
        new_arr.forEach { s -> old.add(s) }
        return setArray(key, old.toTypedArray())
    }

    fun setArray(key: String, array: Array<String?>?): Int {
        var size = 0
        if (array == null) {
            editor.putInt("${key}_size", size)
        } else {
            size = array.size
            editor.putInt("${key}_size", size)
            for (i in array.indices) editor.putString("${key}_$i", array[i])
        }
        if (!editor.commit())
            return -1
        return size
    }

    fun pullAllArray(key: String, clear: Boolean = true): Array<String?> {
        val size = sharedPreferences.getInt("${key}_size", 0)
        val array = arrayOfNulls<String>(size)
        for (i in 0 until size) {
            array[i] = sharedPreferences.getString("${key}_$i", null)
            if (clear) editor.remove("${key}_$i")
        }
        if (clear) {
            editor.putInt("${key}_size", 0)
            editor.commit()

        }
        return array
    }

    //Images - Картинки
    //_______________________________________________________________________________________
    /*fun rmImages(name: String, images: ArrayList<ImageLoader?>) {
        val editor = editor
        editor.putInt(name, images.size)
        editor.commit()
        val path = GetFileCache(context)!!.absolutePath + "/" + name + "/"
        val dirs = File(path)
        val test = false
        if (!dirs.exists()) return
        if (dirs.delete()) return
        val path_s = dirs.absolutePath + "/image_"
        var i = -1
        for (image in images) {
            ++i
            File(path_s + i).delete()
        }
    }

    fun setImages(name: String, images: ArrayList<ImageLoader>) {
        val editor = editor
        editor.putInt(name, images.size)
        editor.commit()
        val path = GetFileCache(context)!!.absolutePath + "/" + name + "/"
        val dirs = File(path)
        var test = false
        if (!dirs.exists()) test = dirs.mkdirs()
        val path_s = dirs.absolutePath + "/image_"
        var i = -1
        for (image in images) {
            ++i
            val bitmap = GetImage(image.getImageView())
            if (null == bitmap) {
                File(path_s + i).delete()
            } else {
                SaveImageFile(bitmap, path_s + i)
            }
        }
    }
*/
    /*   fun getImages(name: String): ArrayList<Bitmap> {
           val bitmaps = ArrayList<Bitmap>()
           val sharedPref = sharedPreferences
           val count = sharedPref.getInt(name, -1)
           if (-1 == count) return bitmaps
           val path = GetFileCache(context)!!.absolutePath + "/" + name + "/"
           for (i in 0 until count) {
               val bitmap = BitmapFactory.decodeFile("$path/image_$i")
               bitmaps.add(bitmap)
           }
           return bitmaps
       }

       fun rmImages(name: String) {
           val file = File(GetFileCache(context)!!.absolutePath + "/" + name)
           file.delete()
       }

       fun addImage(bitmap: Bitmap, name: String) {
           val path = GetFileCache(context)!!.absolutePath + "/" + name
           SaveImageFile(bitmap, path)
       }

       fun getImage(name: String): Bitmap {
           val path = GetFileCache(context)!!.absolutePath + "/" + name
           return BitmapFactory.decodeFile(path)
       }*/


    val cachePwd: File

    init {
        cachePwd = iniFileDir(context, "cache")
    }

    private fun iniFileDir(context: Context, dir_name: String): File {
        var ret = if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)
            File(Environment.getExternalStorageDirectory(), "Android/data/${context.packageName}/${dir_name}/")
        else context.cacheDir
        try {
            ret.mkdirs()
        } catch (ex: java.lang.Exception) {

        }
        return ret
    }

    fun copyToLFile(src_path: String, file_name: String, cache_dir: String = "cache"): File? {
        var ret_f: File? = null
        try {
            val dst_file = File(file_path(file_name, format(src_path), cache_dir))
            ret_f = File(src_path).copyTo(dst_file, true)
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
        return ret_f
    }

    fun format(path_to_file: String): String {
        return path_to_file.substringAfterLast('.', "")
    }

    fun file_path(file_name: String, format: String, sub_dir: String = ""): String {
        return (cachePwd.absolutePath + File.separator + sub_dir).also { File(it).mkdirs() } + File.separator + file_name + "." + format
    }

    fun readFile(fileName: String?): String {
        return ReaadFile(fileName)
    }

    fun writeFile(fileContents: String?, fileName: String?) {
        WriteFile(context, fileContents, fileName)
    }

    companion object {
        private const val TAG = "AppResource"
        private const val RESOURCE_STRING = "string"
        private const val RESOURCE_DRAWABLE = "drawable"
        private const val RESOURCE_ANIM = "anim"
        private const val RESOURCES_INTEGER = "integer"
        private const val RESOURCES_BOOLEAN = "bool"
        private const val RESOURCES_ARRAY = "array"

        @Throws(NoSuchFieldException::class, ClassNotFoundException::class, IllegalArgumentException::class, IllegalAccessException::class)
        fun getResourceId(context: Context, type: String, key: String?): Int {
            val rc = context.packageName + ".R$" + type
            val f = Class.forName(rc).getField(key!!)
            return f.getInt(null)
        }

        fun SaveImageFile(bitmap: Bitmap, file_name: String?): Boolean {
            try {
                val out = FileOutputStream(file_name)
                CompressBitmap(bitmap, out)
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
            return true
        }


        fun RmRecursive(fileOrDirectory: File?) {
            fileOrDirectory ?: return
            if (fileOrDirectory.isDirectory) for (child in fileOrDirectory.listFiles()) RmRecursive(child)
            fileOrDirectory.delete()

//            if (fileOrDirectory.absolutePath == cacheDir?.absolutePath) {
//                cacheDir = null
//            }
        }

        //###########################################################################################
        fun ReadAssetFile(context: Context, fileName: String?): String {
            val returnString = StringBuilder()
            var fIn: InputStream? = null
            var isr: InputStreamReader? = null
            var input: BufferedReader? = null
            try {
                fIn = context.resources.assets.open(fileName!!, Context.MODE_WORLD_READABLE)
                isr = InputStreamReader(fIn)
                input = BufferedReader(isr)
                var line = ""
                while (input.readLine().also { line = it } != null) {
                    returnString.append(line)
                }
            } catch (e: Exception) {
                e.message
            } finally {
                try {
                    isr?.close()
                    fIn?.close()
                    input?.close()
                } catch (e2: Exception) {
                    e2.message
                }
            }
            return returnString.toString()
        }

        fun ReaadFile(fileName: String?): String {
            var ret = ""
            try {
                val br = BufferedReader(FileReader(fileName))
                var strLine: String
                while (br.readLine().also { strLine = it } != null) {
                    ret += strLine
                }
                br.close()
            } catch (e: IOException) {
                //Log.e("notes_err", e.getLocalizedMessage());
            }
            return ret

//        try {
//            InputStream inputStream = context.openFileInput(fileName);
//
//            if (inputStream != null) {
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//
//                int size = inputStream.available();
//                char[] buffer = new char[size];
//
//                inputStreamReader.read(buffer);
//
//                inputStream.close();
//                ret = new String(buffer);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return ret;
        }

        fun WriteFile(context: Context?, fileContents: String?, fileName: String?) {
            try {
                val bw = BufferedWriter(FileWriter(fileName))
                bw.write(fileContents)
                bw.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }


//        FileOutputStream fos = null;
//        try {
//            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
//            fos.write(fileContents.getBytes());
//            fos.flush();
//            fos.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (fos != null) {
//                fos = null;
//            }
//        }
        }

        //##############################################################################################
        fun GetIntArray(context: Context, array_strings_id: Int): Array<Int?> {
            return GetIntArray(context.resources.getStringArray(array_strings_id))
        }

        fun GetFloatArray(context: Context, array_strings_id: Int): FloatArray {
            return GetFloatArray(context.resources.getStringArray(array_strings_id))
        }

        fun GetFloatArray(strings: Array<String>): FloatArray {
            val ret = FloatArray(strings.size)
            for (i in ret.indices) {
                ret[i] = strings[i].toFloat()
            }
            return ret
            //return Arrays.stream(strings).map(Float::valueOf).toArray(Float[]::new);
            //return Arrays.stream(strings).mapToDouble(Float::parseFloat).toArray();
        }

        fun GetIntArray(strings: Array<String>): Array<Int?> {
            val ret = arrayOfNulls<Int>(strings.size)
            for (i in ret.indices) {
                ret[i] = strings[i].toInt()
            }
            return ret
        }

        fun GetImage(imageView: ImageView): Bitmap {
            val bitmapDrawable = imageView.drawable as BitmapDrawable
            val bitmap: Bitmap
            if (bitmapDrawable == null) {
                imageView.buildDrawingCache()
                bitmap = imageView.drawingCache
                imageView.buildDrawingCache(false)
            } else {
                bitmap = bitmapDrawable.bitmap
            }
            return bitmap
        }

        fun CompressBitmap(bitmap: Bitmap, outputStream: OutputStream?) {
            //bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); last old
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream) //last 75
        }
    }

    init {
        sharedPreferences = context.getSharedPreferences(NAME_DB, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }
}