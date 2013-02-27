package com.stackrocker.androino;

import com.stackrocker.androino.R;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ArduinoService extends Service {

    private final static String TAG 	= "Arduino-Service";
    private final static boolean DEBUG 	= true;

    private boolean mIsRunning = false;
    
    final static String DATA_RECEIVED_INTENT 		= "tieto.arduino.intent.action.DATA_RECEIVED";
    final static String DATA_SEND_INTENT 			= "tieto.arduino.intent.action.SEND_DATA";
    final static String DATA_SENT_INTERNAL_INTENT 	= "tieto.arduino.internal.intent.action.DATA_SENT";
    final static String DATA_EXTRA 					= "tieto.arduino.intent.extra.DATA";

    UsbService usbService;
    
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        if (DEBUG) Log.d(TAG, "onCreate()");
        
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        
        filter.addAction(DATA_SEND_INTENT);        
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
//        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.i(TAG, "onDestroy()");
        super.onDestroy();
        
        unregisterReceiver(mReceiver);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) Log.d(TAG, "onStartCommand() " + intent + " " + flags + " " + startId);

        if ( mIsRunning ) {
            if (DEBUG) Log.i(TAG, "Service already running.");
            return Service.START_REDELIVER_INTENT;
        }

        mIsRunning = true;

        if (!intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
            if (DEBUG) Log.i(TAG, "Permission denied");
            Toast.makeText(getBaseContext(), getString(R.string.permission_denied), Toast.LENGTH_LONG).show();
            stopSelf();
            return Service.START_REDELIVER_INTENT;
        }

        if (DEBUG) Log.d(TAG, "Permission granted");
        
             
        usbService = new UsbService( this );
        
        if ( !usbService.setupUsbConnection( intent ) ) {
            if (DEBUG) Log.e(TAG, "Init of device failed!");
            stopSelf();
            return Service.START_REDELIVER_INTENT;
        }

        if (DEBUG) Log.i(TAG, "Receiving!");
        
        Toast.makeText(getBaseContext(), getString(R.string.receiving), Toast.LENGTH_SHORT).show();
        
        usbService.startRxThread();
        usbService.startTxThread();
        
        return Service.START_REDELIVER_INTENT;
    }
    
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            
            if (DEBUG) Log.d(TAG, "onReceive() : Action : " + action);

            /*!
             * ################################  DATA_SEND_INTENT  ################################
             */
            if (DATA_SEND_INTENT.equals(action)) {
                
                final byte[] dataToSend = intent.getByteArrayExtra(DATA_EXTRA);
                
                if (dataToSend == null) {
                    if (DEBUG) Log.i(TAG, "No " + DATA_EXTRA + " extra in intent!");
                
                    String text = String.format(getResources().getString(R.string.no_extra_in_intent), DATA_EXTRA);
                    Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                    return;
                }

                Handler handler = usbService.getTxHandler();
                handler.obtainMessage(10, dataToSend).sendToTarget();
            
            /*!
             * ################################  ACTION_USB_DEVICE_DETACHED  ################################
             */
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            	if (DEBUG) Log.i( TAG, "Action - " + UsbManager.ACTION_USB_DEVICE_DETACHED );
                
            	Toast.makeText(context, getString(R.string.device_detaches), Toast.LENGTH_LONG).show();
                Handler handler = usbService.getTxHandler();
                handler.sendEmptyMessage(11);
                
                stopSelf();
                
            /*!
             * ################################  ACTION_USB_DEVICE_ATTACHED  ################################
             */	
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            	
            }
        }
    };


}
