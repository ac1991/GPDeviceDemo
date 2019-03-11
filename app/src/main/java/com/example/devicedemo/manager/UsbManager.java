package com.example.devicedemo.manager;

/**
 * Created by sqwu on 2019/3/8
 *
 * 富友连接逻辑，先断开其它所有的usb，将需要接入的设备连接进来，
 * 所以我们只需要监听最新的一个接入设备就好，如果有多个接入设备，则提示用户先断开其它所有设备，再进行接入。
 *
 *
 * usb 连接管理
 * 包括：
 * 1.usb状态监听：
 *      如果状态为断开，则获取断开的设备，释放连接；
 *      如果有新的设备接入，则判断是否是本地已有设备，如果是，则连接
 * 2.建立一个usb连接池，和usb的key做一个映射，根据key来进行打印
 * 3.监听打印机的状态
 *
 * 流程：
 *    1.获取USB连接数据集
 *    2.
 */
public class UsbManager {
}