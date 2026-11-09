package com.smartschool.permit.tubespbo.gui.helper;

import com.smartschool.permit.tubespbo.util.export.ExcelDocumentGenerator;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Komponen helper presentasi khusus antarmuka Swing.
 * Menerapkan Single Responsibility Principle (SRP):
 * Hanya bertanggung jawab menangani alur interaksi antarmuka pengguna:
 * dialog file chooser, ekstraksi data dari JTable, pendelegasian ke generator Excel,
 * serta menampilkan notifikasi modal hasil kepada pengguna.
 */
public final class SwingExportHelper {

    private SwingExportHelper() {
        // Utility helper
    }

    /**
     * Memandu pengguna memilih berkas tujuan melalui dialog, mengekstrak data dari JTable,
     * dan mengekspornya ke format Excel menggunakan ExcelDocumentGenerator.
     *
     * @param parent Komponen induk Swing (dapat berupa null jika tidak ada frame acuan)
     * @param table Komponen JTable sumber data
     * @param defaultFileName Nama berkas usulan tanpa ekstensi
     */
    public static void exportTableWithDialog(Component parent, JTable table, String defaultFileName) {
        if (table == null) {
            JOptionPane.showMessageDialog(parent, "Tabel data kosong atau belum diinisialisasi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan Excel");
        fileChooser.setSelectedFile(new File(defaultFileName + ".xlsx"));

        int userSelection = fileChooser.showSaveDialog(parent);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getName().toLowerCase().endsWith(".xlsx")) {
            fileToSave = new File(fileToSave.getAbsolutePath() + ".xlsx");
        }

        try {
            TableModel model = table.getModel();
            List<String> headers = new ArrayList<>();
            for (int col = 0; col < model.getColumnCount(); col++) {
                headers.add(model.getColumnName(col));
            }

            List<List<Object>> rows = new ArrayList<>();
            for (int row = 0; row < model.getRowCount(); row++) {
                List<Object> rowData = new ArrayList<>();
                for (int col = 0; col < model.getColumnCount(); col++) {
                    rowData.add(model.getValueAt(row, col));
                }
                rows.add(rowData);
            }

            try (FileOutputStream fileOut = new FileOutputStream(fileToSave)) {
                ExcelDocumentGenerator.generate(headers, rows, fileOut);
            }

            JOptionPane.showMessageDialog(parent, 
                    "Laporan berhasil diexport ke:\n" + fileToSave.getAbsolutePath(), 
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, 
                    "Gagal export Excel: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}

