#include <jni.h>
#include <string>
#include <../libpcap/pcap.h>
#include <android/log.h>

#define DEBUG_TAG "native-lib.cpp"

int submit_log(const char *msgType, char *string) {
    __android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, msgType, string);
    //printf(msgType, string);
    return 0;
}

extern "C" jstring Java_it_uniroma2_giadd_aitm_MainActivity_stringFromJNI(JNIEnv *env, jobject) {
    std::string hello = "Hello from C++";
    char errbuf[PCAP_ERRBUF_SIZE];
    char *dev;

    dev = pcap_lookupdev(errbuf);
    if (dev == NULL) {
        submit_log("pcap_lookupdev => errbuf: [%s]\n", errbuf);
        return NULL;
    }

    submit_log("Device: [%s]\n", dev);
    return env->NewStringUTF(hello.c_str());
}
