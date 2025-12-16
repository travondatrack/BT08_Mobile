package com.example.myapplication;

public final class SecureConfig {
    private SecureConfig() {}

    public static String getAzureSasUrl() {
        return BuildConfig.AZURE_SAS_URL != null ? BuildConfig.AZURE_SAS_URL : "";
    }
}

