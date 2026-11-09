package com.smartschool.permit.tubespbo.repository.interfaces;

import com.smartschool.permit.tubespbo.model.StudentPermit;
import com.smartschool.permit.tubespbo.model.enums.PermitType;

import java.util.List;

/**
 * Role Interface khusus operasi pembacaan data izin (Read-Only).
 * Menerapkan Interface Segregation Principle (ISP):
 * Klien pembaca laporan (seperti ReportService) hanya bergantung pada method baca,
 * tanpa terekspos pada operasi mutasi (create, update, delete).
 */
public interface PermitReader {

    /**
     * Mengambil seluruh data izin untuk sekolah tertentu.
     *
     * @param schoolId ID sekolah
     * @return Daftar StudentPermit
     */
    List<StudentPermit> getBySchool(String schoolId);

    /**
     * Mengambil daftar izin berdasarkan tipe tertentu (LATE_ENTRY / EXIT_PERMIT).
     *
     * @param schoolId ID sekolah
     * @param type Tipe izin
     * @return Daftar StudentPermit yang sesuai tipe
     */
    List<StudentPermit> getByType(String schoolId, PermitType type);

    /**
     * Mengambil satu data izin berdasarkan ID dokumen.
     *
     * @param id ID dokumen
     * @return Entitas StudentPermit atau null jika tidak ditemukan
     */
    StudentPermit getById(String id);
}

