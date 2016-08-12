add_executable(native-binary ${CMAKE_CURRENT_SOURCE_DIR}/native-binary.cpp)
target_link_libraries( native-binary libpcap)
target_include_directories (native-binary PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})
