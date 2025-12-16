package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AzureUploader {
    private static final String TAG = "AzureUploader";
    private final OkHttpClient client = new OkHttpClient();
    private final Context context;
    private final String sasUrl; // container SAS URL, may include query

    public AzureUploader(Context context, String sasUrl) {
        this.context = context;
        this.sasUrl = sasUrl;
    }

    // Uploads a file stream to a specific blob path using PUT. Returns response message or throws IOException.
    public String uploadBlob(String blobName, Uri fileUri) throws IOException {
        // If sasUrl contains a query (SAS token), we need to insert the blob name before the query or append appropriately.
        String url;
        int q = sasUrl.indexOf('?');
        if (q >= 0) {
            // sasUrl like https://account.blob.core.windows.net/container?sv=... -> insert /blobName before ?
            String before = sasUrl.substring(0, q);
            String after = sasUrl.substring(q); // includes ?
            if (!before.endsWith("/")) before += "/";
            url = before + blobName + after;
        } else {
            url = sasUrl;
            if (!url.endsWith("/")) url += "/";
            url += blobName;
        }

        // Read input stream
        InputStream is = context.getContentResolver().openInputStream(fileUri);
        if (is == null) throw new IOException("Unable to open input stream for URI: " + fileUri);

        RequestBody body = new InputStreamRequestBody(is, MediaType.parse("video/mp4"));

        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .addHeader("x-ms-blob-type", "BlockBlob")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String msg = "Upload failed: " + response.code() + " " + response.message();
                Log.e(TAG, msg);
                throw new IOException(msg + " - " + (response.body() != null ? response.body().string() : ""));
            }
            return response.body() != null ? response.body().string() : "OK";
        }
    }
}
