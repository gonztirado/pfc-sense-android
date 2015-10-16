package com.celulabs.pfcsense.ble.sensortag;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import com.celulabs.pfcsense.ble.common.BluetoothLeService;
import com.celulabs.pfcsense.ble.common.GenericBluetoothProfile;
import com.celulabs.pfcsense.ble.util.GenericCharacteristicTableRow;
import com.celulabs.pfcsense.ble.util.Point3D;
import com.celulabs.pfcsense.controller.SensorDataController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensorTagAmbientTemperatureProfile extends GenericBluetoothProfile {
	public SensorTagAmbientTemperatureProfile(Context con,BluetoothDevice device,BluetoothGattService service,BluetoothLeService controller) {
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
		this.tRow.setIcon(this.getIconPrefix(), this.dataC.getUuid().toString(),"temperature");
		
		//this.tRow.title.setText(GattInfo.uuidToName(UUID.fromString(this.dataC.getUuid().toString())));
        this.tRow.title.setText("Temperatura");
        this.tRow.uuidLabel.setText(this.dataC.getUuid().toString());
		this.tRow.value.setText("0.0'C");
		this.tRow.periodMinVal = 200;
		this.tRow.periodBar.setMax(255 - (this.tRow.periodMinVal / 10));
		this.tRow.periodBar.setProgress(100);
	}
	public void configureService() {
        int error = mBTLeService.writeCharacteristic(this.configC, (byte)0x01);
        if (error != 0) {
            if (this.configC != null)
            Log.d("SensorTagAmbientTemperatureProfile","Sensor config failed: " + this.configC.getUuid().toString() + " Error: " + error);
        }
        error = this.mBTLeService.setCharacteristicNotification(this.dataC, true);
        if (error != 0) {
            if (this.dataC != null)
            Log.d("SensorTagAmbientTemperatureProfile","Sensor notification enable failed: " + this.configC.getUuid().toString() + " Error: " + error);
        }
        /*
		if (mBTLeService.writeCharacteristic(this.configC, (byte)0x01)) {
			mBTLeService.waitIdle(GATT_TIMEOUT);
		} else {
			Log.d("SensorTagAmbientTemperatureProfile","Sensor config failed: " + this.configC.getUuid().toString());
        }
        */

		this.isConfigured = true;
	}
	public void deConfigureService() {
        int error = mBTLeService.writeCharacteristic(this.configC, (byte)0x00);
        if (error != 0) {
            if (this.configC != null)
            Log.d("SensorTagAmbientTemperatureProfile","Sensor config failed: " + this.configC.getUuid().toString() + " Error: " + error);
        }
        error = this.mBTLeService.setCharacteristicNotification(this.dataC, false);
        if (error != 0) {
            if (this.dataC != null)
            Log.d("SensorTagAmbientTemperatureProfile","Sensor notification enable failed: " + this.configC.getUuid().toString() + " Error: " + error);
        }
        this.isConfigured = false;
	}
    @Override
    public void didUpdateValueForCharacteristic(BluetoothGattCharacteristic c) {
        byte[] value = c.getValue();
		if (c.equals(this.dataC)){
			Point3D v = Sensor.IR_TEMPERATURE.convert(value);
            double temperatureValue = v.x;

			if (this.tRow.config == false) { 
				if ((this.isEnabledByPrefs("imperial")) == true) this.tRow.value.setText(String.format("%.1f'F", (temperatureValue * 1.8) + 32));
				else this.tRow.value.setText(String.format("%.1f'C", temperatureValue));
			}
			this.tRow.sl1.addValue((float)temperatureValue);

            /* Añadimos valor de temperatura al controlador */
			SensorDataController.getInstance().addTemperatureValue(temperatureValue);
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
        map.put("ambient_temp",String.format("%.2f",v.x));
        return map;
    }
}
