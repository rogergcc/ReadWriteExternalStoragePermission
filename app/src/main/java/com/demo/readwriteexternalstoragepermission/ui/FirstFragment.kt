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
import com.demo.readwriteexternalstoragepermission.ui.utils.FileStorageManager
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
    private val fileStorageManager by lazy { FileStorageManager(requireContext()) }
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
        binding.btnSaveSampleXML.setOnClickListener{
            val fileName = "xml_"+getCalendarInstanceName()
            Log.d(TAG, "saveXml: init")
            val xmlContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <note>
                <to>Tove</to>
                <from>Jani</from>
                <heading>Reminder</heading>
                <body>Don't forget me this weekend!</body>
            </note>
        """.trimIndent()

            fileStorageManager.saveXmlToExternalStorageSDCard("$fileName.xml", xmlContent)
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

            val fileDestination1 = fileStorageManager.saveImageBitmapToSdCard(
                myBitmap, folderDirectory, "$fileName.jpg"
            )

            val fileName2 = "img_"+getCalendarInstanceName()
            val fileDestination2 = fileStorageManager.saveImageBitmapToSdCardPrivate(
                myBitmap,
                "$fileName2.jpg"
            )
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
        Calendar.getInstance().timeInMillis.toString().replace(":", ".")

    private fun toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}