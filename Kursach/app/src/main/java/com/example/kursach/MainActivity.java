package com.example.kursach;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kursach.Lobb.Lobbi;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public DatabaseReference mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        init();
        GetNick();
        try{
            GetFromDB();
        }
        catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Toast.makeText(MainActivity.this, "Назад", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private Button create_new_lobbi;
    private EditText ed_search_id_lobbi;
    private ListView list_lobbi;
    private List<String> lobbiList, keys, passwords;
    private List<Boolean> isCloseLobbi;
    private ArrayAdapter<String> adapter;

    final Context context = this;
    public static String USER_NICK_NAME;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    int countUs;
    List<String> userList;
    List<Lobbi> Listik_lobistik;

    public void init(){
        mDB = FirebaseDatabase.getInstance().getReference("Lobbies");

        create_new_lobbi = findViewById(R.id.create_lobbi);

        list_lobbi = findViewById(R.id.lobbi_list);
        lobbiList = new ArrayList<String>();
        keys = new ArrayList<String>();
        passwords = new ArrayList<String>();
        isCloseLobbi = new ArrayList<Boolean>();

        userList = new ArrayList<>();
        Listik_lobistik = new ArrayList<>();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lobbiList);
        list_lobbi.setAdapter(adapter);
        list_lobbi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String nameLob = (String) list_lobbi.getItemAtPosition(i);

                //Toast.makeText(MainActivity.this, nameLob+"/"+lobbiList.indexOf(nameLob)+"/"+keys.get(lobbiList.indexOf(nameLob)), Toast.LENGTH_SHORT).show();
                Lobbi lobi = Listik_lobistik.get(keys.indexOf(keys.get(lobbiList.indexOf(nameLob))));
                if(userList.size() > 0) userList.clear();
                for(String k : lobi.userList){
                    userList.add(k);
                }
                countUs = lobi.max_count_users;
                //Toast.makeText(MainActivity.this, String.valueOf(userList.size()), Toast.LENGTH_SHORT).show();

                /*mDB.child(keys.get(lobbiList.indexOf(nameLob))).child("max_count_users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        countUs = task.getResult().getValue(Integer.class);
                        //Toast.makeText(MainActivity.this, "Размер/"+lob[0].userList.get(0), Toast.LENGTH_SHORT).show();
                    }
                });

                mDB.child(keys.get(lobbiList.indexOf(nameLob))).child("userList").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(userList.size() > 0) userList.clear();
                        for(DataSnapshot ds : snapshot.getChildren()){
                            userList.add(ds.getValue(String.class));
                            Toast.makeText(MainActivity.this, ds.getValue(String.class), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/


                if(userList.size() < countUs){
                    //Toast.makeText(MainActivity.this, String.valueOf(userList.size()) + "/" + String.valueOf(countUs), Toast.LENGTH_SHORT).show();
                    if(isCloseLobbi.get(lobbiList.indexOf(nameLob))){
                        try{
                            EnterLobbi(passwords.get(lobbiList.indexOf(nameLob)), keys.get(lobbiList.indexOf(nameLob)), nameLob, userList);
                            //Toast.makeText(MainActivity.this, String.valueOf(users.size()), Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e){
                            //Toast.makeText(MainActivity.this, String.valueOf(passwords.get(lobbiList.indexOf(nameLob))), Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        EnterLobbiOpen(keys.get(lobbiList.indexOf(nameLob)), nameLob, userList);
                        //AddDataListUsers(keys.get(lobbiList.indexOf(nameLob)), nameLob, list);
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Комната полная.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        ed_search_id_lobbi = findViewById(R.id.ed_search_id_lobbi);
        ed_search_id_lobbi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String serach = ed_search_id_lobbi.getText().toString();
                ArrayList<String> searchList = new ArrayList<>();
                ArrayAdapter<String> adap = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, searchList);
                if(serach != ""){
                    if(searchList.size() > 0) searchList.clear();
                    for(String sear : lobbiList){
                        if(sear.contains(serach)){
                            searchList.add(lobbiList.get(lobbiList.indexOf(sear)));
                        }
                    }
                    adap.notifyDataSetChanged();
                    list_lobbi.setAdapter(adap);
                }
                else{
                    list_lobbi.setAdapter(adapter);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        pref = getSharedPreferences("user_nickname", MODE_PRIVATE);
        editor = pref.edit();
    }

    private void EnterLobbi(String pass, String key, String nameLob, List<String> usersList){
        try{
            LayoutInflater lai = LayoutInflater.from(context);
            View promptsView = lai.inflate(R.layout.enter_the_lobb, null);

            AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);
            mDialogBuilder.setView(promptsView);
            final EditText inputPass = (EditText) promptsView.findViewById(R.id.input_text);
            mDialogBuilder
                    .setCancelable(true)
                    .setPositiveButton("Готово!",
                            (dialog, id) -> {
                                String password = inputPass.getText().toString();
                                if(!pass.equals(password)){
                                    Toast.makeText(MainActivity.this, " Неверный пароль", Toast.LENGTH_SHORT).show();
                                    EnterLobbi(pass, key, nameLob, usersList);
                                    return;
                                }

                                AddDataListUsers(key, nameLob, usersList);
                            });

            AlertDialog alertDialog = mDialogBuilder.create();
            alertDialog.show();
        }
        catch (Exception e){
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
        }

    }

    private void EnterLobbiOpen(String key, String nameLob, List<String> usersList){
        try{
            LayoutInflater lai = LayoutInflater.from(context);
            View promptsView = lai.inflate(R.layout.enter_the_lobb_open, null);

            AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);
            mDialogBuilder.setView(promptsView);
            final TextView tw = (TextView) promptsView.findViewById(R.id.tv);
            tw.setText(tw.getText()+nameLob);
            mDialogBuilder
                    .setCancelable(true)
                    .setPositiveButton("Войти",
                            (dialog, id) -> {
                                AddDataListUsers(key, nameLob, usersList);
                            });

            AlertDialog alertDialog = mDialogBuilder.create();
            alertDialog.show();
        }
        catch (Exception e){
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
        }

    }

    private void AddDataListUsers(String key, String nameLob, List<String> usersList)
    {
        String nick = pref.getString("user_nickname", "the_user_does_not_have_nick_name");
        if(!usersList.contains(nick))
        {
            usersList.add(nick);
            mDB.child(keys.get(lobbiList.indexOf(nameLob))).child("userList").setValue(usersList);
            //Toast.makeText(MainActivity.this, "Да", Toast.LENGTH_LONG).show();
        }

        Intent intent = new Intent(this, activity_users_lobbi.class);
        intent.putExtra("id", keys.get(lobbiList.indexOf(nameLob)));
        intent.putExtra("namelob", nameLob);
        startActivity(intent);
    }

    private void GetFromDB(){
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (lobbiList.size() > 0) lobbiList.clear();
                if (isCloseLobbi.size() > 0) isCloseLobbi.clear();
                if( keys.size() > 0) keys.clear();
                if( passwords.size() > 0) passwords.clear();

                for(DataSnapshot ds : snapshot.getChildren()){
                    Lobbi lobbi = ds.getValue(Lobbi.class);
                    assert lobbi != null;
                    lobbiList.add(lobbi.id_lobbi);
                    isCloseLobbi.add(lobbi.isClose);
                    keys.add(lobbi.key);
                    passwords.add(lobbi.pass);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        mDB.addValueEventListener(vListener);



        mDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(Listik_lobistik.size() > 0) Listik_lobistik.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    Listik_lobistik.add(ds.getValue(Lobbi.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetNick(){
        //editor.clear().commit();

        if(pref.getString("user_nickname", "the_user_does_not_have_nick_name") == "the_user_does_not_have_nick_name"){
            SetNick();
            Toast.makeText(this, "бамс", Toast.LENGTH_SHORT).show();
        }
        else{
            USER_NICK_NAME = pref.getString("user_nickname", "the_user_does_not_have_nick_name");
            Toast.makeText(this, "Добро пожаловать, " + USER_NICK_NAME, Toast.LENGTH_SHORT)
                 .show();
        }
    }

    private void SetNick(){   //Запоминание пользователя без исользования БД
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.setnick, null);

        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(context);
        mDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.input_text);
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Готово!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {

                                if(userInput.getText().toString().length() < 4){
                                    Toast.makeText(MainActivity.this, "Ник должен содержать 4 и более символов.",
                                            Toast.LENGTH_LONG).show();
                                    SetNick();
                                    return;
                                }
                                editor.putString("user_nickname", userInput.getText().toString())
                                      .commit();
                                USER_NICK_NAME = userInput.getText().toString();
                                Toast.makeText(MainActivity.this, "Добро пожаловать, " + USER_NICK_NAME, Toast.LENGTH_LONG)
                                     .show();
                            }
                        });

        AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();
    }


    public void onCreateLobbi(View view){
        startActivity(new Intent(MainActivity.this, Setting_Lob.class));
    }
}