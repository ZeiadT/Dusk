package iti.mad.dusk.domain.model

data class Location(
    val lat: Double,
    val lon: Double,
    val cityName: String,
    val country: String,
    val displayName: String,
    val isDefault: Boolean,
    val addedAt: Long
)