package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Locale;

public class UploadActivity extends AppCompatActivity {

    private VideoView videoView;
    private Button btnSelectVideo, btnUploadVideo;
    private ProgressBar progressBar;
    private Uri videoUri;

    private final ActivityResultLauncher<Intent> videoPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    videoUri = result.getData().getData();
                    videoView.setVideoURI(videoUri);
                    videoView.start();
                    btnUploadVideo.setEnabled(true);
                }
            }
    );

    private AzureUploader azureUploader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // Initialize uploader with SAS URL from BuildConfig (injected via app/build.gradle.kts reading local.properties)
        String sasUrl = BuildConfig.AZURE_SAS_URL;
        azureUploader = new AzureUploader(this, sasUrl);

        videoView = findViewById(R.id.videoView);
        btnSelectVideo = findViewById(R.id.btnSelectVideo);
        btnUploadVideo = findViewById(R.id.btnUploadVideo);
        progressBar = findViewById(R.id.progressBar);

        btnUploadVideo.setEnabled(false);

        btnSelectVideo.setOnClickListener(v -> selectVideo());
        btnUploadVideo.setOnClickListener(v -> uploadVideo());
    }

    private void selectVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        videoPickerLauncher.launch(Intent.createChooser(intent, "Select Video"));
    }

    private void uploadVideo() {
        if (videoUri != null) {
            progressBar.setVisibility(View.VISIBLE);
            btnUploadVideo.setEnabled(false);
            btnSelectVideo.setEnabled(false);

            // Create a blob name using timestamp
            String blobName = String.format(Locale.US, "%d.mp4", System.currentTimeMillis());

            // Perform upload on background thread
            new Thread(() -> {
                try {
                    String response = azureUploader.uploadBlob(blobName, videoUri);
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(UploadActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                        btnUploadVideo.setEnabled(true);
                        btnSelectVideo.setEnabled(true);
                        finish();
                    });
                } catch (IOException e) {
                    String msg = "Upload failed: " + e.getMessage();
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(UploadActivity.this, msg, Toast.LENGTH_LONG).show();
                        btnUploadVideo.setEnabled(true);
                        btnSelectVideo.setEnabled(true);
                    });
                }
            }).start();

        } else {
            Toast.makeText(this, "No video selected", Toast.LENGTH_SHORT).show();
        }
    }
}