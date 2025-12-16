package com.example.myapplication;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.Map;

public class AuthManager {
    public interface Callback {
        void onComplete(boolean success, String message);
    }

    private static AuthManager instance;
    private final Map<String, String> users = new HashMap<>(); // email -> password
    private User currentUser;

    private AuthManager() {}

    public static synchronized AuthManager getInstance() {
        if (instance == null) instance = new AuthManager();
        return instance;
    }

    // Simulate async sign-in
    public void signInWithEmailAndPassword(String email, String password, Callback callback) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            String pw = users.get(email);
            if (pw != null && pw.equals(password)) {
                currentUser = new User(email);
                callback.onComplete(true, "Signed in");
            } else {
                callback.onComplete(false, "Invalid credentials");
            }
        }, 500);
    }

    // Simulate async registration
    public void createUserWithEmailAndPassword(String email, String password, Callback callback) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (users.containsKey(email)) {
                callback.onComplete(false, "User already exists");
            } else {
                users.put(email, password);
                currentUser = new User(email);
                callback.onComplete(true, "User created");
            }
        }, 500);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void signOut() {
        currentUser = null;
    }
}

