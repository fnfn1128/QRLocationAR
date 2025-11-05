package com.example.arlocationqr.qr

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.arlocationqr.DestinationActivity
import com.example.arlocationqr.R
import com.example.arlocationqr.database.AppDatabase
import com.example.arlocationqr.entity.Location
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

class QrActivity : ComponentActivity() {

    private lateinit var previewView: PreviewView
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var isScanned = false // ✅ 중복 방지 flag
    private val CAMERA_PERMISSION_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 권한 체크
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            setupCameraUI()
            startCamera()
        }
    }

    private fun setupCameraUI() {
        val container = FrameLayout(this)

        // ✅ 카메라 프리뷰
        previewView = PreviewView(this)
        previewView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        container.addView(previewView)

        // ✅ 오버레이 (중앙 프레임 + 안내문)
        val overlay = layoutInflater.inflate(R.layout.qr_overlay, container, false)
        container.addView(overlay)

        setContentView(container)
    }

    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                val scanner = BarcodeScanning.getClient()

                analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                    val mediaImage = imageProxy.image
                    if (mediaImage != null && !isScanned) {
                        val image = InputImage.fromMediaImage(
                            mediaImage,
                            imageProxy.imageInfo.rotationDegrees
                        )

                        scanner.process(image)
                            .addOnSuccessListener { barcodes ->
                                for (barcode in barcodes) {
                                    barcode.rawValue?.let { qrValue ->
                                        if (!isScanned) {
                                            isScanned = true // ✅ 중복 방지
                                            Log.d("QRScanner", "출발 지점: $qrValue")
                                            Toast.makeText(
                                                this,
                                                "출발 지점: $qrValue",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            saveToDatabase(qrValue)
                                        }
                                    }
                                }
                            }
                            .addOnFailureListener {
                                Log.e("QRScanner", "인식 실패: ${it.message}")
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    } else {
                        imageProxy.close()
                    }
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, analysis
                )

            } catch (e: Exception) {
                Log.e("CameraX", "카메라 초기화 실패", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun saveToDatabase(qrValue: String) {
        val db = AppDatabase.getDatabase(this)
        val dao = db.locationDao()

        Thread {
            try {
                val existing = dao.getLocationByZone(qrValue)
                if (existing == null) {
                    dao.insert(Location(qrValue, 0, 0))
                }

                runOnUiThread {
                    val intent = Intent(this, DestinationActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "QR 저장 실패, 다시 인식해주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupCameraUI()
                startCamera()
            } else {
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
