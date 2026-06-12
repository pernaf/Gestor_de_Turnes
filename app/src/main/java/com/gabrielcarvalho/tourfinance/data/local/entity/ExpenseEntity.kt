package com.gabrielcarvalho.tourfinance.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    foreignKeys = [ForeignKey(
        entity = TourEntity::class,
        parentColumns = ["id"],
        childColumns = ["tourId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("tourId")]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tourId: Long,
    val description: String,
    val amount: Double,
    val category: String,
    val date: String,
    val notes: String,
    val city: String = ""
)