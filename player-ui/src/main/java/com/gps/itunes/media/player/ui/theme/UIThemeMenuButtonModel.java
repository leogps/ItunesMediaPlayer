package com.gps.itunes.media.player.ui.theme;

import javax.swing.*;

/**
 * @author leogps
 * Created on 5/29/21
 */
public class UIThemeMenuButtonModel extends DefaultButtonModel {

    private final UITheme uiTheme;

    public UIThemeMenuButtonModel(UITheme uiTheme) {
        this.uiTheme = uiTheme;
    }

    public UITheme getUiTheme() {
        return uiTheme;
    }
}
