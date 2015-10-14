package com.celulabs.pfcsense.ble.btsig.profiles;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TableRow;

import com.celulabs.pfcsense.ble.common.BluetoothLeService;
import com.celulabs.pfcsense.ble.common.GenericBluetoothProfile;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class DeviceInformationServiceProfile extends GenericBluetoothProfile {
	private static final String dISService_UUID = "0000180a-0000-1000-8000-00805f9b34fb";
	private static final String dISSystemID_UUID = "00002a23-0000-1000-8000-00805f9b34fb";
	private static final String dISModelNR_UUID = "00002a24-0000-1000-8000-00805f9b34fb";
	private static final String dISSerialNR_UUID = "00002a25-0000-1000-8000-00805f9b34fb";
	private static final String dISFirmwareREV_UUID = "00002a26-0000-1000-8000-00805f9b34fb";
	private static final String dISHardwareREV_UUID = "00002a27-0000-1000-8000-00805f9b34fb";
	private static final String dISSoftwareREV_UUID = "00002a28-0000-1000-8000-00805f9b34fb";
	private static final String dISManifacturerNAME_UUID = "00002a29-0000-1000-8000-00805f9b34fb";
	public final static String ACTION_FW_REV_UPDATED = "com.celulabs.pfcsense.ble.btsig.ACTION_FW_REV_UPDATED";
	public final static String EXTRA_FW_REV_STRING = "com.celulabs.pfcsense.ble.btsig.EXTRA_FW_REV_STRING";
	
	BluetoothGattCharacteristic systemIDc;
	BluetoothGattCharacteristic modelNRc;
	BluetoothGattCharacteristic serialNRc;
	BluetoothGattCharacteristic firmwareREVc;
	BluetoothGattCharacteristic hardwareREVc;
	BluetoothGattCharacteristic softwareREVc;
	BluetoothGattCharacteristic ManifacturerNAMEc;
	DeviceInformationServiceTableRow tRow;
	
	public DeviceInformationServiceProfile(Context con,BluetoothDevice device,BluetoothGattService service,BluetoothLeService controller) {
		super(con,device,service,controller);
		this.tRow =  new DeviceInformationServiceTableRow(con);
		
		List<BluetoothGattCharacteristic> characteristics = this.mBTService.getCharacteristics();
		
		for (BluetoothGattCharacteristic c : characteristics) {
			if (c.getUuid().toString().equals(dISSystemID_UUID)) {
				this.systemIDc = c;
			}
			if (c.getUuid().toString().equals(dISModelNR_UUID)) {
				this.modelNRc = c;
			}
			if (c.getUuid().toString().equals(dISSerialNR_UUID)) {
				this.serialNRc = c;
			}
			if (c.getUuid().toString().equals(dISFirmwareREV_UUID)) {
				this.firmwareREVc = c;
			}
			if (c.getUuid().toString().equals(dISHardwareREV_UUID)) {
				this.hardwareREVc = c;
			}
			if (c.getUuid().toString().equals(dISSoftwareREV_UUID)) {
				this.softwareREVc = c;
			}
			if (c.getUuid().toString().equals(dISManifacturerNAME_UUID)) {
				this.ManifacturerNAMEc = c;
			}
		}
		tRow.title.setText("Device Information Service");
		tRow.sl1.setVisibility(View.INVISIBLE);
		this.tRow.setIcon(this.getIconPrefix(), service.getUuid().toString());
	}
	public static boolean isCorrectService(BluetoothGattService service) {
		// FIXME deshabilitado servicio para que no lo descubra
//		if ((service.getUuid().toString().compareTo(dISService_UUID)) == 0) {
//			return true;
//		}
//		else return false;
		return false;
	}
	@Override
	public void configureService() {
		// Nothing to do here
		
	}
	@Override
	public void deConfigureService() {
		// Nothing to do here
	}
	@Override
	public void enableService () {
		// Read all values
		this.mBTLeService.readCharacteristic(this.systemIDc);
		mBTLeService.waitIdle(GATT_TIMEOUT);
		this.mBTLeService.readCharacteristic(this.modelNRc);
		mBTLeService.waitIdle(GATT_TIMEOUT);
		this.mBTLeService.readCharacteristic(this.serialNRc);
		mBTLeService.waitIdle(GATT_TIMEOUT);
		this.mBTLeService.readCharacteristic(this.firmwareREVc);
		mBTLeService.waitIdle(GATT_TIMEOUT);
		this.mBTLeService.readCharacteristic(this.hardwareREVc);
		mBTLeService.waitIdle(GATT_TIMEOUT);
		this.mBTLeService.readCharacteristic(this.softwareREVc);
		mBTLeService.waitIdle(GATT_TIMEOUT);
		this.mBTLeService.readCharacteristic(this.ManifacturerNAMEc);
	}
	@Override
	public void disableService () {
		// Nothing to do here
	}
	@Override
	public void didWriteValueForCharacteristic(BluetoothGattCharacteristic c) {
		
	}
	@Override
	public void didReadValueForCharacteristic(BluetoothGattCharacteristic c) {
		if (this.systemIDc != null) {
			if (c.equals(this.systemIDc)) {
				String s = "System ID: ";
				for (byte b : c.getValue()) {
					s+= String.format("%02x:", b);
				}
				this.tRow.SystemIDLabel.setText(s);
				
			}
		}
		if (this.modelNRc != null) {
			if (c.equals(this.modelNRc)) {
				try {
					this.tRow.ModelNRLabel.setText("Model NR: " + new String(c.getValue(),"UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (this.serialNRc != null) {
			if (c.equals(this.serialNRc)) {
				try {
					this.tRow.SerialNRLabel.setText("Serial NR: " + new String(c.getValue(),"UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (this.firmwareREVc != null) {
			if (c.equals(this.firmwareREVc)) {
				try {
					String s = new String(c.getValue(),"UTF-8");
					this.tRow.FirmwareREVLabel.setText("Firmware Revision: " + s);
					//Post firmware revision to Device activity
					final Intent intent = new Intent(ACTION_FW_REV_UPDATED);
					intent.putExtra(EXTRA_FW_REV_STRING, s);
					context.sendBroadcast(intent);
					
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (this.hardwareREVc != null) {
			if (c.equals(this.hardwareREVc)) {
				try {
					this.tRow.HardwareREVLabel.setText("Hardware Revision: " + new String(c.getValue(),"UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (this.softwareREVc != null) {
			if (c.equals(this.softwareREVc)) {
				try {
					this.tRow.SoftwareREVLabel.setText("Software Revision: " + new String(c.getValue(),"UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (this.ManifacturerNAMEc != null) {
			if (c.equals(this.ManifacturerNAMEc)) {
				try {
					this.tRow.ManifacturerNAMELabel.setText("Manifacturer Name: " + new String(c.getValue(),"UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	public void didUpdateValueForCharacteristic(BluetoothGattCharacteristic c) {

	}
	@Override
	public String getIconPrefix() {
		String iconPrefix;
		if (this.mBTDevice.getName().equals("CC2650 SensorTag")) {
			iconPrefix = "sensortag2";
		}
		else iconPrefix = ""; 
		return iconPrefix;
	}
	@Override 
	public TableRow getTableRow() {
		return this.tRow;
	}
	
}
