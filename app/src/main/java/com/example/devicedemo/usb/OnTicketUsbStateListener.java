package com.example.devicedemo.usb;

/**
 * Created by sqwu on 2019/3/11
 *
 * 标签打印机状态查询
 */
public interface OnTicketUsbStateListener {
    //打印机无纸
    void onPaperErr();
    //打印机盖打开
    void onCoverOpen();
    //其它错误
    void onOtherErr();
}
