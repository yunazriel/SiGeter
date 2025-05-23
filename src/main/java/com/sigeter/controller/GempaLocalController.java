package com.sigeter.controller;

import com.sigeter.model.DataShare;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.sigeter.service.GempaBmkg;
import com.sigeter.model.DetailGempa;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class GempaLocalController implements Initializable {
    private GempaBmkg service;
    private static final String url = "https://data.bmkg.go.id/DataMKG/TEWS/";
    
    @FXML private Label date,mag,dlm,wil,pot,cor,map;
    @FXML private ImageView bmkgImg;
    
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
        if (gempa != null) {
            date.setText(gempa.getTanggal() + ", " + gempa.getJam());
            mag.setText(gempa.getMagnitude());
            dlm.setText(gempa.getKedalaman());
            wil.setText(gempa.getWilayah());
            pot.setText(gempa.getPotensi());
            cor.setText(gempa.getCordinate());
            
            try {
                Image image = new Image(url + gempa.getMap());
                bmkgImg.setImage(image);
            } catch (Exception e) {
                System.out.println("Gagal memuat gambar : " + e.getMessage());
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "Data gagal diload").showAndWait();
        }
    }
    
    public void btnAddCatatan() {
        DetailGempa gempa = getData();
                
        if (!DataShare.catatan.contains(gempa)) {
            DataShare.catatan.add(gempa);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Menambahkan Data");
            alert.setHeaderText(null);
            alert.setContentText("Data Berhasil Ditambahkan");
            // Membuat Timeline untuk menutup alert otomatis setelah 2 detik
            Duration delay = Duration.seconds(2);
            Timeline timeline = new Timeline(
                new KeyFrame(delay, event -> {
                    alert.hide();
                })
            );
            timeline.play();
            alert.show();
        } else {
            System.out.println("Data sudah ada di catatan.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        System.out.println("[API BMKG]: " + getData());
        tampilkanDataGempa();
    }    
}
