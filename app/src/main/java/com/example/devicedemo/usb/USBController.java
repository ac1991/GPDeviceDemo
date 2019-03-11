package com.example.devicedemo.usb;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class USBController {
    private static String ACTION_USB_PERMISSION = "com.posin.usbdevice.USB_PERMISSION";
    private static final String TAG = "UseController";
    private OnUsbListener onUsbListener;
    private Context context;
    private UsbManager mUsbManager;
    private OnUsbPermissionCallback onPermissionCallback = null;

    private static USBController controller;
    private USBController(){
        super();
    }

    public static USBController getInstance(){
        if(null == controller){
            synchronized (USBController.class){
                controller = new USBController();
            }
        }
        return controller;
    }

    public void init(Context context){
        this.context = context;
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        bindUSBReceiver();
    }

    public void setOnUsbListener(OnUsbListener onUsbListener) {
        this.onUsbListener = onUsbListener;
    }

    public void bindUSBReceiver(){
        try{
            IntentFilter filter = new IntentFilter();
            filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            context.registerReceiver(mUsbReceiver, filter);
        }catch (Exception e){
        }
    }

    public void unbindUSBReceiver(){
        try{
            context.unregisterReceiver(mUsbReceiver);
        }catch (Exception e){

        }
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            String deviceName = usbDevice.getDeviceName();
            Log.d(TAG,"--- 接收到广播， action: " + action + " USB_ID:"+usbDevice.getVendorId());
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                Log.d(TAG, "USB device is Attached: " + deviceName);
                if(onUsbListener != null){
                    onUsbListener.onUsbAttached(usbDevice);
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                Log.e(TAG, "USB device is Detached: " + deviceName);
                if(onUsbListener != null){
                    onUsbListener.onUsbDetached(usbDevice);
                }
            }
        }
    };

    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, intent.getAction());

            if(ACTION_USB_PERMISSION.equals(intent.getAction())) {
                UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    if(onPermissionCallback != null)
                        onPermissionCallback.onUsbPermissionEvent(device, true);
                } else {
                    if(onPermissionCallback != null)
                        onPermissionCallback.onUsbPermissionEvent(device, false);
                }
                context.unregisterReceiver(this);
            }
        }
    };

    public boolean requestPermission(UsbDevice usbDevice, OnUsbPermissionCallback callback) {
        if (!mUsbManager.hasPermission(usbDevice)) {
            IntentFilter ifilter = new IntentFilter(ACTION_USB_PERMISSION);
            context.registerReceiver(mReceiver, ifilter);

            PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent( ACTION_USB_PERMISSION), 0);
            onPermissionCallback = callback;
            mUsbManager.requestPermission(usbDevice, pi);
            return false;
        } else {
            return true;
        }
    }

    public List<UsbDevice> findAllUsbPrinter(String usbType) {
        Log.d(TAG, "find usb printer...");
        final List<UsbDevice> result = new ArrayList<UsbDevice>();
        if(usbType == null || usbType.length() < 1){
            result.addAll(mUsbManager.getDeviceList().values());
        }else{
            switch (usbType){
                case Constants.DeviceType.GP_LABEL:
                {
                    for (final UsbDevice usbDevice : mUsbManager.getDeviceList().values()) {
                        if (USBSupper.isGpLabel(usbDevice)) {
                            result.add(usbDevice);
                        }
                    }
                }
                break;
            }
        }
        return result;
    }
}
