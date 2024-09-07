#include <jni.h>
#ifdef __APPLE__
    #define GL_SILENCE_DEPRECATION
    #include <OpenGL/OpenGL.h>
    #include <OpenGL/gl.h>
#else
//    #include <GL/gl.h>
    #include <GL/glew.h>
#endif

#include <cmath>
#include <png.h>
#include <stdlib.h>
#include <vector>
#include <iostream>
#include <cstring> // For strcmp

#define GL_PACK_REVERSE_ROW_ORDER_ANGLE 0x93A4 // extension

// https://gist.github.com/dobrokot/10486786
typedef unsigned char ui8;
#define ASSERT_EX(cond, error_message) do { if (!(cond)) { std::cerr << error_message; exit(1);} } while(0)

static void PngWriteCallback(png_structp  png_ptr, png_bytep data, png_size_t length) {
    std::vector<ui8> *p = (std::vector<ui8>*)png_get_io_ptr(png_ptr);
    p->insert(p->end(), data, data + length);
}

struct TPngDestructor {
    png_struct *p;
    TPngDestructor(png_struct *p) : p(p)  {}
    ~TPngDestructor() { if (p) {  png_destroy_write_struct(&p, NULL); } }
};


void WritePngToMemory(size_t w, size_t h, const ui8 *dataRGB, std::vector<ui8> &out) {
    out.clear();
    png_structp p = png_create_write_struct(PNG_LIBPNG_VER_STRING, NULL, NULL, NULL);
    ASSERT_EX(p, "png_create_write_struct() failed");
    TPngDestructor destroyPng(p);
    png_infop info_ptr = png_create_info_struct(p);
    ASSERT_EX(info_ptr, "png_create_info_struct() failed");
    ASSERT_EX(0 == setjmp(png_jmpbuf(p)), "setjmp(png_jmpbuf(p) failed");
    png_set_IHDR(p, info_ptr, w, h, 8,
            PNG_COLOR_TYPE_RGB,
            PNG_INTERLACE_NONE,
            PNG_COMPRESSION_TYPE_DEFAULT,
            PNG_FILTER_TYPE_DEFAULT);
    //png_set_compression_level(p, 1);
    std::vector<ui8*> rows(h);
    for (size_t y = 0; y < h; ++y)
        rows[y] = (ui8*)dataRGB + y * w * 3;
    png_set_rows(p, info_ptr, &rows[0]);
    png_set_write_fn(p, &out, PngWriteCallback, NULL);
    png_write_png(p, info_ptr, PNG_TRANSFORM_IDENTITY, NULL);
}

// FIXME: Use glGetIntegerv(GL_NUM_EXTENSIONS) then use glGetStringi for OpenGL 3.0+
bool isExtensionSupported(const char* extName) {
    // Get the list of supported extensions
    const char* extensions = reinterpret_cast<const char*>(glGetString(GL_EXTENSIONS));
    // FIXME: It returns nullptr after OpenGL 3.0+, even if there are extensions
    // Check for NULL pointer (just in case no OpenGL context is active)
    if (extensions == nullptr) {
        std::cerr << "Could not get OpenGL extensions list. Make sure an OpenGL context is active." << std::endl;
        return false;
    }

    // Search for the extension in the list
    const char* start = extensions;
    const char* where;
    const char* terminator;

    // Extension names should not have spaces
    while ((where = strchr(start, ' ')) || (where = strchr(start, '\0'))) {
        terminator = where;
        if ((terminator - start) == strlen(extName) && strncmp(start, extName, terminator - start) == 0) {
            // Found the extension
            return true;
        }
        if (*where == '\0') {
            break; // Reached the end of the list
        }
        start = where + 1; // Move past the space
    }

    // Extension not found
    return false;
}

extern "C" JNIEXPORT jboolean JNICALL Java_com_kyhsgeekcode_minecraft_1env_FramebufferCapturer_checkExtension
    (JNIEnv *env, jclass clazz) {
    // Check for the GL_ARB_pixel_buffer_object extension
    return (jboolean) isExtensionSupported("GL_ANGLE_pack_reverse_row_order");
}

extern "C" JNIEXPORT jboolean JNICALL Java_com_kyhsgeekcode_minecraft_1env_FramebufferCapturer_initializeGLEW
    (JNIEnv *env, jclass clazz) {
    #ifdef __APPLE__
        return true;
    #else
        glewExperimental = GL_TRUE;
        GLenum err = glewInit();
        if (err != GLEW_OK) {
            std::cerr << "GLEW initialization failed: " << glewGetErrorString(err) << std::endl;
        }
        return err == GLEW_OK;
    #endif
}

enum EncodingMode {
    RAW = 0,
    PNG = 1
};

extern "C" JNIEXPORT jobject JNICALL Java_com_kyhsgeekcode_minecraft_1env_FramebufferCapturer_captureFramebuffer(
    JNIEnv *env,
    jclass clazz,
    jint textureId,
    jint frameBufferId,
    jint textureWidth,
    jint textureHeight,
    jint targetSizeX,
    jint targetSizeY,
    jint encodingMode,
    jboolean isExtensionAvailable,
    jboolean drawCursor,
    jint xPos,   // 추가: 커서의 X 좌표
    jint yPos    // 추가: 커서의 Y 좌표
) {
//    // 텍스처 바인딩
//    glBindTexture(GL_TEXTURE_2D, textureId);
//    glPixelStorei(GL_PACK_ALIGNMENT, 1); // 픽셀 데이터 정렬 설정
//    // 텍스처 데이터를 저장할 메모리 할당
//    auto* pixels = new GLubyte[textureWidth * textureHeight * 3]; // RGB 포맷 가정
//
//    // 현재 바인딩된 텍스처로부터 이미지 데이터 읽기
//    glGetTexImage(GL_TEXTURE_2D, 0, GL_RGB, GL_UNSIGNED_BYTE, pixels);
    // ByteString 클래스를 찾습니다.
    jclass byteStringClass = env->FindClass("com/google/protobuf/ByteString");
    if (byteStringClass == nullptr || env->ExceptionCheck()) {
        // Handle error
        return nullptr;
    }
    // copyFrom 정적 메서드의 메서드 ID를 얻습니다.
    jmethodID copyFromMethod = env->GetStaticMethodID(byteStringClass, "copyFrom", "([B)Lcom/google/protobuf/ByteString;");
    if (copyFromMethod == nullptr || env->ExceptionCheck()) {
        // Handle error
        return nullptr;
    }
    jbyteArray byteArray = nullptr;
    if (encodingMode == RAW) {
        // 호출하려는 바이트 배열을 생성합니다.
        byteArray = env->NewByteArray(targetSizeX * targetSizeY * 3);
        if (byteArray == nullptr || env->ExceptionCheck()) {
            // Handle error
            return nullptr;
        }
    }
    // **Note**: Flipping should be done in python side.
    glBindFramebuffer(GL_READ_FRAMEBUFFER, frameBufferId);
    auto* pixels = new GLubyte[textureWidth * textureHeight * 3];
    glPixelStorei(GL_PACK_ALIGNMENT, 1);
    glReadPixels(0, 0, textureWidth, textureHeight, GL_RGB, GL_UNSIGNED_BYTE, pixels);

    // resize if needed
    if (textureWidth != targetSizeX || textureHeight != targetSizeY) {
        auto* resizedPixels = new GLubyte[targetSizeX * targetSizeY * 3];
        for (int y = 0; y < targetSizeY; y++) {
            for (int x = 0; x < targetSizeX; x++) {
                int srcX = x * textureWidth / targetSizeX;
                int srcY = y * textureHeight / targetSizeY;
                int dstIndex = (y * targetSizeX + x) * 3;
                int srcIndex = (srcY * textureWidth + srcX) * 3;
                resizedPixels[dstIndex] = pixels[srcIndex];
                resizedPixels[dstIndex + 1] = pixels[srcIndex + 1];
                resizedPixels[dstIndex + 2] = pixels[srcIndex + 2];
            }
        }
        delete[] pixels;
        pixels = resizedPixels;
    }

    if (drawCursor && xPos >= 0 && xPos < targetSizeX && yPos >= 0 && yPos < targetSizeY) {
        int cursorSize = 10; // Cursor size
        for (int dy = 0; dy < cursorSize; ++dy) {
            for (int dx = 0; dx < cursorSize; ++dx) {
                // Check if the cursor is in the bounds of the image
                int pixelX = yPos + dy;
                int pixelY = xPos + dx;

                if (pixelX >= 0 && pixelX < targetSizeX && pixelY >= 0 && pixelY < targetSizeY) {
                    int index = (pixelY * targetSizeX + pixelX) * 3; // 해당 좌표의 픽셀 인덱스
                    pixels[index] = 255;      // Red
                    pixels[index + 1] = 0;    // Green
                    pixels[index + 2] = 0;    // Blue
                }
            }
        }
    }

    // make png bytes from the pixels
    // 이미지 데이터를 바이트 배열로 변환
    if (encodingMode == PNG) {
        std::vector<ui8> imageBytes;
        WritePngToMemory((size_t) targetSizeX, (size_t) targetSizeY, pixels, imageBytes);
        // 호출하려는 바이트 배열을 생성합니다.
        byteArray = env->NewByteArray(imageBytes.size());
        env->SetByteArrayRegion(byteArray, 0, imageBytes.size(), reinterpret_cast<jbyte*>(imageBytes.data()));
    } else if (encodingMode == RAW) {
        env->SetByteArrayRegion(byteArray, 0, targetSizeX * targetSizeY * 3, reinterpret_cast<jbyte*>(pixels));
    }
    // 정적 메서드를 호출하여 ByteString 객체를 얻습니다.
    jobject byteStringObject = env->CallStaticObjectMethod(byteStringClass, copyFromMethod, byteArray);
    if (byteStringObject == nullptr || env->ExceptionCheck()) {
        // Handle error
        if (byteArray != nullptr) {
            env->DeleteLocalRef(byteArray);
        }
        if (pixels != nullptr) {
            delete[] pixels;
        }
        return nullptr;
    }
    // 메모리 정리
    env->DeleteLocalRef(byteArray);
    delete[] pixels;
    return byteStringObject;
}
