package com.example.devicedemo.usb;

import android.hardware.usb.UsbDevice;

/**
 * Created by springsky on 2019/1/2.
 */

public interface OnUsbListener {

    /***
     * 标签打印机插入
     */
    void onUsbAttached(UsbDevice device);

    /***
     * 标签打印机拔出
     */
    void onUsbDetached(UsbDevice device);
}
