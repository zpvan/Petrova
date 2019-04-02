# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

# include h file
include_directories(include)

# include ffmpeg library
set(FFLIB ${CMAKE_CURRENT_SOURCE_DIR}/libs/${ANDROID_ABI})

add_library(avcodec SHARED IMPORTED)
set_target_properties(avcodec PROPERTIES IMPORTED_LOCATION ${FFLIB}/libavcodec.so)

add_library(avformat SHARED IMPORTED)
set_target_properties(avformat PROPERTIES IMPORTED_LOCATION ${FFLIB}/libavformat.so)

add_library(avutil SHARED IMPORTED)
set_target_properties(avutil PROPERTIES IMPORTED_LOCATION ${FFLIB}/libavutil.so)

add_library(swscale SHARED IMPORTED)
set_target_properties(swscale PROPERTIES IMPORTED_LOCATION ${FFLIB}/libswscale.so)

add_library(swresample SHARED IMPORTED)
set_target_properties(swresample PROPERTIES IMPORTED_LOCATION ${FFLIB}/libswresample.so)

add_library( # Sets the name of the library.
        kismet

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        # JNI
        src/main/cpp/jni/native-lib.cpp
        src/main/cpp/jni/com_knox_kismet_player_KismetPlayer.cpp

        # Player Component
        src/main/cpp/player/KismetPlayer.cpp
        src/main/cpp/player/FFDemuxer.cpp
        src/main/cpp/player/FFDecoder.cpp

        # OpenGL
        src/main/cpp/player/GLWindow.cpp
        src/main/cpp/player/GLTexture.cpp
        src/main/cpp/player/GLShader.cpp

        # Data
        src/main/cpp/player/VoData.cpp)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        kismet

        # ffmpeg
        avcodec avformat avutil swscale swresample

        # android
        android

        # opengl
        GLESv2 EGL

        # opensl
        OpenSLES

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})