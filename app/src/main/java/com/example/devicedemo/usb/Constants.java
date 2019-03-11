package com.example.devicedemo.usb;

public interface Constants {

    interface BeanType{
        int ORDER = 1;
    }
    
    interface TicketPrintConst{
        int MM_TO_PX = 8; //1mm = 8px

        int SIZE_CHINESE = 24; //中文字体占用24像素 (默认字体大小为24)
        int SIZE_ENGLISH = 12; //英文字体占用12像素 (默认字体大小为24)

        int ALIGN_LEFT = 0;
        int ALIGN_CENTER = 1;
        int ALIGN_RIGHT = 2;
        float FONT_SIZE_BIG = 32.0f;
        float FONT_SIZE_MAX_BIG = 48.0f;
        float FONT_SIZE_NORMAL = 24.0f;
        float FONT_SIZE_SAMLL = 14.0f;
    }

    interface ConnectType{
        int USB = 1;
        int WIFI = 2;
    }

    interface DeviceType{
        String GP_LABEL = "01";
    }

}
