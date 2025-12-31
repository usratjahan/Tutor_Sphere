import javax.swing.*;
import java.awt.*;

public class StudentDashboard extends DashboardBase {

    public StudentDashboard(String name, String email) {
        // ❗ Footer disabled for student dashboard
        super("TutorSphere - Student Dashboard", name, email, false);
    }

    @Override
    protected void setupContent() {
        Color whiteText = new Color(0xFF, 0xFF, 0xFF);
        Color boxColor = new Color(0x09, 0x26, 0x35);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridLayout(2, 2, 40, 40));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(100, 400, 100, 400));

        Font buttonFont = new Font("Serif", Font.BOLD, 26);
        Dimension buttonSize = new Dimension(250, 80);

        JButton profileBtn = new JButton("Profile Page");
        JButton findTutorBtn = new JButton("Find Tutor");
        JButton requestsBtn = new JButton("My Requests");
        JButton logoutBtn = new JButton("Logout");

        JButton[] buttons = {profileBtn, findTutorBtn, requestsBtn, logoutBtn};
        for (JButton btn : buttons) {
            btn.setFont(buttonFont);
            btn.setPreferredSize(buttonSize);
            btn.setBackground(boxColor);
            btn.setForeground(whiteText);
            btn.setFocusPainted(false);
            addHoverEffect(btn, boxColor, boxColor.brighter());
            buttonPanel.add(btn);
        }

        background.add(buttonPanel, BorderLayout.CENTER);

        // Actions
        profileBtn.addActionListener(e -> {
            new ProfilePage("Student", name, email).setVisible(true);
            dispose();
        });

        findTutorBtn.addActionListener(e -> {
            new MatchmakingPage(name, email).setVisible(true);
            dispose();
        });

        requestsBtn.addActionListener(e -> {
            new StudentRequestsPage(name, email).setVisible(true);
            dispose();
        });

        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?", "Logout",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new Homepage().setVisible(true);
                dispose();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new StudentDashboard("student", "student@gmail.com").setVisible(true));
    }
}








