package com.smartschool.permit.tubespbo.gui.dashboard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import com.smartschool.permit.tubespbo.app.UserSession;
import com.smartschool.permit.tubespbo.model.AdminUser;
import com.smartschool.permit.tubespbo.model.StudentPermit;
import com.smartschool.permit.tubespbo.model.enums.PermitType;
import com.smartschool.permit.tubespbo.app.AppServices;
import com.smartschool.permit.tubespbo.service.PermitService;
import com.smartschool.permit.tubespbo.util.DateUtils;
import com.smartschool.permit.tubespbo.util.SchoolUtils;

/**
 * Panel admin untuk mengelola data siswa terlambat.
 * Fitur: tabel data, filter kelas, search nama, pagination 25/page, approve.
 */
public class LateEntryPanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField searchField;
    private JComboBox<String> classFilter;
    private JLabel pageLabel;

    private final PermitService permitService = AppServices.getPermitService();
    private List<StudentPermit> allPermits;     // data mentah dari Firestore
    private List<StudentPermit> filteredPermits; // data setelah filter
    private static final int PAGE_SIZE = 25;
    private int currentPage = 0;

    public LateEntryPanel() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // === TOP: Title + Filter + Search ===
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Siswa Terlambat");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));

        // Class filter dropdown (from SchoolUtils)
        filterPanel.add(new JLabel("Kelas:"));
        classFilter = new JComboBox<>();
        classFilter.addItem("Semua");
        for (String cls : SchoolUtils.getAllClasses()) {
            classFilter.addItem(cls);
        }
        classFilter.addActionListener(e -> { currentPage = 0; applyFilter(); });
        filterPanel.add(classFilter);

        filterPanel.add(Box.createHorizontalStrut(10));

        // Search field
        filterPanel.add(new JLabel("Cari:"));
        searchField = new JTextField(12);
        searchField.addActionListener(e -> { currentPage = 0; applyFilter(); });
        filterPanel.add(searchField);

        JButton searchBtn = new JButton("Cari");
        searchBtn.addActionListener(e -> { currentPage = 0; applyFilter(); });
        filterPanel.add(searchBtn);

        topPanel.add(filterPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // === CENTER: Table ===
        String[] columns = {"No", "Nama", "Kelas", "Alasan", "Waktu Tiba", "Menit", "Status", "Disetujui Oleh"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(4).setPreferredWidth(130);
        table.getColumnModel().getColumn(5).setPreferredWidth(60);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // === BOTTOM: Pagination + Actions ===
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Pagination controls
        JPanel pagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton prevBtn = new JButton("← Sebelumnya");
        prevBtn.addActionListener(e -> { if (currentPage > 0) { currentPage--; renderPage(); } });
        pageLabel = new JLabel("Halaman 1");
        JButton nextBtn = new JButton("Berikutnya →");
        nextBtn.addActionListener(e -> {
            if (filteredPermits != null && (currentPage + 1) * PAGE_SIZE < filteredPermits.size()) {
                currentPage++;
                renderPage();
            }
        });
        pagePanel.add(prevBtn);
        pagePanel.add(pageLabel);
        pagePanel.add(nextBtn);
        bottomPanel.add(pagePanel, BorderLayout.CENTER);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton approveBtn = new JButton("Approve Terpilih");
        approveBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Pilih baris yang ingin di-approve!", "Info", JOptionPane.WARNING_MESSAGE);
                return;
            }
            approvePermit(row);
        });

        JButton deleteBtn = new JButton("Hapus Terpilih");
        deleteBtn.setForeground(Color.RED);
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus!", "Info", JOptionPane.WARNING_MESSAGE);
                return;
            }
            deletePermit(row);
        });

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadData());
        
        actionPanel.add(approveBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(refreshBtn);
        bottomPanel.add(actionPanel, BorderLayout.WEST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void loadData() {
        String schoolId = UserSession.getInstance().getSchoolId();
        if (schoolId == null) return;

        new SwingWorker<List<StudentPermit>, Void>() {
            @Override
            protected List<StudentPermit> doInBackground() {
                return permitService.getPermitsByType(schoolId, PermitType.LATE_ENTRY);
            }

            @Override
            protected void done() {
                try {
                    allPermits = get();
                    currentPage = 0;
                    applyFilter();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(LateEntryPanel.this,
                        "Error memuat data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void applyFilter() {
        if (allPermits == null) return;
        String classKey = classFilter.getSelectedIndex() == 0 ? null : (String) classFilter.getSelectedItem();
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) keyword = null;
        filteredPermits = permitService.filterPermits(allPermits, classKey, keyword);
        renderPage();
    }

    private void renderPage() {
        if (filteredPermits == null) return;
        tableModel.setRowCount(0);
        int start = currentPage * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, filteredPermits.size());
        int totalPages = Math.max(1, (int) Math.ceil((double) filteredPermits.size() / PAGE_SIZE));

        for (int i = start; i < end; i++) {
            StudentPermit p = filteredPermits.get(i);
            String statusText;
            String approvedByText = p.getApprovedBy() != null ? p.getApprovedBy() : "-";

            if (p.getStatus() == null) {
                // Data lama atau field status kosong
                statusText = "Disetujui";
                if (approvedByText.equals("-")) approvedByText = "Admin";
            } else {
                statusText = p.isPending() ? "Menunggu" : "Disetujui";
            }

            tableModel.addRow(new Object[]{
                i + 1,
                p.getStudentName(),
                p.getClassName(),
                p.getReason(),
                p.getArrivalTimestamp() > 0 ? DateUtils.formatDateTime(p.getArrivalTimestamp())
                    : DateUtils.formatDateTime(p.getTimestamp()),
                p.getDurationMinutes(),
                statusText,
                approvedByText
            });
        }

        pageLabel.setText("Halaman " + (currentPage + 1) + " / " + totalPages
            + " (" + filteredPermits.size() + " data)");
    }

    private void approvePermit(int tableRow) {
        int dataIndex = currentPage * PAGE_SIZE + tableRow;
        if (filteredPermits == null || dataIndex >= filteredPermits.size()) return;
        StudentPermit permit = filteredPermits.get(dataIndex);

        if (permit.getStatus() != null && !permit.isPending()) {
            JOptionPane.showMessageDialog(this, "Izin ini sudah disetujui!", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        AdminUser admin = UserSession.getInstance().getCurrentUser();
        int confirm = JOptionPane.showConfirmDialog(this,
            "Approve izin terlambat untuk " + permit.getStudentName() + "?",
            "Konfirmasi", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    permitService.approvePermit(permit.getId(), admin);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(LateEntryPanel.this,
                            "Berhasil di-approve!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        loadData();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(LateEntryPanel.this,
                            "Gagal approve: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }

    private void deletePermit(int tableRow) {
        int dataIndex = currentPage * PAGE_SIZE + tableRow;
        if (filteredPermits == null || dataIndex >= filteredPermits.size()) return;
        StudentPermit permit = filteredPermits.get(dataIndex);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Hapus data izin " + permit.getStudentName() + "? Tindakan ini tidak bisa dibatalkan.",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    permitService.deletePermit(permit.getId());
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(LateEntryPanel.this, "Data berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        loadData();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(LateEntryPanel.this, "Gagal menghapus: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }
}
