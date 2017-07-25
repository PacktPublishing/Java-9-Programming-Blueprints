package com.steeplesoft.deskdroid.desktop;

import com.steeplesoft.deskdroid.model.Conversation;
import com.steeplesoft.deskdroid.model.Participant;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author jason
 */
public class ConversationCell extends ListCell<Conversation> {

    public ConversationCell(ListView list) {
        super();
        prefWidthProperty().bind(list.widthProperty().subtract(2));
        setMaxWidth(Control.USE_PREF_SIZE);
    }

    @Override
    protected void updateItem(Conversation conversation, boolean empty) {
        super.updateItem(conversation, empty);
        if (conversation != null) {
            setWrapText(true);
            final Participant participant = ConversationService.getInstance()
                    .getParticipant(conversation.getParticipant());

            HBox hbox = createWrapper(participant);
            hbox.getChildren().add(createConversationSnippet(participant, conversation.getSnippet()));

            setGraphic(hbox);
        } else {
            setGraphic(null);
        }
    }

    protected VBox createConversationSnippet(final Participant participant, String snippet) {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(0, 0, 0, 5));
        Label sender = new Label(participant.getName());
        sender.setWrapText(true);
        Label phoneNumber = new Label(participant.getPhoneNumber());
        phoneNumber.setWrapText(true);
        Label label = new Label(snippet);
        label.setWrapText(true);
        vbox.getChildren().addAll(sender, phoneNumber, label);
        return vbox;
    }

    protected HBox createWrapper(final Participant participant) {
        HBox hbox = new HBox();
        hbox.setManaged(true);
        ImageView thumbNail = new ImageView();
        thumbNail.prefWidth(65);
        thumbNail.setPreserveRatio(true);
        thumbNail.setFitHeight(65);
        thumbNail.setImage(new Image(ConversationService.getInstance()
                .getParticipantThumbnail(participant.getPhoneNumber())));
        hbox.getChildren().add(thumbNail);
        return hbox;
    }
}
