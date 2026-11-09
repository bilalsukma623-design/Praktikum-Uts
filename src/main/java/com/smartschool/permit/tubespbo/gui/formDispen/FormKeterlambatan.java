/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.smartschool.permit.tubespbo.gui.formDispen;

import com.smartschool.permit.tubespbo.model.StudentPermit;
import com.smartschool.permit.tubespbo.model.enums.PermitType;
import com.smartschool.permit.tubespbo.repository.PermitRepository;
import com.smartschool.permit.tubespbo.service.PermitService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Logger;

/**
 * Form for Late Entries (Keterlambatan)
 */
public class FormKeterlambatan extends javax.swing.JFrame {
    
    private static final Logger logger = Logger.getLogger(FormKeterlambatan.class.getName());
    private JRadioButton RadioAlphabetK;

    /**
     * Creates new form FormKeterlambatan
     */
    public FormKeterlambatan() {
        if (!com.smartschool.permit.tubespbo.app.UserSession.getInstance().isStudent()) {
            SwingUtilities.invokeLater(() -> {
                new com.smartschool.permit.tubespbo.gui.login.LoginFrame().setVisible(true);
            });
            this.dispose();
            return;
        }
        initComponents();
        applyCustomStyles();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    private void initComponents() {
        buttonGroup1 = new ButtonGroup();
        buttonGroup2 = new ButtonGroup();
        
        jPanel1 = new JPanel();
        jLabel1 = new JLabel("Catat Keterlambatan");
        jLabel2 = new JLabel("SMAN 1 Rejotangan");
        
        jPanel2 = new JPanel();
        jLabel3 = new JLabel("Nama Lengkap");
        FieldNama = new JTextField();
        jLabel4 = new JLabel("Tingkat & Kelas");
        jLabel5 = new JLabel("Tingkat:");
        RadioX = new JRadioButton("X");
        RadioXI = new JRadioButton("XI");
        RadioXII = new JRadioButton("XII");
        jLabel6 = new JLabel("Kelas:");
        RadioAlphabet = new JRadioButton("A");
        RadioAlphabetB = new JRadioButton("B");
        RadioAlphabetC = new JRadioButton("C");
        RadioAlphabetD = new JRadioButton("D");
        RadioAlphabetE = new JRadioButton("E");
        RadioAlphabetF = new JRadioButton("F");
        RadioAlphabetG = new JRadioButton("G");
        RadioAlphabetH = new JRadioButton("H");
        RadioAlphabetI = new JRadioButton("I");
        RadioAlphabetJ = new JRadioButton("J");
        RadioAlphabetK = new JRadioButton("K");
        
        jLabel7 = new JLabel("Dipilih: -");
        jLabel8 = new JLabel("Alasan Keterlambatan:");
        jScrollPane1 = new JScrollPane();
        AreaReason = new JTextArea();
        ButtonNext = new JButton("Lanjut ->");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Form Keterlambatan - SMAN 1 Rejotangan");

        AreaReason.setColumns(20);
        AreaReason.setRows(5);
        jScrollPane1.setViewportView(AreaReason);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 480, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 750, Short.MAX_VALUE));

        pack();
    }

    private void applyCustomStyles() {
        // 1. Center the Titles
        jPanel1.removeAll();
        jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.Y_AXIS));
        jLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        jLabel1.setFont(new Font("Segoe UI", Font.BOLD, 24));
        jLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        jLabel2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jPanel1.add(jLabel1);
        jPanel1.add(Box.createVerticalStrut(5));
        jPanel1.add(jLabel2);
        
        // 2. Custom Action Buttons
        ButtonNext.setVisible(false);
        JButton newNextBtn = new JButton("Berikutnya");
        newNextBtn.addActionListener(e -> submitLateEntry());
        
        JButton switchFormBtn = new JButton("Form Dispensasi");
        switchFormBtn.addActionListener(e -> {
            this.dispose();
            new FormDispensasi().setVisible(true);
        });
        
        JPanel actionPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 15, 10));
        actionPanel.add(switchFormBtn);
        actionPanel.add(newNextBtn);

        // 3. Rebuild jPanel2 using GridBagLayout
        jPanel2.removeAll();
        jPanel2.setLayout(new GridBagLayout());
        GridBagConstraints gbcForm = new GridBagConstraints();
        gbcForm.gridx = 0; gbcForm.gridy = 0; gbcForm.anchor = GridBagConstraints.WEST;
        gbcForm.insets = new Insets(5, 10, 5, 10);
        gbcForm.fill = GridBagConstraints.HORIZONTAL;
        gbcForm.weightx = 1.0;

        jPanel2.add(jLabel3, gbcForm); 
        gbcForm.gridy++; jPanel2.add(FieldNama, gbcForm);
        
        gbcForm.gridy++; jPanel2.add(new JLabel(" "), gbcForm);
        
        jLabel4.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbcForm.gridy++; jPanel2.add(jLabel4, gbcForm);
        gbcForm.gridy++; jPanel2.add(jLabel5, gbcForm);
        
        JPanel tingkatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        tingkatPanel.add(RadioX); tingkatPanel.add(RadioXI); tingkatPanel.add(RadioXII);
        gbcForm.gridy++; jPanel2.add(tingkatPanel, gbcForm);
        
        gbcForm.gridy++; jPanel2.add(jLabel6, gbcForm);
        
        JPanel kelasPanel = new JPanel(new GridLayout(2, 6, 5, 5));
        kelasPanel.add(RadioAlphabet); kelasPanel.add(RadioAlphabetB); kelasPanel.add(RadioAlphabetC);
        kelasPanel.add(RadioAlphabetD); kelasPanel.add(RadioAlphabetE); kelasPanel.add(RadioAlphabetF);
        kelasPanel.add(RadioAlphabetG); kelasPanel.add(RadioAlphabetH); kelasPanel.add(RadioAlphabetI);
        kelasPanel.add(RadioAlphabetJ); kelasPanel.add(RadioAlphabetK);
        gbcForm.gridy++; jPanel2.add(kelasPanel, gbcForm);
        
        jLabel7.setFont(new Font("Segoe UI", Font.BOLD, 12));
        jLabel7.setForeground(new Color(0, 102, 204));
        gbcForm.gridy++; jPanel2.add(jLabel7, gbcForm);
        
        gbcForm.gridy++; jPanel2.add(new JLabel(" "), gbcForm);
        gbcForm.gridy++; jPanel2.add(jLabel8, gbcForm);
        gbcForm.gridy++; gbcForm.fill = GridBagConstraints.BOTH; gbcForm.weighty = 1.0;
        jPanel2.add(jScrollPane1, gbcForm);
        gbcForm.fill = GridBagConstraints.HORIZONTAL; gbcForm.weighty = 0.0;

        // Listeners
        ActionListener updateDipilih = e -> {
            if (RadioX.isSelected()) {
                RadioAlphabetK.setVisible(false);
                if (RadioAlphabetK.isSelected()) buttonGroup2.clearSelection();
            } else {
                RadioAlphabetK.setVisible(true);
            }

            String t = RadioX.isSelected() ? "X" : (RadioXI.isSelected() ? "XI" : (RadioXII.isSelected() ? "XII" : ""));
            String k = "";
            if(RadioAlphabet.isSelected()) k="A";
            else if(RadioAlphabetB.isSelected()) k="B";
            else if(RadioAlphabetC.isSelected()) k="C";
            else if(RadioAlphabetD.isSelected()) k="D";
            else if(RadioAlphabetE.isSelected()) k="E";
            else if(RadioAlphabetF.isSelected()) k="F";
            else if(RadioAlphabetG.isSelected()) k="G";
            else if(RadioAlphabetH.isSelected()) k="H";
            else if(RadioAlphabetI.isSelected()) k="I";
            else if(RadioAlphabetJ.isSelected()) k="J";
            else if(RadioAlphabetK.isSelected()) k="K";

            if(!t.isEmpty() && !k.isEmpty()) jLabel7.setText("Dipilih: " + t + "-" + k);
            else if (!t.isEmpty()) jLabel7.setText("Dipilih: " + t);
            else jLabel7.setText("Dipilih: -");
        };

        JRadioButton[] radios = {RadioX, RadioXI, RadioXII, RadioAlphabet, RadioAlphabetB, RadioAlphabetC, RadioAlphabetD, RadioAlphabetE, RadioAlphabetF, RadioAlphabetG, RadioAlphabetH, RadioAlphabetI, RadioAlphabetJ, RadioAlphabetK};
        for(JRadioButton r : radios) r.addActionListener(updateDipilih);

        // Wrapper
        JPanel formBoxWrapper = new JPanel();
        formBoxWrapper.setLayout(new BoxLayout(formBoxWrapper, BoxLayout.Y_AXIS));
        formBoxWrapper.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        formBoxWrapper.add(jPanel2);
        formBoxWrapper.add(actionPanel);

        Container cp = getContentPane();
        cp.removeAll();
        cp.setLayout(new BorderLayout());

        JPanel mainWrapper = new JPanel();
        mainWrapper.setLayout(new BoxLayout(mainWrapper, BoxLayout.Y_AXIS));
        mainWrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainWrapper.add(jPanel1);
        mainWrapper.add(Box.createVerticalStrut(18));
        mainWrapper.add(formBoxWrapper);

        JPanel centeringPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.insets = new Insets(20, 40, 20, 40);
        centeringPanel.add(mainWrapper, gbc);

        JScrollPane scrollPane = new JScrollPane(centeringPanel);
        scrollPane.setBorder(null);
        cp.add(scrollPane, BorderLayout.CENTER);

        // Footer welcome info
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JLabel welcomeLabel = new JLabel("Login sebagai: " + 
            com.smartschool.permit.tubespbo.app.UserSession.getInstance().getCurrentStudent().getFullName() + " (" + 
            com.smartschool.permit.tubespbo.app.UserSession.getInstance().getCurrentStudent().getClassName() + ")");
        welcomeLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        footerPanel.add(welcomeLabel);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            com.smartschool.permit.tubespbo.app.UserSession.getInstance().logout();
            this.dispose();
            new com.smartschool.permit.tubespbo.gui.login.LoginFrame().setVisible(true);
        });
        footerPanel.add(logoutBtn);
        
        // Hide input fields (Mandatory Login Mode)
        FieldNama.setVisible(false);
        jLabel3.setVisible(false);
        jLabel4.setVisible(false);
        jLabel5.setVisible(false);
        jLabel6.setVisible(false);
        jLabel7.setVisible(false);
        tingkatPanel.setVisible(false);
        kelasPanel.setVisible(false);
        
        cp.add(footerPanel, BorderLayout.SOUTH);

        buttonGroup1.add(RadioX); buttonGroup1.add(RadioXI); buttonGroup1.add(RadioXII);
        buttonGroup2.add(RadioAlphabet); buttonGroup2.add(RadioAlphabetB); buttonGroup2.add(RadioAlphabetC);
        buttonGroup2.add(RadioAlphabetD); buttonGroup2.add(RadioAlphabetE); buttonGroup2.add(RadioAlphabetF);
        buttonGroup2.add(RadioAlphabetG); buttonGroup2.add(RadioAlphabetH); buttonGroup2.add(RadioAlphabetI);
        buttonGroup2.add(RadioAlphabetJ); buttonGroup2.add(RadioAlphabetK);

        revalidate(); repaint();
        setSize(480, 750);
        setMinimumSize(new Dimension(450, 700));
        setLocationRelativeTo(null);
    }

    private void submitLateEntry() {
        String nama = "";
        String tingkat = "";
        String kelas = "";
        String studentId = "";
        String alasan = AreaReason.getText().trim();

        if (com.smartschool.permit.tubespbo.app.UserSession.getInstance().isStudent()) {
            nama = com.smartschool.permit.tubespbo.app.UserSession.getInstance().getCurrentStudent().getFullName();
            String fullClassName = com.smartschool.permit.tubespbo.app.UserSession.getInstance().getCurrentStudent().getClassName();
            studentId = com.smartschool.permit.tubespbo.app.UserSession.getInstance().getCurrentStudent().getId();
            // Split to tingkat and kelas for fallback if needed, but we'll use fullClassName
            String[] split = fullClassName.split("-");
            tingkat = split[0];
            if (split.length > 1) kelas = split[1];
        } else {
            nama = FieldNama.getText().trim();
            if (nama.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama lengkap harus diisi!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Format to Title Case
            String[] words = nama.split("\\s+");
            StringBuilder sb = new StringBuilder();
            for (String w : words) {
                if (!w.isEmpty()) sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1).toLowerCase()).append(" ");
            }
            nama = sb.toString().trim();
            FieldNama.setText(nama);

            tingkat = RadioX.isSelected() ? "X" : (RadioXI.isSelected() ? "XI" : (RadioXII.isSelected() ? "XII" : ""));
            if (tingkat.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih tingkat kelas!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (RadioAlphabet.isSelected()) kelas = "A";
            else if (RadioAlphabetB.isSelected()) kelas = "B";
            else if (RadioAlphabetC.isSelected()) kelas = "C";
            else if (RadioAlphabetD.isSelected()) kelas = "D";
            else if (RadioAlphabetE.isSelected()) kelas = "E";
            else if (RadioAlphabetF.isSelected()) kelas = "F";
            else if (RadioAlphabetG.isSelected()) kelas = "G";
            else if (RadioAlphabetH.isSelected()) kelas = "H";
            else if (RadioAlphabetI.isSelected()) kelas = "I";
            else if (RadioAlphabetJ.isSelected()) kelas = "J";
            else if (RadioAlphabetK.isSelected()) kelas = "K";
            
            if (kelas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pilih kelas!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        if (alasan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Alasan keterlambatan harus diisi!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        final String submissionNama = nama;
        final String submissionKelas = tingkat + "-" + kelas;
        final String finalStudentId = studentId;

        int confirm = JOptionPane.showConfirmDialog(this,
            "Apakah data sudah benar?\n\nNama: " + submissionNama + "\nKelas: " + submissionKelas + "\nAlasan: " + alasan,
            "Konfirmasi", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        setEnabled(false);
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {

                PermitService service = com.smartschool.permit.tubespbo.app.AppServices.getPermitService();

                StudentPermit permit = new com.smartschool.permit.tubespbo.model.LateEntryPermit();
                permit.setStudentName(submissionNama);
                permit.setClassName(submissionKelas);
                permit.setStudentId(finalStudentId);
                permit.setReason(alasan);
                permit.setType(PermitType.LATE_ENTRY);
                permit.setSchoolId("sch_001");
                
                long now = System.currentTimeMillis();
                permit.setTimestamp(now);
                permit.setArrivalTimestamp(now);

                return service.createPermit(permit);
            }

            @Override
            protected void done() {
                try {
                    String id = get();
                    JOptionPane.showMessageDialog(FormKeterlambatan.this, "Keterlambatan berhasil dicatat!\nID: " + id, "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                    FieldNama.setText("");
                    AreaReason.setText("");
                    buttonGroup1.clearSelection();
                    buttonGroup2.clearSelection();
                    jLabel7.setText("Dipilih: -");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FormKeterlambatan.this, "Gagal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setEnabled(true);
                }
            }
        }.execute();
    }

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new FormKeterlambatan().setVisible(true));
    }

    private JTextArea AreaReason;
    private JButton ButtonNext;
    private JTextField FieldNama;
    private JRadioButton RadioAlphabet;
    private JRadioButton RadioAlphabetB;
    private JRadioButton RadioAlphabetC;
    private JRadioButton RadioAlphabetD;
    private JRadioButton RadioAlphabetE;
    private JRadioButton RadioAlphabetF;
    private JRadioButton RadioAlphabetG;
    private JRadioButton RadioAlphabetH;
    private JRadioButton RadioAlphabetI;
    private JRadioButton RadioAlphabetJ;
    private JRadioButton RadioX;
    private JRadioButton RadioXI;
    private JRadioButton RadioXII;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JLabel jLabel8;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JScrollPane jScrollPane1;
    private ButtonGroup buttonGroup1;
    private ButtonGroup buttonGroup2;
}
