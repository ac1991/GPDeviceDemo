package com.example.devicedemo.usb;

import android.hardware.usb.UsbDevice;

public interface OnUsbPermissionCallback {
    public void onUsbPermissionEvent(UsbDevice dev, boolean granted);
}
