package com.yelp.fusion.ui

import android.Manifest.permission
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.yelp.fusion.R
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks
import timber.log.Timber

/**
 * This class primarily handles location permissions and location logic in terms of fetching user's current location.
 * Also handles permission requests from user before requesting user location
 */

open class LocationActivity : AppCompatActivity(), PermissionCallbacks {

  private var fusedLocationClient: FusedLocationProviderClient? = null
  private var locationRequest: LocationRequest? = null
  private var locationCallback: LocationCallback? = null
  var currentLocation: Location? = null

  /**
   * Location permissions
   */
  companion object {
    const val LOCATION_REQUEST_CODE = 1002
    val locationPermissions = arrayOf(permission.ACCESS_FINE_LOCATION,
        permission.ACCESS_COARSE_LOCATION)
  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    createLocationListener()
  }

  /**
   * Location listener that provides user's current location.
   */
  private fun createLocationListener() {
    createLocationRequest()
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    locationCallback = object : LocationCallback() {
      override fun onLocationResult(locationResult: LocationResult?) {
        locationResult ?: return
        locationResult.locations.forEach { location ->
          Timber.d("Current Location: $location")
          currentLocation = location
        }
      }
    }
  }

  /**
   * Create the location request
   * Note: PRIORITY_BALANCED_POWER_ACCURACY for battery life efficiency.
   */
  fun createLocationRequest() {
    locationRequest = LocationRequest.create()?.apply {
      interval = 10000
      fastestInterval = 5000
      priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }
  }

  /**
   * Note: This location updates approach is more reliable to fetch user's current location than the fusedLocationClient.getLastLocation()
   * fusedLocationClient.getLastLocation() will not return a location if Google Play services on the device has restarted,
   * and there is no active Fused Location Provider client that has requested location after the services restarted.
   */
   fun startLocationUpdates() {
    fusedLocationClient?.requestLocationUpdates(locationRequest,
        locationCallback,
        Looper.getMainLooper())
  }

  /**
   * Remove Location updates Listener
   */
  override fun onDestroy() {
    super.onDestroy()
    fusedLocationClient?.removeLocationUpdates(locationCallback)
  }



  /**
   * Using Google's EasyPermission's to handle App-Level permissions.
   */
  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
      grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
  }

  /**
   * EasyPermission callback when permission is granted.
   */
  override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    Timber.i("location permission granted")
    startLocationUpdates()
  }

  /**
   * EasyPermission callback when permission is granted.
   */
  override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
    Timber.i("location permission denied")
    requestPermissionWithLocationRationale()
  }

  /**
   * Check Location Permission.
   */
  fun checkLocationPermission() {
    when {
      hasLocationPermission() -> {
        Timber.i("app already has location permission")
      }
      else -> {
        requestPermissionWithLocationRationale()
      }
    }
  }

  /**
   * Request Permission with rationale.
   */
  private fun requestPermissionWithLocationRationale() {
    EasyPermissions.requestPermissions(this@LocationActivity,
        getString(R.string.location_required_rationale),
        LOCATION_REQUEST_CODE, *locationPermissions)
  }

  /**
   * @param context is the context.
   * @param locationPermissions is the array of permissions.
   */
  fun hasLocationPermission(): Boolean = EasyPermissions.hasPermissions(this@LocationActivity,
      *locationPermissions)


}