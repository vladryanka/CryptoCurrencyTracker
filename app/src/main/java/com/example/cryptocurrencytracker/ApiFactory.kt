package com.example.cryptocurrencytracker

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiFactory {
    private val BASE_URL: String = "https://api.coingecko.com/api/v3/"
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)

}