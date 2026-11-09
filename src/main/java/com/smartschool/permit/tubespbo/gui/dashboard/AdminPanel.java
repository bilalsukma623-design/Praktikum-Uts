package com.smartschool.permit.tubespbo.gui.dashboard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import com.smartschool.permit.tubespbo.app.UserSession;
import com.smartschool.permit.tubespbo.model.AdminUser;
import com.smartschool.permit.tubespbo.repository.AdminRepository;
import com.smartschool.permit.tubespbo.service.AdminService;
import com.smartschool.permit.tubespbo.util.DateUtils;

/**
 * Panel admin untuk mengelola akun admin piket.
 * Hanya Super Admin yang bisa menambah/menghapus admin.
 */
public class AdminPanel extends JPanel {

    private DefaultTableModel tableModel;
    private final AdminRepository adminRepo = new AdminRepository();
    private final AdminService adminService = new AdminService(adminRepo);
    private List<AdminUser> currentAdmins;

    // Form fields
    private JTextField nameField, emailField;
    private JPasswordField passwordField;
    private JButton addBtn, deleteBtn, changePassBtn;
    private JTable table;

    public AdminPanel() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- TITLE ---
        JLabel titleLabel = new JLabel("Kelola Admin");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // --- TABLE ---
        String[] columns = {"No", "Nama", "Email", "Role", "Dibuat Pada"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- BOTTOM: Form + Actions ---
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // Form tambah admin
        JPanel formPanel = new JPanel();
        formPanel.setBorder(BorderFactory.createTitledBorder("Tambah Admin Baru (Hanya Super Admin)"));
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.anchor = GridBagConstraints.WEST;

        nameField = new JTextField(15);
        emailField = new JTextField(15);
        passwordField = new JPasswordField(15);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nama:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 3;
        formPanel.add(emailField, gbc);

        gbc.gridx = 4;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 5;
        formPanel.add(passwordField, gbc);

        addBtn = new JButton("Tambah Admin");
        addBtn.addActionListener(e -> createAdmin());
        gbc.gridx = 6;
        formPanel.add(addBtn, gbc);

        bottomPanel.add(formPanel, BorderLayout.CENTER);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        deleteBtn = new JButton("Hapus Terpilih");
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Pilih admin yang ingin dihapus!", "Info", JOptionPane.WARNING_MESSAGE);
                return;
            }
            deleteAdmin(row);
        });

        changePassBtn = new JButton("Ubah Password");
        changePassBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Pilih admin yang ingin diubah passwordnya!", "Info", JOptionPane.WARNING_MESSAGE);
                return;
            }
            changeAdminPassword(row);
        });

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadData());

        actionPanel.add(changePassBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(refreshBtn);
        bottomPanel.add(actionPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // Disable form if not super admin
        updateFormAccess();
    }

    private void updateFormAccess() {
        boolean isSuperAdmin = UserSession.getInstance().isSuperAdmin();
        addBtn.setEnabled(isSuperAdmin);
        deleteBtn.setEnabled(isSuperAdmin);
        changePassBtn.setEnabled(isSuperAdmin);
        nameField.setEnabled(isSuperAdmin);
        emailField.setEnabled(isSuperAdmin);
        passwordField.setEnabled(isSuperAdmin);
        if (!isSuperAdmin) {
            addBtn.setToolTipText("Hanya Super Admin yang bisa menambah admin");
            deleteBtn.setToolTipText("Hanya Super Admin yang bisa menghapus admin");
            changePassBtn.setToolTipText("Hanya Super Admin yang bisa mengubah password");
        }
    }

    public void loadData() {
        String schoolId = UserSession.getInstance().getSchoolId();
        if (schoolId == null) return;

        new SwingWorker<List<AdminUser>, Void>() {
            @Override
            protected List<AdminUser> doInBackground() {
                return adminService.getAllAdmins(schoolId);
            }

            @Override
            protected void done() {
                try {
                    currentAdmins = get();
                    tableModel.setRowCount(0);
                    for (int i = 0; i < currentAdmins.size(); i++) {
                        AdminUser a = currentAdmins.get(i);
                        tableModel.addRow(new Object[]{
                            i + 1,
                            a.getName(),
                            a.getEmail(),
                            a.getRole() != null ? a.getRole().name() : "-",
                            a.getCreatedAt() > 0 ? DateUtils.formatDateTime(a.getCreatedAt()) : "-"
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(AdminPanel.this, "Error memuat data admin: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void createAdmin() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String schoolId = UserSession.getInstance().getSchoolId();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password minimal 6 karakter!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        addBtn.setEnabled(false);
        addBtn.setText("Memproses...");

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                adminService.createAdmin(email, password, name, schoolId);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(AdminPanel.this, "Admin baru berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    nameField.setText("");
                    emailField.setText("");
                    passwordField.setText("");
                    loadData();
                } catch (Exception ex) {
                    String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    JOptionPane.showMessageDialog(AdminPanel.this, "Gagal tambah admin: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    addBtn.setEnabled(true);
                    addBtn.setText("Tambah Admin");
                }
            }
        }.execute();
    }

    private void deleteAdmin(int row) {
        if (currentAdmins == null || row >= currentAdmins.size()) return;
        AdminUser target = currentAdmins.get(row);

        // Prevent self-delete
        AdminUser currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getId().equals(target.getId())) {
            JOptionPane.showMessageDialog(this, "Tidak bisa menghapus akun sendiri!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Hapus admin " + target.getName() + " (" + target.getEmail() + ")?",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    adminService.deleteAdmin(target.getId());
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(AdminPanel.this, "Admin berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        loadData();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(AdminPanel.this, "Gagal hapus: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }

    private void changeAdminPassword(int row) {
        if (currentAdmins == null || row >= currentAdmins.size()) return;
        AdminUser target = currentAdmins.get(row);

        // Hanya boleh ganti password admin biasa
        if (target.getRole() == com.smartschool.permit.tubespbo.model.enums.UserRole.SUPER_ADMIN) {
            JOptionPane.showMessageDialog(this, "Tidak bisa mengubah password sesama Super Admin!", "Akses Ditolak", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPasswordField pf = new JPasswordField();
        int okCxl = JOptionPane.showConfirmDialog(null, pf, "Masukkan Password Baru untuk " + target.getName(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (okCxl == JOptionPane.OK_OPTION) {
            String newPassword = new String(pf.getPassword());
            if (newPassword.length() < 6) {
                JOptionPane.showMessageDialog(this, "Password minimal 6 karakter!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                return;
            }

            changePassBtn.setEnabled(false);
            changePassBtn.setText("Memproses...");

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    adminService.changePassword(target.getId(), newPassword);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(AdminPanel.this, "Password berhasil diubah!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                        JOptionPane.showMessageDialog(AdminPanel.this, "Gagal mengubah password: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        changePassBtn.setEnabled(true);
                        changePassBtn.setText("Ubah Password");
                    }
                }
            }.execute();
        }
    }
}
