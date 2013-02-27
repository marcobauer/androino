package com.stackrocker.androino;

import java.util.ArrayList;
import android.app.Activity;
import android.util.Log;
import android.webkit.WebView;

public class WebBridge{
	Activity 			activity;
    private WebView 	webView;
    
    static final String TAG = "WebBridge";
    
    private final static boolean DEBUG = true;
    private ArrayList<String> arrayMessage;
    public boolean  pageLoaded = false;
    
    /** Instantiate the interface and set the context */
    WebBridge(Activity activity, WebView webView) {
        this.activity = activity;
        this.webView = webView;             
        arrayMessage = new ArrayList<String>();
    }

    public void enable() {
    	pageLoaded = true;
    	
    	Log.i( TAG, "Now, WebBridge is enabled" );
    	    	    		
		for( int i= (arrayMessage.size()-1); i >= 0 ; i-- )
			log( arrayMessage.get(i) );
    	
    }
    
    public void log( String msg ) {
    	if( pageLoaded ) {
    		webView.loadUrl("javascript:( function() { addLog('" + msg + "'); })()");	
    	} else {
    		if(DEBUG) Log.d( TAG, "Page not ready, message will be cached :" + msg );
        	arrayMessage.add(msg);
    	}
    }
    
    public void detectUsb() {
    	
    }
    
}
