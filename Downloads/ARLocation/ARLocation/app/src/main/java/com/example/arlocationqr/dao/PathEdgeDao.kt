package com.example.arlocationqr.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.arlocationqr.entity.PathEdge

@Dao
interface PathEdgeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(edge: PathEdge)

    @Query("SELECT * FROM path_edges")
    suspend fun getAllEdges(): List<PathEdge>
}
