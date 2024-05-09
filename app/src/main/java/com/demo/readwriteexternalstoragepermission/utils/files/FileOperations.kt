package com.demo.readwriteexternalstoragepermission.utils.files

import android.content.Context
import java.io.File


/**
 * Created on mayo.
 * year 2024 .
 */
interface FileOperations {
 fun save(filename: String, content: String)
 fun read(filename: String): String?
}
