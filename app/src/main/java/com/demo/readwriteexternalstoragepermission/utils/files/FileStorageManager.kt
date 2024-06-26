package com.demo.readwriteexternalstoragepermission.utils.files


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
import com.demo.readwriteexternalstoragepermission.utils.AppContants
import com.demo.readwriteexternalstoragepermission.utils.AppUtils
import com.demo.readwriteexternalstoragepermission.utils.ImageUtils.convertBitmapToString
import com.demo.readwriteexternalstoragepermission.utils.ImageUtils.convertStringToBitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class FileStorageManager(private val context: Context) {
    private val xmlFileManager = XmlFileManager(context, AppContants.IMAGE_DIRECTORY)
    private val imageFileManager = ImageFileManager(context, AppContants.IMAGE_DIRECTORY)

    companion object {
        private const val TAG = "FileStorageManager"
    }

    fun saveXmlFileToSdCard(filename: String, xmlContent: String) {
        xmlFileManager.save(filename, xmlContent)
    }

    fun readXmlFromExternalStorageSDCard(filename: String): String? {

        try {
            return xmlFileManager.read(filename)
        } catch (ex: Exception) {
            Log.e(TAG, "error readXmlFromExternalStorageSDCard: ${ex.message}")
            throw ex
//            return null
        }


    }

    fun saveImageBitmapToSdCard(myBitmap: Bitmap, fileName: String) {
        // Convertir el Bitmap a String y guardar
        val content = convertBitmapToString(myBitmap)
        imageFileManager.save(fileName, content)
    }

    fun readImageBitmapFromSdCard(filename: String): Bitmap? {
        // Leer el contenido y convertirlo a Bitmap

        try {
            val content = imageFileManager.read(filename)
            return convertStringToBitmap(content)
        } catch (ex: Exception) {
            Log.e(TAG, "error readImageBitmapFromSdCard: ${ex.message}")
            throw ex
//            return null
        }

    }



    fun saveXmlToExternalStorageDocumentFolder(filename: String, xmlContent: String) {
        try {
            //save in documents folder
            val folderDirectory = AppUtils.folderNamePath(AppContants.IMAGE_DIRECTORY)
            if (!folderDirectory.exists()) {
                val wasDirectoryCreated = folderDirectory.mkdirs()
                if (!wasDirectoryCreated) {
                    Log.d(TAG, "Folder directory not created.")
                    throw Exception("Folder not exists")
                }
            }

            Log.d(TAG, "saveXmlToExternalStorage: init")
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/xml")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_DOCUMENTS + AppContants.IMAGE_DIRECTORY
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

    //region SAVEING IN SDCARD EXTERNAL

//    fun saveXmlFileToSdCard(filename: String, xmlContent: String) {
//        // sdcard/abc_test
//        try {
//
//            val folderDirectory = folderNamePath(getImageDirectory())
//
//            if (!folderDirectory.exists()) {
//                val wasDirectoryCreated = folderDirectory.mkdirs()
//                if (!wasDirectoryCreated) {
//                    throw Exception("Folder directory not created")
//                }
//            }
//
//            val file = File(folderDirectory, filename)
//            if (!file.exists()) {
//                val fileCreated = file.createNewFile()
//                if (!fileCreated) {
//                    // Failed to create the file
//                    throw Exception("Failed to create the file")
//                }
//            }
//
//            //TODO create file normal
////            val fos = FileOutputStream(fileDestination)
////            fos.write(xmlContent.toByteArray())
////            fos.close()
//
//            //TODO create file encripted
//            // Create an instance of EncryptedFileStorage
//            val encryptedFileStorage = EncryptedFileStorage(file)
//
//            // Use EncryptedFileStorage to write the content to the file
//            encryptedFileStorage.write(context, xmlContent)
//
//
//
//        } catch (e: Exception) {
//            Log.e("FileStorageManager", "Error al crear fileDestination: ${e.message}")
//        }
//
//    }
//
//    fun saveImageBitmapToSdCard(myBitmap: Bitmap, fileName: String): File? {
//        // sdcard/abc_test
//        try {
//            val folderDirectory = folderNamePath(getImageDirectory())
//
//            if (!folderDirectory.exists()) {
//                val wasDirectoryCreated = folderDirectory.mkdirs()
//                if (!wasDirectoryCreated) {
//                    throw Exception("Folder directory not created")
//                }
//            }
//
//            val fileDestination1 = File(folderDirectory, fileName)
//            Log.i(TAG,"Ubication: ${fileDestination1.absolutePath}")
//            if (!fileDestination1.exists()) {
//                val fileCreated = fileDestination1.createNewFile()
//                if (!fileCreated) {
//                    // Failed to create the file
//                    throw Exception("File not exists")
//                }
//            }
//            val fo = FileOutputStream(fileDestination1)
//            myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fo)
//            fo.close()
//            return fileDestination1
//
//        } catch (e: Exception) {
//            Log.e(TAG, "Error en save Image SD(): ${e.message}")
//        }
//
//        return null
//    }
//
//    //endregion
//
//    fun readXmlFromExternalStorageSDCard(filename: String): String? {
//        try {
//            val folderDirectory = folderNamePath(getImageDirectory())
//            val file = File(folderDirectory, filename)
//
//            if (!file.exists()) {
////                Log.e(TAG, "El archivo $filename no existe.")
//                throw Exception("El archivo $filename no existe.")
//            }
//            //todo read file normal
////            file.readText()
//
//            //todo read file encripted
//            // Create an instance of EncryptedFileStorage
//            val encryptedFileStorage = EncryptedFileStorage(file)
//            // Use EncryptedFileStorage to read the content from the file
//            return encryptedFileStorage.read(context)
//
//
//
//        }catch (ex: Exception){
////            Log.e(TAG,"error readxml: ${ex.message}")
//            return "read xml error: ${ex.message}"
//        }
//
//    }
//
//    fun readImageBitmapFromSdCard(filename: String): Bitmap? {
//        try {
//            val folderDirectory = folderNamePath(getImageDirectory())
//            val file = File(folderDirectory, filename)
//            if (!file.exists()) {
////                Log.e(TAG, "El archivo $filename no existe.")
//                throw Exception("El archivo $filename no existe.")
//            }
//            return BitmapFactory.decodeFile(file.absolutePath)
//        }catch (ex: Exception){
//            Log.e(TAG,"error read Image: ${ex.message}")
//            return null
//        }
//    }


    fun saveImageBitmapToSdCardPrivate(myBitmap: Bitmap, fileName: String): File? {
        ///sdcard/Android/data/com.demo.readwriteexternalstoragepermission/files/Pictures/img_1714161561721.jpg
        try {
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)

            if (!file.exists()) {
                val fileCreated = file.createNewFile()
                if (!fileCreated) {
                    // Failed to create the file
                    throw Exception("Failed to create the file")
                }
            }


            val fo = FileOutputStream(file)
            fo.write(ByteArrayOutputStream().apply {
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, this)
            }.toByteArray())
            MediaScannerConnection.scanFile(
                context,
                arrayOf(file.path),
                arrayOf("image/jpeg"),
                null
            )
            fo.close()
            return file

        } catch (e: Exception) {
            Log.e(TAG, "Error save Sd private(): ${e.message}")
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