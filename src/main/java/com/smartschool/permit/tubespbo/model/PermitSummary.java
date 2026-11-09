/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartschool.permit.tubespbo.model;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author Doni354
 */
public class PermitSummary implements Exportable {
    private String studentName;
    private String className;
    private int lateCount;
    private int exitCount;
    private int totalLateMinutes;
    private int totalExitMinutes;

    public int getTotalCount() {
        return lateCount + exitCount;
    }

    @Override
    public Map<String, Object> toExportRow() {
        Map<String, Object> map = new HashMap<>();
        map.put("Nama", studentName);
        map.put("Kelas", className);
        map.put("Total Terlambat", lateCount);
        map.put("Total Menit Terlambat", totalLateMinutes);
        map.put("Total Izin Keluar", exitCount);
        map.put("Total Menit Izin", totalExitMinutes);
        map.put("Total Semua", getTotalCount());
        return map;
    }

    // Getters and Settersss
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public int getLateCount() { return lateCount; }
    public void setLateCount(int lateCount) { this.lateCount = lateCount; }
    public int getExitCount() { return exitCount; }
    public void setExitCount(int exitCount) { this.exitCount = exitCount; }
    public int getTotalLateMinutes() { return totalLateMinutes; }
    public void setTotalLateMinutes(int totalLateMinutes) { this.totalLateMinutes = totalLateMinutes; }
    public int getTotalExitMinutes() { return totalExitMinutes; }
    public void setTotalExitMinutes(int totalExitMinutes) { this.totalExitMinutes = totalExitMinutes; }
}