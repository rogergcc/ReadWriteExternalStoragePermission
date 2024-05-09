package com.demo.readwriteexternalstoragepermission.utils.files

import android.content.Context
import android.os.Environment
import android.util.Log
import com.demo.readwriteexternalstoragepermission.utils.AppContants
import com.demo.readwriteexternalstoragepermission.utils.AppUtils
import java.io.File


/**
 * Created on mayo.
 * year 2024 .
 */
class XmlFileManager(private val context: Context, private val directory: String) : FileOperations {
    override fun save(filename: String, content: String) {
//        val file = File(context.getExternalFilesDir(directory), filename)
//        val file =  File(Environment.getExternalStorageDirectory().toString() + directory,filename)

        val folderDirectory = AppUtils.folderNamePath(directory)
        if (!folderDirectory.exists()) {
            val wasDirectoryCreated = folderDirectory.mkdirs()
            if (!wasDirectoryCreated) {
                Log.d(TAG, "Folder directory not created.")
                throw Exception("Folder not created")
            }
        }
        val file = AppUtils.fileCreate( filename)
        file.writeText(content)

    }

    override fun read(filename: String): String? {
//        val file = File(context.getExternalFilesDir(directory), filename)
//        val file =  File(Environment.getExternalStorageDirectory().toString() + directory,filename)
        val file = AppUtils.fileCreate( filename)
        if (!file.exists()) {
            throw Exception("Error at create file")
        } else {
            return file.readText()
        }
    }

    companion object {
        private const val TAG = "XmlFileManager"
    }
}