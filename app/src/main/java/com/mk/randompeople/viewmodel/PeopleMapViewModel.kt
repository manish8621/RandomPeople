package com.mk.randompeople.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.mk.randompeople.model.network.NetworkResponse
import com.mk.randompeople.model.network.Profile
import com.mk.randompeople.model.network.States
import com.mk.randompeople.model.repository.PeopleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PeopleMapViewModel:ViewModel() {
    val profiles = MutableLiveData<List<Profile>>()
    var radius = 5.0F
    var locationSelected:LatLng? = null
    val status = MutableLiveData<States?>(null)
    private val repository = PeopleRepository()

    fun fetchProfiles(){
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getProfiles(100)
            when(response){
                is NetworkResponse.Success -> {
                    profiles.postValue(response.profiles)
                    status.postValue(States.SUCCESS)
                }
                is NetworkResponse.Error -> status.postValue(States.ERROR)
            }
        }
    }
}