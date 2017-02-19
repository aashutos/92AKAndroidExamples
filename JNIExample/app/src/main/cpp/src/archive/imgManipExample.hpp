#ifndef JNIEXAMPLE_IMGMANIPEXAMPLE_H
#define JNIEXAMPLE_IMGMANIPEXAMPLE_H

#endif //JNIEXAMPLE_IMGMANIPEXAMPLE_H

#include <jni.h>

#include <android/log.h>
#include <stdio.h>

#include <iostream>
#include <cstdlib>

// Defining templates
#define  LOG_TAG    "imgManipExample"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)


enum CriticalChunk {
    IHDR,PLTE,IDAT,IEND
};

enum ColourTypeEnum {
    GS=0,RGB=2,PLT=3,GS_ALPHA=4,RGB_ALPHA=6
};

struct PNGMetaData {
    char  name;
    int   width;
    int   height;
    char  bitDepth;
    char  colorType;
    char  compMethod;
    char  filtMethod;
    char  intMethod;
};

struct PNGChunk {
    char length[4];
    char type[4];
    char* data;
    char crc[4];
};

bool comparePNGSignatures(char signature[8]);

PNGChunk parseChunk(FILE *pFILE);

PNGMetaData genPNGMetaData(char *data);
