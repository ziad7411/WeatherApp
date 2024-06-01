package com.example.weatherapp.model

data class TimeZoneResponse(
    val displayName: String,
    val effectiveTimeZoneFull: String,
    val effectiveTimeZoneShort: String,
    val ianaTimeId: String,
    val isDaylightSavingTime: Boolean,
    val localTime: String,
    val utcOffset: String,
    val utcOffsetSeconds: Int
)