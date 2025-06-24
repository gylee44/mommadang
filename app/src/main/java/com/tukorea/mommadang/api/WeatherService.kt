package com.tukorea.mommadang.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("getVilageFcst")
    suspend fun getWeather(
        @Query("serviceKey") serviceKey: String,
        @Query("pageNo") pageNo: Int,
        @Query("numOfRows") numOfRows: Int,
        @Query("dataType") dataType: String,
        @Query("base_date") baseDate: String,
        @Query("base_time") baseTime: String,
        @Query("nx") nx: Int,
        @Query("ny") ny: Int
    ): Response<WeatherResponse>
}