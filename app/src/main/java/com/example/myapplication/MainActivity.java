package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private TextView tvEmail, tvLikeCount;
    private Button btnLike, btnDislike, btnShare, btnUpload, btnProfile;
    private AuthManager auth;
    private int likeCount = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = AuthManager.getInstance();
        User currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        webView = findViewById(R.id.webView);
        tvEmail = findViewById(R.id.tvEmail);
        tvLikeCount = findViewById(R.id.tvLikeCount);
        btnLike = findViewById(R.id.btnLike);
        btnDislike = findViewById(R.id.btnDislike);
        btnShare = findViewById(R.id.btnShare);
        btnUpload = findViewById(R.id.btnUpload);
        btnProfile = findViewById(R.id.btnProfile);

        tvEmail.setText(currentUser.getEmail());

        setupWebView();
        setupButtons();
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        
        webView.setWebViewClient(new WebViewClient());

        webView.loadUrl("https://www.google.com"); 
    }

    private void setupButtons() {
        btnLike.setOnClickListener(v -> {
            likeCount++;
            tvLikeCount.setText(String.valueOf(likeCount));
        });

        btnDislike.setOnClickListener(v -> {
            if (likeCount > 0) likeCount--;
            tvLikeCount.setText(String.valueOf(likeCount));
        });

        btnShare.setOnClickListener(v -> {
            Toast.makeText(this, "Shared!", Toast.LENGTH_SHORT).show();
        });

        btnUpload.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, UploadActivity.class));
        });

        btnProfile.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });
    }
}