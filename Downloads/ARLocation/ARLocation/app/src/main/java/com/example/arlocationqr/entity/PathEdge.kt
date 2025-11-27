package com.example.arlocationqr.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "path_edges")
data class PathEdge(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fromId: String,
    val toId: String,
    val distance: Double
)
