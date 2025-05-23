package com.sigeter.service;

import com.sigeter.model.DetailGempa;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class GempaUsgs {

    public static String getFormattedDate(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZonedDateTime dateTime = instant.atZone(ZoneOffset.UTC);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.forLanguageTag("id"));
        return dateTime.format(dateFormatter);
    }

    public static String getFormattedTime(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZonedDateTime dateTime = instant.atZone(ZoneOffset.UTC);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return dateTime.format(timeFormatter);
    }

    public List<DetailGempa> fetchDataGempa(String apiUrl) {
        List<DetailGempa> listGempa = new ArrayList<>();
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            InputStream responseStream = conn.getInputStream();
            Scanner scanner = new Scanner(responseStream);
            String json = scanner.useDelimiter("\\A").next();
            scanner.close();

            JSONObject root = new JSONObject(json);
            JSONArray features = root.getJSONArray("features");

//            int jumlahData = Math.min(10, features.length()); // limit data
            int jumlahData = features.length();
            for (int i = 0; i < jumlahData; i++) {
                JSONObject feature = features.getJSONObject(i);
                JSONObject properties = feature.getJSONObject("properties");
                JSONObject geometry = feature.getJSONObject("geometry");
                JSONArray coordinates = geometry.getJSONArray("coordinates");

                String mag = String.valueOf(properties.getDouble("mag"));
                String place = properties.getString("place");
                long times = properties.getLong("time");
                String felt = properties.isNull("felt") ? "Tidak Berpotensi Tsunami" : "Berpotensi Tsunami";
                String date = getFormattedDate(times);
                String time = getFormattedTime(times);

                String kordinat = coordinates.get(0).toString() + ", " + coordinates.get(1).toString();
                String depth = coordinates.get(2).toString();
                String maps = properties.getString("url");

                listGempa.add(new DetailGempa(
                    mag, depth, felt, kordinat, date, time, place, maps
                ));
            }
        } catch (Exception e) {
            System.out.println("[API USGS]: " + e.getMessage());
            return null;
        }
        return listGempa;
    } 

}
