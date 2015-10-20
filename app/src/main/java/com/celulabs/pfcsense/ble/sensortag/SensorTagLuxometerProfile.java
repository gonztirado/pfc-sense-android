package com.celulabs.pfcsense.ble.sensortag;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import com.celulabs.pfcsense.ble.common.BluetoothLeService;
import com.celulabs.pfcsense.ble.common.GattInfo;
import com.celulabs.pfcsense.ble.common.GenericBluetoothProfile;
import com.celulabs.pfcsense.ble.util.GenericCharacteristicTableRow;
import com.celulabs.pfcsense.ble.util.Point3D;
import com.celulabs.pfcsense.controller.SensorDataController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SensorTagLuxometerProfile extends GenericBluetoothProfile {
		public SensorTagLuxometerProfile(Context con,BluetoothDevice device,BluetoothGattService service,BluetoothLeService controller) {
			super(con,device,service,controller);
			this.tRow =  new GenericCharacteristicTableRow(con);
			
			List<BluetoothGattCharacteristic> characteristics = this.mBTService.getCharacteristics();
			
			for (BluetoothGattCharacteristic c : characteristics) {
				if (c.getUuid().toString().equals(SensorTagGatt.UUID_OPT_DATA.toString())) {
					this.dataC = c;
				}
				if (c.getUuid().toString().equals(SensorTagGatt.UUID_OPT_CONF.toString())) {
					this.configC = c;
				}
				if (c.getUuid().toString().equals(SensorTagGatt.UUID_OPT_PERI.toString())) {
					this.periodC = c;
				}
			}
			
			this.tRow.sl1.autoScale = true;
			this.tRow.sl1.autoScaleBounceBack = true;
			this.tRow.sl1.setColor(255, 0, 150, 125);
			this.tRow.setIcon(this.getIconPrefix(), this.dataC.getUuid().toString());
			
			this.tRow.title.setText(GattInfo.uuidToName(UUID.fromString(this.dataC.getUuid().toString())));
			this.tRow.uuidLabel.setText(this.dataC.getUuid().toString());
			this.tRow.value.setText("0.0 Lux");
			this.tRow.periodBar.setProgress(100);
		}
		
		public static boolean isCorrectService(BluetoothGattService service) {
			if ((service.getUuid().toString().compareTo(SensorTagGatt.UUID_OPT_SERV.toString())) == 0) {
				return true;
			}
			else return false;
		}
    @Override
    public void didUpdateValueForCharacteristic(BluetoothGattCharacteristic c) {
        byte[] value = c.getValue();
				if (c.equals(this.dataC)){
					Point3D v = Sensor.LUXOMETER.convert(value);
					double luxometerValue = v.x;

					if (this.tRow.config == false)
						this.tRow.value.setText(String.format("%.1f Lux", luxometerValue));
					this.tRow.sl1.addValue((float) luxometerValue);

					/* Añadimos valor de temperatura al controlador */
					SensorDataController.getInstance().addLuxometerValue(luxometerValue);
				}
		}
    @Override
    public Map<String,String> getMQTTMap() {
        Point3D v = Sensor.LUXOMETER.convert(this.dataC.getValue());
        Map<String,String> map = new HashMap<String, String>();
        map.put("light",String.format("%.2f",v.x));
        return map;
    }
}
