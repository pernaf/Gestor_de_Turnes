package com.gabrielcarvalho.tourfinance.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tours",
    foreignKeys = [ForeignKey(
        entity = BandEntity::class,
        parentColumns = ["id"],
        childColumns = ["bandId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("bandId")]
)
data class TourEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bandId: Long,
    val name: String,
    val startDate: String,
    val endDate: String? = null,
    val status: String = "ACTIVE",
    val notes: String = ""
)