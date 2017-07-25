package com.steeplesoft.photobeans.main;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.openide.util.Utilities;

/**
 *
 * @author jason
 */
public class PhotoViewerController implements Initializable {

    @FXML
    private BorderPane borderPane;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private ImageView imageView;
    private Pane pane;
    private String photo;
    private Image image;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        Group grp = new Group();
//        imageView = new ImageView();
//        imageView.setPreserveRatio(true);
//        scrollPane.setContent(grp);
//        grp.getChildren().add(imageView);

        imageView.fitWidthProperty().bind(borderPane.widthProperty());
        imageView.fitHeightProperty().bind(borderPane.heightProperty());
        imageView.setPreserveRatio(true);
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
        image = new Image(Utilities.toURI(new File(photo)).toString());
        imageView.setImage(image);
    }

    @FXML
    public void rotateLeft(ActionEvent event) {
        imageView.setRotate(imageView.getRotate() - 90);
    }

    @FXML
    public void rotateRight(ActionEvent event) {
        imageView.setRotate(imageView.getRotate() + 90);
    }
}
