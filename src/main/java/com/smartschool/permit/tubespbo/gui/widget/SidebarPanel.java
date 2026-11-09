package com.smartschool.permit.tubespbo.gui.widget;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class SidebarPanel extends JPanel {

    private Consumer<String> onMenuSelected;
    private Runnable onLogout;

    public SidebarPanel(Consumer<String> onMenuSelected, Runnable onLogout) {
        this.onMenuSelected = onMenuSelected;
        this.onLogout = onLogout;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
        setPreferredSize(new Dimension(200, 0));

        JLabel titleLabel = new JLabel("SMAN 1 Rejotangan");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 0, 10));

        String userName = com.smartschool.permit.tubespbo.app.UserSession.getInstance().getCurrentUser() != null 
                ? com.smartschool.permit.tubespbo.app.UserSession.getInstance().getCurrentUser().getName() 
                : "Admin";
        JLabel userLabel = new JLabel(userName);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setForeground(new Color(100, 100, 100));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 20, 10));

        add(titleLabel);
        add(userLabel);
        add(new JSeparator());
        add(Box.createVerticalStrut(10));

        String[] menuItems = {
            "Dashboard",
            "Siswa Terlambat",
            "Izin Keluar",
            "Laporan",
            "Data Siswa",
            "Kelola Admin"
        };

        for (String item : menuItems) {
            JButton btn = new JButton(item);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(180, 40));
            btn.setFocusPainted(false);
            
            btn.addActionListener(e -> {
                if (onMenuSelected != null) {
                    onMenuSelected.accept(item);
                }
            });
            
            add(btn);
            add(Box.createVerticalStrut(10));
        }

        // Pushes the following components to the bottom
        add(Box.createVerticalGlue());
        add(new JSeparator());

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.setMaximumSize(new Dimension(180, 40));
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> {
            if (onLogout != null) {
                onLogout.run();
            }
        });
        
        add(Box.createVerticalStrut(10));
        add(logoutBtn);
        add(Box.createVerticalStrut(20));
    }
}
