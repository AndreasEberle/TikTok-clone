package com.android.androidapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Formatter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.android.androidapp.ui.main.PlaceholderFragment;
import com.android.androidapp.ui.main.SectionsPagerAdapter;
import com.android.androidapp.ui.tasks.InitializeIndexTask;
import com.google.android.material.tabs.TabLayout;

// https://stackoverflow.com/questions/39961794/android-application-not-asking-for-camera-permission/39961857

public class MainActivity extends AppCompatActivity {
    private final String[] permissions = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        ViewPager viewPager = findViewById(R.id.view_pager);

        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        if (!permissionsGranted()) {
            ActivityCompat.requestPermissions(this, permissions, 1);
        }

        Context context = getApplicationContext();
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        PlaceholderFragment.settingsViewModel.localip.setValue(ip);

        if (!PlaceholderFragment.appnodeViewModel.started) {
            InitializeIndexTask task = new InitializeIndexTask(this, PlaceholderFragment.settingsViewModel, PlaceholderFragment.appnodeViewModel);
            task.execute();
        }
    }

    private boolean permissionsGranted() {

        boolean allPermissionsGranted = true;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            boolean hasWriteExternalPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            boolean hasReadExternalPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            boolean hasCameraPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
            if (!hasWriteExternalPermission || !hasReadExternalPermission || !hasCameraPermission) {
                allPermissionsGranted = false;
            }
        }
        return allPermissionsGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                boolean startActivity = true;

                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults.length > 0 && grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        String permission = permissions[i];
                        boolean showRationale = shouldShowRequestPermissionRationale(permission);
                        if (!showRationale) {
                            Toast.makeText(this, "Enable required permissions", Toast.LENGTH_LONG).show();
                            startActivity = false;
                            break;
                        } else {
                            Toast.makeText(this, "Enable required permissions", Toast.LENGTH_LONG).show();
                            break;
                        }
                    }
                }

                if(startActivity) {
                    MainActivity.this.finish();
                    MainActivity.this.startActivity(getIntent());
                }
            }
        }
    }
}