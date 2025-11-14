#include <jni.h>
#include <string>
#include <android/log.h>
#include <android/bitmap.h>
#include "opencv_processor.h"

#define LOG_TAG "NativeLib"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_edgevisionrt_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "OpenCV 4.8.0 Loaded Successfully!";
    LOGD("Native library with OpenCV loaded!");
    return env->NewStringUTF(hello.c_str());
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

    // Get bitmap info and lock pixels
    if (AndroidBitmap_getInfo(env, bitmapIn, &infoIn) < 0) {
        LOGE("Failed to get input bitmap info");
        return;
    }

    if (AndroidBitmap_getInfo(env, bitmapOut, &infoOut) < 0) {
        LOGE("Failed to get output bitmap info");
        return;
    }

    if (AndroidBitmap_lockPixels(env, bitmapIn, &pixelsIn) < 0) {
        LOGE("Failed to lock input pixels");
        return;
    }

    if (AndroidBitmap_lockPixels(env, bitmapOut, &pixelsOut) < 0) {
        LOGE("Failed to lock output pixels");
        AndroidBitmap_unlockPixels(env, bitmapIn);
        return;
    }

    // Process based on type: 0 = Canny, 1 = Grayscale
    if (processingType == 0) {
        processFrameCanny((uint8_t*)pixelsIn, (uint8_t*)pixelsOut,
                          infoIn.width, infoIn.height);
    } else {
        processFrameGrayscale((uint8_t*)pixelsIn, (uint8_t*)pixelsOut,
                              infoIn.width, infoIn.height);
    }

    // Unlock pixels
    AndroidBitmap_unlockPixels(env, bitmapIn);
    AndroidBitmap_unlockPixels(env, bitmapOut);

    LOGD("Image processing completed");
}
