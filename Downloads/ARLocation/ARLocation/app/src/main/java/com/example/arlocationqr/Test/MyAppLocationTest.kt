package com.example.arlocationqr.Test

import android.app.Application
import com.example.arlocationqr.dao.LocationDao
import com.example.arlocationqr.database.AppDatabase
import com.example.arlocationqr.entity.Location

class MyAppLocationTest : Application() {
    override fun onCreate() {
        super.onCreate()

        val db: AppDatabase = AppDatabase.getDatabase(this)
        val locationDao: LocationDao = db.locationDao()

        Thread(Runnable {
            locationDao.insert(Location("A구역", 100, 200))
            //테스트용
            locationDao.insert(Location("B구역", 200, 300))
        }).start()



    }
}
