/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

#include "../headers/PNGComparison.hpp"

bool cmpPNGSignatures(unsigned char signature[8]) {
    unsigned char PNG_SIGNATURE[8] = {0x89, 0x50, 0x4E, 0x47,
        0x0D, 0x0A, 0x1A, 0x0A};

    int i = 0;
    for (i = 0; i < sizeof (PNG_SIGNATURE) / sizeof (*PNG_SIGNATURE); i++) {
        if (PNG_SIGNATURE[i] != signature[i])
            return false;
    }
    return true;
}

bool cmpPNGChunkType(char left[4], char right[4]) {
    int lft = ((int*) left)[0];
    int rgt = ((int*) right)[0];

    if (lft != rgt)
        return false;

    return true;
}