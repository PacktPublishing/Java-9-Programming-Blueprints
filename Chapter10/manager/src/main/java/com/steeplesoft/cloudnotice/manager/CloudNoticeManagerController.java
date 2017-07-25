package com.steeplesoft.cloudnotice.manager;

import com.steeplesoft.cloudnotice.api.CloudNoticeDAO;
import com.steeplesoft.cloudnotice.api.SnsClient;
import com.steeplesoft.cloudnotice.api.Recipient;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.WindowEvent;

public class CloudNoticeManagerController implements Initializable {

    @FXML
    private BorderPane pane;
    @FXML
    private ComboBox<String> type;
    @FXML
    private TextField address;
    @FXML
    private TextArea messageText;
    @FXML
    private ListView<Recipient> recipList;
    @FXML
    private ComboBox<String> topicCombo;

    private final ObservableList<String> types = FXCollections.observableArrayList("SMS", "Email");
    private final ObservableList<Recipient> recips = FXCollections.observableArrayList();
    private final ObservableList<String> topics = FXCollections.observableArrayList();

    private final CloudNoticeDAO dao = new CloudNoticeDAO(false);
    private final SnsClient sns = new SnsClient();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        recips.setAll(dao.getRecipients());
        topics.setAll(sns.getTopics());

        type.setItems(types);
        recipList.setItems(recips);
        topicCombo.setItems(topics);

        recipList.setCellFactory(p -> new ListCell<Recipient>() {
            @Override
            public void updateItem(Recipient recip, boolean empty) {
                super.updateItem(recip, empty);
                if (!empty) {
                    setText(String.format("%s - %s", recip.getType(), recip.getAddress()));
                } else {
                    setText(null);
                }
            }
        });
        recipList.getSelectionModel().selectedItemProperty().addListener((obs, oldRecipient, newRecipient) -> {
            type.valueProperty().setValue(newRecipient != null ? newRecipient.getType() : "");
            address.setText(newRecipient != null ? newRecipient.getAddress() : "");
        });
    }

    @FXML
    public void exitApplication(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    public void addRecipient(ActionEvent event) {
        final Recipient recipient = new Recipient();
        recips.add(recipient);
        recipList.getSelectionModel().select(recipient);
        type.requestFocus();
    }

    @FXML
    public void removeRecipient(ActionEvent event) {
        final Recipient recipient = recipList.getSelectionModel().getSelectedItem();
        dao.deleteRecipient(recipient);
        recips.remove(recipient);
    }

    @FXML
    public void saveChanges(ActionEvent event) {
        final Recipient recipient = recipList.getSelectionModel().getSelectedItem();
        recipient.setType(type.getValue());
        recipient.setAddress(address.getText());
        dao.saveRecipient(recipient);
        recipList.refresh();
    }

    @FXML
    public void cancelChanges(ActionEvent event) {
        final Recipient recipient = recipList.getSelectionModel().getSelectedItem();
        type.setValue(recipient.getType());
        address.setText(recipient.getAddress());
    }

    @FXML
    public void sendMessage(ActionEvent event) {
        sns.sendMessage(topicCombo.getSelectionModel().getSelectedItem(),
                messageText.getText());
        messageText.clear();
    }

    @FXML
    public void cancelMessage(ActionEvent event) {
        messageText.clear();
    }

    public void cleanup() {
        dao.shutdown();
        sns.shutdown();
    }
}
