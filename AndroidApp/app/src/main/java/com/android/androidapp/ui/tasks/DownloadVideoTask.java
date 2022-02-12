package com.android.androidapp.ui.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.androidapp.ui.controllers.AppNodeViewModel;

import java.io.IOException;

import media.Video;

public class DownloadVideoTask extends AsyncTask<Void, String, Video> {
    private Video video;
    private final Activity activity;
    private final AppNodeViewModel appnodeViewModel;

    public DownloadVideoTask(Video video, Activity activity , AppNodeViewModel appnodeViewModel) {
        this.video = video;
        this.activity = activity;
        this.appnodeViewModel = appnodeViewModel;
    }

    @Override
    protected Video doInBackground(Void... voids) {


        try {
            return appnodeViewModel.node.pull(video.topic,video.filename);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Video video) {
        super.onPostExecute(video);

        if (video != null) {
            Toast.makeText(activity, "Download successful", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "Download failed", Toast.LENGTH_SHORT).show();
        }
    }
}
