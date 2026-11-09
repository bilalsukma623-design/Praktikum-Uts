/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartschool.permit.tubespbo.app;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.InputStream;
/**
 *
 * @author Doni354
 */
public class FirestoreConnection {
    private static FirestoreConnection instance;
    private Firestore db;

    private FirestoreConnection() {
        try {
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");
            
            if (serviceAccount == null) {
                throw new RuntimeException("File serviceAccountKey.json tidak ditemukan di folder resources!");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setProjectId("smartschool-34158")
                .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase berhasil diinisialisasi!");
            }

            this.db = FirestoreClient.getFirestore();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Gagal menghubungkan ke Firebase.");
        }
    }

    public static synchronized FirestoreConnection getInstance() {
        if (instance == null) {
            instance = new FirestoreConnection();
        }
        return instance;
    }

    public Firestore getDb() {
        return db;
    }
}
