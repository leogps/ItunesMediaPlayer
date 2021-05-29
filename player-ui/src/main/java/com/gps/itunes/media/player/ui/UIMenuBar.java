package com.gps.itunes.media.player.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import static com.gps.itunes.media.player.ui.UIFrame.RESOURCE_BUNDLE;

/**
 * Created by leogps on 11/27/14.
 */
public class UIMenuBar extends JMenuBar {

    // File Menu
    private final JMenu fileMenu;
    private final JMenuItem openMenuItem;
    private final JMenuItem openNetworkFileMenuItem;
    private final JMenuItem exitMenuItem;

    private final JMenu actionsMenu;

    private final JMenuItem increaseVolumeMenuItem;
    private final JMenuItem decreaseVolumeMenuItem;

    private final JMenuItem goToMenuItem;
    private final JMenuItem reloadLibraryMenuItem;
    private final JMenuItem copyPlaylistsMenuItem;
    private final JMenuItem copyTracksMenuItem;

    private final JMenu toolsMenu;

    private final JMenuItem themesMenuItem;

    private final List<JRadioButtonMenuItem> themesSubMenuList;

    private final JMenuItem updatesMenuItem;

    private final JMenu aboutMenu;

    private final JMenuItem vlcMenuItem;

    private final JMenuItem vlcjMenuItem;

    private final JMenuItem aboutIMPMenuItem;

    public UIMenuBar() {
        super();

        // File Menu.
        fileMenu = new JMenu(RESOURCE_BUNDLE.getString("menu.file"));
        fileMenu.getAccessibleContext().setAccessibleDescription(RESOURCE_BUNDLE.getString("menu.file.description"));

        this.add(fileMenu);

        openMenuItem = new JMenuItem(RESOURCE_BUNDLE.getString("menu.file.open"));
        openMenuItem.setMnemonic(KeyEvent.VK_O);
        fileMenu.add(openMenuItem);

        openNetworkFileMenuItem = new JMenuItem(RESOURCE_BUNDLE.getString("menu.file.network"));
        openNetworkFileMenuItem.setMnemonic(KeyEvent.VK_N);
        fileMenu.add(openNetworkFileMenuItem);

        exitMenuItem = new JMenuItem(RESOURCE_BUNDLE.getString("menu.file.exit"));
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        exitMenuItem.setMnemonic(KeyEvent.VK_X);
        fileMenu.add(exitMenuItem);

        // Actions Menu.
        actionsMenu = new JMenu(RESOURCE_BUNDLE.getString("menu.actions"));
        actionsMenu.getAccessibleContext().setAccessibleDescription(RESOURCE_BUNDLE.getString("menu.actions.description"));

        this.add(actionsMenu);

        increaseVolumeMenuItem = new JMenuItem(RESOURCE_BUNDLE.getString("menu.actions.increase_volume"));
        decreaseVolumeMenuItem = new JMenuItem(RESOURCE_BUNDLE.getString("menu.actions.decrease_volume"));

        actionsMenu.add(increaseVolumeMenuItem);
        actionsMenu.add(decreaseVolumeMenuItem);

        actionsMenu.addSeparator();
        goToMenuItem = new JMenuItem(RESOURCE_BUNDLE.getString("menu.actions.goto"));
        goToMenuItem.setMnemonic(KeyEvent.VK_G);
        actionsMenu.add(goToMenuItem);

        reloadLibraryMenuItem = new JMenuItem(RESOURCE_BUNDLE.getString("menu.actions.reload"));
        reloadLibraryMenuItem.setMnemonic(KeyEvent.VK_R);
        actionsMenu.add(reloadLibraryMenuItem);

        copyPlaylistsMenuItem = new JMenuItem(RESOURCE_BUNDLE.getString("menu.actions.copy_playlist"));
        copyPlaylistsMenuItem.setMnemonic(KeyEvent.VK_C);
        actionsMenu.add(copyPlaylistsMenuItem);

        copyTracksMenuItem = new JMenuItem(RESOURCE_BUNDLE.getString("menu.actions.copy_track"));
        copyTracksMenuItem.setMnemonic(KeyEvent.VK_T);
        actionsMenu.add(copyTracksMenuItem);

        // Tools Menu
        toolsMenu = new JMenu(RESOURCE_BUNDLE.getString("menu.tools"));
        toolsMenu.getAccessibleContext().setAccessibleDescription(RESOURCE_BUNDLE.getString("menu.tools.description"));
        this.add(toolsMenu);

        themesMenuItem = new JMenu(RESOURCE_BUNDLE.getString("menu.tools.themes"));
        themesMenuItem.setMnemonic(KeyEvent.VK_T);
        toolsMenu.add(themesMenuItem);

        themesSubMenuList = new ArrayList<JRadioButtonMenuItem>();

        updatesMenuItem = new JMenuItem(RESOURCE_BUNDLE.getString("menu.tools.update"));
        updatesMenuItem.setMnemonic(KeyEvent.VK_U);
        toolsMenu.add(updatesMenuItem);

        // About Menu
        aboutMenu = new JMenu(RESOURCE_BUNDLE.getString("menu.about"));
        aboutMenu.getAccessibleContext().setAccessibleDescription(RESOURCE_BUNDLE.getString("menu.about.description"));
        this.add(aboutMenu);

        vlcMenuItem = new JMenuItem(RESOURCE_BUNDLE.getString("menu.about.vlc"));
        aboutMenu.add(vlcMenuItem);

        vlcjMenuItem = new JMenuItem(RESOURCE_BUNDLE.getString("menu.about.vlcj"));
        aboutMenu.add(vlcjMenuItem);


        aboutIMPMenuItem = new JMenuItem(RESOURCE_BUNDLE.getString("menu.about.imp"));
        aboutMenu.add(aboutIMPMenuItem);

    }

    public JMenuItem getOpenMenuItem() {
        return openMenuItem;
    }

    public JMenuItem getOpenNetworkFileMenuItem() {
        return openNetworkFileMenuItem;
    }

    public JMenu getFileMenu() {
        return fileMenu;
    }

    public JMenu getActionsMenu() {
        return actionsMenu;
    }

    public JMenuItem getIncreaseVolumeMenuItem() {
        return increaseVolumeMenuItem;
    }

    public JMenuItem getDecreaseVolumeMenuItem() {
        return decreaseVolumeMenuItem;
    }

    public JMenuItem getReloadLibraryMenuItem() {
        return reloadLibraryMenuItem;
    }

    public JMenuItem getCopyPlaylistsMenuItem() {
        return copyPlaylistsMenuItem;
    }

    public JMenuItem getCopyTracksMenuItem() {
        return copyTracksMenuItem;
    }

    public JMenuItem getGoToMenuItem() {
        return goToMenuItem;
    }

    public JMenuItem getVlcMenuItem() {
        return vlcMenuItem;
    }

    public JMenuItem getVlcjMenuItem() {
        return vlcjMenuItem;
    }

    public JMenuItem getUpdatesMenuItem() {
        return updatesMenuItem;
    }

    public JMenuItem getAboutIMPMenuItem() {
        return aboutIMPMenuItem;
    }

    public JMenuItem getThemesMenuItem() {
        return themesMenuItem;
    }

    public List<JRadioButtonMenuItem> getThemesSubMenuList() {
        return themesSubMenuList;
    }

    public JMenuItem getExitMenuItem() {
        return exitMenuItem;
    }
}
