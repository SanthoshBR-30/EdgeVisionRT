package com.example.edgevisionrt

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.ImageReader
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.edgevisionrt.camera.CameraManager
import com.example.edgevisionrt.gl.GLRenderer
import com.example.edgevisionrt.utils.ImageProcessor
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val CAMERA_PERMISSION_REQUEST = 100

        init {
            try {
                System.loadLibrary("edgevisionrt")
                Log.d(TAG, "Native library loaded in MainActivity")
            } catch (e: UnsatisfiedLinkError) {
                Log.e(TAG, "Failed to load native library: ${e.message}")
            }
        }
    }

    private lateinit var cameraManager: CameraManager
    private lateinit var imageProcessor: ImageProcessor
    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var glRenderer: GLRenderer
    private lateinit var statusText: TextView

    private var frameCount = 0
    private var lastFrameTime = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create layout with GLSurfaceView
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
        }

        statusText = TextView(this).apply {
            text = stringFromJNI()
            textSize = 16f
            setPadding(20, 20, 20, 20)
            setBackgroundColor(0xDD000000.toInt())
            setTextColor(0xFFFFFFFF.toInt())
        }

        // Create GLSurfaceView
        glRenderer = GLRenderer()
        glSurfaceView = GLSurfaceView(this).apply {
            setEGLContextClientVersion(2) // OpenGL ES 2.0
            setRenderer(glRenderer)
            renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        layout.addView(statusText)
        layout.addView(glSurfaceView)
        setContentView(layout)

        // Initialize
        imageProcessor = ImageProcessor()
        cameraManager = CameraManager(this).apply {
            onFrameAvailable = { reader ->
                processFrame(reader)
            }
        }

        // Check and request camera permission
        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Requesting camera permission")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST
            )
        } else {
            Log.d(TAG, "Camera permission already granted")
            startCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Camera permission granted")
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
                startCamera()
            } else {
                Log.e(TAG, "Camera permission denied")
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Camera permission not granted, cannot start camera")
            Toast.makeText(this, "Camera permission required", Toast.LENGTH_LONG).show()
            return
        }

        cameraManager.startBackgroundThread()
        cameraManager.openCamera()
        statusText.text = "Camera starting..."
        Log.d(TAG, "Camera started")
    }

    private fun processFrame(reader: ImageReader) {
        val image = reader.acquireLatestImage() ?: return

        try {
            // Convert YUV to Bitmap
            val bitmap = yuv420ToBitmap(image)

            if (bitmap.width == 0 || bitmap.height == 0) {
                Log.e(TAG, "Invalid bitmap dimensions")
                return
            }

            // Process with native edge detection
            val processedBitmap = imageProcessor.processImage(
                bitmap,
                ImageProcessor.PROCESSING_CANNY
            )

            // Calculate FPS
            frameCount++
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastFrameTime >= 1000) {
                val fps = frameCount
                runOnUiThread {
                    statusText.text = "OpenGL Rendering | FPS: $fps | ${bitmap.width}x${bitmap.height}"
                }
                frameCount = 0
                lastFrameTime = currentTime
            }

            // Update OpenGL texture
            glRenderer.updateBitmap(processedBitmap)

            // Clean up
            if (bitmap != processedBitmap) {
                bitmap.recycle()
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error processing frame: ${e.message}", e)
        } finally {
            image.close()
        }
    }

    private fun yuv420ToBitmap(image: android.media.Image): Bitmap {
        try {
            val planes = image.planes
            val yBuffer = planes[0].buffer
            val uBuffer = planes[1].buffer
            val vBuffer = planes[2].buffer

            val ySize = yBuffer.remaining()
            val uSize = uBuffer.remaining()
            val vSize = vBuffer.remaining()

            val nv21 = ByteArray(ySize + uSize + vSize)

            yBuffer.get(nv21, 0, ySize)
            vBuffer.get(nv21, ySize, vSize)
            uBuffer.get(nv21, ySize + vSize, uSize)

            val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
            val out = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 100, out)
            val imageBytes = out.toByteArray()

            return android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        } catch (e: Exception) {
            Log.e(TAG, "YUV conversion error: ${e.message}", e)
            return Bitmap.createBitmap(640, 480, Bitmap.Config.ARGB_8888)
        }
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            cameraManager.startBackgroundThread()
        }
    }

    override fun onPause() {
        glSurfaceView.onPause()
        cameraManager.closeCamera()
        cameraManager.stopBackgroundThread()
        super.onPause()
    }

    private external fun stringFromJNI(): String
}