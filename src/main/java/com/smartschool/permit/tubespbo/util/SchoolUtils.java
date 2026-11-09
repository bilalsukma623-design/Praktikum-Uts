/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartschool.permit.tubespbo.util;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Doni354
 */
public class SchoolUtils {
    
    // Logic ganti tahun ajaran per Juli
    public static String getTahunAjaran(long timestamp) {
        ZonedDateTime date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.of("Asia/Jakarta"));
        int year = date.getYear();
        int month = date.getMonthValue();
        
        if (month >= 7) {
            return year + "/" + (year + 1);
        } else {
            return (year - 1) + "/" + year;
        }
    }

    public static String[] getGrades() {
        return new String[]{"X", "XI", "XII"};
    }

    public static List<String> getAllClasses() {
        List<String> classes = new ArrayList<>();
        String[] grades = getGrades();
        String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K"};
        
        for (String grade : grades) {
            int limit = grade.equals("X") ? 10 : 11; // X cuma sampai J, XI & XII sampai K 
            for (int i = 0; i < limit; i++) {
                classes.add(grade + "-" + letters[i]);
            }
        }
        return classes;
    }

    public static String[] parseClass(String cls) {
        if (cls == null || !cls.contains("-")) return new String[]{"", ""};
        return cls.split("-");
    }
}
