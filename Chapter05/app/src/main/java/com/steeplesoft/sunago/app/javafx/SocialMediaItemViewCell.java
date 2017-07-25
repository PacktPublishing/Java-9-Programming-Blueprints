package com.steeplesoft.sunago.app.javafx;

import com.steeplesoft.sunago.api.SocialMediaItem;
import com.steeplesoft.sunago.SunagoUtil;
import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

/**
 *
 * @author jason
 */
public class SocialMediaItemViewCell extends ListCell<SocialMediaItem> {

    @Override
    public void updateItem(SocialMediaItem item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            setGraphic(buildItemCell(item));
            this.setOnMouseClicked(me -> openUrlInDefaultApplication(item.getUrl()));
        } else {
            setGraphic(null);
        }
    }

    private Node buildItemCell(SocialMediaItem item) {
        HBox hbox = new HBox();
        InputStream resource = item.getClass().getResourceAsStream("icon.png");
        if (resource != null) {
            ImageView sourceImage = new ImageView();
            sourceImage.setFitHeight(18);
            sourceImage.setPreserveRatio(true);
            sourceImage.setSmooth(true);
            sourceImage.setCache(true);
            sourceImage.setImage(new Image(resource));
            hbox.getChildren().add(sourceImage);
        }

        if (item.getImage() != null) {
            HBox picture = new HBox();
            picture.setPadding(new Insets(0, 10, 0, 0));
            ImageView imageView = new ImageView(item.getImage());
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(150);
            picture.getChildren().add(imageView);
            hbox.getChildren().add(picture);
        }

        Label text = new Label(item.getBody());
        text.setFont(Font.font(null, 20));
        text.setWrapText(true);
        hbox.getChildren().add(text);

        return hbox;
    }
    
    private void openUrlInDefaultApplication(String url) {
        try {
            URI uri = URI.create(url);
            Desktop.getDesktop().browse(uri);
        } catch (IOException ex) {
            Logger.getLogger(SunagoUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
