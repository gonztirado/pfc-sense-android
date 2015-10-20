package com.celulabs.pfcsense.ble.sensortag;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.view.View;

import com.celulabs.pfcsense.ble.common.BluetoothLeService;
import com.celulabs.pfcsense.ble.common.GattInfo;
import com.celulabs.pfcsense.ble.common.GenericBluetoothProfile;
import com.celulabs.pfcsense.ble.util.GenericCharacteristicTableRow;
import com.celulabs.pfcsense.ble.util.Point3D;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SensorTagAccelerometerProfile extends GenericBluetoothProfile {
	public SensorTagAccelerometerProfile(Context con,BluetoothDevice device,BluetoothGattService service,BluetoothLeService controller) {
		super(con,device,service,controller);
		this.tRow =  new GenericCharacteristicTableRow(con);
		
		List<BluetoothGattCharacteristic> characteristics = this.mBTService.getCharacteristics();
		for (BluetoothGattCharacteristic c : characteristics) {

            if (c.getUuid().toString().equals(SensorTagGatt.UUID_ACC_DATA.toString())) {
				this.dataC = c;
			}
			if (c.getUuid().toString().equals(SensorTagGatt.UUID_ACC_CONF.toString())) {
				this.configC = c;
			}
			if (c.getUuid().toString().equals(SensorTagGatt.UUID_ACC_PERI.toString())) {
				this.periodC = c;
			}
		}
		
		this.tRow.sl1.autoScale = true;
		this.tRow.sl1.autoScaleBounceBack = true;
		
		this.tRow.sl2.autoScale = true;
		this.tRow.sl2.autoScaleBounceBack = true;
		this.tRow.sl2.setColor(255, 0, 150, 125);
		this.tRow.sl2.setVisibility(View.VISIBLE);
        this.tRow.sl2.setEnabled(true);
		
		this.tRow.sl3.autoScale = true;
		this.tRow.sl3.autoScaleBounceBack = true;
		this.tRow.sl3.setColor(255, 0, 0, 0);
		this.tRow.sl3.setVisibility(View.VISIBLE);
        this.tRow.sl3.setEnabled(true);
		
		this.tRow.setIcon(this.getIconPrefix(), this.dataC.getUuid().toString());
		
		this.tRow.title.setText(GattInfo.uuidToName(UUID.fromString(this.dataC.getUuid().toString())));
		this.tRow.uuidLabel.setText(this.dataC.getUuid().toString());
		this.tRow.value.setText("X:0.00G, Y:0.00G, Z:0.00G");
		
	}
	
	public static boolean isCorrectService(BluetoothGattService service) {
        // FIXME deshabilitado servicio para que no lo descubra
//		if ((service.getUuid().toString().compareTo(SensorTagGatt.UUID_ACC_SERV.toString())) == 0) {
//			return true;
//		}
//		else return false;
        return false;
    }
    @Override
	public void didUpdateValueForCharacteristic(BluetoothGattCharacteristic c) {
			if (c.equals(this.dataC)){
				Point3D v = Sensor.ACCELEROMETER.convert(this.dataC.getValue());
				if (this.tRow.config == false) this.tRow.value.setText(String.format("X:%.2fG, Y:%.2fG, Z:%.2fG", v.x,v.y,v.z));
				this.tRow.sl1.addValue((float)v.x);
				this.tRow.sl2.addValue((float)v.y);
				this.tRow.sl3.addValue((float)v.z);
			}
	}
    @Override
    public Map<String,String> getMQTTMap() {
        Point3D v = Sensor.ACCELEROMETER.convert(this.dataC.getValue());
        Map<String,String> map = new HashMap<String, String>();
        map.put("acc_x", String.format("%.2f", v.x));
        map.put("acc_y",String.format("%.2f",v.y));
        map.put("acc_z",String.format("%.2f",v.z));
        return map;
    }
}
