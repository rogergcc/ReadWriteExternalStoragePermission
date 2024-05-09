package com.demo.readwriteexternalstoragepermission.utils

import android.content.Context
import android.os.Environment
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import java.io.File
import java.util.*


/**
 * Created on abril.
 * year 2024 .
 */
object AppUtils {
    fun coloredText(context: Context, text: String, colorId: Int): SpannableStringBuilder {
        val spannable = SpannableStringBuilder(text)
        val color: Int = ContextCompat.getColor(context, colorId)
        spannable.setSpan(
            ForegroundColorSpan(color), 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
    }

    fun getCalendarInstanceName() = Calendar.getInstance().timeInMillis.toString().replace(":", ".")

    fun fileNameImageSdPublic() = "ImageSdPublic"
    fun fileNameXmlSdPublic() = "XmlSdPublic"
    fun fileNameImageSdPrivate() = "ImageSdPrivate"
    fun xmlContentSample() = """
            <?xml version="1.0" encoding="UTF-8"?>
            <note>
                <to>Tove</to>
                <from>Jani</from>
                <heading>Reminder</heading>
                <body>Don't forget me this weekend!</body>
            </note>
        """.trimIndent()

    fun folderNamePath(imageDirectory: String = AppContants.IMAGE_DIRECTORY): File {
        //sdcard/$name or //storage/emulted/0/$name
        return File(Environment.getExternalStorageDirectory().toString() + imageDirectory)
    }

    fun fileCreate(fileName:String): File {
        //sdcard/IMAGE_DIRECTORY/$fileName or //storage/emulted/0/IMAGE_DIRECTORY/$fileName
        return File(Environment.getExternalStorageDirectory().toString() + AppContants.IMAGE_DIRECTORY,fileName)
    }

}