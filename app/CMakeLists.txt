# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.

cmake_minimum_required(VERSION 3.4.1)

# This variable is a cache entry initialized (to FALSE) by the project() command. Users may enable
# the option in their local build tree to get more verbose output from Makefile builds and
# show each command line as it is launched.

set(CMAKE_VERBOSE_MAKEFILE on)

add_subdirectory (src/main/cpp/libpcap)
add_subdirectory (src/main/cpp/native-lib)

