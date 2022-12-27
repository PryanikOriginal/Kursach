package com.example.kursach.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoID {

    @SerializedName("videoId")
    @Expose
    private String videoId;

    public VideoID(){
    }

    public VideoID(String vidoeId) {
        this.videoId = vidoeId;
    }

    public String getVidoeId() {
        return videoId;
    }

    public void setVidoeId(String vidoeId) {
        this.videoId = vidoeId;
    }
}
