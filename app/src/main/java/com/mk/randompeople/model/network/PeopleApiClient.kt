package com.mk.randompeople.model.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object PeopleApiClient {
    const val API_BASE_URL = "https://randomuser.me/"
    private val moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(API_BASE_URL)
        .build()
    val peopleApi = retrofit.create(PeopleApi::class.java)
}