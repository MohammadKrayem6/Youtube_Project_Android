package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
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
import okhttp3.RequestBody;

public class VideoListActivity extends AppCompatActivity {

    private static final String TAG = "VideoListActivity";
    private static final int REQUEST_ADD_VIDEO = 1;

    private RecyclerView recyclerView;
    private RecyclerView recommendedRecyclerView;
    private VideoAdapter videoAdapter;
    private VideoAdapter recommendedVideoAdapter;
    private List<Video> videoList;
    private List<Video> recommendedVideoList;
    private DrawerLayout drawerLayout;
    private OkHttpClient client;

    private static final String PREFS_NAME = "MyPrefs";
    private static final String TOKEN_KEY = "token";
    private static final String USER_ID_KEY = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_add_video) {
                    Intent intent = new Intent(VideoListActivity.this, AddVideoActivity.class);
                    startActivityForResult(intent, REQUEST_ADD_VIDEO);
                } else if (id == R.id.nav_my_videos) {
                    Intent intent = new Intent(VideoListActivity.this, MyVideosActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_videos) {
                    loadVideos();
                    loadRecommendedVideos();
                } else if (id == R.id.nav_logout) {
                    logout();
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up the recommended videos RecyclerView
        recommendedRecyclerView = findViewById(R.id.recycler_view_recommended);
        recommendedRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        client = new OkHttpClient();

        loadVideos();
        loadRecommendedVideos();  // Load recommended videos on startup
    }

    private void loadVideos() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(TOKEN_KEY, null);

        if (token == null) {
            Log.e(TAG, "Token not found in SharedPreferences");
            return;
        }

        Request request = new Request.Builder()
                .url("http://10.0.2.2:5000/api/videos2")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error loading videos", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Log.d(TAG, "JSON Content: " + json);

                    Type videoListType = new TypeToken<List<Video>>() {}.getType();
                    videoList = new Gson().fromJson(json, videoListType);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            videoAdapter = new VideoAdapter(VideoListActivity.this, videoList);
                            recyclerView.setAdapter(videoAdapter);
                            Log.d(TAG, "RecyclerView adapter set");
                        }
                    });
                } else {
                    Log.e(TAG, "Failed to fetch videos: " + response.message());
                }
            }
        });
    }

    private void loadRecommendedVideos() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(TOKEN_KEY, null);

        if (token == null) {
            Log.e(TAG, "Token not found in SharedPreferences");
            return;
        }

        Request request = new Request.Builder()
                .url("http://10.0.2.2:5000/api/recommendations")
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error loading recommended videos", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Log.d(TAG, "Recommended JSON Content: " + json);

                    Type videoListType = new TypeToken<List<Video>>() {}.getType();
                    recommendedVideoList = new Gson().fromJson(json, videoListType);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recommendedVideoAdapter = new VideoAdapter(VideoListActivity.this, recommendedVideoList);
                            recommendedRecyclerView.setAdapter(recommendedVideoAdapter);
                            Log.d(TAG, "Recommended RecyclerView adapter set");
                        }
                    });
                } else {
                    Log.e(TAG, "Failed to fetch recommended videos: " + response.message());
                }
            }
        });
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(TOKEN_KEY, null);

        if (token != null) {
            // Make API call to invalidate token
            Request request = new Request.Builder()
                    .url("http://10.0.2.2:5000/api/logout") // Replace with your actual logout endpoint
                    .addHeader("Authorization", "Bearer " + token)
                    .post(RequestBody.create(null, new byte[0]))
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Failed to logout", e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Logout successful");
                    } else {
                        Log.e(TAG, "Logout failed: " + response.message());
                    }
                }
            });
        }

        // Clear user data from SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(TOKEN_KEY);
        editor.remove(USER_ID_KEY);
        editor.apply();

        // Navigate back to the login screen
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // This closes the current activity
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_VIDEO && resultCode == RESULT_OK && data != null) {
            String title = data.getStringExtra("title");
            String description = data.getStringExtra("description");
            String url = data.getStringExtra("url");

            Video newVideo = new Video();
            newVideo.setTitle(title);
            newVideo.setDescription(description);
            newVideo.setUrl(url);

            videoList.add(newVideo);
            videoAdapter.notifyItemInserted(videoList.size() - 1);
        }
    }

    public void onAddVideoClick(View view) {
        Intent intent = new Intent(this, AddVideoActivity.class);
        startActivityForResult(intent, REQUEST_ADD_VIDEO);
    }
}
