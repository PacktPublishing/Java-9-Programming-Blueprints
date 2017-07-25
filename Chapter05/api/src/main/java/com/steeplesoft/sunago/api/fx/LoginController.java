package com.steeplesoft.sunago.api.fx;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author jason
 */
public class LoginController implements Initializable {

    @FXML
    private WebView webView;
    private Predicate<WebEngine> loginSuccessTest;
    private Consumer<WebEngine> handler;

    public static void showAndWait(String url, 
            Predicate<WebEngine> loginSuccessTest,
            Consumer<WebEngine> handler) {
        try {
            FXMLLoader loader = new FXMLLoader(LoginController.class.getResource("/fxml/login.fxml"));

            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            LoginController controller = loader.<LoginController>getController();
            controller.setUrl(url);
            controller.setLoginSuccessTest(loginSuccessTest);
            controller.setHandler(handler);

            stage.setTitle("Login...");
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final WebEngine webEngine = webView.getEngine();

        webEngine.getLoadWorker().stateProperty()
                .addListener((ov, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        if (loginSuccessTest.test(webEngine)) {
                            handler.accept(webEngine);
                            ((Stage) webView.getScene().getWindow()).close();
                        }
                    }
                });
    }

    public void setUrl(String url) {
        webView.getEngine().load(url);
    }

    private void setLoginSuccessTest(Predicate<WebEngine> loginSuccessTest) {
        this.loginSuccessTest = loginSuccessTest;
    }

    private void setHandler(Consumer<WebEngine> handler) {
        this.handler = handler;
    }
}
