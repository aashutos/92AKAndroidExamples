/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* 
 * File:   PNGComparison.hpp
 * Author: akakshepati
 *
 * Created on 19 February 2017, 12:52
 */

#ifndef PNGCOMPARISON_HPP
#define PNGCOMPARISON_HPP
#endif /* PNGCOMPARISON_HPP */

// PNG Comparison methods
bool cmpPNGSignatures(unsigned char signature[8]);
bool cmpPNGChunkType(char left[4], char right[4]);