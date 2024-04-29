package com.demo.readwriteexternalstoragepermission.ui.utils

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
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
            ForegroundColorSpan(color),
            0,
            text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
    }
    fun getCalendarInstanceName() =
        Calendar.getInstance().timeInMillis.toString().replace(":", ".")
}