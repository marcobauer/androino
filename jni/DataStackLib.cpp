#include "DataStack.h"
#include <jni.h>

namespace {
DataStack* toDataStack(jlong dataStackPtr) {
        return reinterpret_cast<DataStack*>(dataStackPtr);
    }
}

extern "C" {

    jlong Java_com_stackrocker_DataStack_create(JNIEnv* env, jobject jobj) {
    	DataStack* dstack = new DataStack( env, jobj );
        return reinterpret_cast<jlong>(dstack);
    }

    void Java_com_stackrocker_DataStack_destroy(JNIEnv* env, jobject jobj, jlong dataStackPtr) {
        delete toDataStack(dataStackPtr);
    }

    jboolean Java_com_stackrocker_DataStack_intoCache(JNIEnv* env, jobject jobj, jlong dataStackPtr, jbyteArray array )
    {
//    	jbyte* buffer = (*env)->GetByteArrayElements(env, array, NULL);
//    	jsize  length = (*env)->GetArrayLength(env, array);
//
//    	for(int i=0; i< length; i++ )
//    	{
//    		if(toDataStack(dataStackPtr)->intoCache( buffer[i] ) == false )
//    		{
//    			(*env)->ReleaseByteArrayElements(env, array, buffer, 0);
//    			return false;
//    		}
//    	}
//		(*env)->ReleaseByteArrayElements(env, array, buffer, 0);
		return true;
    }

//    void Java_com_stackrocker_DataStack_update(JNIEnv* env, jobject jobj, jlong gamePtr, jboolean isTouching, jfloat touchX, jfloat touchY) {
////        toGame(gamePtr)->update(isTouching, touchX, touchY);
//    }
//
//    void Java_com_stackrocker_DataStack_render(JNIEnv* env, jobject jobj, jlong gamePtr) {
////        toGame(gamePtr)->render();
//    }


}
