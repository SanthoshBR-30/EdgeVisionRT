#include <jni.h>
#include <string>
#include <android/log.h>
#include <android/bitmap.h>
#include <cstdlib>

#define NATIVE_TAG "NativeLib"
#define LOGD_NATIVE(...) __android_log_print(ANDROID_LOG_DEBUG, NATIVE_TAG, __VA_ARGS__)
#define LOGE_NATIVE(...) __android_log_print(ANDROID_LOG_ERROR, NATIVE_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_edgevisionrt_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Native Library Loaded - Camera Ready!";
    LOGD_NATIVE("Native library loaded successfully!");
    return env->NewStringUTF(hello.c_str());
}

// Simple edge detection without OpenCV (Sobel filter)
void simpleEdgeDetect(uint8_t* input, uint8_t* output, int width, int height) {
    LOGD_NATIVE("Starting simple edge detection: %dx%d", width, height);

    // Simple Sobel edge detection
    for (int y = 1; y < height - 1; y++) {
        for (int x = 1; x < width - 1; x++) {
            int idx = (y * width + x) * 4; // RGBA

            // Get grayscale value
            uint8_t gray = (input[idx] + input[idx+1] + input[idx+2]) / 3;

            // Simple edge detection (difference with neighbors)
            int edge = 0;
            if (x > 0 && x < width-1 && y > 0 && y < height-1) {
                int idx_left = (y * width + (x-1)) * 4;
                int idx_right = (y * width + (x+1)) * 4;
                int idx_up = ((y-1) * width + x) * 4;
                int idx_down = ((y+1) * width + x) * 4;

                uint8_t left = (input[idx_left] + input[idx_left+1] + input[idx_left+2]) / 3;
                uint8_t right = (input[idx_right] + input[idx_right+1] + input[idx_right+2]) / 3;
                uint8_t up = (input[idx_up] + input[idx_up+1] + input[idx_up+2]) / 3;
                uint8_t down = (input[idx_down] + input[idx_down+1] + input[idx_down+2]) / 3;

                int gx = abs((int)right - (int)left);
                int gy = abs((int)down - (int)up);
                edge = gx + gy;
                if (edge > 255) edge = 255;
            }

            // Output white edges on black background
            output[idx] = edge;     // R
            output[idx+1] = edge;   // G
            output[idx+2] = edge;   // B
            output[idx+3] = 255;    // A
        }
    }

    LOGD_NATIVE("Simple edge detection completed");
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_edgevisionrt_utils_ImageProcessor_processImageNative(
        JNIEnv* env,
        jobject /* this */,
        jobject bitmapIn,
        jobject bitmapOut,
        jint processingType) {

    AndroidBitmapInfo infoIn, infoOut;
    void* pixelsIn;
    void* pixelsOut;

    if (AndroidBitmap_getInfo(env, bitmapIn, &infoIn) < 0) {
        LOGE_NATIVE("Failed to get input bitmap info");
        return;
    }

    if (AndroidBitmap_getInfo(env, bitmapOut, &infoOut) < 0) {
        LOGE_NATIVE("Failed to get output bitmap info");
        return;
    }

    if (AndroidBitmap_lockPixels(env, bitmapIn, &pixelsIn) < 0) {
        LOGE_NATIVE("Failed to lock input pixels");
        return;
    }

    if (AndroidBitmap_lockPixels(env, bitmapOut, &pixelsOut) < 0) {
        LOGE_NATIVE("Failed to lock output pixels");
        AndroidBitmap_unlockPixels(env, bitmapIn);
        return;
    }

    // Apply simple edge detection
    simpleEdgeDetect((uint8_t*)pixelsIn, (uint8_t*)pixelsOut, infoIn.width, infoIn.height);

    AndroidBitmap_unlockPixels(env, bitmapIn);
    AndroidBitmap_unlockPixels(env, bitmapOut);

    LOGD_NATIVE("Image processing completed: %dx%d", infoIn.width, infoIn.height);
}