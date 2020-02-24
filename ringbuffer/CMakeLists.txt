
cmake_minimum_required(VERSION 3.4.1)

# 配置输出lib目录
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/libs/${ANDROID_ABI})

# 自定义变量
set(IO ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/io)

# 配置头文件目录
include_directories(${IO})

# 配置输出lib的名字, 类型, 源文件
add_library( # Sets the name of the library.
        cjringbuffer

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${IO}/CjRingbuffer.cpp
        )

# 配置输出lib的链接库
target_link_libraries( # Specifies the target library.
        cjringbuffer

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log
        )