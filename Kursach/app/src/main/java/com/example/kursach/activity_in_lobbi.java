package com.example.kursach;

import static android.content.ContentValues.TAG;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.kursach.Lobb.Video;
import com.example.kursach.fragments.FragmentTrackList;
import com.example.kursach.fragments.FragmentUserList;
import com.example.kursach.fragments.SearchFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class activity_in_lobbi extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener
, YouTubePlayer.PlaybackEventListener, YouTubePlayer.PlayerStateChangeListener{

    Fragment fragmentButtonList;

    private String admin;

    private String nameLob, key;
    public DatabaseReference mDB;
    private ArrayList<String> users;
    private ArrayAdapter<String> adapterUsers;
    private List<String> lobbiList, keys;

    String VIDEO_ID = "1uwvxTT5V5M";

    TextView tw_in_lobbi;

    YouTubePlayerView playerView;
    YouTubePlayer ytPlayer;
    ImageButton start_button, next_button, previous_button;
    ProgressBar pb;

    ListView lw_user_list;

    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_lobbi);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Log.v(TAG, "создалось");

        init();
        LoadTrackList();
        getFromDB();
    }

    private void getFromDB() {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(users.size() > 0) users.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    users.add(ds.getValue(String.class));
                }
                //Toast.makeText(activity_in_lobbi.this, "Назад"+String.valueOf(users.size()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };


        mDB.child(key).child("userList").addValueEventListener(vListener);
    }

    public void init(){
        pref = getSharedPreferences("user_nickname", MODE_PRIVATE);

        Bundle getData = getIntent().getExtras();
        nameLob = getData.get("namelob").toString();
        key = getData.get("id").toString();
        mDB = FirebaseDatabase.getInstance().getReference("Lobbies/");

        mDB.child(key).child("admin").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                admin = task.getResult().getValue(String.class);
            }
        });


        users = new ArrayList<>();
        lobbiList = new ArrayList<>();
        keys = new ArrayList<>();


        tw_in_lobbi = findViewById(R.id.tw_id_lobbi);
        tw_in_lobbi.setText(nameLob);
        playerView = findViewById(R.id.playerView);

        String GOOGLE_YOUTUBE_API_KEY = "AIzaSyAGZQPcXhLR1qj7BB4zCJI25XnE9S2kp1k";
        playerView.initialize(GOOGLE_YOUTUBE_API_KEY, activity_in_lobbi.this);


        start_button = findViewById(R.id.btn_start_video);
        next_button = findViewById(R.id.btn_next_video);
        previous_button = findViewById(R.id.btn_previous_video);
        pb = findViewById(R.id.pb);

    }

    public void onClickMenuButton(View view){
        Bundle bundle = new Bundle();
        bundle.putString("key", key);
        switch (view.getId()){
            case R.id.btn_search:{
                fragmentButtonList = new SearchFragment();
                fragmentButtonList.setArguments(bundle);
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.frame_for_fragments, fragmentButtonList).commit();
                break;
            }
            case R.id.btn_track_list:{
                fragmentButtonList = new FragmentTrackList();
                fragmentButtonList.setArguments(bundle);
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.frame_for_fragments, fragmentButtonList).commit();
                break;
            }
            case R.id.btn_users_list:{
                fragmentButtonList = new FragmentUserList();
                fragmentButtonList.setArguments(bundle);
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.frame_for_fragments, fragmentButtonList).commit();
                break;
            }
        }
    }

    int maxProgress = 0;

    public void setProgressValue(int progress){
        pb.setProgress(progress);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Thread.sleep(1);
                    if (pb.getProgress() >= maxProgress && !ytPlayer.isPlaying()){

                    }
                    else{
                        setProgressValue(ytPlayer.getCurrentTimeMillis());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    public void onStartVideo(View view){
        if(!ytPlayer.isPlaying()){
            start_button.setImageResource(R.drawable.pause);
            ytPlayer.play();
        }
        else{
            start_button.setImageResource(R.drawable.play);
            ytPlayer.pause();
        }
    }

    List<Video> video = new ArrayList<>();
    boolean isPlaying = false;
    boolean play = false;

    private void LoadTrackList(){
        mDB.child(key).child("trackList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(video.size() > 0) {
                    video.clear();
                    play = false;
                }
                else{
                    play = true;
                }
                for(DataSnapshot ds : snapshot.getChildren()){
                    video.add(ds.getValue(Video.class));
                }
                if(play && !isPlaying) videoStart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void videoStart(){
        if (video.size() > 0) {
            ytPlayer.loadVideo(video.get(0).getId());
            video.remove(0);
            mDB.child(key).child("trackList").setValue(video);
            start_button.setImageResource(R.drawable.pause);
        }
    }

    public void onNextVideo(View view){
        videoStart();
    }

    public void onPreviousVideo(View view){

    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.v(TAG, "запустились");
        if(dd) startActivity(new Intent(this, MainActivity.class));
    }

    boolean dd = false;

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "свернули");
        onDestroy();
        dd = true;
    }

    boolean destroy = false;

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "закрылись");
        Toast.makeText(this, admin, Toast.LENGTH_LONG).show();
        if(!dd)
        {
            if (pref.getString("user_nickname", "the_user_does_not_have_nick_name").equals(users.get(0)) &&
                    pref.getString("user_nickname", "the_user_does_not_have_nick_name").equals(admin)) {
                destroy = true;
                //Toast.makeText(this, "Удаляем", Toast.LENGTH_LONG).show();
            }

            users.remove(pref.getString("user_nickname", "the_user_does_not_have_nick_name"));
            mDB.child(key).child("userList").setValue(users);

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (destroy) {
                mDB.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.v(TAG, "норм вышли");
                    }
                });
            }
        }
    }

    int seccount = 0;

    @Override
    public void onBackPressed(){
        String newTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String[] time = newTime.split(":");
        int secs = Integer.parseInt(time[0]) * 3600 + Integer.parseInt(time[1]) * 60 + Integer.parseInt(time[2]);


        if(seccount != 0){
            if(secs - seccount < 10){
                ytPlayer.pause();
                startActivity(new Intent(this, MainActivity.class));
            }
            else{
                Toast.makeText(this, "Для выхода нажмите еще раз", Toast.LENGTH_SHORT).show();
                seccount = secs;
            }
        }
        else{
            Toast.makeText(this, "Для выхода нажмите еще раз", Toast.LENGTH_SHORT).show();
            seccount = secs;
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.setPlayerStateChangeListener(this);
        youTubePlayer.setPlaybackEventListener(this);

        ytPlayer = youTubePlayer;
    }

    @Override
    public void onPlaying(){
        maxProgress = ytPlayer.getDurationMillis();
        pb.setMax(maxProgress);
        setProgressValue(ytPlayer.getCurrentTimeMillis());
        isPlaying = true;
        //Toast.makeText(this, "playing", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoaded(String s){
        //Toast.makeText(this, "load", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoading(){

    }

    @Override
    public void onVideoStarted() {
        //Toast.makeText(this, "started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onVideoEnded() {
        //Toast.makeText(this, "ended", Toast.LENGTH_SHORT).show();

        videoStart();
        if (play)
        isPlaying = false;
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {

    }

    @Override
    public void onAdStarted() {

    }

    @Override
    public void onBuffering(boolean b) {

    }

    @Override
    public void onStopped() {
        //Toast.makeText(this, "stopped", Toast.LENGTH_SHORT).show();
        //ytPlayer.play();
    }

    @Override
    public void onPaused() {
        //Toast.makeText(this, "paused", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSeekTo(int i) {

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }
}