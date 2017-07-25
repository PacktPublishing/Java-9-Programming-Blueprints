package com.steeplesoft.deskdroid.desktop;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author jason
 */
public class ConnectToPhoneController implements Initializable {
    @FXML
    protected TextField phoneAddress;
    @FXML
    protected TextField securityCode;
    protected final DeskDroidPreferences preferences = DeskDroidPreferences.getInstance();

    public static void showAndWait() {
        try {
            FXMLLoader loader = new FXMLLoader(ConnectToPhoneController.class.getResource("/fxml/connect.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Connect to Phone");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        phoneAddress.setText(preferences.getPhoneAddress());
    }
    
    @FXML
    public void connectToPhone(ActionEvent event) {
        String address = phoneAddress.getText();
        String code = securityCode.getText();
        preferences.setPhoneAddress(address);
        final ConversationService conversationService = 
                ConversationService.getInstance();

        conversationService.setPhoneAddress(address);
        Optional<String> token = conversationService
                .getAuthorization(code);
        if (token.isPresent()) {
            preferences.setToken(token.get());
            closeDialog(event);
        }
    }
    
    @FXML
    public void closeDialog(ActionEvent event) {
        ((Stage) phoneAddress.getScene().getWindow()).close();
    }
}
