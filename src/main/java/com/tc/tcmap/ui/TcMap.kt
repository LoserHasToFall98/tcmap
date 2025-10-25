package com.tc.tcmap.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.allowHardware
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.tc.tcmap.R
import com.tc.tcmap.domain.MarkerInfo
import com.tc.tcmap.domain.PersonInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun <T>TcMap( mapType: MapType<T>,
              markers : List<MarkerInfo>,
              onMarkerClicked : (MarkerInfo) -> Unit) {

    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )


    val context = LocalContext.current
    val locationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val scope = rememberCoroutineScope()

    val singapore = LatLng(33.899610, -84.06)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 16f)
    }

    LaunchedEffect(Unit) {
        if (!locationPermissionsState.allPermissionsGranted) {
            if (locationPermissionsState.shouldShowRationale) {

            }
            locationPermissionsState.launchMultiplePermissionRequest()
        }
    }

    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        if (locationPermissionsState.allPermissionsGranted) {
            fetchCurrentLocation(context, locationClient, updateCameraPosition = { latLng ->
                cameraPositionState.position = CameraPosition.fromLatLngZoom(
                    latLng, 16f
                )
            }) {
                // send open dialog intent to viewmodel
            }

        }
    }

    MainMapView(context, scope, cameraPositionState, locationClient, mapType, markers, onMarkerClicked)

}


@Composable
fun <T>MainMapView(
    context: Context,
    scope: CoroutineScope,
    cameraPositionState: CameraPositionState,
    locationClient: FusedLocationProviderClient,
    mapType: MapType<T>,
    markers : List<MarkerInfo>,
    onMarkerClicked : (MarkerInfo) -> Unit
) {
    var openSettingsDialog by rememberSaveable { mutableStateOf(false) }
    val gpsLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d("LOCN_RESULT", "${result.resultCode == 0}")
            openSettingsDialog = false
        }


    Box(Modifier.fillMaxSize()) {


        val painters = mutableListOf<AsyncImagePainter>()
        markers.forEach {
            painters.add(rememberAsyncImagePainter(ImageRequest.Builder(context).data(it.imageUrl).allowHardware(false).build()))
        }


        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {

            markers.forEachIndexed { index, pi ->


                MarkerComposable(
                    keys = arrayOf(pi),
                    state = rememberUpdatedMarkerState(position = LatLng(pi.latitude,pi.longitude)),
                    title = pi.title,
                    snippet = "",
                    onClick = { marker ->
                        onMarkerClicked(pi)
                        true
                    }
                ) {

                    Card(
                        shape = CircleShape,
                        elevation = CardDefaults.cardElevation(4.dp),
                    ) {
                        Image(
                            painter = painters[index],
                            contentDescription = "",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)

                        )
                    }



                }
            }


        }

        val navToCurrentLocation = {
            fetchCurrentLocation(context, locationClient, updateCameraPosition = { latLng ->
                scope.launch(Dispatchers.IO) {

                    withContext(Dispatchers.Main) {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newCameraPosition(
                                CameraPosition.fromLatLngZoom(
                                    latLng, 16f
                                )
                            ),
                            4000
                        )
                    }
                }
            }) {
                openSettingsDialog = true
            }
        }

        when(mapType){
            is MapType.MarkedMapWithPeople -> {
                val onPersonClick = mapType.onPersonClick
                PersonInfoList(navToCurrentLocation = navToCurrentLocation, mapType.people) { p1 ->
                    onPersonClick(p1)
                }
            }
            MapType.SimpleMap -> {

            }

            is MapType.MarkedMap-> {
                BottomRowLayout(navToCurrentLocation = navToCurrentLocation, data =  mapType.data) { item ->
                    mapType.itemContent(item )
                }
            }
        }


        OpenSettingsDialog(openSettingsDialog, {
            openSettingsDialog = false
        }) {
            try {
                gpsLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            } catch (sendEx: IntentSender.SendIntentException) {
                Log.d("LOCN_GPS", sendEx.toString())
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> BottomRowLayout(
    navigationIcon: @Composable () -> Unit = {
        RoundIconBtn(onClick = {}, painterResource(R.drawable.back))
    },
    actions: @Composable (RowScope.() -> Unit) = {
        RoundIconBtn(onClick = {}, painterResource(R.drawable.star))
        Spacer(Modifier.width(8.dp))
        RoundIconBtn(onClick = {}, painterResource(R.drawable.back))
    },
    navToCurrentLocation: () -> Unit,
    data: List<T>,
    dataItemContent: @Composable (T) -> Unit,
) {
    ConstraintLayout(
        Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        val (topBarRef, bottomLazyRow, navToCurrentLocnRef) = createRefs()

        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            title = {}, navigationIcon = navigationIcon,
            actions = actions,
            modifier = Modifier.constrainAs(topBarRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start, margin = 0.dp)
                end.linkTo(parent.end, margin = 0.dp)
                width = Dimension.fillToConstraints
            })

        RoundIconBtn(
            onClick = {
                navToCurrentLocation()
            },
            painterResource(R.drawable.star),
            modifier = Modifier.constrainAs(navToCurrentLocnRef) {
                bottom.linkTo(bottomLazyRow.top)
                end.linkTo(parent.end, margin = 16.dp)
            })

        LazyRow(
            Modifier.constrainAs(bottomLazyRow) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start, margin = 0.dp)
                end.linkTo(parent.end, margin = 0.dp)
                width = Dimension.fillToConstraints
            },
            contentPadding = PaddingValues(32.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            items(data) {
                dataItemContent(it)
            }

        }
    }
}

@Composable
fun PersonInfoList(
    navToCurrentLocation: () -> Unit,
    people : List<PersonInfo>,
    onPersonClicked: (PersonInfo) -> Unit
) {


    BottomRowLayout(navToCurrentLocation = navToCurrentLocation, data = people) {
        PersonInfoCard(Modifier.width(240.dp), it) {
            onPersonClicked(it)
        }
    }
}

@Composable
fun RoundIconBtn(onClick: () -> Unit, painter: Painter, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Icon(
            tint = Color(0xFFFF7A1A),
            painter = painter, contentDescription = "",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White)
                .padding(6.dp)
        )
    }
}

@SuppressLint("MissingPermission")
fun fetchCurrentLocation(
    context: Context,
    locationClient: FusedLocationProviderClient,
    updateCameraPosition: (LatLng) -> Unit,
    onResolvableApiException: () -> Unit
) {

    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
        .setMinUpdateIntervalMillis(5000)
        .build()

    val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

    val client = LocationServices.getSettingsClient(context)

    val task = client.checkLocationSettings(builder.build())

    Log.d("LOCN_CUR", "Fetching locn....")
    task.addOnSuccessListener { response ->
        response.locationSettingsStates?.let {
            Log.d("LOCN_GPS", "${it.isGpsUsable}")

            if (it.isGpsUsable) {

                val result = locationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    CancellationTokenSource().token,
                )
                result.addOnSuccessListener { response ->
                    Log.d("LOCN_CUR", response.toString())
                    updateCameraPosition(LatLng(response.latitude, response.longitude))
                }

                result.addOnFailureListener { exception ->
                    Log.d("LOCN_CUR", exception.toString())
                }
            }
            return@let
        }.run {
            Log.d("LOCN_CUR", "Location Settings is null")
        }
    }
    task.addOnFailureListener { exception ->
        Log.d("LOCN_DEN", exception.toString())
        if (exception is ResolvableApiException) {
            onResolvableApiException()
        } else {
            Log.d("LOCN_CUR", exception.toString())
        }

    }

}


