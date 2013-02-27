
#ifndef DATASTACK_H_
#define DATASTACK_H_

#include <jni.h>
#include "comstack/MessageHandler.h"

#define CACHE_SIZE	256

class DataStack : public ComStack::MessageHandler
{

public:
	DataStack( JNIEnv* env, jobject obj );

	boolean intoCache( byte );

private:
	byte 	data_read();
	size_t  data_write( byte data );
	size_t 	data_write( byte *, byte );
	boolean data_available();

	void response( ComStack::RxMessage * );
	void request( ComStack::RxMessage * );
	void event( ComStack::RxMessage * );
	void error( ComStack::Error::Type );
	void warn( ComStack::Warning::Type );

private:
	size_t index;
	byte cache[CACHE_SIZE];
	boolean	cache_full;

	JNIEnv* jniEnv;
	jobject jniObj;
	jclass 	jniClass;

};

#endif /* DATASTACK_H_ */
