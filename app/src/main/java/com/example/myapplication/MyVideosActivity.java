package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyVideosActivity extends AppCompatActivity {
    private static final String TAG = "MyVideosActivity";
    private RecyclerView recyclerView;
    private MyVideosAdapter myVideosAdapter;
    private List<Video> videoList;
    private OkHttpClient client;

    private static final String PREFS_NAME = "MyPrefs";
    private static final String TOKEN_KEY = "token";
    private static final String USER_ID_KEY = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_videos);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        client = new OkHttpClient();

        loadUserVideos();
    }

    private void loadUserVideos() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(TOKEN_KEY, null);
        String userId = sharedPreferences.getString(USER_ID_KEY, null);

        if (token == null || userId == null) {
            Log.e(TAG, "Token or User ID not found in SharedPreferences");
            return;
        }

        Request request = new Request.Builder()
                .url("http://10.0.2.2:5000/api/users/" + userId + "/videos")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error loading user videos", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Log.d(TAG, "JSON Content: " + json);

                    Type videoListType = new TypeToken<List<Video>>() {}.getType();
                    videoList = new Gson().fromJson(json, videoListType);

                    runOnUiThread(() -> {
                        myVideosAdapter = new MyVideosAdapter(MyVideosActivity.this, videoList);
                        recyclerView.setAdapter(myVideosAdapter);
                        Log.d(TAG, "RecyclerView adapter set for user videos");
                    });
                } else {
                    Log.e(TAG, "Failed to fetch user videos: " + response.message());
                }
            }
        });
    }
}