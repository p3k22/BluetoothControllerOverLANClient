package com.p3kIndustries.P3kBT2PC.models

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.p3kIndustries.P3kBT2PC.services.Logger

// Logic for prompting user to give access to required permissions
class Permissions(private val activity: Activity, private val logger: Logger) {

    // Array of all permissions required by app
    private val permissionsRequired = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.BLUETOOTH_CONNECT
    )

    // Checks current permissions granted and prompts user otherwise
    fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                showPermissionRequestPopup()
            }
        } else if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            showPermissionRequestPopup()
        }
    }

    // Pop-up logic for accepting/declining required permissions
    private fun showPermissionRequestPopup() {
        logger.addLog("Requesting permissions from user")
        AlertDialog.Builder(activity)
            .setTitle("Permissions Required")
            .setMessage("This app requires Bluetooth and Location permissions to function properly. Please grant the required permissions.")
            .setPositiveButton("OK") { _, _ ->
                // Request all the required permissions
                requestRequiredPermissions()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss() // Handle when user cancels the permission request
                logger.addLog("Permissions request cancelled.")
            }
            .create()
            .show()
    }

    // Logic for changing permission status
    private fun requestRequiredPermissions() {
        logger.addLog("Permissions granted by user")
        ActivityCompat.requestPermissions(activity, permissionsRequired, 100)
    }
}