package com.demo.readwriteexternalstoragepermission.ui.encrypt

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import java.io.ByteArrayOutputStream
import java.io.File


/**
 * Created on abril.
 * year 2024 .
 */
//thanks to https://github.com/BRZ-GmbH/CovidCertificate-App-Android
class EncryptedFileStorage(private val file: File) {
    private val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    fun write(context: Context, content: String) {
        Log.i(TAG, "write: init")
//        Log.i(TAG, "write: context.filesDir: ${context.filesDir}") ///data/user/0/com.demo.readwriteexternalstoragepermission/files

        if (file.exists()) {
            file.delete()
        }

        val encryptedFile = EncryptedFile.Builder(
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

    fun writeByteArray(context: Context, content: ByteArray) {
        val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        if (file.exists()) {
            file.delete()
        }

        val encryptedFile = EncryptedFile.Builder(
            file,
            context,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        val encryptedOutputStream = encryptedFile.openFileOutput()
        try {
            encryptedOutputStream.write(content)
            encryptedOutputStream.flush()
        } catch (ignored: Exception) {
            Log.e(TAG, "writeByteArray: error: ${ignored.message}")
        } finally {
            encryptedOutputStream.close()
        }
    }

    fun read(context: Context): String? {
        Log.i(TAG, "read(): init")
        if (!file.exists()) return null
        Log.i(TAG, "read() file: ${file.path}")
        val encryptedFile: EncryptedFile = EncryptedFile.Builder(
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

    fun readByteArray(context: Context): ByteArray? {
        val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        if (!file.exists()) return null

        val encryptedFile: EncryptedFile = EncryptedFile.Builder(
            file,
            context,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        return try {
            val encryptedInputStream = encryptedFile.openFileInput()
            val byteArrayOutputStream = ByteArrayOutputStream()
            var nextByte: Int = encryptedInputStream.read()
            while (nextByte != -1) {
                byteArrayOutputStream.write(nextByte)
                nextByte = encryptedInputStream.read()
            }
            byteArrayOutputStream.toByteArray()
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        private const val TAG = "EncryptedFileStorage"
    }

}