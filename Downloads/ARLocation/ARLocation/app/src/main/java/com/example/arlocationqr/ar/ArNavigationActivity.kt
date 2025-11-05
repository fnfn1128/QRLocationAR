package com.example.arlocationqr.ar

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.arlocationqr.dao.LocationDao
import com.example.arlocationqr.database.AppDatabase
import com.example.arlocationqr.entity.Location
import com.google.ar.core.ArCoreApk
import io.github.sceneview.SceneView
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.CylinderNode
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.sqrt
import kotlinx.coroutines.delay
import io.github.sceneview.ar.ARSceneView

class ArNavigationActivity : ComponentActivity() {

    private var sceneView: SceneView? = null
    private lateinit var locationDao: LocationDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val startZone = intent.getStringExtra("startZone") ?: "A구역"
        val endZone = intent.getStringExtra("endZone") ?: "프린트실"

        locationDao = AppDatabase.getDatabase(this).locationDao()

        setContent {
            Box(Modifier.fillMaxSize()) {
                AndroidView(factory = { ctx ->
                    SceneView(ctx).apply {
                        this@ArNavigationActivity.sceneView = this
                    }
                }, modifier = Modifier.fillMaxSize())

                ArOverlayUI(startZone, endZone) {
                    ensureArCoreInstalled()
                    seedIfNeeded()
                    renderARPath(startZone, endZone)
                }
            }
        }
    }

    private fun ensureArCoreInstalled() {
        try {
            val availability = ArCoreApk.getInstance().checkAvailability(this)
            Log.d("AR", "ARCore availability: $availability")
        } catch (e: Exception) {
            Log.w("AR", "ARCore check failed: ${e.message}")
        }
    }

    /** 기본 위치 데이터 삽입 */
    private fun seedIfNeeded() {
        Thread {
            try {
                if (locationDao.count() == 0) {
                    locationDao.insert(Location("A구역", 0, 0))
                    locationDao.insert(Location("프린트실", 10, 0))
                    locationDao.insert(Location("회의실", 15, 5))
                    locationDao.insert(Location("화장실", 6, -4))
                }
            } catch (e: Exception) {
                Log.e("AR", "Seed failed", e)
            }
        }.start()
    }

    /** 출발지→도착지 경로 표시 */
    private fun renderARPath(startZone: String, endZone: String) {
        Thread {
            val start = locationDao.getLocationByZone(startZone)
            val end = locationDao.getLocationByZone(endZone)
            if (start == null || end == null) {
                Log.e("AR", "존재하지 않는 위치: start=$startZone, end=$endZone")
                return@Thread
            }

            val s = start.toWorld()
            val e = end.toWorld()

            runOnUiThread {
                val sv = sceneView
                if (sv == null) {
                    Toast.makeText(this, "AR 초기화 중...", Toast.LENGTH_SHORT).show()
                    return@runOnUiThread
                }

                // 출발점과 도착점 마커 추가
                addDotMarker(s, 0xFFEB5757.toInt())  // 빨간색
                addDotMarker(e, 0xFF2F80ED.toInt())  // 파란색

                // 경로선 그리기
                drawPathLine(s, e, 0.25f)

                // 화살표 추가
                addArrowAtEnd(s, e)
            }
        }.start()
    }

    /** 점 마커 (작은 Cylinder로 구현) */
    private fun addDotMarker(pos: Position, color: Int) {
        val sv = sceneView ?: return

        try {
            val node = CylinderNode(
                engine = sv.engine,
                radius = 0.05f,
                height = 0.1f,
                materialInstance = sv.materialLoader.createColorInstance(
                    color = color,
                    metallic = 0.5f,
                    roughness = 0.2f,
                    reflectance = 0.4f
                )
            ).apply {
                position = pos
            }
            sv.addChildNode(node)
            Log.d("AR", "Marker added at position: $pos")
        } catch (e: Exception) {
            Log.e("AR", "addDotMarker failed: ${e.message}", e)
        }
    }

    /** 경로 연결선 */
    private fun drawPathLine(s: Position, e: Position, step: Float) {
        val dir = e - s
        val length = dir.length()
        val n = max(1, (length / step).toInt())
        val unit = dir.normalized()
        var cursor = Position(s.x, s.y, s.z)

        repeat(n) {
            val next = cursor + (unit * step)
            addPathSegment(cursor, next, 0xFF2F80ED.toInt())
            cursor = next
        }
    }

    /** 경로 세그먼트 (Cylinder로 구현) */
    private fun addPathSegment(a: Position, b: Position, color: Int) {
        val sv = sceneView ?: return
        val ab = b - a
        val dist = ab.length()
        val mid = a + (ab * 0.5f)

        try {
            // Cylinder를 경로 방향으로 회전
            val yaw = yawFrom(ab)
            val pitch = pitchFrom(ab)

            val node = CylinderNode(
                engine = sv.engine,
                radius = 0.015f,
                height = dist,
                materialInstance = sv.materialLoader.createColorInstance(
                    color = color,
                    metallic = 0.3f,
                    roughness = 0.5f,
                    reflectance = 0.3f
                )
            ).apply {
                position = mid
                rotation = Rotation(x = pitch, y = yaw, z = 0f)
            }
            sv.addChildNode(node)
            Log.d("AR", "Path segment added from $a to $b")
        } catch (e: Exception) {
            Log.e("AR", "addPathSegment failed: ${e.message}", e)
        }
    }

    /** 끝 화살표 (Cylinder로 구현) */
    private fun addArrowAtEnd(s: Position, e: Position) {
        val sv = sceneView ?: return
        val dir = e - s
        val arrowPos = s + (dir * 0.95f)

        try {
            val yaw = yawFrom(dir)
            val pitch = pitchFrom(dir)

            val node = CylinderNode(
                engine = sv.engine,
                radius = 0.05f,
                height = 0.2f,
                materialInstance = sv.materialLoader.createColorInstance(
                    color = 0xFF2F80ED.toInt(),
                    metallic = 0.5f,
                    roughness = 0.2f,
                    reflectance = 0.5f
                )
            ).apply {
                position = arrowPos
                rotation = Rotation(x = pitch, y = yaw, z = 0f)
            }
            sv.addChildNode(node)
            Log.d("AR", "Arrow added at: $arrowPos")
        } catch (e: Exception) {
            Log.e("AR", "addArrowAtEnd failed: ${e.message}", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sceneView = null
    }
}

@Composable
fun ArOverlayUI(startZone: String, endZone: String, onReady: () -> Unit) {
    var showLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(1500)
        onReady()
        delay(3000)
        showLoading = false
    }

    if (showLoading) {
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(listOf(Color(0xFF2C2C2E), Color(0xFF1C1C1E)))
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "AR 안내 준비중...",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    "출발지 : $startZone\n목적지 : $endZone",
                    color = Color(0xFFB0B0B0),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(22.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0x33007AFF)),
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF007AFF),
                            strokeWidth = 5.dp,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "경로 데이터를 불러오는 중입니다...",
                    color = Color(0xFFB0B0B0),
                    fontSize = 14.sp
                )
            }
        }
    }
}

// Extension functions
private operator fun Position.minus(other: Position): Position =
    Position(x - other.x, y - other.y, z - other.z)

private operator fun Position.plus(other: Position): Position =
    Position(x + other.x, y + other.y, z + other.z)

private operator fun Position.times(scalar: Float): Position =
    Position(x * scalar, y * scalar, z * scalar)

private fun Position.length(): Float = sqrt(x * x + y * y + z * z)

private fun Position.normalized(): Position {
    val len = length()
    return if (len > 1e-6f) Position(x / len, y / len, z / len) else Position(0f, 0f, 0f)
}

private fun yawFrom(v: Position): Float =
    Math.toDegrees(atan2(v.x.toDouble(), -v.z.toDouble())).toFloat()

private fun pitchFrom(v: Position): Float {
    val horizontalLength = sqrt(v.x * v.x + v.z * v.z)
    return Math.toDegrees(atan2(v.y.toDouble(), horizontalLength.toDouble())).toFloat()
}

private fun Location.toWorld(gridMeter: Float = 0.2f): Position =
    Position(this.x * gridMeter, 0f, -this.y * gridMeter)