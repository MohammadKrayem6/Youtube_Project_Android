package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.io.IOException;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private static final String TAG = "VideoAdapter";
    private Context context;
    private List<Video> videoList;
    private OkHttpClient client;

    public VideoAdapter(Context context, List<Video> videoList) {
        this.context = context;
        this.videoList = videoList;
        this.client = new OkHttpClient();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = videoList.get(position);
        holder.videoTitle.setText(video.getTitle());
        holder.videoDescription.setText(video.getDescription());
        holder.videoViews.setText(String.valueOf(video.getViews()) + " views");
        holder.videoOwner.setText("Owner: " + (video.getOwner() != null ? video.getOwner().getName() : "Unknown"));

        // Log each video being bound
        Log.d(TAG, "Binding Video: " + video.getTitle() + " with URL: " + video.getUrl());

        // Load video thumbnail using Glide
        Glide.with(context)
                .setDefaultRequestOptions(
                        new RequestOptions()
                                .frame(1000000) // Load frame at 1 second
                                .centerCrop()
                )
                .load(video.getUrl())
                .into(holder.videoThumbnail);

        holder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "Video clicked: " + video.getTitle());
            Log.d(TAG, "Owner ID: " + (video.getOwner() != null ? video.getOwner().getId() : "Unknown"));
            Log.d(TAG, "Video ID: " + video.getId());
            incrementVideoViews(video);
            Intent intent = new Intent(context, VideoDetailActivity.class);
            intent.putExtra("videoUrl", video.getUrl());
            intent.putExtra("videoTitle", video.getTitle());
            intent.putExtra("videoDescription", video.getDescription());
            intent.putExtra("videoViews", video.getViews());
            intent.putExtra("videoOwner", video.getOwner() != null ? video.getOwner().getName() : "Unknown");
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    private void incrementVideoViews(Video video) {
        String ownerId = video.getOwner().getId();
        String videoId = video.getId();
        Log.d(TAG, "Incrementing views for owner ID: " + ownerId + ", video ID: " + videoId);

        String url = "http://10.0.2.2:5000/api/users/" + ownerId + "/videos/" + videoId + "/increment-views";
        RequestBody body = RequestBody.create("{}", MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .addHeader("Authorization", "Bearer " + getToken())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Failed to increment views for video: " + video.getTitle(), e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Successfully incremented views for video: " + video.getTitle());
                    // Update the views count in the UI if needed
                    video.setViews(video.getViews() + 1);
                    ((VideoListActivity) context).runOnUiThread(() -> notifyDataSetChanged());
                } else {
                    Log.e(TAG, "Failed to increment views for video: " + video.getTitle() + ", response: " + response.message());
                }
            }
        });
    }

    private String getToken() {
        // Retrieve the token from SharedPreferences or any other secure storage
        return context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("token", null);
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {

        ImageView videoThumbnail;
        TextView videoTitle;
        TextView videoDescription;
        TextView videoViews;
        TextView videoOwner;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.video_thumbnail);
            videoTitle = itemView.findViewById(R.id.video_title);
            videoDescription = itemView.findViewById(R.id.video_description);
            videoViews = itemView.findViewById(R.id.video_views);
            videoOwner = itemView.findViewById(R.id.video_owner);
        }
    }
}
