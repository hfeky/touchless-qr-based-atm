package com.husseinelfeky.smartbank.ui.transaction

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.husseinelfeky.smartbank.R
import com.husseinelfeky.smartbank.util.Navigator
import com.husseinelfeky.smartbank.util.showError

abstract class TransactionFragment : Fragment() {

    private val cameraPermissionResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                navigateToQrCodeScannerScreen()
            } else {
                showError(R.string.error_camera_permission_needed, R.string.settings) {
                    Navigator.navigateToAppSettings(requireContext())
                }
            }
        }

    protected fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    protected fun requestCameraPermissionThenNavigate() {
        if (isCameraPermissionGranted()) {
            navigateToQrCodeScannerScreen()
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(R.string.rationale_camera_permission)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        cameraPermissionResultLauncher.launch(Manifest.permission.CAMERA)
                    }
                    .show()
            } else {
                cameraPermissionResultLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    abstract fun navigateToQrCodeScannerScreen()
}
