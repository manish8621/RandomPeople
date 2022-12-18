package com.mk.randompeople.model.network

import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query


interface PeopleApi {
    @GET("api")
    fun getProfiles(
        @Query("results")
        results:Int=25
    ):Deferred<NetworkResultContainer>
}