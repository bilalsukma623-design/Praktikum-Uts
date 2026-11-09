package com.smartschool.permit.tubespbo.service;

import com.smartschool.permit.tubespbo.model.ExitPermit;
import com.smartschool.permit.tubespbo.model.LateEntryPermit;
import com.smartschool.permit.tubespbo.model.PermitSummary;
import com.smartschool.permit.tubespbo.model.StatisticsData;
import com.smartschool.permit.tubespbo.model.StudentPermit;
import com.smartschool.permit.tubespbo.model.enums.PermitStatus;
import com.smartschool.permit.tubespbo.model.enums.PermitType;
import com.smartschool.permit.tubespbo.repository.interfaces.PermitReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pengujian Unit: ReportService & Segregasi Interface (ISP)")
public class ReportServiceIspTest {

    private static final ZoneId ZONE = ZoneId.of("Asia/Jakarta");

    /**
     * Stub implementasi PermitReader (murni read-only, tanpa operasi CRUD mutasi).
     * Membuktikan bahwa ReportService dapat beroperasi hanya dengan role interface PermitReader.
     */
    static class StubPermitReader implements PermitReader {
        private final List<StudentPermit> permits = new ArrayList<>();

        public void addPermit(StudentPermit permit) {
            permits.add(permit);
        }

        @Override
        public List<StudentPermit> getBySchool(String schoolId) {
            return new ArrayList<>(permits);
        }

        @Override
        public List<StudentPermit> getByType(String schoolId, PermitType type) {
            List<StudentPermit> filtered = new ArrayList<>();
            for (StudentPermit p : permits) {
                if (p.getType() == type) filtered.add(p);
            }
            return filtered;
        }

        @Override
        public StudentPermit getById(String id) {
            for (StudentPermit p : permits) {
                if (id.equals(p.getId())) return p;
            }
            return null;
        }
    }

    @Test
    @DisplayName("ReportService harus menghitung statistik dashboard dengan benar dari PermitReader")
    public void testGetDashboardStatsWithReader() {
        StubPermitReader stubReader = new StubPermitReader();

        long now = System.currentTimeMillis();

        LateEntryPermit late = new LateEntryPermit();
        late.setId("P1");
        late.setStudentName("Budi");
        late.setClassName("X-A");
        late.setTimestamp(now);
        late.setStatus(PermitStatus.PENDING);
        stubReader.addPermit(late);

        ExitPermit exit = new ExitPermit();
        exit.setId("P2");
        exit.setStudentName("Budi");
        exit.setClassName("X-A");
        exit.setTimestamp(now);
        exit.setStatus(PermitStatus.APPROVED);
        stubReader.addPermit(exit);

        // Injeksi role interface PermitReader ke ReportService
        ReportService service = new ReportService(stubReader);
        StatisticsData stats = service.getDashboardStats("sch_001");

        assertEquals(1, stats.getTotalLateEntry());
        assertEquals(1, stats.getTotalExitPermit());
        assertEquals(1, stats.getPendingCount());
        assertEquals(2, stats.getTodayCount());
    }

    @Test
    @DisplayName("ReportService harus mengagregasi ringkasan per siswa secara terurut")
    public void testGetStudentSummary() {
        StubPermitReader stubReader = new StubPermitReader();

        long now = System.currentTimeMillis();

        LateEntryPermit p1 = new LateEntryPermit();
        p1.setStudentName("Ahmad");
        p1.setClassName("XII-A");
        p1.setTimestamp(now);
        stubReader.addPermit(p1);

        LateEntryPermit p2 = new LateEntryPermit();
        p2.setStudentName("Ahmad");
        p2.setClassName("XII-A");
        p2.setTimestamp(now);
        stubReader.addPermit(p2);

        ExitPermit p3 = new ExitPermit();
        p3.setStudentName("Budi");
        p3.setClassName("XI-B");
        p3.setTimestamp(now);
        stubReader.addPermit(p3);

        ReportService service = new ReportService(stubReader);
        List<PermitSummary> summaries = service.getStudentSummary("sch_001");

        assertEquals(2, summaries.size());
        // Ahmad memiliki 2 izin, Budi 1 izin -> Ahmad harus di urutan pertama
        assertEquals("Ahmad", summaries.get(0).getStudentName());
        assertEquals(2, summaries.get(0).getLateCount());
        assertEquals(2, summaries.get(0).getTotalCount());

        assertEquals("Budi", summaries.get(1).getStudentName());
        assertEquals(1, summaries.get(1).getExitCount());
    }
}

