package com.demo.readwriteexternalstoragepermission.ui.filemanager

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.demo.readwriteexternalstoragepermission.databinding.FragmentFileManagementBinding
import com.demo.readwriteexternalstoragepermission.ui.encrypt.EncryptedFileStorage
import com.demo.readwriteexternalstoragepermission.ui.utils.FileStorageManager
import com.demo.readwriteexternalstoragepermission.ui.utils.AppUtils
import com.demo.readwriteexternalstoragepermission.ui.utils.highlight
import com.demo.readwriteexternalstoragepermission.ui.utils.toast


class FileManagementFragment : Fragment() {

    private lateinit var folderName: String
    private var _binding: FragmentFileManagementBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val fileStorageManager by lazy { FileStorageManager(requireContext()) }

    companion object {
        private const val TAG: String = "FileManagementFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentFileManagementBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.buttonFirst.setOnClickListener {
//            findNavController().navigate(R.id.action_FileManagementFragment_to_SecondFragment)
//        }

        binding.tvFolderUbication.text="Folder ubication: ${fileStorageManager.folderNamePath().absolutePath} "
        binding.btnCreateFolder.setOnClickListener {

            folderName = binding.textEdtFolderName.text.toString().trim()
            createFolder(folderName)
        }
        binding.imvPhoto.setOnClickListener {
            takePicture.launch(null)
//            takePhotoFromCamera()
        }
        binding.btnSaveSampleXML.setOnClickListener {
            val fileName = "xml_" + AppUtils.fileNameXmlSdPublic()
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

            fileStorageManager.saveXmlFileToSdCard("$fileName.xml", xmlContent)

        }

        binding.btnReadXml.setOnClickListener {
            val filename = "xml_" + AppUtils.fileNameXmlSdPublic() + ".xml"

            //todo version anterior leer
            val xmlContent = fileStorageManager.readXmlFromExternalStorageSDCard(filename)?.highlight()



            Log.d(TAG, "Contenido del archivo XML: $xmlContent")

            binding.fileText.text = xmlContent
        }

        binding.btnReadImage.setOnClickListener {
            val filename = "image_" + AppUtils.fileNameImageSdPublic() + ".jpg"
            val bitmap = fileStorageManager.readImageBitmapFromSdCard(filename)
            binding.imvRead.setImageBitmap(bitmap)
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
            val file = fileStorageManager.folderNamePath(folderNameM)
            if (file.exists()) {
                binding.tvResult.text = "Folder already exists"
                requireContext().toast("Folder already exists")
                return false
            }
            val folderCreated = file.mkdir()
            ///storage/emulated/0/myfolderimage

            if (!folderCreated) {
                binding.tvResult.text = "Folder not created"
                requireContext().toast("Folder not created")
                return false
            }
            binding.tvResult.text = "Folder created ${file.absolutePath}"
            requireContext().toast("Folder created ${file.absolutePath}")
            return true

        } catch (e: Exception) {
            binding.tvResult.text = "Error: ${e.message}"
            requireContext().toast("Error: ${e.message}")
            return false
        }

    }


    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            try {
                if (bitmap == null) {
                    // La captura de la imagen falló o el usuario canceló la operación
                    requireContext().toast("La captura de la imagen falló o el usuario canceló la operación")
                    Log.i(TAG, "La captura de la imagen falló o el usuario canceló la operación")
                    return@registerForActivityResult
                }
                // La imagen se capturó con éxito
                // Aquí puedes guardar la imagen o hacer algo con ella
                binding.imvPhoto.setImageBitmap(bitmap)


                saveImageBitmaptoDirectoriesExternal(bitmap)
            } catch (e: Exception) {
                Log.e(TAG, "Error: takePicture ${e.message}")
                requireContext().toast("Error: takePicture ${e.message}")
            }

        }


    private fun saveImageBitmaptoDirectoriesExternal(myBitmap: Bitmap): String {

        try {


            val fileName = "image_" + AppUtils.fileNameImageSdPublic()

            val fileDestination1 = fileStorageManager.saveImageBitmapToSdCard(
                myBitmap, "$fileName.jpg"
            )

            val fileName2 = "img_" + AppUtils.fileNameImageSdPrivate()
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

            if (fileDestination1 == null || !fileDestination1.exists()) {
                binding.tvFile1.text = "SD Publico No creado : ${fileDestination1?.absolutePath}"
            } else {
                binding.tvFile1.text = "SD Publico Creado : ${fileDestination1}"
            }


            if (fileDestination2 != null) {
                if (fileDestination2.exists()) {
                    binding.tvFile2.text = "SD Privado Creado : ${fileDestination2.path}"
                } else {
                    binding.tvFile2.text = "SD Privado No creado : ${fileDestination2.absolutePath}"
                }
            } else {
                binding.tvFile2.text = "SD  Privado No creado : ${fileDestination2?.absolutePath}"
            }


            return ""

        } catch (ex: Exception) {
            ex.printStackTrace()
            Log.d(TAG, "failed saveImageBitmap ${ex.message}")
            requireContext().toast("failed ${ex.message}")
        }

        return ""
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}