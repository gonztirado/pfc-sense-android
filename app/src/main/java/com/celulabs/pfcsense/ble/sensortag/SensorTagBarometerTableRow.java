package com.celulabs.pfcsense.ble.sensortag;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.celulabs.pfcsense.ble.util.GenericCharacteristicTableRow;

public class SensorTagBarometerTableRow extends GenericCharacteristicTableRow {
	public SensorTagBarometerTableRow(Context con) {
		super(con);
		this.calibrateButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		if (v.equals(this.calibrateButton)) {
			this.calibrationButtonTouched();
			return;
		}
		this.config = !this.config;
		Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
		fadeOut.setAnimationListener(this);
		fadeOut.setDuration(500);
		fadeOut.setStartOffset(0);
		Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
		fadeIn.setAnimationListener(this);
		fadeIn.setDuration(500);
		fadeIn.setStartOffset(250);
		if (this.config == true) {
			this.sl1.startAnimation(fadeOut);
			if ((this.sl2.isEnabled()))this.sl2.startAnimation(fadeOut);
			if ((this.sl3.isEnabled()))this.sl3.startAnimation(fadeOut);
			this.value.startAnimation(fadeOut);
			this.onOffLegend.startAnimation(fadeIn);
			this.onOff.startAnimation(fadeIn);
			this.periodLegend.startAnimation(fadeIn);
			this.periodBar.startAnimation(fadeIn);
			this.calibrateButton.startAnimation(fadeIn);
		}
		else {
			this.sl1.startAnimation(fadeIn);
			if ((this.sl2.isEnabled()))this.sl2.startAnimation(fadeIn);
			if ((this.sl3.isEnabled()))this.sl3.startAnimation(fadeIn);
			this.value.startAnimation(fadeIn);
			this.onOffLegend.startAnimation(fadeOut);
			this.onOff.startAnimation(fadeOut);
			this.periodLegend.startAnimation(fadeOut);
			this.periodBar.startAnimation(fadeOut);
			this.calibrateButton.startAnimation(fadeOut);
		}
		
		
	}
	@Override 
	public void onAnimationStart (Animation animation) {
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if (this.config == true) {
			this.sl1.setVisibility(View.INVISIBLE);
			if ((this.sl2.isEnabled()))this.sl2.setVisibility(View.INVISIBLE);
			if ((this.sl3.isEnabled()))this.sl3.setVisibility(View.INVISIBLE);
			this.onOff.setVisibility(View.VISIBLE);
			this.onOffLegend.setVisibility(View.VISIBLE);
			this.periodBar.setVisibility(View.VISIBLE);
			this.periodLegend.setVisibility(View.VISIBLE);
			this.calibrateButton.setVisibility(View.VISIBLE);
		}
		else {
			this.sl1.setVisibility(View.VISIBLE);
			if ((this.sl2.isEnabled()))this.sl2.setVisibility(View.VISIBLE);
			if ((this.sl3.isEnabled()))this.sl3.setVisibility(View.VISIBLE);
			this.onOff.setVisibility(View.INVISIBLE);
			this.onOffLegend.setVisibility(View.INVISIBLE);
			this.periodBar.setVisibility(View.INVISIBLE);
			this.periodLegend.setVisibility(View.INVISIBLE);
			this.calibrateButton.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		
	}
	public void calibrationButtonTouched() {
		final Intent intent = new Intent(ACTION_CALIBRATE);
		intent.putExtra(EXTRA_SERVICE_UUID, this.uuidLabel.getText());
		this.context.sendBroadcast(intent);
	}

    @Override
    public void grayedOut(boolean gray) {
        super.grayedOut(gray);
        if (gray) {
            calibrateButton.setAlpha(0.4f);
        }
        else {
            calibrateButton.setAlpha(1.0f);
        }
    }
}
