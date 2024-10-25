package com.example.myapplication;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditVideoActivity extends AppCompatActivity {
    private static final String TAG = "EditVideoActivity";
    private static final String PREFS_NAME = "MyPrefs";
    private static final String USER_ID_KEY = "userId";
    private static final String TOKEN_KEY = "token";

    private EditText editTitle, editDescription, editUrl;
    private Button btnSave;
    private OkHttpClient client;
    private String videoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_video);

        editTitle = findViewById(R.id.edit_title);
        editDescription = findViewById(R.id.edit_description);
        editUrl = findViewById(R.id.edit_url);
        btnSave = findViewById(R.id.btn_save);

        client = new OkHttpClient();

        // Retrieve video details from intent
        videoId = getIntent().getStringExtra("videoId");
        String title = getIntent().getStringExtra("videoTitle");
        String description = getIntent().getStringExtra("videoDescription");
        String url = getIntent().getStringExtra("videoUrl");

        // Populate fields with current video data
        editTitle.setText(title);
        editDescription.setText(description);
        editUrl.setText(url);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTitle = editTitle.getText().toString().trim();
                String newDescription = editDescription.getText().toString().trim();
                String newUrl = editUrl.getText().toString().trim();

                if (newTitle.isEmpty() || newUrl.isEmpty()) {
                    Toast.makeText(EditVideoActivity.this, "Title and URL are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                updateVideo(newTitle, newDescription, newUrl);
            }
        });
    }

    private void updateVideo(String title, String description, String url) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString(USER_ID_KEY, null);
        String token = sharedPreferences.getString(TOKEN_KEY, null);

        if (userId == null || token == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("title", title);
            json.put("description", description);
            json.put("url", url);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url("http://10.0.2.2:5000/api/users/" + userId + "/videos/" + videoId)
                .put(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error updating video", e);
                runOnUiThread(() -> Toast.makeText(EditVideoActivity.this, "Network error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(EditVideoActivity.this, "Video updated successfully", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    });
                } else {
                    Log.e(TAG, "Error updating video: " + response.message());
                    runOnUiThread(() -> Toast.makeText(EditVideoActivity.this, "Error updating video", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}