set(tcpick_srcs args.c colors.c datalink.c display.c fragments.c lookup_query.c lookup_tree.c loop.c msg.c quit.c tcpick.c time.c timer.c tracker.c verify.c write.c)

SET(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -O2")
SET(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} ")


add_executable(tcpick ${tcpick_srcs})
target_link_libraries( tcpick libpcap)
target_include_directories (tcpick PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})
