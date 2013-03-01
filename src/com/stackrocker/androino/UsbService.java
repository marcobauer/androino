package com.stackrocker.androino;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class UsbService {
	
    private SenderThread txThread;
    private final static String TAG 	= "UsbService";
    private final static boolean DEBUG 	= true;
    private Service service; 
    
    private volatile UsbDevice usbDevice = null;
    private volatile UsbDeviceConnection usbConnection = null;
    private volatile UsbEndpoint mInUsbEndpoint = null;
    private volatile UsbEndpoint mOutUsbEndpoint = null;
    private byte[] inBuffer;
    
    UsbService( Service service ) {
    	super();
    	inBuffer 		= new byte[256];
    	this.service	= service;
    }

    @Override
    protected void finalize() throws Throwable {
    	
    	this.usbConnection.close();
    	usbDevice = null;
                
        super.finalize();
    }
    
    public Handler getTxHandler()
    {
    	return txThread.mHandler;
    }
    
    public void startRxThread() {
        new Thread("arduino_receiver") {
        	
            public void run() {
            	
                while( usbDevice != null ) {
                	
                    final int len = usbConnection.bulkTransfer( mInUsbEndpoint, inBuffer, inBuffer.length, 0);
                    
                    if (len > 0) {
                    	
                        if (DEBUG) Log.d(TAG, "bulkTransfer() in buffer size  : " + Integer.valueOf( len ).toString() );

//                        intoCache( inBuffer );
                        
//                        threadRead();
                        
                        
//                        byte[] buffer = new byte[len];
//                        
//                        
//                      System.arraycopy( inBuffer, 0, buffer, 0, len);
//                     
//                    	Intent intent = new Intent(ArduinoService.DATA_RECEIVED_INTENT);
//                		intent.putExtra(ArduinoService.DATA_EXTRA, buffer);
//                		service.sendBroadcast(intent);
                		
                    }
                }

                if (DEBUG) Log.d(TAG, "receiver thread stopped.");
            } // end of run (main)
            
        }.start();
    }
    
    public void startTxThread() {
    	txThread = new SenderThread("arduino_sender");
    	txThread.start();
    }
    
    private class SenderThread extends Thread {
        public Handler mHandler;

        public SenderThread(String string) {
            super(string);
        }

        public void run() {

            Looper.prepare();

            mHandler = new Handler() {
                public void handleMessage(Message msg) {
                	
                    if (DEBUG) Log.i(TAG, "handleMessage() " + msg.what);
                    if (msg.what == 10) {
                        final byte[] dataToSend = (byte[]) msg.obj;

                        if (DEBUG) Log.d(TAG, "calling bulkTransfer() out");
                        final int len = usbConnection.bulkTransfer(mOutUsbEndpoint, dataToSend, dataToSend.length, 0);
                        if (DEBUG) Log.d(TAG, len + " of " + dataToSend.length + " sent.");
                        
                        Intent sendIntent = new Intent(ArduinoService.DATA_SENT_INTERNAL_INTENT);
                        sendIntent.putExtra( ArduinoService.DATA_EXTRA, dataToSend);
                        service.sendBroadcast(sendIntent);
                        
                    } else if (msg.what == 11) {
                        Looper.myLooper().quit();
                    }
                }
            };

            Looper.loop();
            if (DEBUG) Log.i(TAG, "sender thread stopped");
        }
    }
    
    public boolean setupUsbConnection( Intent intent ) {

    	usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
    	
        if( usbDevice == null )
       	 if (DEBUG) Log.e(TAG, "Scheisse eeee");
 
    	
    	UsbManager usbManager = (UsbManager) service.getSystemService(Context.USB_SERVICE);
		this.usbConnection = usbManager.openDevice( usbDevice );

		if (this.usbConnection == null) {
			if (DEBUG)
				Log.e(TAG, "Opening USB device failed!");
			
			Toast.makeText( service, service.getString(R.string.opening_device_failed), Toast.LENGTH_LONG).show();
			return false;
		}

		UsbInterface usbInterface = usbDevice.getInterface(1);
		if (!this.usbConnection.claimInterface(usbInterface, true)) {
			if (DEBUG)
				Log.e(TAG, "Claiming interface failed!");
			
			Toast.makeText( service, service.getString(R.string.claimning_interface_failed), Toast.LENGTH_LONG).show();
			this.usbConnection.close();
			return false;
		}

		// Arduino USB serial converter setup
		// Set control line state
		this.usbConnection.controlTransfer(0x21, 0x22, 0, 0, null, 0, 0);
		// Set line encoding.
		this.usbConnection.controlTransfer(0x21, 0x20, 0, 0, getLineEncoding(9600), 7, 0);

		for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
			if (usbInterface.getEndpoint(i).getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
				if (usbInterface.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_IN) {
					mInUsbEndpoint = usbInterface.getEndpoint(i);
				} else if (usbInterface.getEndpoint(i).getDirection() == UsbConstants.USB_DIR_OUT) {
					mOutUsbEndpoint = usbInterface.getEndpoint(i);
				}
			}
		}

		if (mInUsbEndpoint == null) {
			if (DEBUG)
				Log.e(TAG, "No in endpoint found!");

			Toast.makeText( service, service.getString(R.string.no_in_endpoint_found), Toast.LENGTH_LONG).show();
			this.usbConnection.close();
			return false;
		}

		if (mOutUsbEndpoint == null) {
			if (DEBUG)
				Log.e(TAG, "No out endpoint found!");
			Toast.makeText( service, service.getString(R.string.no_out_endpoint_found), Toast.LENGTH_LONG).show();
			this.usbConnection.close();
			return false;
		}

		return true;
	}
    
    private byte[] getLineEncoding(int baudRate) {
        final byte[] lineEncodingRequest = { (byte) 0x80, 0x25, 0x00, 0x00, 0x00, 0x00, 0x08 };
        switch (baudRate) {
        
	        case 14400:
	            lineEncodingRequest[0] = 0x40;
	            lineEncodingRequest[1] = 0x38;
	            break;
	
	        case 19200:
	            lineEncodingRequest[0] = 0x00;
	            lineEncodingRequest[1] = 0x4B;
	            break;
        }

        return lineEncodingRequest;
    }
    
}
