package com.steeplesoft.sunago.app;

import com.steeplesoft.sunago.app.javafx.SocialMediaItemViewCell;
import com.steeplesoft.sunago.api.SocialMediaItem;
import com.steeplesoft.sunago.api.SocialMediaClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SunagoController implements Initializable {

    private final ObservableList<SocialMediaItem> entriesList = FXCollections.observableArrayList();
    private ServiceLoader<SocialMediaClient> clientLoader;

    @FXML
    private ListView<SocialMediaItem> entriesListView;
    @FXML
    private Button refreshButton;
    @FXML
    private Button settingsButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        refreshButton.setGraphic(getButtonImage("/images/reload.png"));
        refreshButton.setOnAction(ae -> loadItemsFromNetworks());
        refreshButton.setTooltip(new Tooltip("Refresh"));

        settingsButton.setGraphic(getButtonImage("/images/settings.png"));
        settingsButton.setOnAction(ae -> showPreferences(ae));
        settingsButton.setTooltip(new Tooltip("Settings"));

        entriesListView.setCellFactory(listView -> new SocialMediaItemViewCell());
        entriesListView.setItems(entriesList);

        clientLoader = ServiceLoader.load(SocialMediaClient.class);
        loadItemsFromNetworks();
    }


    @FXML
    public void closeApplication(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    public void showAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About...");
        alert.setHeaderText("Sunago (συνάγω)");
        alert.setContentText("(c) Copyright 2016");
        alert.showAndWait();
    }

    @FXML
    public void showPreferences(ActionEvent event) {
        PreferencesController.showAndWait();
    }

    private ImageView getButtonImage(String path) {
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(path)));
        imageView.setFitHeight(32);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    private void loadItemsFromNetworks() {
        List<SocialMediaItem> items = new ArrayList<>();
        clientLoader.forEach(smc -> {
            if (smc.isAuthenticated()) {
                items.addAll(smc.getItems());
            }
        });

        items.sort((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));
        entriesList.addAll(0, items);
    }
}
