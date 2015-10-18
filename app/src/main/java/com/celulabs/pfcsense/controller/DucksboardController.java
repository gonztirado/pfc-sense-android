package com.celulabs.pfcsense.controller;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONObject;

/**
 * Controlador de envío de datos al dashboard de Ducksboard
 *
 * @author gonztirado
 */
public class DucksboardController {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    public static final String API_KEY = "dUpDbnYxWG1hdlFPejVBUXhPaXBrTXpBd1phbzBXeUZ4VlNlamludXhqQnFpZnplMEQ6Z2FuenUxM0dU";
    public static final String JSON_PARAM_TIMESTAMP = "timestamp";
    public static final String JSON_PARAM_VALUE = "value";
    public static final String API_PUSH_URL = "https://push.ducksboard.com/v/";
    public static final String WIDGET_VALUE_TEMPERATURA = "730979";


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

    public void putTemperatureValue(Double value, long timepstamp) {
        putDashboardValue(WIDGET_VALUE_TEMPERATURA, value, timepstamp);
    }

    public void putDashboardValue(final String widgetID, final double value, final long timestamp) {
        Thread sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(JSON_PARAM_VALUE, value);
                    jsonObject.put(JSON_PARAM_TIMESTAMP, timestamp);
                    String json = jsonObject.toString();
                    String url = API_PUSH_URL + widgetID;

                    OkHttpClient _httpClient = new OkHttpClient();
                    RequestBody body = RequestBody.create(JSON, json);
                    Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .header("Authorization", "Basic " + API_KEY)
                            .build();

                    _httpClient.newCall(request).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        sendThread.start();
    }
}
