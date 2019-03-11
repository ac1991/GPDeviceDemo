package com.example.devicedemo.ticket;

import com.example.devicedemo.utils.DeviceConnectType;
import com.example.devicedemo.utils.OnActionCallback;

public interface BaseAction<T> {

    boolean isCanTask();
    void open();
    void close();
    void setConfig(T config);
    T getConfig();
    DeviceConnectType getDeviceConnectType();
    void setActionCallback(OnActionCallback callback);

}
