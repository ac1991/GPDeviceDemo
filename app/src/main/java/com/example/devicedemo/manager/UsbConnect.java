package com.example.devicedemo.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import net.posprinter.posprinterface.IMyBinder;
import net.posprinter.posprinterface.ProcessData;
import net.posprinter.posprinterface.UiExecute;
import net.posprinter.service.PosprinterService;
import net.posprinter.utils.DataForSendToPrinterPos58;
import net.posprinter.utils.DataForSendToPrinterPos80;
import net.posprinter.utils.PosPrinterDev;

import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by sqwu on 2019/3/8
 * usb连接管理，对单一的usbDevice进行管理
 * 提供，连接、断开的功能
 */
public class UsbConnect {
    private boolean ISCONNECT;

    private IMyBinder binder;
    //bindService的参数connection
    private ServiceConnection conn= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //绑定成功
            binder= (IMyBinder) iBinder;
            Log.e("binder","connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("disbinder","disconnected");
        }
    };

    private Context appContext;

    public UsbConnect(Context context){
        this.appContext = context;
        //绑定service，获取ImyBinder对象
        Intent intent=new Intent(appContext, PosprinterService.class);
        context.bindService(intent, conn, BIND_AUTO_CREATE);
    }

    public void connect(String usbAddress){
        if (ISCONNECT){
            disconnect();
        }
        binder.connectUsbPort(appContext, usbAddress, new UiExecute() {
            @Override
            public void onsucess() {
                //连接成功后在UI线程中的执行
                ISCONNECT=true;
                log("成功连接");
            }

            @Override
            public void onfailed() {
                ISCONNECT=false;
                log("连接失败");
            }
        });
    }

    public void print(final List<byte[]> list){
        binder.writeDataByUSB(new UiExecute() {
            @Override
            public void onsucess() {
                log("打印成功");
            }

            @Override
            public void onfailed() {
                log("打印失败");
            }
        }, new ProcessData() {
            @Override
            public List<byte[]> processDataBeforeSend() {
                //追加一个打印换行指令，因为，pos打印机满一行才打印，不足一行，不打印
                list.add(DataForSendToPrinterPos58.printAndFeedLine());
                list.add(DataForSendToPrinterPos58.printAndFeedLine());
                list.add(DataForSendToPrinterPos58.printAndFeedLine());
                //打印并切纸
                list.add(DataForSendToPrinterPos80.selectCutPagerModerAndCutPager(66,1));

                return list;
            }
        });
    }

    private void disconnect(){
        binder.disconnectCurrentPort(new UiExecute() {
            @Override
            public void onsucess() {
                ISCONNECT=false;
                log("成功断开连接");
            }

            @Override
            public void onfailed() {
                log("断开连接失败");
            }
        });
    }

    public void close(){
        disconnect();
        appContext.unbindService(conn);
    }

    public boolean isConnect(){
        return ISCONNECT;
    }

    private void log(String msg){
        Log.d("UsbConnect", msg );
    }

}
