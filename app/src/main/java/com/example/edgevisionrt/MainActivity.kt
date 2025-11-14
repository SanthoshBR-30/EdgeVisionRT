//
//
//package com.example.edgevisionrt
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.ImageFormat
//import android.graphics.Rect
//import android.graphics.YuvImage
//import android.media.ImageReader
//import android.opengl.GLSurfaceView
//import android.os.Bundle
//import android.util.Log
//import android.widget.Button
//import android.widget.LinearLayout
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import com.example.edgevisionrt.camera.CameraManager
//import com.example.edgevisionrt.gl.GLRenderer
//import com.example.edgevisionrt.utils.ImageProcessor
//import java.io.ByteArrayOutputStream
//
//class MainActivity : AppCompatActivity() {
//
//    companion object {
//        private const val TAG = "MainActivity"
//        private const val CAMERA_PERMISSION_REQUEST = 100
//
//        init {
//            try {
//                System.loadLibrary("edgevisionrt")
//                Log.d(TAG, "Native library loaded in MainActivity")
//            } catch (e: UnsatisfiedLinkError) {
//                Log.e(TAG, "Failed to load native library: ${e.message}")
//            }
//        }
//    }
//
//    private lateinit var cameraManager: CameraManager
//    private lateinit var imageProcessor: ImageProcessor
//    private lateinit var glSurfaceView: GLSurfaceView
//    private lateinit var glRenderer: GLRenderer
//    private lateinit var statusText: TextView
//    private lateinit var toggleButton: Button
//
//    private var frameCount = 0
//    private var lastFrameTime = System.currentTimeMillis()
//    private var showEdgeDetection = true
//    private var totalProcessingTime = 0L
//    private var processedFrames = 0
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Create layout
//        val mainLayout = LinearLayout(this).apply {
//            orientation = LinearLayout.VERTICAL
//        }
//
//        // Status bar with controls
//        val controlLayout = LinearLayout(this).apply {
//            orientation = LinearLayout.HORIZONTAL
//            setPadding(20, 20, 20, 20)
//            setBackgroundColor(0xDD000000.toInt())
//        }
//
//        statusText = TextView(this).apply {
//            text = stringFromJNI()
//            textSize = 14f
//            setTextColor(0xFFFFFFFF.toInt())
//            layoutParams = LinearLayout.LayoutParams(
//                0,
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                1f
//            )
//        }
//
//        toggleButton = Button(this).apply {
//            text = "RAW FEED"
//            textSize = 12f
//            setOnClickListener {
//                showEdgeDetection = !showEdgeDetection
//                text = if (showEdgeDetection) "RAW FEED" else "EDGE DETECTION"
//                Toast.makeText(
//                    this@MainActivity,
//                    if (showEdgeDetection) "Edge Detection ON" else "Raw Feed ON",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//
//        controlLayout.addView(statusText)
//        controlLayout.addView(toggleButton)
//
//        // GLSurfaceView
//        glRenderer = GLRenderer()
//        glSurfaceView = GLSurfaceView(this).apply {
//            setEGLContextClientVersion(2)
//            setRenderer(glRenderer)
//            renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
//            layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT
//            )
//        }
//
//        mainLayout.addView(controlLayout)
//        mainLayout.addView(glSurfaceView)
//        setContentView(mainLayout)
//
//        // Initialize
//        imageProcessor = ImageProcessor()
//        cameraManager = CameraManager(this).apply {
//            onFrameAvailable = { reader ->
//                processFrame(reader)
//            }
//        }
//
//        checkCameraPermission()
//    }
//
//    private fun checkCameraPermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//            != PackageManager.PERMISSION_GRANTED) {
//            Log.d(TAG, "Requesting camera permission")
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.CAMERA),
//                CAMERA_PERMISSION_REQUEST
//            )
//        } else {
//            Log.d(TAG, "Camera permission already granted")
//            startCamera()
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == CAMERA_PERMISSION_REQUEST) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Log.d(TAG, "Camera permission granted")
//                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
//                startCamera()
//            } else {
//                Log.e(TAG, "Camera permission denied")
//                Toast.makeText(this, "Camera permission required", Toast.LENGTH_LONG).show()
//            }
//        }
//    }
//
//    private fun startCamera() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//            != PackageManager.PERMISSION_GRANTED) {
//            Log.e(TAG, "Camera permission not granted, cannot start camera")
//            Toast.makeText(this, "Camera permission required", Toast.LENGTH_LONG).show()
//            return
//        }
//
//        cameraManager.startBackgroundThread()
//        cameraManager.openCamera()
//        statusText.text = "Camera starting..."
//        Log.d(TAG, "Camera started")
//    }
//
//    private fun processFrame(reader: ImageReader) {
//        val image = reader.acquireLatestImage() ?: return
//
//        try {
//            val startTime = System.nanoTime()
//
//            // Convert YUV to Bitmap
//            val bitmap = yuv420ToBitmap(image)
//
//            if (bitmap.width == 0 || bitmap.height == 0) {
//                Log.e(TAG, "Invalid bitmap dimensions")
//                return
//            }
//
//            // Process based on toggle
//            val displayBitmap = if (showEdgeDetection) {
//                imageProcessor.processImage(bitmap, ImageProcessor.PROCESSING_CANNY)
//            } else {
//                bitmap
//            }
//
//            val endTime = System.nanoTime()
//            val processingTime = (endTime - startTime) / 1_000_000 // Convert to ms
//
//            totalProcessingTime += processingTime
//            processedFrames++
//
//            // Calculate FPS and average processing time
//            frameCount++
//            val currentTime = System.currentTimeMillis()
//            if (currentTime - lastFrameTime >= 1000) {
//                val fps = frameCount
//                val avgProcessing = if (processedFrames > 0) totalProcessingTime / processedFrames else 0
//
//                runOnUiThread {
//                    val mode = if (showEdgeDetection) "Edge Detection" else "Raw Feed"
//                    statusText.text = "$mode | FPS: $fps | ${bitmap.width}x${bitmap.height} | Proc: ${avgProcessing}ms"
//                }
//
//                frameCount = 0
//                lastFrameTime = currentTime
//                totalProcessingTime = 0
//                processedFrames = 0
//            }
//
//            // Update OpenGL texture
//            glRenderer.updateBitmap(displayBitmap)
//
//            // Clean up
//            if (bitmap != displayBitmap) {
//                bitmap.recycle()
//            }
//
//        } catch (e: Exception) {
//            Log.e(TAG, "Error processing frame: ${e.message}", e)
//        } finally {
//            image.close()
//        }
//    }
//
//    private fun yuv420ToBitmap(image: android.media.Image): Bitmap {
//        try {
//            val planes = image.planes
//            val yBuffer = planes[0].buffer
//            val uBuffer = planes[1].buffer
//            val vBuffer = planes[2].buffer
//
//            val ySize = yBuffer.remaining()
//            val uSize = uBuffer.remaining()
//            val vSize = vBuffer.remaining()
//
//            val nv21 = ByteArray(ySize + uSize + vSize)
//
//            yBuffer.get(nv21, 0, ySize)
//            vBuffer.get(nv21, ySize, vSize)
//            uBuffer.get(nv21, ySize + vSize, uSize)
//
//            val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
//            val out = ByteArrayOutputStream()
//            yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 100, out)
//            val imageBytes = out.toByteArray()
//
//            return android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
//        } catch (e: Exception) {
//            Log.e(TAG, "YUV conversion error: ${e.message}", e)
//            return Bitmap.createBitmap(640, 480, Bitmap.Config.ARGB_8888)
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        glSurfaceView.onResume()
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//            == PackageManager.PERMISSION_GRANTED) {
//            cameraManager.startBackgroundThread()
//        }
//    }
//
//    override fun onPause() {
//        glSurfaceView.onPause()
//        cameraManager.closeCamera()
//        cameraManager.stopBackgroundThread()
//        super.onPause()
//    }
//
//    private external fun stringFromJNI(): String
//}
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
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.edgevisionrt.camera.CameraManager
import com.example.edgevisionrt.gl.GLRenderer
import com.example.edgevisionrt.utils.ImageProcessor
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

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
    private lateinit var toggleButton: Button

    private var frameCount = 0
    private var lastFrameTime = System.currentTimeMillis()
    private var showEdgeDetection = true
    private var totalProcessingTime = 0L
    private var processedFrames = 0
    private var sampleFrameSaved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create layout
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val controlLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(20, 20, 20, 20)
            setBackgroundColor(0xDD000000.toInt())
        }

        statusText = TextView(this).apply {
            text = stringFromJNI()
            textSize = 14f
            setTextColor(0xFFFFFFFF.toInt())
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        toggleButton = Button(this).apply {
            text = "RAW FEED"
            textSize = 12f
            setOnClickListener {
                showEdgeDetection = !showEdgeDetection
                text = if (showEdgeDetection) "RAW FEED" else "EDGE DETECTION"
                Toast.makeText(
                    this@MainActivity,
                    if (showEdgeDetection) "Edge Detection ON" else "Raw Feed ON",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        controlLayout.addView(statusText)
        controlLayout.addView(toggleButton)

        glRenderer = GLRenderer()
        glSurfaceView = GLSurfaceView(this).apply {
            setEGLContextClientVersion(2)
            setRenderer(glRenderer)
            renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        mainLayout.addView(controlLayout)
        mainLayout.addView(glSurfaceView)
        setContentView(mainLayout)

        imageProcessor = ImageProcessor()
        cameraManager = CameraManager(this).apply {
            onFrameAvailable = { reader ->
                processFrame(reader)
            }
        }

        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST
            )
        } else {
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
                startCamera()
            } else {
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Camera permission required", Toast.LENGTH_LONG).show()
            return
        }

        cameraManager.startBackgroundThread()
        cameraManager.openCamera()
        statusText.text = "Camera starting..."
    }

    private fun processFrame(reader: ImageReader) {
        val image = reader.acquireLatestImage() ?: return

        try {
            val startTime = System.nanoTime()

            val bitmap = yuv420ToBitmap(image)

            val displayBitmap = if (showEdgeDetection) {
                imageProcessor.processImage(bitmap, ImageProcessor.PROCESSING_CANNY)
            } else bitmap

            // ⭐ Save processed frame once after 30 frames
            if (frameCount == 30 && !sampleFrameSaved) {
                saveProcessedFrame(displayBitmap)
                sampleFrameSaved = true
            }

            val endTime = System.nanoTime()
            val processingTime = (endTime - startTime) / 1_000_000
            totalProcessingTime += processingTime
            processedFrames++

            frameCount++
            val currentTime = System.currentTimeMillis()

            if (currentTime - lastFrameTime >= 1000) {
                val fps = frameCount
                val avgProcessing = if (processedFrames > 0) totalProcessingTime / processedFrames else 0

                runOnUiThread {
                    val mode = if (showEdgeDetection) "Edge Detection" else "Raw Feed"
                    statusText.text = "$mode | FPS: $fps | ${bitmap.width}x${bitmap.height} | Proc: ${avgProcessing}ms"
                }

                frameCount = 0
                lastFrameTime = currentTime
                totalProcessingTime = 0
                processedFrames = 0
            }

            glRenderer.updateBitmap(displayBitmap)

            if (bitmap != displayBitmap) bitmap.recycle()

        } catch (e: Exception) {
            Log.e(TAG, "Error processing frame: ${e.message}", e)
        } finally {
            image.close()
        }
    }

    private fun yuv420ToBitmap(image: android.media.Image): Bitmap {
        return try {
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

            android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        } catch (e: Exception) {
            Log.e(TAG, "YUV conversion error: ${e.message}", e)
            Bitmap.createBitmap(640, 480, Bitmap.Config.ARGB_8888)
        }
    }

    // ⭐ Save processed frame to internal storage
    private fun saveProcessedFrame(bitmap: Bitmap) {
        try {
            val file = File(getExternalFilesDir(null), "processed_frame.png")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()
            Log.d(TAG, "Saved processed frame to: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save frame: ${e.message}")
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
