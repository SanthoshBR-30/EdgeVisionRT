cmake_minimum_required(VERSION 3.22.1)

project("edgevisionrt")

add_library(edgevisionrt SHARED
        src/main/cpp/native-lib.cpp)

find_library(log-lib log)
find_library(jnigraphics-lib jnigraphics)

target_link_libraries(edgevisionrt
        ${log-lib}
        ${jnigraphics-lib})