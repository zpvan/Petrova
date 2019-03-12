#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_knox_kismet_NativeWrapper_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++ Kismet";
    return env->NewStringUTF(hello.c_str());
}
