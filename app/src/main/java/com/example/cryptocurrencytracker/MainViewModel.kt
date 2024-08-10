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
    private var successfulDownload: Boolean = true
    private val apiFactory: ApiFactory = ApiFactory()

    public fun getUsdList(): LiveData<List<Currency>> {
        return usdList
    }
    public fun getRubList(): LiveData<List<Currency>> {
        return rubList
    }
    public fun getSuccessfulDownload():Boolean{
        return successfulDownload
    }


    fun loadUsd(){
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiFactory.apiService.loadUSDResponse()
                }
                successfulDownload=true
                usdList.postValue(response)

            } catch (error: Throwable) {
                successfulDownload=false
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
                successfulDownload=true
                rubList.postValue(response)


            } catch (error: Throwable) {
                successfulDownload=false
                Log.d("Doing", error.toString())
            }
        }
    }

}