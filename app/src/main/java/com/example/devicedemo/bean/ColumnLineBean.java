package com.example.devicedemo.bean;



public class ColumnLineBean extends AbstractLineBean{

    public String[] texts;
    public TicketAlign[] aligns;
    public int[] widths;

    public ColumnLineBean() {
    }

    public ColumnLineBean(TicketTextSize textSize, String[] texts, TicketAlign[] aligns, int[] widths) {
        this.textSize = textSize;
        this.texts = texts;
        this.aligns = aligns;
        this.widths = widths;
    }

    public ColumnLineBean(String[] texts, TicketAlign[] aligns, int[] widths) {
        this.texts = texts;
        this.aligns = aligns;
        this.widths = widths;
    }


    @Override
    public int getType() {
        return TicketPrintBean.COLUMN_LINE_BEAN;
    }

    public int[] getAligns() {
        int[] vals = new int[aligns.length];
        int i = 0;
        for(TicketAlign ta:aligns){
            vals[i++] = ta.getVal();
        }
        return vals;
    }

}
