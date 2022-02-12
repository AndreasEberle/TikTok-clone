package com.android.androidapp.ui.controllers;

import androidx.lifecycle.ViewModel;

import java.io.IOException;

import index.Index;
import media.Video;
import nodes.AppNode;

public class AppNodeViewModel extends ViewModel {

    private final SettingsViewModel settingsViewModel;
    public AppNode node;
    public boolean started = false;

    public AppNodeViewModel(SettingsViewModel settingsViewModel) {
        this.settingsViewModel = settingsViewModel;
    }

    public void start(String gatewayIP, int gatewayPort, String localip, int localport) {
        if (started) {
            return;
        }

        try {
            node = new AppNode(settingsViewModel.username.getValue(), settingsViewModel.localport.getValue());

            node.loadIndex();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        started = true;

        try {
            node.printIndex();

            node.startServer();

            node.register(gatewayIP, gatewayPort, localip, localport);

            node.printBrokers();

            node.connectToAll(gatewayIP, gatewayPort, localip, localport);

            node.pushAll();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }


    public Video refresh() {
        try {
            return node.loadIndex();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    public Index getDownloads() {
        return node.getDownloads();
    }

    public Index getAvailableList() {
        return node.getAvailableList();
    }
}
