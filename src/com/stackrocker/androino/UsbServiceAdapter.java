package com.stackrocker.androino;

import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

public abstract class UsbServiceAdapter {

	private Activity 	activity;
	
	private final static String TAG = "Service-Adapter";
    private static final int VENDOR_ID_ARDUINO 					= 0x2341;
    private static final int PRODUCT_ID_ARDUINO_UNO 			= 0x01;
    private static final int PRODUCT_ID_ARDUINO_UNO_R3 			= 0x43;
    private static final int PRODUCT_ID_ARDUINO_MEGA_2560 		= 0x10;
    private static final int PRODUCT_ID_ARDUINO_MEGA_2560_R3	= 0x42;
    
//    private Boolean mIsReceiving;
//    private ArrayList<ByteArray> mTransferedDataList = new ArrayList<ByteArray>();
//    private ArrayAdapter<ByteArray> mDataAdapter;
    
    public abstract void info(  String tag, String msg );
    public abstract void debug( String tag, String msg );
    public abstract void error( String tag, String msg );
    
    /** Instantiate the interface and set the context */
	UsbServiceAdapter( Activity activity ) {
        this.activity = activity;        
        
        registerService();
        findDevice();
    }
	
	public void registerService ()  {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ArduinoService.DATA_RECEIVED_INTENT);
        filter.addAction(ArduinoService.DATA_SENT_INTERNAL_INTENT);
        activity.registerReceiver( mReceiver, filter);
	}
	
	
	public void unregisterService ()  {
		activity.unregisterReceiver( mReceiver );	
	}
	
	public void findDevice() {
		UsbDevice usbDevice = null;
		UsbManager usbManager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);       
        HashMap<String, UsbDevice> usbDeviceList = usbManager.getDeviceList();
        
		Iterator<UsbDevice> deviceIterator = usbDeviceList.values().iterator();
		if (deviceIterator.hasNext()) {
			
		    UsbDevice tempUsbDevice = deviceIterator.next();

		    debug( TAG, "VendorId:     0x" 	+ Integer.toHexString( tempUsbDevice.getVendorId() ) );
		    debug( TAG, "ProductId:    0x" 	+ Integer.toHexString( tempUsbDevice.getProductId() ) );
		    debug( TAG, "DeviceName:     " 	+ tempUsbDevice.getDeviceName());
		    debug( TAG, "DeviceId:     0x" 	+ Integer.toHexString( tempUsbDevice.getDeviceId() ) );
		    debug( TAG, "DeviceClass:    " 	+ tempUsbDevice.getDeviceClass());
		    debug( TAG, "DeviceSubclass: " 	+ tempUsbDevice.getDeviceSubclass());
		    debug( TAG, "InterfaceCount: " 	+ tempUsbDevice.getInterfaceCount());
		    debug( TAG, "DeviceProtocol: " 	+ tempUsbDevice.getDeviceProtocol());
	         
			if (tempUsbDevice.getVendorId() == VENDOR_ID_ARDUINO) {
			
			    switch (tempUsbDevice.getProductId()) {
			        
			    	case PRODUCT_ID_ARDUINO_UNO:
			            Toast.makeText(activity.getBaseContext(), "Arduino Uno " + activity.getString(R.string.found), Toast.LENGTH_SHORT).show();
			            usbDevice = tempUsbDevice;
			            break;
			        
			        case PRODUCT_ID_ARDUINO_UNO_R3:
			            Toast.makeText(activity.getBaseContext(), "Arduino Uno R3 " + activity.getString(R.string.found), Toast.LENGTH_SHORT).show();
			            usbDevice = tempUsbDevice;
			            break;
			            
			        case PRODUCT_ID_ARDUINO_MEGA_2560:
			            Toast.makeText(activity.getBaseContext(), "Arduino Mega 2560 " + activity.getString(R.string.found), Toast.LENGTH_SHORT).show();
			            usbDevice = tempUsbDevice;
			            break;
			        
			        case PRODUCT_ID_ARDUINO_MEGA_2560_R3:
			            Toast.makeText(activity.getBaseContext(), "Arduino Mega 2560 R3 " + activity.getString(R.string.found), Toast.LENGTH_SHORT).show();
			            usbDevice = tempUsbDevice;
			            break;
			        
		            default:
		            	Toast.makeText(activity.getBaseContext(), activity.getString(R.string.no_device_found), Toast.LENGTH_LONG).show();
		            	break;
			    } // end of switch case
			} else {//end of condition ARDUINO_USB_VENDOR_ID	
				Toast.makeText(activity.getBaseContext(), activity.getString(R.string.no_device_found), Toast.LENGTH_LONG).show();
			}
			
		} // end of iterator
		
		if (usbDevice == null) {
            info(TAG, "No device found!");
            Toast.makeText( activity.getBaseContext(), activity.getString(R.string.no_device_found), Toast.LENGTH_LONG).show();
        } else {
            info(TAG, "Device found!");
            Intent startIntent = new Intent( activity.getApplicationContext(), ArduinoService.class);
            PendingIntent pendingIntent = PendingIntent.getService ( activity.getApplicationContext(), 0, startIntent, 0);
            usbManager.requestPermission( usbDevice, pendingIntent );
        }
		
	} // end of method findDevice
	
	BroadcastReceiver mReceiver = new BroadcastReceiver() {

//		private void handleTransferedData(Intent intent, boolean receiving) {
//			
//			if (mIsReceiving == null || mIsReceiving != receiving) {
//				mIsReceiving = receiving;
//				mTransferedDataList.add(new ByteArray());
//			}
//
//			final byte[] newTransferedData = intent.getByteArrayExtra(ArduinoService.DATA_EXTRA);
//
////			debug( TAG, " data: " + newTransferedData.length + " \"" + new String(newTransferedData) + "\"");
//
//			ByteArray transferedData = mTransferedDataList.get(mTransferedDataList.size() - 1);
//			transferedData.add(newTransferedData);
//			mTransferedDataList.set(mTransferedDataList.size() - 1, transferedData);
//			mDataAdapter.notifyDataSetChanged();
//		}

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			StringBuffer data = new StringBuffer();
			
			debug( TAG, " onReceive() - Action :" + action);
			
			final byte[] newTransferedData = intent.getByteArrayExtra(ArduinoService.DATA_EXTRA);
			
			for(int i=0; i< newTransferedData.length; i++)
				data.append( String.format(" %02x |", newTransferedData[i] ) ); 
			
			Log.d( TAG, " data: " + data.toString()  );			
			debug( TAG, " data: " + newTransferedData.length + " \"" + data.toString() + "\"");
			
			
//			if (ArduinoService.DATA_RECEIVED_INTENT.equals(action)) {
//				handleTransferedData(intent, true);
//			} else if (ArduinoService.DATA_SENT_INTERNAL_INTENT.equals(action)) {
//				handleTransferedData(intent, false);
//			}
		}

	};
	
	
}

//private void sendString(String toSend) {
//	  Intent i = new Intent("primavera.arduino.intent.action.SEND_DATA");
//	  i.putExtra("primavera.arduino.intent.extra.DATA", toSend.getBytes());
//	  sendBroadcast(i);
//	 }



