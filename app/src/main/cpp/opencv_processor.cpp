#include "opencv_processor.h"

void processFrame(uint8_t* inputData, uint8_t* outputData, int width, int height) {
    // Placeholder - we'll add OpenCV edge detection in next step
    LOGD("Processing frame: %dx%d", width, height);

    // For now, just copy input to output (passthrough)
    int totalPixels = width * height;
    for (int i = 0; i < totalPixels; i++) {
        outputData[i] = inputData[i];
    }
}