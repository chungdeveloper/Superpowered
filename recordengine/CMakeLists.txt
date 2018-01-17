# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

set(
	PATH_TO_SUPERPOWERED
	CACHE STRING ""
)

message(${ANDROID_ABI})

file(GLOB CPP_FILES "*.cpp")

add_library( record-engine
             SHARED
            ${CPP_FILES}
            ${PATH_TO_SUPERPOWERED}/AndroidIO/SuperpoweredAndroidAudioIO.cpp
            ${PATH_TO_SUPERPOWERED}/SuperpoweredNBandEQ.cpp
             src/main/jni/record-engine.cpp
              )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

include_directories(src/main/jni)
include_directories(${PATH_TO_SUPERPOWERED})

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       record-engine
                       # Links the target library to the log library
                       # included in the NDK.
                       log
                       android
                       OpenSLES
                       ${PATH_TO_SUPERPOWERED}/libSuperpoweredAndroid${ANDROID_ABI}.a
                       ${log-lib} )