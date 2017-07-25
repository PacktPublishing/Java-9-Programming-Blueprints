package com.steeplesoft.cloudnotice.manager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CloudNoticeManager extends Application {
    private FXMLLoader fxmlLoader;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage stage) throws Exception {
        fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/manager.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/styles.css");

        stage.setTitle("CloudNotice");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        System.out.println("stop");
        CloudNoticeManagerController controller = (CloudNoticeManagerController) fxmlLoader.getController();
        controller.cleanup();
        super.stop(); 
    }
    
    
}
