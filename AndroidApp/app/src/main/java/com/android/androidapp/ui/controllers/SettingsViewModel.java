package com.android.androidapp.ui.controllers;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {
    public final MutableLiveData<String> username = new MutableLiveData<>();
    public final MutableLiveData<String> localip = new MutableLiveData<>();
    public final MutableLiveData<Integer> localport = new MutableLiveData<>();
    public final MutableLiveData<String> gatewayIP = new MutableLiveData<>();
    public final MutableLiveData<Integer> gatewayPort = new MutableLiveData<>();

    public SettingsViewModel() {
        username.setValue("Luke");
        localport.setValue(44100);
        gatewayIP.setValue("192.168.1.23");
        gatewayPort.setValue(55100);
    }
}
