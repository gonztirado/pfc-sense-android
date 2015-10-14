package com.celulabs.pfcsense.ble.sensortag;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import com.celulabs.pfcsense.ble.common.BluetoothLeService;
import com.celulabs.pfcsense.ble.common.GattInfo;
import com.celulabs.pfcsense.ble.common.GenericBluetoothProfile;
import com.celulabs.pfcsense.ble.util.GenericCharacteristicTableRow;
import com.celulabs.pfcsense.ble.util.Point3D;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SensorTagHumidityProfile extends GenericBluetoothProfile {
		public SensorTagHumidityProfile(Context con,BluetoothDevice device,BluetoothGattService service,BluetoothLeService controller) {
			super(con,device,service,controller);
			this.tRow =  new GenericCharacteristicTableRow(con);
			
			List<BluetoothGattCharacteristic> characteristics = this.mBTService.getCharacteristics();
			
			for (BluetoothGattCharacteristic c : characteristics) {
				if (c.getUuid().toString().equals(SensorTagGatt.UUID_HUM_DATA.toString())) {
					this.dataC = c;
				}
				if (c.getUuid().toString().equals(SensorTagGatt.UUID_HUM_CONF.toString())) {
					this.configC = c;
				}
				if (c.getUuid().toString().equals(SensorTagGatt.UUID_HUM_PERI.toString())) {
					this.periodC = c;
				}
			}
			
			this.tRow.setIcon(this.getIconPrefix(), this.dataC.getUuid().toString());
			
			this.tRow.title.setText(GattInfo.uuidToName(UUID.fromString(this.dataC.getUuid().toString())));
			this.tRow.uuidLabel.setText(this.dataC.getUuid().toString());
			this.tRow.value.setText("0.0%rH");
			this.tRow.periodBar.setProgress(100);
		}
		
		public static boolean isCorrectService(BluetoothGattService service) {
			if ((service.getUuid().toString().compareTo(SensorTagGatt.UUID_HUM_SERV.toString())) == 0) {//service.getUuid().toString().compareTo(SensorTagGatt.UUID_HUM_DATA.toString())) {
				Log.d("Test", "Match !");
				return true;
			}
			else return false;
		}
		public void didWriteValueForCharacteristic(BluetoothGattCharacteristic c) {
			
		}
		public void didReadValueForCharacteristic(BluetoothGattCharacteristic c) {
			
		}
		@Override
        public void didUpdateValueForCharacteristic(BluetoothGattCharacteristic c) {
            byte[] value = c.getValue();
				if (c.equals(this.dataC)){
					Point3D v = Sensor.HUMIDITY.convert(value);
					if (this.tRow.config == false) this.tRow.value.setText(String.format("%.1f %%rH", v.x));
					this.tRow.sl1.maxVal = 100;
					this.tRow.sl1.minVal = 0;
					this.tRow.sl1.addValue((float)v.x);
				}
		}
    @Override
    public Map<String,String> getMQTTMap() {
        Point3D v = Sensor.HUMIDITY.convert(this.dataC.getValue());
        Map<String,String> map = new HashMap<String, String>();
        map.put("humidity",String.format("%.2f",v.x));
        return map;
    }
}
