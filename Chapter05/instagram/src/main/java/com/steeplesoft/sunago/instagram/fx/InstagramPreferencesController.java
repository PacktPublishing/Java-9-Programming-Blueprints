/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steeplesoft.sunago.instagram.fx;

import com.steeplesoft.sunago.api.fx.SocialMediaPreferencesController;
import com.steeplesoft.sunago.api.fx.LoginController;
import com.steeplesoft.sunago.instagram.InstagramClient;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jinstagram.auth.model.Token;

/**
 *
 * @author jason
 */
public class InstagramPreferencesController extends SocialMediaPreferencesController {

    private final InstagramClient instagram;
    private Tab tab;
    private static final String CODE_QUERY_PARAM = "code=";

    public InstagramPreferencesController() {
        instagram = new InstagramClient();
    }

    @Override
    public Tab getTab() {
        if (tab == null) {
            tab = new Tab();
            tab.setText("Instagram");
            tab.setContent(getNode());
        }

        return tab;
    }

    @Override
    public void savePreferences() {
        
    }

    private Node getNode() {
        Node node = instagram.isAuthenticated()
                ? buildConfigurationUI() : buildConnectUI();
        return node;
    }

    private Node buildConfigurationUI() {
        VBox box = new VBox();
        box.setPadding(new Insets(10));
        Label label = new Label("Configure Instragram");
        box.getChildren().add(label);
        return box;
    }

    private Node buildConnectUI() {
        HBox box = new HBox();
        box.setPadding(new Insets(10));
        Button connect = new Button("Connect");
        connect.setOnAction(event -> {
            showConnectWindow();
        });
        box.getChildren().addAll(connect);
        return box;
    }

    private void showConnectWindow() {
        LoginController.showAndWait(instagram.getAuthorizationUrl(),
                e -> e.getLocation().contains(CODE_QUERY_PARAM),
                e -> {
                    saveInstagramToken(e.getLocation());
                    showInstagramConfig();
                });
    }
    
    private void showInstagramConfig() {
        tab.setContent(buildConfigurationUI());
    }

    private void saveInstagramToken(String location) {
        int index = location.indexOf(CODE_QUERY_PARAM);
        String code = location.substring(index + CODE_QUERY_PARAM.length());
        Token accessToken = instagram.verifyCodeAndGetAccessToken(code);
        instagram.authenticateUser(accessToken.getToken(), accessToken.getSecret());
    }
}
