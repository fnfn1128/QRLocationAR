package com.example.arlocationqr.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.arlocationqr.dao.LocationDao
import com.example.arlocationqr.dao.PathEdgeDao
import com.example.arlocationqr.entity.Location
import com.example.arlocationqr.entity.PathEdge

@Database(entities = [Location::class, PathEdge::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao
    abstract fun pathEdgeDao(): PathEdgeDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "arlocation-db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
