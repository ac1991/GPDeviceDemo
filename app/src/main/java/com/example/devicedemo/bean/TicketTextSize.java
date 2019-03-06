package com.example.devicedemo.bean;

import com.tools.command.EscCommand;

public enum  TicketTextSize {

    FONT_SIZE_BIG(32.0f),
    FONT_SIZE_MAX_BIG(48.0f),
    FONT_SIZE_NORMAL(24.0f),
    FONT_SIZE_SAMLL(14.0f);

    float val = 0;
    TicketTextSize(float val){
        this.val = val;
    }

    public float getVal() {
        return val;
    }

    /**
     * 获取佳博打印机宽高
     * 默认12*24
     * @return
     */
    public byte[] getGP58MMIIITextSize(){
        byte temp = 0;

        if (val == FONT_SIZE_BIG.getVal() || val == FONT_SIZE_MAX_BIG.getVal()){
            // 设置为倍高倍宽
            temp = (byte)(temp | 16);//倍高
            temp = (byte)(temp | 32);//倍宽

        }
        byte[] command = new byte[]{27, 33, temp};
        return command;
    }

}
