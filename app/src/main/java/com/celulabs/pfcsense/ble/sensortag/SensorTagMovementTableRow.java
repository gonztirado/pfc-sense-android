package com.celulabs.pfcsense.ble.sensortag;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.celulabs.pfcsense.ble.util.GenericCharacteristicTableRow;
import com.celulabs.pfcsense.ble.util.SparkLineView;

public class SensorTagMovementTableRow extends GenericCharacteristicTableRow {
	public final SparkLineView sl4,sl5,sl6;
	public final SparkLineView sl7,sl8,sl9;
	public final TextView gyroValue;
	public final TextView magValue;
	
	public SensorTagMovementTableRow(Context con) {
		super(con);

		this.sl1.autoScale = this.sl2.autoScale = this.sl3.autoScale = true;
		this.sl1.autoScaleBounceBack = this.sl2.autoScaleBounceBack = this.sl3.autoScaleBounceBack = true;
		this.sl2.setVisibility(View.VISIBLE);
		this.sl3.setVisibility(View.VISIBLE);	
		this.sl2.setEnabled(true);
		this.sl3.setEnabled(true);
		this.sl2.setColor(255, 0, 150, 125);
		this.sl3.setColor(255, 0, 0, 0);

		this.sl2.autoScale = true;
		this.sl3.autoScale = true;
		this.sl2.autoScaleBounceBack = true;
		this.sl3.autoScaleBounceBack = true;
		
		//One Sparkline showing Gyroscope trends
		this.sl4 = new SparkLineView(con) {
			{
				setVisibility(View.VISIBLE);
				autoScale = true;
				autoScaleBounceBack = true;
				setId(6);
			}
		};
		this.sl5 = new SparkLineView(con) {
			{
				setVisibility(View.VISIBLE);
				autoScale = true;
				autoScaleBounceBack = true;
				setColor(255, 0, 150, 125);
				setId(7);
			}
		};
		this.sl6 = new SparkLineView(con) {
			{
				setVisibility(View.VISIBLE);
				autoScale = true;
				autoScaleBounceBack = true;
				setColor(255, 0, 0, 0);
				setId(8);
			}
		};
		//Three Sparkline showing Magnetometer trends
		this.sl7 = new SparkLineView(con) {
			{
				setVisibility(View.VISIBLE);
				autoScale = true;
				autoScaleBounceBack = true;
				setId(9);
			}
		};
		this.sl8 = new SparkLineView(con) {
			{
				setVisibility(View.VISIBLE);
				setColor(255, 0, 150, 125);
				autoScale = true;
				autoScaleBounceBack = true;
				setId(10);
			}
		};
		this.sl9 = new SparkLineView(con) {
			{
				setVisibility(View.VISIBLE);
				setColor(255, 0, 0, 0);
				autoScale = true;
				autoScaleBounceBack = true;
				setId(11);
			}
		};
		this.gyroValue = new TextView(con) {
			{
				setTextSize(TypedValue.COMPLEX_UNIT_PT,8.0f);
				setTextAlignment(TEXT_ALIGNMENT_VIEW_END);
				setId(12);
				setVisibility(View.VISIBLE);
			}
		};
		this.magValue = new TextView(con) {
			{
				setTextSize(TypedValue.COMPLEX_UNIT_PT,8.0f);
				setTextAlignment(TEXT_ALIGNMENT_VIEW_END);
				setId(13);
				setVisibility(View.VISIBLE);
			}
		};
		
		RelativeLayout.LayoutParams tmpLayoutParams = new RelativeLayout.LayoutParams(
		        RelativeLayout.LayoutParams.MATCH_PARENT,
		        RelativeLayout.LayoutParams.MATCH_PARENT);
        tmpLayoutParams.addRule(RelativeLayout.BELOW,
		        this.sl3.getId());
        tmpLayoutParams.addRule(RelativeLayout.RIGHT_OF,icon.getId());
		gyroValue.setLayoutParams(tmpLayoutParams);

        tmpLayoutParams = new RelativeLayout.LayoutParams(
		        RelativeLayout.LayoutParams.MATCH_PARENT,
		        RelativeLayout.LayoutParams.MATCH_PARENT);
        tmpLayoutParams.addRule(RelativeLayout.BELOW,
				gyroValue.getId());
        tmpLayoutParams.addRule(RelativeLayout.RIGHT_OF, icon.getId());
		this.sl4.setLayoutParams(tmpLayoutParams);
		this.sl5.setLayoutParams(tmpLayoutParams);
		this.sl6.setLayoutParams(tmpLayoutParams);

        tmpLayoutParams = new RelativeLayout.LayoutParams(
		        RelativeLayout.LayoutParams.MATCH_PARENT,
		        RelativeLayout.LayoutParams.MATCH_PARENT);
        tmpLayoutParams.addRule(RelativeLayout.BELOW,
		        this.sl6.getId());
        tmpLayoutParams.addRule(RelativeLayout.RIGHT_OF,icon.getId());
		magValue.setLayoutParams(tmpLayoutParams);

        tmpLayoutParams = new RelativeLayout.LayoutParams(
		        RelativeLayout.LayoutParams.MATCH_PARENT,
		        RelativeLayout.LayoutParams.MATCH_PARENT);
        tmpLayoutParams.addRule(RelativeLayout.BELOW,
				magValue.getId());
        tmpLayoutParams.addRule(RelativeLayout.RIGHT_OF, icon.getId());
		this.sl7.setLayoutParams(tmpLayoutParams);
		this.sl8.setLayoutParams(tmpLayoutParams);
		this.sl9.setLayoutParams(tmpLayoutParams);
		
		
		rowLayout.addView(gyroValue);
		rowLayout.addView(this.sl4);
		rowLayout.addView(this.sl5);
		rowLayout.addView(this.sl6);
		
		rowLayout.addView(magValue);
		rowLayout.addView(this.sl7);
		rowLayout.addView(this.sl8);
		rowLayout.addView(this.sl9);
		
		
		
		
		
	}
	@Override
	public void onClick(View v) {
		this.config = !this.config;
		Log.d("onClick","Row ID" + v.getId());
		//Toast.makeText(this.context, "Found row with title : " + this.title.getText(), Toast.LENGTH_SHORT).show();
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
			this.sl2.startAnimation(fadeOut);
			this.sl3.startAnimation(fadeOut);
			this.sl4.startAnimation(fadeOut);
			this.sl5.startAnimation(fadeOut);
			this.sl6.startAnimation(fadeOut);
			this.sl7.startAnimation(fadeOut);
			this.sl8.startAnimation(fadeOut);
			this.sl9.startAnimation(fadeOut);
			this.value.startAnimation(fadeOut);
			this.gyroValue.startAnimation(fadeOut);
			this.magValue.startAnimation(fadeOut);
			this.onOffLegend.startAnimation(fadeIn);
			this.onOff.startAnimation(fadeIn);
			this.periodLegend.startAnimation(fadeIn);
			this.periodBar.startAnimation(fadeIn);
		}
		else {
			this.sl1.startAnimation(fadeIn);
			this.sl1.startAnimation(fadeIn);
			this.sl2.startAnimation(fadeIn);
			this.sl3.startAnimation(fadeIn);
			this.sl4.startAnimation(fadeIn);
			this.sl5.startAnimation(fadeIn);
			this.sl6.startAnimation(fadeIn);
			this.sl7.startAnimation(fadeIn);
			this.sl8.startAnimation(fadeIn);
			this.sl9.startAnimation(fadeIn);
			this.value.startAnimation(fadeIn);
			this.gyroValue.startAnimation(fadeIn);
			this.magValue.startAnimation(fadeIn);
			this.onOffLegend.startAnimation(fadeOut);
			this.onOff.startAnimation(fadeOut);
			this.periodLegend.startAnimation(fadeOut);
			this.periodBar.startAnimation(fadeOut);
		}
		
		
	}
	@Override 
	public void onAnimationStart (Animation animation) {
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if (this.config == true) {
			this.sl1.setVisibility(View.INVISIBLE);
			this.sl2.setVisibility(View.INVISIBLE);
			this.sl3.setVisibility(View.INVISIBLE);
			this.sl4.setVisibility(View.INVISIBLE);
			this.sl5.setVisibility(View.INVISIBLE);
			this.sl6.setVisibility(View.INVISIBLE);
			this.sl7.setVisibility(View.INVISIBLE);
			this.sl8.setVisibility(View.INVISIBLE);
			this.sl9.setVisibility(View.INVISIBLE);
			this.onOff.setVisibility(View.VISIBLE);
			this.onOffLegend.setVisibility(View.VISIBLE);
			this.periodBar.setVisibility(View.VISIBLE);
			this.periodLegend.setVisibility(View.VISIBLE);
			this.gyroValue.setVisibility(View.INVISIBLE);
			this.magValue.setVisibility(View.INVISIBLE);
			this.value.setVisibility(View.INVISIBLE);
		}
		else {
			this.sl1.setVisibility(View.VISIBLE);
			this.sl2.setVisibility(View.VISIBLE);
			this.sl3.setVisibility(View.VISIBLE);
			this.sl4.setVisibility(View.VISIBLE);
			this.sl5.setVisibility(View.VISIBLE);
			this.sl6.setVisibility(View.VISIBLE);
			this.sl7.setVisibility(View.VISIBLE);
			this.sl8.setVisibility(View.VISIBLE);
			this.sl9.setVisibility(View.VISIBLE);
			this.gyroValue.setVisibility(View.VISIBLE);
			this.magValue.setVisibility(View.VISIBLE);
			this.value.setVisibility(View.VISIBLE);
			this.onOff.setVisibility(View.INVISIBLE);
			this.onOffLegend.setVisibility(View.INVISIBLE);
			this.periodBar.setVisibility(View.INVISIBLE);
			this.periodLegend.setVisibility(View.INVISIBLE);
		}
	}
}
