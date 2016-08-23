#include <jni.h>
#include <string>
#include <../libpcap/pcap.h>
#include <net/ethernet.h>
#include <netinet/ip.h>
#include <netinet/tcp.h>
#include <netinet/udp.h>
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
    env->ExceptionClear();
    const char *pcap_path = env->GetStringUTFChars(path, JNI_FALSE);

    jclass pcapParserClass = env->FindClass("it/uniroma2/giadd/aitm/models/PcapParser");
    if (pcapParserClass == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Unable to find class PcapParser");
        return 1;
    }
    // Get the method that you want to call
    jmethodID onPacketParsed = env->GetMethodID(pcapParserClass, "onPacketParsed",
                                                "(Lit/uniroma2/giadd/aitm/models/MyIpPacket;)V");
    if (onPacketParsed == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Unable to find method onPacketParsed");
        return 1;
    }

    callback *cb = new callback;
    cb->env = env;
    cb->obj = obj;
    cb->method = onPacketParsed;

    pcap_t *descr;
    char errbuf[PCAP_ERRBUF_SIZE];

    // open capture file for offline processing
    // If all goes well, we get a pcap_t descriptor returned. If not, check the error buffer for details.
    descr = pcap_open_offline(pcap_path, errbuf);
    if (descr == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Error in pcap_open_offline: %s", errbuf);
        free(cb);
        env->ReleaseStringUTFChars(path, pcap_path);
        return 1;
    }

    // start packet processing loop, just like live capture
    if (pcap_loop(descr, -1, (pcap_handler) packetHandler, (u_char *) cb) < 0) {
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
    const struct udphdr *udpHeader;
    char sourceIp[INET_ADDRSTRLEN];
    char destIp[INET_ADDRSTRLEN];
    u_int sourcePort, destPort;
    u_char *data;
    int dataLength = 0;

    ethernetHeader = (struct ether_header *) packet;
    // we use the ntohs() to convert the type from network byte order to host byte order.
    if (ntohs(ethernetHeader->ether_type) == ETHERTYPE_IP) {
        const u_char *ipPacket = packet + sizeof(struct ether_header);
        ipHeader = (struct ip *) ipPacket;
        inet_ntop(AF_INET, &(ipHeader->ip_src), sourceIp, INET_ADDRSTRLEN);
        inet_ntop(AF_INET, &(ipHeader->ip_dst), destIp, INET_ADDRSTRLEN);

        jobject myTransportLayerPacket;


        if (ipHeader->ip_p == IPPROTO_TCP) {
            const u_char *tcpPacket = ipPacket + sizeof(struct ip);
            tcpHeader = (tcphdr *) (tcpPacket);
            sourcePort = ntohs(tcpHeader->source);
            destPort = ntohs(tcpHeader->dest);

            data = (u_char *) (tcpPacket +
                               sizeof(struct tcphdr));
            dataLength = pkthdr->len -
                         (sizeof(struct ether_header) + sizeof(struct ip) + sizeof(struct tcphdr));
            jbyte transportData[dataLength];
            int i;
            for (i = 0; i < dataLength; i++) {
                transportData[i] = *(data + i);
            }

            jbyteArray byteArray = (cb->env)->NewByteArray(dataLength);
            if (byteArray == NULL) {
                __android_log_print(ANDROID_LOG_ERROR, TAG,
                                    "Unable to find create a new bytearray of size %d [1]",
                                    dataLength);
                (cb->env)->ExceptionClear();
                return;
            }
            (cb->env)->SetByteArrayRegion(byteArray, 0, dataLength, transportData);
            jboolean flag = (cb->env)->ExceptionCheck();
            if (flag) {
                __android_log_print(ANDROID_LOG_ERROR, TAG,
                                    "Exception occurred after SetByteArrayRegion [1]");
                (cb->env)->ExceptionClear();
                return;
            }
            jint tcpSourcePort = sourcePort;
            jint tcpDestinationPort = destPort;
            jlong tcpAcknowledgmentNumber = tcpHeader->ack;
            jlong tcpAcknowledgmentSequence = tcpHeader->ack_seq;
            jint tcpChecksum = tcpHeader->check;
            jint tcpCwr = tcpHeader->cwr;
            jint tcpDataOffset = tcpHeader->doff;
            jint tcpEce = tcpHeader->ece;
            jint tcpFin = tcpHeader->fin;
            jint tcpPsh = tcpHeader->psh;
            jint tcpRes1 = tcpHeader->res1;
            jint tcpRst = tcpHeader->rst;
            jlong tcpSequenceNumber = tcpHeader->seq;
            jint tcpSyn = tcpHeader->syn;
            jint tcpUrg = tcpHeader->urg;
            jint tcpUrgPointer = tcpHeader->urg_ptr;
            jint tcpWindow = tcpHeader->window;

            jclass myTcpPacketClass = (cb->env)->FindClass(
                    "it/uniroma2/giadd/aitm/models/MyTcpPacket");
            if (myTcpPacketClass == NULL) {
                __android_log_print(ANDROID_LOG_ERROR, TAG, "Unable to find class MyTcpPacket");
                (cb->env)->ExceptionClear();
                return;
            }
            jmethodID myTcpPacketConstructor = (cb->env)->GetMethodID(
                    myTcpPacketClass, "<init>", "([BIIJJIIIIJIIIIIIII)V");
            if (myTcpPacketConstructor == NULL) {
                __android_log_print(ANDROID_LOG_ERROR, TAG,
                                    "Unable to find method myTcpPacketConstructor");
                (cb->env)->ExceptionClear();
                return;
            }
            myTransportLayerPacket = (cb->env)->NewObject(myTcpPacketClass, myTcpPacketConstructor,
                                                          byteArray, tcpSourcePort,
                                                          tcpDestinationPort, tcpSequenceNumber,
                                                          tcpAcknowledgmentNumber, tcpDataOffset,
                                                          tcpWindow, tcpChecksum, tcpUrgPointer,
                                                          tcpAcknowledgmentSequence, tcpCwr, tcpEce,
                                                          tcpFin, tcpPsh, tcpRes1, tcpRst, tcpSyn,
                                                          tcpUrg);
            if (myTransportLayerPacket == NULL) {
                __android_log_print(ANDROID_LOG_ERROR, TAG,
                                    "Unable to instantiate class MyTcpPacket");
                (cb->env)->ExceptionClear();
                return;
            }
            (cb->env)->DeleteLocalRef(myTcpPacketClass);
            (cb->env)->DeleteLocalRef(byteArray);
        }
        else if (ipHeader->ip_p == IPPROTO_UDP) {
            const u_char *udpPacket = ipPacket + sizeof(struct ip);
            udpHeader = (udphdr *) udpPacket;
            sourcePort = ntohs(udpHeader->source);
            destPort = ntohs(udpHeader->dest);
            data = (u_char *) (udpPacket + sizeof(struct udphdr));
            dataLength = pkthdr->len -
                         (sizeof(struct ether_header) + sizeof(struct ip) +
                          sizeof(struct udphdr));
            jbyte transportData[dataLength];
            int i;
            for (i = 0; i < dataLength; i++) {
                transportData[i] = *(data + i);
            }

            jbyteArray byteArray = (cb->env)->NewByteArray(dataLength);
            if (byteArray == NULL) {
                __android_log_print(ANDROID_LOG_ERROR, TAG,
                                    "Unable to find create a new bytearray of size %d [2]",
                                    dataLength);
                (cb->env)->ExceptionClear();
                return;
            }
            (cb->env)->SetByteArrayRegion(byteArray, 0, dataLength, transportData);
            jboolean flag = (cb->env)->ExceptionCheck();
            if (flag) {
                __android_log_print(ANDROID_LOG_ERROR, TAG,
                                    "Exception occurred after SetByteArrayRegion [2]");
                (cb->env)->ExceptionClear();
                return;
            }
            jint udpSourcePort = sourcePort;
            jint udpDestinationPort = destPort;
            jint udpLength = udpHeader->len;
            jint udpChecksum = udpHeader->check;

            jclass myUdpPacketClass = (cb->env)->FindClass(
                    "it/uniroma2/giadd/aitm/models/MyUdpPacket");
            if (myUdpPacketClass == NULL) {
                __android_log_print(ANDROID_LOG_ERROR, TAG, "Unable to find class MyUdpPacket");
                (cb->env)->ExceptionClear();
                return;
            }
            jmethodID myUdpPacketConstructor = (cb->env)->GetMethodID(
                    myUdpPacketClass, "<init>", "([BIIII)V");
            if (myUdpPacketConstructor == NULL) {
                __android_log_print(ANDROID_LOG_ERROR, TAG,
                                    "Unable to find method myUdpPacketConstructor");
                (cb->env)->ExceptionClear();
                return;
            }
            myTransportLayerPacket = (cb->env)->NewObject(myUdpPacketClass, myUdpPacketConstructor,
                                                          byteArray, udpSourcePort,
                                                          udpDestinationPort, udpLength,
                                                          udpChecksum);
            if (myTransportLayerPacket == NULL) {
                __android_log_print(ANDROID_LOG_ERROR, TAG,
                                    "Unable to instantiate class MyUdpPacket");
                (cb->env)->ExceptionClear();
                return;
            }
            (cb->env)->DeleteLocalRef(myUdpPacketClass);
            (cb->env)->DeleteLocalRef(byteArray);
        }
        else {
            const u_char *transportLayerPacket = ipPacket + sizeof(struct ip);
            dataLength = pkthdr->len -
                         (sizeof(struct ether_header) + sizeof(struct ip));
            jbyte transportData[dataLength];
            int i;
            for (i = 0; i < (dataLength); i++) {
                transportData[i] = *(transportLayerPacket + i);
            }
            jbyteArray byteArray = (cb->env)->NewByteArray(dataLength);
            if (byteArray == NULL) {
                __android_log_print(ANDROID_LOG_ERROR, TAG,
                                    "Unable to find create a new bytearray of size %d [3]",
                                    dataLength);
                (cb->env)->ExceptionClear();
                return;
            }
            (cb->env)->SetByteArrayRegion(byteArray, 0, dataLength, transportData);
            jboolean flag = (cb->env)->ExceptionCheck();
            if (flag) {
                __android_log_print(ANDROID_LOG_ERROR, TAG,
                                    "Exception occurred after SetByteArrayRegion [3]");
                (cb->env)->ExceptionClear();
                return;
            }

            jclass myTransportLayerPacketClass = (cb->env)->FindClass(
                    "it/uniroma2/giadd/aitm/models/MyTransportLayerPacket");
            if (myTransportLayerPacketClass == NULL) {
                __android_log_print(ANDROID_LOG_ERROR, TAG,
                                    "Unable to find class MyTransportLayerPacket");
                (cb->env)->ExceptionClear();
                return;
            }
            jmethodID myTransportLayerPacketConstructor = (cb->env)->GetMethodID(
                    myTransportLayerPacketClass, "<init>", "([B)V");
            if (myTransportLayerPacketConstructor == NULL) {
                __android_log_print(ANDROID_LOG_ERROR, TAG,
                                    "Unable to find method myTransportLayerPacketConstructor");
                (cb->env)->ExceptionClear();
                return;
            }
            myTransportLayerPacket = (cb->env)->NewObject(myTransportLayerPacketClass,
                                                          myTransportLayerPacketConstructor,
                                                          byteArray);
            if (myTransportLayerPacket == NULL) {
                __android_log_print(ANDROID_LOG_ERROR, TAG,
                                    "Unable to instantiate class MyTransportLayerPacket");
                (cb->env)->ExceptionClear();
                return;
            }
            (cb->env)->DeleteLocalRef(myTransportLayerPacketClass);
            (cb->env)->DeleteLocalRef(byteArray);

        }

        jstring ipSource = (cb->env)->NewStringUTF(sourceIp);
        jstring ipDestination = (cb->env)->NewStringUTF(destIp);
        jint ipHeaderLength = ipHeader->ip_hl;
        jint ipId = ipHeader->ip_id;
        jint ipLength = ipHeader->ip_len;
        jshort ipOffset = ipHeader->ip_off;
        jshort ipProtocol = ipHeader->ip_p;
        jint ipChecksum = ipHeader->ip_sum;
        jshort ipTos = ipHeader->ip_tos;
        jshort ipTtl = ipHeader->ip_ttl;
        jlong ipVersion = ipHeader->ip_v;

        jclass myIpPacketClass = (cb->env)->FindClass("it/uniroma2/giadd/aitm/models/MyIpPacket");
        if (myIpPacketClass == NULL) {
            __android_log_print(ANDROID_LOG_ERROR, TAG, "Unable to find class MyIpPacket");
            (cb->env)->ExceptionClear();
            return;
        }
        jmethodID myIpPacketConstructor = (cb->env)->GetMethodID(myIpPacketClass, "<init>",
                                                                 "(Ljava/lang/String;Ljava/lang/String;IIISSISSJLit/uniroma2/giadd/aitm/models/MyTransportLayerPacket;)V");
        if (myIpPacketConstructor == NULL) {
            __android_log_print(ANDROID_LOG_ERROR, TAG,
                                "Unable to find method myIpPacketConstructor");
            (cb->env)->ExceptionClear();
            return;
        }
        jobject myIpPacket = (cb->env)->NewObject(myIpPacketClass, myIpPacketConstructor,
                                                  ipSource, ipDestination, ipHeaderLength,
                                                  ipLength, ipId, ipOffset, ipProtocol,
                                                  ipChecksum, ipTos, ipTtl, ipVersion,
                                                  myTransportLayerPacket);
        if (myIpPacket == NULL) {
            __android_log_print(ANDROID_LOG_ERROR, TAG, "Unable to instantiate class MyIpPacket");
            (cb->env)->ExceptionClear();
            return;
        }


        (cb->env)->CallVoidMethod(cb->obj, cb->method, myIpPacket);
        (cb->env)->DeleteLocalRef(ipSource);
        (cb->env)->DeleteLocalRef(ipDestination);
        (cb->env)->DeleteLocalRef(myIpPacketClass);
        (cb->env)->DeleteLocalRef(myIpPacket);
        (cb->env)->DeleteLocalRef(myTransportLayerPacket);
    }
}
#ifdef __cplusplus
}
#endif
