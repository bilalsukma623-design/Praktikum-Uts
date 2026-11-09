/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartschool.permit.tubespbo.app;
import com.smartschool.permit.tubespbo.model.AdminUser;
import com.smartschool.permit.tubespbo.model.StudentPermit;
import com.smartschool.permit.tubespbo.model.enums.PermitStatus;
import com.smartschool.permit.tubespbo.model.enums.PermitType;
import com.smartschool.permit.tubespbo.model.enums.UserRole;
import com.smartschool.permit.tubespbo.repository.PermitRepository;
import com.smartschool.permit.tubespbo.service.PermitService;
import com.smartschool.permit.tubespbo.util.DateUtils;

import java.util.List;
/**
 *
 * @author Doni354
 */
public class TestCRUD {
    public static void main(String[] args) {
        System.out.println("=== MEMULAI INTEGRASI TESTING FIRESTORE ===");
        
        // 1. Inisialisasi Layer Repository & Service
        PermitRepository permitRepo = new PermitRepository();
        PermitService permitService = new PermitService(permitRepo);
        
        // Buat session bohongan dulu buat isi School ID (karena belum login UI)
        AdminUser dummyAdmin = new AdminUser();
        dummyAdmin.setId("admin_piket_01");
        dummyAdmin.setName("Pak Doni Piket");
        dummyAdmin.setRole(UserRole.ADMIN_PIKET);
        dummyAdmin.setSchoolId("sch_001");
        UserSession.getInstance().login(dummyAdmin);

        try {
            // -------------------------------------------------------
            // TEST 1: CREATE (Kirim Data Izin Masuk Terlambat)
            // -------------------------------------------------------
            System.out.println("\n[1] Menjalankan Test CREATE...");
            StudentPermit permitBaru = new StudentPermit();
            permitBaru.setStudentName("Budi Santoso");
            permitBaru.setClassName("XI-A");
            permitBaru.setReason("Ban motor bocor di pasar");
            permitBaru.setType(PermitType.LATE_ENTRY);
            
            // Eksekusi Simpan lewat Service
            String newId = permitService.createPermit(permitBaru);
            System.out.println(">> BERHASIL! Data tersimpan di Firestore dengan ID: " + newId);

            // -------------------------------------------------------
            // TEST 2: READ ALL (Ambil Semua Data dari Firestore)
            // -------------------------------------------------------
            System.out.println("\n[2] Menjalankan Test READ...");
            List<StudentPermit> listPermit = permitService.getAllPermits("sch_001");
            System.out.println(">> Total data izin di Firestore saat ini: " + listPermit.size());
            for (StudentPermit p : listPermit) {
                System.out.println("- " + p.getStudentName() + " (" + p.getClassName() + ") | Status: " + p.getStatus() + " | Waktu: " + DateUtils.formatDateTime(p.getTimestamp()));
            }

            // -------------------------------------------------------
            // TEST 3: UPDATE / APPROVE (Setujui Izin si Budi)
            // -------------------------------------------------------
            System.out.println("\n[3] Menjalankan Test UPDATE (Approve)...");
            if (newId != null) {
                permitService.approvePermit(newId, dummyAdmin);
                System.out.println(">> BERHASIL! Izin dengan ID " + newId + " telah di-APPROVE oleh " + dummyAdmin.getName());
                
                // Cek status terbarunya langsung dari DB
                StudentPermit updatedData = permitRepo.getById(newId);
                System.out.println(">> Status terbaru di DB: " + updatedData.getStatus());
            }

            // -------------------------------------------------------
            // TEST 4: DELETE (Hapus Data Tersebut Biar Ga Nyampah di DB)
            // -------------------------------------------------------
            System.out.println("\n[4] Menjalankan Test DELETE...");
            if (newId != null) {
                permitService.deletePermit(newId);
                System.out.println(">> BERHASIL! Data dengan ID " + newId + " telah dihapus dari Firestore.");
            }

        } catch (Exception e) {
            System.err.println("Waduh error pas testing: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n=== TESTING SELESAI DENGAN SUKSES! ===");
    }
}