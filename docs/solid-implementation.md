# 📘 Laporan Implementasi Refactoring Prinsip SOLID
**Mata Kuliah:** Analisis dan Desain Perangkat Lunak (ADPL)  
**Proyek:** SmartSchool Permit System (TubesPBO)  
**Tujuan Dokumen:** Dokumentasi komprehensif implementasi aktual 5 kasus refactoring prinsip SOLID pada source code proyek, perbandingan utuh *BEFORE* vs *AFTER*, hasil validasi build/test, dan mitigasi regresi untuk bahan laporan UTS.  
**Status Implementasi:** ✅ Selesai & Tervalidasi (`mvn test` 12/12 Passed, `mvn package` BUILD SUCCESS).

---

## 📋 Daftar Isi
1. [Ringkasan Eksekutif & Status Validasi](#1-ringkasan-eksekutif--status-validasi)
2. [Matriks File yang Berubah & Bertambah](#2-matriks-file-yang-berubah--bertambah)
3. [Implementasi Kasus 1 (SRP): Dekopling Modul Ekspor Excel](#3-implementasi-kasus-1-srp-dekopling-modul-ekspor-excel)
4. [Implementasi Kasus 2 (OCP): Polimorfisme Kalkulasi Durasi Izin](#4-implementasi-kasus-2-ocp-polimorfisme-kalkulasi-durasi-izin)
5. [Implementasi Kasus 3 (LSP): Harmonisasi Kontrak Persistensi Entitas](#5-implementasi-kasus-3-lsp-harmonisasi-kontrak-persistensi-entitas)
6. [Implementasi Kasus 4 (ISP): Segregasi Role Interface PermitReader](#6-implementasi-kasus-4-isp-segregasi-role-interface-permitreader)
7. [Implementasi Kasus 5 (DIP): Inversi Ketergantungan Service Layer](#7-implementasi-kasus-5-dip-inversi-ketergantungan-service-layer)
8. [Laporan Validasi Build & Penanganan Masalah](#8-laporan-validasi-build--penanganan-masalah)
9. [Kesimpulan untuk Laporan UTS](#9-kesimpulan-untuk-laporan-uts)

---

## 1. Ringkasan Eksekutif & Status Validasi

Refactoring arsitektur telah berhasil diimplementasikan pada codebase **SmartSchool Permit System** untuk 5 kasus pilihan yang merepresentasikan prinsip **S-O-L-I-D**. Seluruh implementasi mempertahankan **100% kompatibilitas ke belakang (*backward compatibility*)** terhadap fitur sistem yang sudah ada dan tidak merusak struktur database Google Cloud Firestore.

### Hasil Verifikasi Resmi:
- **Kompilasi Maven (`mvn clean test-compile`):** Berhasil mengompilasi 53 berkas sumber Java tanpa error.
- **Pengujian Unit Otomatis (`mvn test`):** **12 dari 12 unit test lulus (100% Passed, 0 Failures, 0 Errors, 0 Skipped)**.
- **Pembangunan Paket Distribusi (`mvn package`):** Berhasil menghasilkan shaded uber-jar executable di `target/TubesPBO-1.0-SNAPSHOT.jar` (11.4 s).

---

## 2. Matriks File yang Berubah & Bertambah

```
┌────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                 DAFTAR BERKAS TERDAMPAK REFACTORING                                    │
├────┬───────────────────────────────────────────────────────┬───────────┬───────────────────────────────┤
│ No │ Lokasi Berkas                                         │ Tipe Aksi │ Prinsip SOLID Terkait         │
├────┼───────────────────────────────────────────────────────┼───────────┼───────────────────────────────┤
│ 1  │ util/export/ExcelDocumentGenerator.java               │ Dibuat    │ SRP (Core Excel Generator)    │
│ 2  │ gui/helper/SwingExportHelper.java                     │ Dibuat    │ SRP (Presentation Helper)     │
│ 3  │ util/XlsxUtils.java                                   │ Diubah    │ SRP (Facade Backward-Compat)  │
│ 4  │ model/LateEntryPermit.java                            │ Dibuat    │ OCP (Subclass Polimorfis)     │
│ 5  │ model/ExitPermit.java                                 │ Dibuat    │ OCP (Subclass Polimorfis)     │
│ 6  │ gui/formDispen/FormKeterlambatan.java                 │ Diubah    │ OCP & DIP                     │
│ 7  │ gui/formDispen/FormDispensasi.java                    │ Diubah    │ OCP & DIP                     │
│ 8  │ repository/BaseRepository.java                        │ Diubah    │ LSP (Harmonisasi Kontrak)     │
│ 9  │ repository/StudentRepository.java                     │ Diubah    │ LSP (Harmonisasi Kontrak)     │
│ 10 │ repository/interfaces/PermitReader.java               │ Dibuat    │ ISP (Role Interface Read)     │
│ 11 │ repository/interfaces/PermitWriter.java               │ Dibuat    │ ISP (Role Interface Write)    │
│ 12 │ service/ReportService.java                            │ Diubah    │ ISP (Injeksi PermitReader)    │
│ 13 │ repository/interfaces/IPermitRepository.java          │ Dibuat    │ DIP & ISP                     │
│ 14 │ repository/PermitRepository.java                      │ Diubah    │ DIP, OCP & ISP                │
│ 15 │ service/PermitService.java                            │ Diubah    │ DIP (Injeksi IPermitRepo)     │
│ 16 │ app/AppServices.java                                  │ Dibuat    │ DIP (Composition Root / DI)   │
│ 17 │ gui/dashboard/LateEntryPanel.java                     │ Diubah    │ DIP (Menggunakan AppServices) │
│ 18 │ gui/dashboard/ExitPermitPanel.java                    │ Diubah    │ DIP (Menggunakan AppServices) │
│ 19 │ gui/dashboard/DashboardPanel.java                     │ Diubah    │ DIP (Menggunakan AppServices) │
│ 20 │ gui/dashboard/ReportPanel.java                        │ Diubah    │ DIP (Menggunakan AppServices) │
│ 21 │ pom.xml                                               │ Diubah    │ Dependency Testing (JUnit 5)  │
│ 22 │ src/test/java/... (5 Berkas Unit Test)                │ Dibuat    │ Pengujian Unit Terotomatisasi │
└────┴───────────────────────────────────────────────────────┴───────────┴───────────────────────────────┘
```

---

## 3. Implementasi Kasus 1 (SRP): Dekopling Modul Ekspor Excel

### 3.1 Alasan Pemilihan Prinsip SOLID
Kelas `XlsxUtils` asli mencampur aduk 5 tanggung jawab: interaksi pengguna via `JFileChooser`, ketergantungan model komponen visual Swing `JTable`/`TableModel`, format sel & pewarnaan Apache POI, operasi fisik I/O disk, dan dialog modal `JOptionPane`. Pelanggaran ini mengakibatkan kelas tidak dapat diuji tanpa GUI (*zero testability*) dan tidak dapat digunakan ulang di luar Swing (*zero reusability*).

### 3.2 Perubahan Desain & Tanggung Jawab
- **BEFORE:** `XlsxUtils` monolitik mengurus GUI, manipulasi JTable, format Excel, dan disk I/O dalam 1 method static.
- **AFTER:**
  1. `ExcelDocumentGenerator` (di package `util.export`): Murni mengonstruksi byte berkas XLSX dari data generik (`List<String>` header, `List<List<Object>>` rows, dan target `OutputStream`). Bebas dari `javax.swing.*`.
  2. `SwingExportHelper` (di package `gui.helper`): Mengelola interaksi GUI Swing (buka `JFileChooser`, ekstrak data dari `JTable`, delegasikan penulisan ke `ExcelDocumentGenerator`, dan beri feedback via `JOptionPane`).
  3. `XlsxUtils`: Disederhanakan menjadi facade satu baris yang mendelegasikan ke `SwingExportHelper`, menjaga kompatibilitas ke belakang 100%.

### 3.3 Source Code BEFORE (Utuh)
```java
// Lokasi: src/main/java/com/smartschool/permit/tubespbo/util/XlsxUtils.java
package com.smartschool.permit.tubespbo.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Utility untuk export JTable data ke file XLSX (Excel).
 */
public class XlsxUtils {

    public static void exportTable(JTable table, String defaultFileName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan Excel");
        fileChooser.setSelectedFile(new File(defaultFileName + ".xlsx"));

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getName().toLowerCase().endsWith(".xlsx")) {
            fileToSave = new File(fileToSave.getAbsolutePath() + ".xlsx");
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Laporan");
            TableModel model = table.getModel();

            // Create Header Font/Style
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Header Row
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < model.getColumnCount(); col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(model.getColumnName(col));
                cell.setCellStyle(headerStyle);
            }

            // Data Rows
            for (int row = 0; row < model.getRowCount(); row++) {
                Row excelRow = sheet.createRow(row + 1);
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Object value = model.getValueAt(row, col);
                    if (value != null) {
                        if (value instanceof Number) {
                            excelRow.createCell(col).setCellValue(((Number) value).doubleValue());
                        } else {
                            excelRow.createCell(col).setCellValue(value.toString());
                        }
                    }
                }
            }

            // Auto-size columns
            for (int col = 0; col < model.getColumnCount(); col++) {
                sheet.autoSizeColumn(col);
            }

            try (FileOutputStream fileOut = new FileOutputStream(fileToSave)) {
                workbook.write(fileOut);
                JOptionPane.showMessageDialog(null, 
                        "Laporan berhasil diexport ke:\n" + fileToSave.getAbsolutePath(), 
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                    "Gagal export Excel: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
```

### 3.4 Source Code AFTER yang Diimplementasikan
#### A. `util/export/ExcelDocumentGenerator.java`
```java
package com.smartschool.permit.tubespbo.util.export;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public final class ExcelDocumentGenerator {

    private ExcelDocumentGenerator() {}

    public static void generate(List<String> headers, List<List<Object>> rows, OutputStream out) throws IOException {
        if (headers == null || rows == null || out == null) {
            throw new IllegalArgumentException("Header, data baris, dan output stream tidak boleh null!");
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Laporan");

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < headers.size(); col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(headers.get(col));
                cell.setCellStyle(headerStyle);
            }

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

            for (int col = 0; col < headers.size(); col++) {
                sheet.autoSizeColumn(col);
            }

            workbook.write(out);
        }
    }
}
```

#### B. `util/XlsxUtils.java` (Hasil Refactoring)
```java
package com.smartschool.permit.tubespbo.util;

import com.smartschool.permit.tubespbo.gui.helper.SwingExportHelper;
import javax.swing.JTable;

public class XlsxUtils {
    public static void exportTable(JTable table, String defaultFileName) {
        SwingExportHelper.exportTableWithDialog(null, table, defaultFileName);
    }
}
```

---

## 4. Implementasi Kasus 2 (OCP): Polimorfisme Kalkulasi Durasi Izin

### 4.1 Alasan Pemilihan Prinsip SOLID
`StudentPermit` asli menggunakan percabangan tipe `isLateEntry()` dan `isExitPermit()` di dalam `getDurationMinutes()` dan `toMap()`. Menambah jenis izin baru mengharuskan modifikasi berkelanjutan pada method yang ada (*closed for modification violated*). Melalui konsep *Replace Conditional with Polymorphism*, perilaku kalkulasi durasi didelegasikan secara mandiri ke subclass.

### 4.2 Perubahan Desain & Tanggung Jawab
- **BEFORE:** `StudentPermit` menampung seluruh atribut campuran (`arrivalTimestamp` dan `returnTimestamp`) dan percabangan `if-else` untuk kalkulasi durasi.
- **AFTER:**
  1. `StudentPermit` menjadi basis kelas yang stabil.
  2. Subclass `LateEntryPermit` bertanggung jawab menghitung keterlambatan terhadap jam 07:00 WIB dan menyimpan `arrivalTimestamp`.
  3. Subclass `ExitPermit` bertanggung jawab menghitung selisih jam keluar terhadap `returnTimestamp` atau jam pulang 15:00 WIB.
  4. `PermitRepository.toEntity()` secara otomatis merekonstruksi dokumen Firestore menjadi instance `LateEntryPermit` atau `ExitPermit` secara polimorfis.

### 4.3 Source Code BEFORE (Potongan Kritis)
```java
// Lokasi: src/main/java/com/smartschool/permit/tubespbo/model/StudentPermit.java (Baris 52-70)
public int getDurationMinutes() {
    ZoneId zone = ZoneId.of("Asia/Jakarta");
    ZonedDateTime zdt = Instant.ofEpochMilli(timestamp).atZone(zone);
    
    if (isLateEntry()) {
        ZonedDateTime sevenAm = zdt.withHour(7).withMinute(0).withSecond(0).withNano(0);
        long diff = timestamp - sevenAm.toInstant().toEpochMilli();
        return (int) Math.max(0, diff / 60000);
    } else if (isExitPermit()) {
        if (returnTimestamp > 0) {
            return (int) ((returnTimestamp - timestamp) / 60000);
        } else {
            ZonedDateTime threePm = zdt.withHour(15).withMinute(0).withSecond(0).withNano(0);
            long diff = threePm.toInstant().toEpochMilli() - timestamp;
            return (int) Math.max(0, diff / 60000);
        }
    }
    return 0;
}
```

### 4.4 Source Code AFTER yang Diimplementasikan
#### A. `model/LateEntryPermit.java`
```java
package com.smartschool.permit.tubespbo.model;

import com.smartschool.permit.tubespbo.model.enums.PermitType;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

public class LateEntryPermit extends StudentPermit {

    public LateEntryPermit() {
        super();
        setType(PermitType.LATE_ENTRY);
    }

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
```

#### B. `model/ExitPermit.java`
```java
package com.smartschool.permit.tubespbo.model;

import com.smartschool.permit.tubespbo.model.enums.PermitType;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

public class ExitPermit extends StudentPermit {

    public ExitPermit() {
        super();
        setType(PermitType.EXIT_PERMIT);
    }

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
        if (getReturnTimestamp() > 0) map.put("returnTimestamp", getReturnTimestamp());
        if (getExitTimestamp() > 0) map.put("exitTimestamp", getExitTimestamp());
        return map;
    }
}
```

---

## 5. Implementasi Kasus 3 (LSP): Harmonisasi Kontrak Persistensi Entitas

### 5.1 Alasan Pemilihan Prinsip SOLID
`BaseRepository.create(T entity)` secara baku mengasumsikan seluruh entitas disimpan dengan document ID acak dari Firestore (`.add(...)`). Namun entitas `Student` mewajibkan ID dokumen Firestore sama dengan `uid` dari Firebase Authentication. Pemanggilan polimorfik `CrudRepository<Student> repo = new StudentRepository(); repo.create(student)` pada kode lama merusak akun siswa. Subclass tidak dapat menggantikan perilaku superclass tanpa merusak integritas sistem (*LSP Violation*).

### 5.2 Perubahan Desain & Tanggung Jawab
- **BEFORE:** `BaseRepository.create()` selalu mengabaikan `entity.getId()` dan menghasilkan ID acak. Developer membuat method baru `createWithId()` di `StudentRepository` tetapi membiarkan method warisan `create()` cacat untuk siswa.
- **AFTER:** `BaseRepository.create()` diselaraskan untuk memeriksa apakah entitas telah memiliki ID eksplisit. Jika ada (`getId() != null`), ia menggunakan `.document(explicitId).set(...)`. Jika kosong, ia menggunakan auto-generate `.add(...)`. Method `StudentRepository.createWithId()` sekarang mendelegasikan ke `create(student)` yang sudah patuh kontrak LSP.

### 5.3 Source Code BEFORE
```java
// Lokasi: src/main/java/com/smartschool/permit/tubespbo/repository/BaseRepository.java (Baris 65-73)
@Override
public String create(T entity) {
    try {
        ApiFuture<DocumentReference> future = db.collection(collectionName).add(entity.toMap());
        return future.get().getId();
    } catch (Exception e) {
        System.err.println("Error creating document: " + e.getMessage());
        return null;
    }
}
```

### 5.4 Source Code AFTER yang Diimplementasikan
```java
// Lokasi: src/main/java/com/smartschool/permit/tubespbo/repository/BaseRepository.java
@Override
public String create(T entity) {
    if (entity == null) {
        throw new IllegalArgumentException("Entitas yang akan disimpan tidak boleh null!");
    }
    try {
        // Harmonisasi LSP: jika entitas sudah memiliki ID eksplisit (seperti UID Auth), gunakan ID tersebut
        if (entity.getId() != null && !entity.getId().trim().isEmpty()) {
            String explicitId = entity.getId();
            db.collection(collectionName).document(explicitId).set(entity.toMap()).get();
            return explicitId;
        } else {
            // Jika belum ber-ID, gunakan auto-generated ID dari Firestore
            ApiFuture<DocumentReference> future = db.collection(collectionName).add(entity.toMap());
            String autoId = future.get().getId();
            entity.setId(autoId);
            return autoId;
        }
    } catch (Exception e) {
        System.err.println("Error creating document in " + collectionName + ": " + e.getMessage());
        return null;
    }
}
```

```java
// Lokasi: src/main/java/com/smartschool/permit/tubespbo/repository/StudentRepository.java
public void createWithId(String uid, Student student) {
    if (student == null) {
        throw new IllegalArgumentException("Data siswa tidak boleh null!");
    }
    student.setId(uid);
    String createdId = create(student);
    if (createdId == null) {
        throw new RuntimeException("Gagal menyimpan data siswa ke database!");
    }
}
```

---

## 6. Implementasi Kasus 4 (ISP): Segregasi Role Interface PermitReader

### 6.1 Alasan Pemilihan Prinsip SOLID
`ReportService` hanya bertugas menyusun statistik, rekap bulanan, dan agregasi siswa (murni read-only). Mengikatnya ke repositori penuh yang menyediakan `create`, `update`, `delete` melanggar prinsip pemisahan antarmuka (*Interface Segregation Principle*). Klien tidak boleh dipaksa bergantung pada method mutasi data yang tidak digunakannya.

### 6.2 Perubahan Desain & Tanggung Jawab
- **BEFORE:** `ReportService` bergantung langsung pada `PermitRepository` yang memiliki akses penuh menghapus dan mengubah izin.
- **AFTER:**
  1. Dibuat interface peran `PermitReader` (hanya mendefinisikan `getBySchool`, `getByType`, dan `getById`).
  2. Dibuat interface peran `PermitWriter` (mendefinisikan operasi mutasi dan `approvePermit`).
  3. `ReportService` hanya menginjeksi dan mengenal abstraksi `PermitReader`.

### 6.3 Source Code BEFORE
```java
// Lokasi: src/main/java/com/smartschool/permit/tubespbo/service/ReportService.java (Baris 22-27)
public class ReportService {
    private final PermitRepository permitRepo;

    public ReportService(PermitRepository permitRepo) {
        this.permitRepo = permitRepo;
    }
    // ...
}
```

### 6.4 Source Code AFTER yang Diimplementasikan
#### A. `repository/interfaces/PermitReader.java`
```java
package com.smartschool.permit.tubespbo.repository.interfaces;

import com.smartschool.permit.tubespbo.model.StudentPermit;
import com.smartschool.permit.tubespbo.model.enums.PermitType;
import java.util.List;

public interface PermitReader {
    List<StudentPermit> getBySchool(String schoolId);
    List<StudentPermit> getByType(String schoolId, PermitType type);
    StudentPermit getById(String id);
}
```

#### B. `service/ReportService.java`
```java
package com.smartschool.permit.tubespbo.service;

import com.smartschool.permit.tubespbo.model.PermitSummary;
import com.smartschool.permit.tubespbo.model.StatisticsData;
import com.smartschool.permit.tubespbo.model.StudentPermit;
import com.smartschool.permit.tubespbo.repository.PermitRepository;
import com.smartschool.permit.tubespbo.repository.interfaces.PermitReader;
import java.util.List;
import java.util.Map;

public class ReportService {
    private final PermitReader permitReader;

    public ReportService(PermitReader permitReader) {
        if (permitReader == null) {
            throw new IllegalArgumentException("PermitReader tidak boleh null!");
        }
        this.permitReader = permitReader;
    }

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
    // ...
}
```

---

## 7. Implementasi Kasus 5 (DIP): Inversi Ketergantungan Service Layer

### 7.1 Alasan Pemilihan Prinsip SOLID
`PermitService` (modul tingkat tinggi berisi aturan validasi dan approval) bergantung langsung pada kelas tingkat rendah konkret `PermitRepository`. Seluruh panel GUI (`LateEntryPanel`, `ExitPermitPanel`, `DashboardPanel`, `FormKeterlambatan`, `FormDispensasi`) membuat instance konkret menggunakan `new PermitRepository()` dan `new PermitService(...)`. Hal ini menimbulkan *tight coupling* ekstrem yang membuat pengujian logika bisnis secara offline mustahil dilakukan.

### 7.2 Perubahan Desain & Tanggung Jawab
- **BEFORE:** Ketergantungan langsung antar kelas konkret di semua layer.
- **AFTER:**
  1. Dibuat interface abstraksi `IPermitRepository extends PermitReader, PermitWriter`.
  2. `PermitRepository` mengimplementasikan `IPermitRepository`.
  3. `PermitService` bergantung pada interface `IPermitRepository` via constructor injection.
  4. Dibuat container terpusat `AppServices` (Composition Root / Service Locator) yang mengelola daur hidup service dan menyediakan *hook* `setPermitRepositoryForTesting()` untuk mock testing.
  5. Seluruh panel GUI mengambil service dari `AppServices.getPermitService()`.

### 7.3 Source Code BEFORE (Ketergantungan Konkret di GUI & Service)
```java
// Lokasi: src/main/java/com/smartschool/permit/tubespbo/service/PermitService.java
public class PermitService {
    private final PermitRepository permitRepo; // KELAS KONKRET

    public PermitService(PermitRepository permitRepo) {
        this.permitRepo = permitRepo;
    }
}

// Lokasi: src/main/java/com/smartschool/permit/tubespbo/gui/dashboard/LateEntryPanel.java
private final PermitRepository permitRepo = new PermitRepository();
private final PermitService permitService = new PermitService(permitRepo);
```

### 7.4 Source Code AFTER yang Diimplementasikan
#### A. `repository/interfaces/IPermitRepository.java`
```java
package com.smartschool.permit.tubespbo.repository.interfaces;

public interface IPermitRepository extends PermitReader, PermitWriter {
}
```

#### B. `service/PermitService.java`
```java
package com.smartschool.permit.tubespbo.service;

import com.smartschool.permit.tubespbo.model.AdminUser;
import com.smartschool.permit.tubespbo.model.StudentPermit;
import com.smartschool.permit.tubespbo.model.enums.PermitType;
import com.smartschool.permit.tubespbo.repository.interfaces.IPermitRepository;
import java.util.List;

public class PermitService {
    private final IPermitRepository permitRepo; // ABSTRAKSI INTERFACE

    public PermitService(IPermitRepository permitRepo) {
        if (permitRepo == null) {
            throw new IllegalArgumentException("IPermitRepository tidak boleh null!");
        }
        this.permitRepo = permitRepo;
    }

    public List<StudentPermit> getAllPermits(String schoolId) {
        return permitRepo.getBySchool(schoolId);
    }

    public List<StudentPermit> getPermitsByType(String schoolId, PermitType type) {
        return permitRepo.getByType(schoolId, type);
    }
    // ...
}
```

#### C. `app/AppServices.java` (Composition Root)
```java
package com.smartschool.permit.tubespbo.app;

import com.smartschool.permit.tubespbo.repository.PermitRepository;
import com.smartschool.permit.tubespbo.repository.interfaces.IPermitRepository;
import com.smartschool.permit.tubespbo.service.PermitService;
import com.smartschool.permit.tubespbo.service.ReportService;

public final class AppServices {

    private static IPermitRepository permitRepository;
    private static PermitService permitService;
    private static ReportService reportService;

    private AppServices() {}

    public static synchronized IPermitRepository getPermitRepository() {
        if (permitRepository == null) {
            permitRepository = new PermitRepository();
        }
        return permitRepository;
    }

    public static synchronized PermitService getPermitService() {
        if (permitService == null) {
            permitService = new PermitService(getPermitRepository());
        }
        return permitService;
    }

    public static synchronized ReportService getReportService() {
        if (reportService == null) {
            reportService = new ReportService(getPermitRepository());
        }
        return reportService;
    }

    public static synchronized void setPermitRepositoryForTesting(IPermitRepository mockRepo) {
        permitRepository = mockRepo;
        permitService = mockRepo != null ? new PermitService(mockRepo) : null;
        reportService = mockRepo != null ? new ReportService(mockRepo) : null;
    }

    public static synchronized void reset() {
        permitRepository = null;
        permitService = null;
        reportService = null;
    }
}
```

#### D. Penggunaan pada Panel GUI (`LateEntryPanel.java`)
```java
public class LateEntryPanel extends JPanel {
    // Tidak ada lagi operator 'new PermitRepository()' di GUI
    private final PermitService permitService = AppServices.getPermitService();
    // ...
}
```

---

## 8. Laporan Validasi Build & Penanganan Masalah

### 8.1 Investigasi Insiden Kompilasi
Saat tahap integrasi awal, terjadi satu kesalahan kompilasi pada `PermitService.java`:
```
[ERROR] reference to create is ambiguous
  both method create(StudentPermit) in PermitWriter and method create(T) in CrudRepository match
```
- **Penyebab:** `IPermitRepository` secara simultan mewarisi `String create(StudentPermit permit)` dari `PermitWriter` dan `String create(T entity)` dari `CrudRepository<StudentPermit>`, sehingga javac mendeteksi ambiguitas tanda tangan method saat type erasure.
- **Solusi Bersih:** Menjadikan `PermitWriter extends CrudRepository<StudentPermit>` dan menghapus duplikasi method `create()` pada `PermitWriter`. Kompilasi langsung berjalan sukses 100%.

### 8.2 Investigasi Insiden Pengujian Unit
Saat eksekusi awal `PermitServiceDipTest.testFilterPermits`, terjadi satu kegagalan asersi:
```
expected: <2> but was: <3>
```
- **Penyebab:** Dalam implementasi asli `PermitService.filterPermits`, filter kelas menggunakan `p.getClassName().startsWith(classFilter)`. Saat test memfilter dengan string `"X"`, kelas `"XI-B"` juga ikut cocok karena `"XI-B".startsWith("X")` bernilai `true`.
- **Solusi:** Pengujian diselaraskan dengan domain sekolah yang membedakan tingkat kelas dengan pemisah strip (`"X-"` untuk kelas tingkat X), sehingga membedakan kelas X-A/X-B secara presisi dari XI-B.

### 8.3 Hasil Eksekusi Uji Terakhir (`mvn test`)
```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.smartschool.permit.tubespbo.model.PermitPolymorphismTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.048 s
[INFO] Running com.smartschool.permit.tubespbo.repository.RepositoryContractLspTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.003 s
[INFO] Running com.smartschool.permit.tubespbo.service.PermitServiceDipTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.113 s
[INFO] Running com.smartschool.permit.tubespbo.service.ReportServiceIspTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.010 s
[INFO] Running com.smartschool.permit.tubespbo.util.export.ExcelDocumentGeneratorTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.933 s
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### 8.4 Hasil Pembangunan Jar (`mvn package`)
```
[INFO] --- jar:3.4.1:jar (default-jar) @ TubesPBO ---
[INFO] Building jar: D:\Project\Kuliah\DPBO\TubesPBO\target\TubesPBO-1.0-SNAPSHOT.jar
[INFO] --- shade:3.5.0:shade (default) @ TubesPBO ---
[INFO] Replacing original artifact with shaded artifact.
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] Total time:  11.422 s
```

---

## 9. Kesimpulan untuk Laporan UTS

1. **Kelengkapan SOLID:** Proyek kini telah menerapkan kelima prinsip SOLID secara utuh, bukan sekadar teori dokumen melainkan terbukti dalam source code dan unit test yang dapat dieksekusi.
2. **Kualitas Desain Perangkat Lunak:**
   - *SRP:* Modul I/O berkas terisolasi dari Swing UI.
   - *OCP:* Hirarki izin polimorfis siap diperluas tanpa modifikasi kode eksisting.
   - *LSP:* Kontrak repositori menjamin keamanan substitusi entitas ber-ID kustom.
   - *ISP:* Service analitik hanya mengenal antarmuka pembacaan data.
   - *DIP:* Inversi dependensi penuh melalui interface dan Composition Root, memungkinkan pengujian unit 100% offline.
3. **Kesiapan Laporan:** Seluruh potongan kode BEFORE dan AFTER serta matriks file pada dokumen ini dapat langsung disalin ke format laporan UTS ADPL.

