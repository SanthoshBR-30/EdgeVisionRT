#cmake_minimum_required(VERSION 3.22.1)
#
#project("edgevisionrt")
#
## Add the native library with correct path
#add_library(edgevisionrt SHARED
#        src/main/cpp/native-lib.cpp
#        src/main/cpp/opencv_processor.cpp)
#
## Find and link log library
#find_library(log-lib log)
#
#target_link_libraries(edgevisionrt ${log-lib})

cmake_minimum_required(VERSION 3.22.1)

project("edgevisionrt")

# Set OpenCV directory
set(OpenCV_DIR ${CMAKE_SOURCE_DIR}/../opencv/native/jni)

# Include OpenCV headers
include_directories(${OpenCV_DIR}/include)

# Add OpenCV native libraries
add_library(opencv_java4 SHARED IMPORTED)
set_target_properties(opencv_java4 PROPERTIES
        IMPORTED_LOCATION ${OpenCV_DIR}/../libs/${ANDROID_ABI}/libopencv_java4.so)

# Add your native source files
add_library(edgevisionrt SHARED
        src/main/cpp/native-lib.cpp
        src/main/cpp/opencv_processor.cpp)

# Find log library
find_library(log-lib log)

# Find jnigraphics for bitmap operations
find_library(jnigraphics-lib jnigraphics)

# Link all libraries
target_link_libraries(edgevisionrt
        opencv_java4
        ${log-lib}
        ${jnigraphics-lib})