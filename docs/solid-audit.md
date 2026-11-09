# 📑 Laporan Audit Prinsip SOLID — SmartSchool Permit System (TubesPBO)
**Mata Kuliah:** Analisis dan Desain Perangkat Lunak (ADPL)  
**Tujuan Dokumen:** Identifikasi, pembuktian source code, dan analisis pelanggaran prinsip SOLID beserta rekomendasi refactoring (BEFORE vs AFTER) untuk bahan laporan Ujian Tengah Semester (UTS).  
**Status Audit:** Audit Murni (Read-Only) — Tidak ada modifikasi source code aktual pada repositori.

---

## 1. Ringkasan Project

### 1.1 Gambaran Umum Sistem
**SmartSchool Permit System** adalah aplikasi desktop berbasis **Java Swing** yang digunakan untuk pencatatan dan pengelolaan surat izin siswa di lingkungan sekolah (studi kasus: SMAN 1 Rejotangan). Sistem ini mengelola dua kategori izin utama:
1. **Late Entry (Keterlambatan Masuk Sekolah)**: Pencatatan waktu kedatangan siswa yang terlambat beserta kalkulasi durasi keterlambatan dari batas jam masuk (07:00 WIB).
2. **Exit Permit (Izin Keluar / Dispensasi Sekolah)**: Pengajuan izin keluar kelas/sekolah dengan opsi pencatatan batas estimasi waktu kembali (return timestamp).

Aplikasi terhubung ke database cloud **Google Cloud Firestore** sebagai database backend dan menggunakan **Firebase Authentication** untuk manajemen otentikasi akun pengguna (Admin Piket, Super Admin, dan Siswa).

### 1.2 Struktur Arsitektur Aktual
Berdasarkan pemeriksaan codebase, aplikasi dirancang menggunakan pola berlapis (*Layered Architecture*) dengan package-package sebagai berikut:
- `com.smartschool.permit.tubespbo.model`: Menyimpan entitas data (`BaseModel`, `StudentPermit`, `AdminUser`, `Student`, `SchoolProfile`), data transfer/summary (`PermitSummary`, `StatisticsData`), interface kontrak (`Filterable`, `Exportable`), serta enum status/tipe (`PermitStatus`, `PermitType`, `UserRole`).
- `com.smartschool.permit.tubespbo.repository`: Menyediakan akses data ke Firestore (`CrudRepository`, `BaseRepository`, `AdminRepository`, `PermitRepository`, `StudentRepository`).
- `com.smartschool.permit.tubespbo.service`: Menangani alur bisnis aplikasi (`AuthService`, `StudentAuthService`, `AdminService`, `PermitService`, `ReportService`).
- `com.smartschool.permit.tubespbo.util`: Berisi kelas pembantu umum (`DateUtils`, `SchoolUtils`, `XlsxUtils`).
- `com.smartschool.permit.tubespbo.app`: Berisi infrastruktur aplikasi (`FirestoreConnection` sebagai database singleton, `UserSession` sebagai session state singleton, `MainApp`, dan skrip pengujian `TestCRUD`).
- `com.smartschool.permit.tubespbo.gui`: Antarmuka grafis pengguna (Swing) yang terbagi menjadi `gui.login`, `gui.formDispen`, `gui.dashboard`, dan `gui.widget`.

---

## 2. Hasil Audit Masing-Masing Prinsip SOLID

Secara keseluruhan, arsitektur proyek telah memiliki niat modularitas yang baik (pemisahan layer GUI, Service, Repository, dan Model). Namun, dalam implementasi konkretnya ditemukan pelanggaran signifikan terhadap **kelima prinsip SOLID** yang saling berkaitan:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                      RINGKASAN AUDIT PRINSIP SOLID                         │
├──────┬───────────────────────────────────────────┬──────────────┬───────────┤
│ Kode │ Prinsip SOLID                             │ Status Audit │ Temuan    │
├──────┼───────────────────────────────────────────┼──────────────┼───────────┤
│ S    │ Single Responsibility Principle (SRP)     │ Dilanggar    │ 4 Kasus   │
│ O    │ Open/Closed Principle (OCP)               │ Dilanggar    │ 3 Kasus   │
│ L    │ Liskov Substitution Principle (LSP)       │ Dilanggar    │ 2 Kasus   │
│ I    │ Interface Segregation Principle (ISP)     │ Dilanggar    │ 3 Kasus   │
│ D    │ Dependency Inversion Principle (DIP)      │ Dilanggar    │ 4 Kasus   │
└──────┴───────────────────────────────────────────┴──────────────┴───────────┘
```

### Evaluasi Singkat per Prinsip:
1. **Single Responsibility Principle (SRP):**
   Ditemukan kelas yang memiliki lebih dari satu alasan untuk berubah (*multiple reasons to change*). Kasus paling mencolok adalah `XlsxUtils` yang mencampuradukkan GUI dialog, manipulasi model Swing `JTable`, pembangkitan berkas Apache POI, dan disk I/O. Selain itu, kelas `AuthService` dan `AdminService` mencampur logika bisnis dengan pemanggilan jaringan HTTP mentah (*raw JSON & HttpURLConnection*) serta akses database langsung.
2. **Open/Closed Principle (OCP):**
   Ditemukan penggunaan percabangan logika bertingkat (*conditional branching*) berbasis enum tipe izin (`PermitType`) di dalam `StudentPermit`, `StatisticsData`, dan `ReportService`. Penambahan varian izin baru (misalnya *Izin Sakit* atau *Dispensasi Kegiatan Ekstrakurikuler*) memaksa pengembang memodifikasi banyak kelas yang sudah ada, alih-alih memperluas (*extending*) melalui polimorfisme.
3. **Liskov Substitution Principle (LSP):**
   Subclass `StudentRepository` tidak dapat sepenuhnya menggantikan kontrak parent class `BaseRepository<Student>`. Method `create(entity)` pada `BaseRepository` menghasilkan auto-generated ID dari Firestore, padahal `Student` mewajibkan Document ID identik dengan Firebase Auth UID. Pemanggilan `repo.create(student)` melalui referensi parent class akan merusak konsistensi data akun siswa, sehingga developer terpaksa membuat method baru `createWithId(uid, student)` yang memecah substitutabilitas.
4. **Interface Segregation Principle (ISP):**
   Ditemukan "kontrak kosong / mati": `Filterable` dipaksakan pada `AdminUser` padahal admin tidak pernah difilter; `Exportable` dipaksakan pada entitas model padahal utilitas ekspor (`XlsxUtils`) tidak pernah mengonsumsinya; dan `CrudRepository` memaksakan seluruh operasi CRUD kepada klien yang hanya membutuhkan kapabilitas read-only (seperti `ReportService`).
5. **Dependency Inversion Principle (DIP):**
   Pelanggaran paling masif di seluruh aplikasi. Modul tingkat tinggi (Service) bergantung langsung pada kelas konkret tingkat rendah (Repository), bukan pada interface abstraksi. Repository bergantung pada Singleton konkret (`FirestoreConnection`). Terlebih lagi, Presentation Layer (semua Panel Swing) langsung menginisialisasi Repository dan Service menggunakan kata kunci `new`, mengakibatkan *tight coupling* yang menghalangi pengujian unit (*unit testing*) dan *mocking*.

---

## 3. Daftar Temuan yang Terbukti

Berikut adalah matriks temuan pelanggaran SOLID yang diverifikasi langsung dari baris kode sumber:

| No | ID Temuan | Prinsip | File / Lokasi | Komponen Terdampak | Tingkat Keparahan |
|:--:|:---------:|:-------:|:--------------|:-------------------|:-----------------:|
| 1 | **SRP-01** | SRP | `util/XlsxUtils.java` (L16–84) | Method `exportTable` | 🔴 Tinggi |
| 2 | **SRP-02** | SRP | `service/AuthService.java` (L43–90) | Method `login` | 🔴 Tinggi |
| 3 | **SRP-03** | SRP | `gui/dashboard/StudentDataPanel.java` (L70–120) | Method `loadData` & Worker | 🟡 Sedang |
| 4 | **SRP-04** | SRP | `service/AdminService.java` (L38–88, L94–100) | Method `createAdmin`, `changePassword` | 🟡 Sedang |
| 5 | **OCP-01** | OCP | `model/StudentPermit.java` (L52–70, L87–92) | `getDurationMinutes`, `toMap` | 🔴 Tinggi |
| 6 | **OCP-02** | OCP | `model/StatisticsData.java` (L23–41) & `ReportService.java` (L47–51, L74–78) | `calculate`, `getStudentSummary` | 🟡 Sedang |
| 7 | **OCP-03** | OCP | `gui/dashboard/LateEntryPanel.java` & `ExitPermitPanel.java` | Arsitektur Panel GUI Izin | 🟡 Sedang |
| 8 | **LSP-01** | LSP | `repository/StudentRepository.java` (L34–41) & `BaseRepository.java` (L65–73) | Kontrak Method `create` vs `createWithId` | 🔴 Tinggi |
| 9 | **LSP-02** | LSP | `service/AuthService.java` (L43, L76–87) & `gui/login/LoginFrame.java` (L153–157) | Return Type `Object` & Role Polymorphism | 🟡 Sedang |
| 10 | **ISP-01** | ISP | `model/Filterable.java` (L11–13) & `model/AdminUser.java` (L13, L60–65) | Interface `Filterable` | 🟡 Sedang |
| 11 | **ISP-02** | ISP | `model/Exportable.java` (L11–13) & `model/StudentPermit.java` (L130–138) | Interface `Exportable` | 🟡 Sedang |
| 12 | **ISP-03** | ISP | `repository/CrudRepository.java` (L13–19) & `service/ReportService.java` (L22–26) | Fat Interface `CrudRepository` | 🟢 Ringan |
| 13 | **DIP-01** | DIP | `service/PermitService.java` (L21–26), `ReportService.java` (L22–27), `AuthService.java` (L24–31) | Service Layer coupling ke Concrete Repo | 🔴 Tinggi |
| 14 | **DIP-02** | DIP | `repository/BaseRepository.java` (L24–32) | BaseRepository coupling ke `FirestoreConnection` | 🔴 Tinggi |
| 15 | **DIP-03** | DIP | Semua Panel GUI (`LateEntryPanel`, `ExitPermitPanel`, `AdminPanel`, `ReportPanel`, `FormKeterlambatan`) | GUI layer instansiasi `new Repository()` | 🔴 Tinggi |
| 16 | **DIP-04** | DIP | `service/PermitService.java` (L42–44) & `service/AdminService.java` (L77–79) | Service layer coupling ke UI Singleton `UserSession` | 🟡 Sedang |

---

## 4. Bukti Source Code BEFORE

Bagian ini menyajikan kutipan kode sumber aktual proyek yang membuktikan setiap pelanggaran tanpa ada manipulasi atau asumsi teoritis semata.

### 4.1 Bukti SRP
#### Kasus SRP-01: `XlsxUtils.java` (Baris 16–84)
```java
public class XlsxUtils {
    public static void exportTable(JTable table, String defaultFileName) {
        // [Tanggung Jawab 1: GUI Dialog Interaksi Pengguna]
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan Excel");
        fileChooser.setSelectedFile(new File(defaultFileName + ".xlsx"));

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File fileToSave = fileChooser.getSelectedFile();
        // ... (ekstensi .xlsx handling) ...

        // [Tanggung Jawab 2 & 3: Apache POI Workbook Generation & Formatting]
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Laporan");
            TableModel model = table.getModel(); // [Tanggung Jawab 4: Ketergantungan UI Model Swing]

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            // ... formatting cell ...

            for (int row = 0; row < model.getRowCount(); row++) {
                Row excelRow = sheet.createRow(row + 1);
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Object value = model.getValueAt(row, col);
                    // ... parsing object ke cell ...
                }
            }

            // [Tanggung Jawab 5: File I/O ke Harddisk]
            try (FileOutputStream fileOut = new FileOutputStream(fileToSave)) {
                workbook.write(fileOut);
                // [Tanggung Jawab 6: GUI Notifikasi]
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

#### Kasus SRP-02: `AuthService.java` (Baris 43–90)
```java
public Object login(String email, String password) throws Exception {
    // [Tanggung Jawab 1: Low-level Network HTTP Call & REST endpoint handling]
    String urlStr = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + API_KEY;
    URL url = java.net.URI.create(urlStr).toURL();
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/json; utf-8");
    conn.setDoOutput(true);

    // [Tanggung Jawab 2: Manual JSON Serialization & Stream Encoding]
    JsonObject jsonPayload = new JsonObject();
    jsonPayload.addProperty("email", email);
    jsonPayload.addProperty("password", password);
    jsonPayload.addProperty("returnSecureToken", true);

    try (OutputStream os = conn.getOutputStream()) {
        byte[] input = jsonPayload.toString().getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);
    }
    // ... baca input stream & parsing JSON token ...

    // [Tanggung Jawab 3: Domain Authorization & Session Management State]
    AdminUser adminUser = adminRepo.getByUid(uid);
    if (adminUser != null) {
        UserSession.getInstance().login(adminUser);
        return adminUser;
    }

    Student student = studentRepo.getByUid(uid);
    if (student != null) {
        UserSession.getInstance().loginAsStudent(student);
        return student;
    }
    // ...
}
```

#### Kasus SRP-03: `StudentDataPanel.java` (Baris 21–22, 70–108)
```java
public class StudentDataPanel extends JPanel {
    // GUI langsung memegang referensi konkret dua repository
    private final StudentRepository studentRepo = new StudentRepository();
    private final PermitRepository permitRepo = new PermitRepository();
    // ...
    private void loadData() {
        new SwingWorker<List<Object[]>, Void>() {
            @Override
            protected List<Object[]> doInBackground() throws Exception {
                List<Student> students = studentRepo.getAll();
                List<StudentPermit> allPermits = permitRepo.getBySchool(schoolId);

                // [Logika Bisnis di dalam GUI: Agregasi data izin per siswa]
                Map<String, List<StudentPermit>> permitsByStudent = new HashMap<>();
                for (StudentPermit p : allPermits) {
                    String key = p.getStudentId();
                    if (key == null || key.isEmpty()) {
                        key = p.getStudentName() + "|" + p.getClassName();
                    }
                    permitsByStudent.computeIfAbsent(key, k -> new ArrayList<>()).add(p);
                }

                // Kalkulasi menit dan frekuensi pelanggaran
                for (Student s : students) {
                    // ... iterasi kalkulasi lateFreq, lateMins, exitFreq, exitMins ...
                }
                return rows;
            }
        }.execute();
    }
}
```

---

### 4.2 Bukti OCP
#### Kasus OCP-01: `StudentPermit.java` (Baris 52–70, 87–92)
```java
public int getDurationMinutes() {
    ZoneId zone = ZoneId.of("Asia/Jakarta");
    ZonedDateTime zdt = Instant.ofEpochMilli(timestamp).atZone(zone);
    
    // PERCABANGAN BERDASARKAN TIPE IZIN
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
    // ... field umum ...
    // Field spesifik berdasarkan tipe (Percabangan manual)
    if (isLateEntry()) {
        if (arrivalTimestamp > 0) map.put("arrivalTimestamp", arrivalTimestamp);
    } else if (isExitPermit()) {
        if (returnTimestamp > 0) map.put("returnTimestamp", returnTimestamp);
        if (exitTimestamp > 0) map.put("exitTimestamp", exitTimestamp);
    }
    // ...
}
```

#### Kasus OCP-02: `StatisticsData.java` (Baris 28–40)
```java
for (StudentPermit p : permits) {
    if (p.isLateEntry()) totalLateEntry++;
    if (p.isExitPermit()) totalExitPermit++;
    if (p.getStatus() != null && p.isPending()) pendingCount++;

    ZonedDateTime pDate = Instant.ofEpochMilli(p.getTimestamp()).atZone(ZoneId.of("Asia/Jakarta"));
    if (pDate.getDayOfYear() == todayDay && pDate.getYear() == todayYear) {
        todayCount++;
        if (p.isLateEntry()) todayLateCount++;
        if (p.isExitPermit()) todayExitCount++;
    }
}
```

---

### 4.3 Bukti LSP
#### Kasus LSP-01: `BaseRepository.java` (Baris 65–73) vs `StudentRepository.java` (Baris 34–41)
`BaseRepository.java`:
```java
public abstract class BaseRepository<T extends BaseModel> implements CrudRepository<T> {
    // Kontrak standar pembuatan entitas di BaseRepository: ID selalu dibuat otomatis oleh Firestore
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
}
```

`StudentRepository.java`:
```java
public class StudentRepository extends BaseRepository<Student> {
    // ...
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
}
```

---

### 4.4 Bukti ISP
#### Kasus ISP-01 & ISP-02: `Filterable.java` & `Exportable.java`
`Filterable.java`:
```java
public interface Filterable {
    boolean matchesFilter(String keyword);
}
```
`AdminUser.java` (Baris 13, 60–65):
```java
public class AdminUser extends BaseModel implements Filterable {
    // ...
    @Override
    public boolean matchesFilter(String keyword) {
        if (keyword == null || keyword.isEmpty()) return true;
        String lowerKw = keyword.toLowerCase();
        return (name != null && name.toLowerCase().contains(lowerKw)) ||
               (email != null && email.toLowerCase().contains(lowerKw));
    }
}
```
*(Fakta: Tidak ada satupun baris kode di seluruh modul `admin` atau `gui.dashboard.AdminPanel` yang memanggil `matchesFilter` pada `AdminUser` atau menggunakan tipe `Filterable`.)*

`Exportable.java`:
```java
public interface Exportable {
    Map<String, Object> toExportRow();
}
```
`StudentPermit.java` (Baris 130–138):
```java
@Override
public Map<String, Object> toExportRow() {
    Map<String, Object> row = new HashMap<>();
    row.put("Nama", studentName);
    row.put("Kelas", className);
    row.put("Tipe", type != null ? type.name() : "-");
    row.put("Status", status != null ? status.name() : "-");
    row.put("Durasi (Menit)", getDurationMinutes());
    return row;
}
```
*(Fakta: `XlsxUtils.exportTable` hanya menerima `JTable` dan membaca `TableModel`. Method `toExportRow()` tidak pernah dipanggil oleh utilitas ekspor manapun).*

---

### 4.5 Bukti DIP
#### Kasus DIP-01: `PermitService.java` (Baris 21–26) & `AuthService.java` (Baris 24–31)
`PermitService.java`:
```java
public class PermitService {
    // Bergantung langsung pada kelas konkret PermitRepository, bukan interface abstraksi
    private final PermitRepository permitRepo;

    public PermitService(PermitRepository permitRepo) {
        this.permitRepo = permitRepo;
    }
    // ...
}
```

`AuthService.java`:
```java
public class AuthService {
    private final AdminRepository adminRepo;
    private final StudentRepository studentRepo;

    public AuthService() {
        // High-level service langsung membuat instance low-level concrete repository
        this.adminRepo = new AdminRepository();
        this.studentRepo = new StudentRepository();
    }
}
```

#### Kasus DIP-02: `BaseRepository.java` (Baris 24–32)
```java
public abstract class BaseRepository<T extends BaseModel> implements CrudRepository<T> {
    protected final String collectionName;
    protected final Firestore db;

    public BaseRepository(String collectionName) {
        this.collectionName = collectionName;
        // BaseRepository terikat kuat pada Singleton konkret FirestoreConnection
        this.db = FirestoreConnection.getInstance().getDb(); 
    }
    // ...
}
```

#### Kasus DIP-03: `LateEntryPanel.java` (Baris 28–29) & `FormKeterlambatan.java` (Baris 337–338)
`LateEntryPanel.java`:
```java
public class LateEntryPanel extends JPanel {
    // Presentation layer membuat instance konkret sendiri secara hardcoded
    private final PermitRepository permitRepo = new PermitRepository();
    private final PermitService permitService = new PermitService(permitRepo);
    // ...
}
```

`FormKeterlambatan.java`:
```java
new SwingWorker<String, Void>() {
    @Override
    protected String doInBackground() throws Exception {
        // Instansiasi langsung di dalam listener aksi tombol form
        PermitRepository repo = new PermitRepository();
        PermitService service = new PermitService(repo);
        return service.createPermit(permit);
    }
    // ...
}.execute();
```

---

## 5. Analisis Setiap Pelanggaran

### 5.1 Analisis SRP (Single Responsibility Principle)
> *"A class should have one, and only one, reason to change."* — Robert C. Martin

1. **Kelas `XlsxUtils`:**
   - **Tanggung Jawab Bermasalah:** Menggabungkan presentasi UI Swing (`JFileChooser`, `JOptionPane`), ekstraksi data berbasis komponen UI Swing (`JTable`, `TableModel`), format berkas Excel (Apache POI), dan transmisi file I/O ke media penyimpanan.
   - **Mengapa Melanggar:** Kelas memiliki setidaknya 4 alasan berbeda untuk berubah: (1) Perubahan alur UI (misal beralih dari Swing ke JavaFX atau CLI tanpa popup dialog), (2) Perubahan struktur data model yang diekspor (ingin mengekspor `List<StudentPermit>` langsung tanpa harus membuat GUI table), (3) Perubahan format tampilan/styling lembar kerja Excel (warna, font, formula), dan (4) Perubahan target penyimpanan (misal penyimpanan cloud/stream memori).
   - **Dampak Teknis:** 
     - *Testability = 0*: Method `exportTable` mustahil diuji dalam *automated unit test (CI/CD)* tanpa GUI karena pemanggilan `fileChooser.showSaveDialog(null)` akan memblokir thread atau menyebabkan `HeadlessException`.
     - *Coupling Tinggi*: Modul utility terikat ke pustaka `javax.swing.*`.

2. **Kelas `AuthService` dan `AdminService`:**
   - **Tanggung Jawab Bermasalah:** Mencampur otorisasi bisnis domain dengan komunikasi HTTP REST tingkat rendah dan manipulasi state sesi global.
   - **Mengapa Melanggar:** Jika endpoint Google Identity Toolkit berubah, format payload JSON berubah, atau ingin diganti ke provider OAuth lokal, kelas `AuthService` dan `AdminService` harus dirombak total, meskipun aturan bisnis peran akun tidak berubah.
   - **Dampak Teknis:** Uji coba logika login memerlukan koneksi internet aktif ke server Google; kegagalan jaringan akan menggagalkan unit test aturan bisnis.

3. **Kelas `StudentDataPanel`:**
   - **Tanggung Jawab Bermasalah:** Komponen tampilan GUI bertindak sebagai kalkulator analitik domain dengan melakukan pencocokan relasi *student-to-permit* dan akumulasi durasi keterlambatan.
   - **Mengapa Melanggar:** Logika bisnis tersebut tidak dapat dipakai ulang (*reused*) oleh komponen lain (misalnya jika ingin diekspor ke PDF atau ditampilkan di panel lain).

---

### 5.2 Analisis OCP (Open/Closed Principle)
> *"Software entities should be open for extension, but closed for modification."* — Bertrand Meyer

1. **Entitas `StudentPermit` dan Penghitungan Durasi:**
   - **Tanggung Jawab Bermasalah:** `StudentPermit` mengelola properti dari dua jenis entitas yang berbeda (`arrivalTimestamp` milik izin terlambat, sedangkan `exitTimestamp` dan `returnTimestamp` milik izin keluar) dengan logika bercabang di `getDurationMinutes()`, `toMap()`, dan `fromMap()`.
   - **Mengapa Melanggar:** Apabila sekolah menambahkan izin tipe ke-3 (misal: `SICK_LEAVE` / Izin Sakit yang membutuhkan nomor surat dokter, atau `ACTIVITY_DISPENSATION` yang memerlukan nama perlombaan):
     - Enum `PermitType` harus diubah.
     - Method `StudentPermit.getDurationMinutes()` harus dimodifikasi dengan menambah blok `else if`.
     - Method `StudentPermit.toMap()` dan `fromMap()` harus diubah.
     - `StatisticsData.calculate()` dan `ReportService` harus diubah dengan menambah field counter baru.
     Hal ini bertentangan dengan OCP karena kode yang sudah stabil dipaksa dibuka kembali untuk dimodifikasi (*closed for modification violated*).
   - **Dampak Teknis:** *Regression Risk* sangat tinggi. Perubahan pada tipe izin baru rentan merusak kalkulasi durasi izin lama.

2. **Duplikasi Panel GUI (`LateEntryPanel` vs `ExitPermitPanel`):**
   - **Tanggung Jawab Bermasalah:** Dua kelas panel memiliki ~280 baris kode yang hampir 90% identik (pagination, layout table, dialog approve, dialog delete, load data via worker).
   - **Mengapa Melanggar:** Arsitektur GUI tidak menyediakan template atau abstraksi generik untuk pengelolaan data izin. Menambah jenis izin baru mengharuskan *copy-paste* 280 baris kode baru.

---

### 5.3 Analisis LSP (Liskov Substitution Principle)
> *"Let $\Phi(x)$ be a property provable about objects $x$ of type $T$. Then $\Phi(y)$ should be true for objects $y$ of type $S$ where $S$ is a subtype of $T$."* — Barbara Liskov

1. **Pelanggaran Kontrak Pembuatan Entitas (`BaseRepository` vs `StudentRepository`):**
   - **Tanggung Jawab Bermasalah:** Kontrak `BaseRepository.create(T entity)` menjanjikan bahwa entitas akan disimpan dan mengembalikan string identifier hasil generasi otomatis Firestore (`add(entity.toMap())`). Namun, pada domain entitas `Student`, dokumen siswa di Firestore **wajib** memiliki ID yang sama dengan `uid` dari Firebase Auth agar operasi `getByUid(uid)` pada proses login dapat menemukan data profil siswa tersebut.
   - **Mengapa Melanggar:** Jika sebuah modul klien menggunakan polimorfisme:
     ```java
     CrudRepository<Student> repo = new StudentRepository();
     repo.create(newStudent);
     ```
     Kode ini akan mengompilasi dengan valid, namun saat dieksekusi, data siswa akan disimpan dengan ID acak (*random auto-id*). Akibatnya, akun siswa tersebut **rusak secara permanen** (saat login, `getByUid` mencari ID sesuai UID Firebase, tetapi tidak ditemukan). Subclass `StudentRepository` tidak dapat menggantikan parent class `BaseRepository<Student>` secara aman untuk operasi `create()`. Pengembang menyadari kelemahan ini dan membuat method baru `createWithId(uid, student)`, namun membiarkan method warisan `create()` tetap ada dan berpotensi dipanggil secara salah.
   - **Dampak Teknis:** Integritas data rusak saat terjadi substitusi polimorfik; melanggar asumsi dasar abstraksi repositori.

2. **Ketiadaan Abstraksi User (`AuthService.login` Mengembalikan `Object`):**
   - **Tanggung Jawab Bermasalah:** `AdminUser` dan `Student` sama-sama entitas pengguna yang terotentikasi, namun keduanya tidak memiliki abstraksi bersama selain `BaseModel`.
   - **Mengapa Melanggar:** Method `AuthService.login()` mengembalikan tipe `Object`. Pemanggil (`LoginFrame`) terpaksa melakukan type checking runtime (`instanceof` atau memeriksa flag sesi) untuk menentukan alur navigasi.

---

### 5.4 Analisis ISP (Interface Segregation Principle)
> *"Clients should not be forced to depend upon interfaces that they do not use."* — Robert C. Martin

1. **Interface `Filterable`:**
   - **Tanggung Jawab Bermasalah:** Mendefinisikan method `boolean matchesFilter(String keyword)`. Interface ini diimplementasikan oleh `AdminUser` dan `StudentPermit`.
   - **Mengapa Melanggar:** `AdminUser` dipaksa menyediakan implementasi pencocokan kata kunci pada `name` dan `email`, namun sistem tidak pernah menyediakan fungsionalitas pencarian pada manajemen admin. Lebih lanjut, interface `Filterable` tidak pernah digunakan sebagai tipe referensi abstraksi (misalnya `List<Filterable>`), melainkan method-nya hanya dipanggil langsung pada instance konkret `StudentPermit`.
   - **Dampak Teknis:** *Polluted interface* dan kode mati (*dead code*) yang membebani pemeliharaan class.

2. **Interface `Exportable`:**
   - **Tanggung Jawab Bermasalah:** Mendefinisikan method `Map<String, Object> toExportRow()`.
   - **Mengapa Melanggar:** Domain entitas `StudentPermit` dan DTO `PermitSummary` dipaksa mengimplementasikan konversi baris ekspor. Namun komponen pengekspor data aktual (`XlsxUtils`) sama sekali tidak menggunakan interface ini (justru mengambil data dari `TableModel` Swing). Domain model terikat pada representasi tampilan tabel (label kolom bahasa Indonesia).

3. **Interface `CrudRepository<T>`:**
   - **Tanggung Jawab Bermasalah:** Menggabungkan operasi baca (`getAll`, `getById`) dan operasi tulis/hapus (`create`, `update`, `delete`).
   - **Mengapa Melanggar:** Klien seperti `ReportService` hanya memerlukan operasi pembacaan data agregat, tetapi jika bergantung pada interface ini, ia terekspos pada operasi mutasi data yang tidak semestinya.

---

### 5.5 Analisis DIP (Dependency Inversion Principle)
> *"High-level modules should not depend on low-level modules. Both should depend on abstractions. Abstractions should not depend on details. Details should depend on abstractions."* — Robert C. Martin

1. **Ketergantungan Service ke Repository Konkret:**
   - **Tanggung Jawab Bermasalah:** `PermitService`, `ReportService`, dan `AdminService` mendeklarasikan variabel bertipe konkret `PermitRepository` dan `AdminRepository`. Bahkan `AuthService` langsung memanggil `new AdminRepository()` dan `new StudentRepository()` di dalam konstruktor tanpa parameter.
   - **Mengapa Melanggar:** Service (modul tingkat tinggi berisi aturan bisnis) bergantung langsung pada Repository (modul tingkat rendah pelaksana I/O Firestore). Tidak ada interface perantara seperti `IPermitRepository`.
   - **Dampak Teknis:** Mustahil melakukan *unit testing* terisolasi pada `PermitService` tanpa koneksi internet dan tanpa akun Firebase aktif. Pembuatan *Mock Repository* terhambat karena ketiadaan interface.

2. **Ketergantungan Repository ke Singleton Database Konkret:**
   - **Tanggung Jawab Bermasalah:** `BaseRepository` mengikat koneksi database secara statis melalui `FirestoreConnection.getInstance().getDb()`.
   - **Mengapa Melanggar:** Detail persistensi terikat permanen ke Google Firestore. Aplikasi tidak dapat dijalankan menggunakan SQLite, MySQL, atau in-memory database saat testing.

3. **Inisialisasi Konkret di Lapisan GUI:**
   - **Tanggung Jawab Bermasalah:** Komponen tampilan Swing (`LateEntryPanel`, `ExitPermitPanel`, `AdminPanel`, `ReportPanel`, `StudentDataPanel`, `FormKeterlambatan`, `FormDispensasi`) membuat instance service dan repository secara langsung dengan kata kunci `new`.
   - **Mengapa Melanggar:** Layer presentasi terikat mati ke implementasi backend konkret. Mengubah strategi repository mengharuskan pengeditan belasan file GUI.

4. **Ketergantungan Service Bisnis ke Singleton GUI (`UserSession`):**
   - **Tanggung Jawab Bermasalah:** `PermitService.createPermit()` memanggil `UserSession.getInstance().getSchoolId()`.
   - **Mengapa Melanggar:** Logika domain bisnis menjadi dependen terhadap state UI sesi desktop. Jika service ini dieksekusi di background job atau REST controller, method akan melempar `NullPointerException`.

---

## 6. Rekomendasi Solusi AFTER

Bagian ini memaparkan rancangan refactoring konseptual (AFTER) untuk menyelesaikan setiap pelanggaran yang teridentifikasi, lengkap dengan gambaran source code perbaikan.

### 6.1 Solusi SRP: Dekopling `XlsxUtils` menjadi Komponen Terpisah
Pisahkan tanggung jawab menjadi:
1. `ExcelGenerator`: Murni membuat berkas POI dari data generik (tanpa Swing).
2. `TableExportHelper`: Menghubungkan Swing UI (`JTable`, `JFileChooser`) dengan `ExcelGenerator`.

#### Kode AFTER — `ExcelGenerator.java` (Domain/Util Independen):
```java
package com.smartschool.permit.tubespbo.util.export;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.OutputStream;
import java.util.List;

public class ExcelGenerator {
    public static void generateWorkbook(List<String> headers, List<List<Object>> rows, OutputStream out) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Laporan");

            // Header styling
            Font font = workbook.createFont();
            font.setBold(true);
            CellStyle style = workbook.createCellStyle();
            style.setFont(font);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(style);
            }

            // Data rows
            for (int r = 0; r < rows.size(); r++) {
                Row row = sheet.createRow(r + 1);
                List<Object> rowData = rows.get(r);
                for (int c = 0; c < rowData.size(); c++) {
                    Object val = rowData.get(c);
                    Cell cell = row.createCell(c);
                    if (val instanceof Number) {
                        cell.setCellValue(((Number) val).doubleValue());
                    } else if (val != null) {
                        cell.setCellValue(val.toString());
                    }
                }
            }
            for (int i = 0; i < headers.size(); i++) sheet.autoSizeColumn(i);
            workbook.write(out);
        }
    }
}
```

#### Kode AFTER — `SwingExportHelper.java` (Khusus GUI Swing):
```java
package com.smartschool.permit.tubespbo.gui.helper;

import com.smartschool.permit.tubespbo.util.export.ExcelGenerator;
import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SwingExportHelper {
    public static void exportJTableToExcel(JTable table, Component parent, String defaultFileName) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(defaultFileName + ".xlsx"));
        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) return;

        File target = chooser.getSelectedFile();
        if (!target.getName().toLowerCase().endsWith(".xlsx")) {
            target = new File(target.getAbsolutePath() + ".xlsx");
        }

        try {
            TableModel model = table.getModel();
            List<String> headers = new ArrayList<>();
            for (int i = 0; i < model.getColumnCount(); i++) headers.add(model.getColumnName(i));

            List<List<Object>> rows = new ArrayList<>();
            for (int r = 0; r < model.getRowCount(); r++) {
                List<Object> row = new ArrayList<>();
                for (int c = 0; c < model.getColumnCount(); c++) row.add(model.getValueAt(r, c));
                rows.add(row);
            }

            try (FileOutputStream fos = new FileOutputStream(target)) {
                ExcelGenerator.generateWorkbook(headers, rows, fos);
            }
            JOptionPane.showMessageDialog(parent, "Laporan berhasil disimpan:\n" + target.getAbsolutePath(), "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent, "Gagal mengekspor data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
```

---

### 6.2 Solusi OCP: Polimorfisme Tipe Izin Menggunakan Subclassing
Ubah `StudentPermit` menjadi kelas abstrak dasar dan turunkan menjadi `LateEntryPermit` dan `ExitPermit`. Logika kalkulasi durasi didelegasikan secara polimorfis tanpa `if-else`.

#### Kode AFTER — Hirarki Izin Polimorfis:
```java
// Abstract Base Permit
public abstract class BaseStudentPermit extends BaseModel {
    protected String studentName;
    protected String className;
    protected String reason;
    protected long timestamp;
    protected PermitStatus status = PermitStatus.PENDING;
    // ... getter/setter ...

    public abstract PermitType getType();
    public abstract int getDurationMinutes();
}

// Subclass Izin Terlambat
public class LateEntryPermit extends BaseStudentPermit {
    private long arrivalTimestamp;

    @Override
    public PermitType getType() { return PermitType.LATE_ENTRY; }

    @Override
    public int getDurationMinutes() {
        ZonedDateTime zdt = Instant.ofEpochMilli(timestamp).atZone(ZoneId.of("Asia/Jakarta"));
        ZonedDateTime sevenAm = zdt.withHour(7).withMinute(0).withSecond(0).withNano(0);
        long diff = timestamp - sevenAm.toInstant().toEpochMilli();
        return (int) Math.max(0, diff / 60000);
    }
}

// Subclass Izin Keluar
public class ExitPermit extends BaseStudentPermit {
    private long exitTimestamp;
    private long returnTimestamp;

    @Override
    public PermitType getType() { return PermitType.EXIT_PERMIT; }

    @Override
    public int getDurationMinutes() {
        if (returnTimestamp > 0) {
            return (int) ((returnTimestamp - timestamp) / 60000);
        }
        ZonedDateTime zdt = Instant.ofEpochMilli(timestamp).atZone(ZoneId.of("Asia/Jakarta"));
        ZonedDateTime threePm = zdt.withHour(15).withMinute(0).withSecond(0).withNano(0);
        return (int) Math.max(0, (threePm.toInstant().toEpochMilli() - timestamp) / 60000);
    }
}
```
*Manfaat:* Jika ingin menambah `SickLeavePermit` (Izin Sakit), cukup buat class baru yang meng-extend `BaseStudentPermit` tanpa perlu menyentuh method pada kelas-kelas yang telah ada.

---

### 6.3 Solusi LSP: Standardisasi Kontrak `CrudRepository`
Ubah kontrak `CrudRepository` dan `BaseRepository` agar mendukung persistensi entitas baik yang menggunakan ID otomatis maupun ID eksplisit (misal Firebase UID), tanpa melanggar kontrak parent class.

#### Kode AFTER — `CrudRepository.java` & `BaseRepository.java`:
```java
public interface CrudRepository<T extends BaseModel> {
    List<T> getAll();
    T getById(String id);
    void save(T entity); // Menggunakan save: jika id null buat auto-id, jika ada id gunakan id tersebut
    void delete(String id);
}

public abstract class BaseRepository<T extends BaseModel> implements CrudRepository<T> {
    protected final Firestore db;
    protected final String collectionName;

    public BaseRepository(Firestore db, String collectionName) {
        this.db = db;
        this.collectionName = collectionName;
    }

    @Override
    public void save(T entity) {
        try {
            if (entity.getId() == null || entity.getId().trim().isEmpty()) {
                // Auto-generate ID
                DocumentReference ref = db.collection(collectionName).document();
                entity.setId(ref.getId());
                ref.set(entity.toMap()).get();
            } else {
                // Gunakan ID yang telah diset (misal Firebase Auth UID)
                db.collection(collectionName).document(entity.getId()).set(entity.toMap()).get();
            }
        } catch (Exception e) {
            throw new RuntimeException("Gagal menyimpan data ke koleksi " + collectionName, e);
        }
    }
}
```
*Manfaat:* Klien dapat memanggil `repo.save(student)` secara polimorfik tanpa risiko ID acak yang merusak relasi auth.

---

### 6.4 Solusi ISP: Pemisahan Interface Spesifik (*Segregated Interfaces*)
Hapus implementasi `Filterable` dari `AdminUser`, hapus `Exportable` dari domain entity, dan pecah repositori menjadi interface peran (*role interfaces*).

#### Kode AFTER — Interface Repository Tersegregasi:
```java
// Interface khusus pembacaan data izin
public interface PermitReader {
    List<StudentPermit> getBySchool(String schoolId);
    List<StudentPermit> getByType(String schoolId, PermitType type);
    StudentPermit getById(String id);
}

// Interface khusus mutasi data izin
public interface PermitWriter {
    void save(StudentPermit permit);
    void approve(String permitId, AdminUser admin);
    void delete(String permitId);
}

// Interface gabungan untuk service yang memerlukan keduanya
public interface IPermitRepository extends PermitReader, PermitWriter {}
```
*Manfaat:* `ReportService` cukup menginjeksi `PermitReader`. Ia tidak memiliki akses ke method `save()`, `delete()`, atau `approve()`, sehingga mematuhi ISP dan membatasi *surface area* kesalahan.

---

### 6.5 Solusi DIP: Penerapan Abstraksi & Dependency Injection (DI)
1. Buat interface `IPermitRepository`, `IAdminRepository`, `IStudentRepository`.
2. Masukkan dependensi melalui konstruktor (*Constructor Injection*).
3. Buat kelas `ServiceFactory` atau `AppContainer` sebagai penyedia dependensi terpusat.

#### Kode AFTER — `PermitService.java` (Mematuhi DIP):
```java
package com.smartschool.permit.tubespbo.service;

import com.smartschool.permit.tubespbo.model.AdminUser;
import com.smartschool.permit.tubespbo.model.StudentPermit;
import com.smartschool.permit.tubespbo.model.enums.PermitType;
import com.smartschool.permit.tubespbo.repository.interfaces.IPermitRepository;
import java.util.List;

public class PermitService {
    // Bergantung pada abstraksi Interface, bukan implementasi konkret
    private final IPermitRepository permitRepo;

    public PermitService(IPermitRepository permitRepo) {
        this.permitRepo = permitRepo;
    }

    public List<StudentPermit> getAllPermits(String schoolId) {
        return permitRepo.getBySchool(schoolId);
    }

    public List<StudentPermit> getPermitsByType(String schoolId, PermitType type) {
        return permitRepo.getByType(schoolId, type);
    }

    // schoolId di-passing eksplisit, bukan membaca Singleton GUI UserSession
    public void createPermit(StudentPermit permit, String schoolId) {
        permit.setSchoolId(schoolId);
        permit.setTimestamp(System.currentTimeMillis());
        permitRepo.save(permit);
    }
}
```

#### Kode AFTER — `ServiceFactory.java` (Composition Root Sederhana):
```java
package com.smartschool.permit.tubespbo.app;

import com.google.cloud.firestore.Firestore;
import com.smartschool.permit.tubespbo.repository.*;
import com.smartschool.permit.tubespbo.repository.interfaces.*;
import com.smartschool.permit.tubespbo.service.*;

public class ServiceFactory {
    private static ServiceFactory instance;
    private final IPermitRepository permitRepo;
    private final PermitService permitService;
    private final ReportService reportService;

    private ServiceFactory() {
        Firestore db = FirestoreConnection.getInstance().getDb();
        this.permitRepo = new PermitRepository(db);
        this.permitService = new PermitService(permitRepo);
        this.reportService = new ReportService(permitRepo);
    }

    public static synchronized ServiceFactory getInstance() {
        if (instance == null) instance = new ServiceFactory();
        return instance;
    }

    public PermitService getPermitService() { return permitService; }
    public ReportService getReportService() { return reportService; }
}
```
*Manfaat:* Pada GUI panel (misal `LateEntryPanel`), panel cukup memanggil:
```java
this.permitService = ServiceFactory.getInstance().getPermitService();
```
tanpa pernah lagi melakukan `new PermitRepository()` di lapisan tampilan. Saat pengujian unit, kita dapat dengan mudah menyuntikkan *MockPermitRepository*.

---

## 7. Prioritas Kasus untuk Tugas UTS

Untuk keperluan penulisan laporan dan presentasi Ujian Tengah Semester (UTS) mata kuliah **Analisis dan Desain Perangkat Lunak (ADPL)**, temuan-temuan di atas diurutkan berdasarkan **kualitas bobot akademik, kejelasan bukti kode, dan dampak refactoring terhadap desain sistem**:

### 🥇 Peringkat 1: Pelanggaran DIP pada Service & Repository Layer
- **ID Kasus:** DIP-01 & DIP-03 (`PermitService`, `ReportService`, dan seluruh panel GUI).
- **Alasan Pemilihan Utama untuk UTS:**
  - *Sangat Fundamental:* Dependency Inversion Principle dan Inversion of Control (IoC) adalah materi inti ADPL dalam mendesain arsitektur perangkat lunak modern.
  - *Bukti Kode Sangat Konkret:* Fakta bahwa GUI langsung memanggil `new PermitRepository()` dan `new PermitService()` membuktikan pelanggaran *Coupling* secara nyata.
  - *Solusi Elegan:* Memperkenalkan interface repository (`IPermitRepository`) dan *Constructor Injection* dengan `ServiceFactory` memberikan dampak perubahan arsitektur yang paling signifikan dan mudah dinilai oleh dosen/asisten lab.

### 🥈 Peringkat 2: Pelanggaran SRP pada Modul Utilitas & Ekspor Data
- **ID Kasus:** SRP-01 (`XlsxUtils.java` baris 16–84).
- **Alasan Pemilihan untuk UTS:**
  - *Mudah Dipahami dan Tidak Terbantahkan:* Siapapun yang membaca kode `XlsxUtils` akan langsung melihat kejanggalan sebuah kelas utilitas berkas yang memunculkan `JOptionPane` dan meminta komponen visual `JTable`.
  - *Koreksi Sangat Jelas:* Pemisahan antara generator dokumen murni (Domain/Infrastructure) dengan pembantu UI Swing (Presentation Helper) memberikan perbandingan BEFORE-AFTER yang sangat visual dan terstruktur rapi di dokumen laporan.

### 🥉 Peringkat 3: Pelanggaran OCP pada Kalkulasi Durasi Entitas Izin
- **ID Kasus:** OCP-01 (`StudentPermit.java` method `getDurationMinutes()`, `toMap()`).
- **Alasan Pemilihan untuk UTS:**
  - *Mendemonstrasikan Kekuatan OOP:* Mengganti deretan `if (isLateEntry()) ... else if (isExitPermit())` dengan konsep Polimorfisme (subclassing `LateEntryPermit` dan `ExitPermit`) adalah contoh textbook penerapan prinsip Open/Closed.
  - *Relevansi Bisnis Nyata:* Sangat mudah dijelaskan saat presentasi: "Jika sekolah ingin menambah izin sakit, pada kode BEFORE kita harus merusak class lama; pada kode AFTER kita cukup membuat 1 file baru tanpa mengubah class yang sudah berjalan."

### 🏅 Peringkat 4: Pelanggaran LSP pada Kontrak Repositori
- **ID Kasus:** LSP-01 (`BaseRepository.java` vs `StudentRepository.java` method `create()` vs `createWithId()`).
- **Alasan Pemilihan untuk UTS:**
  - *Menunjukkan Ketajaman Analisis Tinggi:* LSP adalah prinsip yang paling sering disalahpahami oleh mahasiswa. Menemukan kasus nyata di mana subclass melanggar asumsi ID parent class membuktikan penguasaan materi ADPL tingkat lanjut (*distinction grade*).
  - *Komentar Developer Sendiri sebagai Bukti:* Developer asli bahkan menulis komentar `* Berbeda dengan create() bawaan yang auto-generate ID`, yang menjadi bukti tak terbantahkan bahwa kontrak parent class telah dilanggar.

### 🏅 Peringkat 5: Pelanggaran ISP pada Interface "Mati" (`Filterable` & `Exportable`)
- **ID Kasus:** ISP-01 & ISP-02 (`model/Filterable.java` dan `model/Exportable.java`).
- **Alasan Pemilihan untuk UTS:**
  - *Kritik Desain yang Berbobot:* Menyoroti kebiasaan buruk membuat interface "formalitas" yang dipaksakan pada entity tanpa pernah dikonsumsi oleh lapisan pemanggil.
  - *Dampak Pembersihan Kode:* Menghapus dependensi yang tidak terpakai membuat domain entity menjadi POJO murni (*Plain Old Java Object*) yang bersih.

---
*Dokumen ini disusun sebagai hasil audit statis kode sumber aktual proyek TubesPBO untuk memenuhi persyaratan penugasan UTS ADPL.*

