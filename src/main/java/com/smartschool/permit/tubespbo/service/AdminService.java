/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartschool.permit.tubespbo.service;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smartschool.permit.tubespbo.app.FirestoreConnection;
import com.smartschool.permit.tubespbo.app.UserSession;
import com.smartschool.permit.tubespbo.model.AdminUser;
import com.smartschool.permit.tubespbo.model.enums.UserRole;
import com.smartschool.permit.tubespbo.repository.AdminRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
/**
 *
 * @author Doni354
 */
public class AdminService {
    private final AdminRepository adminRepo;
    private final String API_KEY = "AIzaSyB9L6LOrxcnDZov4xEH522MZEqOtmTXfmg";

    public AdminService(AdminRepository adminRepo) {
        this.adminRepo = adminRepo;
    }

    public List<AdminUser> getAllAdmins(String schoolId) {
        return adminRepo.getBySchool(schoolId);
    }

    public AdminUser createAdmin(String email, String password, String name, String schoolId) throws Exception {
        String urlStr = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + API_KEY;
        
        URL url = java.net.URI.create(urlStr).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setDoOutput(true);

        JsonObject jsonPayload = new JsonObject();
        jsonPayload.addProperty("email", email);
        jsonPayload.addProperty("password", password);
        jsonPayload.addProperty("returnSecureToken", true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonPayload.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        if (conn.getResponseCode() != 200) {
            throw new Exception("Gagal mendaftarkan akun auth baru (Email mungkin udah dipake)!");
        }

        String responseBody;
        try (Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name())) {
            responseBody = scanner.useDelimiter("\\A").next();
        }

        JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
        String uid = jsonResponse.get("localId").getAsString();

        AdminUser newAdmin = new AdminUser();
        newAdmin.setId(uid);
        newAdmin.setEmail(email);
        newAdmin.setName(name);
        newAdmin.setRole(UserRole.ADMIN_PIKET);
        newAdmin.setSchoolId(schoolId);
        newAdmin.setCreatedAt(System.currentTimeMillis());
        
        String currentUid = UserSession.getInstance().getCurrentUser() != null ? 
                UserSession.getInstance().getCurrentUser().getId() : "SYSTEM";
        newAdmin.setCreatedBy(currentUid);


        FirestoreConnection.getInstance().getDb()
                .collection("admins")
                .document(uid)
                .set(newAdmin.toMap()).get();

        return newAdmin;
    }

    public void deleteAdmin(String uid) {
        adminRepo.delete(uid);
    }

    public void changePassword(String uid, String newPassword) throws Exception {
        try {
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid)
                    .setPassword(newPassword);
            FirebaseAuth.getInstance().updateUser(request);
        } catch (Exception e) {
            throw new Exception("Gagal mereset password akun: " + e.getMessage());
        }
    }
}
