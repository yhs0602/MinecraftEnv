cmake_minimum_required(VERSION 3.10)
project(framebuffer_capturer)

# Set the C++ standard
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED True)
# Hacky: Set the CMAKE_FIND_FRAMEWORK to LAST to avoid finding mismatching libs/headers
set(CMAKE_FIND_FRAMEWORK LAST)

# Find JNI
# set(JAVA_AWT_LIBRARY NotNeeded)
# set(JAVA_JVM_LIBRARY NotNeeded)
find_package(Java COMPONENTS Development)
find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})

# Find OpenGL
find_package(OpenGL REQUIRED)
include_directories(${OPENGL_INCLUDE_DIR})

# Find png
if (APPLE)
  set(CMAKE_FIND_FRAMEWORK NEVER)
endif()
find_package(PNG REQUIRED)
include_directories(${PNG_INCLUDE_DIRS})
message(PNG_INCLUDE_DIRS="${PNG_INCLUDE_DIRS}")
message(PNG_LIBRARIES="${PNG_LIBRARIES}")

# Add your source files here
set(SOURCE_FILES
    src/main/cpp/framebuffer_capturer.cpp
    # Add more source files as needed
)

# Add a library target for your native library
add_library(native-lib SHARED ${SOURCE_FILES})

# Link with JNI and OpenGL libraries
target_link_libraries(native-lib ${JNI_LIBRARIES} ${OPENGL_LIBRARIES} ${PNG_LIBRARIES})

# If you are using additional libraries, like GLFW for OpenGL context creation,
# you can find and link them here as well.
# For example:
# find_package(glfw3 REQUIRED)
# target_link_libraries(native-lib glfw)
