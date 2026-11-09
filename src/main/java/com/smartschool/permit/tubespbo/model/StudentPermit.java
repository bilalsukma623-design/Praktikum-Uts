/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartschool.permit.tubespbo.model;
import com.google.cloud.Timestamp;
import com.smartschool.permit.tubespbo.model.enums.PermitStatus;
import com.smartschool.permit.tubespbo.model.enums.PermitType;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Doni354
 */
public class StudentPermit extends BaseModel implements Exportable, Filterable {
    private PermitType type;
    private String studentName;
    private String className;
    private String studentId;
    private String reason;
    private long timestamp;
    private Timestamp createdAt;
    private String schoolId;
    private String tahunAjaran;
    private PermitStatus status = PermitStatus.PENDING;
    private String approvedBy;
    private String approvedById;
    private long approvedAt;
    private boolean isSuperAdminApproved;
    private long arrivalTimestamp;
    private long exitTimestamp;
    private long returnTimestamp;

    public StudentPermit() {}

    public void approve(AdminUser admin) {
        this.status = PermitStatus.APPROVED;
        this.approvedBy = admin.getName();
        this.approvedById = admin.getId();
        this.approvedAt = System.currentTimeMillis();
        this.isSuperAdminApproved = admin.isSuperAdmin();
    }

    public boolean isPending() { return status == PermitStatus.PENDING; }
    public boolean isLateEntry() { return type == PermitType.LATE_ENTRY; }
    public boolean isExitPermit() { return type == PermitType.EXIT_PERMIT; }

    public int getDurationMinutes() {
        ZoneId zone = ZoneId.of("Asia/Jakarta");
        ZonedDateTime zdt = Instant.ofEpochMilli(timestamp).atZone(zone);
        
        if (isLateEntry()) {
            ZonedDateTime sevenAm = zdt.withHour(7).withMinute(0).withSecond(0).withNano(0);
            long diff = timestamp - sevenAm.toInstant().toEpochMilli();
            return (int) Math.max(0, diff / 60000);
        } else if (isExitPermit()) {
            if (returnTimestamp > 0) {
                return (int) ((returnTimestamp - timestamp) / 60000);
            } else {
                ZonedDateTime threePm = zdt.withHour(15).withMinute(0).withSecond(0).withNano(0);
                long diff = threePm.toInstant().toEpochMilli() - timestamp;
                return (int) Math.max(0, diff / 60000);
            }
        }
        return 0;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type != null ? type.name() : null);
        map.put("studentName", studentName);
        map.put("className", className);
        map.put("studentId", studentId);
        map.put("reason", reason);
        map.put("timestamp", timestamp);
        if (createdAt != null) map.put("createdAt", createdAt);
        map.put("schoolId", schoolId);
        map.put("tahunAjaran", tahunAjaran);
        if (status != null) map.put("status", status.name());

        // Field spesifik berdasarkan tipe (sesuai web app)
        if (isLateEntry()) {
            if (arrivalTimestamp > 0) map.put("arrivalTimestamp", arrivalTimestamp);
        } else if (isExitPermit()) {
            if (returnTimestamp > 0) map.put("returnTimestamp", returnTimestamp);
            if (exitTimestamp > 0) map.put("exitTimestamp", exitTimestamp);
        }

        // Field approval (hanya jika sudah di-approve)
        if (status == PermitStatus.APPROVED) {
            map.put("approvedBy", approvedBy);
            map.put("approvedById", approvedById);
            map.put("approvedAt", approvedAt);
            map.put("isSuperAdminApproved", isSuperAdminApproved);
        }
        
        return map;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null) return;
        if (map.get("type") != null) this.type = PermitType.valueOf((String) map.get("type"));
        this.studentName = (String) map.get("studentName");
        this.className = (String) map.get("className");
        this.studentId = (String) map.get("studentId");
        this.reason = (String) map.get("reason");
        if (map.get("timestamp") != null) this.timestamp = ((Number) map.get("timestamp")).longValue();
        if (map.get("createdAt") != null) this.createdAt = (Timestamp) map.get("createdAt");
        this.schoolId = (String) map.get("schoolId");
        this.tahunAjaran = (String) map.get("tahunAjaran");
        if (map.get("status") != null) this.status = PermitStatus.valueOf((String) map.get("status"));
        else this.status = null; // Explicit null if missing in Firestore (old data)

        this.approvedBy = (String) map.get("approvedBy");
        this.approvedById = (String) map.get("approvedById");
        if (map.get("approvedAt") != null) this.approvedAt = ((Number) map.get("approvedAt")).longValue();
        if (map.get("isSuperAdminApproved") != null) this.isSuperAdminApproved = (boolean) map.get("isSuperAdminApproved");
        if (map.get("arrivalTimestamp") != null) this.arrivalTimestamp = ((Number) map.get("arrivalTimestamp")).longValue();
        if (map.get("exitTimestamp") != null) this.exitTimestamp = ((Number) map.get("exitTimestamp")).longValue();
        if (map.get("returnTimestamp") != null) this.returnTimestamp = ((Number) map.get("returnTimestamp")).longValue();
    }

    @Override
    public Map<String, Object> toExportRow() {
        Map<String, Object> row = new HashMap<>();
        row.put("Nama", studentName);
        row.put("Kelas", className);
        row.put("Tipe", type != null ? type.name() : "-");
        row.put("Status", status != null ? status.name() : "-");
        row.put("Durasi (Menit)", getDurationMinutes());
        return row;
    }

    @Override
    public boolean matchesFilter(String keyword) {
        if (keyword == null || keyword.isEmpty()) return true;
        String kw = keyword.toLowerCase();
        return (studentName != null && studentName.toLowerCase().contains(kw)) ||
               (className != null && className.toLowerCase().contains(kw));
    }

    // getter setterssss
    public PermitType getType() { return type; }
    public void setType(PermitType type) { this.type = type; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public String getSchoolId() { return schoolId; }
    public void setSchoolId(String schoolId) { this.schoolId = schoolId; }
    public String getTahunAjaran() { return tahunAjaran; }
    public void setTahunAjaran(String tahunAjaran) { this.tahunAjaran = tahunAjaran; }
    public PermitStatus getStatus() { return status; }
    public void setStatus(PermitStatus status) { this.status = status; }
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    public String getApprovedById() { return approvedById; }
    public void setApprovedById(String approvedById) { this.approvedById = approvedById; }
    public long getApprovedAt() { return approvedAt; }
    public void setApprovedAt(long approvedAt) { this.approvedAt = approvedAt; }
    public boolean isSuperAdminApproved() { return isSuperAdminApproved; }
    public void setSuperAdminApproved(boolean superAdminApproved) { this.isSuperAdminApproved = superAdminApproved; }
    public long getArrivalTimestamp() { return arrivalTimestamp; }
    public void setArrivalTimestamp(long arrivalTimestamp) { this.arrivalTimestamp = arrivalTimestamp; }
    public long getExitTimestamp() { return exitTimestamp; }
    public void setExitTimestamp(long exitTimestamp) { this.exitTimestamp = exitTimestamp; }
    public long getReturnTimestamp() { return returnTimestamp; }
    public void setReturnTimestamp(long returnTimestamp) { this.returnTimestamp = returnTimestamp; }
}