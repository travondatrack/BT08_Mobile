package com.example.myapplication;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;

public class InputStreamRequestBody extends RequestBody {
    private final InputStream inputStream;
    private final MediaType contentType;

    public InputStreamRequestBody(InputStream inputStream, MediaType contentType) {
        this.inputStream = inputStream;
        this.contentType = contentType;
    }

    @Override
    public MediaType contentType() {
        return contentType;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        try (InputStream is = inputStream) {
            sink.writeAll(Okio.source(is));
        }
    }
}

