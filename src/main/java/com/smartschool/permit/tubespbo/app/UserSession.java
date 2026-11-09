/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartschool.permit.tubespbo.app;
import com.smartschool.permit.tubespbo.model.AdminUser;
import com.smartschool.permit.tubespbo.model.Student;
/**
 *
 * @author Doni354
 */
public class UserSession {
    private static UserSession instance;
    private AdminUser currentUser;
    private Student currentStudent;

    private UserSession() {}

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    // === Admin Session ===
    public void login(AdminUser user) {
        this.currentUser = user;
        this.currentStudent = null; // pastikan hanya 1 role aktif
    }

    public AdminUser getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isSuperAdmin() {
        return currentUser != null && currentUser.isSuperAdmin();
    }

    // === Student Session ===
    public void loginAsStudent(Student student) {
        this.currentStudent = student;
        this.currentUser = null; // pastikan hanya 1 role aktif
    }

    public Student getCurrentStudent() {
        return currentStudent;
    }

    public boolean isStudent() {
        return currentStudent != null;
    }

    // === Shared ===
    public void logout() {
        this.currentUser = null;
        this.currentStudent = null;
    }

    public String getSchoolId() {
        if (currentUser != null) return currentUser.getSchoolId();
        if (currentStudent != null) return currentStudent.getSchoolId();
        return null;
    }
}
