package com.steeplesoft.sunago.app;

import com.steeplesoft.sunago.SunagoPrefsKeys;
import com.steeplesoft.sunago.SunagoUtil;
import com.steeplesoft.sunago.api.fx.SocialMediaPreferencesController;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author jason
 */
public class PreferencesController implements Initializable {

    @FXML
    protected Button savePrefs;
    @FXML
    protected Button cancel;
    @FXML
    protected TabPane tabPane;
    @FXML
    protected TextField itemCount;

    private final List<SocialMediaPreferencesController> smPrefs = new ArrayList<>();

    public static void showAndWait() {
        try {
            FXMLLoader loader = new FXMLLoader(PreferencesController.class.getResource("/fxml/prefs.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Sunago Preferences");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        itemCount.setText(SunagoUtil.getSunagoPreferences().getPreference(SunagoPrefsKeys.ITEM_COUNT.getKey(), "50"));
        final ServiceLoader<SocialMediaPreferencesController> smPrefsLoader
                = ServiceLoader.load(SocialMediaPreferencesController.class);
        smPrefsLoader.forEach(smp -> smPrefs.add(smp));

        smPrefs.forEach(smp -> tabPane.getTabs().add(smp.getTab()));
    }

    @FXML
    public void savePreferences(ActionEvent event) {
        SunagoUtil.getSunagoPreferences().putPreference(SunagoPrefsKeys.ITEM_COUNT.getKey(),
                itemCount.getText());
        smPrefs.forEach(smp -> smp.savePreferences());
        closeDialog(event);
    }

    @FXML
    public void closeDialog(ActionEvent event) {
        ((Stage) savePrefs.getScene().getWindow()).close();
    }

    // endregion Twitter
    // region Facebook
    /*
    private void configureFacebook() {

        facebook = FacebookClient.instance(SunagoProperties.getInstance());
        if (facebook.isAuthenticated()) {
            //
        } else {
            showConnectToFacebook();
        }
    }

    @FXML
    public void connectToFacebook(ActionEvent event) {
        LoginController.showAndWait(facebook.getAuthorizationUrl(),
                e -> e.getLocation().contains("access_token"),
                e -> saveFacebookToken(e.getLocation()));
    }

    private void saveFacebookToken(final String url) {
        int hash = url.indexOf("#");
        if (hash > -1) {
            final SunagoProperties prefs = SunagoProperties.getInstance();
            String token = null;
            String expires = null;
            for (String part : url.substring(hash + 1).split("&")) {
                if (part.startsWith("access_token=")) {
                    token = part.substring(13);
                } else if (part.startsWith("expires_in=")) {
                    expires = part.substring(11);
                }
            }
            prefs.putPreference(FacebookPrefsKeys.TOKEN.getKey(), token);
            prefs.putPreference(FacebookPrefsKeys.TOKEN_EXPIRES.getKey(), expires);
            facebook.authenticateUser(token, Long.parseLong(expires));
            facebook.getPosts();
        }
    }

    private void showConnectToFacebook() {
        showOnlyPane(facebookContent, connectToFacebookPane);
    }
     */
    // endregion Facebook
}
