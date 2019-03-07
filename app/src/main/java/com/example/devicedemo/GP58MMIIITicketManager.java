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

import static com.example.devicedemo.bean.TicketAlign.LEFT;

/**
 * Created by sqwu on 2019/3/5
 */
public class GP58MMIIITicketManager {
    /**
     * 最多允许字符宽度
     * 此处-20是因为防止右边字符溢出换行
     */
    private static int maxSize = (384)/12;

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
                    esc.addSelectJustification(LEFT.getGp58MBIIIAlign());
                    try {
                        addTextRow(esc, columnLineBean.texts, columnLineBean.widths, columnLineBean.aligns);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

        esc.addPrintAndFeedLines((byte) 4);
        // 加入查询打印机状态，用于连续打印
        byte[] bytes={29,114,1};
        esc.addUserCommand(bytes);

        Vector<Byte> datas = esc.getCommand();
        // 发送数据
        DeviceConnFactoryManager.getDeviceConnFactoryManagers().sendDataImmediately(datas);

    }

    /**
     * 获取字符长度
     * 中文字符长度为：2
     * 其它字符长度为：1
     * @param value 字符串
     * @return 字符串的占位长度
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
     * 添加一行需要打印的数据
     * @param escCommand esc命令集
     * @param text 字符数组
     */
    public static void addTextRow(EscCommand escCommand, String[] text){
        try {
            addTextRow(escCommand, text, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加一行需要打印的数据
     * @param escCommand esc命令集
     * @param texts 字符数组
     * @param weights 每列的权重
     * @throws Exception
     */
    public static void addTextRow(EscCommand escCommand, String[] texts, int[] weights) throws Exception {
        addTextRow(escCommand, texts, weights, null);
//        int textArraySize = texts.length;
//
//        int marginSize = 1;//单位：字符
//
//        if (weights == null){
//            weights = new int[textArraySize];
//            for (int i = 0; i < textArraySize; i++){
//                weights[i] = 1;
//            }
//        }
//
//        if (texts.length != weights.length){
//            throw new Exception("text.length != weight.length");
//        }
//
//        //总权重
//        int sumWeight = 0;
//        for (int i = 0; i< weights.length; i++){
//            sumWeight += weights[i];
//        }
//
//        int maxRowNum = 0;//获取最大行数
//        int[] textLengthColumns = new int[textArraySize]; //不同列文本的长度
//        int[] offsetColumns = new int[textArraySize]; //每列文本偏移量
//        int offsetSum = 0;
//        for (int textIndex = 0; textIndex < textArraySize; textIndex++) {
//            offsetColumns[textIndex] = offsetSum;
//            int textLengthColumn = (int) ((maxSize - marginSize * (textArraySize - 1)) * (((float)weights[textIndex] /(float)sumWeight))); //当前列文本的长度
//            offsetSum += textLengthColumn * 12 + marginSize * 12;
//
//            textLengthColumns[textIndex] = textLengthColumn;
//
//            int textLength = getStringLength(texts[textIndex]);//获取文本的长度
//            int tempRowNum = (textLength % textLengthColumn) > 0 ? (textLength/ textLengthColumn) + 1 : (textLength/ textLengthColumn);
//
//            if (maxRowNum < tempRowNum) {
//                maxRowNum = tempRowNum;
//            }
//            Log.i("获取最大行数", "maxRowNum:" + maxRowNum);
//        }
//
//        for (int i = 0; i < maxRowNum; i++) {//分行
//            Log.i("第几行：", i + "");
//
//            for (int index = 0; index < textArraySize; index++) { //为每列文本设置被截取的字符串和位移量
//                int textLength = getStringLength(texts[index]);
//                if (textLength > i * textLengthColumns[index]) { //判断字符长度是否大于 行数 * 每行文本数，如果大于，则添加打印，如果小于，则表示已经无文本可打印
//                    Log.i("第几列：", index + "");
//
//                    //设置绝对打印位置
//                    escCommand.addSetAbsolutePrintPosition((short) offsetColumns[index]);
//                    escCommand.addText(getSubString(texts[index], i * textLengthColumns[index], (i + 1)* textLengthColumns[index]));
//                }
//            }
//            escCommand.addPrintAndLineFeed();
//        }
    }

    /**
     * 添加一行需要打印的数据
     * @param escCommand esc命令集
     * @param texts 字符数组
     * @param weights 每列的权重
     * @throws Exception
     */
    public static void addTextRow(EscCommand escCommand, String[] texts, int[] weights, TicketAlign[] ticketAligns) throws Exception {
        int textArraySize = texts.length;

        int marginSize = 1;//单位：字符

        if (weights == null){
            weights = new int[textArraySize];
            for (int i = 0; i < textArraySize; i++){
                weights[i] = 1;
            }
        }

        if (ticketAligns == null){
            ticketAligns = new TicketAlign[textArraySize];
            for (int i = 0; i < textArraySize; i++){
                ticketAligns[i] = TicketAlign.LEFT;
            }
        }

        if (texts.length != weights.length || ticketAligns.length != weights.length){
            throw new Exception("texts.length != weights.length || ticketAligns.length != weights.length");
        }

        //总权重
        int sumWeight = 0;
        for (int i = 0; i< weights.length; i++){
            sumWeight += weights[i];
        }

        int maxRowNum = 0;//获取最大行数
        int[] textLengthColumns = new int[textArraySize]; //不同列文本的长度
        int[] offsetColumns = new int[textArraySize]; //每列文本偏移量
        int offsetSum = 0;
        for (int textIndex = 0; textIndex < textArraySize; textIndex++) {
            offsetColumns[textIndex] = offsetSum;
            int textLengthColumn = (int) ((maxSize - marginSize * (textArraySize - 1)) * (((float)weights[textIndex] /(float)sumWeight))); //当前列文本的长度
            offsetSum += textLengthColumn * 12 + marginSize * 12;

            textLengthColumns[textIndex] = textLengthColumn;

            int textLength = getStringLength(texts[textIndex]);//获取文本的长度
            int tempRowNum = (textLength % textLengthColumn) > 0 ? (textLength/ textLengthColumn) + 1 : (textLength/ textLengthColumn);

            if (maxRowNum < tempRowNum) {
                maxRowNum = tempRowNum;
            }
            Log.i("获取最大行数", "maxRowNum:" + maxRowNum);
        }

        for (int i = 0; i < maxRowNum; i++) {//分行
            Log.i("第几行：", i + "");

            for (int index = 0; index < textArraySize; index++) { //为每列文本设置被截取的字符串和位移量
                int textLength = getStringLength(texts[index]);
                if (textLength > i * textLengthColumns[index]) { //判断字符长度是否大于 行数 * 每行文本数，如果大于，则添加打印，如果小于，则表示已经无文本可打印
                    Log.i("第几列：", index + "");

                    //设置绝对打印位置
                    escCommand.addSetAbsolutePrintPosition((short) offsetColumns[index]);
                    String subString = getSubString(texts[index], i * textLengthColumns[index], (i + 1)* textLengthColumns[index]);

                    switch (ticketAligns[index]){
                        case LEFT:

                            break;
                        case CENTER:
                            subString = getCenterAlignString(subString, textLengthColumns[index]);
                            break;
                        case RIGHT:
                            subString = getRightAlignString(subString, textLengthColumns[index]);
                            break;
                    }

                    escCommand.addText(subString);
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

    private static String getCenterAlignString(String text, int rowLength){
        int textLength = getStringLength(text);
        String centerString = "";
        if (textLength < rowLength){
            for (int i = 0; i < (rowLength - textLength)/2; i++){
                centerString += " ";
            }
            centerString += text;
        }else {
            centerString = text;
        }

        return centerString;
    }

    private static String getRightAlignString(String text, int rowLength){
        int textLength = getStringLength(text);
        String centerString = "";
        if (textLength < rowLength){
            for (int i = 0; i < (rowLength - textLength); i++){
                centerString += " ";
            }
            centerString += text;
        }else {
            centerString = text;
        }

        return centerString;
    }
}
