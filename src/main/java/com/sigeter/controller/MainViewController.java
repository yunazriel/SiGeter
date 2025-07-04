package com.sigeter.controller;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class MainViewController implements Initializable {
    @FXML private Button btnGempaLocal;
    @FXML private Button btnGempaGlobal;
    @FXML private Button btnCatatan;
    @FXML private Button btnHome;
    @FXML private StackPane mainPane;
    @FXML private List<Button> sidebarButtons;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sidebarButtons = Arrays.asList(btnHome, btnGempaLocal, btnGempaGlobal, btnCatatan);
        
        for (Button btn : sidebarButtons) {
            if (!btn.getStyleClass().contains("sidebar-item")) {
                btn.getStyleClass().add("sidebar-item");
            }
        }
        
        onHomeClicked();
    } 
    
    private void setActiveSidebarButton(Button activeButton) {
        for (Button btn : sidebarButtons) {
            btn.getStyleClass().remove("selected");
        }
        
        if (!activeButton.getStyleClass().contains("selected")) {
            activeButton.getStyleClass().add("selected");
        }
    }
    
    private void loadUI(String fxml) {
        try {
            URL fxmlUrl = getClass().getResource(fxml);
            Parent pane = FXMLLoader.load(fxmlUrl);
            
            mainPane.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    void onHomeClicked() {
        setActiveSidebarButton(btnHome);
        loadUI("/com/sigeter/Home.fxml");
    }
    
    @FXML
    private void onGempaLocalClicked() {
        setActiveSidebarButton(btnGempaLocal);
        loadUI("/com/sigeter/GempaLocal.fxml");
    }
    
    @FXML
    private void onGempaGlobalClicked() {
        setActiveSidebarButton(btnGempaGlobal);        
        loadUI("/com/sigeter/GempaGlobal.fxml");
    }
    
    @FXML
    private void onCatatanClicked() {
        setActiveSidebarButton(btnCatatan);        
        loadUI("/com/sigeter/Catatan.fxml");
    }
}
