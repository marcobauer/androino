package com.stackrocker.androino;

public class DataStack {
	
    static {
        System.loadLibrary("datastack");
    }
    
    // These are the functions you defined in C++
    private native long create();
    private native void destroy(long dataStackPtr);
    
    public  native boolean intoCache( byte[] array );
    public  native void checkRxMessage();
    
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
    
    public void response( byte[] data )
    {

    }

    public void request( byte[] data )
    {

    }

    public void event( byte[] data )
    {

    }

    public void error( byte[] data )
    {
    	
    }
    
}
