
#ifndef DATASTACK_H_
#define DATASTACK_H_

#include <jni.h>
#include "comstack/MessageHandler.h"

#define CACHE_SIZE	100
#define TX	0
#define RX	1

class DataStack : public ComStack::MessageHandler
{

public:
	DataStack( JNIEnv* env, jobject obj );

	boolean intoCache( jbyteArray array );

private:
	byte 	data_read();
	size_t  data_write( byte data );
	size_t 	data_write( byte *, byte );
	boolean data_available();

	void rxMsgCallback( ComStack::RxMessage * );
	void error( ComStack::Error::Type );
	void warn( ComStack::Warning::Type );

private:
	jint cache_size[2];
	byte cache[2][CACHE_SIZE];

	JNIEnv* jniEnv;
	jobject jniObj;
	jclass 	jniClass;

};

#endif /* DATASTACK_H_ */
