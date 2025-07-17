cmake_minimum_required(VERSION 3.22.1)

project("kopus")

set(OPUS_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../../../opus)
set(OPUS_BUILD_DIR ${OPUS_DIR}/build/${ANDROID_ABI})
file(MAKE_DIRECTORY ${OPUS_BUILD_DIR})

add_subdirectory(${OPUS_DIR} ${OPUS_BUILD_DIR})

add_library(lib_opus SHARED IMPORTED)

set_target_properties(lib_opus PROPERTIES
        IMPORTED_LOCATION
        "${CMAKE_RUNTIME_OUTPUT_DIRECTORY}/libopus.so")

include_directories(${OPUS_DIR}/include)

add_library(${CMAKE_PROJECT_NAME} SHARED
        OpusEncoder.cpp)

target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        log
        lib_opus)
