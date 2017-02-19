/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   PNGMetaDataFormatter.hpp
 * Author: akakshepati
 *
 * Created on 19 February 2017, 14:28
 */

#ifndef PNGMETADATAFORMATTER_HPP
#define PNGMETADATAFORMATTER_HPP
#endif /* PNGMETADATAFORMATTER_HPP */

#include <cstring>

using namespace std;

// PNG Formatting methods
char* getColorTypeSignifier(char type, char signifier[8]);
char* getCompMethodSignifier(char compType, char signifier[8]);
char* getFiltMethodSignifier(char method, char signifier[8]);
char* getInterlaceMethodSignifier(char method, char signifier[8]);
