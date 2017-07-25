package com.steeplesoft.sunago.twitter.fx;

import com.steeplesoft.sunago.api.fx.LoginController;
import com.steeplesoft.sunago.api.fx.SelectableItem;
import com.steeplesoft.sunago.api.SunagoPreferences;
import com.steeplesoft.sunago.api.fx.SocialMediaPreferencesController;
import com.steeplesoft.sunago.SunagoUtil;
import com.steeplesoft.sunago.twitter.MessageBundle;
import com.steeplesoft.sunago.twitter.TwitterClient;
import com.steeplesoft.sunago.twitter.TwitterPrefsKeys;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import twitter4j.TwitterException;
import twitter4j.UserList;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 *
 * @author jason
 */
public class TwitterPreferencesController extends SocialMediaPreferencesController {

    private Tab tab;
    private final TwitterClient twitter;
    private boolean showHomeTimeline = false;
    private final ObservableList<SelectableItem<UserList>> itemList = FXCollections.observableArrayList();
    private final SunagoPreferences prefs = SunagoUtil.getSunagoPreferences();
    
    public TwitterPreferencesController() {
        twitter = new TwitterClient();
    }

    @Override
    public Tab getTab() {
        if (tab == null) {
            tab = new Tab("Twitter");
            tab.setContent(getNode());
        }

        return tab;
    }

    @Override
    public void savePreferences() {
        prefs.putPreference(TwitterPrefsKeys.HOME_TIMELINE.getKey(), Boolean.toString(showHomeTimeline));
        List<String> selectedLists = itemList.stream()
                .filter(s -> s != null)
                .filter(s -> s.getSelected().get())
                .map(s -> Long.toString(s.getItem().getId()))
                .collect(Collectors.toList());
        prefs.putPreference(TwitterPrefsKeys.SELECTED_LISTS.getKey(), String.join(",", selectedLists));
    }

    private Node getNode() {
        return twitter.isAuthenticated() ? buildConfigurationUI() : buildConnectUI();
    }

    private Node buildConfigurationUI() {
        VBox box = new VBox();
        box.setPadding(new Insets(10));

        CheckBox cb = new CheckBox(MessageBundle.getInstance().getString("homeTimelineCB"));
        cb.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean oldVal, Boolean newVal) -> {
            showHomeTimeline = newVal;
        });

        Label label = new Label(MessageBundle.getInstance().getString("userListLabel") + ":");

        ListView<SelectableItem<UserList>> lv = new ListView<>();
        lv.setItems(itemList);
        lv.setCellFactory(CheckBoxListCell.forListView(item -> item.getSelected()));
        VBox.setVgrow(lv, Priority.ALWAYS);

        box.getChildren().addAll(cb, label, lv);
        showTwitterListSelection();

        return box;
    }

    private Node buildConnectUI() {
        HBox box = new HBox();
        box.setPadding(new Insets(10));
        Button button = new Button(MessageBundle.getInstance().getString("connect"));
        button.setOnAction(event -> connectToTwitter());
        box.getChildren().add(button);
        return box;
    }

    private void connectToTwitter() {
        try {
            RequestToken requestToken = twitter.getOAuthRequestToken();
            LoginController.showAndWait(requestToken.getAuthorizationURL(),
                    e -> ((String) e.executeScript("document.documentElement.outerHTML"))
                            .contains("You've granted access to"),
                    e -> {
                        // TODO: xslt?
                        final String html = "<kbd aria-labelledby=\"code-desc\"><code>";

                        String body = (String) e.executeScript("document.documentElement.outerHTML");
                        final int start = body.indexOf(html) + html.length();
                        String code = body.substring(start, start + 7);
                        saveTwitterAuthentication(requestToken, code);
                        showConfigurationUI();
                    });
        } catch (TwitterException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void saveTwitterAuthentication(RequestToken requestToken, String code) {
        if (!code.isEmpty()) {
            try {
                AccessToken accessToken = twitter.getAcccessToken(requestToken, code);
                prefs.putPreference(TwitterPrefsKeys.TOKEN.getKey(), accessToken.getToken());
                prefs.putPreference(TwitterPrefsKeys.TOKEN_SECRET.getKey(), accessToken.getTokenSecret());
            } catch (TwitterException ex) {
                Logger.getLogger(TwitterPreferencesController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void showTwitterListSelection() {
        List<SelectableItem<UserList>> selectable = twitter.getLists().stream()
                .map(u -> new SelectableUserList(u))
                .collect(Collectors.toList());
        List<Long> selectedListIds = twitter.getSelectedLists(prefs);
        selectable.forEach(s -> s.getSelected().set(selectedListIds.contains(s.getItem().getId())));
        itemList.clear();
        itemList.addAll(selectable);
    }

    private void showConfigurationUI() {
        getTab().setContent(buildConfigurationUI());
    }

}
