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

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String API_KEY = "dUpDbnYxWG1hdlFPejVBUXhPaXBrTXpBd1phbzBXeUZ4VlNlamludXhqQnFpZnplMEQ6Z2FuenUxM0dU";
    private static final String JSON_PARAM_TIMESTAMP = "timestamp";
    private static final String JSON_PARAM_VALUE = "value";
    private static final String API_PUSH_URL = "https://push.ducksboard.com/v/";

    /*
     * Se permitirá hacer push a los valores cada 3" y a las gráficas cada 60"
     */
    private static final long WIDGET_VALUE_PUT_INTERVAL = 3000;
    private static final long WIDGET_GRAPHIC_PUT_INTERVAL = 60000;

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


    private long _lastPutTemperatureValue;
    private long _lastPutTemperatureIRValue;
    private long _lastPutHumidityValue;
    private long _lastPutBarometerValue;
    private long _lastPutLuxometerValue;

    private long _lastPutTemperatureGraphic;
    private long _lastPutTemperatureIRGraphic;
    private long _lastPutHumidityGraphic;
    private long _lastPutBarometerGraphic;
    private long _lastPutLuxometerGraphic;

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
        _lastPutTemperatureValue = putDashboardValue(WIDGET_VALUE_TEMPERATURE, value, timepstamp, _lastPutTemperatureValue, WIDGET_VALUE_PUT_INTERVAL);
        _lastPutTemperatureGraphic = putDashboardValue(WIDGET_GRAPHIC_TEMPERATURE, value, timepstamp, _lastPutTemperatureGraphic, WIDGET_GRAPHIC_PUT_INTERVAL);
    }

    public void putTemperatureIRValue(Double value, long timepstamp) {
        _lastPutTemperatureIRValue = putDashboardValue(WIDGET_VALUE_TEMPERATURE_IR, value, timepstamp, _lastPutTemperatureIRValue, WIDGET_VALUE_PUT_INTERVAL);
        _lastPutTemperatureIRGraphic = putDashboardValue(WIDGET_GRAPHIC_TEMPERATURE_IR, value, timepstamp, _lastPutTemperatureIRGraphic, WIDGET_GRAPHIC_PUT_INTERVAL);
    }

    public void putHumidityValue(Double value, long timepstamp) {
        _lastPutHumidityValue = putDashboardValue(WIDGET_VALUE_HUMIDITY, value, timepstamp, _lastPutHumidityValue, WIDGET_VALUE_PUT_INTERVAL);
        _lastPutHumidityGraphic = putDashboardValue(WIDGET_GRAPHIC_HUMIDITY, value, timepstamp, _lastPutHumidityGraphic, WIDGET_GRAPHIC_PUT_INTERVAL);
    }

    public void putBarometerValue(Double value, long timepstamp) {
        _lastPutBarometerValue = putDashboardValue(WIDGET_VALUE_BAROMETER, value, timepstamp, _lastPutBarometerValue, WIDGET_VALUE_PUT_INTERVAL);
        _lastPutBarometerGraphic = putDashboardValue(WIDGET_GRAPHIC_BAROMETER, value, timepstamp, _lastPutBarometerGraphic, WIDGET_GRAPHIC_PUT_INTERVAL);
    }

    public void putLuxometerValue(Double value, long timepstamp) {
        _lastPutLuxometerValue = putDashboardValue(WIDGET_VALUE_LUXOMETER, value, timepstamp, _lastPutLuxometerValue, WIDGET_VALUE_PUT_INTERVAL);
        _lastPutLuxometerGraphic = putDashboardValue(WIDGET_GRAPHIC_LUXOMETER, value, timepstamp, _lastPutLuxometerGraphic, WIDGET_GRAPHIC_PUT_INTERVAL);
    }

    private long putDashboardValue(final String widgetID, final double value, final long timestamp, long lastPutTimestamp, long putInterval) {
        long currentTime = System.currentTimeMillis();
        long nextPutAllowed = lastPutTimestamp + putInterval;

        if (currentTime >= nextPutAllowed) {
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
            return currentTime;
        } else {
            return lastPutTimestamp;
        }
    }
}
