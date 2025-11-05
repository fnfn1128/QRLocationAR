 package com.example.arlocationqr

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arlocationqr.qr.QrActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreen(
                onQRClick = {
                    startActivity(Intent(this, QrActivity::class.java))
                },
                onDestinationClick = {
                    startActivity(Intent(this, DestinationActivity::class.java))
                }
            )
        }
    }
}

@Composable
fun HomeScreen(
    onQRClick: () -> Unit,
    onDestinationClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Library Navigation",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.qr),
            contentDescription = "QR Code Icon",
            modifier = Modifier
                .size(180.dp)
                .clickable { onQRClick() }
        )

        Spacer(modifier = Modifier.height(60.dp))

        Button(
            onClick = onDestinationClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
        ) {
            Text(
                text = "목적지 검색",
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}


/*package com.example.arlocationqr

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {

    private val CAMERA_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreen(
                onQRClick = { openCamera() }, // ✅ QR 버튼 누르면 카메라 실행
                onDestinationClick = { openDestinationSearch() } // ✅ 목적지 버튼
            )
        }
    }

    // ✅ 기본 카메라 실행
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    // ✅ 결과 처리 (필요시 QR 결과 처리 가능)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            // TODO: 여기서 QR 인식 처리 로직 추가 가능
        }
    }

    // ✅ 목적지 입력 화면 이동 (예시)
    private fun openDestinationSearch() {
        val intent = Intent(this, DestinationActivity::class.java)
        startActivity(intent)
    }
}

@Composable
fun HomeScreen(
    onQRClick: () -> Unit,
    onDestinationClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단 텍스트
        Text(
            text = "Library Navigation",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        // 중앙 QR 이미지 (버튼 역할)
        Image(
            painter = painterResource(id = R.drawable.qr),
            contentDescription = "QR Code Icon",
            modifier = Modifier
                .size(180.dp)
                .clickable { onQRClick() } // ✅ 누르면 카메라 실행
        )

        Spacer(modifier = Modifier.height(60.dp))

        // 하단 목적지 입력 버튼
        Button(
            onClick = onDestinationClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
        ) {
            Text(
                text = "목적지 검색",
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}*/
