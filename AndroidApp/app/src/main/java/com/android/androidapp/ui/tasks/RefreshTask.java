package com.android.androidapp.ui.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.androidapp.ui.adapters.VideoAdapter;
import com.android.androidapp.ui.controllers.AppNodeViewModel;

import java.io.IOException;

import media.Video;

public class RefreshTask extends AsyncTask<Void, String, Video> {

    private Activity activity;
    private AppNodeViewModel appnodeViewModel;
    private RecyclerView rv;

    public RefreshTask(Activity activity, AppNodeViewModel appnodeViewModel, RecyclerView rv) {

        this.activity = activity;
        this.appnodeViewModel = appnodeViewModel;
        this.rv = rv;
    }
    protected Video doInBackground(Void... videos) {
        publishProgress("starting refreshing to brokers");

        Video video = appnodeViewModel.refresh();

        if (video == null) {
            publishProgress("video already stored to brokers");
            return null;
        }

        try {
            appnodeViewModel.node.push(video);
            return video;
        } catch (IOException e) {
            e.printStackTrace();
            publishProgress("video refresh failed: " + e.getMessage());
            return null;
        }
    }

    protected void onProgressUpdate(String... progress) {
        Toast.makeText(activity, progress[0], Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(Video m) {
        super.onPostExecute(m);

        if (m != null) {
            Toast.makeText(activity, "Refresh complete: " + m.filename, Toast.LENGTH_SHORT).show();

            VideoAdapter adapter = new VideoAdapter(appnodeViewModel.node.index);

            if (rv != null) {
                rv.setAdapter(adapter);
            }
        }
    }
}
