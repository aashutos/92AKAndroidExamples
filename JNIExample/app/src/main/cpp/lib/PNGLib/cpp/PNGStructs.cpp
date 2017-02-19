#include "../headers/PNGStructs.hpp"

PNGChunk parseChunk(FILE *pFILE, bool isBE) { // Validate this - Do we need to place checks
    // for invalid files
    PNGChunk chunk;
    try {

        fread(chunk.length, sizeof (char), 4, pFILE);
        if (feof(pFILE) != 0 || ferror(pFILE))
            return chunk;
        fread(chunk.type, sizeof (char), 4, pFILE);
        int length = ((int*) chunk.length)[0];

        if (!isBE)
            length = htonl(length);
        chunk.data = (char*) malloc(sizeof (char)*length);
        fread(chunk.data, sizeof (char), length, pFILE);
        fread(chunk.crc, sizeof (char), 4, pFILE);
        cout << "Parsing chunk of type: " << chunk.type[0] << chunk.type[1] << chunk.type[2] << chunk.type[3] << " length: " << length << "..." << endl;
        return chunk;
    } catch (std::exception e) {
        cout << e.what() << endl;
        return chunk;
    }

}

PNGMetaData genPNGMetaData(char loc[], int len, char *data, bool isBE) { // Validate this -> Do we need to place
    // checks for invalid files
    PNGMetaData metaData;
    metaData.name = (char*) malloc(len);
    memcpy(metaData.name, loc, len);

    if (!isBE) {
        metaData.width = htonl(((int*) data)[0]); // 32 bit step as cast to integer 0xB9
        metaData.height = htonl(((int*) data)[1]); // 0x100
    } else {
        metaData.width = ((int*) data)[0]; // 32 bit step as cast to integer 0xB9
        metaData.height = ((int*) data)[1]; // 0x100
    }
    metaData.bitDepth = (data)[8]; // 8 bit step as cast to char: 0x8  = 8 bits per channel
    metaData.colorType = (data)[9]; // 0x6: colour + alpha
    metaData.compMethod = (data)[10]; // 0x0: Sliding Window
    metaData.filtMethod = (data)[11]; // 0x0: Adaptive filtering
    metaData.intMethod = (data)[12]; // 0x0: No interlacing (Not using Adam7)

    char signifier[8];

    cout << "PNG Info:" << endl
            << " Name=" << loc << endl
            << " Width=" << metaData.width << " pixels" << endl
            << " Height=" << metaData.height << " pixels" << endl
            << " Bit Depth=" << static_cast<unsigned> (metaData.bitDepth) << " bits" << endl
            << " Color Type=" << static_cast<unsigned> (metaData.colorType) << getColorTypeSignifier(metaData.colorType, signifier) << endl;
    cout << " Compression Method=" << static_cast<unsigned> (metaData.compMethod) << getCompMethodSignifier(metaData.compMethod, signifier) << endl;
    cout << " FilterMethod=" << static_cast<unsigned> (metaData.filtMethod) << getFiltMethodSignifier(metaData.filtMethod, signifier) << endl;
    cout << " Interlace Method=" << static_cast<unsigned> (metaData.intMethod) << getInterlaceMethodSignifier(metaData.intMethod, signifier) << endl;

    return metaData;
}

void freePNGChunkDynUnderlying(PNGChunk chunk) {
    free(chunk.data);
}

void freePNGMetaDataUnderlying(PNGMetaData metaData) {
    free(metaData.name);
}