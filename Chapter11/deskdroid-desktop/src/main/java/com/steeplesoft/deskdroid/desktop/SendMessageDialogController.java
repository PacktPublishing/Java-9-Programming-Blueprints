package com.steeplesoft.deskdroid.desktop;

import com.steeplesoft.deskdroid.model.Conversation;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author jason
 */
public class SendMessageDialogController implements Initializable {

    private Conversation conversation;
    @FXML
    private TextArea msgText;
    @FXML
    private Label recipient;
    @FXML
    private Label charCount;
    @FXML
    private Button btnSend;
    @FXML
    private Button btnCancel;
    @FXML
    private Pane container;
    private String message = null;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        msgText.prefWidthProperty().bind(container.widthProperty().subtract(20));
        msgText.setMaxWidth(Control.USE_PREF_SIZE);
        msgText.setWrapText(true);
    }

    @FXML
    protected void sendClicked(ActionEvent event) {
        message = msgText.getText();
        Stage stage = (Stage) btnSend.getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void cancelClicked(ActionEvent event) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void keyTyped(KeyEvent event) {
        charCount.setText(Integer.toString(msgText.getText().length()));
        if (event.getCode() == KeyCode.TAB) {
            btnSend.requestFocus();
            event.consume();
        }
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
        recipient.setText(conversation.getParticipant());
    }

    public Optional<String> getMessage() {
        return Optional.ofNullable(message);
    }

    public static Optional<String> showAndWait(Conversation conversation) {
        try {
            FXMLLoader loader = new FXMLLoader(SendMessageDialogController.class.getResource("/fxml/message_dialog.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Send Text Message");
            stage.initModality(Modality.APPLICATION_MODAL);
            final SendMessageDialogController controller = (SendMessageDialogController) loader.getController();
            controller.setConversation(conversation);
            stage.showAndWait();
            return controller.getMessage();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
