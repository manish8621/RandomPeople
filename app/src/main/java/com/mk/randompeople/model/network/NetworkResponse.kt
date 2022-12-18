package com.mk.randompeople.model.network


sealed class NetworkResponse{
    class Success(val profiles:List<Profile>):NetworkResponse()
    class Error(val errorMsg:String):NetworkResponse()
}