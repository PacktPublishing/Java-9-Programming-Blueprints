package com.steeplesoft.deskdroid.desktop;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/fxml/deskdroid.fxml")));
        System.out.println(scene);
        scene.getStylesheets().add("/styles/deskdroid.css");
        
        stage.setTitle("DeskDroid");
        stage.setScene(scene);
//        ScenicView.show(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
