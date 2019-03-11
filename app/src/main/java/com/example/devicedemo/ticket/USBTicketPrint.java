package com.example.devicedemo.ticket;

import android.content.Context;
import android.hardware.usb.UsbDevice;

import com.example.devicedemo.bean.TicketPrintBean;
import com.example.devicedemo.printermanager.USBESC58TicketTypesetting;
import com.example.devicedemo.usb.OnUsbPermissionCallback;
import com.example.devicedemo.usb.USBController;
import com.example.devicedemo.usb.USBTicketPrinter;
import com.example.devicedemo.utils.DeviceConnectType;

import java.io.IOException;

/**
 * Created by sqwu on 2019/3/9
 * usb小票打印机包装类
 * 客户端通过此类可以进行打印机连接、关闭和打印的操作
 */
public class USBTicketPrint extends AbstractTicketAction{
    UsbDevice usbDevice;
    USBTicketPrinter usbTicketPrinter;

    Context context;
    private boolean printStatus = false;

    public USBTicketPrint(Context context){
        this.context = context;
    }

    @Override
    public void doPrint(TicketPrintBean bean) throws Exception {
        if(usbTicketPrinter != null){
            usbTicketPrinter.print(USBESC58TicketTypesetting.print(bean));
            notifyAction(true,"标签打印成功");
        }else{
            notifyAction(false,"标签打印机不存在");
        }
    }

    @Override
    public void openCashBox() {
        if (usbTicketPrinter != null){

        }else {

        }
    }

    @Override
    public boolean isCanTask() {
        if (usbTicketPrinter != null){
            return true;
        }
        return false;
    }

    @Override
    public void open() {
        if (usbDevice == null){
           notifyOpen(false, "打印机连接失败，请设置打印机");

        }
        try {
            boolean suc = USBController.getInstance().requestPermission(usbDevice, new OnUsbPermissionCallback() {
                @Override
                public void onUsbPermissionEvent(UsbDevice dev, boolean granted) {
                    if (granted) {
                        try {
                            usbTicketPrinter = new USBTicketPrinter(context, dev);
                            notifyOpen(false, "打印机连接成功");
                        } catch (IOException e) {
                            e.printStackTrace();
                            notifyOpen(false, "打印机连接失败");
                        }
                    }
                }
            });
            if (suc) {
                usbTicketPrinter = new USBTicketPrinter(context, usbDevice);
            }
        } catch (IOException e) {
            e.printStackTrace();
            notifyOpen(false, "打印机连接失败");
        }
    }

    @Override
    public void close() {
        if (usbTicketPrinter != null){
            usbTicketPrinter.close();
        }

        usbTicketPrinter = null;
    }

    @Override
    public DeviceConnectType getDeviceConnectType() {
        return DeviceConnectType.USB;
    }

    public void setUsbDevice(UsbDevice u) {
        close();
        this.usbDevice = u;
    }
}
