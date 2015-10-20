package com.celulabs.pfcsense.controller;

import com.celulabs.pfcsense.model.SensorData;
import com.celulabs.pfcsense.model.TemperatureData;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Controlador de envío de datos al dashboard de Ducksboard
 *
 * @author gonztirado
 */
public class DucksboardController {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String API_KEY = "dUpDbnYxWG1hdlFPejVBUXhPaXBrTXpBd1phbzBXeUZ4VlNlamludXhqQnFpZnplMEQ6Z2FuenUxM0dU";
    private static final String JSON_PARAM_TIMESTAMP = "timestamp";
    private static final String JSON_PARAM_VALUE = "value";
    private static final String API_PUSH_URL = "https://push.ducksboard.com/v/";

    private static final String WIDGET_VALUE_TEMPERATURE = "730979";
    private static final String WIDGET_VALUE_TEMPERATURE_IR = "730980";
    private static final String WIDGET_VALUE_HUMIDITY = "730981";
    private static final String WIDGET_VALUE_BAROMETER = "730982";
    private static final String WIDGET_VALUE_LUXOMETER = "730983";

    private static final String WIDGET_GRAPHIC_TEMPERATURE = "730985";
    private static final String WIDGET_GRAPHIC_TEMPERATURE_IR = "730986";
    private static final String WIDGET_GRAPHIC_HUMIDITY = "730988";
    private static final String WIDGET_GRAPHIC_BAROMETER = "730989";
    private static final String WIDGET_GRAPHIC_LUXOMETER = "730990";


    private static DucksboardController ourInstance = new DucksboardController();

    /**
     * @return devuelve la instancia del controlador
     */
    public static DucksboardController getInstance() {
        return ourInstance;
    }

    /**
     * Constructor
     */
    private DucksboardController() {
    }

    /**
     * Envía al dashboard un dato de temperatura
     *
     * @param sensorData
     */
    public void pushTemperatureData(TemperatureData sensorData) {
        pushData(sensorData, WIDGET_VALUE_TEMPERATURE);
        pushData(sensorData, WIDGET_GRAPHIC_TEMPERATURE);
    }

    public void pushTemperatureIRData(SensorData sensorData) {
        pushData(sensorData, WIDGET_VALUE_TEMPERATURE_IR);
        pushData(sensorData, WIDGET_GRAPHIC_TEMPERATURE_IR);
    }

    public void pushHumidityData(SensorData sensorData) {
        pushData(sensorData, WIDGET_VALUE_HUMIDITY);
        pushData(sensorData, WIDGET_GRAPHIC_HUMIDITY);
    }

    public void pushBarometerData(SensorData sensorData) {
        pushData(sensorData, WIDGET_VALUE_BAROMETER);
        pushData(sensorData, WIDGET_GRAPHIC_BAROMETER);
    }

    public void pushLuxometerData(SensorData sensorData) {
        pushData(sensorData, WIDGET_VALUE_LUXOMETER);
        pushData(sensorData, WIDGET_GRAPHIC_LUXOMETER);
    }


    private void pushData(SensorData sensorData, String widgetID) {
        JSONObject jsonObject = getJsonObject(sensorData);
        pushDashboardRequest(widgetID, jsonObject.toString());
    }

    private JSONObject getJsonObject(SensorData sensorData) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JSON_PARAM_VALUE, sensorData.getValue());
            jsonObject.put(JSON_PARAM_TIMESTAMP, sensorData.getTimestamp() / 1000);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    private void pushDashboardRequest(final String widgetID, final String jsonRequest) {

        Thread sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = API_PUSH_URL + widgetID;
                    OkHttpClient httpClient = new OkHttpClient();
                    RequestBody body = RequestBody.create(JSON, jsonRequest);
                    Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .header("Authorization", "Basic " + API_KEY)
                            .build();

                    httpClient.newCall(request).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        sendThread.start();
    }
}
