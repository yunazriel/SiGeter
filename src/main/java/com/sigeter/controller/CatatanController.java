package com.sigeter.controller;

import com.sigeter.model.DataShare;
import com.sigeter.model.DetailGempa;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CatatanController implements Initializable {
    
    @FXML private TableView<DetailGempa> tableCatatan;
    @FXML private TableColumn<DetailGempa, String> colTgl;
    @FXML private TableColumn<DetailGempa, String> colJam;
    @FXML private TableColumn<DetailGempa, String> colMag;
    @FXML private TableColumn<DetailGempa, String> colDlm;
    @FXML private TableColumn<DetailGempa, String> colWil;    
    @FXML private TableColumn<DetailGempa, String> colPot;    
    @FXML private TableColumn<DetailGempa, String> colCor;
    @FXML private TableColumn<DetailGempa, Void> colAction;
    @FXML private ChoiceBox<String> choiceExportFormat;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> exportOptions = FXCollections.observableArrayList("CSV", "Excel");
        choiceExportFormat.setItems(exportOptions);
        choiceExportFormat.getSelectionModel().selectFirst(); // Pilih CSV sebagai default
        
        tableCatatan.setEditable(true);
        tableCatatan.setSelectionModel(null);
        
        // Kolom data
        colTgl.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colJam.setCellValueFactory(new PropertyValueFactory<>("jam"));
        colMag.setCellValueFactory(new PropertyValueFactory<>("magnitude"));
        colDlm.setCellValueFactory(new PropertyValueFactory<>("kedalaman"));
        colWil.setCellValueFactory(new PropertyValueFactory<>("wilayah"));
        colPot.setCellValueFactory(new PropertyValueFactory<>("potensi"));
        colCor.setCellValueFactory(new PropertyValueFactory<>("cordinate"));
        
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("Edit");
            private final Button btnDelete = new Button("Delete");
            private final HBox hbox = new HBox(5, btnEdit, btnDelete);

            {
                hbox.setAlignment(Pos.CENTER);

                btnEdit.getStyleClass().add("btnCatatan-edit");
                btnEdit.setOnAction(e -> {
                    DetailGempa selected = getTableView().getItems().get(getIndex());
                    showEditPopup(selected);
                });

                btnDelete.getStyleClass().add("btnCatatan-delete");
                btnDelete.setOnAction(e -> {
                    DetailGempa selected = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Peringatan");
                    alert.setHeaderText(null);
                    alert.setContentText("Yakin Ingin Menghapus Data?");
                    
                    Optional<ButtonType> result = alert.showAndWait();
                    // Periksa apakah tombol OK ditekan
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        DataShare.catatan.remove(selected);
                    } else {
                        System.out.println("Penghapusan dibatalkan.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });

        // Tampilkan data
        ObservableList<DetailGempa> catatanList = DataShare.catatan;
        tableCatatan.setItems(catatanList);
    }

    private void showEditPopup(DetailGempa gempa) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Edit Catatan");

        // input
        DatePicker tfTgl = new DatePicker();
        try {
            Locale indonesiaLocale = new Locale("id", "ID");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", indonesiaLocale);

            if (gempa.getTanggal() != null && !gempa.getTanggal().isEmpty()) {
                tfTgl.setValue(LocalDate.parse(gempa.getTanggal(), formatter));
            }
        } catch (Exception e) {
            System.err.println("Gagal mem-parse tanggal '" + gempa.getTanggal() + "': " + e.getMessage());
        }
        
        TextField tfJam = new TextField(gempa.getJam());
        TextField tfMag = new TextField(gempa.getMagnitude());
        TextField tfDlm = new TextField(gempa.getKedalaman());
        TextField tfWil = new TextField(gempa.getWilayah());
        TextField tfPot = new TextField(gempa.getPotensi());
        TextField tfCor = new TextField(gempa.getCordinate());
        
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10); // horizontal
        formGrid.setVgap(10); // vertikal
        formGrid.setPadding(new Insets(20));

        // label
        formGrid.add(new Label("Tanggal:"), 0, 0);
        formGrid.add(tfTgl, 1, 0);
        formGrid.add(new Label("Jam:"), 0, 1);
        formGrid.add(tfJam, 1, 1);
        formGrid.add(new Label("Magnitude:"), 0, 2);
        formGrid.add(tfMag, 1, 2);
        formGrid.add(new Label("Kedalaman:"), 0, 3);
        formGrid.add(tfDlm, 1, 3);
        formGrid.add(new Label("Wilayah:"), 0, 4);
        formGrid.add(tfWil, 1, 4);
        formGrid.add(new Label("Potensi:"), 0, 5);
        formGrid.add(tfPot, 1, 5);
        formGrid.add(new Label("Koordinat:"), 0, 6);
        formGrid.add(tfCor, 1, 6);

        Button btnSave = new Button("Simpan");
        btnSave.getStyleClass().add("button-primary");
        btnSave.setOnAction(e -> {
            LocalDate selectedDate = tfTgl.getValue();
            if (selectedDate != null) {
                Locale indonesiaLocale = new Locale("id", "ID");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", indonesiaLocale);
                gempa.setTanggal(selectedDate.format(formatter));
            } else {
                gempa.setTanggal(null);
            }
            gempa.setJam(tfJam.getText());
            gempa.setMagnitude(tfMag.getText());
            gempa.setKedalaman(tfDlm.getText());
            gempa.setWilayah(tfWil.getText());
            gempa.setPotensi(tfPot.getText());
            gempa.setCordinate(tfCor.getText());

            // memastikan tableCatatan tidak null
            if (tableCatatan != null) {
                tableCatatan.refresh();
            }
            dialog.close();
        });

        Button btnBatal = new Button("Batal");
        btnBatal.getStyleClass().add("button-secondary");
        btnBatal.setOnAction(e -> dialog.close());

        HBox buttonBar = new HBox(8, btnSave, btnBatal);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setPadding(new Insets(0, 20, 20, 20));

        VBox mainLayout = new VBox(formGrid, buttonBar);
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setSpacing(20);

        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add(getClass().getResource("/com/sigeter/style/modal-edit.css").toExternalForm());
        dialog.setScene(scene);
        dialog.sizeToScene();
        dialog.showAndWait();
    }
    
    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    @FXML
    private void handleExport() {
        String selectedFormat = choiceExportFormat.getSelectionModel().getSelectedItem();

        if (selectedFormat == null) {
            showAlert(AlertType.WARNING, "Peringatan", "Pilih Format Ekspor", "Harap pilih format (CSV atau Excel) sebelum mengekspor.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Data Catatan");

        // Set filter berdasarkan format yang dipilih
        if (selectedFormat.equals("CSV")) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));
            fileChooser.setInitialFileName("catatan_data_gempa.csv");
        } else if (selectedFormat.equals("Excel")) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx"));
            fileChooser.setInitialFileName("catatan_data_gempa.xlsx");
        }

        File file = fileChooser.showSaveDialog(null); // Gunakan null jika tidak ada stage yang terhubung
        if (file != null) {
            try {
                if (selectedFormat.equals("CSV")) {
                    exportToCsv(file);
                } else if (selectedFormat.equals("Excel")) {
                    exportToExcel(file);
                }
                showAlert(AlertType.INFORMATION, "Sukses", "Ekspor Berhasil", "Data berhasil diekspor ke:\n" + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Error", "Gagal Ekspor", "Terjadi kesalahan saat mengekspor data: " + e.getMessage());
            }
        }
    }
    
    private void exportToCsv(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            // Tulis header kolom
            writer.append("Tanggal,Jam,Magnitude,Kedalaman,Wilayah,Potensi,Koordinat\n");

            // Tulis data baris
            for (DetailGempa data : tableCatatan.getItems()) {
                writer.append(
                    data.getTanggal() + "," +
                    data.getJam()+ "," +
                    data.getMagnitude() + "," +
                    data.getKedalaman() + "," +
                    "\"" + data.getWilayah().replace("\"", "\"\"") + "\"," +
                    "\"" + data.getPotensi().replace("\"", "\"\"") + "\"," +
                    "\"" + data.getCordinate().replace("\"", "\"\"") + "\"" +
                    "\n"
                );
            }
        }
    }

    private void exportToExcel(File file) throws IOException {
        // Implementasi ekspor ke Excel membutuhkan library Apache POI
        // Pastikan Anda sudah menambahkan Apache POI ke project Anda (misalnya melalui Maven/Gradle)
        // Jika belum, uncomment bagian import di atas dan tambahkan dependency:
        /*
        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi</artifactId>
            <version>5.2.3</version>
        </dependency>
        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi-ooxml</artifactId>
            <version>5.2.3</version>
        </dependency>
        */

        // Contoh implementasi dasar untuk Excel (membutuhkan Apache POI)
        /*
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Catatan Data");

            // Buat header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Tanggal");
            headerRow.createCell(1).setCellValue("Jam");
            headerRow.createCell(2).setCellValue("Magnitude");
            headerRow.createCell(3).setCellValue("Kedalaman");
            headerRow.createCell(4).setCellValue("Wilayah");
            headerRow.createCell(5).setCellValue("Potensi");
            headerRow.createCell(6).setCellValue("Koordinat");

            // Isi data
            int rowNum = 1;
            for (CatatanData data : tableCatatan.getItems()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(data.getTgl());
                row.createCell(1).setCellValue(data.getJam());
                row.createCell(2).setCellValue(data.getMag());
                row.createCell(3).setCellValue(data.getDlm());
                row.createCell(4).setCellValue(data.getWil());
                row.createCell(5).setCellValue(data.getPot());
                row.createCell(6).setCellValue(data.getCor());
            }

            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
            }
        }
        */
        showAlert(AlertType.INFORMATION, "Informasi", "Fitur Belum Lengkap", "Ekspor ke Excel memerlukan library Apache POI dan implementasi tambahan.");
    }
}
