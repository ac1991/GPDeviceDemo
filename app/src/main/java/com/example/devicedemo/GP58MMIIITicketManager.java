package com.example.devicedemo;

import android.util.Log;

import com.example.devicedemo.bean.AbstractLineBean;
import com.example.devicedemo.bean.ColumnLineBean;
import com.example.devicedemo.bean.SimpleLineBean;
import com.example.devicedemo.bean.TicketAlign;
import com.example.devicedemo.bean.TicketPrintBean;
import com.example.devicedemo.bean.TicketTextSize;
import com.example.devicedemo.utils.DeviceConnFactoryManager;
import com.tools.command.EscCommand;

import java.util.Vector;

/**
 * Created by sqwu on 2019/3/5
 */
public class GP58MMIIITicketManager {
    /**
     * 最多允许字符宽度
     * 此处-20是因为防止右边字符溢出换行
     */
    private static int maxSize = (384 - 20)/12;

    public static void print(TicketPrintBean bean){
        EscCommand esc = new EscCommand();
        esc.addInitializePrinter();
        for (int i = 0; i < bean.getLineList().size(); i++) {
            AbstractLineBean lineBean = bean.getLineList().get(i);
            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
            switch (lineBean.getType()){
                case TicketPrintBean.SIMPLE_LINE_BEAN:
                    SimpleLineBean simpleLineBean = (SimpleLineBean) lineBean;
                    //设置居中、居左、居右
                    esc.addSelectJustification(simpleLineBean.align.getGp58MBIIIAlign());
                    esc.addUserCommand(simpleLineBean.textSize.getGP58MMIIITextSize());
//                    esc.add
                    esc.addText(simpleLineBean.text+"\n");
                    break;
                case TicketPrintBean.COLUMN_LINE_BEAN:
                    ColumnLineBean columnLineBean = (ColumnLineBean) lineBean;
                    //默认倍高
                    esc.addUserCommand(TicketTextSize.FONT_SIZE_NORMAL.getGP58MMIIITextSize());
                    //默认居左打印
                    esc.addSelectJustification(TicketAlign.LEFT.getGp58MBIIIAlign());
                    addTextRow(esc, columnLineBean.texts, null);
                    break;
            }
        }

        esc.addPrintAndFeedLines((byte) 4);
        // 加入查询打印机状态，用于连续打印
        byte[] bytes={29,114,1};
        esc.addUserCommand(bytes);

        Vector<Byte> datas = esc.getCommand();
        // 发送数据
        DeviceConnFactoryManager.getDeviceConnFactoryManagers()[0].sendDataImmediately(datas);

    }

    /**
     * 获取字符长度
     * 中文字符长度为：2
     * 其它字符长度为：1
     * @param value
     * @return
     */
    public static int getStringLength(String value) {
        int valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 2;
            } else {
                valueLength += 1;
            }
        }
        return valueLength;
    }

    /**
     * 添加换行符
     * @return
     */
    public static void addTextRow(EscCommand escCommand, String[] text, int[] weight){
        int length = text.length;

        int marginSize = 2;

        //获取每列的宽度
        int columnWidth = maxSize / length - marginSize ;

        Log.i("每列宽度：", columnWidth + "");

        //获取文本的最大长度
        int maxTextLength = 0;
        for (int textIndex = 0; textIndex < length; textIndex++) {
            int tempLength = getStringLength(text[textIndex]);
            if (maxTextLength < tempLength) {
                maxTextLength = tempLength;
            }

            Log.i("获取最大长度", "maxTextLength:" + maxTextLength);
        }

        Log.i("分行", "分行:" + (maxTextLength / columnWidth + 1));
        for (int i = 0; i < maxTextLength / columnWidth + 1; i++) {//分行
            Log.i("第几行：", i + "");

            for (int index = 0; index < length; index++) {
                int textLength = getStringLength(text[index]);
                if (textLength > columnWidth * i) {
                    Log.i("第几列：", index + "");

                    //设置绝对打印位置
                    escCommand.addSetAbsolutePrintPosition((short) (index * (columnWidth + marginSize)* 12));
                    escCommand.addText(getSubString(text[index], i * columnWidth, (i + 1) * columnWidth));
                }
            }
            escCommand.addPrintAndLineFeed();
        }
    }

    /**
     * 截取字符串子集
     * 中文长度算：2， 非中文长度算：1
     * @param text
     * @param startIndex
     * @param endIndex
     * @return
     */
    public static String getSubString(String text, int startIndex, int endIndex){
        Log.i("String_length(text):" + getStringLength(text), "startIndex:" + startIndex);

        if (getStringLength(text) > startIndex){

            String subString = "";
            int lastIndex = 0;
            for (int i = 0; i < text.length(); i++){
                String childString = text.substring(i,i+1);
                int charLength= getStringLength(childString);
                Log.i("lastIndex + charLength:" + (lastIndex + charLength), "lastIndex:" + lastIndex + "   charLength:" + charLength + "   startIndex:" + startIndex);

                Log.d("childString", childString);
                if (lastIndex + charLength > startIndex) {
                    Log.d("endIndex", endIndex + "");
                    if (lastIndex <= endIndex && lastIndex + charLength > endIndex) {
                        Log.d("subString", subString);
                        return subString;
                    }

                    subString += childString;
                }

                lastIndex += charLength;

            }

            return subString;
        }
        return "";
    }
}
