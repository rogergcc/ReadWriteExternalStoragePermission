package com.demo.readwriteexternalstoragepermission.utils


/**
 * Created on abril.
 * year 2024 .
 */
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment


class StoragePermissionManager(private val fragment: Fragment) {

    private val storageActivityResultLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // Handle the result here
            onPermissionResult?.invoke(result.resultCode == Activity.RESULT_OK)
        }

    private val writePermissionLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            onPermissionResult?.invoke(isGranted)
        }

    private val readPermissionLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            onPermissionResult?.invoke(isGranted)
        }

    var onPermissionResult: ((Boolean) -> Unit)? = null

    fun hasPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val hasWritePermission = hasWriteExternalPermission()
            val hasReadPermission = hasReadExternalStoragePermission()
            (hasWritePermission && hasReadPermission)
        }
    }

    fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                onPermissionResult?.invoke(Environment.isExternalStorageManager())
            } else {
                requestPermissionStorageManager()
            }
        } else {
            if (hasWriteExternalPermission()) {
                if (hasReadExternalStoragePermission())
                    onPermissionResult?.invoke(true)
                else
                    requestReadExternalStoragePermission()
            } else {
                requestWriteExternalStoragePermission()
            }
        }
    }

    private fun hasReadExternalStoragePermission() =
        ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    private fun hasWriteExternalPermission() =
        ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestPermissionStorageManager() {
        try {
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
            val uri = Uri.fromParts("package", fragment.requireContext().packageName, null)
            intent.data = uri
            storageActivityResultLauncher.launch(intent)
        } catch (e: Exception) {
            Log.e("TAG", "RequestPermission")
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
            storageActivityResultLauncher.launch(intent)
        }
    }

    private fun requestWriteExternalStoragePermission() {
        writePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun requestReadExternalStoragePermission() {
        readPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}