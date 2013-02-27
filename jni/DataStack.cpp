#include "DataStack.h"

using namespace ComStack;

DataStack::DataStack( JNIEnv* env, jobject obj ) : index(0), cache_full(false), jniEnv(env), jniObj(obj)
{
//	/* Klasse des aufrufenden Java Objektes ermitteln: */
//	jniClass = (*env)->GetObjectClass( env, obj );
}

boolean DataStack::intoCache( byte data )
{
	cache[index] = data;
	index = (++index%CACHE_SIZE);
	cache_full= true;
	if( index == 0 )
		return false;
	else
		return true;
}

byte DataStack::data_read()
{
	if(index == 0 ){
		cache_full = false;
		return cache[0];
	}

	return cache[index--];
}

size_t DataStack::data_write( byte data )
{
	return 0; //Serial.write( data );
}

size_t DataStack::data_write( byte *data, byte size  )
{
	return 0; //Serial.write( data, size );
}

boolean DataStack::data_available()
{
	return cache_full;
}

void DataStack::response( ComStack::RxMessage *data )
{
}

void DataStack::request( ComStack::RxMessage *data )
{
//   jbyteArray rawAudioCopy = env->NewByteArray( data->getContentSize() );
//   jbyte toCopy[10];
//   printf("Filling audio array copy\n");
//   char theBytes[10] = {0,1,2,3,4,5,6,7,8,9};
//   for (int i = 0; i < sizeof(theBytes); i++) {
//	   toCopy[i] = theBytes[i];
//   }
//
//
//
//
//   /* Adresse der Methode "callback" des aufrufenden Java Objektes ermitteln: */
//   jmethodID jmid = (*jniEnv)->GetMethodID( jniClass, "request", "([B)V");
//
//   if (jmid == 0)
//	  return;
//
//   /* Methode "callback" des aufrufenden Java Objektes aufrufen: */
//   (*jniEnv)->CallVoidMethod(jniEnv, jniObj, jmid, in_number);
//
//
//
//   env->SetByteArrayRegion(rawAudioCopy,0,10,toCopy);
//
//
//
//   env->CallVoidMethod(obj, aMethodId, rawAudioCopy);


//
//	env->SetByteArrayRegion(rawAudioCopy,0,10,toCopy);
//	printf("Finding object callback\n");
//	jmethodID aMethodId = env->GetMethodID(env->GetObjectClass(obj),"handleAudio","([B)V");
//	if(0==aMethodId) throw MyRuntimeException("Method not found error",99);
//	printf("Invoking the callback\n");
//	env->CallVoidMethod(obj,aMethodId, &rawAudioCopy);
//
//	/* Adresse der Methode "callback" des aufrufenden Java Objektes ermitteln: */
//	jmethodID jmid = (*jniEnv)->GetMethodID(jniEnv, "request", "(I)V");
}

void DataStack::event( ComStack::RxMessage *data )
{
}

void DataStack::error( ComStack::Error::Type error )
{
	switch( error )
	{
		case ComStack::Error::buffer_rx_full:
		break;

		case ComStack::Error::buffer_tx_full:
		break;

		case ComStack::Error::msg_crc_error:
		break;

		default:
			// unknown error;
			break;
	}
}

void DataStack::warn( ComStack::Warning::Type warning )
{
	switch( warning )
	{
		case ComStack::Warning::buffer_tx_overload:
		break;

		case ComStack::Warning::buffer_rx_overload:
		break;

		case ComStack::Warning::buffer_rx_cleanup:
		break;

		default:
			break;
	}
}
