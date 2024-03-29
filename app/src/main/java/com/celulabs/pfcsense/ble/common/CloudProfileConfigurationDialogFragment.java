package com.celulabs.pfcsense.ble.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.celulabs.pfcsense.ble.sensortag.R;

import java.util.Map;

public class CloudProfileConfigurationDialogFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {

    public final static String PREF_CLOUD_SERVICE = "cloud_service";
    public final static String PREF_CLOUD_USERNAME = "cloud_username";
    public final static String PREF_CLOUD_PASSWORD = "cloud_password";
    public final static String PREF_CLOUD_DEVICE_ID = "cloud_device_id";
    public final static String PREF_CLOUD_BROKER_ADDR = "cloud_broker_address";
    public final static String PREF_CLOUD_BROKER_PORT = "cloud_broker_port";
    public final static String PREF_CLOUD_PUBLISH_TOPIC = "cloud_publish_topic";
    public final static String PREF_CLOUD_CLEAN_SESSION = "cloud_clean_session";
    public final static String PREF_CLOUD_USE_SSL = "cloud_use_ssl";
    public final static String ACTION_CLOUD_CONFIG_WAS_UPDATED = "com.celulabs.pfcsense.ble.common.CloudProfileConfigurationDialogFragment.UPDATE";
    public final static String DEF_CLOUD_IBMQUICKSTART_BROKER_ADDR = "tcp://quickstart.messaging.internetofthings.ibmcloud.com";
    public final static String DEF_CLOUD_IBMQUICKSTART_BROKER_PORT = "1883";
    public final static String DEF_CLOUD_IBMQUICKSTART_PUBLISH_TOPIC = "iot-2/evt/status/fmt/json";
    public final static String DEF_CLOUD_IBMQUICKSTART_USERNAME = "";
    public final static String DEF_CLOUD_IBMQUICKSTART_PASSWORD = "";
    public final static String DEF_CLOUD_IBMQUICKSTART_DEVICEID_PREFIX = "d:quickstart:\"st-app\":";
    public final static Boolean DEF_CLOUD_IBMQUICKSTART_CLEAN_SESSION = true;
    public final static Boolean DEF_CLOUD_IBMQUICKSTART_USE_SSL = false;
    public final static Integer DEF_CLOUD_IBMQUICKSTART_CLOUD_SERVICE = 0;
    public final static Integer DEF_CLOUD_IBMIOTFOUNDATION_CLOUD_SERVICE = 1;
    public final static Integer DEF_CLOUD_CUSTOM_CLOUD_SERVICE = 2;





    private String deviceId = "";
    private View v;

    SharedPreferences prefs = null;

    public CloudProfileConfigurationDialogFragment(String devId) {
        deviceId = devId;
    }
    public static CloudProfileConfigurationDialogFragment newInstance(String devId) {
        CloudProfileConfigurationDialogFragment frag = new CloudProfileConfigurationDialogFragment(devId);
        Bundle args = new Bundle();
        return frag;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder cloudDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Cloud configuration")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Integer sel = ((Spinner)v.findViewById(R.id.cloud_spinner)).getSelectedItemPosition();
                        CloudProfileConfigurationDialogFragment.setCloudPref(PREF_CLOUD_SERVICE,sel.toString(),getActivity());
                        CloudProfileConfigurationDialogFragment.setCloudPref(PREF_CLOUD_USERNAME,((EditText)v.findViewById(R.id.cloud_username)).getText().toString(),getActivity());
                        CloudProfileConfigurationDialogFragment.setCloudPref(PREF_CLOUD_PASSWORD,((EditText)v.findViewById(R.id.cloud_password)).getText().toString(),getActivity());
                        CloudProfileConfigurationDialogFragment.setCloudPref(PREF_CLOUD_DEVICE_ID,((EditText)v.findViewById(R.id.cloud_deviceid)).getText().toString(),getActivity());
                        CloudProfileConfigurationDialogFragment.setCloudPref(PREF_CLOUD_BROKER_ADDR,((EditText)v.findViewById(R.id.cloud_broker_address)).getText().toString(),getActivity());
                        CloudProfileConfigurationDialogFragment.setCloudPref(PREF_CLOUD_BROKER_PORT,((EditText)v.findViewById(R.id.cloud_broker_port)).getText().toString(),getActivity());
                        CloudProfileConfigurationDialogFragment.setCloudPref(PREF_CLOUD_PUBLISH_TOPIC,((EditText)v.findViewById(R.id.cloud_publish_topic)).getText().toString(),getActivity());
                        boolean cleanSession = ((CheckBox)v.findViewById(R.id.cloud_clean_session_checkbox)).isChecked();
                        String cleanSessionString;
                        if (cleanSession) {
                            cleanSessionString = "true";
                        }
                        else cleanSessionString = "false";
                        CloudProfileConfigurationDialogFragment.setCloudPref(PREF_CLOUD_CLEAN_SESSION,cleanSessionString,getActivity());

                        boolean useSSL = ((CheckBox)v.findViewById(R.id.cloud_use_ssl_checkbox)).isChecked();
                        String useSSLString;
                        if (useSSL) {
                            useSSLString = "true";
                        }
                        else useSSLString = "false";
                        CloudProfileConfigurationDialogFragment.setCloudPref(PREF_CLOUD_USE_SSL,useSSLString,getActivity());

                        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        Map <String, ?> keys = prefs.getAll();
                        for (Map.Entry<String,?> entry : keys.entrySet()){
                            Log.d("CloudProfileConfigurationDialogFragment",entry.getKey() + ":" + entry.getValue().toString());
                        }

                        final Intent intent = new Intent(ACTION_CLOUD_CONFIG_WAS_UPDATED);
                        getActivity().sendBroadcast(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(),"No values changed",Toast.LENGTH_LONG);
                    }
                });


        LayoutInflater i = getActivity().getLayoutInflater();

        v = i.inflate(R.layout.cloud_config_dialog, null);
        cloudDialog.setTitle("Cloud Setup");
        Spinner spinner = (Spinner) v.findViewById(R.id.cloud_spinner);



        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.cloud_config_dialog_cloud_services_array,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        spinner.setOnItemSelectedListener(this);
        try {
            Integer sel = Integer.parseInt(CloudProfileConfigurationDialogFragment.retrieveCloudPref(CloudProfileConfigurationDialogFragment.PREF_CLOUD_SERVICE,getActivity()),10);
            spinner.setSelection(sel);
        }
        catch (Exception e) {

        }
/*
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


        });
*/
        cloudDialog.setView(v);
        return cloudDialog.create();


    }

/*
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {


    }
    */
    public void enDisUsername (boolean enable,String username) {
        TextView t = (TextView)v.findViewById(R.id.cloud_username_label);
        EditText e = (EditText)v.findViewById(R.id.cloud_username);
        e.setEnabled(enable);
        e.setText(username);
        if (enable) {
            t.setAlpha(1.0f);
            e.setAlpha(1.0f);
        }
        else {
            t.setAlpha(0.4f);
            e.setAlpha(0.4f);
        }

    }
    public void enDisPassword (boolean enable,String password) {
        TextView t = (TextView)v.findViewById(R.id.cloud_password_label);
        EditText e = (EditText)v.findViewById(R.id.cloud_password);
        e.setEnabled(enable);
        e.setText(password);
        if (enable) {
            t.setAlpha(1.0f);
            e.setAlpha(1.0f);
        }
        else {
            t.setAlpha(0.4f);
            e.setAlpha(0.4f);
        }

    }
    public void setDeviceId (String deviceId) {
        EditText e = (EditText)v.findViewById(R.id.cloud_deviceid);
        e.setText(deviceId);
    }

    public void enDisBrokerAddressPort(boolean en,String brokerAddress, String brokerPort) {
        TextView t = (TextView)v.findViewById(R.id.cloud_broker_address_label);
        EditText e = (EditText)v.findViewById(R.id.cloud_broker_address);
        TextView tP = (TextView)v.findViewById(R.id.cloud_broker_port_label);
        EditText eP = (EditText)v.findViewById(R.id.cloud_broker_port);

        e.setEnabled(en);
        eP.setEnabled(en);
        e.setText(brokerAddress);
        eP.setText(brokerPort);
        if (en) {
            t.setAlpha(1.0f);
            e.setAlpha(1.0f);
            tP.setAlpha(1.0f);
            eP.setAlpha(1.0f);
        }
        else {
            t.setAlpha(0.4f);
            tP.setAlpha(0.4f);
            e.setAlpha(0.4f);
            eP.setAlpha(0.4f);
        }
    }
    public void enDisTopic(boolean en, String topic) {
        TextView t = (TextView)v.findViewById(R.id.cloud_publish_topic_label);
        EditText e = (EditText)v.findViewById(R.id.cloud_publish_topic);

        e.setEnabled(en);
        e.setText(topic);

        if (en) {
            t.setAlpha(1.0f);
            e.setAlpha(1.0f);
        }
        else {
            t.setAlpha(0.4f);
            e.setAlpha(0.4f);
        }
    }

    public void enDisCleanSession(boolean en,boolean checked) {
        CheckBox c = (CheckBox)v.findViewById(R.id.cloud_clean_session_checkbox);
        c.setEnabled(en);
        c.setChecked(checked);
    }

    public void enDisUseSSL(boolean en, boolean checked) {
        CheckBox c = (CheckBox)v.findViewById(R.id.cloud_use_ssl_checkbox);
        c.setEnabled(en);
        c.setChecked(checked);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d("CloudProfileConfigurationDialogFragment", "onItemSelected :" + position);
        switch (position) {
            case 0:
                //IBM IoT Quick Start
                enDisUsername(false,DEF_CLOUD_IBMQUICKSTART_USERNAME);
                enDisPassword(false,DEF_CLOUD_IBMQUICKSTART_PASSWORD);
                setDeviceId(DEF_CLOUD_IBMQUICKSTART_DEVICEID_PREFIX + deviceId);
                enDisBrokerAddressPort(false,CloudProfileConfigurationDialogFragment.DEF_CLOUD_IBMQUICKSTART_BROKER_ADDR,CloudProfileConfigurationDialogFragment.DEF_CLOUD_IBMQUICKSTART_BROKER_PORT);
                enDisTopic(false,CloudProfileConfigurationDialogFragment.DEF_CLOUD_IBMQUICKSTART_PUBLISH_TOPIC);
                enDisCleanSession(false,CloudProfileConfigurationDialogFragment.DEF_CLOUD_IBMQUICKSTART_CLEAN_SESSION);
                enDisUseSSL(false,CloudProfileConfigurationDialogFragment.DEF_CLOUD_IBMQUICKSTART_USE_SSL);
                break;
            case 1:
                enDisUsername(true,CloudProfileConfigurationDialogFragment.retrieveCloudPref(CloudProfileConfigurationDialogFragment.PREF_CLOUD_USERNAME,getActivity()));
                enDisPassword(true,CloudProfileConfigurationDialogFragment.retrieveCloudPref(CloudProfileConfigurationDialogFragment.PREF_CLOUD_PASSWORD,getActivity()));
                setDeviceId(CloudProfileConfigurationDialogFragment.retrieveCloudPref(CloudProfileConfigurationDialogFragment.PREF_CLOUD_DEVICE_ID,getActivity()));
                enDisBrokerAddressPort(true,CloudProfileConfigurationDialogFragment.retrieveCloudPref(CloudProfileConfigurationDialogFragment.PREF_CLOUD_BROKER_ADDR,getActivity()),
                                            CloudProfileConfigurationDialogFragment.retrieveCloudPref(CloudProfileConfigurationDialogFragment.PREF_CLOUD_BROKER_PORT,getActivity()));
                enDisTopic(true,CloudProfileConfigurationDialogFragment.retrieveCloudPref(CloudProfileConfigurationDialogFragment.PREF_CLOUD_PUBLISH_TOPIC,getActivity()));
                boolean cleanSession;
                try {
                    cleanSession = Boolean.parseBoolean(CloudProfileConfigurationDialogFragment.retrieveCloudPref(CloudProfileConfigurationDialogFragment.PREF_CLOUD_CLEAN_SESSION,getActivity()));
                }
                catch (Exception e) {
                    e.printStackTrace();
                    cleanSession = false;
                }
                enDisCleanSession(true,cleanSession);
                boolean useSSL;
                try {
                   useSSL = Boolean.parseBoolean(CloudProfileConfigurationDialogFragment.retrieveCloudPref(CloudProfileConfigurationDialogFragment.PREF_CLOUD_USE_SSL,getActivity()));
                }
                catch (Exception e) {
                    e.printStackTrace();
                    useSSL = false;
                }
                enDisUseSSL(true,useSSL);
                break;
            case 2:
                enDisUsername(true,CloudProfileConfigurationDialogFragment.retrieveCloudPref(CloudProfileConfigurationDialogFragment.PREF_CLOUD_USERNAME,getActivity()));
                enDisPassword(true,CloudProfileConfigurationDialogFragment.retrieveCloudPref(CloudProfileConfigurationDialogFragment.PREF_CLOUD_PASSWORD,getActivity()));
                setDeviceId(CloudProfileConfigurationDialogFragment.retrieveCloudPref(CloudProfileConfigurationDialogFragment.PREF_CLOUD_DEVICE_ID,getActivity()));
                enDisBrokerAddressPort(true,CloudProfileConfigurationDialogFragment.retrieveCloudPref(CloudProfileConfigurationDialogFragment.PREF_CLOUD_BROKER_ADDR,getActivity()),
                                            CloudProfileConfigurationDialogFragment.retrieveCloudPref(CloudProfileConfigurationDialogFragment.PREF_CLOUD_BROKER_PORT,getActivity()));
                enDisTopic(true,CloudProfileConfigurationDialogFragment.retrieveCloudPref(CloudProfileConfigurationDialogFragment.PREF_CLOUD_PUBLISH_TOPIC,getActivity()));
                try {
                    cleanSession = Boolean.parseBoolean(CloudProfileConfigurationDialogFragment.retrieveCloudPref(CloudProfileConfigurationDialogFragment.PREF_CLOUD_CLEAN_SESSION,getActivity()));
                }
                catch (Exception e) {
                    e.printStackTrace();
                    cleanSession = false;
                }
                enDisCleanSession(true,cleanSession);
                try {
                    useSSL = Boolean.parseBoolean(CloudProfileConfigurationDialogFragment.retrieveCloudPref(CloudProfileConfigurationDialogFragment.PREF_CLOUD_USE_SSL,getActivity()));
                }
                catch (Exception e) {
                    e.printStackTrace();
                    useSSL = false;
                }
                enDisUseSSL(true,useSSL);
                break;
            default:
                enDisUsername(true,CloudProfileConfigurationDialogFragment.retrieveCloudPref(CloudProfileConfigurationDialogFragment.PREF_CLOUD_USERNAME,getActivity()));
                enDisPassword(true,CloudProfileConfigurationDialogFragment.retrieveCloudPref(CloudProfileConfigurationDialogFragment.PREF_CLOUD_PASSWORD,getActivity()));
                setDeviceId(CloudProfileConfigurationDialogFragment.retrieveCloudPref(CloudProfileConfigurationDialogFragment.PREF_CLOUD_DEVICE_ID,getActivity()));
                enDisBrokerAddressPort(true,CloudProfileConfigurationDialogFragment.retrieveCloudPref(CloudProfileConfigurationDialogFragment.PREF_CLOUD_BROKER_ADDR,getActivity()),
                                            CloudProfileConfigurationDialogFragment.retrieveCloudPref(CloudProfileConfigurationDialogFragment.PREF_CLOUD_BROKER_PORT,getActivity()));
                enDisTopic(true,CloudProfileConfigurationDialogFragment.retrieveCloudPref(CloudProfileConfigurationDialogFragment.PREF_CLOUD_PUBLISH_TOPIC,getActivity()));
                try {
                    cleanSession = Boolean.parseBoolean(CloudProfileConfigurationDialogFragment.retrieveCloudPref(CloudProfileConfigurationDialogFragment.PREF_CLOUD_CLEAN_SESSION,getActivity()));
                }
                catch (Exception e) {
                    e.printStackTrace();
                    cleanSession = false;
                }
                enDisCleanSession(true,cleanSession);
                try {
                    useSSL = Boolean.parseBoolean(CloudProfileConfigurationDialogFragment.retrieveCloudPref(CloudProfileConfigurationDialogFragment.PREF_CLOUD_USE_SSL,getActivity()));
                }
                catch (Exception e) {
                    e.printStackTrace();
                    useSSL = false;
                }
                enDisUseSSL(true,useSSL);
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d("CloudProfileConfigurationDialogFragment","onNothingSelected" + parent);
    }

    public static String retrieveCloudPref(String prefName,Context con) {
            String preferenceKeyString = "pref_cloud_config_"
                    + prefName;

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(con);

            String defaultValue = "NS";
            return prefs.getString(preferenceKeyString, defaultValue);
    }
    public static boolean setCloudPref(String prefName, String prefValue, Context con) {
        String preferenceKeyString = "pref_cloud_config_"
                + prefName;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(con);

        String defaultValue = "NS";

        SharedPreferences.Editor ed = prefs.edit();
        ed.putString(preferenceKeyString, prefValue);
        return ed.commit();
    }

}
