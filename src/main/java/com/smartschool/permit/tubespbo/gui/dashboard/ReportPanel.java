package com.smartschool.permit.tubespbo.gui.dashboard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.smartschool.permit.tubespbo.app.UserSession;
import com.smartschool.permit.tubespbo.model.PermitSummary;
import com.smartschool.permit.tubespbo.app.AppServices;
import com.smartschool.permit.tubespbo.service.ReportService;

/**
 * Panel laporan rekap izin per siswa dan per bulan/kelas.
 * Fitur: Ringkasan siswa, Rekap bulanan, Export XLSX.
 */
public class ReportPanel extends JPanel {

    private DefaultTableModel summaryTableModel;
    private DefaultTableModel recapTableModel;
    private JTable summaryTable;
    private JTable recapTable;
    private JSpinner yearSpinner, monthSpinner;

    private final ReportService reportService = AppServices.getReportService();

    public ReportPanel() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Laporan & Rekap");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Split into two sections: Summary (top) & Monthly Recap (bottom)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        // --- TOP: Student Summary ---
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Ringkasan Per Siswa (Terbanyak)"));

        JPanel sumTopPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exportSumBtn = new JButton("Export XLSX");
        exportSumBtn.addActionListener(e -> com.smartschool.permit.tubespbo.util.XlsxUtils.exportTable(summaryTable, "Rekap_Siswa"));
        sumTopPanel.add(exportSumBtn);
        summaryPanel.add(sumTopPanel, BorderLayout.NORTH);

        String[] sumCols = {"No", "Nama", "Kelas", "Terlambat", "Izin Keluar", "Total"};
        summaryTableModel = new DefaultTableModel(sumCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        summaryTable = new JTable(summaryTableModel);
        summaryTable.setRowHeight(25);
        summaryPanel.add(new JScrollPane(summaryTable), BorderLayout.CENTER);

        splitPane.setTopComponent(summaryPanel);

        // --- BOTTOM: Monthly Recap ---
        JPanel recapPanel = new JPanel(new BorderLayout());
        recapPanel.setBorder(BorderFactory.createTitledBorder("Rekap Bulanan Per Kelas"));

        // Filter controls
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        LocalDate now = LocalDate.now();
        yearSpinner = new JSpinner(new SpinnerNumberModel(now.getYear(), 2020, 2030, 1));
        yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "#"));
        monthSpinner = new JSpinner(new SpinnerNumberModel(now.getMonthValue(), 1, 12, 1));

        JButton filterBtn = new JButton("Tampilkan");
        filterBtn.addActionListener(e -> loadMonthlyRecap());

        JButton exportRecapBtn = new JButton("Export XLSX");
        exportRecapBtn.addActionListener(e -> com.smartschool.permit.tubespbo.util.XlsxUtils.exportTable(recapTable, "Rekap_Bulanan_" + yearSpinner.getValue() + "_" + monthSpinner.getValue()));

        filterPanel.add(new JLabel("Tahun: "));
        filterPanel.add(yearSpinner);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(new JLabel("Bulan: "));
        filterPanel.add(monthSpinner);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(filterBtn);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(exportRecapBtn);
        recapPanel.add(filterPanel, BorderLayout.NORTH);

        String[] recapCols = {"Kelas", "Terlambat", "Izin Keluar", "Total"};
        recapTableModel = new DefaultTableModel(recapCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        recapTable = new JTable(recapTableModel);
        recapTable.setRowHeight(25);
        recapPanel.add(new JScrollPane(recapTable), BorderLayout.CENTER);

        splitPane.setBottomComponent(recapPanel);

        add(splitPane, BorderLayout.CENTER);

        // Refresh button
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = new JButton("Refresh Semua");
        refreshBtn.addActionListener(e -> loadData());
        btnPanel.add(refreshBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    public void loadData() {
        loadStudentSummary();
        loadMonthlyRecap();
    }

    private void loadStudentSummary() {
        String schoolId = UserSession.getInstance().getSchoolId();
        if (schoolId == null) return;

        new SwingWorker<List<PermitSummary>, Void>() {
            @Override
            protected List<PermitSummary> doInBackground() {
                return reportService.getStudentSummary(schoolId);
            }

            @Override
            protected void done() {
                try {
                    List<PermitSummary> summaries = get();
                    summaryTableModel.setRowCount(0);
                    int limit = Math.min(summaries.size(), 20);
                    for (int i = 0; i < limit; i++) {
                        PermitSummary s = summaries.get(i);
                        summaryTableModel.addRow(new Object[]{
                            i + 1,
                            s.getStudentName(),
                            s.getClassName(),
                            s.getLateCount(),
                            s.getExitCount(),
                            s.getTotalCount()
                        });
                    }
                } catch (Exception ex) {
                    System.err.println("Error loading summary: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void loadMonthlyRecap() {
        String schoolId = UserSession.getInstance().getSchoolId();
        if (schoolId == null) return;

        int year = (int) yearSpinner.getValue();
        int month = (int) monthSpinner.getValue();

        new SwingWorker<Map<String, int[]>, Void>() {
            @Override
            protected Map<String, int[]> doInBackground() {
                return reportService.getMonthlyRecap(schoolId, year, month);
            }

            @Override
            protected void done() {
                try {
                    Map<String, int[]> recap = get();
                    recapTableModel.setRowCount(0);
                    for (Map.Entry<String, int[]> entry : recap.entrySet()) {
                        int[] counts = entry.getValue();
                        recapTableModel.addRow(new Object[]{
                            entry.getKey(),
                            counts[0],
                            counts[1],
                            counts[0] + counts[1]
                        });
                    }
                } catch (Exception ex) {
                    System.err.println("Error loading recap: " + ex.getMessage());
                }
            }
        }.execute();
    }
}
