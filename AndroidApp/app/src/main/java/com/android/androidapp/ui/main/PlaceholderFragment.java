package com.android.androidapp.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.androidapp.R;
import com.android.androidapp.ui.adapters.VideoAdapter;
import com.android.androidapp.ui.controllers.AppNodeViewModel;
import com.android.androidapp.ui.controllers.SettingsViewModel;
import com.android.androidapp.ui.tasks.DownloadListTask;
import com.android.androidapp.ui.tasks.RefreshTask;

import java.io.File;

import index.Index;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    public static final SettingsViewModel settingsViewModel = new SettingsViewModel();
    public static final AppNodeViewModel appnodeViewModel = new AppNodeViewModel(settingsViewModel);
    private final int index;

    private RecyclerView rv = null;

    private ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Toast.makeText(PlaceholderFragment.this.getActivity(), "Capture complete", Toast.LENGTH_SHORT).show();

                RefreshTask task = new RefreshTask(PlaceholderFragment.this.getActivity(), appnodeViewModel, rv);
                task.execute();
            } else {
                Toast.makeText(PlaceholderFragment.this.getActivity(), "Capture cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    });

    public PlaceholderFragment(int index) {
        this.index = index;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        switch (index) {
            case 0:
                return inflater.inflate(R.layout.layout_network, container, false);
            case 1:
                return inflater.inflate(R.layout.layout_data, container, false);
            case 2:
                return inflater.inflate(R.layout.layout_pulled, container, false);
            case 3:
                return inflater.inflate(R.layout.layout_search, container, false);
            default:
                throw new IllegalArgumentException("invalid index");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        switch (index) {
            case 0:
                brokersLayout(view, savedInstanceState);
                break;
            case 1:
                dataLayout(view, savedInstanceState);
                break;
            case 2:
                pulledLayout(view, savedInstanceState);
                break;
            case 3:
                searchLayout(view, savedInstanceState);
                break;
            default:
                throw new IllegalArgumentException("invalid index");
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            View view = getView();
            if (getView() != null) {
                switch (index) {
                    case 0:
                        brokersLayout(view, null);
                        break;
                    case 1:
                        dataLayout(view, null);
                        break;
                    case 2:
                        pulledLayout(view, null);
                        break;
                    case 3:
                        searchLayout(view, null);
                        break;
                    default:
                        throw new IllegalArgumentException("invalid index");
                }
            }
        }
    }

    private View brokersLayout(View rootView, Bundle savedInstanceState) {

        TextView t1 = rootView.findViewById(R.id.editTextUsername);
        TextView t2 = rootView.findViewById(R.id.editTextGatewayIP);
        TextView t3 = rootView.findViewById(R.id.editTextGatewayPort);
        TextView t4 = rootView.findViewById(R.id.editTextLocalIP);
        TextView t5 = rootView.findViewById(R.id.editTextLocalPort);

        settingsViewModel.username.observe(getActivity(), newName -> t1.setText(newName));
        settingsViewModel.gatewayIP.observe(getActivity(), newName -> t2.setText(newName));
        settingsViewModel.gatewayPort.observe(getActivity(), newName -> t3.setText(String.valueOf(newName)));
        settingsViewModel.localip.observe(getActivity(), newName -> t4.setText(newName));
        settingsViewModel.localport.observe(getActivity(), newName -> t5.setText(String.valueOf(newName)));

        return rootView;
    }

    private View dataLayout(View rootView, Bundle savedInstanceState) {
        rv = rootView.findViewById(R.id.recyclerView);

        VideoAdapter adapter = new VideoAdapter(appnodeViewModel.node.index);

        rv.setAdapter(adapter);

        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        VideoAdapter.ViewHolder.clickListener = video -> {
            Uri uri = Uri.parse(video.path);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setDataAndType(uri, "video/mp4");
            startActivity(intent);
        };

        Button capture = rootView.findViewById(R.id.btnCapture);
        TextView t1 = rootView.findViewById(R.id.editTextNewChannelName);
        TextView t2 = rootView.findViewById(R.id.editTextNewVideoName);

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String channelname = t1.getText().toString();
                String videoname = t2.getText().toString();

                if (channelname.trim().isEmpty()) {
                    if (appnodeViewModel.node.index.size() > 0) {
                        channelname = appnodeViewModel.node.index.findByPosition(0).topic.split(":")[1];
                    } else{
                        return;
                    }
                }

                if (videoname.trim().isEmpty()) {
                    videoname = "video_" + appnodeViewModel.node.index.size() + 1 + ".mp4";
                }

                t1.setText(channelname);
                t2.setText(videoname);

                String path = appnodeViewModel.node.uploadDirectory + File.separator + channelname + File.separator + videoname;

                File file = new File(path);
                //Uri uri = Uri.fromFile(file);

                // bug fix:
                Uri uri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", file);

                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                launcher.launch(takeVideoIntent);
            }
        });

        return rootView;
    }

    private View pulledLayout(View rootView, Bundle savedInstanceState) {
        RecyclerView rv = rootView.findViewById(R.id.recyclerView);

        Index index = appnodeViewModel.getDownloads();

        VideoAdapter adapter = new VideoAdapter(index);

        rv.setAdapter(adapter);

        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        VideoAdapter.ViewHolder.clickListener = video -> {
            Uri uri = Uri.parse(video.path);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setDataAndType(uri, "video/mp4");
            startActivity(intent);
        };

        return rootView;
    }

    private View searchLayout(View rootView, Bundle savedInstanceState) {
        RecyclerView rv = rootView.findViewById(R.id.recyclerView);

        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        DownloadListTask task = new DownloadListTask(getActivity(), rv, appnodeViewModel);

        task.execute();

        return rootView;
    }
}