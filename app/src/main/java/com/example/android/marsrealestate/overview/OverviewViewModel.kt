/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.android.marsrealestate.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.marsrealestate.network.MarsApi
import com.example.android.marsrealestate.network.MarsApiFilter
import com.example.android.marsrealestate.network.MarsProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel : ViewModel() {

    //To use coroutines you create a job that allows you to cancel the coroutine or know its state
    val viewModelJob = Job()

    //And you need a scope
    val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    //Enum to define state of network call
    enum class NetworkStatus { LOADING, SUCCESS, FAILED }

    // The internal MutableLiveData String that stores the status of the most recent request
    private val _status = MutableLiveData<NetworkStatus>()

    private val _properties = MutableLiveData<List<MarsProperty>>()

    val properties: LiveData<List<MarsProperty>>
        get() = _properties

    // The external immutable LiveData for the request status
    val status: LiveData<NetworkStatus>
        get() = _status


    private val _navigateToDetail = MutableLiveData<MarsProperty>()

    val navigateDetails : LiveData<MarsProperty>
    get() = _navigateToDetail


    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */
    init {
        getMarsRealEstateProperties(MarsApiFilter.SHOW_ALL)
    }


    fun updateFilter(filter : MarsApiFilter) {
        getMarsRealEstateProperties(filter)
    }

    /**
     * Sets the value of the status LiveData to the Mars API status.
     */
    private fun getMarsRealEstateProperties(filter : MarsApiFilter) {
        //_status.value = "Set the Mars API Response here!"

        coroutineScope.launch {
            var getPropertiesDeffered = MarsApi.retrofitService.getProperties(filter.value)
            try {
                _status.value = NetworkStatus.LOADING
                var listResult = getPropertiesDeffered.await()
                //_status.value = "Success: ${listResult.size} Mars properties retrieved."
                _properties.value = listResult
                _status.value = NetworkStatus.SUCCESS
            } catch (exception: Exception) {
                //_status.value = "failure" + exception.message
                _status.value = NetworkStatus.FAILED
                _properties.value = ArrayList()
            }
        }


        /*MarsApi.retrofitService.getProperties().enqueue(object : Callback<List<MarsProperty>> {
            override fun onFailure(call: Call<List<MarsProperty>>, t: Throwable) {
                _response.value = "failure" + t.message
            }

            override fun onResponse(call: Call<List<MarsProperty>>, response: Response<List<MarsProperty>>) {
                _response.value = "Success: ${response.body()?.size} Mars properties retrieved."
            }

        })*/
    }

    fun onMarsPropertyClicked(property : MarsProperty) {
        //Define a livedata for navigation..
        _navigateToDetail.value = property
    }

    fun navigateDetailCompleted() {
        _navigateToDetail.value = null
    }

    override fun onCleared() {
        super.onCleared()
        //Cancel loading data if viewmodel is destroyed
        viewModelJob.cancel()
    }
}