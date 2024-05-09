package com.demo.readwriteexternalstoragepermission.utils.files


/**
 * Created on mayo.
 * year 2024 .
 */
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import com.demo.readwriteexternalstoragepermission.utils.AppContants
import com.demo.readwriteexternalstoragepermission.utils.AppUtils
import com.demo.readwriteexternalstoragepermission.utils.ImageUtils.convertBitmapToString
import com.demo.readwriteexternalstoragepermission.utils.ImageUtils.convertStringToBitmap
import java.io.File
import java.io.FileOutputStream

class ImageFileManager(private val context: Context, private val directory: String) :
    FileOperations {
    override fun save(filename: String, content: String) {

        val folderDirectory = AppUtils.folderNamePath(directory)
        if (!folderDirectory.exists()) {
            val wasDirectoryCreated = folderDirectory.mkdirs()
            if (!wasDirectoryCreated) {
                Log.d(TAG, "Folder directory not created.")
                throw Exception("Folder not created")
            }
        }

        val bitmap = convertStringToBitmap(content)
//        val file = File(context.getExternalFilesDir(directory), filename)
//        val file = File(Environment.getExternalStorageDirectory().toString() + directory, filename)
        val file = AppUtils.fileCreate( filename)
        val outputStream = FileOutputStream(file)
        try {
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

        } catch (ex: Exception) {
            Log.e(TAG, "error save(): ${ex.message}")
            throw Exception("[$TAG] ${ex.message}")
        } finally {
            outputStream.close()
        }


    }

    override fun read(filename: String): String {
//        val file = File(context.getExternalFilesDir(directory), filename)
//        val file = File(Environment.getExternalStorageDirectory().toString() + directory, filename)
        val file = AppUtils.fileCreate( filename)

        try {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            return convertBitmapToString(bitmap)
        }catch (ex : Exception){
            Log.e(TAG, "error read(): ${ex.message}")
            throw Exception("[$TAG] ${ex.message}")
        }


    }

//    private fun stringToBitmap(string: String): Bitmap? {
//        val bytes = Base64.decode(string, Base64.DEFAULT)
//        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
//    }

    //    private fun bitmapToString(bitmap: Bitmap): String {
//        val byteArrayOutputStream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
//        val byteArray = byteArrayOutputStream.toByteArray()
//        return Base64.encodeToString(byteArray, Base64.DEFAULT)
//    }
    companion object {
        private const val TAG = "ImageFileManager"
    }
}