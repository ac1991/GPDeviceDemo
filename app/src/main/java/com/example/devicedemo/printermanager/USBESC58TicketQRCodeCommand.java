package com.example.devicedemo.printermanager;

import android.util.Log;

import com.example.devicedemo.utils.StringUtils;

import net.posprinter.utils.DataForSendToPrinterPos58;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sqwu on 2019/3/11
 */
public class USBESC58TicketQRCodeCommand {

    public static List<byte[]> printQRCode(String content){

        List<byte[]> qrCodeCommand = new ArrayList<>();

        if (StringUtils.getStringLength(content) >= 100){
            Log.e(USBESC58TicketQRCodeCommand.class.getName(), "58毫米打印机，二维码内容长度不得长于100个字符");
            return qrCodeCommand;
        }

        //设置居中对齐
//        byte[] align = new byte[]{27, 97, 1};
        //设置纠错等级
        byte[] level = new byte[]{29, 40, 107, 3, 0, 49, 69, 0x31};
        //设置模块大小
        byte[] qrcodeSize = new byte[]{29, 40, 107, 3, 0, 49, 67, 5};

        List<byte[]> qrcodeContent = addStoreQRCodeData(content);

        byte[] printQRCode = new byte[]{29, 40, 107, 3, 0, 49, 81, 48};

        byte[] printAndLineFeed = new byte[]{10};

//        qrCodeCommand.add(align);
        qrCodeCommand.add(level);
        qrCodeCommand.add(qrcodeSize);
        qrCodeCommand.addAll(qrcodeContent);
        qrCodeCommand.add(printQRCode);
        qrCodeCommand.add(printAndLineFeed);
        qrCodeCommand.add(DataForSendToPrinterPos58.initializePrinter());
        return qrCodeCommand;
    }

    /**
     * 添加qrcode内容
     * @param content
     * @return
     */
    private static List<byte[]> addStoreQRCodeData(String content) {
        List<byte[]> commands = new ArrayList<>();
        byte[] command = new byte[]{29, 40, 107, (byte)((content.getBytes().length + 3) % 256), (byte)((content.getBytes().length + 3) / 256), 49, 80, 48};
        commands.add(command);
        byte[] bs = null;
        if (!content.equals("")) {
            try {
                bs = content.getBytes("utf-8");
            } catch (UnsupportedEncodingException var5) {
                var5.printStackTrace();
            }

            commands.add(bs);
//            for(int i = 0; i < bs.length; ++i) {
//                this.Command.add(bs[i]);
//            }
        }

        return commands;
    }
}
