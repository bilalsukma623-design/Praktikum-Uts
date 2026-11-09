package com.smartschool.permit.tubespbo.model;

import com.smartschool.permit.tubespbo.model.enums.PermitType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pengujian Unit: Polimorfisme Model Izin (OCP)")
public class PermitPolymorphismTest {

    private static final ZoneId ZONE = ZoneId.of("Asia/Jakarta");

    @Test
    @DisplayName("LateEntryPermit harus menghitung durasi keterlambatan dari jam 07:00 WIB")
    public void testLateEntryPermitDuration() {
        ZonedDateTime todaySevenAm = Instant.now().atZone(ZONE)
                .withHour(7).withMinute(0).withSecond(0).withNano(0);
        ZonedDateTime arrivalTime = todaySevenAm.plusMinutes(42);

        LateEntryPermit latePermit = new LateEntryPermit();
        latePermit.setTimestamp(arrivalTime.toInstant().toEpochMilli());
        latePermit.setArrivalTimestamp(arrivalTime.toInstant().toEpochMilli());

        assertEquals(PermitType.LATE_ENTRY, latePermit.getType());
        assertEquals(42, latePermit.getDurationMinutes(), "Durasi keterlambatan harus tepat 42 menit");

        Map<String, Object> map = latePermit.toMap();
        assertEquals("LATE_ENTRY", map.get("type"));
        assertNotNull(map.get("arrivalTimestamp"));
    }

    @Test
    @DisplayName("ExitPermit harus menghitung durasi keluar terhadap return timestamp")
    public void testExitPermitWithReturnTimestamp() {
        ZonedDateTime exitTime = Instant.now().atZone(ZONE)
                .withHour(9).withMinute(0).withSecond(0).withNano(0);
        ZonedDateTime returnTime = exitTime.plusMinutes(75);

        ExitPermit exitPermit = new ExitPermit();
        exitPermit.setTimestamp(exitTime.toInstant().toEpochMilli());
        exitPermit.setExitTimestamp(exitTime.toInstant().toEpochMilli());
        exitPermit.setReturnTimestamp(returnTime.toInstant().toEpochMilli());

        assertEquals(PermitType.EXIT_PERMIT, exitPermit.getType());
        assertEquals(75, exitPermit.getDurationMinutes(), "Durasi izin keluar harus tepat 75 menit");

        Map<String, Object> map = exitPermit.toMap();
        assertEquals("EXIT_PERMIT", map.get("type"));
        assertNotNull(map.get("returnTimestamp"));
    }

    @Test
    @DisplayName("Polymorphic invocation: Base reference memanggil durasi subclass yang tepat")
    public void testPolymorphicBehavior() {
        ZonedDateTime todaySevenAm = Instant.now().atZone(ZONE)
                .withHour(7).withMinute(0).withSecond(0).withNano(0);
        ZonedDateTime arrival = todaySevenAm.plusMinutes(25);

        StudentPermit p1 = new LateEntryPermit();
        p1.setTimestamp(arrival.toInstant().toEpochMilli());

        StudentPermit p2 = new ExitPermit();
        p2.setTimestamp(arrival.toInstant().toEpochMilli());
        p2.setReturnTimestamp(arrival.plusMinutes(60).toInstant().toEpochMilli());

        // Verifikasi pemanggilan polimorfik tanpa mengetahui tipe konkretnya secara eksplisit
        assertEquals(25, p1.getDurationMinutes());
        assertEquals(60, p2.getDurationMinutes());
    }
}

