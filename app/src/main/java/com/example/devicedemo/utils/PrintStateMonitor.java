package com.example.devicedemo.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.devicedemo.usb.OnTicketUsbStateListener;
import com.example.devicedemo.usb.USBTicketPrinter;

import java.io.IOException;

/**
 * Created by sqwu on 2019/3/11
 *
 * 打印机状态监听
 */
public class PrintStateMonitor {

    public static final byte FLAG = 0x10;
    private static final int READ_DATA = 10000;
    private static final String READ_DATA_CNT = "read_data_cnt";
    private static final String READ_BUFFER_ARRAY = "read_buffer_array";
    /**
     * ESC查询打印机实时状态 缺纸状态
     */
    private static final int ESC_STATE_PAPER_ERR = 0x20;

    /**
     * ESC指令查询打印机实时状态 打印机开盖状态
     */
    private static final int ESC_STATE_COVER_OPEN = 0x04;

    /**
     * ESC指令查询打印机实时状态 打印机报错状态
     */
    private static final int ESC_STATE_ERR_OCCURS = 0x40;

    private USBTicketPrinter usbTicketPrinter = null;
    private OnTicketUsbStateListener mOnTicketUsbStateListener = null;
    private PrinterReader mPrinterReader = null;

    public PrintStateMonitor(USBTicketPrinter usbTicketPrinter){
        this.usbTicketPrinter = usbTicketPrinter;

    }

    public void setOnTicketUsbStateListener(OnTicketUsbStateListener listener){
        mOnTicketUsbStateListener = listener;
    }

    public void startMonitor(){
        if (usbTicketPrinter == null){
            throw new NullPointerException("usbTicketPrinter is null");
        }
        mPrinterReader = new PrinterReader();
        mPrinterReader.start();
    }

    public void cancleMonitor(){
        if (mPrinterReader != null){
            mPrinterReader.cancel();
        }
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;

    }

    /**
     * 开启死循环监听打印机状态
     */
    public class PrinterReader extends Thread {
        private boolean isRun = false;

        private byte[] buffer = new byte[100];

        public PrinterReader() {
            isRun = true;
        }

        @Override
        public void run() {
            if(usbTicketPrinter == null){
                throw  new NullPointerException("usbTicketPrinter is null");
            }
            try {
                while (isRun) {
                    //读取打印机返回信息
                    int len = usbTicketPrinter.readData(buffer);
                    if (len > 0) {
                        Message message = Message.obtain();
                        message.what = READ_DATA;
                        Bundle bundle = new Bundle();
                        bundle.putInt(READ_DATA_CNT, len); //数据长度
                        bundle.putByteArray(READ_BUFFER_ARRAY, buffer); //数据
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    }
                }
            } catch (Exception e) {
                if (usbTicketPrinter != null) {
                    usbTicketPrinter.close();
                }
            }
        }

        public void cancel() {
            isRun = false;

        }
    }

    /**
     * 接收打印机状态
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case READ_DATA:
                    int cnt = msg.getData().getInt(READ_DATA_CNT); //数据长度 >0;
                    byte[] buffer = msg.getData().getByteArray(READ_BUFFER_ARRAY);  //数据
                    //这里只对查询状态返回值做处理，其它返回值可参考编程手册来解析
                    if (buffer == null) {
                        return;
                    }
                    int result = judgeResponseType(buffer[0]); //数据右移
                    String status = "打印机连接正常;";
                    if (result == 1) {//查询打印机实时状态
                        if ((buffer[0] & ESC_STATE_PAPER_ERR) > 0) {
                            status += "打印机缺纸";
                            if (mOnTicketUsbStateListener != null){
                                mOnTicketUsbStateListener.onPaperErr();
                            }
                        }
                        if ((buffer[0] & ESC_STATE_COVER_OPEN) > 0) {
                            status += "打印机开盖";
                            if (mOnTicketUsbStateListener != null){
                                mOnTicketUsbStateListener.onCoverOpen();
                            }
                        }
                        if ((buffer[0] & ESC_STATE_ERR_OCCURS) > 0) {
                            status += "打印机出错";
                            if (mOnTicketUsbStateListener != null){
                                mOnTicketUsbStateListener.onOtherErr();
                            }
                        }
                    }

                    Log.e("PrintStateMonitor", status);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 判断是实时状态（10 04 02）还是查询状态（1D 72 01）
     */
    private int judgeResponseType(byte r) {
        return (byte) ((r & FLAG) >> 4);
    }

}
