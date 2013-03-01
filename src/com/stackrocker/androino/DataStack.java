package com.stackrocker.androino;

import android.util.Log;

public class DataStack {
	
    static {
        System.loadLibrary("datastack");
    }
    
    private final static String TAG = "DataStack";
    
    // These are the functions you defined in C++
    private native long create();
    private native void destroy(long dataStackPtr);
    
    public  native boolean intoCache( byte[] array );
    public  native void threadRead();
    
    private long dataStackPtr = 0;

    DataStack() {
    	dataStackPtr = create();
    }

    @Override
    protected void finalize() throws Throwable {

    	if (dataStackPtr != 0) 
            destroy(dataStackPtr);
        
        super.finalize();
    }
    
    public void jniRxMsgCallback( byte[] data )
    {
    	Log.i( TAG, "Yes, there is a new message" );
    }    
}
