package com.example.devicedemo.printermanager;

import net.posprinter.utils.DataForSendToPrinterPos58;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sqwu on 2019/3/12
 * 打印机命令
 */
public class USBESC58Command {
    List<byte[]>  commands = new ArrayList<>();

    public USBESC58Command(){
        //初始化打印机
        commands.add(DataForSendToPrinterPos58.initializePrinter());
    }

    public void setLineSpaceing(int lineSpaceing){
        commands.add(DataForSendToPrinterPos58.setLineSpaceing(lineSpaceing));
    }

    public void selectAlignment(int alignment){
        commands.add(DataForSendToPrinterPos58.selectAlignment(alignment));
    }

    public void selectCharacterSize(int size){
        commands.add(DataForSendToPrinterPos58.selectCharacterSize(size));
    }
}
