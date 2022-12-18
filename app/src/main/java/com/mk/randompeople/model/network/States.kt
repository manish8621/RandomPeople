package com.mk.randompeople.model.network

enum class States(val msg:String) {
    ERROR("error while connecting to api"),
    NO_INTERNET("No Internet"),
    SUCCESS("api response [OK]")
}