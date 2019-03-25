package com.example.devicedemo.xprint;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.os.IBinder;

import com.example.devicedemo.bean.TicketPrintBean;
import com.example.devicedemo.ticket.AbstractTicketAction;
import com.example.devicedemo.usb.OnTicketUsbStateListener;
import com.example.devicedemo.usb.USB58TicketPrinter;
import com.example.devicedemo.utils.DeviceConnectType;
import com.example.devicedemo.utils.PrintStateMonitor;

import net.posprinter.posprinterface.IMyBinder;
import net.posprinter.posprinterface.ProcessData;
import net.posprinter.posprinterface.UiExecute;
import net.posprinter.service.PosprinterService;

import java.io.IOException;
import java.util.List;

/**
 * Created by sqwu on 2019/3/20
 */
public class XPUSBTicketPrint extends AbstractTicketAction implements OnTicketUsbStateListener {
    private IMyBinder binder;
    UsbDevice usbDevice;
    USB58TicketPrinter usb58TicketPrinter;
    PrintStateMonitor mPrintStateMonitor;

    Context context;
    private boolean printStatus = false;
    private boolean isConnected = false;

    public XPUSBTicketPrint(Context context){
        this.context = context;
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (IMyBinder) service;
            binder.connectUsbPort(context, usbDevice.getDeviceName(), new UiExecute() {
                @Override
                public void onsucess() {
                    isConnected = true;
                    try {
                        usb58TicketPrinter = new USB58TicketPrinter(context, usbDevice);
                    } catch (IOException e) {
                        e.printStackTrace();
                        usb58TicketPrinter = null;
                    }
                    notifyOpen(false, "打印机连接成功");
                }

                @Override
                public void onfailed() {
                    isConnected = false;
                    notifyOpen(false, "打印机连接失败");
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            notifyOpen(false, "打印机连接失败");
        }
    };

    @Override
    public void doPrint(TicketPrintBean bean) throws Exception {
        binder.writeDataByUSB(new UiExecute() {
            @Override
            public void onsucess() {

            }

            @Override
            public void onfailed() {

            }
        }, new ProcessData() {
            @Override
            public List<byte[]> processDataBeforeSend() {
                return null;
            }
        });
    }

    @Override
    public void doPrint(Bitmap bean, int width) throws Exception {

    }

    @Override
    public void openCashBox() {

    }

    @Override
    public boolean isCanTask() {
        return false;
    }

    @Override
    public void open() {
        Intent intent = new Intent(context, PosprinterService.class);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void close() {
        if (binder != null && isConnected){
            binder.disconnectCurrentPort(new UiExecute() {
                @Override
                public void onsucess() {
                    isConnected = false;
                }

                @Override
                public void onfailed() {
                    isConnected = true;
                }
            });
        }

        context.unbindService(connection);
    }

    @Override
    public DeviceConnectType getDeviceConnectType() {
        return DeviceConnectType.USB;
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
    public void onPaperErr() {

    }

    @Override
    public void onCoverOpen() {

    }

    @Override
    public void onOtherErr() {

    }
}
