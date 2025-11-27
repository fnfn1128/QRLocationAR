package com.example.arlocationqr.destination

import android.content.Intent
import android.os.Bundle
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.AnimatedVisibility
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.arlocationqr.MainActivity
import com.example.arlocationqr.R
import com.example.arlocationqr.ar.ArNavigationActivity

data class Place(
    val name: String,
    val floor: String,
    val description: String,
    val imageRes: Int
)

class DestinationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { DestinationSearchScreen() }
    }
}

@Composable
fun DestinationSearchScreen() {

    val context = LocalContext.current

    var fontSize by remember { mutableStateOf(18f) }
    var textState by remember { mutableStateOf(TextFieldValue("")) }
    var selectedPlace by remember { mutableStateOf<Place?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val allPlaces = listOf(
        Place("시네마룸", "3층", "학생들이 쉬면서 영화를 볼 수 있는 공간", R.drawable.place_cinema),
        Place("회의실", "3층", "단체 회의를 진행할 수 있는 공간", R.drawable.place_meeting),
        Place("프린트실", "3층", "문서 출력, 복사, 스캔 가능", R.drawable.place_print),
        Place("사무실", "3층", "도서관 운영 관련 직원 근무 공간", R.drawable.place_office),
        Place("화장실", "2층", "2층 도서관 외부 입구 오른편에 위치한 화장실", R.drawable.place_wc2),
        Place("만화방", "지하 1층", "조용히 만화를 읽을 수 있는 휴식 공간", R.drawable.place_manga),
        Place("화장실", "3층", "3층 엘리베이터 왼쪽 편에 위치한 화장실", R.drawable.place_wc3)
    )

    val floorOrder = listOf("3층", "2층", "지하 1층")

    val filtered = remember(textState.text) {
        if (textState.text.isBlank()) allPlaces
        else allPlaces.filter { it.name.contains(textState.text, ignoreCase = true) }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFF3EFE7))
            .padding(20.dp)
    ) {

        Column {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "목적지 검색",
                    fontSize = (fontSize + 6).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2B3A55)
                )

                Button(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(Color(0xFF4A6FA5)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("홈으로", color = Color.White, fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = textState,
                onValueChange = { textState = it.copy(selection = TextRange(it.text.length)) },
                placeholder = { Text("검색어를 입력하세요") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, null) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2B3A55),
                    unfocusedBorderColor = Color(0xFF2B3A55),
                    cursorColor = Color(0xFF2B3A55)
                )
            )

            Spacer(Modifier.height(14.dp))

            Text("글자 크기 (${fontSize.toInt()} pt)", color = Color(0xFF2B3A55))
            Slider(
                value = fontSize,
                onValueChange = { fontSize = it },
                valueRange = 14f..26f,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF2B3A55),
                    activeTrackColor = Color(0xFF2B3A55),
                    inactiveTrackColor = Color(0xFF8EA3C5)
                )
            )

            Spacer(Modifier.height(12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(18.dp)) {

                floorOrder.forEach { floor ->
                    val itemsInFloor = filtered.filter { it.floor == floor }
                    if (itemsInFloor.isEmpty()) return@forEach

                    item {
                        Text(
                            text = floor,
                            fontSize = (fontSize + 4).sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF4A4A4A)
                        )
                    }

                    items(itemsInFloor) { place ->

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(74.dp)
                                .clickable { selectedPlace = place },
                            shape = RoundedCornerShape(14.dp),
                            elevation = CardDefaults.cardElevation(0.dp),
                            colors = CardDefaults.cardColors(Color.White)
                        ) {
                            Row(
                                Modifier
                                    .fillMaxSize()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                val thumbPainter = rememberAsyncImagePainter(place.imageRes)

                                Image(
                                    thumbPainter,
                                    null,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )

                                Spacer(Modifier.width(14.dp))

                                Text(
                                    place.name,
                                    fontSize = fontSize.sp,
                                    color = Color(0xFF2B3A55)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Blur + dim background
        if (selectedPlace != null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .blur(10.dp)
                    .background(Color(0x66000000))
                    .clickable { selectedPlace = null }
            )
        }

        // 팝업
        AnimatedVisibility(
            visible = selectedPlace != null,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            enter = EnterTransition.None,
            exit = ExitTransition.None
        ) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                selectedPlace?.let { place ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        shape = RoundedCornerShape(26.dp),
                        colors = CardDefaults.cardColors(Color.White),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {

                        Column(
                            Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            val popupPainter = rememberAsyncImagePainter(place.imageRes)

                            Image(
                                popupPainter,
                                null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(170.dp)
                                    .clip(RoundedCornerShape(20.dp))
                            )

                            Text(place.name, fontSize = (fontSize + 6).sp, fontWeight = FontWeight.Bold)
                            Text(place.floor, fontSize = fontSize.sp, color = Color.Gray)
                            Text(place.description, fontSize = fontSize.sp)

                            Button(
                                onClick = {
                                    val intent = Intent(context, ArNavigationActivity::class.java)
                                    intent.putExtra("endZone", place.name)
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(Color(0xFF4A6FA5)),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("AR 안내 시작", color = Color.White, fontSize = (fontSize + 2).sp)
                            }

                            Text(
                                "닫기",
                                Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { selectedPlace = null },
                                color = Color(0xFF4A4A4A),
                                fontSize = fontSize.sp
                            )
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("알림") },
                text = { Text("QR을 다시 인식해야 합니다.\n홈으로 이동할까요?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                        }
                    ) { Text("이동") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("취소")
                    }
                }
            )
        }
    }
}
