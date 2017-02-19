/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

#include "../headers/GenUtils.hpp"

bool isBigEndian() {
    int val = 1;
    if (*(char*) &val == 1)
        return false;
    return true;
}