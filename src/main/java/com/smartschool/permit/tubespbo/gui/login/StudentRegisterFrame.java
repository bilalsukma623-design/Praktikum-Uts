package com.smartschool.permit.tubespbo.gui.login;

import javax.swing.*;
import com.smartschool.permit.tubespbo.gui.formDispen.FormKeterlambatan;
import com.smartschool.permit.tubespbo.service.StudentAuthService;
import com.smartschool.permit.tubespbo.repository.StudentRepository;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Frame registrasi akun siswa baru.
 */
public class StudentRegisterFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField namaField;
    private ButtonGroup tingkatGroup;
    private ButtonGroup kelasGroup;
    private JRadioButton radioX, radioXI, radioXII;
    private JRadioButton radioA, radioB, radioC, radioD, radioE, radioF;
    private JRadioButton radioG, radioH, radioI, radioJ, radioK;
    private JLabel dipilihLabel;
    private JButton registerButton;

    public StudentRegisterFrame() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Daftar Akun Siswa");
        setResizable(false);

        tingkatGroup = new ButtonGroup();
        kelasGroup = new ButtonGroup();

        // === Header ===
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));

        JLabel titleLabel = new JLabel("Daftar Akun Siswa");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("SMAN 1 Rejotangan");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);

        // === Form Panel ===
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;

        // Nama Lengkap
        formPanel.add(new JLabel("Nama Lengkap:"), gbc);
        gbc.gridy++;
        namaField = new JTextField();
        formPanel.add(namaField, gbc);

        // Email
        gbc.gridy++;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridy++;
        emailField = new JTextField();
        formPanel.add(emailField, gbc);

        // Password
        gbc.gridy++;
        formPanel.add(new JLabel("Password (min. 6 karakter):"), gbc);
        gbc.gridy++;
        passwordField = new JPasswordField();
        formPanel.add(passwordField, gbc);

        // Konfirmasi Password
        gbc.gridy++;
        formPanel.add(new JLabel("Konfirmasi Password:"), gbc);
        gbc.gridy++;
        confirmPasswordField = new JPasswordField();
        formPanel.add(confirmPasswordField, gbc);

        // Spacer
        gbc.gridy++;
        formPanel.add(new JLabel(" "), gbc);

        // Tingkat & Kelas
        JLabel kelasTitle = new JLabel("Tingkat & Kelas");
        kelasTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy++;
        formPanel.add(kelasTitle, gbc);

        gbc.gridy++;
        formPanel.add(new JLabel("Tingkat:"), gbc);

        radioX = new JRadioButton("X");
        radioXI = new JRadioButton("XI");
        radioXII = new JRadioButton("XII");
        tingkatGroup.add(radioX); tingkatGroup.add(radioXI); tingkatGroup.add(radioXII);

        JPanel tingkatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        tingkatPanel.add(radioX); tingkatPanel.add(radioXI); tingkatPanel.add(radioXII);
        gbc.gridy++;
        formPanel.add(tingkatPanel, gbc);

        gbc.gridy++;
        formPanel.add(new JLabel("Kelas:"), gbc);

        radioA = new JRadioButton("A"); radioB = new JRadioButton("B");
        radioC = new JRadioButton("C"); radioD = new JRadioButton("D");
        radioE = new JRadioButton("E"); radioF = new JRadioButton("F");
        radioG = new JRadioButton("G"); radioH = new JRadioButton("H");
        radioI = new JRadioButton("I"); radioJ = new JRadioButton("J");
        radioK = new JRadioButton("K");

        kelasGroup.add(radioA); kelasGroup.add(radioB); kelasGroup.add(radioC);
        kelasGroup.add(radioD); kelasGroup.add(radioE); kelasGroup.add(radioF);
        kelasGroup.add(radioG); kelasGroup.add(radioH); kelasGroup.add(radioI);
        kelasGroup.add(radioJ); kelasGroup.add(radioK);

        JPanel kelasPanel = new JPanel(new GridLayout(2, 6, 5, 5));
        kelasPanel.add(radioA); kelasPanel.add(radioB); kelasPanel.add(radioC);
        kelasPanel.add(radioD); kelasPanel.add(radioE); kelasPanel.add(radioF);
        kelasPanel.add(radioG); kelasPanel.add(radioH); kelasPanel.add(radioI);
        kelasPanel.add(radioJ); kelasPanel.add(radioK);
        gbc.gridy++;
        formPanel.add(kelasPanel, gbc);

        dipilihLabel = new JLabel("Dipilih: -");
        dipilihLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dipilihLabel.setForeground(new Color(0, 102, 204));
        gbc.gridy++;
        formPanel.add(dipilihLabel, gbc);

        // Listener for radio buttons to update "Dipilih" label
        ActionListener updateDipilih = e -> {
            // Hide K for class X
            if (radioX.isSelected()) {
                radioK.setVisible(false);
                if (radioK.isSelected()) kelasGroup.clearSelection();
            } else {
                radioK.setVisible(true);
            }

            String t = radioX.isSelected() ? "X" : (radioXI.isSelected() ? "XI" : (radioXII.isSelected() ? "XII" : ""));
            String k = getSelectedKelas();
            if (!t.isEmpty() && !k.isEmpty()) dipilihLabel.setText("Dipilih: " + t + "-" + k);
            else if (!t.isEmpty()) dipilihLabel.setText("Dipilih: " + t);
            else dipilihLabel.setText("Dipilih: -");
        };

        JRadioButton[] allRadios = {radioX, radioXI, radioXII, radioA, radioB, radioC, radioD, radioE, radioF, radioG, radioH, radioI, radioJ, radioK};
        for (JRadioButton r : allRadios) r.addActionListener(updateDipilih);

        // === Button Panel ===
        registerButton = new JButton("Daftar");
        registerButton.addActionListener(this::handleRegister);

        JButton loginLink = new JButton("Sudah punya akun? Masuk");
        loginLink.setBorderPainted(false);
        loginLink.setContentAreaFilled(false);
        loginLink.setForeground(new Color(0, 102, 204));
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.addActionListener(e -> {
            this.dispose();
            new LoginFrame().setVisible(true);
        });

        JButton backButton = new JButton("Kembali");
        backButton.addActionListener(e -> {
            this.dispose();
            new LoginFrame().setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 15, 20));

        JPanel registerRow = new JPanel(new GridLayout(1, 2, 10, 0));
        registerRow.add(backButton);
        registerRow.add(registerButton);
        registerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        loginLink.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(registerRow);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(loginLink);

        // === Main Layout with ScrollPane ===
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(headerPanel);
        mainPanel.add(formPanel);
        mainPanel.add(buttonPanel);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(scrollPane, BorderLayout.CENTER);

        setSize(480, 650);
        setLocationRelativeTo(null);
    }

    private String getSelectedTingkat() {
        if (radioX.isSelected()) return "X";
        if (radioXI.isSelected()) return "XI";
        if (radioXII.isSelected()) return "XII";
        return "";
    }

    private String getSelectedKelas() {
        if (radioA.isSelected()) return "A";
        if (radioB.isSelected()) return "B";
        if (radioC.isSelected()) return "C";
        if (radioD.isSelected()) return "D";
        if (radioE.isSelected()) return "E";
        if (radioF.isSelected()) return "F";
        if (radioG.isSelected()) return "G";
        if (radioH.isSelected()) return "H";
        if (radioI.isSelected()) return "I";
        if (radioJ.isSelected()) return "J";
        if (radioK.isSelected()) return "K";
        return "";
    }

    private void handleRegister(ActionEvent evt) {
        String nama = namaField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPass = new String(confirmPasswordField.getPassword());

        // Validasi
        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama lengkap harus diisi!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (email.isEmpty() || !email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Email tidak valid!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password minimal 6 karakter!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!password.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Konfirmasi password tidak sama!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tingkat = getSelectedTingkat();
        String kelas = getSelectedKelas();
        if (tingkat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih tingkat kelas!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (kelas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih kelas!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Format nama ke Title Case
        String[] words = nama.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1).toLowerCase()).append(" ");
        }
        String formattedName = sb.toString().trim();
        String fullClassName = tingkat + "-" + kelas;

        int confirm = JOptionPane.showConfirmDialog(this,
            "Apakah data sudah benar?\n\nNama: " + formattedName + "\nKelas: " + fullClassName + "\nEmail: " + email,
            "Konfirmasi", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        registerButton.setEnabled(false);
        registerButton.setText("Memproses...");
        setCursor(new Cursor(Cursor.WAIT_CURSOR));

        final String finalName = formattedName;

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                StudentAuthService authService = new StudentAuthService(new StudentRepository());
                authService.register(email, password, finalName, fullClassName);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(StudentRegisterFrame.this,
                        "Registrasi berhasil! Selamat datang, " + finalName + ".",
                        "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                    StudentRegisterFrame.this.dispose();
                    new FormKeterlambatan().setVisible(true);
                } catch (Exception ex) {
                    String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    JOptionPane.showMessageDialog(StudentRegisterFrame.this, msg, "Registrasi Gagal", JOptionPane.ERROR_MESSAGE);
                } finally {
                    registerButton.setEnabled(true);
                    registerButton.setText("Daftar");
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        }.execute();
    }
}
