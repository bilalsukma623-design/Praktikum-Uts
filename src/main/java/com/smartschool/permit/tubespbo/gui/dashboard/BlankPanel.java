package com.smartschool.permit.tubespbo.gui.dashboard;

import javax.swing.*;
import java.awt.*;

public class BlankPanel extends JPanel {

    public BlankPanel(String menuName) {
        setLayout(new BorderLayout());
        
        JLabel label = new JLabel("Halaman " + menuName + " Tidak Ditemukan", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        label.setForeground(Color.GRAY);
        
        add(label, BorderLayout.CENTER);
    }
}
