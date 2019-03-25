package com.example.devicedemo.printermanager;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.example.devicedemo.utils.PrintUtils;
import com.example.devicedemo.utils.StringUtils;
import com.tools.command.EscCommand;
import com.tools.command.GpUtils;

import net.posprinter.utils.BitmapToByteData;
import net.posprinter.utils.DataForSendToPrinterPos58;
import net.posprinter.utils.DataForSendToPrinterPos80;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by sqwu on 2019/3/12
 * 打印机命令
 */
public class USBESC58Command {
    List<byte[]>  commands = new ArrayList<>();

    public USBESC58Command(){
        //初始化打印机
        commands.add(DataForSendToPrinterPos80.initializePrinter());
    }

    /**
     * 设置行间距
     * @param lineSpaceing
     */
    public void setLineSpaceing(int lineSpaceing){
        commands.add(DataForSendToPrinterPos58.setLineSpaceing(lineSpaceing));
    }

    /**
     * 打印并换行
     */
    public void printAndFeedLine(){
        commands.add(DataForSendToPrinterPos58.printAndFeedLine());
    }

    /**
     * 设置对齐
     * @param alignment
     */
    public void selectAlignment(int alignment){
        commands.add(DataForSendToPrinterPos58.selectAlignment(alignment));
    }

    /**
     * 设置字体大小
     * @param size
     */
    public void selectCharacterSize(int size){
        commands.add(DataForSendToPrinterPos58.selectCharacterSize(size));
    }

    /**
     * 设置文字
     * @param text
     */
    public void setText(String text){
        if (!TextUtils.isEmpty(text)){
            commands.add(StringUtils.strTobytes(text));
        }
    }

    /**
     * 设置二维码
     * @param text
     */
    public void setQRCode(String text){
        if (!TextUtils.isEmpty(text)){
            commands.addAll(USBESC58TicketQRCodeCommand.printQRCode(text));
        }
    }

    /**
     * 设置位移单位，一个单位为1个字符长度
     * @param charLength
     */
    public void setHorizontalAndVerticalMoveUnit(int charLength){
        //设置位移单位，算法参考文档
        commands.add(DataForSendToPrinterPos58.setHorizontalAndVerticalMoveUnit((int) (Math.round(22.5 * 8 / 12) * charLength), 0));
    }

    /**
     * 添加图片打印
     * @param bitmap
     */
    public void addRastBitImage(Bitmap bitmap){
//        commands.add(DataForSendToPrinterPos80.initializePrinter());
        int h = 150;
        List<Bitmap> bitmaplist = cutBitmap(h, bitmap);
        if(bitmaplist.size()!=0){
            for (int i=0;i<bitmaplist.size();i++){
                commands.add(DataForSendToPrinterPos80.
                        printRasterBmp(0,bitmaplist.get(i), BitmapToByteData.BmpType.Grey,BitmapToByteData.AlignType.Left,372));
            }
        }
//        printAndFeedLine();
        commands.add(DataForSendToPrinterPos80.printAndFeedForward(3));
        commands.add(DataForSendToPrinterPos80.selectCutPagerModerAndCutPager(66,1));
//        return list;
    }

    /**
     * 由于进行矩阵运算会导致乱码，此方法暂时废弃
     * @param bitmap
     * @param width
     *
     * @deprecated
     */
    public void addRastBitImage(Bitmap bitmap, int width){
        Bitmap newBitmap = PrintUtils.setImgWidth(bitmap, width);
        addRastBitImage(newBitmap);
    }

    /*
    切割图片方法，等高切割
     */
    private List<Bitmap> cutBitmap(int h,Bitmap bitmap){
        int width=bitmap.getWidth();
        int height=bitmap.getHeight();
        boolean full=height%h==0;
        int n=height%h==0?height/h:(height/h)+1;
        Bitmap b;
        List<Bitmap> bitmaps=new ArrayList<>();
        for (int i=0;i<n;i++){
            if (full){
                b=Bitmap.createBitmap(bitmap,0,i*h,width,h);
            }else {
                if (i==n-1){
                    b=Bitmap.createBitmap(bitmap,0,i*h,width,height-i*h);
                }else {
                    b=Bitmap.createBitmap(bitmap,0,i*h,width,h);
                }
            }

            bitmaps.add(b);
        }

        return bitmaps;
    }

    public void addRastBitImage(Bitmap bitmap, int nWidth, int nMode) {
        printAndFeedLine();
        if (bitmap != null) {
            //得到按纸面宽度得到的bitmap等比宽高
            int width = (nWidth + 7) / 8 * 8;
            int height = bitmap.getHeight() * width / bitmap.getWidth();


            //通过颜色矩阵对图片置灰
            Bitmap grayBitmap = PrintUtils.toGrayscale(bitmap);

            Bitmap rszBitmap = PrintUtils.resizeImage(grayBitmap, width, height);
            byte[] src = PrintUtils.bitmapToBWPix(rszBitmap);
            byte[] command = new byte[8];
            height = src.length / width;
            command[0] = 29;
            command[1] = 118;
            command[2] = 48;
            command[3] = (byte)(nMode & 1);
            command[4] = (byte)(width / 8 % 256);
            command[5] = (byte)(width / 8 / 256);
            command[6] = (byte)(Math.abs(height % 256));
            command[7] = (byte)(height / 256);
            commands.add(command);
            byte[] codecontent = PrintUtils.pixToEscRastBitImageCmd(src);

            commands.add(codecontent);
            commands.add(DataForSendToPrinterPos58.printAndFeedLine());
//            for(int k = 0; k < codecontent.length; ++k) {
//                this.commands.add(codecontent[k]);
//            }
        } else {
            Log.d("BMP", "bmp.  null ");
        }
//        EscCommand escCommand = new EscCommand();
//        escCommand.addRastBitImage(bitmap, nWidth, nMode);
//        Vector<Byte> gpCommands = escCommand.getCommand();
//        int commandSize = gpCommands.size();
//        byte[] gpCommandArray = new byte[commandSize];
//        for (int i = 0; i< commandSize; i++){
//            gpCommandArray[i] = gpCommands.get(i);
//        }
//        commands.add(gpCommandArray);
    }

    public List<byte[]> getCommands(){
        printAndFeedLine();
        return commands;
    }

}
