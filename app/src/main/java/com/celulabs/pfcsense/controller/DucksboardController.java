package com.celulabs.pfcsense.controller;

import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Controlador de envío de datos al dashboard de Ducksboard
 *
 * @author gonztirado
 */
public class DucksboardController {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");


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

    public void sendValue(Double value, long timepstamp) {

        final String url = "https://push.ducksboard.com/v/730979";
        final String json = "{\n" +
                "    \"timestamp\": " + timepstamp + ",\n" +
                "    \"value\": " + value + "\n" +
                "  }";

        Thread sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient _httpClient = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, json);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .header("Authorization", "Basic dUpDbnYxWG1hdlFPejVBUXhPaXBrTXpBd1phbzBXeUZ4VlNlamludXhqQnFpZnplMEQ6Z2FuenUxM0dU")
                        .build();
                try {
                    Response response = _httpClient.newCall(request).execute();
                    String responseString = response.body().string();

                    Log.d("DucksboardController", "send to Ducksboard response " + response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        sendThread.start();
    }
}
