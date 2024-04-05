#include <jni.h>
#ifdef __APPLE__
    #include <OpenGL/OpenGL.h>
    #include <OpenGL/gl.h>
#else
    #include <GL/gl.h>
#endif

#include <cmath>
#include <png.h>
#include <stdlib.h>
#include <vector>
#include <iostream>

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


void WritePngToMemory(size_t w, size_t h, const ui8 *dataRGBA, std::vector<ui8> &out) {
    out.clear();
    png_structp p = png_create_write_struct(PNG_LIBPNG_VER_STRING, NULL, NULL, NULL);
    ASSERT_EX(p, "png_create_write_struct() failed");
    TPngDestructor destroyPng(p);
    png_infop info_ptr = png_create_info_struct(p);
    ASSERT_EX(info_ptr, "png_create_info_struct() failed");
    ASSERT_EX(0 == setjmp(png_jmpbuf(p)), "setjmp(png_jmpbuf(p) failed");
    png_set_IHDR(p, info_ptr, w, h, 8,
            PNG_COLOR_TYPE_RGBA,
            PNG_INTERLACE_NONE,
            PNG_COMPRESSION_TYPE_DEFAULT,
            PNG_FILTER_TYPE_DEFAULT);
    //png_set_compression_level(p, 1);
    std::vector<ui8*> rows(h);
    for (size_t y = 0; y < h; ++y)
        rows[y] = (ui8*)dataRGBA + y * w * 4;
    png_set_rows(p, info_ptr, &rows[0]);
    png_set_write_fn(p, &out, PngWriteCallback, NULL);
    png_write_png(p, info_ptr, PNG_TRANSFORM_IDENTITY, NULL);
}

extern "C" JNIEXPORT jobject JNICALL Java_com_kyhsgeekcode_minecraft_env_FramebufferCapturer_captureFramebuffer
  (JNIEnv *env, jobject, jint textureId, jint textureWidth, jint textureHeight, jint targetSizeX, jint targetSizeY) {
    // 텍스처 바인딩
    glBindTexture(GL_TEXTURE_2D, textureId);
    glPixelStorei(GL_PACK_ALIGNMENT, 3); // 픽셀 데이터 정렬 설정
    // 텍스처 데이터를 저장할 메모리 할당
    auto* pixels = new GLubyte[textureWidth * textureHeight * 3]; // RGB 포맷 가정

    // 현재 바인딩된 텍스처로부터 이미지 데이터 읽기
    glGetTexImage(GL_TEXTURE_2D, 0, GL_RGB, GL_UNSIGNED_BYTE, pixels);

    // resize if needed and flip vertically
    if (textureWidth != targetSizeX || textureHeight != targetSizeY) {
        auto* resizedPixels = new GLubyte[targetSizeX * targetSizeY * 3];
        for (int y = 0; y < targetSizeY; y++) {
            for (int x = 0; x < targetSizeX; x++) {
                int srcX = x * textureWidth / targetSizeX;
                // srcY를 계산할 때 이미지를 상하 반전시킵니다.
                int srcY = (textureHeight - 1) - (y * textureHeight / targetSizeY);
//                int srcY = y * textureHeight / targetSizeY;
                int dstIndex = (y * targetSizeX + x) * 3;
                int srcIndex = (srcY * textureWidth + srcX) * 3;
                resizedPixels[dstIndex] = pixels[srcIndex];
                resizedPixels[dstIndex + 1] = pixels[srcIndex + 1];
                resizedPixels[dstIndex + 2] = pixels[srcIndex + 2];
            }
        }
        delete[] pixels;
        pixels = resizedPixels;
    } else {
        // 이미지를 상하 반전시킵니다.
        for (int y = 0; y < textureHeight / 2; y++) {
            for (int x = 0; x < textureWidth; x++) {
                int topIndex = (y * textureWidth + x) * 3;
                int bottomIndex = ((textureHeight - 1 - y) * textureWidth + x) * 3;
                std::swap(pixels[topIndex], pixels[bottomIndex]);
                std::swap(pixels[topIndex + 1], pixels[bottomIndex + 1]);
                std::swap(pixels[topIndex + 2], pixels[bottomIndex + 2]);
            }
        }
    }
    // make png bytes from the pixels
    // 이미지 데이터를 바이트 배열로 변환
    std::vector<ui8> imageBytes;
    WritePngToMemory((size_t) targetSizeX, (size_t) targetSizeY, pixels, imageBytes);
    // ByteString 클래스를 찾습니다.
    jclass byteStringClass = env->FindClass("com/google/protobuf/ByteString");

    // copyFrom 정적 메서드의 메서드 ID를 얻습니다.
    jmethodID copyFromMethod = env->GetStaticMethodID(byteStringClass, "copyFrom", "([B)Lcom/google/protobuf/ByteString;");

    // 호출하려는 바이트 배열을 생성합니다.
    jbyteArray byteArray = env->NewByteArray(imageBytes.size());
    env->SetByteArrayRegion(byteArray, 0, imageBytes.size(), reinterpret_cast<jbyte*>(imageBytes.data()));

    // 정적 메서드를 호출하여 ByteString 객체를 얻습니다.
    jobject byteStringObject = env->CallStaticObjectMethod(byteStringClass, copyFromMethod, byteArray);

    // 메모리 정리
    env->DeleteLocalRef(byteArray);
    // byteStringObject를 사용한 후에는 DeleteLocalRef를 호출하여 메모리를 정리합니다.
//    free(pngBytes);
    delete[] pixels;
    return byteStringObject;
}
