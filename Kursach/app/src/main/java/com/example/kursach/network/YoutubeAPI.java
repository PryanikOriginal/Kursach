package com.example.kursach.network;

import com.example.kursach.models.ModelHome;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;

public class YoutubeAPI {
  //https://www.googleapis.com/youtube/v3/
  //    search?
  //    part=snippet&
  //    maxResults=2&
  //    q=Hattori&
  //    key=AIzaSyDBF5XodYgxHoGha4pb-LqYEDQqr0Mvi8k
    public static final String BASE_URL = "https://www.googleapis.com/youtube/v3/";
    public static final String sch = "search?";
    public static final String part = "part=snippet&";
    public static final String maxResults = "maxResults=";
    public static final String query = "&q=";
    public static final String KEY = "&key=AIzaSyAGZQPcXhLR1qj7BB4zCJI25XnE9S2kp1k";

    public interface HomeVideo{
        @GET
        Call<ModelHome> getYT(@Url String url);
    }

    private static HomeVideo homeVideo = null;

    public static HomeVideo getHomeVideo(){
        if(homeVideo == null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            homeVideo = retrofit.create(HomeVideo.class);
        }
        return homeVideo;
    }
}
