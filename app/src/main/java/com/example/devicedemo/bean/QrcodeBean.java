package com.example.devicedemo.bean;

public class QrcodeBean extends AbstractLineBean {

    public TicketAlign align = TicketAlign.LEFT;
    public String qrcoddeStr;

    public QrcodeBean(TicketAlign align, String qrcoddeStr) {
        this.align = align;
        this.qrcoddeStr = qrcoddeStr;
    }

    @Override
    public int getType() {
        return TicketPrintBean.QRCODE_BEAN;
    }
}
