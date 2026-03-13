package iti.mad.dusk.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
data class LocationEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "locationID")
    val locationID: String,

    @ColumnInfo(name = "lat")
    val lat: Double,

    @ColumnInfo(name = "lon")
    val lon: Double,

    @ColumnInfo(name = "is_default")
    val isDefault: Boolean,

    @ColumnInfo(name = "is_current")
    val isCurrent: Boolean,

    @ColumnInfo(name = "city_name")
    val cityName: String,

    @ColumnInfo(name = "country")
    val country: String,

    @ColumnInfo(name = "display_name")
    val displayName: String,

    @ColumnInfo(name = "added_at")
    val addedAt: Long,
) {
    companion object {
        fun locationId(lat: Double, lon: Double) = "$lat-$lon"
    }
}