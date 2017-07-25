package com.steeplesoft.sunago.app;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author jason
 */
public class Sunago extends Application {

    public Sunago() throws Exception {
        super();
        updateClassLoader();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/sunago.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/styles.css");

        stage.setTitle("Sunago");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void updateClassLoader() {
        final File[] jars = getFiles();
        if (jars != null) {
            URL[] urls = new URL[jars.length];
            int index = 0;
            for (File jar : jars) {
                try {
                    urls[index++] = jar.toURI().toURL();
                } catch (MalformedURLException ex) {
                    Logger.getLogger(Sunago.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            Thread.currentThread().setContextClassLoader(URLClassLoader.newInstance(urls));
        }
    }

    private File[] getFiles() {
        String pluginDir = System.getProperty("user.home") + "/.sunago";
        return new File(pluginDir).listFiles(file -> file.isFile() && file.getName().toLowerCase().endsWith(".jar"));
    }
}
