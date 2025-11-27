package com.example.arlocationqr.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class Location(
    @PrimaryKey val id: String,
    val name: String,
    val x: Double,
    val y: Double,
    val z: Double
)
