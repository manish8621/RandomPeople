package com.mk.randompeople.model.network

import com.google.android.gms.maps.model.LatLng

data class NetworkResultContainer(
    val results: List<Profile>
)

data class Profile(
    val name:Name,
    val location:Location,
    val picture:Picture,
    val phone:String,
    val cell:String
)

data class Name(
    val title:String,
    val first:String,
    val last:String
)
data class Location(
    val coordinates:Coordinates
)
data class Coordinates(
    val latitude:String,
    val longitude:String
)
fun Coordinates.toLatLng():LatLng{
    return LatLng(this.latitude.toDouble() , this.longitude.toDouble())
}
data class Picture(
    val large:String,
    val medium:String,
    val thumbnail:String
)