package com.android.androidapp.ui.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.androidapp.ui.adapters.RemoteVideoAdapter;
import com.android.androidapp.ui.controllers.AppNodeViewModel;

import index.Index;

public class DownloadListTask extends AsyncTask<Void, String, Index> {
    private Activity activity;
    private RecyclerView rv;
    private AppNodeViewModel appnodeViewModel;

    public DownloadListTask(Activity activity, RecyclerView rv, AppNodeViewModel appnodeViewModel) {
        this.activity = activity;
        this.rv = rv;
        this.appnodeViewModel = appnodeViewModel;
    }

    protected Index doInBackground(Void... videos) {
        publishProgress("getting list from brokers");

        Index index = appnodeViewModel.getAvailableList();

        return index;
    }

    protected void onProgressUpdate(String... progress) {
        Toast.makeText(activity, progress[0], Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(Index m) {
        super.onPostExecute(m);

        if (m != null) {
            Toast.makeText(activity, "Refresh complete", Toast.LENGTH_SHORT).show();

            RemoteVideoAdapter adapter = new RemoteVideoAdapter(m);

            if (rv != null) {
                rv.setAdapter(adapter);

                RemoteVideoAdapter.ViewHolder.clickListener = video -> {
                    DownloadVideoTask task = new DownloadVideoTask(video, activity, appnodeViewModel);
                    task.execute();
                };
            }
        }
    }
}