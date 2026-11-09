package com.smartschool.permit.tubespbo.gui.dashboard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.smartschool.permit.tubespbo.app.UserSession;
import com.smartschool.permit.tubespbo.model.Student;
import com.smartschool.permit.tubespbo.model.StudentPermit;
import com.smartschool.permit.tubespbo.repository.StudentRepository;
import com.smartschool.permit.tubespbo.repository.PermitRepository;

public class StudentDataPanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private final StudentRepository studentRepo = new StudentRepository();
    private final PermitRepository permitRepo = new PermitRepository();

    public StudentDataPanel() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Data & Analitik Siswa");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        String[] cols = {
            "No", "Nama", "Kelas", "Email", 
            "Terlambat (Frekuensi)", "Terlambat (Total Menit)", 
            "Izin (Frekuensi)", "Izin (Total Menit)", 
            "Aksi"
        };

        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        
        table.getColumnModel().getColumn(8).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(8).setCellEditor(new ButtonEditor(new JCheckBox()));

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadData());
        bottomPanel.add(refreshBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        String schoolId = UserSession.getInstance().getSchoolId();
        if (schoolId == null) return;

        new SwingWorker<List<Object[]>, Void>() {
            @Override
            protected List<Object[]> doInBackground() throws Exception {
                List<Student> students = studentRepo.getAll();
                List<StudentPermit> allPermits = permitRepo.getBySchool(schoolId);

                Map<String, List<StudentPermit>> permitsByStudent = new HashMap<>();
                for (StudentPermit p : allPermits) {
                    String key = p.getStudentId();
                    if (key == null || key.isEmpty()) {
                        key = p.getStudentName() + "|" + p.getClassName();
                    }
                    permitsByStudent.computeIfAbsent(key, k -> new ArrayList<>()).add(p);
                }

                List<Object[]> rows = new ArrayList<>();
                int no = 1;
                for (Student s : students) {
                    String key = s.getId();
                    List<StudentPermit> studentPermits = permitsByStudent.getOrDefault(key, new ArrayList<>());
                    
                    if (studentPermits.isEmpty()) {
                        studentPermits = permitsByStudent.getOrDefault(s.getFullName() + "|" + s.getClassName(), new ArrayList<>());
                    }

                    int lateFreq = 0;
                    int lateMins = 0;
                    int exitFreq = 0;
                    int exitMins = 0;

                    for (StudentPermit p : studentPermits) {
                        if (p.isLateEntry()) {
                            lateFreq++;
                            lateMins += p.getDurationMinutes();
                        } else if (p.isExitPermit()) {
                            exitFreq++;
                            exitMins += p.getDurationMinutes();
                        }
                    }

                    rows.add(new Object[]{
                        no++,
                        s.getFullName(),
                        s.getClassName(),
                        s.getEmail(),
                        lateFreq,
                        lateMins + " Min",
                        exitFreq,
                        exitMins + " Min",
                        "Lihat Riwayat"
                    });
                }
                return rows;
            }

            @Override
            protected void done() {
                try {
                    List<Object[]> rows = get();
                    tableModel.setRowCount(0);
                    for (Object[] row : rows) {
                        tableModel.addRow(row);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(StudentDataPanel.this, "Gagal memuat data: " + ex.getMessage());
                }
            }
        }.execute();
    }

    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private String label;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            JButton button = new JButton(label);
            button.addActionListener(e -> {
                fireEditingStopped();
                String name = (String) tableModel.getValueAt(row, 1);
                String className = (String) tableModel.getValueAt(row, 2);
                showHistory(name, className);
            });
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            return super.stopCellEditing();
        }

        private void showHistory(String name, String className) {
             new StudentPermitHistoryFrame(name, className).setVisible(true);
        }
    }
}
