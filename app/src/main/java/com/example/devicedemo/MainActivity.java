package com.example.devicedemo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.devicedemo.utils.DeviceConnFactoryManager;
import com.example.devicedemo.utils.Utils;
import com.tools.command.EscCommand;
import com.tools.command.LabelCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;
import static com.example.devicedemo.utils.DeviceConnFactoryManager.ACTION_QUERY_PRINTER_STATE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button connect;
    Button print;
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
        deviceNameTv = findViewById(R.id.deviceName);
        connect.setOnClickListener(this);
        print.setOnClickListener(this);

        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(ACTION_USB_DEVICE_DETACHED);
        filter.addAction(ACTION_QUERY_PRINTER_STATE);
        filter.addAction(DeviceConnFactoryManager.ACTION_CONN_STATE);
        filter.addAction(ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(receiver, filter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.connect:
                    getUsbDeviceList();
//                usbConn();
                break;
            case R.id.print:
                    print1();
                break;
        }
    }

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
                if (checkUsbDevicePidVid(device)) {
//                    mUsbDeviceArrayAdapter.add(devicename);
                    deviceId = device.getDeviceId();
                    Log.d("Print", "DeviceName:" +devicename + " deviceId:" + device.getDeviceId() + "  deviceVid" + device.getVendorId() + "  devicePid" + device.getProductId() + " 序列号:" + device.getSerialNumber());
                    this.deviceName = devicename;
                    deviceNameTv.setText("设备名称:" + this.deviceName);
                    usbConn();
//                    return;
                }
            }
        } else {
//            String noDevices = getResources().getText(R.string.none_usb_device)
//                    .toString();
//            Log.d(DEBUG_TAG, "noDevices " + noDevices);
//            mUsbDeviceArrayAdapter.add(noDevices);

            Utils.toast(this, "无打印机");
        }
    }

    /**
     * 检查usb的产品ID和供应商ID
     * @param dev
     * @return
     */
    boolean checkUsbDevicePidVid(UsbDevice dev) {
        int pid = dev.getProductId();
        int vid = dev.getVendorId();
        return ((vid == 34918 && pid == 256) || (vid == 1137 && pid == 85)
                || (vid == 6790 && pid == 30084)
                || (vid == 26728 && pid == 256) || (vid == 26728 && pid == 512)
                || (vid == 26728 && pid == 256) || (vid == 26728 && pid == 768)
                || (vid == 26728 && pid == 1024) || (vid == 26728 && pid == 1280)
                || (vid == 26728 && pid == 1536));
    }

    /**
     * usb连接
     */
    private void usbConn() {
        closeport();
        UsbDevice usbDevice = Utils.getUsbDeviceFromPidAndVid(MainActivity.this, pid, vid);//Utils.getUsbDeviceFromName(MainActivity.this, deviceName);
        if (usbDevice == null){
            Utils.toast(this, "无设备可连接");
            return;
        }
        //判断USB设备是否有权限
        if (usbManager.hasPermission(usbDevice)) {
            new DeviceConnFactoryManager.Build()
                    .setId(id)
                    .setConnMethod(DeviceConnFactoryManager.CONN_METHOD.USB)
                    .setUsbDevice(usbDevice)
                    .setContext(this)
                    .build();
            DeviceConnFactoryManager.getDeviceConnFactoryManagers().openPort();
        }

    }


    private void checkPermission() {
        for (String permission : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, permission)) {
                per.add(permission);
            }
        }
    }

    private void requestPermission() {
        if (per.size() > 0) {
            String[] p = new String[per.size()];
            ActivityCompat.requestPermissions(this, per.toArray(p), REQUEST_CODE);
        }
    }

    /**
     * 重新连接回收上次连接的对象，避免内存泄漏
     */
    private void closeport(){
        if(DeviceConnFactoryManager.getDeviceConnFactoryManagers()!=null&&DeviceConnFactoryManager.getDeviceConnFactoryManagers().mPort!=null) {
            DeviceConnFactoryManager.getDeviceConnFactoryManagers().reader.cancel();
            DeviceConnFactoryManager.getDeviceConnFactoryManagers().mPort.closePort();
            DeviceConnFactoryManager.getDeviceConnFactoryManagers().mPort=null;
        }
    }

    /**
     * 广播，管理连接状态
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                //Usb连接断开、蓝牙连接断开广播
                case ACTION_USB_DEVICE_DETACHED:
//                    mHandler.obtainMessage(CONN_STATE_DISCONN).sendToTarget();
                    break;
                case DeviceConnFactoryManager.ACTION_CONN_STATE:
                    int state = intent.getIntExtra(DeviceConnFactoryManager.STATE, -1);
                    int deviceId = intent.getIntExtra(DeviceConnFactoryManager.DEVICE_ID, -1);
                    switch (state) {
                        case DeviceConnFactoryManager.CONN_STATE_DISCONNECT:
                            if (id == deviceId) {
//                                tvConnState.setText(getString(R.string.str_conn_state_disconnect));
                            }

                            Utils.toast(MainActivity.this, "断开连接");
                            break;
                        case DeviceConnFactoryManager.CONN_STATE_CONNECTING:
//                            tvConnState.setText(getString(R.string.str_conn_state_connecting));
                            Utils.toast(MainActivity.this, "连接中");
                            break;
                        case DeviceConnFactoryManager.CONN_STATE_CONNECTED:
//                            tvConnState.setText(getString(R.string.str_conn_state_connected) + "\n" + getConnDeviceInfo());
//                            if(DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].connMethod== DeviceConnFactoryManager.CONN_METHOD.WIFI){
//                                wificonn=true;
//                                if(keepConn==null) {
//                                    keepConn = new KeepConn();
//                                    keepConn.start();
//                                }
//                            }

                            Utils.toast(MainActivity.this, "连接成功");
                            break;
                        case CONN_STATE_FAILED:
//                            Utils.toast(MainActivity.this, getString(R.string.str_conn_fail));
//                            //wificonn=false;
//                            tvConnState.setText(getString(R.string.str_conn_state_disconnect));
                            Utils.toast(MainActivity.this, "连接失败");
                            break;
                        default:
                            break;
                    }
                    break;
                case ACTION_QUERY_PRINTER_STATE:
//                    if (counts >=0) {
//                        if(continuityprint) {
//                            printcount++;
//                            Utils.toast(MainActivity.this, getString(R.string.str_continuityprinter) + " " + printcount);
//                        }
//                        if(counts!=0) {
//                            sendContinuityPrint();
//                        }else {
//                            continuityprint=false;
//                        }
//                    }
                    break;
                default:
                    break;
            }
        }
    };

    void print1(){
        TicketPrintBean bean = new TicketPrintBean();
        bean.setPrintCount(1);
        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_BIG, TicketAlign.CENTER, "吃鸡"));
        bean.addLineBean(new SimpleLineBean(TicketTextSize.FONT_SIZE_BIG,TicketAlign.CENTER,"--已在线支付--"));
        bean.addLineStr();

        bean.addLineBean(new ColumnLineBean(TicketTextSize.FONT_SIZE_NORMAL,new String[]{"订单号:"+ 10333001,"人数："+5+"人"},new TicketAlign[]{TicketAlign.LEFT,TicketAlign.RIGHT},new int[]{100,100}));
        bean.addLineBean(new SimpleLineBean(TicketAlign.LEFT,"商品总数："+ 23));
        bean.addLineBean(new SimpleLineBean(TicketAlign.LEFT,"下单时间："+ "2019-1-1 18:00"));
        bean.addLineBean(new SimpleLineBean(TicketAlign.LEFT,"备注："+ "吃鸡"));
        bean.addLineStr();

        GP58MMIIITicketManager.print(bean);
    }

    /**
     * 发送票据
     */
    void print() {
        EscCommand esc = new EscCommand();
        esc.addInitializePrinter();
        Vector<Byte> datas = esc.getCommand();
        esc.addPrintAndFeedLines((byte) 3);
        // 设置打印居中
//        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
//        // 设置为倍高倍宽
//        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);
//        // 打印文字
//        esc.addText("Sample\n");
//
//        esc.addPrintAndLineFeed();
//
//
//        /* 打印文字 */
//        // 取消倍高倍宽
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
////        // 设置打印左对齐
//        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
////        // 打印文字
////        esc.addText("Print text\n");
////        // 打印文字
////        esc.addText("Welcome to use SMARNET printer!\n");
////
////        //设置字体大小，1为正常，2为大
        esc.addSetCharcterSize(EscCommand.WIDTH_ZOOM.MUL_1, EscCommand.HEIGHT_ZOOM.MUL_1);
//        /* 打印繁体中文 需要打印机支持繁体字库 */
//        String message = "佳博智匯票據打印機\n";
//        esc.addText(message, "GB2312");
//        esc.addPrintAndLineFeed();

        /* 绝对位置 具体详细信息请查看GP58编程手册 */
//        esc.addSetPrintingAreaWidth((short) (384 /3));
//        esc.addSetAbsolutePrintPosition((short) 1);
//        esc.addText("智汇测试\n测试测试\n测试测试\n测试测试\n测试");


//        esc.addSetPrintingAreaWidth((short) (384 /3));
        //设置水平和垂直单位
        esc.addSetHorAndVerMotionUnits((byte) 8, (byte) 0);
//        esc.addSetAbsolutePrintPosition((short) 6);
//        // 设置打印居中
////        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
//        esc.addText("测试测试1\n测试测试1\n测试1");
//        esc.addHorTab();

//        esc.addSetPrintingAreaWidth((short) (384 /3));
//        esc.addSetAbsolutePrintPosition((short) 11);
//        esc.addText("设备");
//        esc.addPrintAndLineFeed();

        addTextRow(esc, new String[]{"一a二b三c四d五e六七八九十一二", "一二三四五六七八九十一二", "一二三四五六七八九十一二"}, null);

        /* 打印图片 */
        // 打印文字
        esc.addText("Print bitmap!\n");
//        Bitmap b = BitmapFactory.decodeResource(getResources(),
//                R.drawable.gprinter);
        // 打印图片
//        esc.addRastBitImage(b, 380, 0);

        /* 打印一维条码 */
        // 打印文字
//        esc.addText("Print code128\n");
//        esc.addSelectPrintingPositionForHRICharacters(EscCommand.HRI_POSITION.BELOW);
//        // 设置条码可识别字符位置在条码下方
//        // 设置条码高度为60点
//        esc.addSetBarcodeHeight((byte) 60);
//        // 设置条码单元宽度为1
//        esc.addSetBarcodeWidth((byte) 1);
//        // 打印Code128码
//        esc.addCODE128(esc.genCodeB("SMARNET"));
//        esc.addPrintAndLineFeed();
//
//        /*
//         * QRCode命令打印 此命令只在支持QRCode命令打印的机型才能使用。 在不支持二维码指令打印的机型上，则需要发送二维条码图片
//         */
//        // 打印文字
//        esc.addText("Print QRcode\n");
//        // 设置纠错等级
//        esc.addSelectErrorCorrectionLevelForQRCode((byte) 0x31);
//        // 设置qrcode模块大小
//        esc.addSelectSizeOfModuleForQRCode((byte) 3);
//        // 设置qrcode内容
//        esc.addStoreQRCodeData("www.smarnet.cc");
//        esc.addPrintQRCode();// 打印QRCode
//        esc.addPrintAndLineFeed();
//
//        // 设置打印左对齐
//        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
//        //打印文字
//        esc.addText("Completed!\r\n");
//
//        // 开钱箱
//        esc.addGeneratePlus(LabelCommand.FOOT.F5, (byte) 255, (byte) 255);
        esc.addPrintAndFeedLines((byte) 4);
        // 加入查询打印机状态，用于连续打印
        byte[] bytes={29,114,1};
        esc.addUserCommand(bytes);

        // 发送数据
        DeviceConnFactoryManager.getDeviceConnFactoryManagers().sendDataImmediately(datas);
    }

    /**
     * 获取字符长度
     * 中文字符长度为：2
     * 其它字符长度为：1
     * @param value
     * @return
     */
    public static int getStringLength(String value) {
        int valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 2;
            } else {
                valueLength += 1;
            }
        }
        return valueLength;
    }

    /**
     * 添加换行符
     * @return
     */
    public void addTextRow(EscCommand escCommand, String[] text, int[] weight) {
        int length = text.length;

        int marginSize = 2;

        //获取每列的宽度
        int columnWidth = maxSize / length - marginSize ;

        Log.i("每列宽度：", columnWidth + "");

        //获取文本的最大长度
        int maxTextLength = 0;
        for (int textIndex = 0; textIndex < length; textIndex++) {
            int tempLength = getStringLength(text[textIndex]);
            if (maxTextLength < tempLength) {
                maxTextLength = tempLength;
            }

            Log.i("获取最大长度", "maxTextLength:" + maxTextLength);
        }

        Log.i("分行", "分行:" + (maxTextLength / columnWidth + 1));
        for (int i = 0; i < maxTextLength / columnWidth + 1; i++) {//分行
            Log.i("第几行：", i + "");

            for (int index = 0; index < length; index++) {
                int textLength = getStringLength(text[index]);
                if (textLength > columnWidth * i) {
                    Log.i("第几列：", index + "");

                    //设置绝对打印位置
                    escCommand.addSetAbsolutePrintPosition((short) (index * (columnWidth + marginSize)* 12));
                    escCommand.addText(getSubString(text[index], i * columnWidth, (i + 1) * columnWidth));
                }
            }
            escCommand.addPrintAndLineFeed();
        }
    }

    /**
     * 截取字符串子集
     * 中文长度算：2， 非中文长度算：1
     * @param text
     * @param startIndex
     * @param endIndex
     * @return
     */
    public String getSubString(String text, int startIndex, int endIndex){
        Log.i("String_length(text):" + getStringLength(text), "startIndex:" + startIndex);

        if (getStringLength(text) > startIndex){

            String subString = "";
            int lastIndex = 0;
            for (int i = 0; i < text.length(); i++){
                String childString = text.substring(i,i+1);
                int charLength= getStringLength(childString);
                Log.i("lastIndex + charLength:" + (lastIndex + charLength), "lastIndex:" + lastIndex + "   charLength:" + charLength + "   startIndex:" + startIndex);

                Log.d("childString", childString);
                if (lastIndex + charLength > startIndex) {
                    Log.d("endIndex", endIndex + "");
                    if (lastIndex <= endIndex && lastIndex + charLength > endIndex) {
                        Log.d("subString", subString);
                        return subString;
                    }

                    subString += childString;
                }

                lastIndex += charLength;

            }

            return subString;
        }
        return "";
    }

}
