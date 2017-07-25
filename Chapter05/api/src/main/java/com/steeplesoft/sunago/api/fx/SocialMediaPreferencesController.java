package com.steeplesoft.sunago.api.fx;

import javafx.scene.control.Tab;

/**
 *
 * @author jason
 */
public abstract class SocialMediaPreferencesController {
    public abstract Tab getTab();
    public abstract void savePreferences();
}