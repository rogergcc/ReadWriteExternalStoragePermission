package com.demo.readwriteexternalstoragepermission.ui.permissions

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.demo.readwriteexternalstoragepermission.R
import com.demo.readwriteexternalstoragepermission.databinding.FragmentPermissionsBinding
import com.demo.readwriteexternalstoragepermission.ui.utils.StoragePermissionManager
import com.demo.readwriteexternalstoragepermission.ui.utils.PermissionRequester
import com.demo.readwriteexternalstoragepermission.ui.utils.AppUtils

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



    private val cameraPermissionRequester by lazy {
        PermissionRequester(
            requireContext(),
            Manifest.permission.CAMERA
        ) { isGranted ->
            val text = if (isGranted) {
                AppUtils.coloredText(requireContext(), "Camera permission granted\n", android.R.color.holo_green_dark)
            } else {
                AppUtils.coloredText(requireContext(), "Camera permission denied\n", android.R.color.holo_red_dark)
            }
            binding.tvResultPermissions.append(text)
        }
    }


    private val storagePermissionManager by lazy { StoragePermissionManager(this) }


    private companion object {
        private const val TAG = "PermissionsFragment"
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

        storagePermissionManager.onPermissionResult = { isGranted ->
            val text = if (isGranted) {
                AppUtils.coloredText(requireContext(), "Storage permission granted\n", android.R.color.holo_green_dark)
            } else {
                AppUtils.coloredText(requireContext(), "Storage permission denied\n", android.R.color.holo_red_dark)
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

//    private val storageActivityResultLauncher =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                if (Environment.isExternalStorageManager()) {
//                    coloredText(
//                        "Manage external storage permission granted\n",
//                        android.R.color.holo_green_dark
//                    )
//                } else {
//                    //manage external storage is denied
//                    coloredText(
//                        "Manage external storage permission denied\n",
//                        android.R.color.holo_red_dark
//                    )
//                }
//            } else {
//                //android is below 11
//            }
//        }

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


    private fun navigateToCameraFragment() {
        findNavController().navigate(R.id.action_permissionsFragment_to_FileManagementFragment)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}