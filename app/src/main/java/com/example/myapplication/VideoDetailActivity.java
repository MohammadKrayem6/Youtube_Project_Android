package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class VideoDetailActivity extends AppCompatActivity {

    private VideoView videoView;
    private Button btnLike, btnShare, btnComment;
    private TextView videoTitle, videoDescription, videoViews, videoOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);

        // Initialize views
        videoView = findViewById(R.id.video_view);
        btnLike = findViewById(R.id.btn_like);
        btnShare = findViewById(R.id.btn_share);
        btnComment = findViewById(R.id.btn_comment);
        videoTitle = findViewById(R.id.video_title);
        videoDescription = findViewById(R.id.video_description);
        videoViews = findViewById(R.id.video_views);
        videoOwner = findViewById(R.id.video_owner);

        // Get intent extras
        String videoUrl = getIntent().getStringExtra("videoUrl");
        String title = getIntent().getStringExtra("videoTitle");
        String description = getIntent().getStringExtra("videoDescription");
        int views = getIntent().getIntExtra("videoViews", 0);
        String owner = getIntent().getStringExtra("videoOwner");

        // Set up video player
        if (videoUrl != null && !videoUrl.isEmpty()) {
            videoView.setVideoPath(videoUrl);
            videoView.start();
        }

        // Set text views
        videoTitle.setText(title != null ? title : "");
        videoDescription.setText(description != null ? description : "");
        videoViews.setText(views + " views");
        videoOwner.setText("Posted by: " + (owner != null ? owner : "Unknown"));

        // Set up button click listeners
        btnLike.setOnClickListener(v -> {
            Toast.makeText(VideoDetailActivity.this, "Liked!", Toast.LENGTH_SHORT).show();
            btnLike.setEnabled(false);
            btnLike.setText("Liked");
        });

        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this video: " + videoUrl);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });

        btnComment.setOnClickListener(v -> {
            Toast.makeText(VideoDetailActivity.this, "Comments feature coming soon!", Toast.LENGTH_SHORT).show();
        });
    }
}