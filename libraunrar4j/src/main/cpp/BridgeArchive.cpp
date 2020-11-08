#include <cstdio>
#include <cstdlib>

#include "BridgeArchive.h"
#include "log.h"

android_rar_info get_rar_info(rar_seekable_stream stream) {
    BridgeArchive archive(stream);
    return archive.GetRarInfo();
}

void unstoreFile(ComprDataIO &dataIo, int64 destUnpSize)
{
    Array<byte> buffer(File::CopyBufferSize());
    while (true)
    {
        int readSize=dataIo.UnpRead(&buffer[0], buffer.Size());
        if (readSize <= 0)
            break;
        int writeSize= readSize < destUnpSize ? readSize : (int)destUnpSize;
        if (writeSize > 0)
        {
            dataIo.UnpWrite(&buffer[0], writeSize);
            destUnpSize-=writeSize;
        }
    }
}

size_t extract_rar(rar_seekable_stream stream, rar_entry * entry, void * dest) {
    ComprDataIO DataIO;
    Unpack *pUnpack = new Unpack(&DataIO);
    pUnpack->SetThreads(1);

    BridgeArchive archive(stream);
    archive.Seek(entry->header_pos, SEEK_SET);
    archive.ReadHeader();

    ALOGD("%s %ls unpsize:%d\n", __FUNCTION__, archive.FileHead.FileName, archive.FileHead.UnpSize);
    archive.Seek(archive.NextBlockPos - archive.FileHead.PackSize, SEEK_SET);
//
//    File unpackDestFile;
//    unpackDestFile.SetHandleType(FILE_HANDLESTD);

    DataIO.CurUnpRead=0;
    DataIO.CurUnpWrite=0;
    DataIO.UnpHash.Init(archive.FileHead.FileHash.Type, 1);//Cmd->Threads);
    DataIO.PackedDataHash.Init(archive.FileHead.FileHash.Type, 1);//Cmd->Threads);
    DataIO.SetPackedSizeToRead(archive.FileHead.PackSize);
    DataIO.SetFiles(&archive, NULL);//&unpackDestFile);
    DataIO.SetTestMode(false);//TestMode);
    DataIO.SetSkipUnpCRC(false);//SkipSolid);

    byte *pos = (byte*) dest;
    DataIO.SetUnpackToMemory(pos, entry->unpacked_size);

//    if (!archive.BrokenHeader && archive.FileHead.UnpSize > 1000000 &&
//        archive.FileHead.PackSize * 1024 > archive.FileHead.UnpSize &&
//        (archive.FileHead.UnpSize < 100000000 || archive.FileLength() > archive.FileHead.PackSize)) {
//        unpackDestFile.Prealloc(archive.FileHead.UnpSize);
//    }

    if (!archive.FileHead.SplitBefore) {
        if (archive.FileHead.Method == 0) {
            unstoreFile(DataIO, archive.FileHead.UnpSize);
        } else {
            pUnpack->Init(archive.FileHead.WinSize, archive.FileHead.Solid);
            pUnpack->SetDestSize(archive.FileHead.UnpSize);
            if (archive.Format != RARFMT50 && archive.FileHead.UnpVer <= 15) {
                pUnpack->DoUnpack(15, false);//FileCount > 1 && archive.Solid);
            } else {
                pUnpack->DoUnpack(archive.FileHead.UnpVer, archive.FileHead.Solid);
            }
        }
    }

//    if (archive.FileHead.SplitAfter) {
//        LOGD("SplitAfter-------------->");
//    }
//
//    archive.SeekToNext() ;
//    archive.ReadHeader();
//    if (archive.FileHead.SplitBefore) {
//        LOGD("SplitBefore<--------------");
//    }


//    byte *pUnpData;
//    size_t unpDataSize;
//    DataIO.GetUnpackedData(&pUnpData, &unpDataSize);
//    LOGD("%s DONE DONE %ls unpDataSize:%d", __FUNCTION__, archive.FileHead.FileName, unpDataSize);
//
//    char name[2048];
//    WideToChar(archive.FileHead.FileName, name, 2048);
//    char destname[2048];
//    sprintf(destname, "/sdcard/%s", name);
//    LOGD(">>>>>>>>>>>>>destname %s", destname);
//    FILE *file = fopen(destname, "wb");//O_RDWR);
//    fwrite(pUnpData, 1, unpDataSize, file);
//    fclose(file);

//    memcpy(dest, pUnpData, unpDataSize);
//    unpackDestFile.Close();
    delete pUnpack;
    return entry->unpacked_size;//unpDataSize;
}


BridgeArchive::BridgeArchive(rar_seekable_stream stream_arg) {
    ALOGD("BridgeArchive Construct\n");
    stream = stream_arg;
}

BridgeArchive::BridgeArchive(const char *filename) {
    ALOGD("BridgeArchive Construct\n");
    rar_seekable_stream stream_arg;
    int fd = open(filename, O_RDONLY);
    stream_arg.streami = &fd;
    stream_arg.type = FD;
    stream = stream_arg;
}

BridgeArchive::~BridgeArchive() {
    if (stream.type == FD) {
        int fd = *(int*)stream.streami;
        close(fd);
    }
}

int BridgeArchive::Read(void *data, size_t size) {
    return stream.readFunc(stream.streami, data, size);
}

void BridgeArchive::Seek(int64 offset, int method) {
    return stream.seekFunc(stream.streami, offset, method);
}

int64 BridgeArchive::Tell() {
    return stream.tellFunc(stream.streami);
}

int BridgeArchive::isRarArchive() {

    unsigned char data[SIGNATURE_SIZE_COMMON];
    stream.seekFunc(stream.streami, 0, SEEK_SET);

    size_t n = stream.readFunc(stream.streami, data, SIGNATURE_SIZE_COMMON);

    if (n != SIGNATURE_SIZE_COMMON)
        return 0;
    if (memcmp(data, rar_signature, SIGNATURE_SIZE_COMMON))
        return 0;

    return 1;
}

android_rar_info BridgeArchive::GetRarInfo() {
    android_rar_info info;
    info.file_count = 0;
    ALOGD("GetEntries\n");
    rar_entry *entries = (rar_entry*)malloc(sizeof(rar_entry));
    if (IsArchive(true)) {
        while (ReadHeader() > 0) {
            HEADER_TYPE HeaderType = GetHeaderType();
            if (HeaderType == HEAD_ENDARC) {
                info.entries = entries;
                ALOGD("EOF entries total count:%d\n", info.file_count);
                return info;
            }
            switch (HeaderType) {
                case HEAD_FILE:
                    if (!FileHead.SplitBefore) {
                        entries = (rar_entry*)realloc(entries, (info.file_count +1) * sizeof(rar_entry));
                        int nameLen = sizeof(FileHead.FileName)/sizeof(FileHead.FileName[0]);
                        entries[info.file_count].name = (char*)malloc(nameLen * sizeof(char));
                        memset(entries[info.file_count].name, 0, nameLen);
                        WideToChar(FileHead.FileName, entries[info.file_count].name, nameLen);
                        entries[info.file_count].header_pos = Tell() - FileHead.HeadSize;//FileHead.UnpSize;
                        entries[info.file_count].unpacked_size = FileHead.UnpSize;
                        entries[info.file_count].packed_size = FileHead.PackSize;

//                        LOGD("FileName---->%s\n"//, entries[info.file_count].name);
//                             "HeaderPos---->%d\n"//, entries[info.file_count].header_pos);
//                             "PackedSize---->%d\n"//, entries[info.file_count].packed_size);
//                             "UnPackedSize---->%d\n",// entries[info.file_count].unpacked_size);
//                             entries[info.file_count].name,
//                             entries[info.file_count].header_pos,
//                             entries[info.file_count].packed_size,
//                             entries[info.file_count].unpacked_size);

                        info.file_count++;

                    }
                    break;
                case HEAD_SERVICE:
                    break;
            }
            SeekToNext();
        }

    }
    return info;
}
