package com.demo.readwriteexternalstoragepermission.ui.utils


/**
 * Created on abril.
 * year 2024 .
 */
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class PermissionHandler(
    private val context: Context,
    private val permissions: List<String>,
    private val onPermissionResult: (String, Boolean) -> Unit
) {
    private val requestPermissionLaunchers = mutableMapOf<String, ActivityResultLauncher<String>>()

    fun registerRequestPermissionLaunchers(registry: ActivityResultRegistry) {
        permissions.forEach { permission ->
            requestPermissionLaunchers[permission] = registry.register(
                "key_permission_request_$permission",
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                onPermissionResult(permission, isGranted)
            }
        }
    }

    fun hasPermission(permission: String): Boolean {
        return if (permission == Manifest.permission.MANAGE_EXTERNAL_STORAGE) {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.R || Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestPermission(permission: String) {
        if (permission == Manifest.permission.MANAGE_EXTERNAL_STORAGE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            val uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            context.startActivity(intent)
        } else {
            requestPermissionLaunchers[permission]?.launch(permission)
        }
    }
}