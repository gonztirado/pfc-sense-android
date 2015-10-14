package com.celulabs.pfcsense.ble.sensortag;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import com.celulabs.pfcsense.ble.common.BluetoothLeService;
import com.celulabs.pfcsense.ble.common.GenericBluetoothProfile;
import com.celulabs.pfcsense.ble.util.GenericCharacteristicTableRow;
import com.celulabs.pfcsense.ble.util.Point3D;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

	public class SensorTagIRTemperatureProfile extends GenericBluetoothProfile {
		public SensorTagIRTemperatureProfile(Context con,BluetoothDevice device,BluetoothGattService service,BluetoothLeService controller) {
			super(con,device,service,controller);
			this.tRow =  new GenericCharacteristicTableRow(con);
			
			List<BluetoothGattCharacteristic> characteristics = this.mBTService.getCharacteristics();
			
			for (BluetoothGattCharacteristic c : characteristics) {
				if (c.getUuid().toString().equals(SensorTagGatt.UUID_IRT_DATA.toString())) {
					this.dataC = c;
				}
				if (c.getUuid().toString().equals(SensorTagGatt.UUID_IRT_CONF.toString())) {
					this.configC = c;
				}
				if (c.getUuid().toString().equals(SensorTagGatt.UUID_IRT_PERI.toString())) {
					this.periodC = c;
				}
			}
			this.tRow.sl1.autoScale = true;
			this.tRow.sl1.autoScaleBounceBack = true;
			this.tRow.setIcon(this.getIconPrefix(), this.dataC.getUuid().toString());

			this.tRow.title.setText("Temperatura IR");
			this.tRow.uuidLabel.setText(this.dataC.getUuid().toString());
			this.tRow.value.setText("0.0'C");
			this.tRow.periodMinVal = 200;
			this.tRow.periodBar.setMax(255 - (this.tRow.periodMinVal / 10));
			this.tRow.periodBar.setProgress(100);
		}
        @Override
        public void didUpdateValueForCharacteristic(BluetoothGattCharacteristic c) {
            byte[] value = c.getValue();
			if (c.equals(this.dataC)){
				if (this.mBTDevice.getName().equals("CC2650 SensorTag")) {
					Point3D v = Sensor.IR_TEMPERATURE.convert(value);
					if ((this.isEnabledByPrefs("imperial")) == true) this.tRow.value.setText(String.format("%.1f'F", (v.z * 1.8) + 32));
					else this.tRow.value.setText(String.format("%.1f'C", v.z));
					this.tRow.sl1.addValue((float)v.z);
				}
				else {
					Point3D v = Sensor.IR_TEMPERATURE.convert(value);
					if (this.tRow.config == false) {
						if ((this.isEnabledByPrefs("imperial")) == true) this.tRow.value.setText(String.format("%.1f'F", (v.y * 1.8) + 32));
						else this.tRow.value.setText(String.format("%.1f'C", v.y));
					}	
					this.tRow.sl1.addValue((float)v.y); 
				}
			}
		}
		public static boolean isCorrectService(BluetoothGattService service) {
			if ((service.getUuid().toString().compareTo(SensorTagGatt.UUID_IRT_SERV.toString())) == 0) {
				return true;
			}
			else return false;
		}
        @Override
        public Map<String,String> getMQTTMap() {
            Point3D v = Sensor.IR_TEMPERATURE.convert(this.dataC.getValue());
            Map<String,String> map = new HashMap<String, String>();
            if (this.mBTDevice.getName().equals("CC2650 SensorTag")) {
                map.put("object_temp", String.format("%.2f", v.z));
            }
            else {
                map.put("object_temp", String.format("%.2f", v.y));
            }
            return map;
        }
	}
