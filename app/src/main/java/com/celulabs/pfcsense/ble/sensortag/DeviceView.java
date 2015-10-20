package com.celulabs.pfcsense.ble.sensortag;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;



	// Fragment for Device View
	public class DeviceView extends Fragment {

	public static DeviceView mInstance = null;

	// GUI
	private TableLayout table;
	public boolean first = true;
	
	// House-keeping
	private DeviceActivity mActivity;
	private boolean mBusy;

	// The last two arguments ensure LayoutParams are inflated properly.
	View view;
	
	public DeviceView() {
		super();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
		mInstance = this;
		mActivity = (DeviceActivity) getActivity();
		
		view = inflater.inflate(R.layout.generic_services_browser, container,false);
		table = (TableLayout) view.findViewById(R.id.generic_services_layout);

		// Notify activity that UI has been inflated
		mActivity.onViewInflated(view);
		
		return view;
	}

	public void showProgressOverlay(String title) {
		
	}
		
	public void addRowToTable(TableRow row) {
		if (first) {
			table.removeAllViews();
			table.addView(row);
			table.requestLayout();
			first = false;
		}
		else {
			table.addView(row);
			table.requestLayout();
		}
	}
    public void removeRowsFromTable() {
        table.removeAllViews();
    }


	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	void setBusy(boolean f) {
		if (f != mBusy)
		{
			mActivity.showBusyIndicator(f);
			mBusy = f;
		}
	}
}
