#include <stdlib.h>
#include <string>
#include <iostream>
#include <../libpcap/pcap.h>

using namespace std;

int main(int argc, char *argv[]) {
    string hello = "Hello from C++";
    char errbuf[PCAP_ERRBUF_SIZE];
    char *dev;

    dev = pcap_lookupdev(errbuf);
    if (dev == NULL) {
        printf("pcap_lookupdev => errbuf: [%s]\n", errbuf);
        return NULL;
    }

    cout << "Message from native code: " << hello << "\n";

    printf("Device: [%s]\n", dev);
    return EXIT_SUCCESS;
}
