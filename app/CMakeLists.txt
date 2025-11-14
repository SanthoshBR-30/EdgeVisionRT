cmake_minimum_required(VERSION 3.22.1)

project("edgevisionrt")

# Add the native library with correct path
add_library(edgevisionrt SHARED
        src/main/cpp/native-lib.cpp)

# Find and link log library
find_library(log-lib log)

target_link_libraries(edgevisionrt ${log-lib})