package com.example.kursach.fragments;

import static android.content.ContentValues.TAG;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kursach.Lobb.Video;
import com.example.kursach.R;
import com.example.kursach.adapter.Adapter;
import com.example.kursach.models.ModelHome;
import com.example.kursach.models.VideoYT;
import com.example.kursach.network.YoutubeAPI;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private DatabaseReference mDB;

    private String id;

    String GOOGLE_YOUTUBE_API_KEY = "AIzaSyDBF5XodYgxHoGha4pb-LqYEDQqr0Mvi8k";

    Button btn_search;
    EditText ed_search_text;
    View v;

    private Adapter adapter;
    private LinearLayoutManager manager;
    private List<VideoYT> videoList;
    private List<Video> videoLobbiList;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        v = getActivity().findViewById(android.R.id.content);

        id = getArguments().getString("key");

        videoList = new ArrayList<>();

        videoLobbiList = new ArrayList<>();

        mDB = FirebaseDatabase.getInstance().getReference("Lobbies");

        ed_search_text = view.findViewById(R.id.ed_search_track);

        Adapter.OnItemClickListener onItemClickListener = new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(VideoYT videoYT, int position) {
                VideoYT vid = videoList.get(position);

                Video vidl = new Video(vid.getId().getVidoeId(),
                        vid.getSnippet().getPublishedAt(),
                        vid.getSnippet().getTitle(),
                        vid.getSnippet().getDescription(),
                        vid.getSnippet().getThumbnails().getMedium().getUrl());

                videoLobbiList.add(vidl);

                mDB.child(id).child("trackList").setValue(videoLobbiList);

                Toast.makeText(getContext(), "Добавлен!", Toast.LENGTH_SHORT).show();

                ed_search_text.setText("");

                videoList.clear();
                adapter.notifyDataSetChanged();

            }
        };

        RecyclerView rw = view.findViewById(R.id.recyclerView);
        adapter = new Adapter(getContext(), videoList, onItemClickListener);
        manager = new LinearLayoutManager(getContext());
        rw.setAdapter(adapter);
        rw.setLayoutManager(manager);

        btn_search = view.findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(videoLobbiList.size() > 0) videoLobbiList.clear();
                try{
                    if (!TextUtils.isEmpty(ed_search_text.getText())){
                        String input_text = ed_search_text.getText().toString().replace(" ", "+");
                        getJson(input_text, 3);
                        getFromDB();
                        //TimeUnit.SECONDS.sleep(1);
                        //Toast.makeText(getContext(), String.valueOf(videoLobbiList.size()), Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getContext(), "You need to enter some text", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e){
                    Snackbar.make(v, "нажал", Toast.LENGTH_SHORT).show();
                }

            }
        });


        return view;
    }

    public void getJson(String search_text, int maxResults){
        String url = YoutubeAPI.BASE_URL +
                YoutubeAPI.sch +
                YoutubeAPI.part +
                YoutubeAPI.maxResults +
                String.valueOf(maxResults) +
                YoutubeAPI.query +
                search_text +
                YoutubeAPI.KEY;

        Call<ModelHome> data = YoutubeAPI.getHomeVideo().getYT(url);
        data.enqueue(new Callback<ModelHome>() {
            @Override
            public void onResponse(Call<ModelHome> call, Response<ModelHome> response) {
                if(response.errorBody() == null){
                    if (videoList.size() > 0){
                        videoList.clear();
                    }
                    ModelHome mh = response.body();
                    videoList.addAll(mh.getItems());
                    adapter.notifyDataSetChanged();
                }
                else
                {
                    Log.v(TAG, "onResponse: " + response.errorBody());
                }
            }
            @Override
            public void onFailure(Call<ModelHome> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    public void getFromDB(){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    videoLobbiList.add(ds.getValue(Video.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        mDB.child(id).child("trackList").addValueEventListener(valueEventListener);
    }

}