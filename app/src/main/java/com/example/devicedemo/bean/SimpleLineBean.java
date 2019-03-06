package com.example.devicedemo.bean;


public class SimpleLineBean extends AbstractLineBean {

    public TicketAlign align = TicketAlign.LEFT;
    public String text;

    public SimpleLineBean(TicketTextSize textSize, TicketAlign align, String text) {
        this.textSize = textSize;
        this.align = align;
        this.text = text;
    }
    public SimpleLineBean(TicketAlign align, String text) {
        this.align = align;
        this.text = text;
    }

    public SimpleLineBean() {
    }

    @Override
    public int getType() {
        return TicketPrintBean.SIMPLE_LINE_BEAN;
    }
}
