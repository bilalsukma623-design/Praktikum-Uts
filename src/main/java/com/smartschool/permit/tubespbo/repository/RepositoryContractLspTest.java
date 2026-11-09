/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartschool.permit.tubespbo.repository;

import com.smartschool.permit.tubespbo.model.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pengujian Unit: Kontrak Persistensi Entitas (LSP)")
public class RepositoryContractLspTest {

    /**
     * In-memory implementation of CrudRepository<Student> to verify LSP compliance
     * under polymorphic reference.
     */
    static class InMemoryStudentRepository implements CrudRepository<Student> {
        final Map<String, Student> db = new HashMap<>();

        @Override
        public java.util.List<Student> getAll() {
            return new java.util.ArrayList<>(db.values());
        }

        @Override
        public Student getById(String id) {
            return db.get(id);
        }

        @Override
        public String create(Student student) {
            if (student == null) throw new IllegalArgumentException("Student null");
            // Harmonisasi LSP: hormati explicit ID jika ada
            String id = (student.getId() != null && !student.getId().isEmpty())
                    ? student.getId()
                    : "AUTO_" + java.util.UUID.randomUUID();
            student.setId(id);
            db.put(id, student);
            return id;
        }

        @Override
        public void update(String id, Map<String, Object> data) {
            // Not needed for this test
        }

        @Override
        public void delete(String id) {
            db.remove(id);
        }
    }

    @Test
    @DisplayName("Substitusi Liskov: Pemanggilan polymorphic create(student) harus menjaga explicit ID")
    public void testLspPolymorphicContract() {
        // Objek subclass dapat disubstitusikan ke superclass / interface CrudRepository<Student>
        CrudRepository<Student> repo = new InMemoryStudentRepository();

        Student student = new Student();
        student.setId("FIREBASE_AUTH_UID_999");
        student.setEmail("siswa@sekolah.sch.id");
        student.setFullName("Ahmad Siswa");
        student.setClassName("X-A");

        // Pemanggilan method create standar (kontrak parent)
        String savedId = repo.create(student);

        // Nilai ID yang tersimpan harus identik dengan Auth UID, bukan auto-ID acak
        assertEquals("FIREBASE_AUTH_UID_999", savedId);

        Student fetched = repo.getById("FIREBASE_AUTH_UID_999");
        assertNotNull(fetched, "Data harus dapat ditemukan menggunakan Auth UID");
        assertEquals("Ahmad Siswa", fetched.getFullName());
    }
}