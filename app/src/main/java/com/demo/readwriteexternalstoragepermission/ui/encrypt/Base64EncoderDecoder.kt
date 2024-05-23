package com.demo.readwriteexternalstoragepermission.ui.encrypt

import android.graphics.BitmapFactory
import android.util.Base64


/**
 * Created on mayo.
 * year 2024 .
 */
class Base64EncoderDecoder {
    fun encodeToBase64(bytes: ByteArray): ByteArray {
        val base64Encoded = Base64.encodeToString(bytes, Base64.DEFAULT)
        return base64Encoded.toByteArray()
    }

    fun decodeFromBase64(bytes: ByteArray): ByteArray {
        val base64Decoded = Base64.decode(bytes, Base64.DEFAULT)
        return base64Decoded
    }

    fun isImage(bytes: ByteArray): Boolean {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        return options.outWidth > 0 && options.outHeight > 0
    }

}