package com.gps.itunes.media.player.ui;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * Created by leogps on 11/27/14.
 */
public class UIMenuBar extends JMenuBar {

    // File Menu
    private final JMenu fileMenu;
    private final JMenuItem openMenuItem;
    private final JMenuItem openNetworkFileMenuItem;

    private final JMenu actionsMenu;

    private final JMenuItem increaseVolumeMenuItem;
    private final JMenuItem decreaseVolumeMenuItem;

    private final JMenuItem goToMenuItem;
    private final JMenuItem reloadLibraryMenuItem;
    private final JMenuItem copyPlaylistsMenuItem;
    private final JMenuItem copyTracksMenuItem;

    private final JMenu aboutMenu;

    private final JMenuItem vlcMenuItem;

    public UIMenuBar() {
        super();

        // File Menu.
        fileMenu = new JMenu("File");
        fileMenu.getAccessibleContext().setAccessibleDescription(
                "File operations");

        this.add(fileMenu);

        openMenuItem = new JMenuItem("Open");
        openMenuItem.setMnemonic(KeyEvent.VK_O);
        fileMenu.add(openMenuItem);

        openNetworkFileMenuItem = new JMenuItem("Open Network File");
        openMenuItem.setMnemonic(KeyEvent.VK_N);
        fileMenu.add(openNetworkFileMenuItem);

        // Actions Menu.
        actionsMenu = new JMenu("Actions");
        actionsMenu.getAccessibleContext().setAccessibleDescription(
                "Library Operations");

        this.add(actionsMenu);

        increaseVolumeMenuItem = new JMenuItem("Increase Volume");
        decreaseVolumeMenuItem = new JMenuItem("Decrease Volume");

        actionsMenu.add(increaseVolumeMenuItem);
        actionsMenu.add(decreaseVolumeMenuItem);

        actionsMenu.addSeparator();
        goToMenuItem = new JMenuItem("Go To");
        goToMenuItem.setMnemonic(KeyEvent.VK_G);
        actionsMenu.add(goToMenuItem);

        reloadLibraryMenuItem = new JMenuItem("Reload Library");
        reloadLibraryMenuItem.setMnemonic(KeyEvent.VK_R);
        actionsMenu.add(reloadLibraryMenuItem);

        copyPlaylistsMenuItem = new JMenuItem("Copy Playlist(s)");
        copyPlaylistsMenuItem.setMnemonic(KeyEvent.VK_C);
        actionsMenu.add(copyPlaylistsMenuItem);

        copyTracksMenuItem = new JMenuItem("Copy Tracks");
        copyTracksMenuItem.setMnemonic(KeyEvent.VK_T);
        actionsMenu.add(copyTracksMenuItem);

        // About Menu
        aboutMenu = new JMenu("About");
        aboutMenu.getAccessibleContext().setAccessibleDescription(
                "Library Operations");
        this.add(aboutMenu);

        vlcMenuItem = new JMenuItem("VLC");
        aboutMenu.add(vlcMenuItem);

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
}
