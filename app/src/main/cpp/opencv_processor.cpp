#include "opencv_processor.h"

void processFrameCanny(uint8_t* inputData, uint8_t* outputData, int width, int height) {
    try {
        LOGD("Starting Canny edge detection: %dx%d", width, height);

        // Create OpenCV Mat from input data (assuming RGBA)
        cv::Mat inputMat(height, width, CV_8UC4, inputData);
        cv::Mat grayMat, edgesMat;

        // Convert to grayscale
        cv::cvtColor(inputMat, grayMat, cv::COLOR_RGBA2GRAY);

        // Apply Gaussian blur to reduce noise
        cv::GaussianBlur(grayMat, grayMat, cv::Size(5, 5), 1.5);

        // Apply Canny edge detection
        cv::Canny(grayMat, edgesMat, 50, 150);

        // Convert edges back to RGBA for display
        cv::Mat outputMat(height, width, CV_8UC4);
        cv::cvtColor(edgesMat, outputMat, cv::COLOR_GRAY2RGBA);

        // Copy to output buffer
        memcpy(outputData, outputMat.data, width * height * 4);

        LOGD("Canny edge detection completed successfully");

    } catch (cv::Exception& e) {
        LOGE("OpenCV Exception: %s", e.what());
    } catch (...) {
        LOGE("Unknown exception in processFrameCanny");
    }
}

void processFrameGrayscale(uint8_t* inputData, uint8_t* outputData, int width, int height) {
    try {
        LOGD("Converting to grayscale: %dx%d", width, height);

        // Create OpenCV Mat from input data (assuming RGBA)
        cv::Mat inputMat(height, width, CV_8UC4, inputData);
        cv::Mat grayMat, outputMat;

        // Convert to grayscale
        cv::cvtColor(inputMat, grayMat, cv::COLOR_RGBA2GRAY);

        // Convert back to RGBA
        cv::cvtColor(grayMat, outputMat, cv::COLOR_GRAY2RGBA);

        // Copy to output buffer
        memcpy(outputData, outputMat.data, width * height * 4);

        LOGD("Grayscale conversion completed");

    } catch (cv::Exception& e) {
        LOGE("OpenCV Exception: %s", e.what());
    }
}