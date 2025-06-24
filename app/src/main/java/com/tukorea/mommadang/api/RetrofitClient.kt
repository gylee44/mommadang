package com.tukorea.mommadang.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/"

    val instance: WeatherService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // JSON 변환기
            .build()
            .create(WeatherService::class.java)
    }
}
