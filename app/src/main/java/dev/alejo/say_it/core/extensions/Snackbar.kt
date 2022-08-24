package dev.alejo.say_it.core.extensions

import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar

fun View.snack(@StringRes text: Int) = Snackbar.make(this, text, Snackbar.LENGTH_LONG).show()