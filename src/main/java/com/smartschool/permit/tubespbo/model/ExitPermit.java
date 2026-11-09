package com.smartschool.permit.tubespbo.model;

import com.smartschool.permit.tubespbo.model.enums.PermitType;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Entitas izin keluar sekolah / dispensasi kelas (Exit Permit).
 * Menerapkan Open/Closed Principle (OCP):
 * Menentukan perilaku kalkulasi durasi keluar secara polimorfis
 * tanpa percabangan if-else manual.
 */
public class ExitPermit extends StudentPermit {

    public ExitPermit() {
        super();
        setType(PermitType.EXIT_PERMIT);
    }

    /**
     * Menghitung durasi izin keluar:
     * - Jika ada returnTimestamp: dihitung dari waktu keluar hingga waktu kembali.
     * - Jika tidak ada returnTimestamp: dihitung hingga batas akhir jam sekolah (15:00 WIB).
     */
    @Override
    public int getDurationMinutes() {
        ZoneId zone = ZoneId.of("Asia/Jakarta");
        ZonedDateTime zdt = Instant.ofEpochMilli(getTimestamp()).atZone(zone);

        if (getReturnTimestamp() > 0) {
            return (int) Math.max(0, (getReturnTimestamp() - getTimestamp()) / 60000);
        } else {
            ZonedDateTime threePm = zdt.withHour(15).withMinute(0).withSecond(0).withNano(0);
            long diff = threePm.toInstant().toEpochMilli() - getTimestamp();
            return (int) Math.max(0, diff / 60000);
        }
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        if (getReturnTimestamp() > 0) {
            map.put("returnTimestamp", getReturnTimestamp());
        }
        if (getExitTimestamp() > 0) {
            map.put("exitTimestamp", getExitTimestamp());
        }
        return map;
    }
}

