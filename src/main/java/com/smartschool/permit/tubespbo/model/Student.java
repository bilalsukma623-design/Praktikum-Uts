package com.smartschool.permit.tubespbo.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Model Student untuk collection "students" di Firestore.
 * Menyimpan data akun siswa yang terdaftar.
 */
public class Student extends BaseModel {
    private String email;
    private String fullName;
    private String className;
    private String schoolId;
    private long createdAt;

    public Student() {}

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("email", email);
        map.put("fullName", fullName);
        map.put("className", className);
        map.put("schoolId", schoolId);
        map.put("createdAt", createdAt);
        return map;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null) return;
        this.email = (String) map.get("email");
        this.fullName = (String) map.get("fullName");
        this.className = (String) map.get("className");
        this.schoolId = (String) map.get("schoolId");
        if (map.get("createdAt") != null) {
            this.createdAt = ((Number) map.get("createdAt")).longValue();
        }
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getSchoolId() { return schoolId; }
    public void setSchoolId(String schoolId) { this.schoolId = schoolId; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
