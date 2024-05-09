package com.demo.readwriteexternalstoragepermission.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity


fun String.highlight(): Spanned {
    var high = this.replace("<([^>/]*)/>".toRegex(), "&lt;~blue~$1~/~/&gt;")
    high = high.replace("<([^>]*)>".toRegex(), "&lt;~green~$1~/~&gt;")
    high = high.replace("([\\w]+)=\"([^\"]*)\"".toRegex(), "~red~$1~/~~black~=\"~/~~green~$2~/~~black~\"~/~")
    high = high.replace("~([a-z]+)~".toRegex(), "<span style=\"color: $1;\">")
    high = high.replace("~/~", "</span>")

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(high, Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(high)
    }
}

fun AppCompatActivity.hideKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(findViewById<View>(android.R.id.content).windowToken, 0)
}


fun Context?.toast(text: CharSequence, duration: Int = Toast.LENGTH_LONG) = this?.let { Toast.makeText(it, text, duration).show() }
fun Context?.toast(@StringRes textId: Int, duration: Int = Toast.LENGTH_LONG) = this?.let { Toast.makeText(it, textId, duration).show() }
