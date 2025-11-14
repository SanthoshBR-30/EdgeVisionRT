package com.example.edgevisionrt.utils

import android.graphics.Bitmap
import android.util.Log

class ImageProcessor {

    companion object {
        private const val TAG = "ImageProcessor"

        // Processing types
        const val PROCESSING_CANNY = 0
        const val PROCESSING_GRAYSCALE = 1

        init {
            try {
                System.loadLibrary("edgevisionrt")
                Log.d(TAG, "Native library loaded successfully")
            } catch (e: UnsatisfiedLinkError) {
                Log.e(TAG, "Failed to load native library: ${e.message}")
            }
        }
    }

    /**
     * Process image using native OpenCV
     * @param inputBitmap Input image
     * @param processingType 0 = Canny Edge, 1 = Grayscale
     * @return Processed bitmap
     */
    fun processImage(inputBitmap: Bitmap, processingType: Int = PROCESSING_CANNY): Bitmap {
        val outputBitmap = Bitmap.createBitmap(
            inputBitmap.width,
            inputBitmap.height,
            Bitmap.Config.ARGB_8888
        )

        processImageNative(inputBitmap, outputBitmap, processingType)
        return outputBitmap
    }

    private external fun processImageNative(
        bitmapIn: Bitmap,
        bitmapOut: Bitmap,
        processingType: Int
    )
}