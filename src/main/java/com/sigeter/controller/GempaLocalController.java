package com.sigeter.controller;

import com.sigeter.model.DataShare;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.sigeter.service.GempaBmkg;
import com.sigeter.model.DetailGempa;
import java.text.DecimalFormat;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GempaLocalController implements Initializable {
    private GempaBmkg service;
    
    @FXML private Label date,mag,dlm,wil,pot,cor,map;
    @FXML private WebView mapsBmkg;
    @FXML private Button addCatatanLocal;
    
    public GempaLocalController() {
        this.service = new GempaBmkg();
    }
    
    public DetailGempa getData() {
        try {
            DetailGempa gempa = service.fetchDataGempa();
            return gempa;
        } catch( Exception e ) {
            e.printStackTrace();
            return null;
        }
    }
    
    private void tampilkanDataGempa() {
        DetailGempa gempa = getData();
               
        String latFormatted;
        String lonFormatted;
        String latDirection;
        String lonDirection;
                
        if (gempa != null) {
            date.setText(gempa.getTanggal() + ", " + gempa.getJam());
            mag.setText(gempa.getMagnitude());
            dlm.setText(gempa.getKedalaman());
            wil.setText(gempa.getWilayah());
            pot.setText(gempa.getPotensi());

            try {
                String cordinate = gempa.getCordinate();
                String[] parts = cordinate.split(",");
                double latitude = Double.parseDouble(parts[0]);
                double longitude = Double.parseDouble(parts[1]);

                DecimalFormat df = new DecimalFormat("#.##");

                //Latitude (LS/LU)
                if (latitude < 0) {
                    latDirection = "LS";
                    latFormatted = df.format(Math.abs(latitude));
                } else {
                    latDirection = "LU";
                    latFormatted = df.format(latitude);
                }

                // Longitude (BT/BB)
                if (longitude < 0) {
                    lonDirection = "BB";
                    lonFormatted = df.format(Math.abs(longitude));
                } else {
                    lonDirection = "BT";
                    lonFormatted = df.format(longitude);
                }

                String displayCordinate = latFormatted + " " + latDirection + " - " + lonFormatted + " " + lonDirection;
                cor.setText(displayCordinate);

                String html = "<html>" +
                "<head>" +
                "  <meta charset='utf-8'>" +
                "  <title>Peta</title>" +
                "  <style>" +
                "    #map { height: 100vh; width: 100vw; }" +
                "    html, body { margin: 0; padding: 0; }" +
                "  </style>" +
                "  <link rel='stylesheet' href='https://unpkg.com/leaflet@1.9.3/dist/leaflet.css'/>" +
                "  <script src='https://unpkg.com/leaflet@1.9.3/dist/leaflet.js'></script>" +
                "</head>" +
                "<body>" +
                "  <div id='map'></div>" +
                "  <script>" +
                "    var map = L.map('map').setView([" + latitude + ", " + longitude + "], 6);" +
                "    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 18 }).addTo(map);" +
                "    L.marker([" + latitude + "," + longitude + "]).addTo(map).bindPopup('Lokasi Gempa').openPopup();" +
                "  </script>" +
                "</body>" +
                "</html>";
                mapsBmkg.getEngine().loadContent(html);
                
                addCatatanLocal.setOnAction(e -> {
                    btnAddCatatan(gempa);
                });
            } catch (NumberFormatException e) {
                System.err.println("Gagal mengkonversi koordinat ke double. " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Terjadi kesalahan saat memuat peta: " + e.getMessage());
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "Data gagal diload").showAndWait();
        }
    }
    
    public void btnAddCatatan(DetailGempa gempa) {                
        if (!DataShare.catatan.contains(gempa)) {
            DataShare.catatan.add(gempa);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Menambahkan Data");
            alert.setHeaderText(null);
            alert.setContentText("Data Berhasil Ditambahkan");

            Duration delay = Duration.seconds(2);
            Timeline timeline = new Timeline(
                new KeyFrame(delay, event -> {
                    alert.hide();
                })
            );
            timeline.play();
            alert.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Peringatan!!");
            alert.setHeaderText(null);
            alert.setContentText("Data Sudah Ditambahkan");
            alert.show();
        }
    }
    
    public void btnViewDetailMap() {
        DetailGempa gempa = getData();
        String baseUrlImageBmkg = "https://data.bmkg.go.id/DataMKG/TEWS/";
        
        Stage modalStage = new Stage();
        modalStage.setTitle("Detail Map");
        modalStage.setResizable(true);
        
        String mapFileName = gempa.getMap();
        String fullImageUrl = baseUrlImageBmkg + mapFileName;
        
        ImageView imageView = new ImageView(fullImageUrl);
        imageView.setPreserveRatio(true);
        
        StackPane imageLayout = new StackPane(imageView);
        imageLayout.setAlignment(Pos.CENTER);
        imageView.setFitWidth(550);
        imageView.setFitHeight(550);
        
        Scene modalScene = new Scene(imageLayout);
        modalStage.setScene(modalScene);
        modalStage.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tampilkanDataGempa();
    }    
}
