package com.example.arlocationqr

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.arlocationqr.appinfo.AppInfoActivity
import com.example.arlocationqr.qr.QrActivity
import com.example.arlocationqr.ui.home.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HomeScreen(
                onQRClick = {
                    startActivity(Intent(this, QrActivity::class.java))
                },
                onProfileClick = {
                    startActivity(Intent(this, AppInfoActivity::class.java))
                }
            )
        }
    }
}
