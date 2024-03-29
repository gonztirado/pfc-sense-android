package com.celulabs.pfcsense.ble.sensortag;

import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.celulabs.pfcsense.ble.util.GenericCharacteristicTableRow;

import java.util.Timer;
import java.util.TimerTask;

public class SensorTagSimpleKeysTableRow extends GenericCharacteristicTableRow {
	protected byte lastKeys;
	protected ImageView leftKeyPressStateImage;
	protected ImageView rightKeyPressStateImage;
	protected ImageView reedStateImage;
	protected updateSparkLinesTimerTask sparkLineUpdateTask;
	protected Timer sparkLineUpdateTimer;
	
	public SensorTagSimpleKeysTableRow(Context con) {
		super(con);
		this.periodBar.setEnabled(false);
		this.periodLegend.setText("Sensor period (\"Notification\")");
		this.sl1.maxVal = 1.0f;
		this.sl1.setColor(255, 255, 0, 0);
		this.sl2.maxVal = 1.0f;
		this.sl2.setColor(255, 17, 136, 153);
		this.sl2.setVisibility(View.VISIBLE);
		this.sl3.maxVal = 1.0f;
		this.sl3.setColor(255, 0, 0, 0);
		this.sl3.setVisibility(View.VISIBLE);
		this.sl2.setEnabled(true);
		this.sl3.setEnabled(true);
		this.value.setVisibility(View.INVISIBLE);
		final int nextGuiId = this.sl3.getId();
		this.leftKeyPressStateImage = new ImageView(con) {
			{
				setId(nextGuiId + 1);
			}
		};
		this.leftKeyPressStateImage.setImageResource(R.drawable.leftkeyoff_300);
		this.rightKeyPressStateImage = new ImageView(con) {
			{
				setId(nextGuiId + 2);
			}
		};
		this.rightKeyPressStateImage.setImageResource(R.drawable.rightkeyoff_300);
		this.reedStateImage = new ImageView(con) {
			{
				setId(nextGuiId + 3);
			}
		};
		this.reedStateImage.setImageResource(R.drawable.reedrelayoff_300);
		
		
		
		//Setup layout for all cell elements
		RelativeLayout.LayoutParams iconItemParams = new RelativeLayout.LayoutParams(
						210,
						180) {
					{
						addRule(RelativeLayout.RIGHT_OF,
								icon.getId());
						addRule(RelativeLayout.BELOW, title.getId());
					}
					
				};
				leftKeyPressStateImage.setLayoutParams(iconItemParams);
				leftKeyPressStateImage.setPadding(20, 20, 20, 20);
		
				iconItemParams = new RelativeLayout.LayoutParams(
						160,
						160) {
					{
						addRule(RelativeLayout.RIGHT_OF,
								leftKeyPressStateImage.getId());
						addRule(RelativeLayout.BELOW, title.getId());
					}
					
				};
				rightKeyPressStateImage.setPadding(10, 10, 10, 10);
				rightKeyPressStateImage.setLayoutParams(iconItemParams);	
		iconItemParams = new RelativeLayout.LayoutParams(
				160,
				160) {
			{
				addRule(RelativeLayout.RIGHT_OF,
						rightKeyPressStateImage.getId());
				addRule(RelativeLayout.BELOW, title.getId());
			}
			
		};
		reedStateImage.setLayoutParams(iconItemParams);
		reedStateImage.setPadding(10, 10, 10, 10);
		
		
		//Move sparkLines below the state images
		
		iconItemParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT) {
			{
				addRule(RelativeLayout.RIGHT_OF,
						icon.getId());
				addRule(RelativeLayout.BELOW, reedStateImage.getId());
			}
			
		};
		
		this.sl1.setLayoutParams(iconItemParams);
		this.sl2.setLayoutParams(iconItemParams);
		this.sl3.setLayoutParams(iconItemParams);
		
		
		this.rowLayout.addView(leftKeyPressStateImage);
		this.rowLayout.addView(rightKeyPressStateImage);
		this.rowLayout.addView(reedStateImage);
		
		this.sparkLineUpdateTimer = new Timer();
		this.sparkLineUpdateTask = new updateSparkLinesTimerTask(this);
		this.sparkLineUpdateTimer.scheduleAtFixedRate(this.sparkLineUpdateTask, 1000, 100);
		
	}
	@Override
	public void onClick(View v) {
		super.onClick(v);
		Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
		fadeOut.setAnimationListener(this);
		fadeOut.setDuration(500);
		fadeOut.setStartOffset(0);
		Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
		fadeIn.setAnimationListener(this);
		fadeIn.setDuration(500);
		fadeIn.setStartOffset(250);
		if (this.config == true) {
			this.leftKeyPressStateImage.startAnimation(fadeOut);
			this.rightKeyPressStateImage.startAnimation(fadeOut);
			this.reedStateImage.startAnimation(fadeOut);
		}
		else {
			this.leftKeyPressStateImage.startAnimation(fadeIn);
			this.rightKeyPressStateImage.startAnimation(fadeIn);
			this.reedStateImage.startAnimation(fadeIn);
		}
	}
	
	@Override
	public void onAnimationEnd(Animation animation) {
		super.onAnimationEnd(animation);
		if (this.config == true) {
			this.leftKeyPressStateImage.setVisibility(View.INVISIBLE);
			this.rightKeyPressStateImage.setVisibility(View.INVISIBLE);
			this.reedStateImage.setVisibility(View.INVISIBLE);
			
		}
		else {
			this.leftKeyPressStateImage.setVisibility(View.VISIBLE);
			this.rightKeyPressStateImage.setVisibility(View.VISIBLE);
			this.reedStateImage.setVisibility(View.VISIBLE);
		}
	}
	class updateSparkLinesTimerTask extends TimerTask  {
		SensorTagSimpleKeysTableRow param;

	     public updateSparkLinesTimerTask(SensorTagSimpleKeysTableRow param) {
	    	 this.param = param;
	     }

	     @Override
	     public void run() {
	    	this.param.post(new Runnable() {
	    		 @Override 
	    		 public void run() {
	    	
	    	         if ((param.lastKeys & 0x1) == 0x1) {
	    	        	 param.sl1.addValue(1);
	    	         }
	    	         else param.sl1.addValue(0);
	    	         if ((param.lastKeys & 0x2) == 0x2) {
	    	        	 param.sl2.addValue(1);
	    	         }
	    	         else param.sl2.addValue(0);
	    	         if ((param.lastKeys & 0x4) == 0x4) {
	    	        	 param.sl3.addValue(1);
	    	         }
	    	         else param.sl3.addValue(0);
	    		 }
	    	 }); 
	     }
	}
    @Override
    public void grayedOut(boolean gray) {
        super.grayedOut(gray);
        if (gray) {
            this.leftKeyPressStateImage.setAlpha(0.4f);
            this.rightKeyPressStateImage.setAlpha(0.4f);
            this.reedStateImage.setAlpha(0.4f);
            this.sl3.setAlpha(0.2f);
        }
        else {
            this.leftKeyPressStateImage.setAlpha(1.0f);
            this.rightKeyPressStateImage.setAlpha(1.0f);
            this.reedStateImage.setAlpha(1.0f);
            this.sl3.setAlpha(1.0f);
        }
    }
}
