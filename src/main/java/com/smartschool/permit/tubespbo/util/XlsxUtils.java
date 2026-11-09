package com.smartschool.permit.tubespbo.util;

import com.smartschool.permit.tubespbo.gui.helper.SwingExportHelper;
import javax.swing.JTable;

/**
 * Utility untuk export JTable data ke file XLSX (Excel).
 *
 * Catatan Refactoring (SRP):
 * Implementasi konkret telah didelegasikan kepada:
 * 1. ExcelDocumentGenerator (util.export) untuk pembangkitan POI murni.
 * 2. SwingExportHelper (gui.helper) untuk penanganan interaksi dialog pengguna.
 *
 * Kelas ini dipertahankan sebagai facade pembungkus untuk menjaga kompatibilitas ke belakang.
 */
public class XlsxUtils {

    /**
     * Mengekspor data tabel Swing ke file Excel melalui dialog pengguna.
     *
     * @param table Komponen JTable yang akan diekspor
     * @param defaultFileName Nama file usulan default
     */
    public static void exportTable(JTable table, String defaultFileName) {
        SwingExportHelper.exportTableWithDialog(null, table, defaultFileName);
    }
}
