package com.celulabs.pfcsense.ble.sensortag;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import com.celulabs.pfcsense.ble.common.BluetoothLeService;
import com.celulabs.pfcsense.ble.common.GattInfo;
import com.celulabs.pfcsense.ble.common.GenericBluetoothProfile;
import com.celulabs.pfcsense.ble.util.Point3D;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SensorTagMovementProfile extends GenericBluetoothProfile {
	
	public SensorTagMovementProfile(Context con,BluetoothDevice device,BluetoothGattService service,BluetoothLeService controller) {
		super(con,device,service,controller);
		this.tRow =  new SensorTagMovementTableRow(con);
		
		List<BluetoothGattCharacteristic> characteristics = this.mBTService.getCharacteristics();
		
		for (BluetoothGattCharacteristic c : characteristics) {
			if (c.getUuid().toString().equals(SensorTagGatt.UUID_MOV_DATA.toString())) {
				this.dataC = c;
			}
			if (c.getUuid().toString().equals(SensorTagGatt.UUID_MOV_CONF.toString())) {
				this.configC = c;
			}
			if (c.getUuid().toString().equals(SensorTagGatt.UUID_MOV_PERI.toString())) {
				this.periodC = c;
			}
		}
		
		
		this.tRow.setIcon(this.getIconPrefix(), this.dataC.getUuid().toString());
		
		this.tRow.title.setText(GattInfo.uuidToName(UUID.fromString(this.dataC.getUuid().toString())));
		this.tRow.uuidLabel.setText(this.dataC.getUuid().toString());
		this.tRow.value.setText("X:0.00G, Y:0.00G, Z:0.00G");
		SensorTagMovementTableRow row = (SensorTagMovementTableRow)this.tRow;
		
		row.gyroValue.setText("X:0.00'/s, Y:0.00'/s, Z:0.00'/s");
		row.magValue.setText("X:0.00mT, Y:0.00mT, Z:0.00mT");
		this.tRow.periodBar.setProgress(100);
	}
	
	public static boolean isCorrectService(BluetoothGattService service) {
        // FIXME deshabilitado servicio para que no lo descubra
//		if ((service.getUuid().toString().compareTo(SensorTagGatt.UUID_MOV_SERV.toString())) == 0) {
//			return true;
//		}
//		else return false;
        return false;
    }
	@Override 
	public void enableService() {
        int error = mBTLeService.writeCharacteristic(this.configC, new byte[] {0x7F,0x02});
        if (error != 0) {
            if (this.configC != null)
            Log.d("SensorTagMovementProfile","Sensor config failed: " + this.configC.getUuid().toString() + " Error: " + error);
        }
        error = this.mBTLeService.setCharacteristicNotification(this.dataC, true);
        if (error != 0) {
            if (this.dataC != null)
            Log.d("SensorTagMovementProfile","Sensor notification enable failed: " + this.configC.getUuid().toString() + " Error: " + error);
        }

		this.periodWasUpdated(1000);
        this.isEnabled = true;
	}
	@Override 
	public void disableService() {
        int error = mBTLeService.writeCharacteristic(this.configC, new byte[] {0x00,0x00});
        if (error != 0) {
            if (this.configC != null)
            Log.d("SensorTagMovementProfile","Sensor config failed: " + this.configC.getUuid().toString() + " Error: " + error);
        }
        error = this.mBTLeService.setCharacteristicNotification(this.dataC, false);
        if (error != 0) {
            if (this.dataC != null)
            Log.d("SensorTagMovementProfile","Sensor notification disable failed: " + this.configC.getUuid().toString() + " Error: " + error);
        }
        this.isEnabled = false;
	}
	public void didWriteValueForCharacteristic(BluetoothGattCharacteristic c) {
		
	}
	public void didReadValueForCharacteristic(BluetoothGattCharacteristic c) {
		
	}
	@Override
    public void didUpdateValueForCharacteristic(BluetoothGattCharacteristic c) {
        byte[] value = c.getValue();
			if (c.equals(this.dataC)){
				Point3D v;
				v = Sensor.MOVEMENT_ACC.convert(value);
				if (this.tRow.config == false) this.tRow.value.setText(String.format("X:%.2fG, Y:%.2fG, Z:%.2fG", v.x,v.y,v.z));
				this.tRow.sl1.addValue((float)v.x);
				this.tRow.sl2.addValue((float)v.y);
				this.tRow.sl3.addValue((float)v.z);
				v = Sensor.MOVEMENT_GYRO.convert(value);
				SensorTagMovementTableRow row = (SensorTagMovementTableRow)this.tRow;
				row.gyroValue.setText(String.format("X:%.2f'/s, Y:%.2f'/s, Z:%.2f'/s", v.x,v.y,v.z));
				row.sl4.addValue((float)v.x);
				row.sl5.addValue((float)v.y);
				row.sl6.addValue((float)v.z);
				v = Sensor.MOVEMENT_MAG.convert(value);
				row.magValue.setText(String.format("X:%.2fuT, Y:%.2fuT, Z:%.2fuT", v.x,v.y,v.z));
				row.sl7.addValue((float)v.x);
				row.sl8.addValue((float)v.y);
				row.sl9.addValue((float)v.z);
			}
	}
    @Override
    public Map<String,String> getMQTTMap() {
        Point3D v = Sensor.MOVEMENT_ACC.convert(this.dataC.getValue());
        Map<String,String> map = new HashMap<String, String>();
        map.put("acc_x",String.format("%.2f",v.x));
        map.put("acc_y",String.format("%.2f",v.y));
        map.put("acc_z",String.format("%.2f",v.z));
        v = Sensor.MOVEMENT_GYRO.convert(this.dataC.getValue());
        map.put("gyro_x",String.format("%.2f",v.x));
        map.put("gyro_y",String.format("%.2f",v.y));
        map.put("gyro_z",String.format("%.2f",v.z));
        v = Sensor.MOVEMENT_MAG.convert(this.dataC.getValue());
        map.put("compass_x",String.format("%.2f",v.x));
        map.put("compass_y",String.format("%.2f",v.y));
        map.put("compass_z",String.format("%.2f",v.z));
        return map;
    }
}
