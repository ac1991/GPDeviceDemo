package com.example.devicedemo.ticket;


public class TicketPrintConfig{

    private static final int SIZE_CHINESE = 24; //中文字体占用24像素 (默认字体大小为24)
    private static final int SIZE_ENGLISH = 12; //英文字体占用12像素 (默认字体大小为24)
    private static final int MM_TO_PX = 8; //1mm = 8px

    private int printCount = 1;//小票打印张数（int）
    /** 参考 TicketWidth */
    private int width = 58; //打印纸宽度（单选：58mm、80mm）
    private boolean printLogo = false;//收据打印logo（boolean）
    private boolean beep = false;//打印蜂鸣器预警（boolean）
    private int depth = 5;//小票黑体深度（1~6）

    private String lineStr;


    public int getPrintCount() {
        if(printCount < 1){
            return 1;
        }
        return printCount;
    }

    public void setPrintCount(int printCount) {
        this.printCount = printCount;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
        createLine();
    }

    public boolean isPrintLogo() {
        return printLogo;
    }

    public void setPrintLogo(boolean printLogo) {
        this.printLogo = printLogo;
    }

    public boolean isBeep() {
        return beep;
    }

    public void setBeep(boolean beep) {
        this.beep = beep;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }


    public String getLineStr(String title) {
        if(lineStr == null){
            createLine();
        }
        if(title != null && title.length() > 0){
            int txtLen = title.length()* (SIZE_CHINESE / SIZE_ENGLISH);
            int lineCount = lineStr.length() - txtLen;
            int midIndex = lineCount/2;
            StringBuffer line = new StringBuffer(lineStr);
            line.delete(midIndex,midIndex + txtLen);
            line.insert(midIndex,title);
            return line.toString();
        }
        return lineStr;
    }

    private void createLine(){
        int paperWidthPx = (width - 10) * MM_TO_PX;
        StringBuffer sb = new StringBuffer();
        int count = paperWidthPx / SIZE_ENGLISH;
        for (int i = 0; i < count ; i++) {
            sb.append("-");
        }
        lineStr = sb.toString();
    }
}
