/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartschool.permit.tubespbo.service;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smartschool.permit.tubespbo.app.UserSession;
import com.smartschool.permit.tubespbo.model.AdminUser;
import com.smartschool.permit.tubespbo.model.Student;
import com.smartschool.permit.tubespbo.repository.AdminRepository;
import com.smartschool.permit.tubespbo.repository.StudentRepository;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
/**
 *
 * @author Doni354
 */
public class AuthService {
    private final AdminRepository adminRepo;
    private final StudentRepository studentRepo;
    private final String API_KEY = "AIzaSyB9L6LOrxcnDZov4xEH522MZEqOtmTXfmg";

    public AuthService() {
        this.adminRepo = new AdminRepository();
        this.studentRepo = new StudentRepository();
    }

    // Deprecated constructor for backward compatibility
    public AuthService(AdminRepository adminRepo) {
        this.adminRepo = adminRepo;
        this.studentRepo = new StudentRepository();
    }

    /**
     * Login yang menghandle Admin dan Siswa.
     * Mengembalikan AdminUser atau Student.
     */
    public Object login(String email, String password) throws Exception {
        String urlStr = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + API_KEY;
        
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

        int code = conn.getResponseCode();
        if (code != 200) {
            throw new Exception("Email atau password salah, cuy!");
        }
        
        String responseBody;
        try (Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name())) {
            responseBody = scanner.useDelimiter("\\A").next();
        }

        JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
        String uid = jsonResponse.get("localId").getAsString();

        // 1. Cek apakah Admin
        AdminUser adminUser = adminRepo.getByUid(uid);
        if (adminUser != null) {
            UserSession.getInstance().login(adminUser);
            return adminUser;
        }

        // 2. Cek apakah Siswa
        Student student = studentRepo.getByUid(uid);
        if (student != null) {
            UserSession.getInstance().loginAsStudent(student);
            return student;
        }

        throw new Exception("Akun terdaftar, tapi data profil tidak ditemukan (hubungi admin)!");
    }

    public void logout() {
        UserSession.getInstance().logout();
    }
}
