add_library(pcap-parser SHARED ${CMAKE_CURRENT_SOURCE_DIR}/pcap-parser.cpp)

find_library(log-lib log )
target_link_libraries( pcap-parser log android libpcap)

target_include_directories (pcap-parser PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})
