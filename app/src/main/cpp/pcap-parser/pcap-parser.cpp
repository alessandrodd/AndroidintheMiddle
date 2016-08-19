#include <jni.h>
#include <string>
#include <../libpcap/pcap.h>
#include <iostream>
#include <net/ethernet.h>
#include <netinet/ip.h>
#include <netinet/tcp.h>
#include <arpa/inet.h>
#include <android/log.h>

#define TAG "pcap-parser.cpp"

using namespace std;

#ifdef __cplusplus
extern "C" {
#endif

typedef struct {
    JNIEnv *env;
    jobject obj;
    jmethodID method;
} callback;

void packetHandler(callback *cb, const struct pcap_pkthdr *pkthdr, const u_char *packet);

JNIEXPORT jint JNICALL Java_it_uniroma2_giadd_aitm_models_PcapParser_parsePcapFile(JNIEnv *env,
                                                                                   jobject obj,
                                                                                   jstring path) {
    const char *pcap_path = env->GetStringUTFChars(path, JNI_FALSE);

    jclass clazz = env->FindClass("it/uniroma2/giadd/aitm/models/PcapParser");
    // Get the method that you want to call
    jmethodID onPacketParsed = env->GetMethodID(clazz, "onPacketParsed", "(Ljava/lang/String;)V");

    callback *cb = new callback;
    cb->env = env;
    cb->obj = obj;
    cb->method = onPacketParsed;

    pcap_t *descr;
    char errbuf[PCAP_ERRBUF_SIZE];

    // open capture file for offline processing
    descr = pcap_open_offline(pcap_path, errbuf);
    if (descr == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Error in pcap_open_offline: %s", errbuf);
        free(cb);
        env->ReleaseStringUTFChars(path, pcap_path);
        return 1;
    }

    // start packet processing loop, just like live capture
    if (pcap_loop(descr, 0, (pcap_handler) packetHandler, (u_char *) &cb) < 0) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Error in pcap_loop: %s", pcap_geterr(descr));
        free(cb);
        env->ReleaseStringUTFChars(path, pcap_path);
        return 1;
    }
    free(cb);
    env->ReleaseStringUTFChars(path, pcap_path);
    return 0;
}


void packetHandler(callback *cb, const struct pcap_pkthdr *pkthdr, const u_char *packet) {
    const struct ether_header *ethernetHeader;
    const struct ip *ipHeader;
    const struct tcphdr *tcpHeader;
    char sourceIp[INET_ADDRSTRLEN];
    char destIp[INET_ADDRSTRLEN];
    u_int sourcePort, destPort;
    u_char *data;
    int dataLength = 0;
    string dataStr = "";

    ethernetHeader = (struct ether_header *) packet;
    if (ntohs(ethernetHeader->ether_type) == ETHERTYPE_IP) {
        ipHeader = (struct ip *) (packet + sizeof(struct ether_header));
        inet_ntop(AF_INET, &(ipHeader->ip_src), sourceIp, INET_ADDRSTRLEN);
        inet_ntop(AF_INET, &(ipHeader->ip_dst), destIp, INET_ADDRSTRLEN);

        if (ipHeader->ip_p == IPPROTO_TCP) {
            tcpHeader = (tcphdr *) (packet + sizeof(struct ether_header) + sizeof(struct ip));
            sourcePort = ntohs(tcpHeader->source);
            destPort = ntohs(tcpHeader->dest);
            data = (u_char *) (packet + sizeof(struct ether_header) + sizeof(struct ip) +
                               sizeof(struct tcphdr));
            dataLength = pkthdr->len -
                         (sizeof(struct ether_header) + sizeof(struct ip) + sizeof(struct tcphdr));

            // convert non-printable characters, other than carriage return, line feed,
            // or tab into periods when displayed.
            for (int i = 0; i < dataLength; i++) {
                if ((data[i] >= 32 && data[i] <= 126) || data[i] == 10 || data[i] == 11 ||
                    data[i] == 13) {
                    dataStr += (char) data[i];
                } else {
                    dataStr += ".";
                }
            }

            char msg[60] = "Hello";
            jstring result;

            //result = (cb->env)->NewStringUTF(msg);

            //(cb->env)->CallVoidMethod(cb->obj, cb->method, result);

            // print the results
            cout << sourceIp << ":" << sourcePort << " -> " << destIp << ":" << destPort << endl;
            __android_log_print(ANDROID_LOG_DEBUG, TAG,
                                "Source ip: %s source port: %u => destination ip: %s destination port: %u",
                                sourceIp, sourcePort, destIp, destPort);

            if (dataLength > 0) {
                cout << dataStr << endl;
            }
        }
    }
}
#ifdef __cplusplus
}
#endif
