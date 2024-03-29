package com.celulabs.pfcsense.ble.sensortag;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.celulabs.pfcsense.ble.common.BleDeviceInfo;
import com.celulabs.pfcsense.ble.util.CustomTimer;
import com.celulabs.pfcsense.ble.util.CustomTimerCallback;

import java.util.List;

// import android.util.Log;

public class ScanView extends Fragment {
    // private static final String TAG = "ScanView";
    private final int SCAN_TIMEOUT = 10; // Seconds
    private final int CONNECT_TIMEOUT = 20; // Seconds
    private MainActivity mActivity = null;

    private DeviceListAdapter mDeviceAdapter = null;
    private TextView mEmptyMsg;
    private TextView mStatus;
    private Button mBtnScan = null;
    private ListView mDeviceListView = null;
    private boolean mBusy;

    private CustomTimer mScanTimer = null;
    private CustomTimer mConnectTimer = null;
    @SuppressWarnings("unused")
    private CustomTimer mStatusTimer;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Log.i(TAG, "onCreateView");

        // The last two arguments ensure LayoutParams are inflated properly.
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        mActivity = (MainActivity) getActivity();
        mContext = mActivity.getApplicationContext();

        // Initialize widgets
        mStatus = (TextView) view.findViewById(R.id.status);
        mBtnScan = (Button) view.findViewById(R.id.btn_scan);
        mDeviceListView = (ListView) view.findViewById(R.id.device_list);
        mDeviceListView.setClickable(true);
        mDeviceListView.setOnItemClickListener(mDeviceClickListener);
        mEmptyMsg = (TextView) view.findViewById(R.id.no_device);
        mBusy = false;

        // Alert parent activity
        mActivity.onScanViewReady(view);

        return view;
    }

    @Override
    public void onDestroy() {
        // Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    void setStatus(String txt) {
        mStatus.setText(txt);
        mStatus.setTextAppearance(mContext, R.style.statusStyle_Success);
    }

    void setStatus(String txt, int duration) {
        setStatus(txt);
        mStatusTimer = new CustomTimer(null, duration, mClearStatusCallback);
    }

    void setError(String txt) {
        setBusy(false);
        stopTimers();
        mStatus.setText(txt);
        mStatus.setTextAppearance(mContext, R.style.statusStyle_Failure);
    }

    void notifyDataSetChanged() {
        List<BleDeviceInfo> deviceList = mActivity.getDeviceInfoList();
        if (mDeviceAdapter == null) {
            mDeviceAdapter = new DeviceListAdapter(mActivity, deviceList);
        }
        mDeviceListView.setAdapter(mDeviceAdapter);
        mDeviceAdapter.notifyDataSetChanged();
        if (deviceList.size() > 0) {
            mEmptyMsg.setVisibility(View.GONE);
        } else {
            mEmptyMsg.setVisibility(View.VISIBLE);
        }
    }

    void setBusy(boolean f) {
        if (f != mBusy) {
            mBusy = f;
            if (!mBusy) {
                stopTimers();
                mBtnScan.setEnabled(true);    // Enable in case of connection timeout
                mDeviceAdapter.notifyDataSetChanged(); // Force enabling of all Connect buttons
            }
            mActivity.showBusyIndicator(f);
        }
    }

    void updateGui(boolean scanning) {
        if (mBtnScan == null)
            return; // UI not ready

        setBusy(scanning);

        if (scanning) {
            // Indicate that scanning has started
            mScanTimer = new CustomTimer(null, SCAN_TIMEOUT, mPgScanCallback);
            mBtnScan.setText("Parar");
            mBtnScan.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_cancel, 0);
            mStatus.setTextAppearance(mContext, R.style.statusStyle_Busy);
            mStatus.setText("Escaneando...");
            mEmptyMsg.setText(R.string.nodevice);
            mActivity.updateGuiState();
        } else {
            // Indicate that scanning has stopped
            mStatus.setTextAppearance(mContext, R.style.statusStyle_Success);
            mBtnScan.setText("Escanear");
            mBtnScan.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_refresh, 0);
            mEmptyMsg.setText(R.string.scan_advice);
            mActivity.setProgressBarIndeterminateVisibility(false);
            mDeviceAdapter.notifyDataSetChanged();
        }
    }

    // Listener for device list
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
            // Log.d(TAG,"item click");
            mConnectTimer = new CustomTimer(null, CONNECT_TIMEOUT, mPgConnectCallback);
            mBtnScan.setEnabled(false);
            mDeviceAdapter.notifyDataSetChanged(); // Force disabling of all Connect buttons
            mActivity.onDeviceClick(pos);
        }
    };

    // Listener for progress timer expiration
    private CustomTimerCallback mPgScanCallback = new CustomTimerCallback() {
        public void onTimeout() {
            mActivity.onScanTimeout();
        }

        public void onTick(int i) {
            mActivity.refreshBusyIndicator();
        }
    };

    // Listener for connect/disconnect expiration
    private CustomTimerCallback mPgConnectCallback = new CustomTimerCallback() {
        public void onTimeout() {
            mActivity.onConnectTimeout();
            mBtnScan.setEnabled(true);
        }

        public void onTick(int i) {
            mActivity.refreshBusyIndicator();
        }
    };

    // Listener for connect/disconnect expiration
    private CustomTimerCallback mClearStatusCallback = new CustomTimerCallback() {
        public void onTimeout() {
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    setStatus("");
                }
            });
            mStatusTimer = null;
        }

        public void onTick(int i) {
        }
    };

    private void stopTimers() {
        if (mScanTimer != null) {
            mScanTimer.stop();
            mScanTimer = null;
        }
        if (mConnectTimer != null) {
            mConnectTimer.stop();
            mConnectTimer = null;
        }
    }

    //
    // CLASS DeviceAdapter: handle device list
    //
    @SuppressLint("InflateParams")
    class DeviceListAdapter extends BaseAdapter {
        private List<BleDeviceInfo> mDevices;
        private LayoutInflater mInflater;

        public DeviceListAdapter(Context context, List<BleDeviceInfo> devices) {
            mInflater = LayoutInflater.from(context);
            mDevices = devices;
        }

        public int getCount() {
            return mDevices.size();
        }

        public Object getItem(int position) {
            return mDevices.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup vg;

            if (convertView != null) {
                vg = (ViewGroup) convertView;
            } else {
                vg = (ViewGroup) mInflater.inflate(R.layout.element_device, null);
            }

            BleDeviceInfo deviceInfo = mDevices.get(position);
            BluetoothDevice device = deviceInfo.getBluetoothDevice();
            int rssi = deviceInfo.getRssi();
            String name;
            name = device.getName();
            if (name == null) {
                name = new String("Unknown device");
            }

            String descr = name + "\n" + device.getAddress() + "\nRssi: " + rssi + " dBm";
            ((TextView) vg.findViewById(R.id.descr)).setText(descr);

            ImageView iv = (ImageView) vg.findViewById(R.id.devImage);
            if (name.equals("SensorTag2") || name.equals("CC2650 SensorTag"))
                iv.setImageResource(R.drawable.sensortag2_300);
            else {
                iv.setImageResource(R.drawable.sensortag_300);
            }

            // FIXME quitado botón "Conectar" ya que pulsando sobre la linea del sensor hace mismo efecto. Quitar si se ve que comprueba que no es necesario
//      // Disable connect button when connecting or connected
//      Button bv = (Button)vg.findViewById(R.id.btnConnect);
//      bv.setEnabled(mConnectTimer == null);

            return vg;
        }
    }

}
