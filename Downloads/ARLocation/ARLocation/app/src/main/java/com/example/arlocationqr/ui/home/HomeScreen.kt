package com.example.arlocationqr.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arlocationqr.R
import androidx.compose.animation.core.tween

@Composable
fun HomeScreen(
    onQRClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val fade = remember { Animatable(0f) }
    LaunchedEffect(Unit) { fade.animateTo(1f, tween(900)) }

    var menuOpen by remember { mutableStateOf(false) }

    val slideOffset by animateDpAsState(
        targetValue = if (menuOpen) 0.dp else 260.dp,
        animationSpec = tween(350, easing = FastOutSlowInEasing)
    )

    val qrScale = remember { Animatable(1f) }
    var qrClickTrigger by remember { mutableStateOf(false) }

    LaunchedEffect(qrClickTrigger) {
        if (qrClickTrigger) {
            qrScale.animateTo(0.92f, tween(120))
            qrScale.animateTo(1f, tween(120))
            qrClickTrigger = false
        }
    }

    val fullText = "AR 기반 실내 지도 네비게이션"
    var textToShow by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        while (true) {
            fullText.forEachIndexed { index, _ ->
                textToShow = fullText.substring(0, index + 1)
                kotlinx.coroutines.delay(45)
            }
            kotlinx.coroutines.delay(5000)
            textToShow = ""
        }
    }

    val backgroundBrush = Brush.verticalGradient(
        listOf(Color(0xFFF4EDE3), Color(0xFFE9DFD2))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .graphicsLayer(alpha = fade.value)
    ) {

        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menu",
            tint = Color(0xFF2B3A55),
            modifier = Modifier
                .padding(top = 28.dp, end = 22.dp)
                .size(32.dp)
                .align(Alignment.TopEnd)
                .clickable { menuOpen = !menuOpen }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Library Navigation",
                    fontSize = 28.sp,
                    color = Color(0xFF2B3A55)
                )

                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .width(260.dp)
                        .height(3.dp)
                        .background(Color(0xFFE4DCC3), RoundedCornerShape(50))
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = textToShow,
                    fontSize = 17.sp,
                    color = Color(0xFF6A6A6A)
                )
            }

            Box(
                modifier = Modifier
                    .scale(qrScale.value)
                    .offset(y = (-30).dp)
                    .width(260.dp)
                    .height(230.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        qrClickTrigger = true
                        onQRClick()
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.qr),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(260.dp)
                .offset(x = slideOffset)
                .background(Color(0xFFE5DACD))   // ← 톤다운된 베이지
                .align(Alignment.CenterEnd)
                .pointerInput(menuOpen) {
                    detectDragGestures { _, dragAmount ->
                        if (dragAmount.x > 20) menuOpen = false
                    }
                }
        ) {

            Box(
                modifier = Modifier
                    .width(38.dp)
                    .fillMaxHeight()
                    .align(Alignment.CenterStart)
                    .clickable { menuOpen = false },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "◀",
                    fontSize = 20.sp,
                    color = Color(0xFF2B3A55)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 60.dp, top = 70.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "메뉴",
                    fontSize = 22.sp,
                    color = Color(0xFF2B3A55),
                    modifier = Modifier.padding(bottom = 30.dp)
                )

                Box(
                    modifier = Modifier
                        .width(145.dp)     // ← 메뉴 전체 중앙 위치를 위해 크기 튜닝
                        .height(50.dp)
                        .border(
                            width = 2.dp,
                            color = Color(0xFF2B3A55),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onProfileClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "앱 정보",
                        fontSize = 18.sp,
                        color = Color(0xFF2B3A55)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "© 2025 Baewha AR Navigation • v1.0",
                fontSize = 12.sp,
                color = Color(0x88333333)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "갑작스럽조",
                fontSize = 12.sp,
                color = Color(0x88333333)
            )
        }
    }
}
