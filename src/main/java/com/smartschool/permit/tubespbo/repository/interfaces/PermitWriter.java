package com.smartschool.permit.tubespbo.repository.interfaces;

import com.smartschool.permit.tubespbo.model.StudentPermit;
import com.smartschool.permit.tubespbo.repository.CrudRepository;

import java.util.Map;

/**
 * Role Interface khusus operasi mutasi dan manipulasi data izin (Write/Update/Delete).
 * Menerapkan Interface Segregation Principle (ISP):
 * Memisahkan tanggung jawab mutasi dari pembacaan data.
 */
public interface PermitWriter extends CrudRepository<StudentPermit> {

    /**
     * Menyetujui izin dengan data approval.
     *
     * @param permitId ID dokumen izin
     * @param approvalData Data persetujuan
     */
    void approvePermit(String permitId, Map<String, Object> approvalData);
}
