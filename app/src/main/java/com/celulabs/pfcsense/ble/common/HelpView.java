package com.celulabs.pfcsense.ble.common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class HelpView extends Fragment {
  private String mFile = "about.html";
  private int mIdFragment;
  private int mIdWebPage;

  public HelpView() {
  	
  }
  
  public void setParameters(String file, int idFragment, int idWebPage) {
    if (file != null)
      mFile = file;
    mIdFragment = idFragment;
    mIdWebPage = idWebPage;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(mIdFragment, container, false);
    WebView wv = (WebView) rootView.findViewById(mIdWebPage);

    wv.loadUrl("file:///android_asset/" + mFile);
    return rootView;
  }

}
