#ifndef JNIEXAMPLE_IMGMANIPEXAMPLE_H
#define JNIEXAMPLE_IMGMANIPEXAMPLE_H

#endif //JNIEXAMPLE_IMGMANIPEXAMPLE_H

#include <jni.h>
#include <android/log.h>

// Defining templates
#define  LOG_TAG    "ImgManipExample"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#include "PNGLib/headers/GenUtils.hpp"
#include "PNGLib/headers/PNGComparison.hpp"
#include "PNGLib/headers/PNGStructs.hpp"
#include <cstdlib>
#include <iostream>
#include <stdio.h>

void dispMetaDataInformation(PNGMetaData metaData);