package dev.alejo.say_it.core

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog.Builder
import androidx.core.app.ActivityCompat
import dev.alejo.say_it.databinding.AlertPermissionBinding

class MyPermission(private val context: Context) {

    private fun requestCustomPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                context as Activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ) {
            val binding = AlertPermissionBinding.inflate(LayoutInflater.from(context))
            val builder = Builder(context)
                .setView(binding.root)
            val mAlertDialog = builder.show()
            binding.allowButton.setOnClickListener {
                mAlertDialog.dismiss()
                requestPermission()
            }
            binding.denyButton.setOnClickListener { mAlertDialog.dismiss() }
        } else { requestPermission() }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
        )
    }

    fun validatePermission(): Boolean =
        if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            requestCustomPermission()
            false
        }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123
    }

}