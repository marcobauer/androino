package com.stackrocker.androino;

import com.stackrocker.androino.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class ArduinoActivity extends Activity {

//	private final static String TAG = "Arduino-Activity";
	private WebView 	webView;
	private WebBridge 	webBridge;
	
	private UsbServiceAdapter serviceAdapter; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ------------------------------------------------- Hide title and notification bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// Setup fullscreen layout of page
		setContentView(R.layout.main);

		// ------------------------------------------------- Setup WebView and WebBridge
		webView = (WebView) findViewById(R.id.webview);		
		webBridge = new WebBridge( this, webView );
		
		webView.setWebViewClient( new WebViewClient() {
			 public void onPageFinished(WebView view, String url) {
				 webBridge.enable();
			 } 
		}); 
		
		webView.getSettings().setJavaScriptEnabled(true);
		webView.addJavascriptInterface( webBridge, "webBridge");
		webView.loadUrl("file:///android_asset/index.html");
		
		// ------------------------------------------------- Setup Usb Service Adapter 
		serviceAdapter = new UsbServiceAdapter( this ) {
			
			// Implement abstract method
			public void debug( String tag, String msg) {
				webBridge.log("DEBUG : " + tag + " : " + msg );
			}

			// Implement abstract method
			public void info( String tag, String msg) {
				webBridge.log("INFO  : " + tag + " : " + msg );
			}
			
			// Implement abstract method
			public void error( String tag, String msg) {
				webBridge.log("ERROR : " + tag + " : " + msg );
			}
		
		};
		
	}
	
    @Override
    protected void onDestroy() {
    	serviceAdapter.unregisterService();
        super.onDestroy();  
    }
    
}
