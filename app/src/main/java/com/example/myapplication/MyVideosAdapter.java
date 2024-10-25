package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.io.IOException;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyVideosAdapter extends RecyclerView.Adapter<MyVideosAdapter.VideoViewHolder> {
    private static final String TAG = "MyVideosAdapter";
    private static final String PREFS_NAME = "MyPrefs";
    private static final String TOKEN_KEY = "token";
    private static final String USER_ID_KEY = "userId";

    private Context context;
    private List<Video> videoList;
    private OkHttpClient client;

    public MyVideosAdapter(Context context, List<Video> videoList) {
        this.context = context;
        this.videoList = videoList;
        this.client = new OkHttpClient();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = videoList.get(position);
        holder.videoTitle.setText(video.getTitle());
        holder.videoDescription.setText(video.getDescription());
        holder.videoViews.setText(String.valueOf(video.getViews()) + " views");

        Glide.with(context)
                .load(video.getUrl())
                .into(holder.videoThumbnail);

        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditVideoActivity.class);
            intent.putExtra("videoId", video.getId());
            intent.putExtra("videoTitle", video.getTitle());
            intent.putExtra("videoDescription", video.getDescription());
            intent.putExtra("videoUrl", video.getUrl());
            context.startActivity(intent);
        });

        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Video")
                    .setMessage("Are you sure you want to delete this video?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteVideo(video, position))
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void deleteVideo(Video video, int position) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(TOKEN_KEY, null);
        String userId = sharedPreferences.getString(USER_ID_KEY, null);

        if (token == null || userId == null) {
            Log.e(TAG, "Token or User ID not found in SharedPreferences");
            Toast.makeText(context, "Error: User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:5000/api/users/" + userId + "/videos/" + video.getId();
        Request request = new Request.Builder()
                .url(url)
                .delete()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error deleting video", e);
                ((MyVideosActivity) context).runOnUiThread(() ->
                        Toast.makeText(context, "Network error: Unable to delete video", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    ((MyVideosActivity) context).runOnUiThread(() -> {
                        videoList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, videoList.size());
                        Toast.makeText(context, "Video deleted successfully", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Log.e(TAG, "Failed to delete video: " + response.message());
                    ((MyVideosActivity) context).runOnUiThread(() ->
                            Toast.makeText(context, "Error: Unable to delete video", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView videoThumbnail;
        TextView videoTitle, videoDescription, videoViews;
        Button editButton, deleteButton;

        VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.video_thumbnail);
            videoTitle = itemView.findViewById(R.id.video_title);
            videoDescription = itemView.findViewById(R.id.video_description);
            videoViews = itemView.findViewById(R.id.video_views);
            editButton = itemView.findViewById(R.id.btn_edit);
            deleteButton = itemView.findViewById(R.id.btn_delete);
        }
    }
}