package com.example.devicedemo.printermanager;

import android.annotation.SuppressLint;
import android.util.Log;


import com.example.devicedemo.bean.AbstractLineBean;
import com.example.devicedemo.bean.ColumnLineBean;
import com.example.devicedemo.bean.SimpleLineBean;
import com.example.devicedemo.bean.TicketAlign;
import com.example.devicedemo.bean.TicketPrintBean;
import com.example.devicedemo.utils.StringUtils;

import net.posprinter.utils.DataForSendToPrinterPos58;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by sqwu on 2019/3/5
 *小票打印排版
 */
public class USBESC58TicketTypesetting {
    private static int  charSize = 12;//设置一个字符长度为12
    /**
     * 最多允许字符宽度
     * 此处-20是因为防止右边字符溢出换行
     */
    private static int maxSize = (384 )/charSize;

    public static List<byte[]> print(TicketPrintBean bean){
        List<byte[]> list = new ArrayList<byte[]>();
        for (int i = 0; i < bean.getLineList().size(); i++) {
            AbstractLineBean lineBean = bean.getLineList().get(i);
//            esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
            switch (lineBean.getType()){
                case TicketPrintBean.SIMPLE_LINE_BEAN:
                    list.add(DataForSendToPrinterPos58.initializePrinter());
//                    esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
                    SimpleLineBean simpleLineBean = (SimpleLineBean) lineBean;
                    list.add(DataForSendToPrinterPos58.setLineSpaceing(70));
                    list.add(DataForSendToPrinterPos58.selectAlignment(simpleLineBean.align.getVal()));
                    list.add(DataForSendToPrinterPos58.selectCharacterSize(simpleLineBean.textSize.getXPrinterTextSize()));
                    list.add(StringUtils.strTobytes(simpleLineBean.text));
                    list.add(DataForSendToPrinterPos58.printAndFeedLine());
                    break;
                case TicketPrintBean.COLUMN_LINE_BEAN:
                    ColumnLineBean columnLineBean = (ColumnLineBean) lineBean;
                    list.add(DataForSendToPrinterPos58.initializePrinter());
                    //默认倍高
//                    esc.addUserCommand(TicketTextSize.FONT_SIZE_NORMAL.getGP58MMIIITextSize());
                    //默认居左打印
//                    esc.addSelectJustification(LEFT.getGp58MBIIIAlign());
                    try {
                        addTextRow(list, columnLineBean.texts, columnLineBean.widths, columnLineBean.aligns);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

        return list;

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
            }else {
                valueLength += 1;
            }
        }
        return valueLength;
    }

    // 根据UnicodeBlock方法判断中文标点符号

    @SuppressLint("NewApi")
    public static boolean isChinesePunctuation(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS
                || ub == Character.UnicodeBlock.VERTICAL_FORMS) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 添加一行需要打印的数据
     *
     * 具体算法：
     * 1.计算每一个text的长度（一个中文字符长度为：2，一个英文字符长度为1）
     * 2.根据权重计算每列字符的宽度（默认权重相同）和偏移量
     * 3.根据每列字符宽度截取文本打印（一个中文字符长度为：2，一个英文字符长度为1）
     * 4.根据对齐设置进行文本排版
     * 5.添加打印
     *
     * @param list esc命令集
     * @param texts 字符数组
     * @param weights 每列的权重
     * @throws Exception
     */
    public static void addTextRow(List<byte[]> list, String[] texts, int[] weights, TicketAlign[] ticketAligns) throws Exception {
        //设置位移单位，算法参考文档
        list.add(DataForSendToPrinterPos58.setHorizontalAndVerticalMoveUnit((int) (Math.round(25.4 * 8 / charSize)), 0));
        list.add(DataForSendToPrinterPos58.selectFont(0));
        list.add(DataForSendToPrinterPos58.setLineSpaceing(70));
//        escCommand.addSetHorAndVerMotionUnits((byte)0, (byte)0);
        // 取消倍高倍宽
//        escCommand.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
        //是否反白打印
//        escCommand.addTurnReverseModeOnOrOff(EscCommand.ENABLE.ON);
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
        int[] textLengthColumns = new int[textArraySize]; //不同列文本的宽度
        int[] offsetColumns = new int[textArraySize]; //每列文本偏移量
        int offsetSum = 0;
        for (int textIndex = 0; textIndex < textArraySize; textIndex++) {
            offsetColumns[textIndex] = offsetSum;
            int textLengthColumn = Math.round(((maxSize - marginSize * (textArraySize - 1)) * (((float)weights[textIndex] /(float)sumWeight)))); //当前列文本的长度
            offsetSum += textLengthColumn  + marginSize ;

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
                if (textLength > i * textLengthColumns[index]) { //判断字符长度是否大于 行数 * 每行文本数，如果大于，则添加打印；如果小于，则表示已经无文本可打印
                    Log.i("第几列：", index + "");
                    Log.i("位移：", offsetColumns[index] + "");
                    //设置绝对打印位置，即每列字符串的偏移量
//                    escCommand.addSetAbsolutePrintPosition((short) offsetColumns[index]);
                    list.add(DataForSendToPrinterPos58.setAbsolutePrintPosition(offsetColumns[index], 0));
                    String subString = getSubString(texts[index], i * textLengthColumns[index], (i + 1)* textLengthColumns[index]);

                    //设置对齐方式
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

                    list.add(StringUtils.strTobytes(subString));
                }
            }

            list.add(DataForSendToPrinterPos58.printAndFeedLine());
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
                        Log.d("截取字符", subString);
                        return subString;
                    }

                    subString += childString;
                }

                lastIndex += charLength;

            }
            Log.d("截取字符", subString);
            return subString;
        }
        return "";
    }

    /**
     * 获取经过居中对齐之后的排版字符串
     * @param text
     * @param rowLength
     * @return
     */
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

    /**
     * 获取经过居右对齐的排版字符串
     * @param text
     * @param rowLength
     * @return
     */
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
