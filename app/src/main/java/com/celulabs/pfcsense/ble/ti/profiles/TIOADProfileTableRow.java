package com.celulabs.pfcsense.ble.ti.profiles;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.celulabs.pfcsense.ble.util.GenericCharacteristicTableRow;

public class TIOADProfileTableRow extends GenericCharacteristicTableRow {

	public static final String ACTION_VIEW_CLICKED = "com.celulabs.pfcsense.ble.ti.profiles.TIOADProfileTableRow.ACTION_VIEW_CLICKED";

	public TIOADProfileTableRow(Context con) {
		super(con);
	}
	@Override 
	public void onClick(View v) {
		//Override click and launch the OAD screen
        Intent intent = new Intent(ACTION_VIEW_CLICKED);
        this.context.sendBroadcast(intent);
	}
}
