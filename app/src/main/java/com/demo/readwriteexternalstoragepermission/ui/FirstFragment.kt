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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*


class FirstFragment : Fragment() {

    private lateinit var folderName: String
    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private const val TAG: String = "FirstFragment"
        const val IMAGE_DIRECTORY = "/abc_test"
    }

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
            val file =
                File(Environment.getExternalStorageDirectory().toString() + "/" + folderNameM)
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
            try {
                if (bitmap == null) {
                    // La captura de la imagen falló o el usuario canceló la operación
                    toast("La captura de la imagen falló o el usuario canceló la operación")
                    Log.i(TAG, "La captura de la imagen falló o el usuario canceló la operación")
                    return@registerForActivityResult
                }
                // La imagen se capturó con éxito
                // Aquí puedes guardar la imagen o hacer algo con ella
                binding.imvPhoto.setImageBitmap(bitmap)
                saveImageBitmap(bitmap)
            } catch (e: Exception) {
                Log.e(TAG, "Error: takePicture ${e.message}")
                toast("Error: takePicture ${e.message}")
            }

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

    private fun createFileDestination1(
        myBitmap: Bitmap,
        folderDirectory: File,
        fileName: String,
    ): File? {
        try {
            if (!folderDirectory.exists()) {
                throw Exception("createFileDestination1() Folder not exists")
            }

            val fileDestination1 = File(folderDirectory, fileName)
            if (!fileDestination1.exists()) {
                val fileCreated = fileDestination1.createNewFile()
                if (!fileCreated) {
                    // Failed to create the file
                    throw Exception("createFileDestination1() File not exists")
                }
            }
            val fo = FileOutputStream(fileDestination1)
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fo)
            fo.close()
            return fileDestination1

        } catch (e: Exception) {
            Log.e(TAG, "Error al crear fileDestination1: ${e.message}")
        }

        return null
    }

    private fun createFileDestination2(myBitmap: Bitmap, fileName: String): File? {
        try {
            val fileDestination2 =
                File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)

            if (!fileDestination2.exists()) {
                val fileCreated = fileDestination2.createNewFile()
                if (!fileCreated) {
                    // Failed to create the file
                  throw Exception("Failed to create the file")
                }
            }else{
                Log.d(TAG, "createFileDestination2: file2 exists")
            }

            val fo = FileOutputStream(fileDestination2)
            fo.write(ByteArrayOutputStream().apply {
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, this)
            }.toByteArray())
            MediaScannerConnection.scanFile(
                requireContext(),
                arrayOf(fileDestination2.path),
                arrayOf("image/jpeg"),
                null
            )
            fo.close()
            return fileDestination2
        } catch (e: Exception) {
            Log.e(TAG, "Error fileDestination2(): ${e.message}")
        }
        return null
    }

    private fun saveImageBitmap(myBitmap: Bitmap): String {

        try {
            val folderDirectory = File(Environment.getExternalStorageDirectory().toString() + IMAGE_DIRECTORY)
            if (!folderDirectory.exists()) {
                val wasDirectoryCreated = folderDirectory.mkdirs()
                if (!wasDirectoryCreated) {
                    Log.d(TAG, "El directorio no fue creado.")
                }else{
                    Log.d(TAG, "El directorio fue creado.")
                }
            }


            val fileName = "image_"+getCalendarInstanceName()

            val fileDestination1 = createFileDestination1(myBitmap, folderDirectory, fileName)

            val fileName2 = "img_"+getCalendarInstanceName()
            val fileDestination2 = createFileDestination2(myBitmap, fileName2)
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

//            fileDestination1.createNewFile()

            if (fileDestination1 == null || !fileDestination1.exists() ) {
                binding.tvFile1.text = "file1 No creado : ${fileDestination1?.absolutePath}"
            } else {
                    binding.tvFile1.text = "file1 Creado : ${fileDestination1}"
            }


            if (fileDestination2 != null) {
                if (fileDestination2.exists()) {
                    Log.d(TAG, "saveImage: file2 : $fileDestination2")
                    binding.tvFile2.text = "file2 Creado : ${fileDestination2}"
                } else {
                    binding.tvFile2.text = "file2 No creado : ${fileDestination2.absolutePath}"
                    Log.e(TAG, "file2 No creado : ${fileDestination2.absolutePath}")
                }
            } else {
                binding.tvFile2.text = "file2 No creado : ${fileDestination2?.absolutePath}"
            }


            return ""

        } catch (e1: Exception) {
            e1.printStackTrace()
            Log.d(TAG, "failed saveImageBitmap ${e1.message}")
            toast("failed ${e1.message}")
        }

        return ""
    }

    private fun getCalendarInstanceName() =
        Calendar.getInstance().timeInMillis.toString().replace(":", ".") + ".jpg"

    private fun toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}