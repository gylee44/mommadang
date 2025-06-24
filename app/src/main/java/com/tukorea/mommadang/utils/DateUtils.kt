// DateUtils.kt
package com.tukorea.mommadang.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun getBaseDateTime(): Pair<String, String> {
        val calendar = Calendar.getInstance()
        calendar.time = Date()

        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val baseTimes = listOf(2, 5, 8, 11, 14, 17, 20, 23)
        var baseTimeHour = baseTimes.lastOrNull { it <= currentHour } ?: 23

        if (currentHour == 0 || (currentHour == 2 && currentMinute < 30)) {
            calendar.add(Calendar.DATE, -1)
            baseTimeHour = 23
        }

        val baseDate = SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(calendar.time)
        val baseTime = String.format("%02d00", baseTimeHour)

        return Pair(baseDate, baseTime)
    }
}
