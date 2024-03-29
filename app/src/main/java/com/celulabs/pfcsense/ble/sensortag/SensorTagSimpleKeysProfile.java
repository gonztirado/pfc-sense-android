package com.celulabs.pfcsense.ble.sensortag;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.view.View;

import com.celulabs.pfcsense.ble.common.BluetoothLeService;
import com.celulabs.pfcsense.ble.common.GattInfo;
import com.celulabs.pfcsense.ble.common.GenericBluetoothProfile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SensorTagSimpleKeysProfile extends GenericBluetoothProfile {
	public SensorTagSimpleKeysProfile(Context con,BluetoothDevice device,BluetoothGattService service,BluetoothLeService controller) {
		super(con,device,service,controller);
		this.tRow =  new SensorTagSimpleKeysTableRow(con);
		
		List<BluetoothGattCharacteristic> characteristics = this.mBTService.getCharacteristics();
		
		for (BluetoothGattCharacteristic c : characteristics) {
			if (c.getUuid().toString().equals(SensorTagGatt.UUID_KEY_DATA.toString())) {
				this.dataC = c;
			}
		}
		this.tRow.setIcon(this.getIconPrefix(), this.dataC.getUuid().toString());
		this.tRow.title.setText(GattInfo.uuidToName(UUID.fromString(this.dataC.getUuid().toString())));
		this.tRow.uuidLabel.setText(this.dataC.getUuid().toString());
		
		if (!(this.mBTDevice.getName().equals("CC2650 SensorTag"))) {
			SensorTagSimpleKeysTableRow tmpRow = (SensorTagSimpleKeysTableRow) this.tRow;
			tmpRow.sl3.setVisibility(View.INVISIBLE);
			tmpRow.reedStateImage.setVisibility(View.INVISIBLE);
			
		}
		
		
	}
	public static boolean isCorrectService(BluetoothGattService service) {
		// FIXME deshabilitado servicio para que no lo descubra
//		if ((service.getUuid().toString().compareTo(SensorTagGatt.UUID_KEY_SERV.toString())) == 0) {
//			return true;
//		}
//		else return false;
		return false;
	}
	@Override 
	public void enableService () {
		this.isEnabled = true;
	}
	@Override 
	public void disableService () {
		this.isEnabled = false;
	}
	@Override
	public void didUpdateValueForCharacteristic(BluetoothGattCharacteristic c) {
		SensorTagSimpleKeysTableRow tmpRow = (SensorTagSimpleKeysTableRow) this.tRow;
		if (c.equals(this.dataC)){
            byte[] value = c.getValue();
			switch(value[0]) {
			case 0x1:
				tmpRow.leftKeyPressStateImage.setImageResource(R.drawable.leftkeyon_300);
				tmpRow.rightKeyPressStateImage.setImageResource(R.drawable.rightkeyoff_300);
				tmpRow.reedStateImage.setImageResource(R.drawable.reedrelayoff_300);
				break;
			case 0x2:
				tmpRow.leftKeyPressStateImage.setImageResource(R.drawable.leftkeyoff_300);
				tmpRow.rightKeyPressStateImage.setImageResource(R.drawable.rightkeyon_300);
				tmpRow.reedStateImage.setImageResource(R.drawable.reedrelayoff_300);
				break;
			case 0x3:
				tmpRow.leftKeyPressStateImage.setImageResource(R.drawable.leftkeyon_300);
				tmpRow.rightKeyPressStateImage.setImageResource(R.drawable.rightkeyon_300);
				tmpRow.reedStateImage.setImageResource(R.drawable.reedrelayoff_300);
				break;
			case 0x4:
				tmpRow.leftKeyPressStateImage.setImageResource(R.drawable.leftkeyoff_300);
				tmpRow.rightKeyPressStateImage.setImageResource(R.drawable.rightkeyoff_300);
				tmpRow.reedStateImage.setImageResource(R.drawable.reedrelayon_300);
				break;
			case 0x5:
				tmpRow.leftKeyPressStateImage.setImageResource(R.drawable.leftkeyon_300);
				tmpRow.rightKeyPressStateImage.setImageResource(R.drawable.rightkeyoff_300);
				tmpRow.reedStateImage.setImageResource(R.drawable.reedrelayon_300);
				break;
			case 0x6:
				tmpRow.leftKeyPressStateImage.setImageResource(R.drawable.leftkeyoff_300);
				tmpRow.rightKeyPressStateImage.setImageResource(R.drawable.rightkeyon_300);
				tmpRow.reedStateImage.setImageResource(R.drawable.reedrelayon_300);
				break;
			case 0x7:
				tmpRow.leftKeyPressStateImage.setImageResource(R.drawable.leftkeyon_300);
				tmpRow.rightKeyPressStateImage.setImageResource(R.drawable.rightkeyon_300);
				tmpRow.reedStateImage.setImageResource(R.drawable.reedrelayon_300);
				break;
			default:
				tmpRow.leftKeyPressStateImage.setImageResource(R.drawable.leftkeyoff_300);
				tmpRow.rightKeyPressStateImage.setImageResource(R.drawable.rightkeyoff_300);
				tmpRow.reedStateImage.setImageResource(R.drawable.reedrelayoff_300);
				break;
			}
			tmpRow.lastKeys = value[0];
		}
	}
    @Override
    public Map<String,String> getMQTTMap() {
        byte[] value = this.dataC.getValue();
        Map<String,String> map = new HashMap<String, String>();
        map.put("key_1",String.format("%d",value[0] & 0x1));
        map.put("key_2",String.format("%d",value[0] & 0x2));
        map.put("reed_relay",String.format("%d",value[0] & 0x4));
        return map;
    }
}
