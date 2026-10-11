package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workers")
data class WorkerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val role: String = "Master Painter (कारीगर)", // Master Painter (कारीगर), Helper (मजदूर), Polisher (पॉलिशर)
    val dailyRate: Double = 900.0,
    val mobileNumber: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
