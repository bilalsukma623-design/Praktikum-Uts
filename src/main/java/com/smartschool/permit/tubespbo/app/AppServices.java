package com.smartschool.permit.tubespbo.app;

import com.smartschool.permit.tubespbo.repository.PermitRepository;
import com.smartschool.permit.tubespbo.repository.interfaces.IPermitRepository;
import com.smartschool.permit.tubespbo.service.PermitService;
import com.smartschool.permit.tubespbo.service.ReportService;

/**
 * Service Locator / Composition Root terpusat untuk aplikasi desktop.
 * Menerapkan Dependency Inversion Principle (DIP):
 * Menyediakan titik tunggal penyedia dependensi antarmuka (abstraksi)
 * ke lapisan presentasi GUI, sehingga kelas-kelas GUI tidak perlu lagi
 * melakukan instansiasi 'new PermitRepository()' secara sporadis.
 */
public final class AppServices {

    private static IPermitRepository permitRepository;
    private static PermitService permitService;
    private static ReportService reportService;

    private AppServices() {
        // Mencegah instansiasi langsung
    }

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

    /**
     * Memungkinkan injeksi Mock Repository saat automated unit testing
     * tanpa memerlukan inisialisasi Firestore nyata.
     *
     * @param mockRepo Instance mock IPermitRepository
     */
    public static synchronized void setPermitRepositoryForTesting(IPermitRepository mockRepo) {
        permitRepository = mockRepo;
        permitService = mockRepo != null ? new PermitService(mockRepo) : null;
        reportService = mockRepo != null ? new ReportService(mockRepo) : null;
    }

    /**
     * Mengembalikan state repository ke default.
     */
    public static synchronized void reset() {
        permitRepository = null;
        permitService = null;
        reportService = null;
    }
}

