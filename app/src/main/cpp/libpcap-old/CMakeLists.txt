SET(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wno-unused-parameter -D_BSD_SOURCE -D_U_=\"__attribute__((unused))\" -Werror ")
SET(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -Wno-sign-compare")
SET(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DHAVE_CONFIG_H")
SET(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -include strings.h")

set(libpcap_srcs pcap-linux.c pcap-usb-linux.c pcap-can-linux.c pcap-netfilter-linux.c pcap-netfilter-linux-android.c fad-gifc.c pcap.c inet.c gencode.c optimize.c nametoaddr.c etherent.c savefile.c sf-pcap.c sf-pcap-ng.c pcap-common.c bpf/net/bpf_filter.c bpf_image.c bpf_dump.c version.c scanner.c grammar.c)
add_library( libpcap STATIC ${libpcap_srcs})

# Make sure the compiler can find include files for our libpcap library
# when other libraries or executables link to libpcap
target_include_directories (libpcap PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})