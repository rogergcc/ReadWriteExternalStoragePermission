package com.demo.readwriteexternalstoragepermission.utils


/**
 * Created on abril.
 * year 2024 .
 */

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class PermissionRequester(
    private val context: Context,
    private val permission: String,
    private val onPermissionResult: (Boolean) -> Unit,
) {
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    fun registerRequestPermissionLauncher(registry: ActivityResultRegistry) {
        requestPermissionLauncher = registry.register(
            "key_permission_request_$permission",
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            onPermissionResult(isGranted)
        }
    }

    fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission() {
        requestPermissionLauncher.launch(permission)
    }
}