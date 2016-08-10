#include <jni.h>
#include <string>

extern "C"
jstring
Java_it_uniroma2_giadd_aitm_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
