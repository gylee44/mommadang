package com.tukorea.mommadang.api

data class WeatherResponse(
    val response: ResponseBody
)

data class ResponseBody(
    val body: WeatherBody
)

data class WeatherBody(
    val items: WeatherItems
)

data class WeatherItems(
    val item: List<WeatherItem>
)

data class WeatherItem(
    val category: String,   // 예: "TMP", "POP", "SKY"
    val fcstValue: String,  // 예: "21"
    val fcstTime: String,   // 예: "1200"
    val fcstDate: String,   // 예: "20250624"
    val nx: Int,
    val ny: Int
)
