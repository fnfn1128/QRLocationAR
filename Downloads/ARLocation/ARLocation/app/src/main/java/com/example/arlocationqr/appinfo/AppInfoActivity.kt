package com.example.arlocationqr.appinfo

import android.content.Intent
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.sp
import com.example.arlocationqr.MainActivity

class AppInfoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("intro_prefs", Context.MODE_PRIVATE)
        val isFirstRun = prefs.getBoolean("isFirstRun", true)

        if (isFirstRun) {
            prefs.edit().putBoolean("isFirstRun", false).apply()
        }

        setContent {
            AppInfoScreen(
                onClick = {
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                },
                firstRun = isFirstRun
            )
        }
    }
}

@Composable
fun AppInfoScreen(
    onClick: () -> Unit,
    firstRun: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition()

    val floatY by infiniteTransition.animateFloat(
        initialValue = -35f,
        targetValue = 35f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 4200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    val floatX by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 5200,
                easing = LinearOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    val randomShift = remember { (6..14).random().toFloat() }

    val fadeInAnim = remember { Animatable(if (firstRun) 0f else 1f) }

    LaunchedEffect(Unit) {
        if (firstRun) {
            fadeInAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1800, easing = LinearEasing)
            )
        }
    }

    val breatheAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1800,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1E2235),
            Color(0xFF2F3B52)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .clickable { onClick() }          // ⭐ 화면 터치 → 홈 이동
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .size(230.dp)
                .offset(
                    x = floatX.dp,
                    y = (floatY + randomShift).dp
                )
                .blur(130.dp)
                .clip(CircleShape)
                .background(Color(0xFF798ECF).copy(alpha = 0.55f))
                .align(Alignment.TopCenter)
        )

        Box(
            modifier = Modifier
                .size(190.dp)
                .offset(
                    x = (-floatX).dp,
                    y = (-floatY - randomShift).dp
                )
                .blur(150.dp)
                .clip(CircleShape)
                .background(Color(0xFF9BB2E3).copy(alpha = 0.50f))
                .align(Alignment.BottomCenter)
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = "AR Library Navigation",
                color = Color(0xFFF5EDE1),
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = TextUnit(0.8f, TextUnitType.Sp),
                modifier = Modifier.alpha(fadeInAnim.value)
            )

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "한 걸음, 한 걸음\n당신을 목적지까지 🍀",
                color = Color(0xFFE8EEF5),
                fontSize = 20.sp,
                lineHeight = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(fadeInAnim.value)
            )

            Spacer(modifier = Modifier.height(30.dp))

            val introAlpha by animateFloatAsState(
                targetValue = fadeInAnim.value,
                animationSpec = tween(2000)
            )

            Text(
                text = "배화여대 도서관 전용 AR 네비게이션 앱입니다.\n" +
                        "QR을 스캔하면 목적지까지\n" +
                        "작고 귀여운 화살표가 길잡이가 되어드릴 거예요.\n\n" +
                        "이제, 길 잃지 말아요. :)",
                color = Color(0xFFE4EBF3),
                fontSize = 16.sp,
                lineHeight = 26.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(introAlpha * breatheAlpha)
            )

            Spacer(modifier = Modifier.height(55.dp))

            Text(
                text = "화면을 터치해 시작하세요",
                color = Color(0x80F5EDE1),
                fontSize = 18.sp,
                letterSpacing = 0.4.sp,
                modifier = Modifier.alpha(fadeInAnim.value)
            )
        }
    }
}
