/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   PNGStructs.h
 * Author: akakshepati
 *
 * Created on 19 February 2017, 12:43
 */

#ifndef PNGSTRUCTS_H
#define PNGSTRUCTS_H
#endif

#include <iostream>
#include <arpa/inet.h>
#include <cstring>
#include "PNGMetaDataFormatter.hpp"

using namespace std;

enum CriticalChunk {
    IHDR,PLTE,IDAT,IEND
};

enum ColourTypeEnum {
    GS=0,RGB=2,PLT=3,GS_ALPHA=4,RGB_ALPHA=6
};

struct PNGMetaData {
    char*  name;
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


// PNG Structures and helper methods
PNGChunk parseChunk(FILE *pFILE, bool isBE);
PNGMetaData genPNGMetaData(char loc[], int len, char *data, bool isBE);
void freePNGChunkDynUnderlying(PNGChunk chunk);
void freePNGMetaDataUnderlying(PNGMetaData metaData);
