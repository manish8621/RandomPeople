package com.mk.randompeople.model.repository

import com.mk.randompeople.model.network.NetworkResponse
import com.mk.randompeople.model.network.PeopleApiClient
import retrofit2.HttpException

class PeopleRepository {
    suspend fun getProfiles(count:Int=25):NetworkResponse{
        return try {
            val deferred = PeopleApiClient.peopleApi.getProfiles(count)
            NetworkResponse.Success(deferred.await().results)
        }
        catch (e:HttpException){
            NetworkResponse.Error("HttpException")
        }
    }
}