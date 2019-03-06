package com.example.devicedemo.bean;

import com.tools.command.EscCommand;

public enum TicketAlign {

    LEFT(0),CENTER(1),RIGHT(2);

    int val = 0;
    TicketAlign(int val){
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    public EscCommand.JUSTIFICATION getGp58MBIIIAlign(){
        switch (val){
            case 0:
                return EscCommand.JUSTIFICATION.LEFT;
            case 1:
                return EscCommand.JUSTIFICATION.CENTER;
            case 2:
                return EscCommand.JUSTIFICATION.RIGHT;
                default:
                    return EscCommand.JUSTIFICATION.LEFT;
        }
    }

}
