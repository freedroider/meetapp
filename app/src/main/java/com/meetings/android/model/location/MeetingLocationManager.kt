package com.meetings.android.model.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import io.reactivex.Single
import javax.inject.Inject

@SuppressLint("MissingPermission")
class MeetingLocationManager @Inject constructor(private val context: Context) : LocationSource {
    private val fusedLocationProviderClient = LocationServices
            .getFusedLocationProviderClient(context)
    private var locationCallback: LocationCallback? = null

    private val request: LocationRequest by lazy {
        LocationRequest.create().apply {
            priority = PRIORITY_HIGH_ACCURACY
            numUpdates = 1
            interval = 0
        }
    }

    private val settings: LocationSettingsRequest by lazy {
        LocationSettingsRequest.Builder().apply {
            addLocationRequest(request)
            setAlwaysShow(true)
        }.build()
    }

    private fun getLocationSettingResponse(): Single<LocationSettingsResponse> {
        return Single.create { emitter ->
            LocationServices.getSettingsClient(context)
                    .checkLocationSettings(settings)
                    .addOnCompleteListener { task ->
                        try {
                            val response =
                                    task.getResult(ApiException::class.java)
                            emitter.onSuccess(response)
                        } catch (exception: ApiException) {
                            when (exception.statusCode) {
                                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                                    val resolvableApiException =
                                            exception as ResolvableApiException
                                    emitter.onError(resolvableApiException)
                                    return@addOnCompleteListener
                                }
                            }
                            emitter.onError(exception)
                        }
                    }
        }
    }

    override fun getLastKnownLocation(): Single<Location> {
        return getLocationSettingResponse().flatMap {
            Single.create<Location> { emitter ->
                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        locationResult ?: return
                        stopLocationUpdates()
                        if (locationResult.locations.isNotEmpty()) {
                            emitter.onSuccess(locationResult.locations.last())
                            return
                        }
                        emitter.onError(UnknownLocationException())
                    }
                }
                fusedLocationProviderClient.requestLocationUpdates(request, locationCallback, null)
                        .addOnFailureListener { exception ->
                            exception.printStackTrace()
                            stopLocationUpdates()
                            emitter.onError(exception)
                        }
            }
        }
    }

    private fun stopLocationUpdates() {
        locationCallback ?: return
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        locationCallback = null
    }

}