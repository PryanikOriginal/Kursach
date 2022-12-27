package com.example.kursach.fragments;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kursach.Lobb.Video;
import com.example.kursach.R;
import com.example.kursach.adapter.Adapter;
import com.example.kursach.models.SnippetYT;
import com.example.kursach.models.ThumbnailsYT;
import com.example.kursach.models.VideoID;
import com.example.kursach.models.VideoYT;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentTrackList extends Fragment {

    private String id;

    private List<VideoYT> videoYTList;
    private Adapter adapter;
    private LinearLayoutManager manager;
    private RecyclerView rw;

    DatabaseReference mDB;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_list, container, false);

        id = getArguments().getString("key");

        mDB = FirebaseDatabase.getInstance().getReference("Lobbies/"+id+"/trackList");


        rw = view.findViewById(R.id.rw_track_list);
        videoYTList = new ArrayList<>();
        adapter = new Adapter(getContext(), videoYTList, new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(VideoYT videoYT, int position) {

            }
        });
        manager = new LinearLayoutManager(getContext());
        rw.setAdapter(adapter);
        rw.setLayoutManager(manager);

        getFromDB();

        return view;
    }

    private void getFromDB() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(videoYTList.size() > 0) videoYTList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    Video vid = ds.getValue(Video.class);

                    ThumbnailsYT thumbnailsYT = new ThumbnailsYT();
                    ThumbnailsYT.MediumThumb mediumThumb = new ThumbnailsYT.MediumThumb();
                    mediumThumb.setUrl(vid.getThumbnails());
                    thumbnailsYT.setMedium(mediumThumb);
                    SnippetYT snipp = new SnippetYT(vid.getPublishedAt(), vid.getTitle(), vid.getDescription(), thumbnailsYT);
                    VideoYT vidYT = new VideoYT(new VideoID(vid.getId()), snipp);

                    videoYTList.add(vidYT);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        mDB.addValueEventListener(valueEventListener);
    }
}