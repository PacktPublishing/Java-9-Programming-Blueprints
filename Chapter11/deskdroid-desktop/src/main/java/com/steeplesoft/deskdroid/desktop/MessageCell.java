package com.steeplesoft.deskdroid.desktop;

import com.steeplesoft.deskdroid.model.Message;
import java.text.SimpleDateFormat;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 *
 * @author jason
 */
public class MessageCell extends ListCell<Message> {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, MM/dd/yyyy hh:mm aa");

    public MessageCell(ListView list) {
        prefWidthProperty().bind(list.widthProperty().subtract(20));
        setMaxWidth(Control.USE_PREF_SIZE);
    }

    @Override
    public void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);
        if (message != null && !empty) {
            if (message.isMine()) {
                wrapMyMessage(message);
            } else {
                wrapTheirMessage(message);
            }
        } else {
            setGraphic(null);
        }
    }

    private void wrapMyMessage(Message message) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.TOP_RIGHT);
        createMessageBox(message, hbox, Pos.TOP_RIGHT);
        setGraphic(hbox);
    }

    private void wrapTheirMessage(Message message) {
        HBox hbox = new HBox();
        ImageView thumbNail = new ImageView();
        thumbNail.prefWidth(65);
        thumbNail.setPreserveRatio(true);
        thumbNail.setFitHeight(65);
        thumbNail.setImage(new Image(ConversationService.getInstance()
                .getParticipantThumbnail(message.getAddress())));

        hbox.getChildren().add(thumbNail);

        createMessageBox(message, hbox, Pos.TOP_LEFT);

        setGraphic(hbox);
    }

    private void createMessageBox(Message message, Pane parent, Pos alignment) {
        VBox vbox = new VBox();
        vbox.setAlignment(alignment);
        vbox.setPadding(new Insets(0, 0, 0, 5));
        Label body = new Label();
        body.setWrapText(true);
        body.setText(message.getBody());

        Label date = new Label();
        date.setText(DATE_FORMAT.format(message.getDate()));

        vbox.getChildren().addAll(body, date);
        parent.getChildren().add(vbox);
    }
}
