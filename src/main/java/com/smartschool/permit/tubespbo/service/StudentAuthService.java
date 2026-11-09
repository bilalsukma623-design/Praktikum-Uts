package com.smartschool.permit.tubespbo.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smartschool.permit.tubespbo.app.UserSession;
import com.smartschool.permit.tubespbo.model.Student;
import com.smartschool.permit.tubespbo.repository.StudentRepository;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Service autentikasi khusus siswa.
 * Menggunakan Firebase Auth REST API untuk register & login,
 * lalu menyimpan/mengambil data siswa dari Firestore collection "students".
 */
public class StudentAuthService {
    private final StudentRepository studentRepo;
    private final String API_KEY = "AIzaSyB9L6LOrxcnDZov4xEH522MZEqOtmTXfmg";

    public StudentAuthService(StudentRepository studentRepo) {
        this.studentRepo = studentRepo;
    }

    /**
     * Register akun siswa baru via Firebase Auth, lalu simpan data ke Firestore.
     */
    public Student register(String email, String password, String fullName, String className) throws Exception {
        // 1. Register ke Firebase Auth
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

        int code = conn.getResponseCode();
        if (code != 200) {
            // Baca error message dari Firebase
            String errorBody;
            try (Scanner scanner = new Scanner(conn.getErrorStream(), StandardCharsets.UTF_8.name())) {
                errorBody = scanner.useDelimiter("\\A").next();
            }
            JsonObject errorJson = JsonParser.parseString(errorBody).getAsJsonObject();
            String errorMessage = errorJson.getAsJsonObject("error").get("message").getAsString();

            if (errorMessage.contains("EMAIL_EXISTS")) {
                throw new Exception("Email sudah terdaftar! Silakan login.");
            } else if (errorMessage.contains("WEAK_PASSWORD")) {
                throw new Exception("Password terlalu lemah! Minimal 6 karakter.");
            } else {
                throw new Exception("Registrasi gagal: " + errorMessage);
            }
        }

        String responseBody;
        try (Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name())) {
            responseBody = scanner.useDelimiter("\\A").next();
        }

        JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
        String uid = jsonResponse.get("localId").getAsString();

        // 2. Simpan data siswa ke Firestore (doc ID = UID)
        Student student = new Student();
        student.setId(uid);
        student.setEmail(email);
        student.setFullName(fullName);
        student.setClassName(className);
        student.setSchoolId("sch_001");
        student.setCreatedAt(System.currentTimeMillis());

        studentRepo.createWithId(uid, student);

        // 3. Auto-login
        UserSession.getInstance().loginAsStudent(student);

        return student;
    }

    /**
     * Login siswa via Firebase Auth, lalu ambil data dari Firestore.
     */
    public Student login(String email, String password) throws Exception {
        // 1. Login ke Firebase Auth
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
            throw new Exception("Email atau password salah!");
        }

        String responseBody;
        try (Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name())) {
            responseBody = scanner.useDelimiter("\\A").next();
        }

        JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
        String uid = jsonResponse.get("localId").getAsString();

        // 2. Ambil data siswa dari Firestore
        Student student = studentRepo.getByUid(uid);
        if (student == null) {
            throw new Exception("Akun ini bukan akun siswa! Gunakan login admin jika Anda admin.");
        }

        // 3. Login ke session
        UserSession.getInstance().loginAsStudent(student);

        return student;
    }
}
