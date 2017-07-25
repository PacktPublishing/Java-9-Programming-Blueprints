package com.steeplesoft.deskdroid.desktop;

import com.steeplesoft.deskdroid.model.Conversation;
import com.steeplesoft.deskdroid.model.Message;
//import java.awt.Taskbar;
import java.awt.Toolkit;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import jersey.repackaged.com.google.common.base.Objects;

public class DeskDroidController implements Initializable {

    @FXML
    private ListView<Conversation> convList;
    @FXML
    private ListView<Message> messageList;
    @FXML
    private Button newMessageBtn;
    @FXML
    private VBox convContainer;
    @FXML
    private ToolBar toolbar;

    private final ConversationService cs = ConversationService.getInstance();
    private final DeskDroidPreferences preferences = DeskDroidPreferences.getInstance();
    private final ObservableList<Conversation> conversations = FXCollections.observableArrayList();
    private final SimpleObjectProperty<Conversation> conversation = new SimpleObjectProperty<>();
    private final ObservableList<Message> messages = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        convList.setCellFactory(list -> new ConversationCell(convList));
        convList.setItems(conversations);
        convList.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    conversation.set(newValue);
                    if (newValue != null) {
                        messages.setAll(newValue.getMessages());
                        messageList.scrollTo(messages.size() - 1);
                    } else {
                        messages.clear();
                    }
                });

        messageList.setCellFactory(list -> new MessageCell(messageList));
        messageList.setItems(messages);

        newMessageBtn.setOnAction(event -> sendNewMessage());

        if (!preferences.getPhoneAddress().isEmpty()) {
            refreshAndListen();
        } else {
            connectToPhone(null);
        }
    }

    protected void refreshAndListen() {
        conversations.addAll(cs.getConversations());
        cs.stopListeningForNewMessages();
        cs.subscribeToNewMessageEvents(this::handleMessageReceived);
    }

    @FXML
    protected void closeApplication(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    protected void connectToPhone(ActionEvent event) {
        ConnectToPhoneController.showAndWait();
        if (!preferences.getToken().isEmpty()) {
            refreshAndListen();
        }
    }

    @FXML
    protected void disconnectFromPhone(ActionEvent event) {
        cs.stopListeningForNewMessages();
        preferences.setPhoneAddress("");
        preferences.setToken("");
        conversations.clear();
        messages.clear();
        conversation.set(null);
    }

    @FXML
    public void refreshConversations(ActionEvent event) {
        conversations.setAll(cs.getConversations());
    }

    protected void handleMessageReceived(final Message message) {
        Platform.runLater(() -> {
            Optional<Conversation> optional = conversations.stream()
                    .filter(c -> Objects.equal(c.getParticipant(), message.getAddress()))
                    .findFirst();
            if (optional.isPresent()) {
                Conversation c = optional.get();
                c.getMessages().add(message);
                c.setSnippet(message.getBody());
                convList.refresh();
                if (c == conversation.get()) {
                    messages.setAll(c.getMessages());
                    messageList.scrollTo(messages.size() - 1);
                }
            } else {
                Conversation newConv = new Conversation();
                newConv.setParticipant(message.getAddress());
                newConv.setSnippet(message.getBody());
                newConv.setMessages(Arrays.asList(message));
                conversations.add(0, newConv);
            }
//            final Taskbar taskbar = Taskbar.getTaskbar();
//            if (taskbar.isSupported(Taskbar.Feature.USER_ATTENTION)) {
//                taskbar.requestUserAttention(true, false);
//            }
            Toolkit.getDefaultToolkit().beep();
        });
    }

    protected void conversationClicked(MouseEvent event) {
        final Conversation selected = convList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            conversation.set(selected);
        }
    }

    private void sendNewMessage() {
        Optional<String> result = SendMessageDialogController.showAndWait(conversation.get());
        if (result.isPresent()) {
            Conversation conv = conversation.get();
            Message message = new Message();
            message.setThreadId(conv.getThreadId());
            message.setAddress(conv.getParticipant());
            message.setBody(result.get());
            message.setMine(true);
            if (cs.sendMessage(message)) {
                conv.getMessages().add(message);
                messages.add(message);
            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("An error occured while sending the message.");

                alert.showAndWait();
            }
        }
    }
}
