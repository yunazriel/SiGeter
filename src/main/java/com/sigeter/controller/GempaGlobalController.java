package com.sigeter.controller;

import com.sigeter.model.*;
import com.sigeter.service.GempaUsgs;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import javafx.util.Duration;

public class GempaGlobalController implements Initializable {
    private final GempaUsgs service;
    
    @FXML private WebView usgsImg;
    @FXML private DatePicker sTime, eTime;
    
    @FXML private TableView<DetailGempa> tableGempa;
    @FXML private TableColumn<DetailGempa, String> colTanggal;
    @FXML private TableColumn<DetailGempa, String> colJam;
    @FXML private TableColumn<DetailGempa, String> colMag;
    @FXML private TableColumn<DetailGempa, String> colDlm;
    @FXML private TableColumn<DetailGempa, String> colWil;
    @FXML private TableColumn<DetailGempa, String> colPot;
    @FXML private TableColumn<DetailGempa, String> colKor;
    @FXML private TableColumn<DetailGempa, Void> colAction;
    
    public GempaGlobalController() {
        this.service = new GempaUsgs();
    }
    
    private String apiUrlByDate(String startTime, String endTime) {
        return "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=" +
               startTime + "T00:00:00&endtime=" + endTime + "T23:59:59&minmagnitude=3.7";
    }

    public List<DetailGempa> getData(String sTime, String eTime) {
        try {
            List<DetailGempa> daftarGempa = service.fetchDataGempa(apiUrlByDate(sTime, eTime));
//            System.out.println("[Data Gempa Global]: " + daftarGempa.size());
            return daftarGempa;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @FXML
    private void handleCariButton() {
        LocalDate startDate = sTime.getValue();
        LocalDate endDate = eTime.getValue();
        LocalDate today = LocalDate.now();

        if (startDate != null && endDate != null) {
            if (endDate.isAfter(today)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Peringatan");
                alert.setHeaderText(null);
                alert.setContentText("Tanggal berakhir tidak boleh melebihi hari ini. Tanggal berakhir akan diatur ke hari ini.");
                alert.showAndWait();

                eTime.setValue(today);
                endDate = today;
            }else if(startDate.isAfter(today)){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Peringatan");
                alert.setHeaderText(null);
                alert.setContentText("Tanggal mulai tidak boleh melebihi hari ini. Tanggal mulai akan diatur ke hari ini.");
                alert.showAndWait();
                
                sTime.setValue(today);
                startDate = today;
            }else if (endDate.isBefore(startDate)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Peringatan");
                alert.setHeaderText(null);
                alert.setContentText("Tanggal berakhir tidak boleh kurang dari tanggal mulai. Tanggal berakhir akan diatur sama dengan tanggal mulai.");
                alert.showAndWait();
                
                eTime.setValue(startDate);
                endDate = startDate;
            }
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String startTime = startDate.format(dateFormatter);
            String endTime = endDate.format(dateFormatter);

            tampilkanDataGempa(startTime, endTime);
        } else {
            System.out.println("Harap pilih tanggal mulai dan tanggal berakhir.");
        }
    }
    
    private void tampilkanPeta(DetailGempa gempa) {
        try {
            String kordinat = gempa.getCordinate();
            String[] parts = kordinat.split(",\\s*");
            double latitude = Double.parseDouble(parts[0]);
            double longitude = Double.parseDouble(parts[1]);
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
            "    var map = L.map('map').setView([" + longitude +  ", " + latitude +  "], 4);" +
            "    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 18 }).addTo(map);" +
            "    L.marker([" + longitude +  "," + latitude +  "]).addTo(map).bindPopup('Lokasi Gempa').openPopup();" +
            "  </script>" +
            "</body>" +
            "</html>";
            usgsImg.getEngine().loadContent(html);
        } catch (Exception e) {
            System.out.println("Gagal memuat gambar : " + e.getMessage());
        }
    }
    
    private void tampilkanDataGempa(String sTime, String eTime) {
        List<DetailGempa> gempaList = getData(sTime, eTime);

        if (gempaList != null && !gempaList.isEmpty()) {
            tampilkanSemuaPeta(gempaList);

            tableGempa.getItems().clear();
            tableGempa.getItems().addAll(gempaList);
            tableGempa.setSelectionModel(null);


            colAction.setCellFactory(col -> new TableCell<>() {
                private final Button btnViewMap = new Button("View Map");
                private final Button btnAddCatatan = new Button("Add Catatan");
                private final HBox hbox = new HBox(5, btnViewMap, btnAddCatatan);

                {
                    hbox.setAlignment(Pos.CENTER);
                    
                    btnAddCatatan.getStyleClass().add("btnCatatan-add");
                    btnAddCatatan.setOnAction(e -> {
                        DetailGempa gempa = getTableView().getItems().get(getIndex());
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
                    });
                    
                    btnViewMap.getStyleClass().add("btnViewMaps");
                    btnViewMap.setOnAction(e -> {
                        DetailGempa gempa = getTableView().getItems().get(getIndex());
                        tampilkanPeta(gempa);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : hbox);
                }
            });
            
//            final boolean[] isSingleView = {true};
//            final DetailGempa[] lastSelected = {null};
//
//            tableGempa.setOnMouseClicked(event -> {
//                if (event.getClickCount() == 1) {
//                    DetailGempa selected = tableGempa.getSelectionModel().getSelectedItem();
//                    if (selected != null) {
//                        if (selected != lastSelected[0]) {
//                            isSingleView[0] = true;
//                        }
//
//                        if (isSingleView[0]) {
//                            tampilkanPeta(selected);
//                        } else {
//                            tampilkanSemuaPeta(tableGempa.getItems());
//                        }
//
//                        isSingleView[0] = !isSingleView[0];
//                        lastSelected[0] = selected;                    }
//                }
//            });

        } else {
            tableGempa.getItems().clear();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Informasi");
            alert.setHeaderText(null);
            alert.setContentText("Data gempa tidak tersedia.");
            alert.showAndWait();
        }
    }
    
    private void tampilkanSemuaPeta(List<DetailGempa> gempaList) {
        try {
            StringBuilder markers = new StringBuilder();
            StringBuilder latlngList = new StringBuilder("var markerList = [");

            for (DetailGempa gempa : gempaList) {
                String kordinat = gempa.getCordinate();
                String[] parts = kordinat.split(",\\s*");
                double lat = Double.parseDouble(parts[1]);
                double lon = Double.parseDouble(parts[0]);

                String popup = "Magnitude: " + gempa.getMagnitude() +
                               "<br>Kedalaman: " + gempa.getKedalaman() +
                               "<br>Wilayah: " + gempa.getWilayah() +
                               "<br>Tanggal: " + gempa.getTanggal() +
                               "<br>Waktu: " + gempa.getJam() +
                               "<br>Potensi: " + gempa.getPotensi();

                // marker
                markers.append("L.marker([").append(lat).append(", ").append(lon)
                       .append("]).addTo(map).bindPopup('").append(popup.replace("'", "\\'")).append("');");
                latlngList.append("[").append(lat).append(", ").append(lon).append("],");
            }

            if (!gempaList.isEmpty()) {
                latlngList.setLength(latlngList.length() - 1);
            }
            latlngList.append("];");

            String html = "<html>" +
            "<head>" +
            "  <meta charset='utf-8'>" +
            "  <title>Peta</title>" +
            "  <style>#map { height: 100vh; width: 100vw; } html, body { margin: 0; padding: 0; }</style>" +
            "  <link rel='stylesheet' href='https://unpkg.com/leaflet@1.9.3/dist/leaflet.css'/>" +
            "  <script src='https://unpkg.com/leaflet@1.9.3/dist/leaflet.js'></script>" +
            "</head>" +
            "<body>" +
            "  <div id='map'></div>" +
            "  <script>" +
            "    var map = L.map('map');" +
            "    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 18 }).addTo(map);" +
                 latlngList.toString() +
            "    markerList.forEach(function(coord) {" +
            "        L.marker(coord).addTo(map);" +
            "    });" +
            "    var bounds = L.latLngBounds(markerList);" +
            "    map.fitBounds(bounds);" +
                 markers.toString() +
            "  </script>" +
            "</body>" +
            "</html>";

            usgsImg.getEngine().loadContent(html);

        } catch (Exception e) {
            System.out.println("Gagal memuat peta: " + e.getMessage());
        }

    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        LocalDate today = LocalDate.now();
        sTime.setValue(today);
        eTime.setValue(today);
        String startTime = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String endTime = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colJam.setCellValueFactory(new PropertyValueFactory<>("jam"));
        colMag.setCellValueFactory(new PropertyValueFactory<>("magnitude"));
        colDlm.setCellValueFactory(new PropertyValueFactory<>("kedalaman"));
        colWil.setCellValueFactory(new PropertyValueFactory<>("wilayah"));
        colPot.setCellValueFactory(new PropertyValueFactory<>("potensi"));
        colKor.setCellValueFactory(new PropertyValueFactory<>("cordinate"));

        // Cegah drag and drop urutan kolom
        tableGempa.getColumns().forEach(col -> col.setReorderable(false));

        tampilkanDataGempa(startTime, endTime);
    }
}
