package com.example.cryptocurrencytracker

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private var usdList: MutableLiveData<List<Currency>> = MutableLiveData()
    private var rubList: MutableLiveData<List<Currency>> = MutableLiveData()
    private var successfulDownload: MutableLiveData<Boolean> = MutableLiveData()
    private val apiFactory: ApiFactory = ApiFactory()

    public fun getUsdList(): LiveData<List<Currency>> {
        return usdList
    }
    public fun getRubList(): LiveData<List<Currency>> {
        return rubList
    }
    public fun getSuccessfulDownload():LiveData<Boolean>{
        return successfulDownload
    }

    fun loadUsd(){
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiFactory.apiService.loadUSDResponse()
                }
                successfulDownload.postValue(true)
                usdList.postValue(response)

            } catch (error: Throwable) {
                successfulDownload.postValue(false)
                Log.d("Doing", error.toString())
            }
        }
    }
    fun loadRub(){
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiFactory.apiService.loadRUBResponse()
                }
                successfulDownload.postValue(true)
                rubList.postValue(response)


            } catch (error: Throwable) {
                successfulDownload.postValue(false)
                Log.d("Doing", error.toString())
            }
        }
    }

}