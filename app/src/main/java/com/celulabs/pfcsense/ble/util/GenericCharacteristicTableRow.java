package com.celulabs.pfcsense.ble.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;

import com.celulabs.pfcsense.ble.common.GattInfo;
import com.celulabs.pfcsense.ble.sensortag.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

public class GenericCharacteristicTableRow extends TableRow implements View.OnClickListener, Animation.AnimationListener, SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {
	//Normal cell operation : Show data contents
	protected final Context context;
	public final SparkLineView sl1,sl2,sl3;
	public final TextView value;
	public final ImageView icon;
	public final TextView title;
	public final TextView uuidLabel;
	private final Paint linePaint;
	protected final RelativeLayout rowLayout;
	public int iconSize = 150;
	public boolean config;

	//Configuration operation : Show configuration contents
	public final Switch onOff;
	public final SeekBar periodBar;
	public final TextView onOffLegend;
	public final TextView periodLegend;
	public final Button calibrateButton;
	public final static String ACTION_PERIOD_UPDATE = "com.celulabs.pfcsense.util.ACTION_PERIOD_UPDATE";
	public final static String ACTION_ONOFF_UPDATE = "com.celulabs.pfcsense.util.ACTION_ONOFF_UPDATE";
	public final static String ACTION_CALIBRATE = "com.celulabs.pfcsense.util.ACTION_CALIBRATE";
	public final static String EXTRA_SERVICE_UUID = "com.celulabs.pfcsense.util.EXTRA_SERVICE_UUID";
	public final static String EXTRA_PERIOD = "com.celulabs.pfcsense.util.EXTRA_PERIOD";
	public final static String EXTRA_ONOFF = "com.celulabs.pfcsense.util.EXTRA_ONOFF";
	public int periodMinVal;

	public static boolean isCorrectService(String uuidString) {
		return true;
	}

	public GenericCharacteristicTableRow(Context con) {
		super(con);
		this.context = con;
		this.config = false;
		this.setLayoutParams(new TableRow.LayoutParams(1));
		this.setBackgroundColor(Color.TRANSPARENT);
		this.setOnClickListener(this);
		this.periodMinVal = 100;

		// GATT database
		Resources res = getResources();
		XmlResourceParser xpp = res.getXml(R.xml.gatt_uuid);
		new GattInfo(xpp);

		this.rowLayout = new RelativeLayout(this.context);

		this.linePaint = new Paint() {
			{
			setStrokeWidth(1);
			setARGB(255, 0, 0, 0);
			}
		};


		//Add all views for the default cell
		//Service icon
		this.icon = new ImageView(con) {
			{
				setId(1);
				setPadding(30,30,30,30);
			}
		};
		//Service title
		this.title = new TextView(con) {
			{
				setTextSize(TypedValue.COMPLEX_UNIT_PT,10.0f);
				setTypeface(null,Typeface.BOLD);
				setId(2);
			}
		};
		//Service UUID, hidden by default
		this.uuidLabel = new TextView(con) {
			{
				setTextSize(TypedValue.COMPLEX_UNIT_PT,8.0f);
				setId(3);
				setVisibility(View.INVISIBLE);
			}
		};

		//One Value
		this.value = new TextView(con) {
			{
				setTextSize(TypedValue.COMPLEX_UNIT_PT,8.0f);
				setTextAlignment(TEXT_ALIGNMENT_VIEW_END);
				setId(4);
				setVisibility(View.VISIBLE);
			}
		};
		//One Sparkline showing trends
		this.sl1 = new SparkLineView(con) {
			{
				setVisibility(View.VISIBLE);
				setId(5);
			}
		};
		this.sl2 = new SparkLineView(con) {
			{
				setVisibility(View.INVISIBLE);
				setId(5);
				setEnabled(false);
			}
		};
		this.sl3 = new SparkLineView(con) {
			{
				setVisibility(View.INVISIBLE);
				setId(5);
				setEnabled(false);
			}
		};

		this.onOff = new Switch(con) {
			{
			setVisibility(View.INVISIBLE);
			setId(100);
			setChecked(true);
			}
		};
		this.periodBar = new SeekBar(con) {
			{
				setVisibility(View.INVISIBLE);
				setId(101);
				setMax(245);
			}
		};
		this.onOffLegend = new TextView(con) {
			{
				setVisibility(View.INVISIBLE);
				setId(102);
				setText("Sensor activado");
			}
		};
		this.periodLegend = new TextView(con) {
			{
				setVisibility(View.INVISIBLE);
				setId(103);
				setText("Periodo");
			}
		};
		this.calibrateButton = new Button(con) {
			{
				setVisibility(View.INVISIBLE);
				setId(104);
				setText("Calibración");
			}
		};


		this.periodBar.setOnSeekBarChangeListener(this);
		this.onOff.setOnCheckedChangeListener(this);

		//Setup content of the fields

		//Setup layout for all cell elements
		RelativeLayout.LayoutParams iconItemParams = new RelativeLayout.LayoutParams(
				iconSize,
				iconSize) {
			{
				addRule(RelativeLayout.CENTER_VERTICAL,
						RelativeLayout.TRUE);
				addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
			}

		};
		icon.setLayoutParams(iconItemParams);

		RelativeLayout.LayoutParams tmpLayoutParams = new RelativeLayout.LayoutParams(
		        RelativeLayout.LayoutParams.WRAP_CONTENT,
		        RelativeLayout.LayoutParams.WRAP_CONTENT);
        tmpLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP,
				RelativeLayout.TRUE);
        tmpLayoutParams.addRule(RelativeLayout.RIGHT_OF, icon.getId());
		title.setLayoutParams(tmpLayoutParams);

        tmpLayoutParams = new RelativeLayout.LayoutParams(
		        RelativeLayout.LayoutParams.WRAP_CONTENT,
		        RelativeLayout.LayoutParams.WRAP_CONTENT);
        tmpLayoutParams.addRule(RelativeLayout.BELOW,
		        title.getId());
        tmpLayoutParams.addRule(RelativeLayout.RIGHT_OF, icon.getId());
		uuidLabel.setLayoutParams(tmpLayoutParams);

        tmpLayoutParams = new RelativeLayout.LayoutParams(
		        RelativeLayout.LayoutParams.MATCH_PARENT,
		        RelativeLayout.LayoutParams.MATCH_PARENT);
        tmpLayoutParams.addRule(RelativeLayout.BELOW,
		        title.getId());
        tmpLayoutParams.addRule(RelativeLayout.RIGHT_OF,icon.getId());
		value.setLayoutParams(tmpLayoutParams);

        tmpLayoutParams = new RelativeLayout.LayoutParams(
		        RelativeLayout.LayoutParams.MATCH_PARENT,
		        RelativeLayout.LayoutParams.MATCH_PARENT);
        tmpLayoutParams.addRule(RelativeLayout.BELOW,
				value.getId());
        tmpLayoutParams.addRule(RelativeLayout.RIGHT_OF, icon.getId());
		this.sl1.setLayoutParams(tmpLayoutParams);
		this.sl2.setLayoutParams(tmpLayoutParams);
		this.sl3.setLayoutParams(tmpLayoutParams);


        tmpLayoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
		        RelativeLayout.LayoutParams.MATCH_PARENT);
        tmpLayoutParams.addRule(RelativeLayout.BELOW,
		        value.getId());
        tmpLayoutParams.addRule(RelativeLayout.RIGHT_OF,icon.getId());
		onOffLegend.setLayoutParams(tmpLayoutParams);

        tmpLayoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
		        RelativeLayout.LayoutParams.MATCH_PARENT);
        tmpLayoutParams.addRule(RelativeLayout.BELOW,
				onOffLegend.getId());
        tmpLayoutParams.addRule(RelativeLayout.RIGHT_OF,icon.getId());
		onOff.setLayoutParams(tmpLayoutParams);

        tmpLayoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
		        RelativeLayout.LayoutParams.MATCH_PARENT);
        tmpLayoutParams.addRule(RelativeLayout.BELOW,
				value.getId());
        tmpLayoutParams.addRule(RelativeLayout.RIGHT_OF,onOff.getId());
		calibrateButton.setLayoutParams(tmpLayoutParams);

        tmpLayoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
		        RelativeLayout.LayoutParams.MATCH_PARENT);
        tmpLayoutParams.addRule(RelativeLayout.BELOW,
				onOff.getId());
        tmpLayoutParams.addRule(RelativeLayout.RIGHT_OF,icon.getId());
		periodLegend.setLayoutParams(tmpLayoutParams);

        tmpLayoutParams = new RelativeLayout.LayoutParams(
		        RelativeLayout.LayoutParams.MATCH_PARENT,
		        RelativeLayout.LayoutParams.MATCH_PARENT);
        tmpLayoutParams.addRule(RelativeLayout.BELOW,
				periodLegend.getId());
        tmpLayoutParams.rightMargin = 50;
        tmpLayoutParams.addRule(RelativeLayout.RIGHT_OF, icon.getId());
		this.periodBar.setLayoutParams(tmpLayoutParams);

		// Add all views to cell
//		rowLayout.addView(icon);
		rowLayout.addView(title);
		rowLayout.addView(uuidLabel);
		rowLayout.addView(value);
		rowLayout.addView(this.sl1);
		rowLayout.addView(this.sl2);
		rowLayout.addView(this.sl3);
		rowLayout.addView(this.onOffLegend);
		rowLayout.addView(this.onOff);
		rowLayout.addView(this.periodLegend);
		rowLayout.addView(this.periodBar);
		rowLayout.addView(this.calibrateButton);

		/* Añadido pequeño padding al layout general (quitada la imagen) */
		float scale = getResources().getDisplayMetrics().density;
		int dpAsPixels = (int) (10 * scale + 0.5f);
		rowLayout.setPadding(dpAsPixels, 0, dpAsPixels, 0);

		this.addView(rowLayout);
	}

	public void setIcon(String iconPrefix, String uuid) {
		WindowManager wm = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point dSize = new Point();
		display.getSize(dSize);
		Drawable image = null;


		Log.d("GenericCharacteristicTableRow", "Width : " + dSize.x + " Height : " + dSize.y);
		Log.d("GenericCharacteristicTableRow", "Fetching icon : " + GattInfo.uuidToIcon(UUID.fromString(uuid)));
		if (dSize.x > 1100) {
			Uri uri = Uri.parse("android.resource://"+ this.context.getPackageName()+"/drawable/" + iconPrefix + GattInfo.uuidToIcon(UUID.fromString(uuid)) + "_300");
			try {
				InputStream inputStream = this.context.getContentResolver().openInputStream(uri);
				image = Drawable.createFromStream(inputStream, uri.toString() );
				iconSize = 360;
			}
			catch (FileNotFoundException e) {
				Log.d("Could not find icon filename : ", uri.toString());
			}
		}
		else {
			Uri uri = Uri.parse("android.resource://"+ this.context.getPackageName()+"/drawable/" + iconPrefix + GattInfo.uuidToIcon(UUID.fromString(uuid)));
			try {
				InputStream inputStream = this.context.getContentResolver().openInputStream(uri);
				image = Drawable.createFromStream(inputStream, uri.toString() );
				iconSize = 210;
			}
			catch (FileNotFoundException e) {
				Log.d("Could not find icon filename : ", uri.toString());
			}
		}
		icon.setImageDrawable(image);
		this.sl1.displayWidth = this.sl2.displayWidth = this.sl3.displayWidth = dSize.x - iconSize - 5;
		RelativeLayout.LayoutParams iconItemParams = new RelativeLayout.LayoutParams(
				iconSize,
				iconSize) {
			{
				addRule(RelativeLayout.CENTER_VERTICAL,
						RelativeLayout.TRUE);
				addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
			}

		};
		icon.setLayoutParams(iconItemParams);
	}
	public void setIcon(String iconPrefix, String uuid,String variantName) {
		WindowManager wm = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point dSize = new Point();
		display.getSize(dSize);
		Drawable image = null;


		if (dSize.x > 1100) {
			Uri uri = Uri.parse("android.resource://"+ this.context.getPackageName()+"/drawable/" + iconPrefix + variantName + "_300");
			try {
				InputStream inputStream = this.context.getContentResolver().openInputStream(uri);
				image = Drawable.createFromStream(inputStream, uri.toString() );
				iconSize = 360;
			}
			catch (FileNotFoundException e) {
				Log.d("Could not find icon filename : ", uri.toString());
			}
		}
		else {
			Uri uri = Uri.parse("android.resource://"+ this.context.getPackageName()+"/drawable/" + iconPrefix + variantName);
			try {
				InputStream inputStream = this.context.getContentResolver().openInputStream(uri);
				image = Drawable.createFromStream(inputStream, uri.toString() );
				iconSize = 210;
			}
			catch (FileNotFoundException e) {
				Log.d("Could not find icon filename : ", uri.toString());
			}
		}
		this.sl1.displayWidth = this.sl2.displayWidth = this.sl3.displayWidth = dSize.x - iconSize - 5;
		icon.setImageDrawable(image);
		RelativeLayout.LayoutParams iconItemParams = new RelativeLayout.LayoutParams(
				iconSize,
				iconSize) {
			{
				addRule(RelativeLayout.CENTER_VERTICAL,
						RelativeLayout.TRUE);
				addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
			}

		};
		icon.setLayoutParams(iconItemParams);

	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawLine(0, canvas.getHeight() - this.linePaint.getStrokeWidth(), canvas.getWidth(), canvas.getHeight() - this.linePaint.getStrokeWidth(), this.linePaint);
	}
	@Override
	public void onConfigurationChanged (Configuration newConfig) {
		WindowManager wm = (WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point dSize = new Point();
		display.getSize(dSize);
		this.sl1.displayWidth = this.sl2.displayWidth = this.sl3.displayWidth = dSize.x - iconSize - 5;
		this.invalidate();
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
			if ((this.sl2.isEnabled()))this.sl2.startAnimation(fadeOut);
			if ((this.sl3.isEnabled()))this.sl3.startAnimation(fadeOut);
			this.value.startAnimation(fadeOut);
			this.onOffLegend.startAnimation(fadeIn);
			this.onOff.startAnimation(fadeIn);
			this.periodLegend.startAnimation(fadeIn);
			this.periodBar.startAnimation(fadeIn);
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
		}
		else {
			this.sl1.setVisibility(View.VISIBLE);
			if ((this.sl2.isEnabled()))this.sl2.setVisibility(View.VISIBLE);
			if ((this.sl3.isEnabled()))this.sl3.setVisibility(View.VISIBLE);
			this.onOff.setVisibility(View.INVISIBLE);
			this.onOffLegend.setVisibility(View.INVISIBLE);
			this.periodBar.setVisibility(View.INVISIBLE);
			this.periodLegend.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		Log.d("GenericBluetoothProfile", "Period changed : " + progress);
		this.periodLegend.setText("Periodo actualización (actualmente: " + ((progress * 10) + periodMinVal) + "ms)");
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		Log.d("GenericBluetoothProfile", "Period Start");
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		Log.d("GenericBluetoothProfile", "Period Stop");
		final Intent intent = new Intent(ACTION_PERIOD_UPDATE);
		int period = periodMinVal + (seekBar.getProgress() * 10);
		intent.putExtra(EXTRA_SERVICE_UUID, this.uuidLabel.getText());
		intent.putExtra(EXTRA_PERIOD,period);
		this.context.sendBroadcast(intent);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Log.d("GenericBluetoothProfile", "Switch changed : " + isChecked);
		final Intent intent = new Intent(ACTION_ONOFF_UPDATE);
		intent.putExtra(EXTRA_SERVICE_UUID, this.uuidLabel.getText());
		intent.putExtra(EXTRA_ONOFF,isChecked);
		this.context.sendBroadcast(intent);
	}

    public void grayedOut(boolean gray) {
        if (gray) {
            this.periodBar.setAlpha(0.4f);
            this.value.setAlpha(0.4f);
            this.title.setAlpha(0.4f);
            this.icon.setAlpha(0.4f);
            this.sl1.setAlpha(0.4f);
            this.sl2.setAlpha(0.4f);
            this.sl3.setAlpha(0.4f);
            this.periodLegend.setAlpha(0.4f);

        }
        else {
            this.periodBar.setAlpha(1.0f);
            this.value.setAlpha(1.0f);
            this.title.setAlpha(1.0f);
            this.icon.setAlpha(1.0f);
            this.sl1.setAlpha(1.0f);
            this.sl2.setAlpha(1.0f);
            this.sl3.setAlpha(1.0f);
            this.periodLegend.setAlpha(1.0f);
        }
    }

}

