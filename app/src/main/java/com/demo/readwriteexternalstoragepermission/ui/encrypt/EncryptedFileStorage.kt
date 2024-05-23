package com.demo.readwriteexternalstoragepermission.ui.encrypt

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import com.demo.readwriteexternalstoragepermission.ui.utils.LoggerUtils
import java.io.*


/**
 * Created on abril.
 * year 2024 .
 */
//thanks to https://github.com/BRZ-GmbH/CovidCertificate-App-Android
class EncryptedFileStorage(private val file: File) {
    private lateinit var encryptedFile: EncryptedFile
    private val base64EncoderDecoder = Base64EncoderDecoder()
    private val loggerUtils = LoggerUtils()
    private val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private fun initEncryptedFile(context: Context) {
        encryptedFile = EncryptedFile.Builder(
            file,
            context,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
    }
    fun writeEncryptedData(context: Context, content: String) {
        Log.i(TAG, "write: init")

        deleteFileIfExist(file)

        initEncryptedFile(context)

        val encryptedOutputStream = encryptedFile.openFileOutput()
        try {
            encryptedOutputStream.write(content.encodeToByteArray())
            encryptedOutputStream.flush()

        } catch (ex: Exception) {
            loggerUtils.logError(TAG, "writeEncryptedData(): ex ${ex.message}")
        } finally {
            encryptedOutputStream.close()
        }
    }


    fun readEncryptedData(context: Context): String? {
        if (!file.exists()) return null

        initEncryptedFile(context)

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
            loggerUtils.logError(TAG, "readEncryptedData(): ex ${e.message} ")
            return null
        }
    }

    //region REGION ENCRYPT AND DECRYPT IMAGE


    fun encryptImageFile(context: Context, bitmap: Bitmap): File? {
        try {
            deleteFileIfExist(file)

            initEncryptedFile(context)

            // Convierte el bitmap a un array de bytes
            val bitmapBytes = bitmapToByteArray(bitmap)
                ?: throw Exception("Error at bitmapToByteArray")

            // Escribe los bytes encriptados en el archivo
            writeByteArray(bitmapBytes)

            // Devuelve el archivo encriptado
            return file
        } catch (ex: Exception) {
            loggerUtils.logError(TAG, "encryptImageFile(): ex ${ex.message}")
            return null
        }
    }


    fun decryptImageFile(context: Context): Bitmap? {
        try {

            initEncryptedFile(context)

            // Decryp Image's bytes
            val decryptedImageBytes = decryptByteArray(encryptedFile)
            if (decryptedImageBytes == null || decryptedImageBytes.isEmpty()) {
                Log.e(TAG, "decryptImageFile() Decrypted image bytes are null or empty")
                return null
            }

            // Añade una comprobación para asegurarte de que los bytes representan una imagen
            if (!base64EncoderDecoder.isImage(decryptedImageBytes)) {
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
            Log.e(TAG, "decryptImageFile(): ${ex.message}")
            return null
        }
    }

    private fun writeByteArray(content: ByteArray) {



        val encryptedOutputStream = encryptedFile.openFileOutput()
        try {
            encryptedOutputStream.write(content)
            encryptedOutputStream.flush()
        } catch (ex: IOException) {
            loggerUtils.logError(TAG, "writeByteArray(): ex ${ex.message}")

        } finally {
            encryptedOutputStream.close()
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
            loggerUtils.logError(TAG, "bitmapToByteArray(): ex ${ex.message}")
            return null
        }

    }


    private fun deleteFileIfExist(file: File) {

        if (file.exists()) {
            file.delete()
        }
    }

    private fun decryptByteArray(encryptedFile: EncryptedFile): ByteArray? {
        try {
            val encryptedInputStream = encryptedFile.openFileInput()
            val byteArrayOutputStream = ByteArrayOutputStream()
            var nextByte: Int = encryptedInputStream.read()
            while (nextByte != -1) {
                byteArrayOutputStream.write(nextByte)
                nextByte = encryptedInputStream.read()
            }

            val base64Decoded = base64EncoderDecoder.decodeFromBase64(byteArrayOutputStream.toByteArray())

            return base64Decoded
        } catch (e: IOException) {
            loggerUtils.logError(TAG, "decryptByteArray(): ex ${e.message}")
            return null
        }
    }

    //endregion


    companion object {
        private const val TAG = "EncryptedFileStorage"
    }

}