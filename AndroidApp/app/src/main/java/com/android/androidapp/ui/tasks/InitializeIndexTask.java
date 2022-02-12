package com.android.androidapp.ui.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.androidapp.ui.controllers.AppNodeViewModel;
import com.android.androidapp.ui.controllers.SettingsViewModel;

public class InitializeIndexTask extends AsyncTask<Void, String, String> {
    private final Activity activity;
    private final String gatewayIP;
    private final int gatewayPort;
    private final String localIP;
    private final int localport;
    private final SettingsViewModel settingsViewModel;
    private final AppNodeViewModel appNodeViewModel;

    public InitializeIndexTask(Activity activity, SettingsViewModel settingsViewModel, AppNodeViewModel appNodeViewModel) {
        this.activity = activity;
        gatewayIP = settingsViewModel.gatewayIP.getValue();
        gatewayPort = settingsViewModel.gatewayPort.getValue();
        localIP = settingsViewModel.localip.getValue();
        localport = settingsViewModel.localport.getValue();
        this.settingsViewModel = settingsViewModel;
        this.appNodeViewModel = appNodeViewModel;
    }

    protected String doInBackground(Void... urls) {
        publishProgress("starting initialization (main activity)");

        try {
            appNodeViewModel.start(gatewayIP, gatewayPort, localIP, localport);
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
    }

    protected void onProgressUpdate(String... progress) {
        Toast.makeText(activity, progress[0], Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(String m) {
        super.onPostExecute(m);

        if (m == null) {
            Toast.makeText(activity, "Initialization complete (main activity)", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "Initialization failed:" + m, Toast.LENGTH_SHORT).show();
        }
    }
}
