package com.example.devicedemo.utils;

public interface OnActionCallback {

    void onOpenResult(boolean success, String message);

    void onActionResult(boolean success, String message);
}
