/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartschool.permit.tubespbo.repository;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.smartschool.permit.tubespbo.model.AdminUser;

import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Doni354
 */
public class AdminRepository extends BaseRepository<AdminUser> {

    public AdminRepository() {
        super("admins");
    }

    @Override
    protected AdminUser toEntity(DocumentSnapshot doc) {
        AdminUser admin = new AdminUser();
        admin.fromMap(doc.getData());
        admin.setId(doc.getId());
        return admin;
    }

    public List<AdminUser> getBySchool(String schoolId) {
        List<AdminUser> list = new ArrayList<>();
        try {
            QuerySnapshot query = db.collection(collectionName)
                    .whereEqualTo("schoolId", schoolId)
                    .get().get();
            for (QueryDocumentSnapshot doc : query.getDocuments()) {
                list.add(toEntity(doc));
            }
        } catch (Exception e) {
            System.err.println("Error getBySchool (Admin): " + e.getMessage());
        }
        return list;
    }

    public AdminUser getByUid(String uid) {
        return getById(uid);
    }
}