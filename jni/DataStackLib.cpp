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

    void Java_com_stackrocker_DataStack_destroy(JNIEnv* env, jobject jobj, jlong dataStackPtr)
    {
        delete toDataStack(dataStackPtr);
    }

    jboolean Java_com_stackrocker_DataStack_intoCache(JNIEnv* env, jobject jobj, jlong dataStackPtr, jbyteArray array )
    {
		return toDataStack(dataStackPtr)->intoCache( array );
    }

    void Java_com_stackrocker_DataStack_threadRead( JNIEnv* env, jobject jobj, jlong dataStackPtr )
    {
    	toDataStack(dataStackPtr)->threadRead();
    }

}
