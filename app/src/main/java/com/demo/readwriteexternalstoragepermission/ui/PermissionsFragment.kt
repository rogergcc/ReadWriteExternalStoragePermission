package com.demo.readwriteexternalstoragepermission.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.demo.readwriteexternalstoragepermission.R
import com.demo.readwriteexternalstoragepermission.databinding.FragmentPermissionsBinding
import com.demo.readwriteexternalstoragepermission.ui.utils.PermissionRequester

class PermissionsFragment : Fragment() {

    private var _binding: FragmentPermissionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPermissionsBinding.inflate(inflater, container, false)
        return binding.root
    }


//    private val cameraPermissionRequester = PermissionRequester(
//        requireContext(),
//        Manifest.permission.CAMERA
//    ) { isGranted ->
//        if (isGranted) {
//            binding.tvResultPermissions.text = binding.tvResultPermissions.text.toString() + "Camera permission granted"
//            toast("Camera permission granted")
//        } else {
//            binding.tvResultPermissions.text = binding.tvResultPermissions.text.toString() + "Camera permission denied"
//            toast("Camera permission denied")
//        }
//    }
//
//    private val writeStoragePermissionRequester = PermissionRequester(
//        requireContext(),
//        Manifest.permission.WRITE_EXTERNAL_STORAGE
//    ) { isGranted ->
//        if (isGranted) {
//            toast("Storage permission granted")
//            binding.tvResultPermissions.text = binding.tvResultPermissions.text.toString() + "Storage permission granted"
//        } else {
//            toast("Storage permission denied")
//            binding.tvResultPermissions.text = binding.tvResultPermissions.text.toString() + "Storage permission denied"
//        }
//    }
//    private val readStoragePermissionRequester = PermissionRequester(
//        requireContext(),
//        Manifest.permission.READ_EXTERNAL_STORAGE
//    ) { isGranted ->
//        if (isGranted) {
//            toast("Read storage permission granted")
//            binding.tvResultPermissions.text = binding.tvResultPermissions.text.toString() + "Read storage permission granted"
//        } else {
//            toast("Read storage permission denied")
//            binding.tvResultPermissions.text = binding.tvResultPermissions.text.toString() + "Read storage permission denied"
//        }
//    }

    private val cameraPermissionRequester by lazy {
        PermissionRequester(
            requireContext(),
            Manifest.permission.CAMERA
        ) { isGranted ->
            val text = if (isGranted) {
                coloredText("Camera permission granted\n", android.R.color.holo_green_dark)
            } else {
                coloredText("Camera permission denied\n", android.R.color.holo_red_dark)
            }
            binding.tvResultPermissions.append(text)
        }
    }
    private val writeStoragePermissionRequester by lazy {
        PermissionRequester(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) { isGranted ->
            val text = if (isGranted) {
                coloredText("Write Storage permission granted\n", android.R.color.holo_green_dark)
            } else {
                coloredText("Write Storage permission denied\n", android.R.color.holo_red_dark)
            }
            binding.tvResultPermissions.append(text)
        }
    }
    private val readStoragePermissionRequester by lazy {
        PermissionRequester(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) { isGranted ->
            val text = if (isGranted) {
                coloredText("Read storage permission granted\n", android.R.color.holo_green_dark)
            } else {
                coloredText("Read storage permission denied\n", android.R.color.holo_red_dark)
            }
            binding.tvResultPermissions.append(text)
        }
    }
    private fun coloredText(text: String, colorId: Int): SpannableStringBuilder {
        val spannable = SpannableStringBuilder(text)
        val color: Int = ContextCompat.getColor(requireContext(), colorId)
        spannable.setSpan(
            ForegroundColorSpan(color),
            0,
            text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
    }

    private companion object {
        private const val STORAGE_PERMISSION_CODE = 100
        private const val TAG = "PERMISSION_TAG"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraPermissionRequester.registerRequestPermissionLauncher(requireActivity().activityResultRegistry)
        writeStoragePermissionRequester.registerRequestPermissionLauncher(requireActivity().activityResultRegistry)
        readStoragePermissionRequester.registerRequestPermissionLauncher(requireActivity().activityResultRegistry)


        binding.btnRequestPermissions.setOnClickListener {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                    if (Environment.isExternalStorageManager()) {
//                        toast("Manage external storage permission granted")
//                        binding.tvResultPermissions.text = "Manage external storage permission granted"
//                        binding.tvResultPermissions.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark))
//                    } else {
//                        //manage external storage is denied
//                        toast("Manage external storage permission denied")
//                        binding.tvResultPermissions.text = "Manage external storage permission denied"
//                        binding.tvResultPermissions.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
//                    }
//                } else {
//                    //android is below 11
//                }
            binding.tvResultPermissions.text = ""
            if (!cameraPermissionRequester.hasPermission()) {
                cameraPermissionRequester.requestPermission()
            }
            if (!writeStoragePermissionRequester.hasPermission()) {
                writeStoragePermissionRequester.requestPermission()
            }
            if (!readStoragePermissionRequester.hasPermission()) {
                readStoragePermissionRequester.requestPermission()
            }

            if (cameraPermissionRequester.hasPermission() && writeStoragePermissionRequester.hasPermission() && readStoragePermissionRequester.hasPermission()) {
                navigateToCameraFragment()
            }

        }

    }

    private val storageActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    toast("Manage external storage permission granted")
                    binding.tvResultPermissions.text = "Mapnage external storage ermission granted"
                    colorPermissionGranted()
                } else {
                    //manage external storage is denied
                    toast("Manage external storage permission denied")
                    binding.tvResultPermissions.text = "Manage external storage permission denied"
                    colorPermissionDenied()
                }
            } else {
                //android is below 11
            }
        }

    private fun colorPermissionDenied() {
        binding.tvResultPermissions.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.holo_red_dark
            )
        )
    }

    private fun colorPermissionGranted() {
        binding.tvResultPermissions.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.holo_green_dark
            )
        )
    }

    private fun toast(s: String) {
        Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //android is 11 or above
            try {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                storageActivityResultLauncher.launch(intent)
            } catch (e: Exception) {
                Log.e("TAG", "RequestPermission")
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                storageActivityResultLauncher.launch(intent)
            }
        } else {
            //android is below 10
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                STORAGE_PERMISSION_CODE
            )
        }
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED &&
            (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || Environment.isExternalStorageManager())
        ) {
            navigateToCameraFragment()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 0)
        }
    }

    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //android 11 (R) or above
            return Environment.isExternalStorageManager()
        }

        val write = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val read = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_DENIED
    }

    private fun navigateToCameraFragment() {
        findNavController().navigate(R.id.action_permissionsFragment_to_FirstFragment)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || Environment.isExternalStorageManager()) {
                navigateToCameraFragment()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}