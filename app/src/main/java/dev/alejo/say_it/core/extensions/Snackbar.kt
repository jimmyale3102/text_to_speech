package dev.alejo.say_it.core.extensions

import android.content.Context
import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import dev.alejo.say_it.R
import dev.alejo.say_it.core.MyPermission

fun View.snack(context: Context, @StringRes text: Int, action: Boolean = false) {
    val snack = Snackbar.make(this, text, Snackbar.LENGTH_LONG)
    if(action)
        snack.setAction(R.string.allow) {
            MyPermission(context).requestCustomPermission()
        }
    return snack.show()
}