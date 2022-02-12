package com.android.androidapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.androidapp.R;

import index.Index;
import media.Video;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private Index index;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public interface ClickListener {
        void onItemClick(Video v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        public static ClickListener clickListener = null;

        public final TextView t1;
        public final TextView t2;
        public final TextView t3;
        public final TextView t4;
        public Video video;

        public ViewHolder(View view) {
            super(view);

            t1 = view.findViewById(R.id.id);
            t2 = view.findViewById(R.id.video_topic);
            t3 = view.findViewById(R.id.video_name);
            t4 = view.findViewById(R.id.video_path);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onItemClick(video);
            }
        }
    }

    public VideoAdapter(Index index) {
        this.index = index;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_video, viewGroup, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Video video = index.findByPosition(position);

        viewHolder.video = video;
        viewHolder.t1.setText(String.valueOf(video.id));
        viewHolder.t2.setText(video.topic);
        viewHolder.t3.setText(video.filename);
        viewHolder.t4.setText(video.path);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return index.size();
    }
}
