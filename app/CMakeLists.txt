# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.

cmake_minimum_required(VERSION 3.4.1)

# This variable is a cache entry initialized (to FALSE) by the project() command. Users may enable
# the option in their local build tree to get more verbose output from Makefile builds and
# show each command line as it is launched.

set(CMAKE_VERBOSE_MAKEFILE on)


# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds it for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             # Associated headers in the same location as their source
             # file are automatically included.
             src/main/cpp/hello-cpp/native-lib.cpp )

set(libpcap_root ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/libpcap CACHE PATH "libpcap for Android")
include_directories(${libpcap_root} )
set(libpcap_srcs ${libpcap_root}/pcap-linux.c ${libpcap_root}/pcap-usb-linux.c  ${libpcap_root}/pcap-can-linux.c ${libpcap_root}/pcap-netfilter-linux.c ${libpcap_root}/pcap-netfilter-linux-android.c ${libpcap_root}/fad-gifc.c ${libpcap_root}/pcap.c ${libpcap_root}/inet.c ${libpcap_root}/gencode.c ${libpcap_root}/optimize.c ${libpcap_root}/nametoaddr.c ${libpcap_root}/etherent.c ${libpcap_root}/savefile.c ${libpcap_root}/sf-pcap.c ${libpcap_root}/sf-pcap-ng.c ${libpcap_root}/pcap-common.c ${libpcap_root}/bpf_image.c ${libpcap_root}/bpf_dump.c ${libpcap_root}/scanner.c ${libpcap_root}/grammar.c ${libpcap_root}/bpf_filter.c ${libpcap_root}/version.c)
add_library( libpcap SHARED ${libpcap_srcs})

# Searches for a specified prebuilt library and stores the path as a
# variable. Because system libraries are included in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in the
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       native-lib

                       # Links the target library to the log library
                       # included in the NDK.
                       $\{log-lib} libpcap)
