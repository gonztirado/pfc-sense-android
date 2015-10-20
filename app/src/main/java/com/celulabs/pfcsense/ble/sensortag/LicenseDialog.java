package com.celulabs.pfcsense.ble.sensortag;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;

public class LicenseDialog extends Dialog {
	private static LicenseDialog mDialog;
	private static OkListener mOkListener;

	public LicenseDialog(Context context) {
		super(context);
		mDialog = this;
		mOkListener = new OkListener();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_license);

		// From license.html web page
		WebView wv = (WebView) findViewById(R.id.webpage_license);
		wv.loadUrl("file:///android_asset/license.html");

		// Dismiss button
		Button okButton = (Button) findViewById(R.id.buttonOK);
		okButton.setOnClickListener(mOkListener);
	}

	private class OkListener implements android.view.View.OnClickListener {
		@Override
		public void onClick(View v) {
			mDialog.dismiss();
		}
	}
}
