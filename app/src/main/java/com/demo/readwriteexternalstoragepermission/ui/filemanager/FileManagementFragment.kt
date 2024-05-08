package com.demo.readwriteexternalstoragepermission.ui.filemanager

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.demo.readwriteexternalstoragepermission.databinding.FragmentFileManagementBinding
import com.demo.readwriteexternalstoragepermission.ui.utils.AppUtils
import com.demo.readwriteexternalstoragepermission.ui.utils.FileStorageManager
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

        binding.tvFolderUbication.text="Folder Ubication: ${fileStorageManager.folderNamePath().absolutePath}. \n (Permission Needed) "
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
            val xmlContent = AppUtils.xmlContentSample()

            fileStorageManager.saveXmlFileToSdCard("$fileName.xml", xmlContent)
        }

        binding.btnReadXml.setOnClickListener {
            val filename = "xml_" + AppUtils.fileNameXmlSdPublic() + ".xml"
            val xmlContent = fileStorageManager.readXmlFromExternalStorageSDCard(filename)?.highlight()

            binding.fileText.text = xmlContent
        }

        binding.btnReadImage.setOnClickListener {
            val filename = "image_" + AppUtils.fileNameImageSdPublic() + ".jpg"

            val bitmap = fileStorageManager.readImageBitmapFromSdCard(filename)
            if (bitmap==null){
                requireContext().toast("Archivo de Imagen no existe")
            }
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


                saveImageBitmapDirectoriesExternal(bitmap)
            } catch (e: Exception) {
                Log.e(TAG, "Error: takePicture ${e.message}")
                requireContext().toast("Error: takePicture ${e.message}")
            }

        }


    private fun saveImageBitmapDirectoriesExternal(myBitmap: Bitmap) {
        try {

            val fileName1 = "image_" + AppUtils.fileNameImageSdPublic()

            val fileDestination1 = fileStorageManager.saveImageBitmapToSdCard(
                myBitmap, "$fileName1.jpg"
            )

//            val fileName2 = "img_" + AppUtils.fileNameImageSdPrivate()
//            val fileDestination2 = fileStorageManager.saveImageBitmapToSdCardPrivate(
//                myBitmap,
//                "$fileName2.jpg"
//            )
            /*
            *  use fileDestination1 ->
            *  /storage/emulated/0/abc_test/1629810839754.jpg
            *  failed Operation not permitted // PERMISSION MANAGE_EXTERNAL_STORAGE NEEDED
            *  show in gallery
            *
            *  use fileDestination2 ->
            *  /storage/emulated/0/Android/data/com.demo.readwriteexternalstoragepermission/files/Pictures
            *  works fine
            * */

//            fileDestination1.createNewFile()

            if (fileDestination1 == null || !fileDestination1.exists()) {
                binding.tvFile1.text = "SD Publico No creado : ${fileDestination1?.absolutePath}"
            } else {
                binding.tvFile1.text = "SD Publico Creado : ${fileDestination1.absolutePath}"
            }

//
//            if (fileDestination2 == null || !fileDestination2.exists()) {
//                binding.tvFile2.text = "SD  Privado No creado : ${fileDestination2?.absolutePath}"
//            } else {
//                binding.tvFile2.text = "SD Privado Creado : ${fileDestination2.path}"
//            }


        } catch (ex: Exception) {
            ex.printStackTrace()
            binding.tvResult.text = ex.message
            Log.d(TAG, "failed saveImageBitmap ${ex.message}")
//            requireContext().toast("failed ${ex.message}")
        }

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}