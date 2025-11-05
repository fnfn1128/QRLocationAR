package com.example.arlocationqr.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.arlocationqr.entity.Location

@Dao
interface LocationDao {
    // ✅ null 허용 제거
    @Insert
    fun insert(location: Location)

    // ✅ zone은 여전히 null 허용 가능 (문제 없음)
    @Query("SELECT * FROM location WHERE zone = :zone LIMIT 1")
    fun getLocationByZone(zone: String?): Location?

    @Query("SELECT COUNT(*) FROM location")
    fun count(): Int
}

