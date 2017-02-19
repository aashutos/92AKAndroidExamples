/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   main.cpp
 * Author: akakshepati
 *
 * Created on 16 February 2017, 21:15
 */

#include "imgManipExample.hpp"

using namespace std;

extern "C" JNIEXPORT int JNICALL
Java_com_ntak_examples_jniexample_subscriber_BackgroundRenderResourceSubscriber_getBitmapJNI(
        JNIEnv *env, jobject obj, jstring filePath) {
    LOGI("JNI Method: 'getBitmapJNI' is being executed.");
    try {
        bool isBE = isBigEndian();
        LOGI("Big Endian: %d", isBE);

        const char *loc = env->GetStringUTFChars(filePath, 0);
        int len = std::strlen(loc);
        //char loc[] = "/home/akakshepati/Documents/logos/jenkins.png";

        FILE *file = fopen(loc, "rw"); // TODO: jstring passing

        if (file == NULL) {
            LOGE("JNI Method: File not found.");
            fclose(file);
            return 3;
        }

        unsigned char READ_PNG_SIGNATURE[8];

        fread(READ_PNG_SIGNATURE, sizeof (char), 8, file);

        bool isDefPNG = cmpPNGSignatures(READ_PNG_SIGNATURE);

        if (!isDefPNG) {
            LOGE("JNI Method: File signature does not match that of a PNG File.");
            fclose(file);
            return 4;
        }

        LOGI("JNI Method: The file is a valid PNG File.");
        LOGI("Reading header chunk IHDR...");
        PNGChunk ihdrChunk = parseChunk(file, isBE);
        char ihdr[4];
        std::memcpy(ihdr, "IHDR", 4);
        if (!cmpPNGChunkType(ihdr, ihdrChunk.type)) {
            LOGE("JNI Method: IHDR chunk not found in expected point within file. Please check file is not corrupt.");
            freePNGChunkDynUnderlying(ihdrChunk);
            fclose(file);
            return 2;
        }

        LOGI("Parsing Meta Data...");
        PNGMetaData metaData = genPNGMetaData((char*)loc, len, ihdrChunk.data, isBE);

        dispMetaDataInformation(metaData);

        // Read Palette into a Map/Dictionary lookup structure -> Investigate Boost
        // Lib for data structures
        PNGChunk pltChunk = parseChunk(file, isBE);

        // TODO: Separate this out into PNGStructs (More of a global definition)
        char plte[4];
        std::memcpy(plte, "PLTE", 4);

        char idat[4];
        std::memcpy(idat, "IDAT", 4);

        char iend[4];
        std::memcpy(iend, "IEND", 4);

        // Read chunks until palette chunk is reached or compressed data chunk
        while (!(cmpPNGChunkType(pltChunk.type, plte) || cmpPNGChunkType(pltChunk.type, idat))) {

            // Finished processing previous chunk - so clear up chunk
            freePNGChunkDynUnderlying(pltChunk);

            // Parse next chunk
            pltChunk = parseChunk(file, isBE);

            if (cmpPNGChunkType(pltChunk.type, iend)) {
                LOGE("JNI Method: PLTE or IDAT chunk not found in expected point "
                        "within file. Please check file is not corrupt.");
                freePNGMetaDataUnderlying(metaData);
                freePNGChunkDynUnderlying(pltChunk);
                freePNGChunkDynUnderlying(ihdrChunk);
                fclose(file);
                return 2;
            }
        }

        // Load palette for Colour Type 3 => If not palette then throw error
        // TODO: Do an explicit test here for Colour Mode 3 PNG files
        if (cmpPNGChunkType(pltChunk.type, plte) && !(metaData.colorType & PLT == PLT)) {
            LOGE("JNI Method: PLTE chunk not found in expected point within file. "
                    "Please check file is not corrupt.");
            freePNGMetaDataUnderlying(metaData);
            freePNGChunkDynUnderlying(pltChunk);
            freePNGChunkDynUnderlying(ihdrChunk);
            fclose(file);
            return 2;
        } else {
            if (pltChunk.type == "PLTE") {
                LOGI("Parsing the Palette chunk.");
                // TODO: Parse palette here
            }
        }

        // Take chunk as next just in case an IDAT
        PNGChunk *nxtChunk = &pltChunk;
        PNGChunk nextChunk = *nxtChunk;

        // If palette then get IDAT chunk
        while (!cmpPNGChunkType(nextChunk.type, idat)) {

            if (cmpPNGChunkType(nextChunk.type, iend)) {
                LOGE("JNI Method: IDAT chunk not found in expected point within file. "
                        "Please check file is not corrupt.");
                freePNGMetaDataUnderlying(metaData);
                freePNGChunkDynUnderlying(nextChunk);
                freePNGChunkDynUnderlying(ihdrChunk);
                fclose(file);
                return 2;
            }

            freePNGChunkDynUnderlying(nextChunk);
            nextChunk = parseChunk(file, isBE);
        }

        LOGI("Processing IDAT chunks...");

        // IDAT point - mount consecutive IDATs into a stream
        while (nextChunk.type != NULL && cmpPNGChunkType(nextChunk.type, idat)) {
            // TODO: Generate ByteStream source from chunks then decompress and manipulate

            // Clear up and get next chunk
            freePNGChunkDynUnderlying(nextChunk);
            nextChunk = parseChunk(file, isBE);
        }

        // After IDAT Loop until IEND chunk reached
        while (nextChunk.type != NULL && !cmpPNGChunkType(nextChunk.type, iend)) {
            freePNGChunkDynUnderlying(nextChunk);
            nextChunk = parseChunk(file, isBE);
        }

        LOGI("Trying to parse IEND tag...");

        if (nextChunk.type == NULL || !cmpPNGChunkType(nextChunk.type, iend)) {
            LOGE("JNI Method: IEND chunk not found in expected point within file. Please check file is not corrupt.");
            freePNGMetaDataUnderlying(metaData);
            freePNGChunkDynUnderlying(ihdrChunk);
            freePNGChunkDynUnderlying(nextChunk);
            fclose(file);
            return 2;
        }

        // Clear up files and chunks
        LOGI("Clearing up task...");
        freePNGMetaDataUnderlying(metaData);
        freePNGChunkDynUnderlying(ihdrChunk);
        freePNGChunkDynUnderlying(nextChunk);
        env->ReleaseStringUTFChars(filePath, loc);
        fclose(file);

        // Read in data from file -> Compressed byte stream -> Uncompressed (raw)
        // byte stream -> Parse and substitute value (Using metadata
        // Don't care about interlace atm -> Just want to manipulate data.
        // Check # of pixels match dimensions specified?

        // Re-compress data post manipulation -> write stream to file

        // Append auxiliary chunk with details stating manipulation has occurred.

    } catch (std::exception e) {
        LOGE("JNI Method: Exception occurred. See stacktrace for details. Details: %s",e.what());
        return 1;
    }
    LOGI("JNI Method: 'getBitmapJNI' has executed successfully.");

    return 0;
}

void dispMetaDataInformation(PNGMetaData metaData) {
    char signifier[8];
    LOGI("PNG Info:\n Name=%s",metaData.name);
    LOGI(" Width=%d pixels",metaData.width);
    LOGI(" Height=%d pixels",metaData.height);
    LOGI(" Bit Depth=%d bits",static_cast<unsigned> (metaData.bitDepth));
    LOGI(" Color Type=%s",getColorTypeSignifier(metaData.colorType, signifier));
    LOGI(" Compression Method=%s",getCompMethodSignifier(metaData.compMethod, signifier));
    LOGI(" Filter Method=%s",getFiltMethodSignifier(metaData.filtMethod, signifier));
    LOGI(" Interlace Method=%s",getInterlaceMethodSignifier(metaData.intMethod, signifier));
}

// TODO: Test cases needs to be implemented:
// 1. Utilise Google Test Framework -> Unit test
// 2. Break out code into sub modules - more granular structure
// 3. Break out header file from cpp file
