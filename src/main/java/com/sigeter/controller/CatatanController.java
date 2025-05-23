package com.sigeter.controller;

import com.sigeter.model.DataShare;
import com.sigeter.model.DetailGempa;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
        dialog.setTitle("Ubah Data Gempa");

        TextField tfTgl = new TextField(gempa.getTanggal());
        TextField tfJam = new TextField(gempa.getJam());
        TextField tfMag = new TextField(gempa.getMagnitude());
        TextField tfDlm = new TextField(gempa.getKedalaman());
        TextField tfWil = new TextField(gempa.getWilayah());
        TextField tfPot = new TextField(gempa.getPotensi());
        TextField tfCor = new TextField(gempa.getCordinate());

        Button btnSimpan = new Button("Simpan");
        btnSimpan.setOnAction(e -> {
            gempa.setTanggal(tfTgl.getText());
            gempa.setJam(tfJam.getText());
            gempa.setMagnitude(tfMag.getText());
            gempa.setKedalaman(tfDlm.getText());
            gempa.setWilayah(tfWil.getText());
            gempa.setPotensi(tfPot.getText());
            gempa.setCordinate(tfCor.getText());

            tableCatatan.refresh(); 
            dialog.close();
        });

        VBox vbox = new VBox(10,
            new Label("Tanggal"), tfTgl,
            new Label("Jam"), tfJam,
            new Label("Magnitude"), tfMag,
            new Label("Kedalaman"), tfDlm,
            new Label("Wilayah"), tfWil,
            new Label("Potensi"), tfPot,
            new Label("Koordinat"), tfCor,
            btnSimpan
        );
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 300, 600);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}
