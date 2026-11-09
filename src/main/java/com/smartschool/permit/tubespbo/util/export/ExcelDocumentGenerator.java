package com.smartschool.permit.tubespbo.util.export;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Komponen murni pembuat format berkas Excel (Apache POI).
 * Menerapkan Single Responsibility Principle (SRP):
 * Hanya bertanggung jawab membangkitkan dokumen Excel dari data generik,
 * sepenuhnya independen dari pustaka UI (Swing).
 */
public final class ExcelDocumentGenerator {

    private ExcelDocumentGenerator() {
        // Mencegah instansiasi utility class
    }

    /**
     * Membangkitkan lembar kerja Excel (XLSX) dan menuliskannya ke target OutputStream.
     *
     * @param headers Daftar nama kolom header
     * @param rows Daftar baris data (tiap baris berupa daftar nilai sel)
     * @param out Target stream output berkas
     * @throws IOException jika terjadi kegagalan transmisi I/O
     */
    public static void generate(List<String> headers, List<List<Object>> rows, OutputStream out) throws IOException {
        if (headers == null || rows == null || out == null) {
            throw new IllegalArgumentException("Header, data baris, dan output stream tidak boleh null!");
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Laporan");

            // Konfigurasi Header Style & Font
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Tulis baris header
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < headers.size(); col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(headers.get(col));
                cell.setCellStyle(headerStyle);
            }

            // Tulis baris data
            for (int r = 0; r < rows.size(); r++) {
                Row excelRow = sheet.createRow(r + 1);
                List<Object> rowData = rows.get(r);
                if (rowData != null) {
                    for (int c = 0; c < rowData.size(); c++) {
                        Object val = rowData.get(c);
                        if (val != null) {
                            Cell cell = excelRow.createCell(c);
                            if (val instanceof Number) {
                                cell.setCellValue(((Number) val).doubleValue());
                            } else {
                                cell.setCellValue(val.toString());
                            }
                        }
                    }
                }
            }

            // Auto-size lebar kolom agar teks tidak terpotong
            for (int col = 0; col < headers.size(); col++) {
                sheet.autoSizeColumn(col);
            }

            workbook.write(out);
        }
    }
}

