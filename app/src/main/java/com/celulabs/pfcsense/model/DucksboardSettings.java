package com.celulabs.pfcsense.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Modelo que contiene la configuración del panel Ducksboard para cada sensor
 *
 * @author gonztirado
 */
@ParseClassName("DucksboardSettings")
public class DucksboardSettings extends ParseObject {

    public static final String PROPERTY_SENSOR_ID = SensorInfo.PROPERTY_SENSOR_ID;
    public static final String PROPERTY_SENSOR_NAME = SensorInfo.PROPERTY_SENSOR_NAME;
    private static final String PROPERTY_API_KEY = "apiKey";

    private static final String PROPERTY_VALUE_PUSH_INTERVAL = "valuePushInteval";
    private static final String PROPERTY_GRAPHIC_PUSH_INTERVAL = "graphicPushInterval";

    private static final String PROPERTY_VALUE_TEMPERATURE_WIDGET_ID = "valueTemperatureWidgetID";
    private static final String PROPERTY_VALUE_TEMPERATURE_IR_WIDGET_ID = "valueTemperatureIRWidgetID";
    private static final String PROPERTY_VALUE_HUMIDITY_WIDGET_ID = "valueHumidityWidgetID";
    private static final String PROPERTY_VALUE_BAROMETER_WIDGET_ID = "valueBarometerWidgetID";
    private static final String PROPERTY_VALUE_LUXOMETER_WIDGET_ID = "valueLuxometerWidgetID";
    private static final String PROPERTY_GRAPHIC_TEMPERATURE_WIDGET_ID = "graphicTemperatureWidgetID";
    private static final String PROPERTY_GRAPHIC_TEMPERATURE_IR_WIDGET_ID = "graphicTemperatureIRWidgetID";
    private static final String PROPERTY_GRAPHIC_HUMIDITY_WIDGET_ID = "graphicHumidityWidgetID";
    private static final String PROPERTY_GRAPHIC_BAROMETER_WIDGET_ID = "graphicBarometerWidgetID";
    private static final String PROPERTY_GRAPHIC_LUXOMETER_WIDGET_ID = "graphicLuxometerWidgetID";

    private static final String DEFAULT_API_KEY = "dUpDbnYxWG1hdlFPejVBUXhPaXBrTXpBd1phbzBXeUZ4VlNlamludXhqQnFpZnplMEQ6Z2FuenUxM0dU";
    private static final long DEFAULT_VALUE_PUSH_INTERVAL = 3000;
    private static final long DEFAULT_GRAPHIC_PUSH_INTERVAL = 60000;

    private static final String DEFAULT_WIDGET_VALUE_TEMPERATURE = "730979";
    private static final String DEFAULT_WIDGET_VALUE_TEMPERATURE_IR = "730980";
    private static final String DEFAULT_WIDGET_VALUE_HUMIDITY = "730981";
    private static final String DEFAULT_WIDGET_VALUE_BAROMETER = "730982";
    private static final String DEFAULT_WIDGET_VALUE_LUXOMETER = "730983";
    private static final String DEFAULT_WIDGET_GRAPHIC_TEMPERATURE = "730985";
    private static final String DEFAULT_WIDGET_GRAPHIC_TEMPERATURE_IR = "730986";
    private static final String DEFAULT_WIDGET_GRAPHIC_HUMIDITY = "730988";
    private static final String DEFAULT_WIDGET_GRAPHIC_BAROMETER = "730989";
    private static final String DEFAULT_WIDGET_GRAPHIC_LUXOMETER = "730990";

    public void setDefaultValues() {
        setApiKey(DEFAULT_API_KEY);
        setValuePushInterval(DEFAULT_VALUE_PUSH_INTERVAL);
        setGraphicPushInterval(DEFAULT_GRAPHIC_PUSH_INTERVAL);
        setValueTemperatureWidgetID(DEFAULT_WIDGET_VALUE_TEMPERATURE);
        setValueTemperatureIRWidgetID(DEFAULT_WIDGET_VALUE_TEMPERATURE_IR);
        setValueHumidityWidgetID(DEFAULT_WIDGET_VALUE_HUMIDITY);
        setValueBarometerWidgetID(DEFAULT_WIDGET_VALUE_BAROMETER);
        setValueLuxometerWidgetID(DEFAULT_WIDGET_VALUE_LUXOMETER);
        setGraphicTemperatureWidgetID(DEFAULT_WIDGET_GRAPHIC_TEMPERATURE);
        setGraphicTemperatureIRWidgetID(DEFAULT_WIDGET_GRAPHIC_TEMPERATURE_IR);
        setGraphicHumidityWidgetID(DEFAULT_WIDGET_GRAPHIC_HUMIDITY);
        setGraphicBarometerWidgetID(DEFAULT_WIDGET_GRAPHIC_BAROMETER);
        setGraphicLuxometerWidgetID(DEFAULT_WIDGET_GRAPHIC_LUXOMETER);
    }

    public String getSensorId() {
        return getString(PROPERTY_SENSOR_ID);
    }

    public void setSensorId(String sensorId) {
        put(PROPERTY_SENSOR_ID, sensorId);
    }

    public String getSensorName() {
        return getString(PROPERTY_SENSOR_NAME);
    }

    public void setSensorName(String sensorName) {
        put(PROPERTY_SENSOR_NAME, sensorName);
    }

    public String getApiKey() {
        return getString(PROPERTY_API_KEY);
    }

    public void setApiKey(String apiKey) {
        put(PROPERTY_API_KEY, apiKey);
    }

    public long getValuePushInterval() {
        return getLong(PROPERTY_VALUE_PUSH_INTERVAL);
    }

    public void setValuePushInterval(long valuePushInterval) {
        put(PROPERTY_VALUE_PUSH_INTERVAL, valuePushInterval);
    }

    public long getGraphicPushInterval() {
        return getLong(PROPERTY_GRAPHIC_PUSH_INTERVAL);
    }

    public void setGraphicPushInterval(long graphicPushInterval) {
        put(PROPERTY_GRAPHIC_PUSH_INTERVAL, graphicPushInterval);
    }

    public String getValueTemperatureWidgetID() {
        return getString(PROPERTY_VALUE_TEMPERATURE_WIDGET_ID);
    }

    public void setValueTemperatureWidgetID(String valueTemperatureWidgetID) {
        put(PROPERTY_VALUE_TEMPERATURE_WIDGET_ID, valueTemperatureWidgetID);
    }

    public String getValueTemperatureIRWidgetID() {
        return getString(PROPERTY_VALUE_TEMPERATURE_IR_WIDGET_ID);
    }

    public void setValueTemperatureIRWidgetID(String valueTemperatureIRWidgetID) {
        put(PROPERTY_VALUE_TEMPERATURE_IR_WIDGET_ID, valueTemperatureIRWidgetID);
    }

    public String getValueHumidityWidgetID() {
        return getString(PROPERTY_VALUE_HUMIDITY_WIDGET_ID);
    }

    public void setValueHumidityWidgetID(String valueHumidityWidgetID) {
        put(PROPERTY_VALUE_HUMIDITY_WIDGET_ID, valueHumidityWidgetID);
    }

    public String getValueBarometerWidgetID() {
        return getString(PROPERTY_VALUE_BAROMETER_WIDGET_ID);
    }

    public void setValueBarometerWidgetID(String valueBarometerWidgetID) {
        put(PROPERTY_VALUE_BAROMETER_WIDGET_ID, valueBarometerWidgetID);
    }

    public String getValueLuxometerWidgetID() {
        return getString(PROPERTY_VALUE_LUXOMETER_WIDGET_ID);
    }

    public void setValueLuxometerWidgetID(String valueLuxometerWidgetID) {
        put(PROPERTY_VALUE_LUXOMETER_WIDGET_ID, valueLuxometerWidgetID);
    }

    public String getGraphicTemperatureWidgetID() {
        return getString(PROPERTY_GRAPHIC_TEMPERATURE_WIDGET_ID);
    }

    public void setGraphicTemperatureWidgetID(String graphicTemperatureWidgetID) {
        put(PROPERTY_GRAPHIC_TEMPERATURE_WIDGET_ID, graphicTemperatureWidgetID);
    }

    public String getGraphicTemperatureIRWidgetID() {
        return getString(PROPERTY_GRAPHIC_TEMPERATURE_IR_WIDGET_ID);
    }

    public void setGraphicTemperatureIRWidgetID(String graphicTemperatureIRWidgetID) {
        put(PROPERTY_GRAPHIC_TEMPERATURE_IR_WIDGET_ID, graphicTemperatureIRWidgetID);
    }

    public String getGraphicHumidityWidgetID() {
        return getString(PROPERTY_GRAPHIC_HUMIDITY_WIDGET_ID);
    }

    public void setGraphicHumidityWidgetID(String graphicHumidityWidgetID) {
        put(PROPERTY_GRAPHIC_HUMIDITY_WIDGET_ID, graphicHumidityWidgetID);
    }

    public String getGraphicBarometerWidgetID() {
        return getString(PROPERTY_GRAPHIC_BAROMETER_WIDGET_ID);
    }

    public void setGraphicBarometerWidgetID(String graphicBarometerWidgetID) {
        put(PROPERTY_GRAPHIC_BAROMETER_WIDGET_ID, graphicBarometerWidgetID);
    }

    public String getGraphicLuxometerWidgetID() {
        return getString(PROPERTY_GRAPHIC_LUXOMETER_WIDGET_ID);
    }

    public void setGraphicLuxometerWidgetID(String graphicLuxometerWidgetID) {
        put(PROPERTY_GRAPHIC_LUXOMETER_WIDGET_ID, graphicLuxometerWidgetID);
    }
}
