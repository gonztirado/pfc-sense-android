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
import com.celulabs.pfcsense.controller.SensorDataController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SensorTagBarometerProfile extends GenericBluetoothProfile {
	private BluetoothGattCharacteristic calibC;
	private boolean isCalibrated;
	private boolean isHeightCalibrated;
	private static final double PA_PER_METER = 12.0;
	public SensorTagBarometerProfile(Context con,BluetoothDevice device,BluetoothGattService service,BluetoothLeService controller) {
		super(con,device,service,controller);
		this.tRow =  new SensorTagBarometerTableRow(con);
		
		List<BluetoothGattCharacteristic> characteristics = this.mBTService.getCharacteristics();
		
		for (BluetoothGattCharacteristic c : characteristics) {
			if (c.getUuid().toString().equals(SensorTagGatt.UUID_BAR_DATA.toString())) {
				this.dataC = c;
			}
			if (c.getUuid().toString().equals(SensorTagGatt.UUID_BAR_CONF.toString())) {
				this.configC = c;
			}
			if (c.getUuid().toString().equals(SensorTagGatt.UUID_BAR_PERI.toString())) {
				this.periodC = c;
			}
			if (c.getUuid().toString().equals(SensorTagGatt.UUID_BAR_CALI.toString())) {
				this.calibC = c;
			}
		}
		if (this.mBTDevice.getName().equals("CC2650 SensorTag")) {
			this.isCalibrated = true;
		}
		else {
			this.isCalibrated = false;
		}
		this.isHeightCalibrated = false;
		this.tRow.sl1.autoScale = true;
		this.tRow.sl1.autoScaleBounceBack = true;
		this.tRow.sl1.setColor(255, 0, 150, 125);
		this.tRow.setIcon(this.getIconPrefix(), this.dataC.getUuid().toString());
		
		this.tRow.title.setText(GattInfo.uuidToName(UUID.fromString(this.dataC.getUuid().toString())));
		this.tRow.uuidLabel.setText(this.dataC.getUuid().toString());
		this.tRow.value.setText("0.0mBar, 0.0m");
		this.tRow.periodBar.setProgress(100);
	}
	
	public static boolean isCorrectService(BluetoothGattService service) {
		if ((service.getUuid().toString().compareTo(SensorTagGatt.UUID_BAR_SERV.toString())) == 0) {
			return true;
		}
		else return false;
	}
	public void enableService() {
		while (!(mBTLeService.checkGatt())) {
			mBTLeService.waitIdle(GATT_TIMEOUT);
		}
		if (!(this.isCalibrated)) {
			// Write the calibration code to the configuration registers
			mBTLeService.writeCharacteristic(this.configC, Sensor.CALIBRATE_SENSOR_CODE);
			mBTLeService.waitIdle(GATT_TIMEOUT);
			mBTLeService.readCharacteristic(this.calibC);
			mBTLeService.waitIdle(GATT_TIMEOUT);
		}
		else {
            int error = mBTLeService.writeCharacteristic(this.configC, (byte)0x01);
            if (error != 0) {
                if (this.configC != null)
                Log.d("SensorTagBarometerProfile","Sensor config failed: " + this.configC.getUuid().toString() + " Error: " + error);
            }
            error = this.mBTLeService.setCharacteristicNotification(this.dataC, true);
            if (error != 0) {
                if (this.dataC != null)
                Log.d("SensorTagBarometerProfile","Sensor notification enable failed: " + this.configC.getUuid().toString() + " Error: " + error);
            }
		}
        this.isEnabled = true;

	}
	@Override
	public void didReadValueForCharacteristic(BluetoothGattCharacteristic c) {
        if (this.calibC != null) {
            if (this.calibC.equals(c)) {
                //We have been calibrated
                // Sanity check
                byte[] value = c.getValue();
                if (value.length != 16) {
                    return;
                }

                // Barometer calibration values are read.
                List<Integer> cal = new ArrayList<Integer>();
                for (int offset = 0; offset < 8; offset += 2) {
                    Integer lowerByte = (int) value[offset] & 0xFF;
                    Integer upperByte = (int) value[offset + 1] & 0xFF;
                    cal.add((upperByte << 8) + lowerByte);
                }

                for (int offset = 8; offset < 16; offset += 2) {
                    Integer lowerByte = (int) value[offset] & 0xFF;
                    Integer upperByte = (int) value[offset + 1];
                    cal.add((upperByte << 8) + lowerByte);
                }
                Log.d("SensorTagBarometerProfile", "Barometer calibrated !!!!!");
                BarometerCalibrationCoefficients.INSTANCE.barometerCalibrationCoefficients = cal;
                this.isCalibrated = true;
                int error = mBTLeService.writeCharacteristic(this.configC, (byte)0x01);
                if (error != 0) {
                    if (this.configC != null)
                    Log.d("SensorTagBarometerProfile","Sensor config failed: " + this.configC.getUuid().toString() + " Error: " + error);
                }
                error = this.mBTLeService.setCharacteristicNotification(this.dataC, true);
                if (error != 0) {
                    if (this.dataC != null)
                    Log.d("SensorTagBarometerProfile","Sensor notification enable failed: " + this.configC.getUuid().toString() + " Error: " + error);
                }
            }
        }
	}
	@Override 
	public void didUpdateValueForCharacteristic(BluetoothGattCharacteristic c) {
        byte[] value = c.getValue();
		if (c.equals(this.dataC)){
			Point3D v;
			v = Sensor.BAROMETER.convert(value);
			double barometerValue = v.x;

			if (!(this.isHeightCalibrated)) {
				BarometerCalibrationCoefficients.INSTANCE.heightCalibration = barometerValue;
				//Toast.makeText(this.tRow.getContext(), "Height measurement calibrated",
				//			    Toast.LENGTH_SHORT).show();
				this.isHeightCalibrated = true;
			}
			double h = (barometerValue - BarometerCalibrationCoefficients.INSTANCE.heightCalibration)
					/ PA_PER_METER;
			h = (double) Math.round(-h * 10.0) / 10.0;
			//msg = decimal.format(barometerValue / 100.0f) + "\n" + h;
			if (this.tRow.config == false)
				this.tRow.value.setText(String.format("%.1f mBar %.1f meter", barometerValue / 100, h));
			this.tRow.sl1.addValue((float) barometerValue / 100.0f);
			//mBarValue.setText(msg);

			/* Añadimos valor de presión barométrica al controlador */
			SensorDataController.getInstance().addBarometerValue(barometerValue);
		}
	}
	
	protected void calibrationButtonTouched() {
		this.isHeightCalibrated = false;
	}
    @Override
    public Map<String,String> getMQTTMap() {
        Point3D v = Sensor.BAROMETER.convert(this.dataC.getValue());
        Map<String,String> map = new HashMap<String, String>();
        map.put("air_pressure",String.format("%.2f",v.x / 100));
        return map;
    }
}
