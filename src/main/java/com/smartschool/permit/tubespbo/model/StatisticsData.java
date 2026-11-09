/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartschool.permit.tubespbo.model;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
/**
 *
 * @author Doni354
 */

public class StatisticsData {
    private int totalLateEntry = 0;
    private int totalExitPermit = 0;
    private int todayLateCount = 0;
    private int todayExitCount = 0;
    private int pendingCount = 0;
    private int todayCount = 0;

    public void calculate(List<StudentPermit> permits) {
        ZonedDateTime now = Instant.now().atZone(ZoneId.of("Asia/Jakarta"));
        int todayDay = now.getDayOfYear();
        int todayYear = now.getYear();

        for (StudentPermit p : permits) {
            if (p.isLateEntry()) totalLateEntry++;
            if (p.isExitPermit()) totalExitPermit++;
            // Hanya hitung pending jika status ADA (bukan null) dan bernilai PENDING
            if (p.getStatus() != null && p.isPending()) pendingCount++;

            ZonedDateTime pDate = Instant.ofEpochMilli(p.getTimestamp()).atZone(ZoneId.of("Asia/Jakarta"));
            if (pDate.getDayOfYear() == todayDay && pDate.getYear() == todayYear) {
                todayCount++;
                if (p.isLateEntry()) todayLateCount++;
                if (p.isExitPermit()) todayExitCount++;
            }
        }
    }

    // Getters and Setters
    public int getTotalLateEntry() { return totalLateEntry; }
    public int getTotalExitPermit() { return totalExitPermit; }
    public int getTodayLateCount() { return todayLateCount; }
    public int getTodayExitCount() { return todayExitCount; }
    public int getPendingCount() { return pendingCount; }
    public int getTodayCount() { return todayCount; }
}
