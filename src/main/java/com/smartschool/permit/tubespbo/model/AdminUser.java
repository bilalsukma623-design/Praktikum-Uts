/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartschool.permit.tubespbo.model;
import com.smartschool.permit.tubespbo.model.enums.UserRole;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author Doni354
 */
public class AdminUser extends BaseModel implements Filterable {
    private String email;
    private String name;
    private UserRole role;
    private String schoolId;
    private long createdAt;
    private String createdBy;

    // Default constructor for Firestore
    public AdminUser() {}

    public boolean isSuperAdmin() {
        return role != null && role.isSuperAdmin();
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("email", email);
        map.put("name", name);
        map.put("role", role != null ? role.name() : null); // smpn enum sbg String
        map.put("schoolId", schoolId);
        map.put("createdAt", createdAt);
        map.put("createdBy", createdBy);
        return map;
    }

    @Override
    public void fromMap(Map<String, Object> map) {
        if (map == null) return;
        this.email = (String) map.get("email");
        this.name = (String) map.get("name");
        
        if (map.get("role") != null) {
            this.role = UserRole.valueOf((String) map.get("role"));
        }
        
        this.schoolId = (String) map.get("schoolId");
        
        // konversi angka 
        if (map.get("createdAt") != null) {
            this.createdAt = ((Number) map.get("createdAt")).longValue();
        }
        this.createdBy = (String) map.get("createdBy");
    }

    @Override
    public boolean matchesFilter(String keyword) {
        if (keyword == null || keyword.isEmpty()) return true;
        String lowerKw = keyword.toLowerCase();
        return (name != null && name.toLowerCase().contains(lowerKw)) ||
               (email != null && email.toLowerCase().contains(lowerKw));
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    public String getSchoolId() { return schoolId; }
    public void setSchoolId(String schoolId) { this.schoolId = schoolId; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
