package com.example.cryptocurrencytracker

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private var usdList: MutableLiveData<List<Currency>> = MutableLiveData()
    private var rubList: MutableLiveData<List<Currency>> = MutableLiveData()
    private var successfulDownload: MutableLiveData<Boolean> = MutableLiveData()
    private val apiFactory: ApiFactory = ApiFactory()
    private var currencyInfo: MutableLiveData<CurrencyInfo> = MutableLiveData()

    fun getUsdList(): LiveData<List<Currency>> {
        return usdList
    }


     fun getCurrencyInfo(): LiveData<CurrencyInfo> {
        return currencyInfo
    }

     fun getRubList(): LiveData<List<Currency>> {
        return rubList
    }

     fun getSuccessfulDownload(): LiveData<Boolean> {
        return successfulDownload
    }

    fun loadUsd() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiFactory.apiService.loadUSDResponse()
                }
                successfulDownload.postValue(true)
                usdList.postValue(response)

            } catch (error: HttpException) {
                Log.d("Doing", error.toString())
            }
        }
    }

    fun loadCurrencyInfo(id: String) {
        viewModelScope.launch {
            try {
                val response = apiFactory.apiService.loadCurrencyInfo(id)
                successfulDownload.postValue(true)
                currencyInfo.postValue(response)
            } catch (_: Throwable) {
            }
        }
    }

    fun loadRub() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    apiFactory.apiService.loadRUBResponse()
                }
                successfulDownload.postValue(true)
                rubList.postValue(response)


            } catch (error: HttpException) {
                    Log.d("Doing", error.toString())

            }
        }
    }

}