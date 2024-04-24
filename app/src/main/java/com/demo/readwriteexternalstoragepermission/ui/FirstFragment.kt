package com.demo.readwriteexternalstoragepermission.ui

import android.content.ContentValues
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.demo.readwriteexternalstoragepermission.databinding.FragmentFirstBinding
import java.io.*
import java.util.*


class FirstFragment : Fragment() {

    private lateinit var folderName: String
    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.buttonFirst.setOnClickListener {
//            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
//        }

        binding.btnCreateFolder.setOnClickListener {

            folderName = binding.textEdtFolderName.text.toString().trim()
            createFolder(folderName)
        }
        binding.imvPhoto.setOnClickListener {
            takePicture.launch(null)
//            takePhotoFromCamera()
        }
    }

    //    private fun takePhotoFromCamera() {
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//            takePicture.launch(null)
//        } else {
//            requestPermissions(arrayOf(Manifest.permission.CAMERA), 0)
//
//        }
//    }
    private fun createFolder(folderNameM: String): Boolean {
        try {
            val file = File(Environment.getExternalStorageDirectory().toString() + "/" + folderNameM)
            if (file.exists()) {
                binding.tvResult.text = "Folder already exists"
                toast("Folder already exists")
                return false
            }
            val folderCreated = file.mkdir()
            ///storage/emulated/0/myfolderimage

            if (!folderCreated) {
                binding.tvResult.text = "Folder not created"
                toast("Folder not created")
                return false
            }
            binding.tvResult.text = "Folder created ${file.absolutePath}"
            toast("Folder created ${file.absolutePath}")
            return true

        } catch (e: Exception) {
            binding.tvResult.text = "Error: ${e.message}"
            toast("Error: ${e.message}")
            return false
        }

    }


    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap == null) {
                // La captura de la imagen falló o el usuario canceló la operación
                toast("La captura de la imagen falló o el usuario canceló la operación")
                return@registerForActivityResult
            }

            // La imagen se capturó con éxito
            // Aquí puedes guardar la imagen o hacer algo con ella
            binding.imvPhoto.setImageBitmap(bitmap)
            saveImageBitmap(bitmap)
        }

    private fun saveImage(bitmap: Bitmap) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "Image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val uri = requireContext().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        uri?.let {
            val outputStream: OutputStream? = requireContext().contentResolver.openOutputStream(it)
            outputStream?.let { os ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                os.close()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                requireContext().contentResolver.update(uri, contentValues, null, null)
            }
        }
    }

    private fun saveImageBitmap(myBitmap: Bitmap): String {

        try {
            val bytes = ByteArrayOutputStream()
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)

            val wallpaperDirectory = File(Environment.getExternalStorageDirectory().toString() + IMAGE_DIRECTORY
            )

            val fileName = Calendar.getInstance().timeInMillis.toString().replace(":", ".") +
                        ".jpg"

            if (!wallpaperDirectory.exists()) {
                wallpaperDirectory.mkdirs()
            }

            val fileDestination1 = File(wallpaperDirectory, Calendar.getInstance().timeInMillis.toString().replace(":", ".") +
                    ".jpg")

            val fileDestination2: File = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)

            Log.d(TAG, "saveImage: file1 : $fileDestination1")
            binding.tvFile1.text = "file1 : ${fileDestination1.absolutePath}"
            Log.d(TAG, "saveImage: file2 : $fileDestination2")
            binding.tvFile2.text = "file2 : ${fileDestination2.absolutePath}"

            /*
            *
            *  use fileDestination1 ->
            *  /storage/emulated/0/abc_test/1629810839754.jpg
            *  failed Operation not permitted
            *  show in gallery
            *
            *  use fileDestination2 ->
            *  /storage/emulated/0/Android/data/master.write_external.storage_practice/files/storage/emulated/0/abc_test/1629810839734.jpg
            *  works fine
            *
            * */
            fileDestination2.createNewFile()
            val fo = FileOutputStream(fileDestination2)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(
                requireContext(),
                arrayOf(fileDestination2.path),
                arrayOf("image/jpeg"),
                null
            )
            fo.close()
            Log.d(
                TAG, "File Saved --->"
                        + fileDestination2.absolutePath
            )
            return fileDestination2.absolutePath

        } catch (e1: IOException) {
            e1.printStackTrace()
            Log.d(TAG, "failed ${e1.message}")
            toast("failed ${e1.message}")
        }



        return ""
    }

    private fun toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG: String = "FirstFragment"
        const val IMAGE_DIRECTORY = "/abc_test"
    }
}