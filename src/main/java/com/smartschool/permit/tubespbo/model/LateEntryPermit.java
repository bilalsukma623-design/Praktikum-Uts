package com.smartschool.permit.tubespbo.model;

import com.smartschool.permit.tubespbo.model.enums.PermitType;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Entitas izin keterlambatan masuk sekolah (Late Entry).
 * Menerapkan Open/Closed Principle (OCP):
 * Menentukan perilaku kalkulasi durasi keterlambatan secara polimorfis
 * tanpa percabangan if-else manual.
 */
public class LateEntryPermit extends StudentPermit {

    public LateEntryPermit() {
        super();
        setType(PermitType.LATE_ENTRY);
    }

    /**
     * Menghitung selisih menit keterlambatan terhadap jam batas masuk (07:00 WIB).
     */
    @Override
    public int getDurationMinutes() {
        ZoneId zone = ZoneId.of("Asia/Jakarta");
        ZonedDateTime zdt = Instant.ofEpochMilli(getTimestamp()).atZone(zone);
        ZonedDateTime sevenAm = zdt.withHour(7).withMinute(0).withSecond(0).withNano(0);
        long diff = getTimestamp() - sevenAm.toInstant().toEpochMilli();
        return (int) Math.max(0, diff / 60000);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        if (getArrivalTimestamp() > 0) {
            map.put("arrivalTimestamp", getArrivalTimestamp());
        }
        return map;
    }
}

