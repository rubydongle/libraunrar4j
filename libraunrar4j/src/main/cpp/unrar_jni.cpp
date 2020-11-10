#include <jni.h>
#include <string>

#define LOG_TAG "libraunrar4j"

#include "log.h"
#include "settings.h"
#include "BridgeArchive.h"

#include "unrar/dll.hpp"


//#define _UNIX 1


extern "C" {
#define STOP_PROCESSING -1
#define CONTINUE_PROCESSING 1
#define SUCCESS 0

// FLAG_CONTINUATION -> RAR file is continued from previous volume
#define FLAG_CONTINUATION  0x01

// Used when combining UnpSize and UnpSizeHigh from RarHeaderDataEx into a single long
#define BITS_TO_SHIFT_HIGH_SIZE 32

//typedef int bool;
#define true 1
#define false 0

signed long long computeArchiveSize(char *archiveNamePath);
bool extractArchive(JNIEnv *current_env, jobject current_obj, char *archiveNamePath,
                    char *destinationPath);

typedef struct {
    jobject current_obj;
    JNIEnv *current_env;
} jni_env;

JNIEXPORT jboolean JNICALL
BRIDGE_PACKAGE(extract)(JNIEnv *env, jobject instance, jstring archive_,
                        jstring toPath_) {
    bool result;
    char *archive = (char *) env->GetStringUTFChars(archive_, 0);
    char *toPath = (char *) env->GetStringUTFChars(toPath_, 0);

    // TODO
    result = extractArchive(env, instance, archive, toPath);

    env->ReleaseStringUTFChars(archive_, archive);
    env->ReleaseStringUTFChars(toPath_, toPath);

    return (jboolean) result;
}

JNIEXPORT jlong JNICALL
BRIDGE_PACKAGE(computeArchiveSize)(JNIEnv *env, jobject instance,
                                   jstring archive_) {
    long result;
    char *archive = (char *) env->GetStringUTFChars(archive_, 0);

    // TODO
    result = computeArchiveSize(archive);

    env->ReleaseStringUTFChars(archive_, archive);

    return (jlong) result;
}

static struct fields_t {
    jfieldID    mNativePtr;
//    jmethodID   mPostEvent;
//    jclass      mSoundPoolClass;
} java_archive_fields;

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

size_t do_extract_file(rar_seekable_stream stream, rar_entry * ent, void * dest) {
    ComprDataIO DataIO;
    Unpack *pUnpack = new Unpack(&DataIO);
    pUnpack->SetThreads(1);

    BridgeArchive archive(stream);
    archive.Seek(ent->header_pos, SEEK_SET);
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
    DataIO.SetUnpackToMemory(pos, ent->unpacked_size);

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
    return ent->unpacked_size;//unpDataSize;
}

JNIEXPORT void JNICALL
BRIDGE_ARCHIVE(extractFile)(JNIEnv *env, jobject thiz, jobject hd,
                                                     jobject os) {

    BridgeArchive* nativeArchive = (BridgeArchive*)env->GetLongField(thiz, java_archive_fields.mNativePtr);
//    nativeArchive->extractFile()

    // TODO: implement extractFile()
}

JNIEXPORT jint JNICALL
BRIDGE_ARCHIVE(nativeSetupWithFile)(JNIEnv *env, jobject thiz, jobject file,
                                                       jobject callback, jstring password) {
    jint result = -1;
    jclass clazz;
    clazz = env->GetObjectClass(thiz);

    java_archive_fields.mNativePtr = env->GetFieldID(clazz, "mNativePtr", "J");
    // 从file获取filename xxx 构建native的BridgeArchive对象
    BridgeArchive *native_bridge_archive = new BridgeArchive("xxx");
    env->SetLongField(thiz, java_archive_fields.mNativePtr, (jlong)native_bridge_archive);

    // 后续使用
    BridgeArchive* nativeArchive = (BridgeArchive*)env->GetLongField(thiz, java_archive_fields.mNativePtr);

    // TODO: implement native_setup()
    return 0;
}

JNIEXPORT jint JNICALL
BRIDGE_ARCHIVE(nativeSetupWithFilePath)(JNIEnv *env, jobject thiz,
                                                                 jstring filepath) {
    jint result = -1;
    jclass clazz;
    clazz = env->GetObjectClass(thiz);

    java_archive_fields.mNativePtr = env->GetFieldID(clazz, "mNativePtr", "J");
    // 从file获取filename xxx 构建native的BridgeArchive对象
    const char* path = env->GetStringUTFChars(filepath, NULL);
    BridgeArchive *native_bridge_archive = new BridgeArchive(path);
    env->SetLongField(thiz, java_archive_fields.mNativePtr, (jlong)native_bridge_archive);

    return 0;
}

JNIEXPORT void JNICALL
Java_ruby_blacktech_libraunrar4j_Archive_nativeRelease(JNIEnv *env, jobject thiz) {
    // TODO: implement nativeRelease()
    BridgeArchive* nativeArchive = (BridgeArchive*)env->GetLongField(thiz, java_archive_fields.mNativePtr);
    // 如果不在操作了
    if (nativeArchive != 0) {
        delete nativeArchive;
    }
}

JNIEXPORT void JNICALL
BRIDGE_ARCHIVE(getEntries)(JNIEnv *env, jobject thiz) {
    BridgeArchive* nativeArchive = (BridgeArchive*)env->GetLongField(thiz, java_archive_fields.mNativePtr);
    android_rar_info info = nativeArchive->GetRarInfo();
    ALOGD("archive has entry counts:%d", info.file_count);
    // TODO: implement getEntries()
}

JNIEXPORT jobject JNICALL
Java_ruby_blacktech_libraunrar4j_Archive_getFileHeaderEntries(JNIEnv *env, jobject thiz) {
    // TODO: implement getFileHeaderEntries()
    jclass list_clazz = env->FindClass("java/util/ArrayList");
    if (list_clazz == NULL) {
        ALOGE("ArrayList not Found!");
        return NULL;
    }
    jmethodID list_method_init = env->GetMethodID(list_clazz, "<init>", "()V");
    jobject list_obj = env->NewObject(list_clazz, list_method_init);
    jmethodID list_method_add = env->GetMethodID(list_clazz, "add", "(Ljava/lang/Object;)Z");

    jclass FileHeader_clazz = env->FindClass("ruby/blacktech/libraunrar4j/rarfile/FileHeader");
    if (FileHeader_clazz == NULL) {
        ALOGE("FileHeader not Found!");
        return NULL;
    }
    jmethodID FileHeader_init = env->GetMethodID(FileHeader_clazz, "<init>", "(Ljava/lang/String;)V");
    if (FileHeader_init == NULL) {
        ALOGE("FileHeader init not Found!");
        return NULL;
    }

    BridgeArchive* nativeArchive = (BridgeArchive*)env->GetLongField(thiz, java_archive_fields.mNativePtr);
    list<FileHeader> fileHeaders = nativeArchive->getFileHeaders();
    list<FileHeader>::iterator iter;
    for(iter = fileHeaders.begin(); iter != fileHeaders.end() ;iter++)
    {
        int nameLen = sizeof(iter->FileName)/sizeof(iter->FileName[0]);
        char *name = (char*)malloc(nameLen * sizeof(char));
        memset(name, 0, nameLen);
        WideToChar(iter->FileName, name, nameLen);

        jobject fileHeader_obj = env->NewObject(FileHeader_clazz, FileHeader_init, env->NewStringUTF(name));
        env->CallBooleanMethod(list_obj, list_method_add, fileHeader_obj);

        void *dest = malloc(iter->UnpSize);
        FileHeader file = *iter;
        nativeArchive->extractFile(&file, dest);
        free(dest);
    }
//    android_rar_info info = nativeArchive->GetRarInfo();
//    rar_entry* entry = info.entries;
//    for (int i = 0; i < info.file_count; i++) {
//        ALOGD("name:%s header_pos:%d packed_size:%d unpacked_size:%d", entry->name, entry->header_pos, entry->packed_size, entry->unpacked_size);
//        jobject fileHeader_obj = env->NewObject(FileHeader_clazz, FileHeader_init, env->NewStringUTF(entry->name));
//        env->CallBooleanMethod(list_obj, list_method_add, fileHeader_obj);
//        entry++;
//    }

    return list_obj;
}
// PROCEDURES METHODS


/* === PROCEDURES->METHODS ======================================================================= */

void incrementCurrentUncompressedSize(JNIEnv *current_env, jobject current_obj, long i) {
    jlong ji = i;
    jclass cls = current_env->GetObjectClass(current_obj);
    jmethodID mid = current_env->GetMethodID(cls, "incrementCurrentUncompressedSize", "(J)V");
    if (mid == 0) {
        ALOGE("Could not find method id for incrementCurrentUncompressedSize()");
        return;
    }
    current_env->CallVoidMethod(current_obj, mid, ji);
}

void fileChange(JNIEnv *current_env, jobject current_obj, const char *nextFileName) {
    jclass cls = current_env->GetObjectClass(current_obj);
    jstring fileNameAsJstring = current_env->NewStringUTF(nextFileName);
    jmethodID mid = current_env->GetMethodID(cls, "fileChange", "(Ljava/lang/String;)V");
    if (mid == 0) {
        ALOGE("Could not find method id for fileChange");
        return;
    }
    current_env->CallVoidMethod(current_obj, mid, fileNameAsJstring);
}

void volumeChange(JNIEnv *current_env, jobject current_obj, const char *nextVolumeName) {
    jclass cls = current_env->GetObjectClass(current_obj);
    jstring volumeNameAsJstring = current_env->NewStringUTF(nextVolumeName);
    jmethodID mid = current_env->GetMethodID(cls, "volumeChange", "(Ljava/lang/String;)V");
    ALOGD("volumeChange %s", nextVolumeName);
    if (mid == 0) {
        ALOGE("Could not find method id for volumeChange\n");
        return;
    }
    current_env->CallVoidMethod(current_obj, mid, volumeNameAsJstring);
}

jstring volumeNotFound(JNIEnv *current_env, jobject current_obj, const char *nextVolumeName) {
    jclass cls = current_env->GetObjectClass(current_obj);
    jstring volumeNameAsJstring = current_env->NewStringUTF(nextVolumeName);
    jmethodID mid = current_env->GetMethodID(cls, "volumeNotFound",
                                             "(Ljava/lang/String;)Ljava/lang/String;");
    ALOGD("volumeNotFound %s", nextVolumeName);
    if (mid == 0) {
        ALOGE("Could not find method id for volumeNotFound\n");
        return current_env->NewStringUTF("");
    }
    return (jstring) current_env->CallObjectMethod(current_obj, mid, volumeNameAsJstring);
}

int volumeSwitch(JNIEnv *current_env, jobject current_obj, char *nextArchiveName, int mode) {
    ALOGD("volumeSwitch");
    jstring newArchiveName;
    const char *newArchiveNameUtf;
    switch (mode) {
        case RAR_VOL_ASK :
            newArchiveName = volumeNotFound(current_env, current_obj, nextArchiveName);
            if (current_env->GetStringUTFLength(newArchiveName) == 0) {
                ALOGE("Telling rar library to stop processing, volume was not found");
                return STOP_PROCESSING;
            } else {
                newArchiveNameUtf = current_env->GetStringUTFChars(newArchiveName, JNI_FALSE);
                strcpy(nextArchiveName, newArchiveNameUtf);
                ALOGE("User chose new archive %s", nextArchiveName);
                current_env->ReleaseStringUTFChars(newArchiveName, newArchiveNameUtf);
                return CONTINUE_PROCESSING;
            }
        case RAR_VOL_NOTIFY :
            volumeChange(current_env, current_obj, nextArchiveName);
            return CONTINUE_PROCESSING;
        default :
            ALOGE("Unknown mode on volumeChange\n");
            return STOP_PROCESSING;
    }
}

int processData(JNIEnv *current_env, jobject current_obj, unsigned char *block, int size) {
    incrementCurrentUncompressedSize(current_env, current_obj, (long) size);
    return CONTINUE_PROCESSING;
}

int needPassword(JNIEnv *current_env, jobject current_obj, char *passwordBuffer, int bufferSize) {
    int length;
    const char *passwordUtf;
    jclass cls = current_env->GetObjectClass(current_obj);
    jmethodID mid = current_env->GetMethodID(cls, "requestPassword", "()Ljava/lang/String;");
    if (mid == 0) {
        ALOGE("Could not find method id for requestPassword\n");
        passwordBuffer[0] = 0;
        return STOP_PROCESSING;
    }
    jstring passwordJstring = (jstring) current_env->CallObjectMethod(current_obj, mid);
    length = current_env->GetStringUTFLength(passwordJstring);
    if (length == 0 || length >= bufferSize) {
        passwordBuffer[0] = 0;
        ALOGD("needPassowd get Password %s", passwordBuffer);
        return STOP_PROCESSING;
    } else {
        passwordUtf = current_env->GetStringUTFChars(passwordJstring, JNI_FALSE);
        strcpy(passwordBuffer, passwordUtf);
        ALOGD("needPassowd get Password %s", passwordBuffer);
        current_env->ReleaseStringUTFChars(passwordJstring, passwordUtf);
        return CONTINUE_PROCESSING;
    }
}


// ====RAR CALLBACK
// https://github.com/nitely/ochDownloader/blob/master/addons/unrar/unrar_lib/lib/Documentation/RARCallback.htm
int processRarCallbackMessage(UINT msg, LONG UserData, LONG P1, LONG P2) {
    jobject current_obj = ((jni_env *) UserData)->current_obj;
    JNIEnv *current_env = ((jni_env *) UserData)->current_env;
    switch (msg) {
        /**
        Required volume is absent. The function should prompt user
        and return zero or positive value to retry or return -1 value to
        terminate operation. The function may also set a new volume
        name, placing up to 260 characters to the address specified
        by P1 parameter.<br><br>
        Volume name uses single byte ANSI encoding for UCM_CHANGEVOLUME
        and Unicode for UCM_CHANGEVOLUMEW. If application provides
        a new volume name in UCM_CHANGEVOLUMEW, UCM_CHANGEVOLUME
        is not sent.
        */
        case UCM_CHANGEVOLUME :
            ALOGD("UCM_CHANGEVOLUME");
            return volumeSwitch(current_env, current_obj, (char *) P1, (int) P2);
        case UCM_CHANGEVOLUMEW:
            ALOGD("UCM_CHANGEVOLUME W");
            return volumeSwitch(current_env, current_obj, (char *) P1, (int) P2);
        case UCM_PROCESSDATA :
            ALOGD("UCM_PROCESSDATA");
            return processData(current_env, current_obj, (unsigned char *) P1, (int) P2);
            /**
            DLL needs a password to process archive. This message must be processed if you wish to be able to handle encrypted archives.
            Return zero or a positive value to continue process or -1 to cancel the archive operation.
            P1 contains the address pointing to the buffer for a password in single byte encoding. You need to copy a password
            here. Password uses single byte ANSI encoding for UCM_NEEDPASSWORD
            and Unicode for UCM_NEEDPASSWORDW. If application provides
            a password in UCM_NEEDPASSWORDW, UCM_CHANGEVOLUME is not sent.
            P2 contains the size of password buffer in characters.</p>
            */
        case UCM_NEEDPASSWORD :
            ALOGD("UCM_NEEDPASSWORD");
            return needPassword(current_env, current_obj, (char *) P1, (int) P2);
            //case UCM_NEEDPASSWORDW:
            //   ALOGD("UCM_NEEDPASSWORD W");
            //  return needPassword(current_env, current_obj, (char *) P1, (int) P2);
        default :
            ALOGE("Unknown message passed to RAR callback function.");
            return STOP_PROCESSING;
    }
}

//=====================
bool
extractAllFiles(JNIEnv *current_env, jobject current_obj, HANDLE archive, char *destinationPath) {
    struct RARHeaderData fileHeader;
    int result;

    //fileHeader.CmtBuf = NULL;

    while ((result = RARReadHeader(archive, &fileHeader)) == SUCCESS) {
        ALOGD("Extracting file %s to %s", fileHeader.FileName, destinationPath);

        fileChange(current_env, current_obj, fileHeader.FileName);

        result = RARProcessFile(archive, RAR_EXTRACT, destinationPath, NULL);

        if (result != SUCCESS) {
            ALOGE("Error processing file reason %d", result);
            return false;
        }
    }

    if (result != ERAR_END_ARCHIVE) {
        ALOGE("Error in archive\n");
        return false;
    }

    return true;
}

bool extractArchive(JNIEnv *current_env, jobject current_obj, char *archiveNamePath,
                    char *destinationPath) {
    struct RAROpenArchiveData archiveData;
    HANDLE archive;
    bool result;
    jni_env env_wrapper;

    archiveData.OpenMode = RAR_OM_EXTRACT;
    archiveData.ArcName = archiveNamePath;
    //archiveData.CmtBuf     = NULL;
    //archiveData.CmtBufSize = 0;

    //        0成功
    //        ERAR_NO_MEMORY 内存不足，无法初始化数据结构
    //        ERAR_BAD_DATA 压缩包头损坏
    //        ERAR_BAD_ARCHIVE 不是有效的Rar压缩包
    //         ERAR_UNKNOWN_FORMAT 无法识别的压缩方式
    //        ERAR_EOPEN 压缩包打开错误
    archiveData.OpenResult = 0;
    //        设置存放注释缓冲区，最大不能超过64KB;设为null表示不读取注释
    archiveData.CmtBuf = NULL;
    //        设置缓冲区大小
    archiveData.CmtBufSize = 0;
    //        实际读取到的注释大小
    archiveData.CmtSize = 0;
    //        输出注释状态：
    //        ０　注释不存在
    //        1  注释读取完毕
    //         ERAR_NO_MEMORY 内存不足
    //         ERAR_BAD_DATA 注释损坏
    //        ERAR_UNKNOWN_FORMAT 注释格式无效
    //        ERAR_SMALL_BUF 缓冲区过小
    archiveData.CmtState = 0;

    archive = RAROpenArchive(&archiveData);

    if (archiveData.OpenResult != SUCCESS) {
        ALOGE("Error opening archive %s\n", archiveNamePath);
        return false;
    }

    env_wrapper.current_obj = current_obj;
    env_wrapper.current_env = current_env;

    RARSetCallback(archive, processRarCallbackMessage, (LONG) &env_wrapper);

    volumeChange(current_env, current_obj, archiveNamePath);

    result = extractAllFiles(current_env, current_obj, archive, destinationPath);

    if (RARCloseArchive(archive) != SUCCESS) {
        ALOGE("Error closing archive %s\n", archiveNamePath);
        return false;
    }

    return result;
}


signed long long computeArchiveSizeAllFiles(HANDLE archive) {
    struct RARHeaderDataEx fileHeader;
    int result;
    signed long long totalSize = 0;

    //fileHeader.CmtBuf = NULL;

    while ((result = RARReadHeaderEx(archive, &fileHeader)) == SUCCESS) {
        ALOGE("Reading header for file %s\n", fileHeader.FileName);

        result = RARProcessFile(archive, RAR_SKIP, NULL, NULL);

        if (result != SUCCESS) {
            ALOGE("Error processing file\n");
            return 0;
        }

        ALOGE("  size --> %u %u (%lli)\n", fileHeader.UnpSize, fileHeader.UnpSizeHigh, totalSize);

        if (!(fileHeader.Flags & FLAG_CONTINUATION)) {
            totalSize += (unsigned long long) fileHeader.UnpSize +
                         (((unsigned long long) fileHeader.UnpSizeHigh) << BITS_TO_SHIFT_HIGH_SIZE);
        }
    }

    if (result != ERAR_END_ARCHIVE)
        ALOGE("Error in archive\n");

    ALOGE("  Final total size: %lli\n", totalSize);

    return totalSize;
}

signed long long computeArchiveSize(char *archiveNamePath) {
    struct RAROpenArchiveData archiveData;
    HANDLE archive;
    signed long long size;

    archiveData.OpenMode = RAR_OM_LIST;
    archiveData.ArcName = archiveNamePath;
    archiveData.CmtBuf = NULL;
    archiveData.CmtBufSize = 0;

    archive = RAROpenArchive(&archiveData);

    if (archiveData.OpenResult != SUCCESS) {
        ALOGE("Error opening archive %s\n", archiveNamePath);
        return 0;
    }

    size = computeArchiveSizeAllFiles(archive);

    if (RARCloseArchive(archive) != SUCCESS)
        ALOGE("Error closing archive %s\n", archiveNamePath);

    return size;
}












rar_seekable_stream* open_file(int fd) {
    rar_seekable_stream stream;

    stream.streami = &fd;
    stream.readFunc = NULL;
    stream.seekFunc = NULL;
    stream.tellFunc = NULL;
}


struct buffer_s
{
    int refs;
    unsigned char *data;
    size_t cap, len;
    int unused_bits;
    int shared;
};

buffer_s* read_rar_entry(BridgeArchive *arch, const char *name) {

    rar_seekable_stream* stream;// = arch.get()
    buffer_s *ubuf;
    rar_entry *ent;

    //ent = 查找entry
    if (!ent) {
        // throw exception to java,没有entry
    }

    int rar_unpacked_size = ent->unpacked_size;//获取entry大小
    //ubuf = 分配内存,依据entry大小
    stream->seekFunc(stream->streami, 0, SEEK_SET);
    // do_extract将数据解压到内存
    extract_rar(*stream, ent, ubuf);
    if (ubuf->len != ent->unpacked_size) {
        // 解压数据不一致，抛出异常到java
    }
    //释放数据
    return ubuf;
}






}
