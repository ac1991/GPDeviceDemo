package com.example.devicedemo.bean;


import java.util.ArrayList;
import java.util.List;

public class TicketPrintBean {
    public static final int SIMPLE_LINE_BEAN = 0;
    public static final int COLUMN_LINE_BEAN = SIMPLE_LINE_BEAN+1;
    public static final int QRCODE_BEAN = COLUMN_LINE_BEAN+1;

    private int printCount = 1;
    private List<AbstractLineBean> lineList = new ArrayList<>();

    public TicketPrintBean() {
    }


    public int getPrintCount() {
        return printCount;
    }

    public void setPrintCount(int printCount) {
        this.printCount = printCount;
    }

    public List<AbstractLineBean> getLineList() {
        return lineList;
    }

    public void addLineBean(AbstractLineBean lineBean){
        lineList.add(lineBean);
    }

    public void addLineStr(){
        lineList.add(new SimpleLineBean(TicketAlign.LEFT, "-------------------------------"));
    }

    public void addEmptyLine(){
        lineList.add(new SimpleLineBean(TicketAlign.LEFT, " "));
    }


}
