package com.smartschool.permit.tubespbo.gui.dashboard;

import javax.swing.*;
import java.awt.*;
import com.smartschool.permit.tubespbo.gui.login.LoginFrame;
import com.smartschool.permit.tubespbo.gui.widget.SidebarPanel;
import com.smartschool.permit.tubespbo.service.AuthService;
import com.smartschool.permit.tubespbo.repository.AdminRepository;

public class DashboardUtama extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    public DashboardUtama() {
        setTitle("SMAN 1 Rejotangan - Admin Panel");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setLayout(new BorderLayout());

        // Inisialisasi CardLayout untuk konten utama
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);

        // Tambahkan halaman-halaman fungsional ke dalam CardLayout
        mainContentPanel.add(new DashboardPanel(), "Dashboard");
        mainContentPanel.add(new LateEntryPanel(), "Siswa Terlambat");
        mainContentPanel.add(new ExitPermitPanel(), "Izin Keluar");
        mainContentPanel.add(new ReportPanel(), "Laporan");
        mainContentPanel.add(new StudentDataPanel(), "Data Siswa");
        mainContentPanel.add(new AdminPanel(), "Kelola Admin");

        // Tambahkan komponen sidebar yang terpisah di sebelah kiri
        SidebarPanel sidebar = new SidebarPanel(
            // onMenuSelected
            (menuName) -> {
                cardLayout.show(mainContentPanel, menuName);
            },
            // onLogout
            () -> {
                int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin logout?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // Logout dari session
                    AuthService authService = new AuthService(new AdminRepository());
                    authService.logout();
                    this.dispose();
                    new LoginFrame().setVisible(true);
                }
            }
        );

        add(sidebar, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new DashboardUtama().setVisible(true);
        });
    }
}