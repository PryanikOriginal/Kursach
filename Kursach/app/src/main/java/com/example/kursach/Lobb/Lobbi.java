package com.example.kursach.Lobb;

import java.util.ArrayList;

public class Lobbi {

    public String id_lobbi, admin;
    public Integer max_count_users;
    public boolean isClose;
    public String pass;
    public ArrayList<Video> trackList;
    public ArrayList<String> userList;
    public String key;

    public Lobbi() {
    }

    public Lobbi(String _key, String _id_lobbi, String _admin, Integer _max_count_users, boolean _isClose, ArrayList<String> userList, ArrayList<Video> trackList)
    {
        this.key = _key;
        this.id_lobbi = _id_lobbi;
        this.admin = _admin;
        this.max_count_users = _max_count_users;
        this.isClose = _isClose;
        this.userList = userList;
        this.trackList = trackList;
    }

    public Lobbi(String _key, String id_lobbi, String admin, boolean isClose, Integer max_count_users, String pass, ArrayList<String> userList, ArrayList<Video> trackList) {
        this.key = _key;
        this.id_lobbi = id_lobbi;
        this.admin = admin;
        this.max_count_users = max_count_users;
        this.isClose = isClose;
        this.pass = pass;
        this.userList = userList;
        this.trackList = trackList;
    }

    public void addInQueue(Video video)
    {
        this.trackList.add(video);
    }

    public void addUserInLobbi(String name)
    {
        this.userList.add(name);
    }
}
