package com.smartschool.permit.tubespbo.repository;

import com.google.cloud.firestore.DocumentSnapshot;
import com.smartschool.permit.tubespbo.model.Student;

/**
 * Repository untuk collection "students" di Firestore.
 * Menerapkan Liskov Substitution Principle (LSP):
 * create(Student) kini dapat menggantikan kontrak superclass secara aman,
 * baik dipanggil langsung dengan ID yang sudah terisi maupun via createWithId.
 */
public class StudentRepository extends BaseRepository<Student> {

    public StudentRepository() {
        super("students");
    }

    @Override
    protected Student toEntity(DocumentSnapshot doc) {
        Student student = new Student();
        student.fromMap(doc.getData());
        student.setId(doc.getId());
        return student;
    }

    /**
     * Get student by Firebase Auth UID (doc ID = UID).
     */
    public Student getByUid(String uid) {
        return getById(uid);
    }

    /**
     * Menyimpan data siswa ke Firestore dengan ID eksplisit (Firebase Auth UID).
     * Terintegrasi dengan kontrak superclass create(Student) yang telah selaras LSP.
     */
    public void createWithId(String uid, Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Data siswa tidak boleh null!");
        }
        student.setId(uid);
        String createdId = create(student);
        if (createdId == null) {
            throw new RuntimeException("Gagal menyimpan data siswa ke database!");
        }
    }
}
