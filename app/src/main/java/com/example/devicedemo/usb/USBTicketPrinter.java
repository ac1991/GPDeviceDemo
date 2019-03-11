package com.example.devicedemo.usb;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

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

	private static final int TRANSFER_TIMEOUT = 200;

	public USBTicketPrinter(Context context, UsbDevice device) throws IOException {
		UsbInterface iface = null;
		UsbEndpoint epout = null;
		
		for(int i=0; i<device.getInterfaceCount(); i++) {
			iface = device.getInterface(i);
			if (iface == null)
				throw new IOException("failed to get interface "+i);

			int epcount = iface.getEndpointCount();
			for (int j = 0; j < epcount; j++) {
				UsbEndpoint ep = iface.getEndpoint(j);
				if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
					epout = ep;
					break;
				}
			}
			
			if(epout != null)
				break;
		}

		if (epout == null) {
			throw new IOException("no output endpoint.");
		}

		mDevice = device;
		mInterface = iface;
		mEndpoint = epout;

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

}
