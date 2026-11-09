/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartschool.permit.tubespbo.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.smartschool.permit.tubespbo.app.FirestoreConnection;
import com.smartschool.permit.tubespbo.model.BaseModel;

/**
 * Kelas dasar repositori untuk akses koleksi Cloud Firestore.
 * Menerapkan Liskov Substitution Principle (LSP):
 * Kontrak method create(T entity) diselaraskan agar mendukung entitas
 * yang memiliki ID eksplisit (misal Firebase Auth UID) maupun entitas
 * dengan ID acak yang di-generate otomatis.
 *
 * @author Doni354
 */
public abstract class BaseRepository<T extends BaseModel> implements CrudRepository<T> {
    protected final String collectionName;
    protected final Firestore db;

    public BaseRepository(String collectionName) {
        this(FirestoreConnection.getInstance().getDb(), collectionName);
    }

    public BaseRepository(Firestore db, String collectionName) {
        this.collectionName = collectionName;
        this.db = db;
    }

    protected abstract T toEntity(DocumentSnapshot doc);

    @Override
    public List<T> getAll() {
        List<T> list = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = db.collection(collectionName).get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                list.add(toEntity(document));
            }
        } catch (Exception e) {
            System.err.println("Error getting all documents: " + e.getMessage());
        }
        return list;
    }

    @Override
    public T getById(String id) {
        try {
            DocumentSnapshot document = db.collection(collectionName).document(id).get().get();
            if (document.exists()) {
                return toEntity(document);
            }
        } catch (Exception e) {
            System.err.println("Error getting document by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String create(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entitas yang akan disimpan tidak boleh null!");
        }
        try {
            // Harmonisasi LSP: jika entitas sudah memiliki ID eksplisit (seperti UID Auth), gunakan ID tersebut
            if (entity.getId() != null && !entity.getId().trim().isEmpty()) {
                String explicitId = entity.getId();
                db.collection(collectionName).document(explicitId).set(entity.toMap()).get();
                return explicitId;
            } else {
                // Jika belum ber-ID, gunakan auto-generated ID dari Firestore
                ApiFuture<DocumentReference> future = db.collection(collectionName).add(entity.toMap());
                String autoId = future.get().getId();
                entity.setId(autoId);
                return autoId;
            }
        } catch (Exception e) {
            System.err.println("Error creating document in " + collectionName + ": " + e.getMessage());
            return null;
        }
    }

    @Override
    public void update(String id, Map<String, Object> data) {
        db.collection(collectionName).document(id).update(data);
    }

    @Override
    public void delete(String id) {
        db.collection(collectionName).document(id).delete();
    }
}
