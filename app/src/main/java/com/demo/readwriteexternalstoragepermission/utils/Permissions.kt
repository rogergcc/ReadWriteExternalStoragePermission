package com.demo.readwriteexternalstoragepermission.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat


/**
 * Created on abril.
 * year 2024 .
 */
object Permissions {
 fun checkPermission(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
 }

 fun requestPermission(context: Context, permission: String, requestCode: Int, requestPermissionLauncher: ActivityResultLauncher<String>) {
  if (!checkPermission(context, permission)) {
   requestPermissionLauncher.launch(permission)
  }
 }

 fun requestManageExternalStoragePermission(context: Context, activityResultLauncher: ActivityResultLauncher<Intent>) {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
   val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
   val uri = Uri.fromParts("package", context.packageName, null)
   intent.data = uri
   activityResultLauncher.launch(intent)
  }
 }


}