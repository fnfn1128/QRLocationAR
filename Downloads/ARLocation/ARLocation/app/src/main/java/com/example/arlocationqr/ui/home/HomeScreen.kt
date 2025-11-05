package com.example.arlocationqr.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arlocationqr.R
import android.content.Intent
import com.example.arlocationqr.qr.QrActivity

@Composable
fun HomeScreen(
    onQRClick: () -> Unit,
    onDestinationClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Library Navigation",
            fontSize = 18.sp,
            color = Color.Black,
            style = MaterialTheme.typography.titleMedium
        )
        Divider(
            modifier = Modifier
                .padding(top = 4.dp, bottom = 32.dp)
                .width(160.dp),
            color = Color.Gray.copy(alpha = 0.4f),
            thickness = 1.dp
        )

        // QR 버튼
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(180.dp) // RoundedCornerShape 제거, 높이 바로 적용
                .background(Color(0xFFEAEAEA))
                .clickable { onQRClick() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.qr),
                contentDescription = "QR Camera",
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // 목적지 입력 버튼 + 검색 버튼 나란히
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(
                onClick = { onDestinationClick() },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .height(50.dp)
            ) {
                Text("목적지 입력")
            }

            OutlinedButton(
                onClick = { onSearchClick() },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
                    .height(50.dp)
            ) {
                Text("검색")
            }
        }
    }
}




