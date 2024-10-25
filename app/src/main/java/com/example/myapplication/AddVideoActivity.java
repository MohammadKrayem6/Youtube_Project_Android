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

public class AddVideoActivity extends AppCompatActivity {
    private static final String TAG = "AddVideoActivity";
    private static final String PREFS_NAME = "MyPrefs";
    private static final String USER_ID_KEY = "userId";
    private static final String TOKEN_KEY = "token";

    private EditText editTitle, editDescription, editUrl;
    private Button btnSave;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video);

        editTitle = findViewById(R.id.edit_title);
        editDescription = findViewById(R.id.edit_description);
        editUrl = findViewById(R.id.edit_url);
        btnSave = findViewById(R.id.btn_save);

        client = new OkHttpClient();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTitle.getText().toString().trim();
                String description = editDescription.getText().toString().trim();
                String url = editUrl.getText().toString().trim();

                if (title.isEmpty() || url.isEmpty()) {
                    Toast.makeText(AddVideoActivity.this, "Title and URL are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                addVideo(title, description, url);
            }
        });
    }

    private void addVideo(String title, String description, String url) {
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
                .url("http://10.0.2.2:5000/api/users/" + userId + "/videos")
                .post(body)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error adding video", e);
                runOnUiThread(() -> Toast.makeText(AddVideoActivity.this, "Network error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(AddVideoActivity.this, "Video added successfully", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    });
                } else {
                    Log.e(TAG, "Error adding video: " + response.message());
                    runOnUiThread(() -> Toast.makeText(AddVideoActivity.this, "Error adding video", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}