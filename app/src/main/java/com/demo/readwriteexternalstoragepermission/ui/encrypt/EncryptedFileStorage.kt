package com.demo.readwriteexternalstoragepermission.ui.encrypt

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKeys
import java.io.*
import java.security.GeneralSecurityException


/**
 * Created on abril.
 * year 2024 .
 */
//thanks to https://github.com/BRZ-GmbH/CovidCertificate-App-Android
class EncryptedFileStorage(private val file: File) {
    private lateinit var encryptedFile: EncryptedFile
    private lateinit var masterKey: MasterKey

    private val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    fun write(context: Context, content: String) {
        Log.i(TAG, "write: init")
//        Log.i(TAG, "write: context.filesDir: ${context.filesDir}") ///data/user/0/com.demo.readwriteexternalstoragepermission/files

        if (file.exists()) {
            file.delete()
        }

         encryptedFile = EncryptedFile.Builder(
            file,
            context,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        val encryptedOutputStream = encryptedFile.openFileOutput()
        try {
            encryptedOutputStream.write(content.encodeToByteArray())
            encryptedOutputStream.flush()

        } catch (ignored: Exception) {
            Log.e(TAG, "write: error ${ignored.message}")
        } finally {
            encryptedOutputStream.close()
        }
    }

    fun read(context: Context): String? {
        Log.i(TAG, "read(): init")
        if (!file.exists()) return null
        Log.i(TAG, "read() file: ${file.path}")
         encryptedFile = EncryptedFile.Builder(
            file,
            context,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        try {
            val encryptedInputStream = encryptedFile.openFileInput()
            val byteArrayOutputStream = ByteArrayOutputStream()
            var nextByte: Int = encryptedInputStream.read()
            while (nextByte != -1) {
                byteArrayOutputStream.write(nextByte)
                nextByte = encryptedInputStream.read()
            }
            val bytes: ByteArray = byteArrayOutputStream.toByteArray()
            return bytes.decodeToString()
        } catch (e: Exception) {
            Log.e(TAG, "read: ex ${e.message}" )
            return null
        }
    }

    fun encryptImageFile(context: Context, bitmap: Bitmap): File? {
        try {
//            if (file.exists()) {
//                file.delete()
//            }

            // Convierte el bitmap a un array de bytes
            val bitmapBytes = bitmapToByteArray(bitmap)
                ?: throw Exception("Error at bitmapToByteArray")


            // Escribe los bytes encriptados en el archivo
            writeByteArray(context, bitmapBytes)

            // Devuelve el archivo encriptado
            return file
        } catch (ex: Exception) {
            Log.e(TAG, "encryptImageFile() ${ex.message}")
            return null
        }
    }


    fun decryptImageFile(context: Context): Bitmap? {
        try {
            encryptedFile = EncryptedFile.Builder(
                file,
                context,
                masterKeyAlias,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()

            // Desencripta los bytes de la imagen
            val decryptedImageBytes = decryptByteArray(context,encryptedFile)
            if (decryptedImageBytes == null || decryptedImageBytes.isEmpty()) {
                Log.e(TAG, "decryptImageFile() Decrypted image bytes are null or empty")
                return null
            }

            // Añade una comprobación para asegurarte de que los bytes representan una imagen
            if (!isImage(decryptedImageBytes)) {
                Log.e(TAG, "decryptImageFile() Decrypted image bytes do not represent a valid image")
                return null
            }

            val bitmap = BitmapFactory.decodeByteArray(
                decryptedImageBytes,
                0,
                decryptedImageBytes.size
            ) ?: throw Exception("Error Null Bitmap")

            return bitmap
        } catch (ex: Exception) {
            Log.e(TAG, "decryptImageFile() ${ex.message}")
            return null
        }
    }

    @Throws(GeneralSecurityException::class, IOException::class)
    fun encrypt(context: Context, target: File, bitmap: Bitmap) {
        if (file.exists()) {
            file.delete()
        }
        val mainKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val encryptedFile = EncryptedFile.Builder(
            context,
            file,
            mainKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        val encryptedOutputStream = encryptedFile.openFileOutput()
        try {
            val bitmapBytes = bitmapToByteArray(bitmap)
            val base64Encoded = Base64.encodeToString(bitmapBytes, Base64.DEFAULT)
            encryptedOutputStream.write(base64Encoded.toByteArray())
            encryptedOutputStream.flush()

        } catch (ignored: Exception) {
            Log.e(TAG, "encrypt: error ${ignored.message}")
        } finally {
            encryptedOutputStream.close()
        }
    }

    @Throws(GeneralSecurityException::class, IOException::class)
    fun decrypt(context: Context, target: File): Bitmap? {
        val mainKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val file = EncryptedFile.Builder(
            context,
            file,
            mainKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()


        val inputStream: InputStream = file.openFileInput()
        val byteArrayOutputStream = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead)
        }

        val byteArrayToBitmap= byteArrayToBitmap(byteArrayOutputStream.toByteArray())

        return byteArrayToBitmap
    }



    // Función para comprobar si los bytes representan una imagen
    private fun isImage(bytes: ByteArray): Boolean {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        return options.outWidth > 0 && options.outHeight > 0
    }

    @Throws(GeneralSecurityException::class, IOException::class)
    fun encryptToFile(context: Context, contents: ByteArray?) {
        val encryptedFile = EncryptedFile.Builder(
            file,
            context,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
        val outputStream: OutputStream = encryptedFile.openFileOutput()
        outputStream.write(contents)
        outputStream.flush()
        outputStream.close()
    }

    private fun writeByteArray(context: Context, content: ByteArray) {
        if (file.exists()) {
            file.delete()
        }
        encryptedFile = EncryptedFile.Builder(
            file,
            context,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        val encryptedOutputStream = encryptedFile.openFileOutput()
        try {
            encryptedOutputStream.write(content)
            encryptedOutputStream.flush()
        } catch (ex: IOException) {
            Log.e(TAG, "writeByteArray: error: ${ex.message}")
        } finally {
            encryptedOutputStream.close()
        }
    }



    @Throws(GeneralSecurityException::class, IOException::class)
    fun decryptFile(context: Context, target: File): ByteArray? {

        val mainKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val encryptedFile = EncryptedFile.Builder(
            context,
            file,
            mainKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
        val inputStream: InputStream = encryptedFile.openFileInput()
        val byteArrayOutputStream = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead)
        }
        return byteArrayOutputStream.toByteArray()
    }
    @Throws(GeneralSecurityException::class, IOException::class)
    fun encryptToFile(context: Context, pathToSave: File, contents: ByteArray) {
        if (pathToSave.exists()) {
            pathToSave.delete()
        }

        val encryptedFile = EncryptedFile.Builder(
            pathToSave,
            context,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        val encryptedOutputStream = encryptedFile.openFileOutput()
        try {
            encryptedOutputStream.write(contents)
            encryptedOutputStream.flush()

        } catch (ignored: Exception) {
            Log.e(TAG, "encryptToFile: error ${ignored.message}")
        } finally {
            encryptedOutputStream.close()
        }

//        val outputStream: OutputStream = encryptedFile.openFileOutput()
//        outputStream.write(contents)
//        outputStream.flush()
//        outputStream.close()
    }

    private fun decryptByteArray(context: Context, encryptedFile: EncryptedFile): ByteArray? {
        try {
            val encryptedInputStream = encryptedFile.openFileInput()
            val byteArrayOutputStream = ByteArrayOutputStream()
            var nextByte: Int = encryptedInputStream.read()
            while (nextByte != -1) {
                byteArrayOutputStream.write(nextByte)
                nextByte = encryptedInputStream.read()
            }
            val base64Decoded = Base64.decode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
            return base64Decoded
        } catch (e: IOException) {
            Log.e(TAG, "decryptByteArray: ex ${e.message}" )
            return null
        }
    }

    fun readByteArray(context: Context, inputImageFile: File): ByteArray? {
        Log.i(TAG, "readByteArray(): init")

        val encryptedFile: EncryptedFile = EncryptedFile.Builder(
            inputImageFile,
            context,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

         try {
            val encryptedInputStream = encryptedFile.openFileInput()
            val byteArrayOutputStream = ByteArrayOutputStream()
            var nextByte: Int = encryptedInputStream.read()
            while (nextByte != -1) {
                byteArrayOutputStream.write(nextByte)
                nextByte = encryptedInputStream.read()
            }
            return byteArrayOutputStream.toByteArray()
         } catch (e: IOException) {
             Log.e(TAG, "readByteArray: ex ${e.message}" )
             return null
         }
    }
    fun byteArrayToBitmap(bytes: ByteArray?): Bitmap? {
        try {
            val data = Base64.decode(bytes, Base64.DEFAULT)
            val bitmapDecode= BitmapFactory.decodeByteArray(data, 0, data.size)
            if (bitmapDecode==null){
                throw Exception("Error at decode")
            }
            return bitmapDecode
        }catch (ex:Exception){
            Log.e(TAG, "byteArrayToBitmap: ${ex.message}" )
            return null
        }
    }
    fun encryptByteArray(context: Context, bitmapBytes: ByteArray): ByteArray? {
        try {
            // Convierte el bitmap a un array de bytes
            val base64Encoded = Base64.encodeToString(bitmapBytes, Base64.DEFAULT)
            return base64Encoded.toByteArray()
        } catch (ex: Exception) {
            Log.e(TAG, "encryptByteArray() ${ex.message}")
            return null
        }
    }
    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray? {
        try {
            val stream = ByteArrayOutputStream()
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val base64Encoded = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
                ?: throw Exception("Error at encodeToString")

            return base64Encoded.toByteArray()
        }catch (ex:Exception){
            Log.e(TAG, "bitmapToByteArray: ${ex.message}" )
            return null
        }

    }


    private fun deleteFileIfExist(file: File) {

        if (file.exists()) {
            file.delete()
        }
    }

    fun saveEncryptedImage(context: Context,imageUri: Uri) {
//        deleteFileIfExist(file)
        val encryptedFile = EncryptedFile.Builder(
            file,
            context,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        val imageInputStream = context.contentResolver.openInputStream(imageUri)
        writeFile(encryptedFile.openFileOutput(), imageInputStream)
    }

    private fun writeFile(outputStream: FileOutputStream, inputStream: InputStream?) {
        outputStream.use { output ->
            inputStream.use { input ->
                input?.let {
                    val buffer =
                        ByteArray(4 * 1024) // buffer size
                    while (true) {
                        val byteCount = input.read(buffer)
                        if (byteCount < 0) break
                        output.write(buffer, 0, byteCount)
                    }
                    output.flush()
                }
            }
        }
    }
    companion object {
        private const val TAG = "EncryptedFileStorage"
    }

}