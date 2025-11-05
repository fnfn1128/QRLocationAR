package com.example.arlocationqr.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
class Location(var zone: String?, var x: Int, var y: Int) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
