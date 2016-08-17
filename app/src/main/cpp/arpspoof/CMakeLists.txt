set(arpspoof_srcs hashmap.c arp.c net.c arpspoof.c)

SET(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -O2 -g -ffunction-sections -fdata-sections")
SET(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -Wl,--gc-sections")


add_executable(arpspoof ${arpspoof_srcs})
target_link_libraries( arpspoof libpcap libnet)
target_include_directories (arpspoof PUBLIC ${CMAKE_CURRENT_SOURCE_DIR} ${CMAKE_CURRENT_SOURCE_DIR}/../libnet/include ${CMAKE_CURRENT_SOURCE_DIR}/../include ${CMAKE_CURRENT_SOURCE_DIR}/../include/linux )
