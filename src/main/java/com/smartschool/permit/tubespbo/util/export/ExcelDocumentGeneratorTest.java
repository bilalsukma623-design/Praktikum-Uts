/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartschool.permit.tubespbo.util.export;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pengujian Unit: ExcelDocumentGenerator (SRP)")
public class ExcelDocumentGeneratorTest {

    @Test
    @DisplayName("Harus berhasil membangkitkan dokumen Excel XLSX tanpa komponen GUI")
    public void testGenerateValidExcelDocument() throws Exception {
        List<String> headers = Arrays.asList("No", "Nama", "Kelas", "Durasi (Menit)");
        List<List<Object>> rows = Arrays.asList(
                Arrays.asList(1, "Ahmad Dahlan", "XII-A", 45),
                Arrays.asList(2, "Siti Rahma", "XI-B", 30)
        );

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExcelDocumentGenerator.generate(headers, rows, out);

        byte[] bytes = out.toByteArray();
        assertTrue(bytes.length > 0, "Ukuran berkas output harus lebih dari 0 byte");

        // Verifikasi struktur berkas XLSX menggunakan Apache POI
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            Sheet sheet = workbook.getSheet("Laporan");
            assertNotNull(sheet, "Sheet 'Laporan' harus terbentuk");
            assertEquals(3, sheet.getPhysicalNumberOfRows(), "Jumlah total baris (1 header + 2 data) harus 3");

            // Cek Header
            assertEquals("No", sheet.getRow(0).getCell(0).getStringCellValue());
            assertEquals("Nama", sheet.getRow(0).getCell(1).getStringCellValue());

            // Cek Data
            assertEquals("Ahmad Dahlan", sheet.getRow(1).getCell(1).getStringCellValue());
            assertEquals(45.0, sheet.getRow(1).getCell(3).getNumericCellValue(), 0.001);
            assertEquals("Siti Rahma", sheet.getRow(2).getCell(1).getStringCellValue());
            assertEquals(30.0, sheet.getRow(2).getCell(3).getNumericCellValue(), 0.001);
        }
    }

    @Test
    @DisplayName("Harus melempar IllegalArgumentException jika parameter null")
    public void testNullParametersThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            ExcelDocumentGenerator.generate(null, null, null);
        });
    }
}