package com.celulabs.pfcsense.ble.ti.profiles;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;

import com.celulabs.pfcsense.ble.common.BluetoothLeService;
import com.celulabs.pfcsense.ble.common.GenericBluetoothProfile;

import java.util.List;

public class TIOADProfile extends GenericBluetoothProfile {
	private static final String oadService_UUID = "f000ffc0-0451-4000-b000-000000000000";
	private static final String oadImageNotify_UUID = "f000ffc1-0451-4000-b000-000000000000";
	private static final String oadBlockRequest_UUID = "f000ffc2-0451-4000-b000-000000000000";

    public static final String ACTION_PREPARE_FOR_OAD = "com.example.ti.ble.ti.profiles.ACTION_PREPARE_FOR_OAD";
    public static final String ACTION_RESTORE_AFTER_OAD = "com.example.ti.ble.ti.profiles.ACTION_RESTORE_AFTER_OAD";


	private String fwRev;
    private BroadcastReceiver brRecv;
    private boolean clickReceiverRegistered = false;
	
	public TIOADProfile(Context con,BluetoothDevice device,BluetoothGattService service,BluetoothLeService controller) {
		super(con,device,service,controller);
		this.tRow =  new TIOADProfileTableRow(con);
		
		List<BluetoothGattCharacteristic> characteristics = this.mBTService.getCharacteristics();
		
		for (BluetoothGattCharacteristic c : characteristics) {
			if (c.getUuid().toString().equals(oadImageNotify_UUID)) {
				this.dataC = c;
			}
			if (c.getUuid().toString().equals(oadBlockRequest_UUID)) {
				this.configC = c;
			}
		}
		tRow.title.setText("TI OAD Service");
		tRow.sl1.setVisibility(View.INVISIBLE);
		this.tRow.setIcon(this.getIconPrefix(), service.getUuid().toString());

        brRecv = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (TIOADProfileTableRow.ACTION_VIEW_CLICKED.equals(intent.getAction())) {
                    Log.d("TIOADProfile","SHOW OAD DIALOG !");
                    prepareForOAD();
                }
            }
        };
        this.context.registerReceiver(brRecv,makeIntentFilter());
        this.clickReceiverRegistered = true;
	}

    @Override
    public void onResume() {
        super.onResume();
        if (!this.clickReceiverRegistered) {
            brRecv = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (TIOADProfileTableRow.ACTION_VIEW_CLICKED.equals(intent.getAction())) {
                        Log.d("TIOADProfile", "SHOW OAD DIALOG !");
                        prepareForOAD();
                    }
                }
            };

            this.context.registerReceiver(brRecv, makeIntentFilter());
            this.clickReceiverRegistered = true;
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (this.clickReceiverRegistered) {
            this.context.unregisterReceiver(brRecv);
            this.clickReceiverRegistered = false;
        }
    }
    public static boolean isCorrectService(BluetoothGattService service) {
        // FIXME deshabilitado servicio para que no lo descubra
//		if ((service.getUuid().toString().compareTo(oadService_UUID)) == 0) {
//			return true;
//		}
//		else return false;
        return false;
    }

    public void prepareForOAD () {
        //Override click and launch the OAD screen
        Intent intent = new Intent(ACTION_PREPARE_FOR_OAD);
        context.sendBroadcast(intent);

    }

	@Override
	public void enableService() {
	
	}
	@Override 
	public void disableService() {
		
	}
	@Override
	public void configureService() {
		
	}
	@Override
	public void deConfigureService() {
		
	}
    @Override
    public void periodWasUpdated(int period) {

    }
	@Override
	public void didUpdateFirmwareRevision(String firmwareRev) {
		this.fwRev = firmwareRev;
		this.tRow.value.setText("Current FW :" + firmwareRev); 
	}

    private static IntentFilter makeIntentFilter() {
        final IntentFilter fi = new IntentFilter();
        fi.addAction(TIOADProfileTableRow.ACTION_VIEW_CLICKED);
        return fi;
    }

}
