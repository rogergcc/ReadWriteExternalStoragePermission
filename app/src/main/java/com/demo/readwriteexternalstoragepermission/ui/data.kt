package com.demo.readwriteexternalstoragepermission.ui

import android.content.ContentValues
import android.os.Environment


/**
 * Created on abril.
 * year 2024 .
 */

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.OutputStream

//class MainActivity : AppCompatActivity(), View.OnClickListener {
//
//    private lateinit var binding: ActivityMainBinding
//
//    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
//        if (bitmap != null) {
//            // La imagen se capturó con éxito
//            // Aquí puedes guardar la imagen o hacer algo con ella
//            binding.iv.setImageBitmap(bitmap)
//            saveImage(bitmap)
//        } else {
//            // La captura de la imagen falló o el usuario canceló la operación
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        binding.iv.setOnClickListener(this)
//    }
//
//    private fun takePhotoFromCamera() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//            takePicture.launch(null)
//        } else {
//            requestPermissions(arrayOf(Manifest.permission.CAMERA), 0)
//        }
//    }
//
//    private fun saveImage(bitmap: Bitmap) {
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, "Image_${System.currentTimeMillis()}.jpg")
//            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
//                put(MediaStore.MediaColumns.IS_PENDING, 1)
//            }
//        }
//
//        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//
//        uri?.let {
//            val outputStream: OutputStream? = contentResolver.openOutputStream(it)
//            outputStream?.let { os ->
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
//                os.close()
//            }
//
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//                contentValues.clear()
//                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
//                contentResolver.update(uri, contentValues, null, null)
//            }
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            takePhotoFromCamera()
//        } else {
//            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    override fun onClick(p0: View?) {
//        if (p0?.id == R.id.iv) {
//            takePhotoFromCamera()
//        }
//    }
//}