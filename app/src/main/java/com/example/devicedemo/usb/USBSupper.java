package com.example.devicedemo.usb;

import android.hardware.usb.UsbDevice;

public class USBSupper {

    /***
     * 佳博标签打印机
     * @param usbDevice
     * @return
     */
    public static boolean isGpLabel(final UsbDevice usbDevice) {
        final int vid = usbDevice.getVendorId();
        final int pid = usbDevice.getProductId();

        return (vid==34918) ||
                (vid==1137) ||
                (vid==1659) ||
                (vid==1137) ||
                (vid==1155) ||
                (vid==26728) ||
                (vid==17224) ||
                (vid==7358);
    }
}
