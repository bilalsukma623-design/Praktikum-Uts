/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartschool.permit.tubespbo.service;

import com.smartschool.permit.tubespbo.model.PermitSummary;
import com.smartschool.permit.tubespbo.model.StatisticsData;
import com.smartschool.permit.tubespbo.model.StudentPermit;
import com.smartschool.permit.tubespbo.repository.PermitRepository;
import com.smartschool.permit.tubespbo.repository.interfaces.PermitReader;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service untuk menyusun agregasi, statistik, dan rekapitulasi laporan izin.
 * Menerapkan Interface Segregation Principle (ISP):
 * Hanya bergantung pada role interface PermitReader (khusus baca),
 * tidak terekspos pada operasi mutasi (create/update/delete).
 *
 * @author Doni354
 */
public class ReportService {
    private final PermitReader permitReader;

    /**
     * Konstruktor utama: menerima abstraksi PermitReader (ISP & DIP).
     */
    public ReportService(PermitReader permitReader) {
        if (permitReader == null) {
            throw new IllegalArgumentException("PermitReader tidak boleh null!");
        }
        this.permitReader = permitReader;
    }

    /**
     * Konstruktor penunjang backward-compatibility untuk kode yang telah ada.
     */
    @Deprecated
    public ReportService(PermitRepository permitRepo) {
        this((PermitReader) permitRepo);
    }

    public StatisticsData getDashboardStats(String schoolId) {
        List<StudentPermit> permits = permitReader.getBySchool(schoolId);
        StatisticsData stats = new StatisticsData();
        stats.calculate(permits);
        return stats;
    }

    public List<PermitSummary> getStudentSummary(String schoolId) {
        List<StudentPermit> permits = permitReader.getBySchool(schoolId);
        Map<String, PermitSummary> summaryMap = new HashMap<>();

        for (StudentPermit p : permits) {
            String key = p.getStudentName() + "||" + p.getClassName();
            PermitSummary summary = summaryMap.getOrDefault(key, new PermitSummary());
            
            summary.setStudentName(p.getStudentName());
            summary.setClassName(p.getClassName());
            
            if (p.isLateEntry()) {
                summary.setLateCount(summary.getLateCount() + 1);
            } else if (p.isExitPermit()) {
                summary.setExitCount(summary.getExitCount() + 1);
            }
            
            summaryMap.put(key, summary);
        }

        List<PermitSummary> result = new ArrayList<>(summaryMap.values());
        // Sort descending berdasarkan total izin paling banyak
        result.sort((a, b) -> Integer.compare(b.getTotalCount(), a.getTotalCount()));
        return result;
    }

    public Map<String, int[]> getMonthlyRecap(String schoolId, int year, int month) {
        List<StudentPermit> permits = permitReader.getBySchool(schoolId);
        Map<String, int[]> recapMap = new HashMap<>(); // Key: ClassName, Value: [lateCount, exitCount]

        for (StudentPermit p : permits) {
            ZonedDateTime pDate = Instant.ofEpochMilli(p.getTimestamp()).atZone(ZoneId.of("Asia/Jakarta"));
            
            // Filter berdasarkan tahun dan bulan yang di-request
            if (pDate.getYear() == year && pDate.getMonthValue() == month) {
                String className = p.getClassName();
                int[] counts = recapMap.getOrDefault(className, new int[]{0, 0});
                
                if (p.isLateEntry()) {
                    counts[0]++;
                } else if (p.isExitPermit()) {
                    counts[1]++;
                }
                recapMap.put(className, counts);
            }
        }
        return recapMap;
    }
}
