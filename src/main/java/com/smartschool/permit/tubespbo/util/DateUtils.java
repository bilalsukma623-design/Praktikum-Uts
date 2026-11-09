/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartschool.permit.tubespbo.util;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
/**
 *
 * @author Doni354
 */
public class DateUtils {
    private static final ZoneId ZONE = ZoneId.of("Asia/Jakarta");
    private static final Locale LOCALE_ID = Locale.of("id", "ID");

    public static String formatDate(long timestamp) {
        ZonedDateTime zdt = Instant.ofEpochMilli(timestamp).atZone(ZONE);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", LOCALE_ID);
        return zdt.format(formatter);
    }

    public static String formatTime(long timestamp) {
        ZonedDateTime zdt = Instant.ofEpochMilli(timestamp).atZone(ZONE);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm", LOCALE_ID);
        return zdt.format(formatter);
    }

    public static String formatDateTime(long timestamp) {
        ZonedDateTime zdt = Instant.ofEpochMilli(timestamp).atZone(ZONE);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", LOCALE_ID);
        return zdt.format(formatter);
    }

    public static boolean isToday(long timestamp) {
        ZonedDateTime now = Instant.now().atZone(ZONE);
        ZonedDateTime date = Instant.ofEpochMilli(timestamp).atZone(ZONE);
        return now.getYear() == date.getYear() && now.getDayOfYear() == date.getDayOfYear();
    }
}