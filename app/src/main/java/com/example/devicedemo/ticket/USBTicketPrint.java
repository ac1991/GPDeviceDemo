package com.example.devicedemo.ticket;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.util.Log;

import com.example.devicedemo.bean.TicketPrintBean;
import com.example.devicedemo.printermanager.USBESC58TicketQRCodeCommand;
import com.example.devicedemo.printermanager.USBESC58TicketTypesetting;
import com.example.devicedemo.usb.OnTicketUsbStateListener;
import com.example.devicedemo.usb.OnUsbPermissionCallback;
import com.example.devicedemo.usb.USBController;
import com.example.devicedemo.usb.USBTicketPrinter;
import com.example.devicedemo.utils.DeviceConnectType;
import com.example.devicedemo.utils.PrintStateMonitor;

import java.io.IOException;

/**
 * Created by sqwu on 2019/3/9
 * usb小票打印机包装类
 * 客户端通过此类可以进行打印机连接、关闭和打印的操作
 */
public class USBTicketPrint extends AbstractTicketAction implements OnTicketUsbStateListener {
    UsbDevice usbDevice;
    USBTicketPrinter usbTicketPrinter;
    PrintStateMonitor mPrintStateMonitor;

    Context context;
    private boolean printStatus = false;

    public USBTicketPrint(Context context){
        this.context = context;
    }

    @Override
    public void doPrint(TicketPrintBean bean) throws Exception {
        if(usbTicketPrinter != null){
            usbTicketPrinter.print(USBESC58TicketTypesetting.print(bean));
            notifyAction(true,"小票打印成功");
        }else{
            notifyAction(false,"小票打印机不存在");
        }
    }

    @Override
    public void doPrintQRCode(String qrcode) throws Exception {
        if(usbTicketPrinter != null){
            usbTicketPrinter.print(USBESC58TicketQRCodeCommand.printQRCode(qrcode));
            notifyAction(true,"小票打印成功");
        }else{
            notifyAction(false,"小票打印机不存在");
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
                            startMonitor();
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
                startMonitor();
            }
        } catch (IOException e) {
            e.printStackTrace();
            notifyOpen(false, "打印机连接失败");
        }
    }

    private void startMonitor(){
        mPrintStateMonitor = new PrintStateMonitor(usbTicketPrinter);
        mPrintStateMonitor.setOnTicketUsbStateListener(this);
        mPrintStateMonitor.startMonitor();
    }

    private void stopMonitor(){
        if (mPrintStateMonitor != null){
            mPrintStateMonitor.cancleMonitor();
        }
    }

    @Override
    public void close() {
        stopMonitor();

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

    @Override
    public void onPaperErr() {
        Log.e("onPaperErr", "打印机缺纸");
    }

    @Override
    public void onCoverOpen() {
        Log.e("onCoverOpen", "打印机纸盒已打开");
    }

    @Override
    public void onOtherErr() {
        Log.e("onOtherErr", "打印机其它错误");
    }
}
