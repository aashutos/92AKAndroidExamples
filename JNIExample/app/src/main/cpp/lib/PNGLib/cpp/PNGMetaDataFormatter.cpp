/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

// All char[] returned are auto null terminating and so cout can handle outputting these values

#include "../headers/PNGMetaDataFormatter.hpp"

char* getColorTypeSignifier(char type, char outStr[8]) {
    switch (type) {
        case 0:
        {
            memcpy(outStr, ":GScale",8);
            return outStr;
        }

        case 2:
        {
            memcpy(outStr, ":RGB   ",8);
            return outStr;
        }

        case 3:
        {
            memcpy(outStr, ":Plte  ",8);
            return outStr;
        }

        case 4:
        {
            memcpy(outStr, ":GS-Alp",8);
            return outStr;
        }

        case 6:
        {
            memcpy(outStr, ":RGB-Al",8);
            return outStr;
        }

        default:
        {
            memcpy(outStr, ":Unknwn",8);
            return outStr;
        }
    }
}

char* getCompMethodSignifier(char compType, char outStr[8]) {
    switch (compType) {
        case 0:
        {
            memcpy(outStr, ":SldWin",8);
            return outStr;
        }
        default:
        {
            memcpy(outStr, ":Unknwn",8);
            return outStr;
        }
    }
}

char* getFiltMethodSignifier(char method, char outStr[8]) {
    switch (method) {
        case 0:
        {
            memcpy(outStr, ":AdpFlt",8);
            return outStr;
        }

        default:
        {
            memcpy(outStr, ":Unknwn",8);
            return outStr;
        }
    }
}

char* getInterlaceMethodSignifier(char method, char outStr[8]) {
    switch (method) {
        case 0:
        {
            memcpy(outStr, ":None  ",8);
            return outStr;
        }

        case 1:
        {
            memcpy(outStr, ":Adam7 ",8);
            return outStr;
        }

        default:
        {
            memcpy(outStr, ":Unknwn",8);
            return outStr;
        }
    }
}
