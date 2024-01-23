package com.example.qunlnhns.firebase;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseHelper {
    private FirebaseFirestore db;

    public FirebaseHelper() {
        // Khởi tạo Firestore trong constructor
        db = FirebaseFirestore.getInstance();
    }
}
