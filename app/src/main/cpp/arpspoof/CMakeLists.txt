set(arpspoof_srcs arp.c arpspoof.c ensure_death.c)
add_executable(arpspoof ${arpspoof_srcs})
target_link_libraries( arpspoof libpcap libnet)
target_include_directories (arpspoof PUBLIC ${CMAKE_CURRENT_SOURCE_DIR} ${CMAKE_CURRENT_SOURCE_DIR}/../libnet/include ${CMAKE_CURRENT_SOURCE_DIR}/../include )
