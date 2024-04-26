package com.demo.readwriteexternalstoragepermission.ui

import android.Manifest
import android.content.Intent
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.demo.readwriteexternalstoragepermission.R
import com.demo.readwriteexternalstoragepermission.databinding.FragmentPermissionsBinding
import com.demo.readwriteexternalstoragepermission.ui.permissions.StoragePermissionManager
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

    private val storagePermissionManager by lazy { StoragePermissionManager(this) }

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
        private const val TAG = "PERMISSION_TAG"
    }
    override fun onResume() {
        super.onResume()
//        if (!storagePermissionManager.hasPermission()) {
//            storagePermissionManager.requestStoragePermission()
//        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraPermissionRequester.registerRequestPermissionLauncher(requireActivity().activityResultRegistry)
//        writeStoragePermissionRequester.registerRequestPermissionLauncher(requireActivity().activityResultRegistry)
//        readStoragePermissionRequester.registerRequestPermissionLauncher(requireActivity().activityResultRegistry)
//        manageExternalStorageRequester.registerRequestPermissionLauncher(requireActivity().activityResultRegistry)

        storagePermissionManager.onPermissionResult = { isGranted ->
            val text = if (isGranted) {
                coloredText("Storage permission granted\n", android.R.color.holo_green_dark)
            } else {
                coloredText("Storage permission denied\n", android.R.color.holo_red_dark)
            }
            binding.tvResultPermissions.append(text)
        }

        binding.btnRequestPermissions.setOnClickListener {

            binding.tvResultPermissions.text = ""
            if (!cameraPermissionRequester.hasPermission()) {
                cameraPermissionRequester.requestPermission()
            }


            if (!storagePermissionManager.hasPermission()) {
                storagePermissionManager.requestStoragePermission()
            }

            if (cameraPermissionRequester.hasPermission() && storagePermissionManager.hasPermission()) {
                navigateToCameraFragment()
            }

        }

    }

    private val storageActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    coloredText(
                        "Manage external storage permission granted\n",
                        android.R.color.holo_green_dark
                    )
                } else {
                    //manage external storage is denied
                    coloredText(
                        "Manage external storage permission denied\n",
                        android.R.color.holo_red_dark
                    )
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


    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestPermissionStorageManager() {
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
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun checkPermissionStorageManager(): Boolean {
        try {
            val isPermissionStorageManager = Environment.isExternalStorageManager()
            if (!isPermissionStorageManager) {
                coloredText(
                    "Manage external storage permission denied\n",
                    android.R.color.holo_red_dark
                )
            } else {
                coloredText(
                    "Manage external storage permission granted\n",
                    android.R.color.holo_green_dark
                )
            }
            Log.e(
                TAG,
                "checkPermissionStorageManager() isPermissionStorageManager: $isPermissionStorageManager"
            )
            return isPermissionStorageManager
        } catch (e: Exception) {
            Log.e(TAG, "checkPermissionStorageManager() error: ${e.message}")
            coloredText(
                "Manage external storage permission denied\n",
                android.R.color.holo_red_dark
            )
            return false
        }

    }


    private fun navigateToCameraFragment() {
        findNavController().navigate(R.id.action_permissionsFragment_to_FirstFragment)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}