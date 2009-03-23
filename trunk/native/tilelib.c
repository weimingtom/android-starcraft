#include <jni.h>
#include "tilelib.h"

#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT jint JNICALL Java_hotheart_JNITest_JNITestActivity_hello
  (JNIEnv *a, jclass b, jint c, jstring d)
{
  return c;
}

#ifdef __cplusplus
}
#endif


static JNINativeMethod sMethods[] = {
     /* name, signature, funcPtr */

    {"init_native", "([B[B[B)V", (void*)Java_hotheart_starcraft_map_TileLib_init_1native},
    {"draw_native", "(III[II)V", (void*)Java_hotheart_starcraft_map_TileLib_draw_1native},
};

 
  jint JNI_OnLoad(JavaVM* vm, void* reserved)
  {
    JNIEnv* env = NULL;
    jint result = -1;

    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        return result;
    }

    jniRegisterNativeMethods(env, "hotheart/starcraft/map/TileLib", sMethods, 2);
    return JNI_VERSION_1_4;
  }
