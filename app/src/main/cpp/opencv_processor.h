#ifndef EDGEVISIONRT_OPENCV_PROCESSOR_H
#define EDGEVISIONRT_OPENCV_PROCESSOR_H

#include <jni.h>
#include <android/log.h>
#include <opencv2/opencv.hpp>
#include <opencv2/imgproc.hpp>

#define LOG_TAG "OpenCVProcessor"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" {
// Process image with Canny edge detection
void processFrameCanny(uint8_t* inputData, uint8_t* outputData, int width, int height);

// Convert to grayscale
void processFrameGrayscale(uint8_t* inputData, uint8_t* outputData, int width, int height);
}

#endif //EDGEVISIONRT_OPENCV_PROCESSOR_H