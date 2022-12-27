package com.example.kursach.fragments;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.kursach.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentUserList extends Fragment {

    private List<String> listUsers;
    private ArrayAdapter<String> adapter;
    private DatabaseReference mDB;
    private ListView lw_user_list;
    private String id;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        mDB = FirebaseDatabase.getInstance().getReference("Lobbies");

        id = getArguments().getString("key");

        lw_user_list = view.findViewById(R.id.lw_user_list);
        listUsers = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, listUsers);
        lw_user_list.setAdapter(adapter);

        getDataFromDB();

        return view;
    }

    private void getDataFromDB() {
        mDB.child(id).child("userList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (listUsers.size() > 0) listUsers.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    listUsers.add(ds.getValue(String.class));
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}