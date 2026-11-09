/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartschool.permit.tubespbo.repository;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.smartschool.permit.tubespbo.model.ExitPermit;
import com.smartschool.permit.tubespbo.model.LateEntryPermit;
import com.smartschool.permit.tubespbo.model.StudentPermit;
import com.smartschool.permit.tubespbo.model.enums.PermitType;
import com.smartschool.permit.tubespbo.repository.interfaces.IPermitRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Repository data izin siswa ke koleksi Firestore "permits".
 * Menerapkan OCP, ISP, dan DIP:
 * Mengimplementasikan IPermitRepository (PermitReader & PermitWriter),
 * serta toEntity memetakan dokumen ke subclass polimorfis.
 */
public class PermitRepository extends BaseRepository<StudentPermit> implements IPermitRepository {

    public PermitRepository() {
        super("permits");
    }

    public PermitRepository(Firestore db) {
        super(db, "permits");
    }

    @Override
    protected StudentPermit toEntity(DocumentSnapshot doc) {
        Map<String, Object> data = doc.getData();
        StudentPermit permit;
        String typeStr = data != null ? (String) data.get("type") : null;
        if ("LATE_ENTRY".equals(typeStr)) {
            permit = new LateEntryPermit();
        } else if ("EXIT_PERMIT".equals(typeStr)) {
            permit = new ExitPermit();
        } else {
            permit = new StudentPermit();
        }
        permit.fromMap(data);
        permit.setId(doc.getId());
        return permit;
    }

    @Override
    public List<StudentPermit> getBySchool(String schoolId) {
        List<StudentPermit> list = new ArrayList<>();
        try {
            QuerySnapshot query = db.collection(collectionName)
                    .whereEqualTo("schoolId", schoolId)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get().get();
            for (QueryDocumentSnapshot doc : query.getDocuments()) {
                list.add(toEntity(doc));
            }
        } catch (Exception e) {
            System.err.println("Error getBySchool (Permit): " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<StudentPermit> getByType(String schoolId, PermitType type) {
        List<StudentPermit> list = new ArrayList<>();
        try {
            QuerySnapshot query = db.collection(collectionName)
                    .whereEqualTo("schoolId", schoolId)
                    .whereEqualTo("type", type != null ? type.name() : null)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get().get();
            for (QueryDocumentSnapshot doc : query.getDocuments()) {
                list.add(toEntity(doc));
            }
        } catch (Exception e) {
            System.err.println("Error getByType (Permit): " + e.getMessage());
        }
        return list;
    }

    @Override
    public void approvePermit(String permitId, Map<String, Object> approvalData) {
        update(permitId, approvalData);
    }
}
