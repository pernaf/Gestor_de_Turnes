package com.gabrielcarvalho.tourfinance.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tour_stops",
    foreignKeys = [
        ForeignKey(
            entity = TourEntity::class,
            parentColumns = ["id"],
            childColumns = ["tourId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tourId")]
)
data class TourStopEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tourId: Long,
    val cityName: String,
    val showDate: String
)