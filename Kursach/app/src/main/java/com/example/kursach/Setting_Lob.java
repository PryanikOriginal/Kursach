package com.example.kursach;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kursach.Lobb.Lobbi;
import com.example.kursach.Lobb.Video;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Setting_Lob extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_lob);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        init();
        GetData();
    }

    private void GetData(){
        ValueEventListener vel = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if(idLobbiList.size() > 0) idLobbiList.clear();

                    Lobbi lobbi = ds.getValue(Lobbi.class);
                    assert lobbi != null;
                    idLobbiList.add(lobbi.id_lobbi);
                    keyList.add(lobbi.key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        };
        mDB.addValueEventListener(vel);
    }

    private LinearLayout root;
    private EditText ed_id_lobbi, ed_count_us, ed_lobbi_pass;
    private CheckBox ch_isClose, ch_unlim_lob;
    private Button butt_confirm, butt_cancel;
    private DatabaseReference mDB;
    private List<String> idLobbiList, keyList;


    public void init(){
        ed_id_lobbi = (EditText) findViewById(R.id.ed_id_lobbi);
        ed_count_us = (EditText) findViewById(R.id.ed_count_us);
        ed_lobbi_pass = (EditText) findViewById(R.id.ed_lobbi_pass);
        ch_isClose = (CheckBox) findViewById(R.id.isClose_check);
        ch_unlim_lob = (CheckBox) findViewById(R.id.count_us_check);
        root = (LinearLayout) findViewById(R.id.set_lobbi);
        butt_confirm = (Button) findViewById(R.id.butt_confirm);
        butt_cancel = (Button) findViewById(R.id.butt_cancel);

        mDB = FirebaseDatabase.getInstance().getReference("Lobbies");

        idLobbiList = new ArrayList<>();
        keyList = new ArrayList<>();

        ch_unlim_lob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ch_unlim_lob.isChecked()){
                    ed_count_us.setEnabled(true);
                }
                else{
                    ed_count_us.setEnabled(false);
                    ed_count_us.setText("");
                }
            }
        });

        ch_isClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ch_isClose.isChecked()){
                    ed_lobbi_pass.setEnabled(true);
                }
                else{
                    ed_lobbi_pass.setEnabled(false);
                    ed_lobbi_pass.setText("");
                }
            }
        });


    }

    public void onCreateLobbi(View view){
        Integer maxCountUsers = Integer.MAX_VALUE;
        String passLobbi = ed_lobbi_pass.getText().toString();
        String admin_nick = MainActivity.USER_NICK_NAME;
        String id_lobbi = ed_id_lobbi.getText().toString();
        boolean isClose = false;

        if(idLobbiList.contains(id_lobbi)){
            Toast.makeText(this, "Такой идентификатор уже существует.", Toast.LENGTH_LONG).show();
            return;
        }
        if(id_lobbi.length() < 6){
            Snackbar.make(root, "Идентификатор лобби должен быть длинне 5 символов", Snackbar.LENGTH_LONG).show();
            return;
        }
        if(ch_unlim_lob.isChecked()){
                if(ed_count_us.isEnabled() &&
                        ed_count_us.getText().toString().length() < 1 ) {
                    Snackbar.make(root, "Количество не может быть пустым или <1", Snackbar.LENGTH_LONG).show();
                    return;
                }
                else{
                    maxCountUsers = Integer.parseInt(ed_count_us.getText().toString());
                    if(maxCountUsers < 1){
                        Snackbar.make(root, "Количество не может быть пустым или <1", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                }
        }
        if(ch_isClose.isChecked()){
                if(ed_lobbi_pass.isEnabled() &&
                        passLobbi.length() > 3) {
                    passLobbi = ed_lobbi_pass.getText().toString();
                    isClose = ch_isClose.isChecked();
                }
                else{
                    Snackbar.make(root, "Пароль не может быть пустым или <4 символов", Snackbar.LENGTH_LONG).show();
                    return;
                }
        }

        String key = mDB.push().getKey();
        Lobbi lob = makeLobbi(key, id_lobbi, admin_nick, isClose, maxCountUsers, passLobbi);

        try{
            mDB.child(lob.key).setValue(lob);
            Intent intent = new Intent(Setting_Lob.this, activity_in_lobbi.class);
            intent.putExtra("id", key);
            intent.putExtra("namelob", id_lobbi);
            startActivity(intent);
        }
        catch (Exception e){
            Snackbar.make(root, e.toString(), Snackbar.LENGTH_LONG).show();
        }

    }

    private Lobbi makeLobbi(String key, String id_lobbi, String admin_nick, boolean isClose, int maxCountUsers, String passLobbi){
        Lobbi lob;

        ArrayList<String> userList = new ArrayList<String>();
        ArrayList<Video> trackList = new ArrayList<Video>();

        if(isClose){
            lob = new Lobbi(key, id_lobbi, admin_nick, isClose, maxCountUsers, passLobbi, userList, trackList);
            lob.addUserInLobbi(lob.admin);
        }
        else{
            lob = new Lobbi(key, id_lobbi, admin_nick, maxCountUsers, isClose, userList, trackList);
            lob.addUserInLobbi(lob.admin);
        }
        return lob;
    }

    private void backToMainWin(View view){

    }
}