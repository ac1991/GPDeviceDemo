package com.example.devicedemo.ticket;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.util.Log;

import com.example.devicedemo.bean.TicketPrintBean;
import com.example.devicedemo.printermanager.USBESC58Command;
import com.example.devicedemo.printermanager.USBESC58TicketTypesetting;
import com.example.devicedemo.usb.OnTicketUsbStateListener;
import com.example.devicedemo.usb.OnUsbPermissionCallback;
import com.example.devicedemo.usb.USBController;
import com.example.devicedemo.usb.USB58TicketPrinter;
import com.example.devicedemo.utils.DeviceConnectType;
import com.example.devicedemo.utils.PrintStateMonitor;

import java.io.IOException;

/**
 * Created by sqwu on 2019/3/9
 * usb小票打印机包装类
 * 客户端通过此类可以进行打印机连接、关闭和打印的操作
 */
public class USB58TicketPrint extends AbstractTicketAction implements OnTicketUsbStateListener {
    UsbDevice usbDevice;
    USB58TicketPrinter usb58TicketPrinter;
    PrintStateMonitor mPrintStateMonitor;

    Context context;
    private boolean printStatus = false;

    public USB58TicketPrint(Context context){
        this.context = context;
    }

    @Override
    public void doPrint(TicketPrintBean bean) throws Exception {
        if(usb58TicketPrinter != null){
            usb58TicketPrinter.print(USBESC58TicketTypesetting.print(bean));
            notifyAction(true,"小票打印成功");
        }else{
            notifyAction(false,"小票打印机不存在");
        }
    }

    @Override
    public void doPrint(Bitmap bean, int width) throws Exception {
        USBESC58Command usbesc58Command = new USBESC58Command();
        usbesc58Command.addRastBitImage(bean);
        usb58TicketPrinter.print(usbesc58Command.getCommands());
    }

    @Override
    public void openCashBox() {
        if (usb58TicketPrinter != null){

        }else {

        }
    }

    @Override
    public boolean isCanTask() {
        if (usb58TicketPrinter != null){
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
                            usb58TicketPrinter = new USB58TicketPrinter(context, dev);
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
                usb58TicketPrinter = new USB58TicketPrinter(context, usbDevice);
                startMonitor();
            }
        } catch (IOException e) {
            e.printStackTrace();
            notifyOpen(false, "打印机连接失败");
        }
    }

    private void startMonitor(){
        mPrintStateMonitor = new PrintStateMonitor(usb58TicketPrinter);
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

        if (usb58TicketPrinter != null){
            usb58TicketPrinter.close();
        }

        usb58TicketPrinter = null;
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
