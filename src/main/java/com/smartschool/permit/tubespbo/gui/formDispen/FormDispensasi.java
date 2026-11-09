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
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Form for Exit Permits (Dispensasi)
 */
public class FormDispensasi extends javax.swing.JFrame {
    
    private static final Logger logger = Logger.getLogger(FormDispensasi.class.getName());
    private JCheckBox CheckKembali;

    /**
     * Creates new form FormDispensasi
     */
    public FormDispensasi() {
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
        
        Top = new JPanel();
        jLabel1 = new JLabel("Form Dispensasi");
        jLabel2 = new JLabel("SMAN 1 Rejotangan");
        
        Bottom = new JPanel();
        jLabel3 = new JLabel("Nama Lengkap");
        FieldNama = new JTextField();
        jLabel4 = new JLabel("Nama otomatis diformat huruf besar di awal.");
        jLabel5 = new JLabel("Tingkat & Kelas");
        jLabel6 = new JLabel("Tingkat:");
        RadioX = new JRadioButton("X");
        RadioXI = new JRadioButton("XI");
        RadioXII = new JRadioButton("XII");
        jLabel7 = new JLabel("Kelas:");
        RadioKelasAlphabetA = new JRadioButton("A");
        RadioKelasAlphabetB = new JRadioButton("B");
        RadioKelasAlphabetC = new JRadioButton("C");
        RadioKelasAlphabetD = new JRadioButton("D");
        RadioKelasAlphabetE = new JRadioButton("E");
        RadioKelasAlphabetF = new JRadioButton("F");
        RadioKelasAlphabetG = new JRadioButton("G");
        RadioKelasAlphabetH = new JRadioButton("H");
        RadioKelasAlphabetI = new JRadioButton("I");
        RadionKelasAlphabetJ = new JRadioButton("J");
        RadioKelasAlphabetK = new JRadioButton("K");
        
        jLabel8 = new JLabel("Dipilih: -");
        jLabel9 = new JLabel("Alasan Dispensasi");
        jScrollPane1 = new JScrollPane();
        TextAreaAlasan = new JTextArea();
        jLabel10 = new JLabel("Rencana Kembali (Opsional)");
        SpinnerTimer = new JSpinner(new SpinnerDateModel());
        jLabel11 = new JLabel("Hapus centang jika tidak kembali hari ini.");
        ButtonNext = new JButton("Lanjut ->");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Form Dispensasi - SMAN 1 Rejotangan");

        TextAreaAlasan.setColumns(20);
        TextAreaAlasan.setRows(5);
        jScrollPane1.setViewportView(TextAreaAlasan);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 480, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 750, Short.MAX_VALUE));

        pack();
    }

    private void applyCustomStyles() {
        // 1. Center the Titles
        Top.removeAll();
        Top.setLayout(new BoxLayout(Top, BoxLayout.Y_AXIS));
        jLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        jLabel1.setFont(new Font("Segoe UI", Font.BOLD, 24));
        jLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        jLabel2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        Top.add(jLabel1);
        Top.add(Box.createVerticalStrut(5));
        Top.add(jLabel2);
        
        // 2. Custom Action Buttons
        ButtonNext.setVisible(false);
        JButton newNextBtn = new JButton("Berikutnya");
        newNextBtn.addActionListener(e -> submitDispensasi());
        
        JButton switchFormBtn = new JButton("Form Terlambat");
        switchFormBtn.addActionListener(e -> {
            this.dispose();
            new FormKeterlambatan().setVisible(true);
        });
        
        JPanel actionPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 15, 10));
        actionPanel.add(switchFormBtn);
        actionPanel.add(newNextBtn);

        // 3. Rebuild Bottom Panel using GridBagLayout
        Bottom.removeAll();
        Bottom.setLayout(new GridBagLayout());
        GridBagConstraints gbcForm = new GridBagConstraints();
        gbcForm.gridx = 0; gbcForm.gridy = 0; gbcForm.anchor = GridBagConstraints.WEST;
        gbcForm.insets = new Insets(5, 10, 5, 10);
        gbcForm.fill = GridBagConstraints.HORIZONTAL;
        gbcForm.weightx = 1.0;

        Bottom.add(jLabel3, gbcForm); 
        gbcForm.gridy++; Bottom.add(FieldNama, gbcForm);
        gbcForm.gridy++; Bottom.add(jLabel4, gbcForm);
        
        gbcForm.gridy++; Bottom.add(new JLabel(" "), gbcForm);
        
        jLabel5.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbcForm.gridy++; Bottom.add(jLabel5, gbcForm);
        gbcForm.gridy++; Bottom.add(jLabel6, gbcForm);
        
        JPanel tingkatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        tingkatPanel.add(RadioX); tingkatPanel.add(RadioXI); tingkatPanel.add(RadioXII);
        gbcForm.gridy++; Bottom.add(tingkatPanel, gbcForm);
        
        gbcForm.gridy++; Bottom.add(jLabel7, gbcForm);
        
        JPanel kelasPanel = new JPanel(new GridLayout(2, 6, 5, 5));
        kelasPanel.add(RadioKelasAlphabetA); kelasPanel.add(RadioKelasAlphabetB); kelasPanel.add(RadioKelasAlphabetC); 
        kelasPanel.add(RadioKelasAlphabetD); kelasPanel.add(RadioKelasAlphabetE); kelasPanel.add(RadioKelasAlphabetF);
        kelasPanel.add(RadioKelasAlphabetG); kelasPanel.add(RadioKelasAlphabetH); kelasPanel.add(RadioKelasAlphabetI); 
        kelasPanel.add(RadionKelasAlphabetJ); kelasPanel.add(RadioKelasAlphabetK);
        gbcForm.gridy++; Bottom.add(kelasPanel, gbcForm);
        
        jLabel8.setFont(new Font("Segoe UI", Font.BOLD, 12));
        jLabel8.setForeground(new Color(0, 102, 204));
        gbcForm.gridy++; Bottom.add(jLabel8, gbcForm);
        
        gbcForm.gridy++; Bottom.add(new JLabel(" "), gbcForm);
        gbcForm.gridy++; Bottom.add(jLabel9, gbcForm);
        gbcForm.gridy++; gbcForm.fill = GridBagConstraints.BOTH; gbcForm.weighty = 1.0;
        Bottom.add(jScrollPane1, gbcForm);
        gbcForm.fill = GridBagConstraints.HORIZONTAL; gbcForm.weighty = 0.0;
        
        gbcForm.gridy++; Bottom.add(new JLabel(" "), gbcForm);
        gbcForm.gridy++; Bottom.add(jLabel10, gbcForm);
        
        CheckKembali = new JCheckBox("Saya berencana kembali ke sekolah hari ini", true);
        CheckKembali.addActionListener(e -> SpinnerTimer.setEnabled(CheckKembali.isSelected()));
        gbcForm.gridy++; Bottom.add(CheckKembali, gbcForm);
        
        Calendar defaultTime = Calendar.getInstance();
        defaultTime.add(Calendar.HOUR_OF_DAY, 1);
        SpinnerTimer.setModel(new SpinnerDateModel(defaultTime.getTime(), null, null, Calendar.MINUTE));
        SpinnerTimer.setEditor(new JSpinner.DateEditor(SpinnerTimer, "HH:mm"));
        gbcForm.gridy++; Bottom.add(SpinnerTimer, gbcForm);
        
        gbcForm.gridy++; Bottom.add(jLabel11, gbcForm);

        // Listeners for selection display
        ActionListener updateDipilih = e -> {
            if (RadioX.isSelected()) {
                RadioKelasAlphabetK.setVisible(false);
                if (RadioKelasAlphabetK.isSelected()) buttonGroup2.clearSelection();
            } else {
                RadioKelasAlphabetK.setVisible(true);
            }

            String t = RadioX.isSelected() ? "X" : (RadioXI.isSelected() ? "XI" : (RadioXII.isSelected() ? "XII" : ""));
            String k = "";
            if(RadioKelasAlphabetA.isSelected()) k="A";
            else if(RadioKelasAlphabetB.isSelected()) k="B";
            else if(RadioKelasAlphabetC.isSelected()) k="C";
            else if(RadioKelasAlphabetD.isSelected()) k="D";
            else if(RadioKelasAlphabetE.isSelected()) k="E";
            else if(RadioKelasAlphabetF.isSelected()) k="F";
            else if(RadioKelasAlphabetG.isSelected()) k="G";
            else if(RadioKelasAlphabetH.isSelected()) k="H";
            else if(RadioKelasAlphabetI.isSelected()) k="I";
            else if(RadionKelasAlphabetJ.isSelected()) k="J";
            else if(RadioKelasAlphabetK.isSelected()) k="K";

            if(!t.isEmpty() && !k.isEmpty()) jLabel8.setText("Dipilih: " + t + "-" + k);
            else if (!t.isEmpty()) jLabel8.setText("Dipilih: " + t);
            else jLabel8.setText("Dipilih: -");
        };

        JRadioButton[] radios = {RadioX, RadioXI, RadioXII, RadioKelasAlphabetA, RadioKelasAlphabetB, RadioKelasAlphabetC, RadioKelasAlphabetD, RadioKelasAlphabetE, RadioKelasAlphabetF, RadioKelasAlphabetG, RadioKelasAlphabetH, RadioKelasAlphabetI, RadionKelasAlphabetJ, RadioKelasAlphabetK};
        for(JRadioButton r : radios) r.addActionListener(updateDipilih);

        // Wrapper
        JPanel formBoxWrapper = new JPanel();
        formBoxWrapper.setLayout(new BoxLayout(formBoxWrapper, BoxLayout.Y_AXIS));
        formBoxWrapper.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        formBoxWrapper.add(Bottom);
        formBoxWrapper.add(actionPanel);

        Container cp = getContentPane();
        cp.removeAll();
        cp.setLayout(new BorderLayout());

        JPanel mainWrapper = new JPanel();
        mainWrapper.setLayout(new BoxLayout(mainWrapper, BoxLayout.Y_AXIS));
        mainWrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainWrapper.add(Top);
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
        jLabel8.setVisible(false);
        tingkatPanel.setVisible(false);
        kelasPanel.setVisible(false);
        
        cp.add(footerPanel, BorderLayout.SOUTH);

        buttonGroup1.add(RadioX); buttonGroup1.add(RadioXI); buttonGroup1.add(RadioXII);
        buttonGroup2.add(RadioKelasAlphabetA); buttonGroup2.add(RadioKelasAlphabetB); buttonGroup2.add(RadioKelasAlphabetC);
        buttonGroup2.add(RadioKelasAlphabetD); buttonGroup2.add(RadioKelasAlphabetE); buttonGroup2.add(RadioKelasAlphabetF);
        buttonGroup2.add(RadioKelasAlphabetG); buttonGroup2.add(RadioKelasAlphabetH); buttonGroup2.add(RadioKelasAlphabetI);
        buttonGroup2.add(RadionKelasAlphabetJ); buttonGroup2.add(RadioKelasAlphabetK);

        revalidate(); repaint();
        setSize(480, 750);
        setMinimumSize(new Dimension(450, 700));
        setLocationRelativeTo(null);
    }

    private void submitDispensasi() {
        String nama = "";
        String tingkat = "";
        String kelas = "";
        String studentId = "";
        String alasan = TextAreaAlasan.getText().trim();

        if (com.smartschool.permit.tubespbo.app.UserSession.getInstance().isStudent()) {
            nama = com.smartschool.permit.tubespbo.app.UserSession.getInstance().getCurrentStudent().getFullName();
            String fullClassName = com.smartschool.permit.tubespbo.app.UserSession.getInstance().getCurrentStudent().getClassName();
            studentId = com.smartschool.permit.tubespbo.app.UserSession.getInstance().getCurrentStudent().getId();
            String[] split = fullClassName.split("-");
            tingkat = split[0];
            if (split.length > 1) kelas = split[1];
        } else {
            // This else block should theoretically not be reachable due to redirect
            nama = FieldNama.getText().trim();
            tingkat = RadioX.isSelected() ? "X" : (RadioXI.isSelected() ? "XI" : (RadioXII.isSelected() ? "XII" : ""));
            // ... truncated manual validation ...
        }

        if (alasan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Alasan dispensasi harus diisi!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        final String submissionNama = nama;
        final String submissionKelas = tingkat + "-" + kelas;
        final String finalStudentId = studentId;

        int confirm = JOptionPane.showConfirmDialog(this,
            "Apakah data sudah benar?\n\nNama: " + submissionNama + "\nKelas: " + submissionKelas + "\nAlasan: " + alasan,
            "Konfirmasi", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        long returnTs = 0;
        if (CheckKembali.isSelected()) {
            Date selectedTime = (Date) SpinnerTimer.getValue();
            Calendar now = Calendar.getInstance();
            Calendar target = Calendar.getInstance();
            target.setTime(selectedTime);
            
            Calendar result = Calendar.getInstance();
            result.set(Calendar.HOUR_OF_DAY, target.get(Calendar.HOUR_OF_DAY));
            result.set(Calendar.MINUTE, target.get(Calendar.MINUTE));
            result.set(Calendar.SECOND, 0);
            
            if (result.getTimeInMillis() < now.getTimeInMillis()) result.add(Calendar.DAY_OF_YEAR, 1);
            returnTs = result.getTimeInMillis();
        }

        final long finalReturnTs = returnTs;
        setEnabled(false);
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {

                PermitService service = com.smartschool.permit.tubespbo.app.AppServices.getPermitService();

                StudentPermit permit = new com.smartschool.permit.tubespbo.model.ExitPermit();
                permit.setStudentName(submissionNama);
                permit.setClassName(submissionKelas);
                permit.setStudentId(finalStudentId);
                permit.setReason(alasan);
                permit.setType(PermitType.EXIT_PERMIT);
                permit.setSchoolId("sch_001");
                
                long now = System.currentTimeMillis();
                permit.setTimestamp(now); 
                permit.setExitTimestamp(now);
                permit.setReturnTimestamp(finalReturnTs);

                return service.createPermit(permit);
            }

            @Override
            protected void done() {
                try {
                    String id = get();
                    JOptionPane.showMessageDialog(FormDispensasi.this, "Dispensasi berhasil dicatat!\nID: " + id, "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                    FieldNama.setText("");
                    TextAreaAlasan.setText("");
                    buttonGroup1.clearSelection();
                    buttonGroup2.clearSelection();
                    jLabel8.setText("Dipilih: -");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FormDispensasi.this, "Gagal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        java.awt.EventQueue.invokeLater(() -> new FormDispensasi().setVisible(true));
    }

    private JPanel Bottom;
    private JButton ButtonNext;
    private JTextField FieldNama;
    private JRadioButton RadioKelasAlphabetA;
    private JRadioButton RadioKelasAlphabetB;
    private JRadioButton RadioKelasAlphabetC;
    private JRadioButton RadioKelasAlphabetD;
    private JRadioButton RadioKelasAlphabetE;
    private JRadioButton RadioKelasAlphabetF;
    private JRadioButton RadioKelasAlphabetG;
    private JRadioButton RadioKelasAlphabetH;
    private JRadioButton RadioKelasAlphabetI;
    private JRadioButton RadioX;
    private JRadioButton RadioXI;
    private JRadioButton RadioXII;
    private JRadioButton RadionKelasAlphabetJ;
    private JRadioButton RadioKelasAlphabetK;
    private JSpinner SpinnerTimer;
    private JTextArea TextAreaAlasan;
    private JPanel Top;
    private ButtonGroup buttonGroup1;
    private ButtonGroup buttonGroup2;
    private JLabel jLabel1;
    private JLabel jLabel10;
    private JLabel jLabel11;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JLabel jLabel8;
    private JLabel jLabel9;
    private JScrollPane jScrollPane1;
}
