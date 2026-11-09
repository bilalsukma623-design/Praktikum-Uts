package com.smartschool.permit.tubespbo.service;

import com.smartschool.permit.tubespbo.app.AppServices;
import com.smartschool.permit.tubespbo.model.AdminUser;
import com.smartschool.permit.tubespbo.model.LateEntryPermit;
import com.smartschool.permit.tubespbo.model.StudentPermit;
import com.smartschool.permit.tubespbo.model.enums.PermitStatus;
import com.smartschool.permit.tubespbo.model.enums.PermitType;
import com.smartschool.permit.tubespbo.model.enums.UserRole;
import com.smartschool.permit.tubespbo.repository.interfaces.IPermitRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pengujian Unit: Inversi Ketergantungan PermitService (DIP)")
public class PermitServiceDipTest {

    /**
     * Mock repositori murni in-memory yang mengimplementasikan IPermitRepository.
     * Tidak membutuhkan koneksi internet atau emulator Firebase Firestore.
     */
    static class MockPermitRepository implements IPermitRepository {
        final Map<String, StudentPermit> store = new HashMap<>();

        @Override
        public List<StudentPermit> getBySchool(String schoolId) {
            List<StudentPermit> list = new ArrayList<>();
            for (StudentPermit p : store.values()) {
                if (schoolId.equals(p.getSchoolId())) list.add(p);
            }
            return list;
        }

        @Override
        public List<StudentPermit> getByType(String schoolId, PermitType type) {
            List<StudentPermit> list = new ArrayList<>();
            for (StudentPermit p : store.values()) {
                if (schoolId.equals(p.getSchoolId()) && type == p.getType()) list.add(p);
            }
            return list;
        }

        @Override
        public StudentPermit getById(String id) {
            return store.get(id);
        }

        @Override
        public List<StudentPermit> getAll() {
            return new ArrayList<>(store.values());
        }

        @Override
        public String create(StudentPermit permit) {
            String id = "MOCK_" + UUID.randomUUID().toString().substring(0, 8);
            permit.setId(id);
            store.put(id, permit);
            return id;
        }

        @Override
        public void update(String id, Map<String, Object> data) {
            StudentPermit p = store.get(id);
            if (p != null) {
                if (data.containsKey("status")) {
                    p.setStatus(PermitStatus.valueOf((String) data.get("status")));
                }
                if (data.containsKey("approvedBy")) {
                    p.setApprovedBy((String) data.get("approvedBy"));
                }
            }
        }

        @Override
        public void delete(String id) {
            store.remove(id);
        }

        @Override
        public void approvePermit(String permitId, Map<String, Object> approvalData) {
            update(permitId, approvalData);
        }
    }

    private MockPermitRepository mockRepo;
    private PermitService service;

    @BeforeEach
    public void setUp() {
        mockRepo = new MockPermitRepository();
        service = new PermitService(mockRepo);
        AppServices.setPermitRepositoryForTesting(mockRepo);
    }

    @AfterEach
    public void tearDown() {
        AppServices.reset();
    }

    @Test
    @DisplayName("createPermit harus melengkapi metadata permit dan menyimpan ke IPermitRepository")
    public void testCreatePermitWithExplicitSchool() {
        LateEntryPermit permit = new LateEntryPermit();
        permit.setStudentName("Doni Ramadhan");
        permit.setClassName("XI-A");
        permit.setReason("Ban motor kempes");

        String createdId = service.createPermit(permit, "sch_001");

        assertNotNull(createdId);
        assertTrue(createdId.startsWith("MOCK_"));

        StudentPermit saved = mockRepo.getById(createdId);
        assertNotNull(saved);
        assertEquals("sch_001", saved.getSchoolId());
        assertEquals(PermitStatus.PENDING, saved.getStatus());
        assertNotNull(saved.getTahunAjaran());
        assertTrue(saved.getTimestamp() > 0);
    }

    @Test
    @DisplayName("approvePermit harus memperbarui status dan approver pada repository")
    public void testApprovePermit() {
        LateEntryPermit permit = new LateEntryPermit();
        permit.setStudentName("Siti");
        permit.setClassName("X-B");
        String id = service.createPermit(permit, "sch_001");

        AdminUser admin = new AdminUser();
        admin.setId("admin_01");
        admin.setName("Pak Guru Piket");
        admin.setRole(UserRole.ADMIN_PIKET);

        service.approvePermit(id, admin);

        StudentPermit updated = mockRepo.getById(id);
        assertEquals(PermitStatus.APPROVED, updated.getStatus());
        assertEquals("Pak Guru Piket", updated.getApprovedBy());
    }

    @Test
    @DisplayName("filterPermits harus memfilter berdasarkan kelas dan pencarian kata kunci")
    public void testFilterPermits() {
        LateEntryPermit p1 = new LateEntryPermit();
        p1.setStudentName("Budi Santoso");
        p1.setClassName("X-A");

        LateEntryPermit p2 = new LateEntryPermit();
        p2.setStudentName("Andi Santoso");
        p2.setClassName("XI-B");

        LateEntryPermit p3 = new LateEntryPermit();
        p3.setStudentName("Citra Lestari");
        p3.setClassName("X-B");

        List<StudentPermit> list = Arrays.asList(p1, p2, p3);

        // Filter kelas dengan prefix "X-" (kelas X-A dan X-B)
        List<StudentPermit> filterKelas = service.filterPermits(list, "X-", null);
        assertEquals(2, filterKelas.size());

        // Filter search "Santoso"
        List<StudentPermit> filterNama = service.filterPermits(list, null, "santoso");
        assertEquals(2, filterNama.size());

        // Filter kelas "X-" dan search "Budi"
        List<StudentPermit> filterKombinasi = service.filterPermits(list, "X-", "budi");
        assertEquals(1, filterKombinasi.size());
        assertEquals("Budi Santoso", filterKombinasi.get(0).getStudentName());
    }

    @Test
    @DisplayName("AppServices harus menyediakan singleton PermitService yang terinjeksi")
    public void testAppServicesContainer() {
        PermitService containerService = AppServices.getPermitService();
        assertNotNull(containerService);

        LateEntryPermit p = new LateEntryPermit();
        p.setStudentName("Uji Container");
        String id = containerService.createPermit(p, "sch_001");

        assertNotNull(mockRepo.getById(id), "Data harus tersimpan ke mockRepo yang disuntikkan via AppServices");
    }
}
