package com.tc.tcmap.domain

import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.allowHardware
import com.google.android.gms.maps.model.LatLng

data class MarkerInfo(
    val id : Int = 0,
    val imageUrl: String,
    val title: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

class PersonInfo(
     val imageUrl: String,
     val title: String,
    val profession: String,
    val rating: Float,
    val distance: Float,
    val proximity: String,
    val latitude: Double,
    val longitude: Double
)

