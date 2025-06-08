package com.sigeter.service;

import com.sigeter.model.DetailGempa;
import org.json.JSONObject;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class GempaBmkg {
    private static final String API_URL_BMKG = "https://data.bmkg.go.id/DataMKG/TEWS/autogempa.json";

    public DetailGempa fetchDataGempa() {
        try {
            URL url = new URL(API_URL_BMKG);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            InputStream responseStream = conn.getInputStream();
            Scanner scanner = new Scanner(responseStream);
            String json = scanner.useDelimiter("\\A").next();
            scanner.close();

            JSONObject root = new JSONObject(json);
            JSONObject detailGempa = root.getJSONObject("Infogempa").getJSONObject("gempa");
            String potensi = detailGempa.getString("Potensi").contains("dirasakan") ? "Gempa Dirasakan" : "Berpotensi Tsunami" ;
            
            return new DetailGempa(
                    detailGempa.getString("Magnitude"),
                    detailGempa.getString("Kedalaman"),
                    potensi,
                    detailGempa.getString("Coordinates"),
                    detailGempa.getString("Tanggal"),
                    detailGempa.getString("Jam"),
                    detailGempa.getString("Wilayah"),
                    detailGempa.getString("Shakemap")
            );
        } catch (Exception e) {
            System.out.println("Response dari API: " + e.getMessage());
            return null;
        }
    }
}
