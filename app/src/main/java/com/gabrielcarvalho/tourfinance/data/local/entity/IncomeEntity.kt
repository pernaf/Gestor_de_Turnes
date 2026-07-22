package com.gabrielcarvalho.tourfinance.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "incomes",
    foreignKeys = [ForeignKey(
        entity = TourEntity::class,
        parentColumns = ["id"],
        childColumns = ["tourId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("tourId")]
)
data class IncomeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tourId: Long,
    val description: String,
    val amount: Double,
    val date: String,
    val type: String,
    val city: String,
    val notes: String = ""
)