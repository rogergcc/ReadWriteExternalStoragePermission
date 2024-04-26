package com.demo.readwriteexternalstoragepermission.ui.utils


/**
 * Created on abril.
 * year 2024 .
 */
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.demo.readwriteexternalstoragepermission.ui.FirstFragment.Companion.IMAGE_DIRECTORY
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class FileStorageManager(private val context: Context) {

    companion object {
        private const val TAG = "FileStorageManager"
    }

    fun saveXmlToExternalStorageDocumentFolder(filename: String, xmlContent: String) {
        try {
            Log.d(TAG, "saveXmlToExternalStorage: init")
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/xml")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_DOCUMENTS + IMAGE_DIRECTORY
                    )
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

            val uri = context.contentResolver.insert(
                MediaStore.Files.getContentUri("external"),
                contentValues
            )

            context.contentResolver.openOutputStream(uri!!)?.use { outputStream ->
                outputStream.write(xmlContent.toByteArray())
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                context.contentResolver.update(uri, contentValues, null, null)
            }
        } catch (ex: Exception) {
            Log.e(TAG, "saveXmlToExternalStorage:Exception ${ex.message}")
        }
    }

    fun saveXmlToExternalStorageSDCard(filename: String, xmlContent: String) {
        try {
            val folderDirectory =
                File(Environment.getExternalStorageDirectory().toString() + IMAGE_DIRECTORY)
            if (!folderDirectory.exists()) {
                val wasDirectoryCreated = folderDirectory.mkdirs()
                if (!wasDirectoryCreated) {
                    Log.d("FileStorageManager", "El directorio no fue creado.")
                } else {
                    Log.d("FileStorageManager", "El directorio fue creado.")
                }
            }

            val fileDestination = File(folderDirectory, filename)
            if (!fileDestination.exists()) {
                val fileCreated = fileDestination.createNewFile()
                if (!fileCreated) {
                    // Failed to create the file
                    throw Exception("Failed to create the file")
                }
            }

            val fos = FileOutputStream(fileDestination)
            fos.write(xmlContent.toByteArray())
            fos.close()
        } catch (e: Exception) {
            Log.e("FileStorageManager", "Error al crear fileDestination: ${e.message}")
        }
    }

    fun saveImageBitmapToSdCard(myBitmap: Bitmap, folderDirectory: File, fileName: String, ): File? {
        try {
            if (!folderDirectory.exists()) {
                throw Exception("createFileDestination1() Folder not exists")
            }

            val fileDestination1 = File(folderDirectory, fileName)
            if (!fileDestination1.exists()) {
                val fileCreated = fileDestination1.createNewFile()
                if (!fileCreated) {
                    // Failed to create the file
                    throw Exception("createFileDestination1() File not exists")
                }
            }
            val fo = FileOutputStream(fileDestination1)
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fo)
            fo.close()
            return fileDestination1

        } catch (e: Exception) {
            Log.e(TAG, "Error al crear fileDestination1: ${e.message}")
        }

        return null
    }

    fun saveImageBitmapToSdCardPrivate(myBitmap: Bitmap, fileName: String): File? {
        ///sdcard/Android/data/com.demo.readwriteexternalstoragepermission/files/Pictures/img_1714161561721.jpg
        try {
            val fileDestination2 =
                File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)

            if (!fileDestination2.exists()) {
                val fileCreated = fileDestination2.createNewFile()
                if (!fileCreated) {
                    // Failed to create the file
                    throw Exception("Failed to create the file")
                }
            } else {
                Log.d(TAG, "createFileDestination2: file2 exists")
            }

            val fo = FileOutputStream(fileDestination2)
            fo.write(ByteArrayOutputStream().apply {
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, this)
            }.toByteArray())
            MediaScannerConnection.scanFile(
                context,
                arrayOf(fileDestination2.path),
                arrayOf("image/jpeg"),
                null
            )
            fo.close()
            return fileDestination2
        } catch (e: Exception) {
            Log.e(TAG, "Error fileDestination2(): ${e.message}")
        }
        return null
    }

    private fun saveImageInPictureFolder(bitmap: Bitmap) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "Image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        uri?.let {
            val outputStream: OutputStream? = context.contentResolver.openOutputStream(it)
            outputStream?.let { os ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                os.close()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                context.contentResolver.update(uri, contentValues, null, null)
            }
        }
    }


}