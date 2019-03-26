package com.meetings.android.utils

import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat


val Activity?.REQUEST_CODE_LOCATION_PERMISSIONS
    get() = 7355
val Activity?.REQUEST_CODE_LOCATION_RESOLUTION
    get() = 7356

fun Activity.requestLocationPermissionsIfNeeded() {
    if (!isLocationPermissionsGranted()) {
        requestLocationPermissions()
    }
}

fun Activity.requestLocationPermissions() {
    val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION)
    ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_LOCATION_PERMISSIONS)
}

fun Activity.isLocationPermissionsGranted() = ContextCompat.checkSelfPermission(this,
        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this,
        android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

fun Activity.isLocationPermissionsGranted(permissions: Array<out String>,
                                          grantResults: IntArray) =
        (permissions.contains(android.Manifest.permission.ACCESS_FINE_LOCATION)
                || permissions.contains(android.Manifest.permission.ACCESS_COARSE_LOCATION))
                && grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED