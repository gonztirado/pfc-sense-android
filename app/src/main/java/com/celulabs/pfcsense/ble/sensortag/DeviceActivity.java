package com.celulabs.pfcsense.ble.sensortag;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.celulabs.pfcsense.ble.btsig.profiles.DeviceInformationServiceProfile;
import com.celulabs.pfcsense.ble.common.BluetoothLeService;
import com.celulabs.pfcsense.ble.common.GattInfo;
import com.celulabs.pfcsense.ble.common.GenericBluetoothProfile;
import com.celulabs.pfcsense.ble.common.HelpView;
import com.celulabs.pfcsense.ble.common.IBMIoTCloudProfile;
import com.celulabs.pfcsense.ble.ti.profiles.TIOADProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// import android.util.Log;


@SuppressLint("InflateParams")
public class DeviceActivity extends ViewPagerActivity {
    // Log
    // private static String TAG = "DeviceActivity";

    // Activity
    public static final String EXTRA_DEVICE = "EXTRA_DEVICE";
    private static final int PREF_ACT_REQ = 0;
    private static final int FWUPDATE_ACT_REQ = 1;

    private DeviceView mDeviceView = null;

    // BLE
    private BluetoothLeService mBtLeService = null;
    private BluetoothDevice mBluetoothDevice = null;
    private BluetoothGatt mBtGatt = null;
    private List<BluetoothGattService> mServiceList = null;
    private boolean mServicesRdy = false;
    private boolean mIsReceiving = false;
    private IBMIoTCloudProfile mqttProfile;

    // SensorTagGatt
    private BluetoothGattService mOadService = null;
    private BluetoothGattService mConnControlService = null;
    private boolean mIsSensorTag2;
    private String mFwRev;
    public ProgressDialog progressDialog;

    //GUI
    private List<GenericBluetoothProfile> mProfiles;

    public DeviceActivity() {
        mResourceFragmentPager = R.layout.fragment_pager;
        mResourceIdPager = R.id.pager;
        mFwRev = new String("1.5"); // Assuming all SensorTags are up to date until actual FW revision is read
    }

    public static DeviceActivity getInstance() {
        return (DeviceActivity) mThis;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        // BLE
        mBtLeService = BluetoothLeService.getInstance();
        mBluetoothDevice = intent.getParcelableExtra(EXTRA_DEVICE);
        mServiceList = new ArrayList<BluetoothGattService>();

        mIsSensorTag2 = false;
        // Determine type of SensorTagGatt
        String deviceName = mBluetoothDevice.getName();
        if ((deviceName.equals("SensorTag2")) || (deviceName.equals("CC2650 SensorTag"))) {
            mIsSensorTag2 = true;
        } else mIsSensorTag2 = false;

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        // Log.i(TAG, "Preferences for: " + deviceName);

        // GUI
        mDeviceView = new DeviceView();
        mSectionsPagerAdapter.addSection(mDeviceView, "Sensores");
        HelpView hw = new HelpView();
        hw.setParameters("help_device.html", R.layout.fragment_help, R.id.webpage);
        mSectionsPagerAdapter.addSection(hw, "Ayuda");
        mProfiles = new ArrayList<GenericBluetoothProfile>();
        progressDialog = new ProgressDialog(DeviceActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle("Descubriendo servicios");
        progressDialog.setMessage("");
        progressDialog.setMax(100);
        progressDialog.setProgress(0);
        progressDialog.show();

        // GATT database
        Resources res = getResources();
        XmlResourceParser xpp = res.getXml(R.xml.gatt_uuid);
        new GattInfo(xpp);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mqttProfile != null) {
            mqttProfile.disconnect();

        }
        if (mIsReceiving) {
            unregisterReceiver(mGattUpdateReceiver);
            mIsReceiving = false;
        }
        for (GenericBluetoothProfile p : mProfiles) {
            p.onPause();
        }
        if (!this.isEnabledByPrefs("keepAlive")) {
            this.mBtLeService.timedDisconnect();
        }
        //View should be started again from scratch
        this.mDeviceView.first = true;
        this.mProfiles = null;
        this.mDeviceView.removeRowsFromTable();
        this.mDeviceView = null;
        finishActivity(PREF_ACT_REQ);
        finishActivity(FWUPDATE_ACT_REQ);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.device_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.opt_prefs:
                startPreferenceActivity();
                break;
            case R.id.opt_about:
                openAboutDialog();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public boolean isEnabledByPrefs(String prefName) {
        String preferenceKeyString = "pref_"
                + prefName;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mBtLeService);

        Boolean defaultValue = true;
        return prefs.getBoolean(preferenceKeyString, defaultValue);
    }

    @Override
    protected void onResume() {
        // Log.d(TAG, "onResume");
        super.onResume();
        if (!mIsReceiving) {
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
            mIsReceiving = true;
        }
        for (GenericBluetoothProfile p : mProfiles) {
            if (p.isConfigured != true) p.configureService();
            if (p.isEnabled != true) p.enableService();
            p.onResume();
        }
        this.mBtLeService.abortTimedDisconnect();
    }

    @Override
    protected void onPause() {
        // Log.d(TAG, "onPause");
        super.onPause();
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter fi = new IntentFilter();
        fi.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        fi.addAction(BluetoothLeService.ACTION_DATA_NOTIFY);
        fi.addAction(BluetoothLeService.ACTION_DATA_WRITE);
        fi.addAction(BluetoothLeService.ACTION_DATA_READ);
        fi.addAction(DeviceInformationServiceProfile.ACTION_FW_REV_UPDATED);
        fi.addAction(TIOADProfile.ACTION_PREPARE_FOR_OAD);
        return fi;
    }

    void onViewInflated(View view) {
        // Log.d(TAG, "Gatt view ready");
        setBusy(true);

        // Set title bar to device name
        setTitle(mBluetoothDevice.getName());

        // Create GATT object
        mBtGatt = BluetoothLeService.getBtGatt();
        // Start service discovery
        if (!mServicesRdy && mBtGatt != null) {
            if (mBtLeService.getNumServices() == 0)
                discoverServices();
            else {
            }
        }
    }

    boolean isSensorTag2() {
        return mIsSensorTag2;
    }

    String firmwareRevision() {
        return mFwRev;
    }

    BluetoothGattService getOadService() {
        return mOadService;
    }

    BluetoothGattService getConnControlService() {
        return mConnControlService;
    }

    private void startPreferenceActivity() {
        // Launch preferences
        final Intent i = new Intent(this, PreferencesActivity.class);
        i.putExtra(PreferencesActivity.EXTRA_SHOW_FRAGMENT,
                PreferencesFragment.class.getName());
        i.putExtra(PreferencesActivity.EXTRA_NO_HEADERS, true);
        i.putExtra(EXTRA_DEVICE, mBluetoothDevice);
        startActivityForResult(i, PREF_ACT_REQ);
    }

    private void discoverServices() {
        if (mBtGatt.discoverServices()) {
            mServiceList.clear();
            setBusy(true);

        } else {

        }
    }

    private void setBusy(boolean b) {
        mDeviceView.setBusy(b);
    }

    // Activity result handling
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            default:
                break;
        }
    }

    private void setError(String txt) {
        setBusy(false);
        Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
    }

    private void setStatus(String txt) {
        Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
    }


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        List<BluetoothGattService> serviceList;
        List<BluetoothGattCharacteristic> charList = new ArrayList<BluetoothGattCharacteristic>();

        @Override
        public void onReceive(final Context context, Intent intent) {
            final String action = intent.getAction();
            final int status = intent.getIntExtra(BluetoothLeService.EXTRA_STATUS,
                    BluetoothGatt.GATT_SUCCESS);


            if (DeviceInformationServiceProfile.ACTION_FW_REV_UPDATED.equals(action)) {
                mFwRev = intent.getStringExtra(DeviceInformationServiceProfile.EXTRA_FW_REV_STRING);
                Log.d("DeviceActivity", "Got FW revision : " + mFwRev + " from DeviceInformationServiceProfile");
                for (GenericBluetoothProfile p : mProfiles) {
                    p.didUpdateFirmwareRevision(mFwRev);
                }
            }
            if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                if (status == BluetoothGatt.GATT_SUCCESS) {

                    serviceList = mBtLeService.getSupportedGattServices();
                    if (serviceList.size() > 0) {
                        for (int ii = 0; ii < serviceList.size(); ii++) {
                            BluetoothGattService s = serviceList.get(ii);
                            List<BluetoothGattCharacteristic> c = s.getCharacteristics();
                            if (c.size() > 0) {
                                for (int jj = 0; jj < c.size(); jj++) {
                                    charList.add(c.get(jj));
                                }
                            }
                        }
                    }
                    Log.d("DeviceActivity", "Total characteristics " + charList.size());
                    Thread worker = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            //Iterate through the services and add GenericBluetoothServices for each service
                            int nrNotificationsOn = 0;
                            int maxNotifications;
                            int servicesDiscovered = 0;
                            int totalCharacteristics = 0;
                            //serviceList = mBtLeService.getSupportedGattServices();
                            for (BluetoothGattService s : serviceList) {
                                List<BluetoothGattCharacteristic> chars = s.getCharacteristics();
                                totalCharacteristics += chars.size();
                            }

                            // FIXME quitado perfil de conexión con IBM cloud
                            // Special profile for Cloud service
//                            mqttProfile = new IBMIoTCloudProfile(context, mBluetoothDevice, null, mBtLeService);
//                            mProfiles.add(mqttProfile);
                            if (totalCharacteristics == 0) {
                                //Something bad happened, we have a problem
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        progressDialog.dismiss();
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                                context);
                                        alertDialogBuilder.setTitle("Error !");
                                        alertDialogBuilder.setMessage(serviceList.size() + " Servicios encontrados, pero sin características encontradas, el sensor será desconectado!");
                                        alertDialogBuilder.setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                mBtLeService.refreshDeviceCache(mBtGatt);
                                                //Try again
                                                discoverServices();
                                            }
                                        });
                                        alertDialogBuilder.setNegativeButton("Desconectar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                mBtLeService.disconnect(mBluetoothDevice.getAddress());
                                            }
                                        });
                                        AlertDialog a = alertDialogBuilder.create();
                                        a.show();
                                    }
                                });
                                return;
                            }
                            final int final_totalCharacteristics = totalCharacteristics;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.setIndeterminate(false);
                                    progressDialog.setTitle("Generando UI");
                                    progressDialog.setMessage("Encontrados un total de " + serviceList.size() + " servicios con un total de " + final_totalCharacteristics + " características en el sensor");

                                }
                            });
                            if (Build.VERSION.SDK_INT > 18) maxNotifications = 7;
                            else {
                                maxNotifications = 4;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Versión de Android 4.3 detectada, máximo 4 notificaciones activas", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            for (int ii = 0; ii < serviceList.size(); ii++) {
                                BluetoothGattService s = serviceList.get(ii);
                                List<BluetoothGattCharacteristic> chars = s.getCharacteristics();
                                if (chars.size() == 0) {

                                    Log.d("DeviceActivity", "No characteristics found for this service !!!");
                                    return;
                                }
                                servicesDiscovered++;
                                final float serviceDiscoveredcalc = (float) servicesDiscovered;
                                final float serviceTotalcalc = (float) serviceList.size();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.setProgress((int) ((serviceDiscoveredcalc / (serviceTotalcalc - 1)) * 100));
                                    }
                                });
                                Log.d("DeviceActivity", "Configuring service with uuid : " + s.getUuid().toString());
                                if (SensorTagHumidityProfile.isCorrectService(s)) {
                                    SensorTagHumidityProfile hum = new SensorTagHumidityProfile(context, mBluetoothDevice, s, mBtLeService);
                                    mProfiles.add(hum);
                                    if (nrNotificationsOn < maxNotifications) {
                                        hum.configureService();
                                        nrNotificationsOn++;
                                    } else {
                                        hum.grayOutCell(true);
                                    }
                                    Log.d("DeviceActivity", "Found Humidity !");
                                }
                                if (SensorTagLuxometerProfile.isCorrectService(s)) {
                                    SensorTagLuxometerProfile lux = new SensorTagLuxometerProfile(context, mBluetoothDevice, s, mBtLeService);
                                    mProfiles.add(lux);
                                    if (nrNotificationsOn < maxNotifications) {
                                        lux.configureService();
                                        nrNotificationsOn++;
                                    } else {
                                        lux.grayOutCell(true);
                                    }
                                }
                                if (SensorTagSimpleKeysProfile.isCorrectService(s)) {
                                    SensorTagSimpleKeysProfile key = new SensorTagSimpleKeysProfile(context, mBluetoothDevice, s, mBtLeService);
                                    mProfiles.add(key);
                                    if (nrNotificationsOn < maxNotifications) {
                                        key.configureService();
                                        nrNotificationsOn++;
                                    } else {
                                        key.grayOutCell(true);
                                    }
                                    Log.d("DeviceActivity", "Found Simple Keys !");
                                }
                                if (SensorTagBarometerProfile.isCorrectService(s)) {
                                    SensorTagBarometerProfile baro = new SensorTagBarometerProfile(context, mBluetoothDevice, s, mBtLeService);
                                    mProfiles.add(baro);
                                    if (nrNotificationsOn < maxNotifications) {
                                        baro.configureService();
                                        nrNotificationsOn++;
                                    } else {
                                        baro.grayOutCell(true);
                                    }
                                    Log.d("DeviceActivity", "Found Barometer !");
                                }
                                if (SensorTagAmbientTemperatureProfile.isCorrectService(s)) {
                                    SensorTagAmbientTemperatureProfile irTemp = new SensorTagAmbientTemperatureProfile(context, mBluetoothDevice, s, mBtLeService);
                                    mProfiles.add(irTemp);
                                    if (nrNotificationsOn < maxNotifications) {
                                        irTemp.configureService();
                                        nrNotificationsOn++;
                                    } else {
                                        irTemp.grayOutCell(true);
                                    }
                                    Log.d("DeviceActivity", "Found Ambient Temperature !");
                                }
                                if (SensorTagIRTemperatureProfile.isCorrectService(s)) {
                                    SensorTagIRTemperatureProfile irTemp = new SensorTagIRTemperatureProfile(context, mBluetoothDevice, s, mBtLeService);
                                    mProfiles.add(irTemp);
                                    if (nrNotificationsOn < maxNotifications) {
                                        irTemp.configureService();
                                    } else {
                                        irTemp.grayOutCell(true);
                                    }
                                    //No notifications add here because it is already enabled above ..
                                    Log.d("DeviceActivity", "Found IR Temperature !");
                                }
                                if (SensorTagMovementProfile.isCorrectService(s)) {
                                    SensorTagMovementProfile mov = new SensorTagMovementProfile(context, mBluetoothDevice, s, mBtLeService);
                                    mProfiles.add(mov);
                                    if (nrNotificationsOn < maxNotifications) {
                                        mov.configureService();
                                        nrNotificationsOn++;
                                    } else {
                                        mov.grayOutCell(true);
                                    }
                                    Log.d("DeviceActivity", "Found Motion !");
                                }
                                if (SensorTagAccelerometerProfile.isCorrectService(s)) {
                                    SensorTagAccelerometerProfile acc = new SensorTagAccelerometerProfile(context, mBluetoothDevice, s, mBtLeService);
                                    mProfiles.add(acc);
                                    if (nrNotificationsOn < maxNotifications) {
                                        acc.configureService();
                                        nrNotificationsOn++;
                                    } else {
                                        acc.grayOutCell(true);
                                    }
                                    Log.d("DeviceActivity", "Found Motion !");

                                }
                                if (DeviceInformationServiceProfile.isCorrectService(s)) {
                                    DeviceInformationServiceProfile devInfo = new DeviceInformationServiceProfile(context, mBluetoothDevice, s, mBtLeService);
                                    mProfiles.add(devInfo);
                                    devInfo.configureService();
                                    Log.d("DeviceActivity", "Found Device Information Service");
                                }
                                if (TIOADProfile.isCorrectService(s)) {
                                    TIOADProfile oad = new TIOADProfile(context, mBluetoothDevice, s, mBtLeService);
                                    mProfiles.add(oad);
                                    oad.configureService();
                                    mOadService = s;
                                    Log.d("DeviceActivity", "Found TI OAD Service");
                                }
                                if ((s.getUuid().toString().compareTo("f000ccc0-0451-4000-b000-000000000000")) == 0) {
                                    mConnControlService = s;
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.setTitle("Activando Servicios");
                                    progressDialog.setMax(mProfiles.size());
                                    progressDialog.setProgress(0);
                                }
                            });
                            for (final GenericBluetoothProfile p : mProfiles) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mDeviceView.addRowToTable(p.getTableRow());
                                        p.enableService();
                                        progressDialog.setProgress(progressDialog.getProgress() + 1);
                                    }
                                });
                                p.onResume();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hide();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    });
                    worker.start();
                } else {
                    Toast.makeText(getApplication(), "Fallo descubriendo servicios",
                            Toast.LENGTH_LONG).show();
                    return;
                }
            } else if (BluetoothLeService.ACTION_DATA_NOTIFY.equals(action)) {
                // Notification
                byte[] value = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                String uuidStr = intent.getStringExtra(BluetoothLeService.EXTRA_UUID);
                //Log.d("DeviceActivity","Got Characteristic : " + uuidStr);
                for (int ii = 0; ii < charList.size(); ii++) {
                    BluetoothGattCharacteristic tempC = charList.get(ii);
                    if ((tempC.getUuid().toString().equals(uuidStr))) {
                        for (int jj = 0; jj < mProfiles.size(); jj++) {
                            GenericBluetoothProfile p = mProfiles.get(jj);
                            if (p.isDataC(tempC)) {
                                p.didUpdateValueForCharacteristic(tempC);
                                //Do MQTT
                                Map<String, String> map = p.getMQTTMap();
                                if (map != null) {
                                    for (Map.Entry<String, String> e : map.entrySet()) {
                                        if (mqttProfile != null)
                                            mqttProfile.addSensorValueToPendingMessage(e);
                                    }
                                }
                            }
                        }
                        //Log.d("DeviceActivity","Got Characteristic : " + tempC.getUuid().toString());
                        break;
                    }
                }

                //onCharacteristicChanged(uuidStr, value);
            } else if (BluetoothLeService.ACTION_DATA_WRITE.equals(action)) {
                // Data written
                byte[] value = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                String uuidStr = intent.getStringExtra(BluetoothLeService.EXTRA_UUID);
                for (int ii = 0; ii < charList.size(); ii++) {
                    BluetoothGattCharacteristic tempC = charList.get(ii);
                    if ((tempC.getUuid().toString().equals(uuidStr))) {
                        for (int jj = 0; jj < mProfiles.size(); jj++) {
                            GenericBluetoothProfile p = mProfiles.get(jj);
                            p.didWriteValueForCharacteristic(tempC);
                        }
                        //Log.d("DeviceActivity","Got Characteristic : " + tempC.getUuid().toString());
                        break;
                    }
                }
            } else if (BluetoothLeService.ACTION_DATA_READ.equals(action)) {
                // Data read
                byte[] value = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                String uuidStr = intent.getStringExtra(BluetoothLeService.EXTRA_UUID);
                for (int ii = 0; ii < charList.size(); ii++) {
                    BluetoothGattCharacteristic tempC = charList.get(ii);
                    if ((tempC.getUuid().toString().equals(uuidStr))) {
                        for (int jj = 0; jj < mProfiles.size(); jj++) {
                            GenericBluetoothProfile p = mProfiles.get(jj);
                            p.didReadValueForCharacteristic(tempC);
                        }
                        //Log.d("DeviceActivity","Got Characteristic : " + tempC.getUuid().toString());
                        break;
                    }
                }
            } else {
                if (TIOADProfile.ACTION_PREPARE_FOR_OAD.equals(action)) {
                    new firmwareUpdateStart(progressDialog, context).execute();
                }
            }
            if (status != BluetoothGatt.GATT_SUCCESS) {
                setError("GATT error code: " + status);
            }
        }
    };

    class firmwareUpdateStart extends AsyncTask<String, Integer, Void> {
        ProgressDialog pd;
        Context con;

        public firmwareUpdateStart(ProgressDialog p, Context c) {
            this.pd = p;
            this.con = c;
        }

        @Override
        protected void onPreExecute() {
            this.pd = new ProgressDialog(DeviceActivity.this);
            this.pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.pd.setIndeterminate(false);
            this.pd.setTitle("Starting firmware update");
            this.pd.setMessage("");
            this.pd.setMax(mProfiles.size());
            this.pd.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            Integer ii = 1;
            for (GenericBluetoothProfile p : mProfiles) {

                p.disableService();
                p.deConfigureService();
                publishProgress(ii);
                ii = ii + 1;
            }

            if (isSensorTag2()) {
                final Intent i = new Intent(this.con, FwUpdateActivity_CC26xx.class);
                startActivityForResult(i, FWUPDATE_ACT_REQ);
            } else {
                final Intent i = new Intent(this.con, FwUpdateActivity.class);
                startActivityForResult(i, FWUPDATE_ACT_REQ);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            this.pd.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            this.pd.dismiss();
            super.onPostExecute(result);
        }

    }
}
