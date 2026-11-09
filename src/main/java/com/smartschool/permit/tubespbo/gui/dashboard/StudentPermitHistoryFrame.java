package com.smartschool.permit.tubespbo.gui.dashboard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import com.smartschool.permit.tubespbo.model.StudentPermit;
import com.smartschool.permit.tubespbo.repository.PermitRepository;
import com.smartschool.permit.tubespbo.util.DateUtils;

public class StudentPermitHistoryFrame extends JFrame {

    private DefaultTableModel tableModel;
    private JTable table;
    private final String studentName;
    private final String className;
    private final PermitRepository permitRepo = new PermitRepository();

    public StudentPermitHistoryFrame(String studentName, String className) {
        this.studentName = studentName;
        this.className = className;
        
        setTitle("Riwayat Izin: " + studentName + " (" + className + ")");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("Riwayat Izin & Keterlambatan");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        topPanel.add(titleLabel);
        add(topPanel, BorderLayout.NORTH);

        String[] cols = {"No", "Tipe", "Waktu", "Alasan", "Durasi", "Status", "Disetujui Oleh"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeBtn = new JButton("Tutup");
        closeBtn.addActionListener(e -> dispose());
        bottomPanel.add(closeBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        new SwingWorker<List<StudentPermit>, Void>() {
            @Override
            protected List<StudentPermit> doInBackground() throws Exception {
                // Fetch permits for this specific student
                // Standard search by name and class (fallback version)
                List<StudentPermit> all = permitRepo.getBySchool("sch_001");
                return all.stream()
                        .filter(p -> p.getStudentName().equalsIgnoreCase(studentName) && p.getClassName().equalsIgnoreCase(className))
                        .sorted((a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()))
                        .collect(java.util.stream.Collectors.toList());
            }

            @Override
            protected void done() {
                try {
                    List<StudentPermit> list = get();
                    tableModel.setRowCount(0);
                    int no = 1;
                    for (StudentPermit p : list) {
                        tableModel.addRow(new Object[]{
                            no++,
                            p.getType(),
                            DateUtils.formatDateTime(p.getTimestamp()),
                            p.getReason(),
                            p.getDurationMinutes() + " Min",
                            p.isPending() ? "Menunggu" : "Disetujui",
                            p.getApprovedBy() != null ? p.getApprovedBy() : "-"
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }
}
