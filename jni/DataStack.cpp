#include "DataStack.h"

using namespace ComStack;

DataStack::DataStack( JNIEnv* env, jobject obj ) : jniEnv(env), jniObj(obj)
{
//	/* Klasse des aufrufenden Java Objektes ermitteln: */
//	jniClass = (*env)->GetObjectClass( env, obj );
}

boolean DataStack::intoCache( jbyteArray array )
{
	cache_size[RX] = jniEnv->GetArrayLength( array );
	if( cache_size[RX] >= CACHE_SIZE)
	{
		jniEnv->GetByteArrayRegion(array, 0, CACHE_SIZE,(jbyte *) &cache[RX]);
		return false;
	}else
	{
		jniEnv->GetByteArrayRegion(array, 0, cache_size[RX],(jbyte *) &cache[RX]);
		return true;
	}
}

byte DataStack::data_read()
{
	if(cache_size[RX] == 0 ){
		return cache[RX][0];
	}

	return cache[RX][cache_size[RX]--];
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
	return cache_size[TX] > 0 ;
}

void DataStack::rxMsgCallback( ComStack::RxMessage *rxMsg )
{
	byte *data;
	cache_size[RX] = rxMsg->getContentSize();
	jbyteArray msgArray = jniEnv->NewByteArray( cache_size[RX] );

	Iterator* it = rxMsg->getContent();

	while( ( data = it->next()) )
		cache[RX][ cache_size[RX]++ ] = *data;

	jniEnv->SetByteArrayRegion( msgArray, 0, cache_size[RX], (jbyte*)&cache[RX]);

	/* Adresse der Methode "callback" des aufrufenden Java Objektes ermitteln: */
	jmethodID aMethodId = jniEnv->GetMethodID(jniEnv->GetObjectClass(jniObj),"jniRxMsgCallback","([B)V");
	if(0==aMethodId)
		return;

	jniEnv->CallVoidMethod( jniObj, aMethodId, &msgArray);
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
