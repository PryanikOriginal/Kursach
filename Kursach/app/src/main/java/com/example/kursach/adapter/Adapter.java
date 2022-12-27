package com.example.kursach.adapter;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kursach.R;
import com.example.kursach.models.VideoYT;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnItemClickListener{
        void onItemClick(VideoYT videoYT, int position);
    }

    private final OnItemClickListener onItemClickListener;

    private Context context;
    private List<VideoYT> videoList;

    public Adapter(Context context, List<VideoYT> videoList, OnItemClickListener _onItemClickListener) {
        this.context = context;
        this.videoList = videoList;
        this.onItemClickListener = _onItemClickListener;
    }

    class YoutubeHolder extends RecyclerView.ViewHolder{

        ImageView thumbnail;
        TextView tw_title, tw_id;

        public YoutubeHolder(@NonNull View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.iw_thumbnails);
            tw_title = itemView.findViewById(R.id.tw_video_title);
            tw_id = itemView.findViewById(R.id.tw_videoId);
        }

        public void setData(VideoYT data) {
            String getTitle = data.getSnippet().getTitle();
            String getId = data.getId().getVidoeId();
            String getThumb = data.getSnippet().getThumbnails().getMedium().getUrl();
            tw_title.setText(getTitle);
            tw_id.setText(getId);
            Picasso.get()
                    .load(getThumb)
                    .placeholder(R.mipmap.ic_launcher)
                    .fit()
                    .centerCrop()
                    .into(thumbnail, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Thumbnail success");
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e(TAG, "Thumbnail error", e);
                        }
                    });
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_lobbi_class, parent, false);

        return new YoutubeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        VideoYT videoYT = videoList.get(position);
        YoutubeHolder yth = (YoutubeHolder) holder;
        yth.setData(videoYT);

        yth.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(videoYT, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }
}
