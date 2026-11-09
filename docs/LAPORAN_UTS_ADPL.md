# LAPORAN TUGAS UJIAN TENGAH SEMESTER (UTS)
## PRAKTIKUM ANALISIS DAN DESAIN PERANGKAT LUNAK (ADPL)

---

### **AUDIT DAN REFACTORING KODE BERBASIS PRINSIP SOLID PADA SISTEM INFORMASI PERIZINAN SISWA (SMARTSCHOOL PERMIT SYSTEM)**

```
Disusun oleh:
Nama Mahasiswa  : [Nama Mahasiswa]
Nomor Induk Mhs : [NIM Mahasiswa]
Kelas           : [Kelas / Rombel]
Program Studi   : [Program Studi]
Mata Kuliah     : Analisis dan Desain Perangkat Lunak (ADPL)
Dosen Pengampu  : [Nama Dosen Pengampu, Gelar]
```

**PROGRAM STUDI TEKNIK INFORMATIKA / ILMU KOMPUTER**  
**FAKULTAS ILMU KOMPUTER**  
**TAHUN AKADEMIK 2026/2027**

---

## DAFTAR ISI

- [HALAMAN JUDUL](#laporan-tugas-ujian-tengah-semester-uts)
- [BAB I PENDAHULUAN](#bab-i-pendahuluan)
  - [1.1 Latar Belakang](#11-latar-belakang)
  - [1.2 Tujuan](#12-tujuan)
  - [1.3 Deskripsi Singkat Project](#13-deskripsi-singkat-project)
- [BAB II LANDASAN TEORI](#bab-ii-landasan-teori)
  - [2.1 Code Review](#21-code-review)
  - [2.2 SOLID Principles](#22-solid-principles)
  - [2.3 Single Responsibility Principle (SRP)](#23-single-responsibility-principle-srp)
  - [2.4 Open/Closed Principle (OCP)](#24-openclosed-principle-ocp)
  - [2.5 Liskov Substitution Principle (LSP)](#25-liskov-substitution-principle-lsp)
  - [2.6 Interface Segregation Principle (ISP)](#26-interface-segregation-principle-isp)
  - [2.7 Dependency Inversion Principle (DIP)](#27-dependency-inversion-principle-dip)
- [BAB III HASIL REVIEW DAN PERBAIKAN KODE](#bab-iii-hasil-review-dan-perbaikan-kode)
  - [3.1 Kasus 1: Pemisahan Tanggung Jawab Ekspor Berkas Excel dari Antarmuka Grafis (SRP)](#31-kasus-1-pemisahan-tanggung-jawab-ekspor-berkas-excel-dari-antarmuka-grafis-srp)
  - [3.2 Kasus 2: Polimorfisme Kalkulasi Durasi Izin Siswa (OCP)](#32-kasus-2-polimorfisme-kalkulasi-durasi-izin-siswa-ocp)
  - [3.3 Kasus 3: Harmonisasi Kontrak Persistensi Entitas Ber-ID Kustom (LSP)](#33-kasus-3-harmonisasi-kontrak-persistensi-entitas-ber-id-kustom-lsp)
  - [3.4 Kasus 4: Segregasi Role Interface PermitReader untuk Modul Laporan (ISP)](#34-kasus-4-segregasi-role-interface-permitreader-untuk-modul-laporan-isp)
  - [3.5 Kasus 5: Inversi Ketergantungan Service Layer Melalui Abstraksi Repositori (DIP)](#35-kasus-5-inversi-ketergantungan-service-layer-melalui-abstraksi-repositori-dip)
- [BAB IV PENUTUP](#bab-iv-penutup)
  - [4.1 Kesimpulan](#41-kesimpulan)
  - [4.2 Saran Pengembangan](#42-saran-pengembangan)
- [LAMPIRAN](#lampiran)
  - [Lampiran 1: Struktur Direktori Proyek](#lampiran-1-struktur-direktori-proyek)
  - [Lampiran 2: Bukti Pengujian Unit & Packaging Maven](#lampiran-2-bukti-pengujian-unit--packaging-maven)
  - [Lampiran 3: Matriks Perubahan File](#lampiran-3-matriks-perubahan-file)

---

## BAB I PENDAHULUAN

### 1.1 Latar Belakang
Dalam rekayasa perangkat lunak berorientasi objek (*Object-Oriented Software Engineering*), penulisan source code yang semata-mata dapat berjalan (*working code*) belum menjamin keberlanjutan siklus hidup perangkat lunak. Tanpa penerapan prinsip desain yang disiplin, perangkat lunak cenderung mengalami fenomena *code rot* atau *software entropy*, yaitu kondisi di mana basis kode menjadi rapuh (*fragile*), kaku (*rigid*), dan sulit diuji (*untestable*) seiring bertambahnya fitur baru.

Salah satu pedoman utama dalam merancang sistem perangkat lunak berorientasi objek yang tangguh dan mudah dirawat adalah lima prinsip **SOLID** yang diperkenalkan oleh Robert C. Martin. Prinsip ini memberikan panduan terstruktur mengenai penempatan tanggung jawab kelas, keterbukaan sistem terhadap ekstensi, keabsahan pewarisan polimorfik, pemisahan antarmuka kontrak, serta pembalikan arah ketergantungan antar modul.

Melalui tugas Ujian Tengah Semester (UTS) mata kuliah **Analisis dan Desain Perangkat Lunak (ADPL)** ini, dilakukan audit teknis (*code review*) mendalam terhadap sistem informasi perizinan sekolah bernama **SmartSchool Permit System (TubesPBO)**. Dari hasil audit, ditemukan sejumlah kelemahan desain arsitektural di mana kelima prinsip SOLID belum diterapkan secara optimal. Laporan ini mendokumentasikan analisis masalah kode sumber asli (*BEFORE*), rancangan perbaikan berbasis desain berorientasi objek, implementasi refactoring aktual (*AFTER*), serta verifikasi empiris menggunakan pengujian unit otomatis (*unit testing*).

### 1.2 Tujuan
Tujuan dari pelaksanaan audit dan refactoring kode ini adalah:
1. Menganalisis dan mengidentifikasi pelanggaran prinsip-prinsip SOLID pada basis kode aktual proyek Java Swing.
2. Mempelajari dan menerapkan teknik *code refactoring* untuk meningkatkan kohesi (*high cohesion*), menurunkan keterikatan antar komponen (*loose coupling*), serta meningkatkan keterujian (*testability*).
3. Mendokumentasikan transformasi kode sebelum (*BEFORE*) dan sesudah (*AFTER*) perbaikan secara objektif dan sistematis.
4. Memvalidasi hasil refactoring menggunakan suite pengujian unit terotomatisasi (*automated unit testing*) untuk membuktikan bahwa perbaikan desain tidak menimbulkan regresi (*regression-free*).

### 1.3 Deskripsi Singkat Project
**SmartSchool Permit System (TubesPBO)** adalah aplikasi desktop berbasis **Java Swing** yang digunakan oleh pihak sekolah (studi kasus: SMAN 1 Rejotangan) untuk mengelola surat izin siswa. Sistem ini mencakup dua kategori izin utama:
1. **Late Entry (Izin Masuk Terlambat):** Pencatatan kedatangan siswa yang melampaui batas toleransi pagi (07:00 WIB) beserta kalkulasi menit keterlambatan.
2. **Exit Permit (Izin Keluar / Dispensasi):** Pengajuan izin meninggalkan sekolah pada jam pelajaran berlangsung dengan estimasi waktu kembali (*return timestamp*).

Aplikasi terhubung ke database **Google Cloud Firestore** sebagai media penyimpanan cloud terpusat dan memanfaatkan **Firebase Authentication** untuk manajemen otentikasi akun pengguna (Admin Piket, Super Admin, dan Siswa). Secara arsitektural, proyek terbagi menjadi empat lapisan utama: Lapisan Presentasi (`gui`), Lapisan Layanan Bisnis (`service`), Lapisan Akses Data (`repository`), dan Lapisan Domain Data (`model`).

---

## BAB II LANDASAN TEORI

### 2.1 Code Review
*Code review* adalah proses evaluasi sistematis terhadap kode sumber program yang dilakukan untuk menemukan kesalahan logika, mendeteksi pelanggaran arsitektur, memastikan kepatuhan standar pengkodean, serta meningkatkan kualitas pemeliharaan (*maintainability*) perangkat lunak. Dalam konteks desain perangkat lunak, *code review* difokuskan pada analisis struktur kelas, penempatan tanggung jawab, serta arah ketergantungan antar modul.

### 2.2 SOLID Principles
SOLID merupakan akronim dari lima prinsip desain perangkat lunak berorientasi objek yang dirumuskan oleh Robert C. Martin (Uncle Bob) pada awal tahun 2000-an:
- **S** – *Single Responsibility Principle* (SRP)
- **O** – *Open/Closed Principle* (OCP)
- **L** – *Liskov Substitution Principle* (LSP)
- **I** – *Interface Segregation Principle* (ISP)
- **D** – *Dependency Inversion Principle* (DIP)

Kelima prinsip ini saling melengkapi untuk menciptakan arsitektur perangkat lunak yang modular, adaptif terhadap perubahan kebutuhan bisnis, dan memiliki tingkat keterujian yang tinggi.

### 2.3 Single Responsibility Principle (SRP)
> *"A class should have one, and only one, reason to change."*

Prinsip Tanggung Jawab Tunggal menyatakan bahwa setiap kelas atau modul perangkat lunak harus bertanggung jawab terhadap satu fungsionalitas spesifik dari sistem. Jika sebuah kelas menangani lebih dari satu domain fungsional (misalnya mencampuradukkan logika manipulasi antarmuka GUI dengan logika pembentukan file biner dan operasi I/O), maka kelas tersebut memiliki banyak alasan untuk berubah (*multiple reasons to change*). Dampak negatifnya adalah tingginya risiko efek samping (*side effects*) ketika satu bagian diubah, serta sulitnya kelas tersebut diuji secara terisolasi.

### 2.4 Open/Closed Principle (OCP)
> *"Software entities (classes, modules, functions, etc.) should be open for extension, but closed for modification."*

Prinsip Terbuka/Tertutup menyatakan bahwa sebuah entitas perangkat lunak harus dirancang sedemikian rupa sehingga perilakunya dapat diperluas (*extended*) tanpa harus memodifikasi kode sumber aslinya (*closed for modification*). Prinsip ini umumnya diwujudkan melalui abstraksi, antarmuka (*interface*), dan polimorfisme (*inheritance/polymorphism*). Dengan mematuhi OCP, penambahan fitur atau varian baru tidak akan merusak kestabilan kode lama yang telah teruji (*regression risk* berkurang drastis).

### 2.5 Liskov Substitution Principle (LSP)
> *"Subtypes must be substitutable for their base types without altering the correctness of the program."*

Prinsip Substitusi Liskov diperkenalkan oleh Barbara Liskov pada tahun 1987. Prinsip ini menyatakan bahwa objek dari kelas turunan (*subclass*) harus dapat menggantikan objek dari kelas induk (*superclass*) tanpa mengubah kebenaran atau perilaku program yang diharapkan oleh klien. Subclass tidak boleh mempersempit aturan kontrak superclass, tidak boleh melanggar pra-kondisi (*pre-conditions*), dan tidak boleh menyebabkan efek samping yang tidak terduga pada pemanggilan polimorfik.

### 2.6 Interface Segregation Principle (ISP)
> *"Clients should not be forced to depend upon interfaces that they do not use."*

Prinsip Segregasi Antarmuka menyatakan bahwa antarmuka (*interface*) yang berukuran besar dan serba ada (*fat interface*) harus dipecah menjadi antarmuka-antarmuka yang lebih kecil dan spesifik berdasarkan peran (*role interfaces*). Klien yang hanya membutuhkan operasi pembacaan data tidak boleh dipaksa untuk mengimplementasikan atau bergantung pada antarmuka yang menyediakan operasi mutasi, penghapusan, atau transaksi data yang berpotensi membahayakan integritas sistem.

### 2.7 Dependency Inversion Principle (DIP)
> *"High-level modules should not depend on low-level modules. Both should depend on abstractions. Abstractions should not depend on details. Details should depend on abstractions."*

Prinsip Inversi Ketergantungan menyatakan bahwa modul tingkat tinggi (berisi aturan bisnis dan *use case* sistem) tidak boleh bergantung langsung pada modul tingkat rendah (berisi implementasi fisik database, antarmuka GUI, atau pustaka jaringan). Keduanya harus bergantung pada abstraksi (*interface*). Dengan menerapkan *Dependency Inversion* melalui teknik *Dependency Injection* (DI), pengujian unit dapat dilakukan secara terisolasi (*test-driven development*) dengan menyuntikkan objek tiruan (*mock objects*) tanpa membutuhkan infrastruktur produksi nyata.

---

## BAB III HASIL REVIEW DAN PERBAIKAN KODE

### 3.1 Kasus 1: Pemisahan Tanggung Jawab Ekspor Berkas Excel dari Antarmuka Grafis (SRP)

#### A. Identifikasi Masalah
- **Lokasi Berkas:** [`src/main/java/com/smartschool/permit/tubespbo/util/XlsxUtils.java`](file:///d:/Project/Kuliah/DPBO/TubesPBO/src/main/java/com/smartschool/permit/tubespbo/util/XlsxUtils.java#L16-L84)
- **Nama Kelas / Method:** `XlsxUtils.exportTable(JTable table, String defaultFileName)`
- **Prinsip SOLID Terkait:** **Single Responsibility Principle (SRP)**
- **Kondisi Sebelum Perbaikan:** Kelas utilitas ini memikul 5 tanggung jawab sekaligus dalam satu method statis, yaitu menangani dialog visual pemilihan file (`JFileChooser`), membaca struktur internal komponen Swing `JTable` dan `TableModel`, memformat sel dokumen menggunakan pustaka Apache POI, menulis aliran data byte ke media penyimpanan disk (`FileOutputStream`), serta memunculkan pop-up notifikasi dialog modal (`JOptionPane`).

#### B. BEFORE
```java
// Lokasi: com.smartschool.permit.tubespbo.util.XlsxUtils.java (Baris 16-84)
package com.smartschool.permit.tubespbo.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileOutputStream;

public class XlsxUtils {

    public static void exportTable(JTable table, String defaultFileName) {
        // [BAGIAN BERMASALAH 1: Interaksi UI Swing - Dialog Pemilihan File]
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

        // [BAGIAN BERMASALAH 2 & 3: Ketergantungan Model Swing TableModel & Logika Apache POI]
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Laporan");
            TableModel model = table.getModel();

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

            for (int col = 0; col < model.getColumnCount(); col++) {
                sheet.autoSizeColumn(col);
            }

            // [BAGIAN BERMASALAH 4 & 5: File I/O Lokal dan Dialog Pop-up Swing]
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

#### C. Analisis
- **Mengapa Melanggar SRP?** Kelas utilitas ini memiliki banyak alasan untuk berubah (*multiple reasons to change*): (1) jika alur antarmuka dialog file diubah, (2) jika model visual tabel diubah, (3) jika aturan pewarnaan/gaya sel Excel diubah, atau (4) jika target penyimpanan dialihkan ke *cloud storage* atau memori buffer.
- **Dampak Desain:** Kelas menjadi *tightly coupled* terhadap pustaka `javax.swing.*`. Akibatnya, method ini sama sekali tidak dapat diuji dalam pengujian unit terotomatisasi (*CI/CD headless pipeline*) karena pemanggilan `fileChooser.showSaveDialog(null)` akan melempar `java.awt.HeadlessException` di lingkungan server tanpa monitor. Selain itu, logika pembuatan dokumen Excel tidak dapat digunakan kembali (*not reusable*) untuk data selain `JTable`.

#### D. AFTER
Dilakukan pemisahan kelas menjadi komponen utilitas dokumen mandiri dan komponen pembantu presentasi antarmuka pengguna:

**1. Generator Dokumen Murni:** [`src/main/java/com/smartschool/permit/tubespbo/util/export/ExcelDocumentGenerator.java`](file:///d:/Project/Kuliah/DPBO/TubesPBO/src/main/java/com/smartschool/permit/tubespbo/util/export/ExcelDocumentGenerator.java)
```java
package com.smartschool.permit.tubespbo.util.export;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Komponen murni pembuat berkas Excel (Apache POI).
 * Sepenuhnya independen dari pustaka UI Swing.
 */
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

**2. Helper Presentasi Antarmuka GUI:** [`src/main/java/com/smartschool/permit/tubespbo/gui/helper/SwingExportHelper.java`](file:///d:/Project/Kuliah/DPBO/TubesPBO/src/main/java/com/smartschool/permit/tubespbo/gui/helper/SwingExportHelper.java)
```java
package com.smartschool.permit.tubespbo.gui.helper;

import com.smartschool.permit.tubespbo.util.export.ExcelDocumentGenerator;
import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public final class SwingExportHelper {

    private SwingExportHelper() {}

    public static void exportTableWithDialog(Component parent, JTable table, String defaultFileName) {
        if (table == null) {
            JOptionPane.showMessageDialog(parent, "Tabel data kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan Excel");
        fileChooser.setSelectedFile(new File(defaultFileName + ".xlsx"));

        if (fileChooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File targetFile = fileChooser.getSelectedFile();
        if (!targetFile.getName().toLowerCase().endsWith(".xlsx")) {
            targetFile = new File(targetFile.getAbsolutePath() + ".xlsx");
        }

        try {
            TableModel model = table.getModel();
            List<String> headers = new ArrayList<>();
            for (int c = 0; c < model.getColumnCount(); c++) {
                headers.add(model.getColumnName(c));
            }

            List<List<Object>> rows = new ArrayList<>();
            for (int r = 0; r < model.getRowCount(); r++) {
                List<Object> rowData = new ArrayList<>();
                for (int c = 0; c < model.getColumnCount(); c++) {
                    rowData.add(model.getValueAt(r, c));
                }
                rows.add(rowData);
            }

            try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                ExcelDocumentGenerator.generate(headers, rows, fos);
            }

            JOptionPane.showMessageDialog(parent, 
                    "Laporan berhasil diexport ke:\n" + targetFile.getAbsolutePath(), 
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent, 
                    "Gagal export Excel: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
```

**3. Kompatibilitas Facade:** [`src/main/java/com/smartschool/permit/tubespbo/util/XlsxUtils.java`](file:///d:/Project/Kuliah/DPBO/TubesPBO/src/main/java/com/smartschool/permit/tubespbo/util/XlsxUtils.java)
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

#### E. Hasil Perbaikan
- **Perbandingan BEFORE vs AFTER:** Sebelum refactoring, logika styling Apache POI bercampur dengan `JFileChooser` dan `JOptionPane`. Sesudah refactoring, `ExcelDocumentGenerator` murni menerima struktur data umum dan output stream tanpa ketergantungan GUI.
- **Manfaat:** Pembangkitan file Excel kini 100% dapat diuji secara otomatis (*unit-testable*) menggunakan unit test [`ExcelDocumentGeneratorTest`](file:///d:/Project/Kuliah/DPBO/TubesPBO/src/test/java/com/smartschool/permit/tubespbo/util/export/ExcelDocumentGeneratorTest.java) tanpa memerlukan tampilan desktop.

---

### 3.2 Kasus 2: Polimorfisme Kalkulasi Durasi Izin Siswa (OCP)

#### A. Identifikasi Masalah
- **Lokasi Berkas:** [`src/main/java/com/smartschool/permit/tubespbo/model/StudentPermit.java`](file:///d:/Project/Kuliah/DPBO/TubesPBO/src/main/java/com/smartschool/permit/tubespbo/model/StudentPermit.java#L52-L70, #L87-L93)
- **Nama Kelas / Method:** `StudentPermit.getDurationMinutes()` dan `StudentPermit.toMap()`
- **Prinsip SOLID Terkait:** **Open/Closed Principle (OCP)**
- **Kondisi Sebelum Perbaikan:** Entitas `StudentPermit` menggabungkan dua varian entitas izin yang berbeda (izin masuk terlambat dan izin keluar sekolah) ke dalam satu kelas monolitik. Perhitungan durasi menggunakan percabangan tipe `if (isLateEntry()) ... else if (isExitPermit())` dengan konstanta waktu *hardcoded* (07:00 pagi untuk terlambat dan 15:00 sore untuk izin keluar).

#### B. BEFORE
```java
// Lokasi: com.smartschool.permit.tubespbo.model.StudentPermit.java (Baris 52-70, 87-93)
public int getDurationMinutes() {
    ZoneId zone = ZoneId.of("Asia/Jakarta");
    ZonedDateTime zdt = Instant.ofEpochMilli(timestamp).atZone(zone);
    
    // [BAGIAN BERMASALAH: Percabangan bertingkat berbasis tipe izin]
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

@Override
public Map<String, Object> toMap() {
    Map<String, Object> map = new HashMap<>();
    // ...
    // [BAGIAN BERMASALAH: Pemetaan atribut spesifik dengan pengecekan manual]
    if (isLateEntry()) {
        if (arrivalTimestamp > 0) map.put("arrivalTimestamp", arrivalTimestamp);
    } else if (isExitPermit()) {
        if (returnTimestamp > 0) map.put("returnTimestamp", returnTimestamp);
        if (exitTimestamp > 0) map.put("exitTimestamp", exitTimestamp);
    }
    // ...
    return map;
}
```

#### C. Analisis
- **Mengapa Melanggar OCP?** Apabila sistem ingin menambahkan jenis perizinan baru, misalnya `SICK_LEAVE` (izin sakit yang memerlukan surat dokter dan durasi harian) atau `DISPENSATION_ACTIVITY` (izin dispensasi perlombaan), pengembang dipaksa membuka kembali (*modifying*) kelas `StudentPermit`, menambah percabangan `else if` baru pada `getDurationMinutes()`, serta menambahkan atribut-atribut baru pada kelas yang sama.
- **Dampak Desain:** Kerapuhan kode (*fragility*) meningkat karena pengeditan kelas inti yang sudah stabil berisiko merusak fungsionalitas izin lama yang telah berjalan dengan benar.

#### D. AFTER
Diterapkan teknik refactoring **"Replace Conditional with Polymorphism"**:

**1. Subclass Izin Terlambat:** [`src/main/java/com/smartschool/permit/tubespbo/model/LateEntryPermit.java`](file:///d:/Project/Kuliah/DPBO/TubesPBO/src/main/java/com/smartschool/permit/tubespbo/model/LateEntryPermit.java)
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

**2. Subclass Izin Keluar:** [`src/main/java/com/smartschool/permit/tubespbo/model/ExitPermit.java`](file:///d:/Project/Kuliah/DPBO/TubesPBO/src/main/java/com/smartschool/permit/tubespbo/model/ExitPermit.java)
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

**3. Integrasi Factory pada Repositori:** [`src/main/java/com/smartschool/permit/tubespbo/repository/PermitRepository.java`](file:///d:/Project/Kuliah/DPBO/TubesPBO/src/main/java/com/smartschool/permit/tubespbo/repository/PermitRepository.java#L37-L50)
```java
@Override
protected StudentPermit toEntity(DocumentSnapshot doc) {
    Map<String, Object> data = doc.getData();
    StudentPermit permit;
    String typeStr = data != null ? (String) data.get("type") : null;
    if ("LATE_ENTRY".equals(typeStr)) {
        permit = new LateEntryPermit();
    } else if ("EXIT_PERMIT".equals(typeStr)) {
        permit = new ExitPermit();
    } else {
        permit = new StudentPermit();
    }
    permit.fromMap(data);
    permit.setId(doc.getId());
    return permit;
}
```

#### E. Hasil Perbaikan
- **Perbandingan BEFORE vs AFTER:** Sebelum refactoring, logika kalkulasi durasi bertumpuk dalam satu method dengan `if-else`. Sesudah refactoring, masing-masing varian izin mengkapsulasi logika kalkulasinya sendiri melalui *dynamic method dispatch*.
- **Manfaat:** Sistem kini mematuhi OCP (*open for extension, closed for modification*). Penambahan izin jenis baru cukup dilakukan dengan membuat kelas baru yang meng-extend `StudentPermit`, tanpa perlu menyentuh atau memodifikasi kelas yang sudah ada.

---

### 3.3 Kasus 3: Harmonisasi Kontrak Persistensi Entitas Ber-ID Kustom (LSP)

#### A. Identifikasi Masalah
- **Lokasi Berkas:** [`src/main/java/com/smartschool/permit/tubespbo/repository/BaseRepository.java`](file:///d:/Project/Kuliah/DPBO/TubesPBO/src/main/java/com/smartschool/permit/tubespbo/repository/BaseRepository.java#L65-L73) dan [`src/main/java/com/smartschool/permit/tubespbo/repository/StudentRepository.java`](file:///d:/Project/Kuliah/DPBO/TubesPBO/src/main/java/com/smartschool/permit/tubespbo/repository/StudentRepository.java#L34-L41)
- **Nama Kelas / Method:** `BaseRepository.create(T entity)` dan `StudentRepository.createWithId(String uid, Student student)`
- **Prinsip SOLID Terkait:** **Liskov Substitution Principle (LSP)**
- **Kondisi Sebelum Perbaikan:** Superclass `BaseRepository.create(T)` menetapkan asumsi bahwa ID dokumen Firestore selalu dibuat otomatis secara acak menggunakan `.add(...)`. Di sisi lain, entitas `Student` mewajibkan ID dokumen Firestore sama persis dengan `uid` dari Firebase Authentication agar proses login siswa dapat menemukan data profilnya. Karena method `create()` bawaan superclass tidak dapat digunakan secara benar untuk entitas `Student`, developer membuat method baru `createWithId()`, tetapi membiarkan method warisan `create()` tetap ada dan berpotensi merusak integritas akun siswa jika dipanggil melalui referensi polimorfik.

#### B. BEFORE
```java
// Lokasi: com.smartschool.permit.tubespbo.repository.BaseRepository.java (Baris 65-73)
@Override
public String create(T entity) {
    try {
        // [BAGIAN BERMASALAH: Asumsi sepihak bahwa ID selalu di-generate acak]
        ApiFuture<DocumentReference> future = db.collection(collectionName).add(entity.toMap());
        return future.get().getId();
    } catch (Exception e) {
        System.err.println("Error creating document: " + e.getMessage());
        return null;
    }
}

// Lokasi: com.smartschool.permit.tubespbo.repository.StudentRepository.java (Baris 30-41)
/**
 * Create student document with specific ID (Firebase Auth UID).
 * Berbeda dengan create() bawaan yang auto-generate ID.
 */
public void createWithId(String uid, Student student) {
    try {
        db.collection(collectionName).document(uid).set(student.toMap()).get();
    } catch (Exception e) {
        System.err.println("Error creating student with ID: " + e.getMessage());
        throw new RuntimeException("Gagal menyimpan data siswa ke database!", e);
    }
}
```

#### C. Analisis
- **Mengapa Melanggar LSP?** Menurut LSP, subclass harus dapat menggantikan peran superclass secara aman. Apabila klien memperlakukan `StudentRepository` sebagai abstraksi `CrudRepository<Student>` atau `BaseRepository<Student>` dan memanggil `repo.create(student)`, kode akan berjalan tanpa pesan kesalahan, namun data siswa tersimpan dengan ID acak di Firestore. Akibatnya, saat siswa tersebut login menggunakan akun Firebase Auth, pemanggilan `studentRepo.getByUid(uid)` tidak akan pernah menemukan data siswa tersebut (akun siswa menjadi *broken/orphaned*). Subclass gagal memenuhi pra-kondisi dan ekspektasi perilaku superclass.
- **Dampak Desain:** Melanggar jaminan polimorfisme, membingungkan developer yang menggunakan repositori secara polimorfik, dan menciptakan inkonsistensi data transaksi.

#### D. AFTER
Kontrak method `create(T entity)` pada `BaseRepository` diperluas dan diselaraskan agar mengenali entitas yang telah memiliki ID eksplisit:

**1. Pembaruan Superclass:** [`src/main/java/com/smartschool/permit/tubespbo/repository/BaseRepository.java`](file:///d:/Project/Kuliah/DPBO/TubesPBO/src/main/java/com/smartschool/permit/tubespbo/repository/BaseRepository.java#L68-L88)
```java
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

**2. Pembaruan Subclass:** [`src/main/java/com/smartschool/permit/tubespbo/repository/StudentRepository.java`](file:///d:/Project/Kuliah/DPBO/TubesPBO/src/main/java/com/smartschool/permit/tubespbo/repository/StudentRepository.java#L30-L45)
```java
public void createWithId(String uid, Student student) {
    if (student == null) {
        throw new IllegalArgumentException("Data siswa tidak boleh null!");
    }
    student.setId(uid);
    String createdId = create(student); // Memanggil method superclass yang sudah patuh LSP
    if (createdId == null) {
        throw new RuntimeException("Gagal menyimpan data siswa ke database!");
    }
}
```

#### E. Hasil Perbaikan
- **Perbandingan BEFORE vs AFTER:** Sebelum refactoring, pemanggilan `create(student)` via interface parent merusak mapping database. Sesudah refactoring, `BaseRepository.create()` secara cerdas membedakan entitas dengan ID eksplisit maupun auto-generate.
- **Manfaat:** Subclass `StudentRepository` kini sepenuhnya dapat disubstitusikan untuk superclass `BaseRepository<Student>` atau interface `CrudRepository<Student>` secara aman tanpa merusak integritas akun pengguna (*LSP Compliant*).

---

### 3.4 Kasus 4: Segregasi Role Interface PermitReader untuk Modul Laporan (ISP)

#### A. Identifikasi Masalah
- **Lokasi Berkas:** [`src/main/java/com/smartschool/permit/tubespbo/service/ReportService.java`](file:///d:/Project/Kuliah/DPBO/TubesPBO/src/main/java/com/smartschool/permit/tubespbo/service/ReportService.java#L22-L27) dan [`src/main/java/com/smartschool/permit/tubespbo/repository/CrudRepository.java`](file:///d:/Project/Kuliah/DPBO/TubesPBO/src/main/java/com/smartschool/permit/tubespbo/repository/CrudRepository.java#L13-L19)
- **Nama Kelas / Method:** `ReportService(PermitRepository permitRepo)`
- **Prinsip SOLID Terkait:** **Interface Segregation Principle (ISP)**
- **Kondisi Sebelum Perbaikan:** `ReportService` adalah service yang murni bertugas menyusun analitik statistik, rekap bulanan, dan ringkasan siswa (murni pembacaan data). Namun kelas ini dihubungkan langsung ke kelas repositori penuh yang menyediakan fungsionalitas penambahan (`create`), pengubahan (`update`, `approvePermit`), dan penghapusan data (`delete`).

#### B. BEFORE
```java
// Lokasi: com.smartschool.permit.tubespbo.repository.CrudRepository.java (Baris 13-19)
// [FAT INTERFACE: Operasi pembacaan dan mutasi dicampur aduk]
public interface CrudRepository<T> {
    List<T> getAll();
    T getById(String id);
    String create(T entity);
    void update(String id, Map<String, Object> data);
    void delete(String id);
}

// Lokasi: com.smartschool.permit.tubespbo.service.ReportService.java (Baris 22-27)
public class ReportService {
    // [BAGIAN BERMASALAH: Menginjeksi repositori penuh yang memiliki kapabilitas mutasi/delete]
    private final PermitRepository permitRepo;

    public ReportService(PermitRepository permitRepo) {
        this.permitRepo = permitRepo;
    }

    public StatisticsData getDashboardStats(String schoolId) {
        List<StudentPermit> permits = permitRepo.getBySchool(schoolId);
        StatisticsData stats = new StatisticsData();
        stats.calculate(permits);
        return stats;
    }
    // ...
}
```

#### C. Analisis
- **Mengapa Melanggar ISP?** Sesuai prinsip ISP, sebuah modul tidak boleh dipaksa bergantung pada antarmuka atau metode yang tidak digunakannya. `ReportService` hanya memerlukan operasi *read-only* untuk menarik data izin sekolah. Mengikatnya pada antarmuka repositori yang memiliki method `delete()` dan `update()` melanggar prinsip *Least Privilege* pada tingkat arsitektur.
- **Dampak Desain:** Keterikatan komponen yang tidak perlu (*unnecessary coupling*) dan terbukanya risiko pemanggilan method mutasi secara tidak sengaja dari dalam service pelaporan.

#### D. AFTER
Dilakukan segregasi antarmuka repositori menjadi antarmuka peran (*role interfaces*):

**1. Role Interface Khusus Pembaca:** [`src/main/java/com/smartschool/permit/tubespbo/repository/interfaces/PermitReader.java`](file:///d:/Project/Kuliah/DPBO/TubesPBO/src/main/java/com/smartschool/permit/tubespbo/repository/interfaces/PermitReader.java)
```java
package com.smartschool.permit.tubespbo.repository.interfaces;

import com.smartschool.permit.tubespbo.model.StudentPermit;
import com.smartschool.permit.tubespbo.model.enums.PermitType;
import java.util.List;

/**
 * Role Interface khusus operasi pembacaan data izin (Read-Only).
 */
public interface PermitReader {
    List<StudentPermit> getBySchool(String schoolId);
    List<StudentPermit> getByType(String schoolId, PermitType type);
    StudentPermit getById(String id);
}
```

**2. Role Interface Khusus Penulis/Mutasi:** [`src/main/java/com/smartschool/permit/tubespbo/repository/interfaces/PermitWriter.java`](file:///d:/Project/Kuliah/DPBO/TubesPBO/src/main/java/com/smartschool/permit/tubespbo/repository/interfaces/PermitWriter.java)
```java
package com.smartschool.permit.tubespbo.repository.interfaces;

import com.smartschool.permit.tubespbo.model.StudentPermit;
import com.smartschool.permit.tubespbo.repository.CrudRepository;
import java.util.Map;

/**
 * Role Interface khusus operasi mutasi dan manipulasi data izin.
 */
public interface PermitWriter extends CrudRepository<StudentPermit> {
    void approvePermit(String permitId, Map<String, Object> approvalData);
}
```

**3. Pembaruan pada `ReportService.java`:** [`src/main/java/com/smartschool/permit/tubespbo/service/ReportService.java`](file:///d:/Project/Kuliah/DPBO/TubesPBO/src/main/java/com/smartschool/permit/tubespbo/service/ReportService.java#L23-L45)
```java
public class ReportService {
    // Bergantung secara spesifik hanya pada PermitReader (ISP Compliant)
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

#### E. Hasil Perbaikan
- **Perbandingan BEFORE vs AFTER:** Sebelum refactoring, `ReportService` bergantung pada kelas konkret repositori yang memiliki akses mutasi data. Sesudah refactoring, `ReportService` hanya mengenal abstraksi peran `PermitReader`.
- **Manfaat:** Keamanan arsitektur terjamin karena modul laporan terlindungi secara struktural dari kemungkinan salah panggil method mutasi atau hapus data. Pengujian unit `ReportService` menjadi sangat mudah dilakukan dengan membuat stub `PermitReader` tanpa perlu menyediakan operasi database tulis.

---

### 3.5 Kasus 5: Inversi Ketergantungan Service Layer Melalui Abstraksi Repositori (DIP)

#### A. Identifikasi Masalah
- **Lokasi Berkas:** [`src/main/java/com/smartschool/permit/tubespbo/service/PermitService.java`](file:///d:/Project/Kuliah/DPBO/TubesPBO/src/main/java/com/smartschool/permit/tubespbo/service/PermitService.java#L21-L26), [`src/main/java/com/smartschool/permit/tubespbo/gui/dashboard/LateEntryPanel.java`](file:///d:/Project/Kuliah/DPBO/TubesPBO/src/main/java/com/smartschool/permit/tubespbo/gui/dashboard/LateEntryPanel.java#L28-L29), dan [`src/main/java/com/smartschool/permit/tubespbo/gui/formDispen/FormKeterlambatan.java`](file:///d:/Project/Kuliah/DPBO/TubesPBO/src/main/java/com/smartschool/permit/tubespbo/gui/formDispen/FormKeterlambatan.java#L337-L338)
- **Nama Kelas / Method:** `PermitService(PermitRepository permitRepo)`
- **Prinsip SOLID Terkait:** **Dependency Inversion Principle (DIP)**
- **Kondisi Sebelum Perbaikan:** Modul logika bisnis tingkat tinggi `PermitService` bergantung langsung pada modul tingkat rendah konkret `PermitRepository`. Terlebih lagi, lapisan presentasi antarmuka GUI (`LateEntryPanel`, `ExitPermitPanel`, `DashboardPanel`, `FormKeterlambatan`, `FormDispensasi`) langsung menginisialisasi repositori dan service konkret menggunakan operator `new PermitRepository()` di dalam kelas visual Swing.

#### B. BEFORE
```java
// Lokasi: com.smartschool.permit.tubespbo.service.PermitService.java (Baris 21-26)
public class PermitService {
    // [BAGIAN BERMASALAH: Bergantung langsung ke kelas konkret low-level I/O]
    private final PermitRepository permitRepo;

    public PermitService(PermitRepository permitRepo) {
        this.permitRepo = permitRepo;
    }
    // ...
}

// Lokasi: com.smartschool.permit.tubespbo.gui.dashboard.LateEntryPanel.java (Baris 28-29)
// [BAGIAN BERMASALAH: Komponen GUI melakukan instansiasi langsung dependensi backend]
private final PermitRepository permitRepo = new PermitRepository();
private final PermitService permitService = new PermitService(permitRepo);
```

#### C. Analisis
- **Mengapa Melanggar DIP?** Modul tingkat tinggi (`PermitService`) dan modul presentasi GUI tidak boleh bergantung pada kelas implementasi konkret. Keduanya harus bergantung pada abstraksi (*interface*). Ketergantungan langsung ke `PermitRepository` mengikat logika bisnis ke infrastruktur fisik Google Cloud Firestore.
- **Dampak Desain:** Tidak memungkinkan dilakukannya pengujian unit otomatis (*unit testing*) pada logika persetujuan atau validasi izin tanpa adanya koneksi internet aktif ke server Google Firestore. Mengubah penyedia persistensi (misal ke MySQL atau PostgreSQL) akan mengharuskan pengeditan belasan berkas GUI.

#### D. AFTER
Dibuat antarmuka abstraksi repositori dan kontainer penyedia dependensi (*Composition Root / Dependency Injection*):

**1. Interface Abstraksi Repositori:** [`src/main/java/com/smartschool/permit/tubespbo/repository/interfaces/IPermitRepository.java`](file:///d:/Project/Kuliah/DPBO/TubesPBO/src/main/java/com/smartschool/permit/tubespbo/repository/interfaces/IPermitRepository.java)
```java
package com.smartschool.permit.tubespbo.repository.interfaces;

/**
 * Abstraksi utama repositori izin siswa (DIP & ISP).
 */
public interface IPermitRepository extends PermitReader, PermitWriter {
}
```

**2. Pembaruan `PermitService.java` (Injeksi Interface):** [`src/main/java/com/smartschool/permit/tubespbo/service/PermitService.java`](file:///d:/Project/Kuliah/DPBO/TubesPBO/src/main/java/com/smartschool/permit/tubespbo/service/PermitService.java#L23-L45)
```java
public class PermitService {
    // Inversi Ketergantungan: bergantung pada Interface abstraksi
    private final IPermitRepository permitRepo;

    public PermitService(IPermitRepository permitRepo) {
        if (permitRepo == null) {
            throw new IllegalArgumentException("IPermitRepository tidak boleh null!");
        }
        this.permitRepo = permitRepo;
    }

    @Deprecated
    public PermitService(PermitRepository permitRepo) {
        this((IPermitRepository) permitRepo);
    }
    // ...
}
```

**3. Kontainer Terpusat (Composition Root):** [`src/main/java/com/smartschool/permit/tubespbo/app/AppServices.java`](file:///d:/Project/Kuliah/DPBO/TubesPBO/src/main/java/com/smartschool/permit/tubespbo/app/AppServices.java)
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

    // Hook pengujian: menyuntikkan Mock Repository saat Unit Testing
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

**4. Penggunaan Bersih pada Komponen GUI:** [`src/main/java/com/smartschool/permit/tubespbo/gui/dashboard/LateEntryPanel.java`](file:///d:/Project/Kuliah/DPBO/TubesPBO/src/main/java/com/smartschool/permit/tubespbo/gui/dashboard/LateEntryPanel.java#L28)
```java
public class LateEntryPanel extends JPanel {
    // Lapisan GUI bebas dari operator 'new PermitRepository()'
    private final PermitService permitService = AppServices.getPermitService();
    // ...
}
```

#### E. Hasil Perbaikan
- **Perbandingan BEFORE vs AFTER:** Sebelum refactoring, `PermitService` terikat permanen ke `PermitRepository` dan GUI melakukan *hardcoded instantiation*. Sesudah refactoring, service layer bergantung pada interface `IPermitRepository`, dan GUI mendapatkan instance melalui kontainer `AppServices`.
- **Manfaat:** Tingkat *testability* meningkat drastis. Seluruh aturan alur persetujuan (*approval logic*) dan pembuatan surat izin dapat diuji 100% secara offline tanpa internet menggunakan objek `MockPermitRepository` in-memory pada test suite [`PermitServiceDipTest`](file:///d:/Project/Kuliah/DPBO/TubesPBO/src/test/java/com/smartschool/permit/tubespbo/service/PermitServiceDipTest.java).

---

## BAB IV PENUTUP

### 4.1 Kesimpulan
Berdasarkan hasil analisis, desain refactoring, implementasi aktual, dan pengujian empiris yang telah dilakukan, dapat diambil beberapa kesimpulan pokok:
1. Basis kode sistem **SmartSchool Permit System** berhasil diaudit dan ditemukan memiliki kelemahan desain arsitektur pada seluruh prinsip SOLID (SRP, OCP, LSP, ISP, dan DIP).
2. Refactoring telah dieksekusi secara nyata pada 5 kasus unggulan dengan dampak terukur:
   - **SRP:** Terwujud pemisahan tegas antara logika pembuatan dokumen POI Excel (`ExcelDocumentGenerator`) dan antarmuka interaksi pengguna Swing (`SwingExportHelper`).
   - **OCP:** Struktur kalkulasi durasi izin bertransformasi menjadi hierarki kelas polimorfik (`LateEntryPermit` dan `ExitPermit`) yang terbuka untuk penambahan jenis izin baru tanpa memodifikasi kode lama.
   - **LSP:** Kontrak penyimpanan entitas pada `BaseRepository` berhasil diselaraskan sehingga subclass `StudentRepository` dapat menggantikan parent class secara aman tanpa merusak integritas ID otentikasi akun siswa.
   - **ISP:** Lapisan analitik laporan berhasil diisolasi menggunakan role interface `PermitReader`, mencegah eksposur method mutasi/hapus data yang tidak relevan.
   - **DIP:** Lapisan layanan bisnis berhasil didekopling dari Firestore menggunakan interface `IPermitRepository` dan kontainer `AppServices`.
3. Keberhasilan implementasi dibuktikan secara empiris dengan kelulusan **12 dari 12 unit test otomatis (100% Passed)** dan keberhasilan pembuatan executable jar via Maven tanpa ada regresi sistem.

### 4.2 Saran Pengembangan
Untuk pengembangan arsitektur di masa mendatang (*future work*), disarankan:
1. **Adopsi Dependency Injection Framework Penuh:** Mengintegrasikan framework IoC/DI standar industri seperti *Spring Framework* atau *Google Guice* untuk menggantikan service locator sederhana pada skala aplikasi yang lebih besar.
2. **Penyelarasan DTO dan Entity Mapper:** Memisahkan representasi dokumen database Firestore dari entity domain menggunakan pola *Data Transfer Object (DTO)* dan *Data Mapper*, sehingga kelas domain tidak perlu memiliki method `toMap()` dan `fromMap()`.
3. **Pemisahan Presentation Logic pada Panel Siswa:** Memindahkan sisa logika analitik data siswa yang masih berada di dalam `StudentDataPanel` ke dalam method spesifik di `ReportService`.

---

## LAMPIRAN

### Lampiran 1: Struktur Direktori Proyek
```
com.smartschool.permit.tubespbo
├── TubesPBO.java
├── app
│   ├── AppServices.java            <-- [BARU: DIP Composition Root]
│   ├── FirestoreConnection.java
│   ├── MainApp.java
│   ├── TestCRUD.java
│   └── UserSession.java
├── gui
│   ├── dashboard
│   │   ├── AdminPanel.java
│   │   ├── BlankPanel.java
│   │   ├── DashboardPanel.java     <-- [DIP refactored]
│   │   ├── DashboardUtama.java
│   │   ├── ExitPermitPanel.java    <-- [DIP refactored]
│   │   ├── LateEntryPanel.java     <-- [DIP refactored]
│   │   ├── ReportPanel.java        <-- [DIP & SRP refactored]
│   │   ├── StudentDataPanel.java
│   │   └── StudentPermitHistoryFrame.java
│   ├── formDispen
│   │   ├── FormDispensasi.java     <-- [OCP & DIP refactored]
│   │   └── FormKeterlambatan.java  <-- [OCP & DIP refactored]
│   ├── helper
│   │   └── SwingExportHelper.java  <-- [BARU: SRP UI Presentation Helper]
│   ├── login
│   │   ├── LoginFrame.java
│   │   ├── Main.java
│   │   └── StudentRegisterFrame.java
│   └── widget
│       └── SidebarPanel.java
├── model
│   ├── AdminUser.java
│   ├── BaseModel.java
│   ├── ExitPermit.java             <-- [BARU: OCP Polymorphic Subclass]
│   ├── Exportable.java
│   ├── Filterable.java
│   ├── LateEntryPermit.java        <-- [BARU: OCP Polymorphic Subclass]
│   ├── PermitSummary.java
│   ├── SchoolProfile.java
│   ├── StatisticsData.java
│   ├── Student.java
│   ├── StudentPermit.java
│   └── enums
│       ├── PermitStatus.java
│       ├── PermitType.java
│       └── UserRole.java
├── repository
│   ├── AdminRepository.java
│   ├── BaseRepository.java         <-- [LSP refactored]
│   ├── CrudRepository.java
│   ├── PermitRepository.java       <-- [DIP, ISP, OCP refactored]
│   ├── StudentRepository.java      <-- [LSP refactored]
│   └── interfaces
│       ├── IPermitRepository.java  <-- [BARU: DIP & ISP Main Abstraction]
│       ├── PermitReader.java       <-- [BARU: ISP Role Interface Read]
│       └── PermitWriter.java       <-- [BARU: ISP Role Interface Write]
├── service
│   ├── AdminService.java
│   ├── AuthService.java
│   ├── PermitService.java          <-- [DIP refactored]
│   ├── ReportService.java          <-- [ISP refactored]
│   └── StudentAuthService.java
└── util
    ├── DateUtils.java
    ├── SchoolUtils.java
    ├── XlsxUtils.java              <-- [SRP refactored]
    └── export
        └── ExcelDocumentGenerator.java <-- [BARU: SRP Pure POI Generator]
```

### Lampiran 2: Bukti Pengujian Unit & Packaging Maven

#### Log Eksekusi Unit Test (`mvn test`):
```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.smartschool.permit.tubespbo.model.PermitPolymorphismTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.048 s -- in com.smartschool.permit.tubespbo.model.PermitPolymorphismTest
[INFO] Running com.smartschool.permit.tubespbo.repository.RepositoryContractLspTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.003 s -- in com.smartschool.permit.tubespbo.repository.RepositoryContractLspTest
[INFO] Running com.smartschool.permit.tubespbo.service.PermitServiceDipTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.113 s -- in com.smartschool.permit.tubespbo.service.PermitServiceDipTest
[INFO] Running com.smartschool.permit.tubespbo.service.ReportServiceIspTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.010 s -- in com.smartschool.permit.tubespbo.service.ReportServiceIspTest
[INFO] Running com.smartschool.permit.tubespbo.util.export.ExcelDocumentGeneratorTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.933 s -- in com.smartschool.permit.tubespbo.util.export.ExcelDocumentGeneratorTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  5.174 s
[INFO] Finished at: 2026-11-04T14:16:55+07:00
[INFO] ------------------------------------------------------------------------
```

#### Log Pembangunan Distribusi JAR (`mvn package`):
```
[INFO] --- jar:3.4.1:jar (default-jar) @ TubesPBO ---
[INFO] Building jar: D:\Project\Kuliah\DPBO\TubesPBO\target\TubesPBO-1.0-SNAPSHOT.jar
[INFO] --- shade:3.5.0:shade (default) @ TubesPBO ---
[INFO] Replacing original artifact with shaded artifact.
[INFO] Replacing D:\Project\Kuliah\DPBO\TubesPBO\target\TubesPBO-1.0-SNAPSHOT.jar with D:\Project\Kuliah\DPBO\TubesPBO\target\TubesPBO-1.0-SNAPSHOT-shaded.jar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  11.422 s
[INFO] Finished at: 2026-11-04T14:17:11+07:00
[INFO] ------------------------------------------------------------------------
```

### Lampiran 3: Matriks Perubahan File
| Nama Berkas | Sebelum Refactoring | Sesudah Refactoring | Status Fungsional |
| :--- | :--- | :--- | :---: |
| `util/XlsxUtils.java` | Monolitik memuat POI & Swing (85 baris) | Facade pendelegasi bersih (22 baris) | Identik (100% Kompatibel) |
| `util/export/ExcelDocumentGenerator.java` | Belum ada | Generator murni POI (80 baris) | Baru (Unit-Testable) |
| `gui/helper/SwingExportHelper.java` | Belum ada | Handler dialog Swing JTable (75 baris) | Baru (UI Helper) |
| `model/LateEntryPermit.java` | Belum ada | Subclass polimorfis izin terlambat (42 baris) | Baru (OCP Compliant) |
| `model/ExitPermit.java` | Belum ada | Subclass polimorfis izin keluar (48 baris) | Baru (OCP Compliant) |
| `repository/BaseRepository.java` | Auto-ID acak saja (84 baris) | Harmonisasi ID eksplisit & auto-ID (92 baris) | LSP Compliant |
| `repository/StudentRepository.java` | Duplikasi `createWithId` (42 baris) | Delegasi selaras kontrak parent (45 baris) | LSP Compliant |
| `repository/interfaces/PermitReader.java`| Belum ada | Role interface pembaca (25 baris) | Baru (ISP Compliant) |
| `repository/interfaces/PermitWriter.java`| Belum ada | Role interface penulis (20 baris) | Baru (ISP Compliant) |
| `repository/interfaces/IPermitRepository.java`| Belum ada | Abstraksi utama izin (10 baris) | Baru (DIP Compliant) |
| `service/ReportService.java` | Bergantung konkret `PermitRepository` | Bergantung abstraksi `PermitReader` (95 baris)| ISP Compliant |
| `service/PermitService.java` | Bergantung konkret `PermitRepository` | Bergantung abstraksi `IPermitRepository` (102 baris)| DIP Compliant |
| `app/AppServices.java` | Belum ada | Composition Root / Dependency Injector (58 baris)| Baru (DIP Container) |
| Panel Dashboard & Form GUI | Instansiasi langsung via `new` operator | Memperoleh service dari `AppServices` | Loose Coupling |

---
*Laporan ini disusun secara resmi untuk memenuhi persyaratan penugasan Ujian Tengah Semester (UTS) mata kuliah Analisis dan Desain Perangkat Lunak (ADPL).*

