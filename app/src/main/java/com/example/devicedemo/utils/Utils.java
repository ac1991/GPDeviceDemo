package com.example.devicedemo.utils;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Administrator
 *
 * @author 猿史森林
 *         Date: 2017/11/30
 *         Class description:
 */
public class Utils {

    private static Toast toast;

    public static UsbDevice getUsbDeviceFromName(Context context, String usbName) {
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String,UsbDevice> usbDeviceList = usbManager.getDeviceList();
        return usbDeviceList.get(usbName);
    }

    public static UsbDevice getUsbDeviceFromDeviceId(Context context, int deviceId){
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String,UsbDevice> usbDeviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = usbDeviceList.values().iterator();
        int count = usbDeviceList.size();
        if (count > 0){
            while (deviceIterator.hasNext()){
                UsbDevice usbDevice = deviceIterator.next();
                if (usbDevice.getDeviceId() == deviceId){
                    return usbDevice;
                }
            }
        }

        return null;
    }

    public static UsbDevice getUsbDeviceFromPidAndVid(Context context, int pid, int vid){
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String,UsbDevice> usbDeviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = usbDeviceList.values().iterator();
        int count = usbDeviceList.size();
        if (count > 0){
            while (deviceIterator.hasNext()){
                UsbDevice usbDevice = deviceIterator.next();
                if (usbDevice.getProductId() == pid && usbDevice.getVendorId() == vid){
                    return usbDevice;
                }
            }
        }

        return null;
    }

    public static void toast(Context context, String message) {
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            toast.setText(message);
        }
        toast.show();
    }
}
