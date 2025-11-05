package com.example.arlocationqr

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arlocationqr.ar.ArNavigationActivity
import kotlinx.coroutines.launch

class DestinationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DestinationScreen()
        }
    }
}

@Composable
fun DestinationScreen() {
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    var textState by remember { mutableStateOf(TextFieldValue("")) }
    val destinations = listOf("프린트실", "회의실", "화장실")
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C2C2E)) // ✅ 차콜 회색 배경
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        // ✅ 팝업 (AR 안내 시작)
        if (showDialog) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .wrapContentHeight()
                        .padding(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "AR 안내를 시작합니다.",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )

                        // ✅ 하단 파란 확인 버튼
                        Button(
                            onClick = {
                                showDialog = false
                                // ✅ AR 화면으로 이동
                                val intent = Intent(context, ArNavigationActivity::class.java)
                                intent.putExtra("startZone", "A구역") // 출발지
                                intent.putExtra("endZone", textState.text) // 선택된 목적지
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("확인", fontSize = 18.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        // ✅ 메인 UI
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter)
        ) {
            // 제목
            Text(
                text = "목적지 검색",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = textState,
                onValueChange = {
                    textState = it.copy(selection = TextRange(it.text.length))
                },
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                placeholder = { Text("목적지 입력", color = Color.Gray) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.White
                )
            )

            Spacer(Modifier.height(20.dp))

            val filtered = remember(textState.text) {
                if (textState.text.isBlank()) destinations
                else destinations.filter { it.contains(textState.text, ignoreCase = true) }
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered) { zone ->
                    Text(
                        text = zone,
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                textState = TextFieldValue(zone, selection = TextRange(zone.length))
                            }
                            .padding(vertical = 4.dp)
                    )
                }
            }
        }

        // ✅ 하단 중앙 파란 버튼
        Button(
            onClick = {
                if (textState.text.isNotBlank()) {
                    coroutineScope.launch {
                        showDialog = true
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.9f)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
        ) {
            Text("확인", fontSize = 18.sp, color = Color.White)
        }
    }
}


