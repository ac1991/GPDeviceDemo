package com.example.devicedemo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.devicedemo.bean.ColumnLineBean;
import com.example.devicedemo.bean.SimpleLineBean;
import com.example.devicedemo.bean.TicketAlign;
import com.example.devicedemo.bean.TicketPrintBean;
import com.example.devicedemo.bean.TicketTextSize;
import com.example.devicedemo.manager.UsbConnect;
import com.example.devicedemo.printermanager.USBESC58TicketTypesetting;
import com.example.devicedemo.printermanager.GP58MMIIITicketManager;
import com.example.devicedemo.ticket.USBTicketPrint;
import com.example.devicedemo.usb.OnUsbPermissionCallback;
import com.example.devicedemo.usb.USBController;
import com.example.devicedemo.usb.USBTicketPrinter;
import com.example.devicedemo.utils.DeviceConnFactoryManager;
import com.example.devicedemo.utils.Utils;
import com.tools.command.EscCommand;

import net.posprinter.utils.DataForSendToPrinterPos58;
import net.posprinter.utils.DataForSendToPrinterPos80;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;
import static com.example.devicedemo.utils.DeviceConnFactoryManager.ACTION_QUERY_PRINTER_STATE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button connect, connect2;
    Button print, print2;
    TextView deviceNameTv;
    String deviceName = "";
    int deviceId;

    private int maxSize = 384/12;//最多允许字符宽度

    int pid = 512;
    int vid = 26728;
    ArrayList<String> per = new ArrayList<>();
    private static final int REQUEST_CODE = 0x004;
    private UsbManager usbManager;
    public static final int CONN_STATE_DISCONNECT = 0x90;
    public static final int CONN_STATE_CONNECTING = CONN_STATE_DISCONNECT << 1;
    public static final int CONN_STATE_FAILED = CONN_STATE_DISCONNECT << 2;
    public static final int CONN_STATE_CONNECTED = CONN_STATE_DISCONNECT << 3;

    public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";


    /**
     * 连接状态断开
     */
    private static final int CONN_STATE_DISCONN = 0x007;
    /**
     * 使用打印机指令错误
     */
    private static final int PRINTER_COMMAND_ERROR = 0x008;
    /**
     * 判断打印机所使用指令是否是ESC指令
     */
    private int id = 0;

    private String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connect = findViewById(R.id.connect);
        print = findViewById(R.id.print);
        connect2 = findViewById(R.id.connect2);
        print2 = findViewById(R.id.print2);
        deviceNameTv = findViewById(R.id.deviceName);
        connect.setOnClickListener(this);
        print.setOnClickListener(this);
        connect2.setOnClickListener(this);
        print2.setOnClickListener(this);

        USBController.getInstance().init(getApplicationContext());

        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        getUsbDeviceList();

//        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
//        filter.addAction(ACTION_USB_DEVICE_DETACHED);
//        filter.addAction(ACTION_QUERY_PRINTER_STATE);
//        filter.addAction(DeviceConnFactoryManager.ACTION_CONN_STATE);
//        filter.addAction(ACTION_USB_DEVICE_ATTACHED);
//        registerReceiver(receiver, filter);
//        usbConnect = new UsbConnect(getApplicationContext());
//        usbConnect2 = new UsbConnect(getApplicationContext());
    }

//    USBTicketPrinter USBTicketPrinter1;
    USBTicketPrint usbTicketPrint1;
    private void connect3(){
        usbTicketPrint1 = new USBTicketPrint(getApplicationContext());
        UsbDevice usbDevice = Utils.getUsbDeviceFromPidAndVid(getApplicationContext(), 1803, 1155);
        usbTicketPrint1.setUsbDevice(usbDevice);
        usbTicketPrint1.open();

//        try {
//            UsbDevice usbDevice = Utils.getUsbDeviceFromName(getApplicationContext(), "/dev/bus/usb/003/006");
//            boolean suc = USBController.getInstance().requestPermission(usbDevice, new OnUsbPermissionCallback() {
//                @Override
//                public void onUsbPermissionEvent(UsbDevice dev, boolean granted) {
//                    if (granted) {
//                        try {
//                            USBTicketPrinter1 = new USBTicketPrinter(getApplicationContext(), dev);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            });
//            if (suc) {
//                USBTicketPrinter1 = new USBTicketPrinter(getApplicationContext(), usbDevice);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

//    USBTicketPrinter USBTicketPrinter2;
    USBTicketPrint usbTicketPrint2;
    private void connect4(){
        usbTicketPrint2 = new USBTicketPrint(getApplicationContext());
        UsbDevice usbDevice = Utils.getUsbDeviceFromPidAndVid(getApplicationContext(), 512, 26728);
        usbTicketPrint2.setUsbDevice(usbDevice);
        usbTicketPrint2.open();

//        try {
//            UsbDevice usbDevice = Utils.getUsbDeviceFromName(getApplicationContext(), "/dev/bus/usb/003/007");
//            boolean suc = USBController.getInstance().requestPermission(usbDevice, new OnUsbPermissionCallback() {
//                @Override
//                public void onUsbPermissionEvent(UsbDevice dev, boolean granted) {
//                    if (granted) {
//                        try {
//                            USBTicketPrinter2 = new USBTicketPrinter(getApplicationContext(), dev);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            });
//            if (suc) {
//                USBTicketPrinter2 = new USBTicketPrinter(getApplicationContext(), usbDevice);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void requestUsbPermission(){
       ;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.connect:
//                    getUsbDeviceList();
//                usbConn();

                connect3();
                break;
            case R.id.print:
                printOrder();
                break;
            case R.id.connect2:
                connect4();
                break;
            case R.id.print2:
                printOrder2();
                break;
        }
    }
//    UsbConnect usbConnect2;
//    private void connect2(){
////        usbConnect2.connect("/dev/bus/usb/001/005");
//    }
//
//    UsbConnect usbConnect;
//    public void connectXP(String address){
//
////        usbConnect.connect("/dev/bus/usb/001/006");
//    }
//
    public void getUsbDeviceList() {
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        // 获取已经通过usb连接的设备
        HashMap<String, UsbDevice> devices = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = devices.values().iterator();
        int count = devices.size();
//        Log.d(DEBUG_TAG, "count " + count);
        if (count > 0) {
            while (deviceIterator.hasNext()) {
                UsbDevice device = deviceIterator.next();
                String devicename = device.getDeviceName();
                Log.d("打印机：", devicename + " pid:" + device.getProductId() +"  vid:" + device.getVendorId() + "  SerialNumber:" + device.getSerialNumber());
            }
        } else {
//            String noDevices = getResources().getText(R.string.none_usb_device)
//                    .toString();
//            Log.d(DEBUG_TAG, "noDevices " + noDevices);
//            mUsbDeviceArrayAdapter.add(noDevices);

            Utils.toast(this, "无打印机");
        }
    }
//
//    /**
//     * 检查usb的产品ID和供应商ID
//     * @param dev
//     * @return
//     */
//    boolean checkUsbDevicePidVid(UsbDevice dev) {
//        int pid = dev.getProductId();
//        int vid = dev.getVendorId();
//        return ((vid == 34918 && pid == 256) || (vid == 1137 && pid == 85)
//                || (vid == 6790 && pid == 30084)
//                || (vid == 26728 && pid == 256) || (vid == 26728 && pid == 512)
//                || (vid == 26728 && pid == 256) || (vid == 26728 && pid == 768)
//                || (vid == 26728 && pid == 1024) || (vid == 26728 && pid == 1280)
//                || (vid == 26728 && pid == 1536));
//    }
//
//    /**
//     * usb连接
//     */
//    private void usbConn() {
//        closeport();
//        UsbDevice usbDevice = Utils.getUsbDeviceFromPidAndVid(MainActivity.this, pid, vid);//Utils.getUsbDeviceFromName(MainActivity.this, deviceName);
//        if (usbDevice == null){
//            Utils.toast(this, "无设备可连接");
//            return;
//        }
//        //判断USB设备是否有权限
//        if (usbManager.hasPermission(usbDevice)) {
//            new DeviceConnFactoryManager.Build()
//                    .setId(id)
//                    .setConnMethod(DeviceConnFactoryManager.CONN_METHOD.USB)
//                    .setUsbDevice(usbDevice)
//                    .setContext(this)
//                    .build();
//            DeviceConnFactoryManager.getDeviceConnFactoryManagers().openPort();
//        }
//
//    }
//
//
//    private void checkPermission() {
//        for (String permission : permissions) {
//            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, permission)) {
//                per.add(permission);
//            }
//        }
//    }
//
//    private void requestPermission() {
//        if (per.size() > 0) {
//            String[] p = new String[per.size()];
//            ActivityCompat.requestPermissions(this, per.toArray(p), REQUEST_CODE);
//        }
//    }
//
//    /**
//     * 重新连接回收上次连接的对象，避免内存泄漏
//     */
//    private void closeport(){
//        if(DeviceConnFactoryManager.getDeviceConnFactoryManagers()!=null&&DeviceConnFactoryManager.getDeviceConnFactoryManagers().mPort!=null) {
//            DeviceConnFactoryManager.getDeviceConnFactoryManagers().reader.cancel();
//            DeviceConnFactoryManager.getDeviceConnFactoryManagers().mPort.closePort();
//            DeviceConnFactoryManager.getDeviceConnFactoryManagers().mPort=null;
//        }
//    }
//
//    /**
//     * 广播，管理连接状态
//     */
//    private BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            switch (action) {
//                //Usb连接断开、蓝牙连接断开广播
//                case ACTION_USB_DEVICE_DETACHED:
////                    mHandler.obtainMessage(CONN_STATE_DISCONN).sendToTarget();
//                    break;
//                case DeviceConnFactoryManager.ACTION_CONN_STATE:
//                    int state = intent.getIntExtra(DeviceConnFactoryManager.STATE, -1);
//                    int deviceId = intent.getIntExtra(DeviceConnFactoryManager.DEVICE_ID, -1);
//                    switch (state) {
//                        case DeviceConnFactoryManager.CONN_STATE_DISCONNECT:
//                            if (id == deviceId) {
////                                tvConnState.setText(getString(R.string.str_conn_state_disconnect));
//                            }
//
//                            Utils.toast(MainActivity.this, "断开连接");
//                            break;
//                        case DeviceConnFactoryManager.CONN_STATE_CONNECTING:
////                            tvConnState.setText(getString(R.string.str_conn_state_connecting));
//                            Utils.toast(MainActivity.this, "连接中");
//                            break;
//                        case DeviceConnFactoryManager.CONN_STATE_CONNECTED:
////                            tvConnState.setText(getString(R.string.str_conn_state_connected) + "\n" + getConnDeviceInfo());
////                            if(DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].connMethod== DeviceConnFactoryManager.CONN_METHOD.WIFI){
////                                wificonn=true;
////                                if(keepConn==null) {
////                                    keepConn = new KeepConn();
////                                    keepConn.start();
////                                }
////                            }
//
//                            Utils.toast(MainActivity.this, "连接成功");
//                            break;
//                        case CONN_STATE_FAILED:
////                            Utils.toast(MainActivity.this, getString(R.string.str_conn_fail));
////                            //wificonn=false;
////                            tvConnState.setText(getString(R.string.str_conn_state_disconnect));
//                            Utils.toast(MainActivity.this, "连接失败");
//                            break;
//                        default:
//                            break;
//                    }
//                    break;
//                case ACTION_QUERY_PRINTER_STATE:
////                    if (counts >=0) {
////                        if(continuityprint) {
////                            printcount++;
////                            Utils.toast(MainActivity.this, getString(R.string.str_continuityprinter) + " " + printcount);
////                        }
////                        if(counts!=0) {
////                            sendContinuityPrint();
////                        }else {
////                            continuityprint=false;
////                        }
////                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//    };
//
//    void print1(){
//        TicketPrintBean bean = new TicketPrintBean();
//        bean.setPrintCount(1);
//        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_BIG, TicketAlign.CENTER, "吃鸡"));
//        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_BIG,TicketAlign.CENTER,"--已在线支付--"));
//        bean.addLineStr();
//
//        bean.addLineBean(new ColumnLineBean(TicketTextSize.FONT_SIZE_NORMAL,new String[]{"订单号:"+ 103330011 +"1122334","人数："+5+"人"},null,new int[]{100, 100}));
//        bean.addLineBean(new SimpleLineBean(TicketAlign.LEFT,"商品总数："+ 23));
//        bean.addLineBean(new SimpleLineBean(TicketAlign.LEFT,"下单时间："+ "2019-1-1 18:00"));
//        bean.addLineBean(new SimpleLineBean(TicketAlign.LEFT,"备注："+ "吃鸡"));
//        bean.addLineStr();
//
//        GP58MMIIITicketManager.print(bean);
//    }
//    void print2(){
//        TicketPrintBean bean = new TicketPrintBean();
//        bean.setPrintCount(1);
//        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_BIG, TicketAlign.CENTER, "富友科技园"));
//        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_BIG,TicketAlign.CENTER,"--交接班打印--"));
//        bean.addLineStr();
//
//        bean.addLineBean(new SimpleLineBean(TicketAlign.LEFT,"收银员：wl"));
//        bean.addLineBean(new SimpleLineBean(TicketAlign.LEFT,"登入时间："+ "2019-1-1 18:00"));
//        bean.addLineBean(new SimpleLineBean(TicketAlign.LEFT,"登出时间："+ "2019-1-1 18:00"));
//        bean.addLineStr();
//
//        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_SAMLL,TicketAlign.CENTER," "));
//        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_NORMAL, TicketAlign.LEFT, "净销售单数：1"));
//        bean.addLineStr();
//        bean.addLineBean(new ColumnLineBean(new String[]{"现金",  "1"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
//        bean.addLineBean(new ColumnLineBean(new String[]{"会员卡",  "1"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
//        bean.addLineBean(new ColumnLineBean(new String[]{"银行卡",  "1"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
//        bean.addLineBean(new ColumnLineBean(new String[]{"微信",  "1"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
//        bean.addLineBean(new ColumnLineBean(new String[]{"支付宝",  "1"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
//        bean.addLineBean(new ColumnLineBean(new String[]{"其它支付",  "1"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
//
//        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_SAMLL,TicketAlign.CENTER," "));
//        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_NORMAL, TicketAlign.LEFT, "净销售金额：￥5"));
//        bean.addLineStr();
//        bean.addLineBean(new ColumnLineBean(new String[]{"现金",  "￥1"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
//        bean.addLineBean(new ColumnLineBean(new String[]{"会员卡",  "￥1"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
//        bean.addLineBean(new ColumnLineBean(new String[]{"银行卡",  "￥1"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
//        bean.addLineBean(new ColumnLineBean(new String[]{"微信",  "￥1"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
//        bean.addLineBean(new ColumnLineBean(new String[]{"支付宝",  "￥1"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
//        bean.addLineBean(new ColumnLineBean(new String[]{"其它支付",  "￥1"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
//
//        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_SAMLL,TicketAlign.CENTER," "));
//        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_NORMAL, TicketAlign.LEFT, "销售占比：100%"));
//        bean.addLineStr();
//        bean.addLineBean(new ColumnLineBean(new String[]{"现金",  "16.7%"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
//        bean.addLineBean(new ColumnLineBean(new String[]{"会员卡",  "16.7%"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
//        bean.addLineBean(new ColumnLineBean(new String[]{"银行卡",  "16.7%"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
//        bean.addLineBean(new ColumnLineBean(new String[]{"微信",  "16.7%"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
//        bean.addLineBean(new ColumnLineBean(new String[]{"支付宝",  "16.7%"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
//        bean.addLineBean(new ColumnLineBean(new String[]{"其它支付",  "16.7%"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
//
//        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_SAMLL,TicketAlign.CENTER," "));
//        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_NORMAL, TicketAlign.LEFT, "净销售单数：1"));
//        bean.addLineStr();
//        bean.addLineBean(new ColumnLineBean(new String[]{"现金",  "1"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
//        bean.addLineBean(new ColumnLineBean(new String[]{"会员卡",  "1"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
//        bean.addLineBean(new ColumnLineBean(new String[]{"银行卡",  "1"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
//        bean.addLineBean(new ColumnLineBean(new String[]{"微信",  "1"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
//        bean.addLineBean(new ColumnLineBean(new String[]{"支付宝",  "1"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
//        bean.addLineBean(new ColumnLineBean(new String[]{"其它支付",  "1"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
//
//        bean.addLineStr();
//        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_SAMLL,TicketAlign.CENTER," "));
//        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_BIG, TicketAlign.LEFT, "退款"));
//        bean.addLineStr();
//        bean.addLineBean(new ColumnLineBean(new String[]{"退款笔数",  "1"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
//        bean.addLineBean(new ColumnLineBean(new String[]{"退款金额", "￥1.00"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
//
//        GP58MMIIITicketManager.print(bean);
//    }
//
//    void printOrder(int i){
//        TicketPrintBean bean = new TicketPrintBean();
//        bean.setPrintCount(1);
//        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_BIG, TicketAlign.CENTER, "富友科技园"));
//        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_BIG,TicketAlign.CENTER,"--已在线支付--"));
//        bean.addLineStr();
//
//        bean.addLineBean(new ColumnLineBean(new String[]{"订单号:10000987",  "人数：0人"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
//        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_NORMAL,TicketAlign.LEFT,"商品总数：1"));
//        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_NORMAL,TicketAlign.LEFT,"下单时间：2019-03-20 18:00:32"));
//        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_NORMAL,TicketAlign.LEFT,"备注："));
//        bean.addLineStr();
//
//        bean.addLineBean(new ColumnLineBean(new String[]{"双双鲜奶茶/大杯",  "x1", "￥32.0000000000000"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.CENTER, TicketAlign.RIGHT},new int[]{150,100, 100}));
//        bean.addLineBean(new ColumnLineBean(new String[]{"已付",  "￥32"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
//        bean.addLineStr();
//
//        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_NORMAL,TicketAlign.CENTER,"谢谢惠顾，欢迎下次光临"));
//        GP58MMIIITicketManager.print(bean);
//    }
//
    void printOrder(){
        TicketPrintBean bean = new TicketPrintBean();
        bean.setPrintCount(1);
        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_BIG, TicketAlign.CENTER, "富友科技园"));
        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_BIG,TicketAlign.CENTER,"--已在线支付--"));
        bean.addLineStr();

        bean.addLineBean(new ColumnLineBean(new String[]{"订单号:10000987",  "人数：0人"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_NORMAL,TicketAlign.LEFT,"商品总数：1"));
        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_NORMAL,TicketAlign.LEFT,"下单时间：2019-03-20 18:00:32"));
        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_NORMAL,TicketAlign.LEFT,"备注："));
        bean.addLineStr();

        bean.addLineBean(new ColumnLineBean(new String[]{"双双鲜奶茶/大杯大杯大大杯",  "x1", "￥32.00"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.LEFT, TicketAlign.RIGHT},new int[]{200,100,100}));
        bean.addLineBean(new ColumnLineBean(new String[]{"已付",  "￥32"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
        bean.addLineStr();

        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_NORMAL,TicketAlign.CENTER,"谢谢惠顾，欢迎下次光临"));
//        return USBESC58TicketTypesetting.print(bean);
        bean.addEmptyLine();
        bean.addEmptyLine();
        try {
            usbTicketPrint1.doPrint(bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void printOrder2(){
        TicketPrintBean bean = new TicketPrintBean();
        bean.setPrintCount(1);
        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_BIG, TicketAlign.CENTER, "富友科技园2"));
        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_BIG,TicketAlign.CENTER,"--已在线支付--"));
        bean.addLineStr();

        bean.addLineBean(new ColumnLineBean(new String[]{"订单号:10000987",  "人数：0人"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_NORMAL,TicketAlign.LEFT,"商品总数：1"));
        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_NORMAL,TicketAlign.LEFT,"下单时间：2019-03-20 18:00:32"));
        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_NORMAL,TicketAlign.LEFT,"备注："));
        bean.addLineStr();

        bean.addLineBean(new ColumnLineBean(new String[]{"双双鲜奶茶/大杯大杯大大杯",  "x1", "￥32.00"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.LEFT, TicketAlign.RIGHT},new int[]{200,100,100}));
        bean.addLineBean(new ColumnLineBean(new String[]{"已付",  "￥32"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
        bean.addLineStr();

        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_NORMAL,TicketAlign.CENTER,"谢谢惠顾，欢迎下次光临"));
        bean.addEmptyLine();
        bean.addEmptyLine();
        try {
            usbTicketPrint2.doPrint(bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
