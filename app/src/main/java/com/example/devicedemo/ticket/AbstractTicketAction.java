package com.example.devicedemo.ticket;

import com.example.devicedemo.bean.TicketPrintBean;
import com.example.devicedemo.utils.OnActionCallback;


/***
 * 打印机处理任务
 */
public abstract class AbstractTicketAction implements BaseAction<TicketPrintConfig> {

    protected TicketPrintConfig config;
    private OnActionCallback callback;

    public abstract void doPrint(TicketPrintBean bean)throws Exception;
    public abstract void openCashBox();

    @Override
    public void setConfig(TicketPrintConfig config) {
        this.config = config;
    }

    @Override
    public TicketPrintConfig getConfig() {
        return config;
    }

    @Override
    public void setActionCallback(OnActionCallback callback) {
        this.callback = callback;
    }


    protected void notifyAction(boolean success,String message){
        if(callback != null){
            callback.onActionResult(success,message);
        }
    }

    protected void notifyOpen(boolean success,String message){
        if(callback != null){
            callback.onOpenResult(success,message);
        }
    }
}
