
#ifndef SUPERUNRAR_BRIDGEARCHIVE_H
#define SUPERUNRAR_BRIDGEARCHIVE_H

#include "unrar/rar.hpp"
#include "unrar/rartypes.hpp"

//https://www.rarlab.com/technote.htm
#define SIGNATURE_SIZE_COMMON    6
#define SIGNATURE_SIZE_RAR5      8
#define SIGNATURE_SIZE_RAR4      7

#define RAR_VERSION14 14
#define RAR_VERSION50 50

const unsigned char rar_signature[SIGNATURE_SIZE_COMMON] = { 0x52, 0x61, 0x72, 0x21, 0x1A, 0x07 };
const unsigned char rar5_signature[SIGNATURE_SIZE_RAR5] = { 0x52, 0x61, 0x72, 0x21, 0x1A, 0x07, 0x01, 0x00 };
const unsigned char rar4_signature[SIGNATURE_SIZE_RAR4] = { 0x52, 0x61, 0x72, 0x21, 0x1A, 0x07, 0x00 };

#ifdef __cplusplus
extern "C" {
#endif

typedef size_t   (*rar_read_func)(/*(void* context, */void* stream, void *data, size_t size);//(voidpf opaque, voidpf address));
typedef void    (*rar_seek_func)(/*void* context, */void* stream, int64_t offset, int method);//(voidpf opaque, voidpf address));
typedef size_t   (*rar_tell_func)(/*void* context, */void* stream);//(voidpf opaque, voidpf address));

enum stream_type {
    FD,
    STREAM,
};


typedef struct rar_seekable_stream_s {
//    void* context;
    union {
        int fd;
        void* streami;
    };
    stream_type type;

    rar_read_func readFunc;
    rar_seek_func seekFunc;
    rar_tell_func tellFunc;
} rar_seekable_stream;

typedef struct rar_entry_s
{
    char *name;
    int64_t header_pos;

    int64_t packed_size;
    int64_t unpacked_size;
} rar_entry;

typedef struct android_rar_info_s {

    int file_count;
    rar_entry *entries;

}android_rar_info;

android_rar_info get_rar_info(rar_seekable_stream stream);//, rar_entry *entries);
size_t extract_rar(rar_seekable_stream stream, rar_entry* entry, void *dest);//, int dest_size);

class BridgeArchive : public Archive{
public:
    BridgeArchive(rar_seekable_stream stream);
    BridgeArchive(const char *filename);
    ~BridgeArchive();

    bool IsOpened() {return true;};

    android_rar_info GetRarInfo();
    int Read(void *data,size_t size);
    void Seek(int64 offset,int method);
    int64 Tell();

//    int isRarArchive();



private:
    rar_seekable_stream stream;

};

#ifdef __cplusplus
}
#endif


#endif //SUPERUNRAR_BRIDGEARCHIVE_H
