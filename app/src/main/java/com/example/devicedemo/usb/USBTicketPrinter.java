package com.example.devicedemo.usb;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.example.devicedemo.manager.UsbConnect;

import java.io.IOException;
import java.util.List;

/**
 * usb小票打印机实际连接和打印功能类
 * 实现usb设备连接和打印功能
 */
public class USBTicketPrinter {

	private static final String TAG = "USBTicketPrinter";

	private final UsbDevice mDevice;
	private final UsbDeviceConnection mConnection;
	private final UsbInterface mInterface;
	private final UsbEndpoint mEndpoint;
	private final UsbEndpoint mInEndpoint;

	private static final int TRANSFER_TIMEOUT = 200;

	/**
	 * ESC查询打印机实时状态指令
	 * 查询脱机状态
	 */
	private byte[] esc = {0x10, 0x04, 0x02};

	public USBTicketPrinter(Context context, UsbDevice device) throws IOException {
		UsbInterface iface = null;
		UsbEndpoint epout = null;
		UsbEndpoint epin = null;
		
		for(int i=0; i<device.getInterfaceCount(); i++) {
			iface = device.getInterface(i);
			if (iface == null)
				throw new IOException("failed to get interface "+i);

			int epcount = iface.getEndpointCount();
			for (int j = 0; j < epcount; j++) {
				UsbEndpoint ep = iface.getEndpoint(j);
				if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
					epout = ep;
//					break;
				}

				if (ep.getDirection() == UsbConstants.USB_DIR_IN){
					epin = ep;
				}
			}
			
			if(epout != null  && epin != null)
				break;
		}

		if (epout == null) {
			throw new IOException("no output endpoint.");
		}

		mDevice = device;
		mInterface = iface;
		mEndpoint = epout;
		mInEndpoint = epin;

		UsbManager usbman = (UsbManager) context.getSystemService(Context.USB_SERVICE);
		mConnection = usbman.openDevice(mDevice);

		if (mConnection == null) {
			throw new IOException("failed to open usb device.");
		}

		boolean res = mConnection.claimInterface(mInterface, true);
		Log.d("USB","claimInterface "+ res);
	}

	public void write(byte[] data) throws IOException {
		if (mConnection.bulkTransfer(mEndpoint, data, data.length,
				TRANSFER_TIMEOUT) != data.length)
			throw new IOException("failed to write usb endpoint.");
	}

	public void close() {
		mConnection.releaseInterface(mInterface);
		mConnection.close();
	}

	public void print(List<byte[]> list){
		if (list != null && list.size() > 0){
			for (int i =0; i < list.size(); i++){
				try {
					write(list.get(i));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		//每次打印完成后发送打印机状态查询指令
		try {
			write(esc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void printQRCode(List<byte[]> list){
		if (list != null && list.size() > 0){
			for (int i =0; i < list.size(); i++){
				try {
					write(list.get(i));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		//每次打印完成后发送打印机状态查询指令
		try {
			write(esc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 打开钱箱
	 */
	public void openCashBox(){
		byte[] command = new byte[]{27, 112, (byte)1,  (byte) 255, (byte) 255};
		try {
			write(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int readData(byte[] bytes) throws IOException {
		return this.mConnection != null ? this.mConnection.bulkTransfer(this.mInEndpoint, bytes, bytes.length, 200) : 0;
	}

}
