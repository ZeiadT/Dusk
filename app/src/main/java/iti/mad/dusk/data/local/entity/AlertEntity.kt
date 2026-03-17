package iti.mad.dusk.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import iti.mad.dusk.domain.model.AlertType

@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fromHour: Int,
    val fromMinute: Int,
    val toHour: Int,
    val toMinute: Int,
    val type: AlertType,
    val isActive: Boolean,
)
