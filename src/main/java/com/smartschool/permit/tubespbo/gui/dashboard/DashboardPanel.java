package com.smartschool.permit.tubespbo.gui.dashboard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import com.smartschool.permit.tubespbo.app.UserSession;
import com.smartschool.permit.tubespbo.model.StatisticsData;
import com.smartschool.permit.tubespbo.model.StudentPermit;
import com.smartschool.permit.tubespbo.app.AppServices;
import com.smartschool.permit.tubespbo.service.PermitService;
import com.smartschool.permit.tubespbo.service.ReportService;
import com.smartschool.permit.tubespbo.util.DateUtils;

public class DashboardPanel extends JPanel {

    private JLabel cardLate, cardExit, cardPending, cardTotal;
    private DefaultTableModel tableModel;

    private final ReportService reportService = AppServices.getReportService();
    private final PermitService permitService = AppServices.getPermitService();

    public DashboardPanel() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- TITLE ---
        JLabel titleLabel = new JLabel("Dashboard / Laporan Histori");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // --- CENTER CONTENT ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // 1. CARDS
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        cardsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        cardLate = new JLabel("...");
        cardExit = new JLabel("...");
        cardPending = new JLabel("...");
        cardTotal = new JLabel("...");

        cardsPanel.add(createCard("Terlambat Hari Ini", cardLate));
        cardsPanel.add(createCard("Izin Keluar Hari Ini", cardExit));
        cardsPanel.add(createCard("Menunggu ACC", cardPending));
        cardsPanel.add(createCard("Total Riwayat", cardTotal));

        centerPanel.add(cardsPanel);
        centerPanel.add(Box.createVerticalStrut(20));

        // 2. TABLE 
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBorder(BorderFactory.createTitledBorder("Aktivitas Terbaru"));

        String[] columns = {"Nama", "Kelas", "Tipe", "Alasan", "Status", "Tanggal"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        tableContainer.add(new JScrollPane(table), BorderLayout.CENTER);

        // Refresh button
        JButton refreshBtn = new JButton("Refresh Data");
        refreshBtn.addActionListener(e -> loadData());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(refreshBtn);
        tableContainer.add(btnPanel, BorderLayout.SOUTH);

        centerPanel.add(tableContainer);

        add(centerPanel, BorderLayout.CENTER);
    }

    public void loadData() {
        String schoolId = UserSession.getInstance().getSchoolId();
        if (schoolId == null) return;

        new SwingWorker<Void, Void>() {
            private StatisticsData stats;
            private List<StudentPermit> permits;

            @Override
            protected Void doInBackground() {
                stats = reportService.getDashboardStats(schoolId);
                permits = permitService.getAllPermits(schoolId);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    // Update cards — data per hari ini + pending total
                    cardLate.setText(String.valueOf(stats.getTodayLateCount()));
                    cardExit.setText(String.valueOf(stats.getTodayExitCount()));
                    cardPending.setText(String.valueOf(stats.getPendingCount()));
                    cardTotal.setText(String.valueOf(permits.size()));

                    // Update table (max 15 terbaru)
                    tableModel.setRowCount(0);
                    int limit = Math.min(permits.size(), 15);
                    for (int i = 0; i < limit; i++) {
                        StudentPermit p = permits.get(i);
                        tableModel.addRow(new Object[]{
                            p.getStudentName(),
                            p.getClassName(),
                            p.getType() != null ? (p.isLateEntry() ? "Terlambat" : "Izin Keluar") : "-",
                            p.getReason(),
                            p.getStatus() != null ? (p.isPending() ? "Menunggu" : "Disetujui") : "-",
                            DateUtils.formatDateTime(p.getTimestamp())
                        });
                    }
                } catch (Exception ex) {
                    System.err.println("Error loading dashboard: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private JPanel createCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }
}
