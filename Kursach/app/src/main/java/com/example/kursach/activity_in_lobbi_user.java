package com.example.kursach;

import static android.content.ContentValues.TAG;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kursach.fragments.FragmentTrackList;
import com.example.kursach.fragments.FragmentUserList;
import com.example.kursach.fragments.SearchFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class activity_in_lobbi_user extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_lobbi_user);

        init();
        getFromDB();
    }

    private String admin;

    private void init(){

        mDB = FirebaseDatabase.getInstance().getReference("Lobbies");

        Bundle getData = getIntent().getExtras();
        nameLob = getData.get("namelob").toString();
        key = getData.get("id").toString();

        pref = getSharedPreferences("user_nickname", MODE_PRIVATE);

        mDB.child(key).child("admin").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                admin = task.getResult().getValue(String.class);
                Toast.makeText(activity_in_lobbi_user.this, admin, Toast.LENGTH_SHORT).show();
            }
        });

        users = new ArrayList<>();

        tw_id_lobbi = findViewById(R.id.tw_id_lobbi);
    }

    private void getFromDB() {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(users.size() > 0) users.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    users.add(ds.getValue(String.class));
                }
                if(!users.contains(admin)) {
                    try{
                        startActivity(new Intent(activity_in_lobbi_user.this, MainActivity.class));
                    }
                    catch (Exception e){
                        Log.e(TAG, e.toString());
                    }
                }
                //Toast.makeText(activity_in_lobbi_user.this, "Назад"+String.valueOf(users.size()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDB.child(key).child("userList").addValueEventListener(vListener);
    }

    DatabaseReference mDB;

    private String nameLob, key;
    Fragment fragmentButtonList;
    private TextView tw_id_lobbi;
    private List<String> users;
    SharedPreferences pref;

    public void onClickMenuButton(View view){
        Bundle bundle = new Bundle();
        bundle.putString("key", key);
        switch (view.getId()){
            case R.id.btn_search2:{
                fragmentButtonList = new SearchFragment();
                fragmentButtonList.setArguments(bundle);
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.frame_for_fragments2, fragmentButtonList).commit();
                break;
            }
            case R.id.btn_track_list2:{
                fragmentButtonList = new FragmentTrackList();
                fragmentButtonList.setArguments(bundle);
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.frame_for_fragments2, fragmentButtonList).commit();
                break;
            }
            case R.id.btn_users_list2:{
                fragmentButtonList = new FragmentUserList();
                fragmentButtonList.setArguments(bundle);
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.frame_for_fragments2, fragmentButtonList).commit();
                break;
            }
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "закрылись");
        if(!dd)
        {
            users.remove(pref.getString("user_nickname", "the_user_does_not_have_nick_name"));
            mDB.child(key).child("userList").setValue(users);
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
}