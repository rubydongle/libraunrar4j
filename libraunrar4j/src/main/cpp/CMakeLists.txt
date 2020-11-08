# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
             superunrar-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             # native-lib.cpp
             unrar/strlist.cpp
             unrar/strfn.cpp
             unrar/pathfn.cpp
             unrar/smallfn.cpp
             unrar/global.cpp
             unrar/file.cpp
             unrar/filefn.cpp
             unrar/filcreat.cpp
             unrar/archive.cpp
             unrar/arcread.cpp
             unrar/unicode.cpp
             unrar/system.cpp
             unrar/isnt.cpp
             unrar/crypt.cpp
             unrar/crc.cpp
             unrar/rawread.cpp
             unrar/encname.cpp
             unrar/resource.cpp
             unrar/match.cpp
             unrar/timefn.cpp
             unrar/rdwrfn.cpp
             unrar/consio.cpp
             unrar/options.cpp
             unrar/errhnd.cpp
             unrar/rarvm.cpp
             unrar/secpassword.cpp
             unrar/rijndael.cpp
             unrar/getbits.cpp
             unrar/sha1.cpp
             unrar/sha256.cpp
             unrar/blake2s.cpp
             unrar/hash.cpp
             unrar/extinfo.cpp
             unrar/extract.cpp
             unrar/volume.cpp
             unrar/list.cpp
             unrar/find.cpp
             unrar/unpack.cpp
             unrar/headers.cpp
             unrar/threadpool.cpp
             unrar/rs16.cpp
             unrar/cmddata.cpp
             unrar/ui.cpp
             unrar/filestr.cpp
             unrar/dll.cpp
             unrar/scantree.cpp
             unrar/qopen.cpp
             unrar/rar.cpp
        BridgeArchive.cpp
             unrar_jni.cpp)

# Specifies a path to native header files.
include_directories(unrar/)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       superunrar-lib

                       # Links the target library to the log library
                       # included in the NDK
                       ${log-lib} )