import javax.swing.*;
import java.awt.*;

public class TutorDashboard extends DashboardBase {

    public TutorDashboard(String name, String email) {
        super("TutorSphere - Tutor Dashboard", name, email, false);
    }

    @Override
    protected void setupContent() {
        Color whiteText = new Color(0xFFFFFF);
        Color boxColor = new Color(0x092635);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 50, 40));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(80, 300, 100, 300));

        JButton profileBtn = new JButton("Profile Page");
        JButton findStudentBtn = new JButton("Find Students");
        JButton viewRequestsBtn = new JButton("View Student Requests");
        JButton notificationsBtn = new JButton("Notifications");
        JButton verificationBtn = new JButton("Verification");
        JButton logoutBtn = new JButton("Logout");

        JButton[] buttons = {
                profileBtn, findStudentBtn, viewRequestsBtn,
                notificationsBtn, verificationBtn, logoutBtn
        };

        Font buttonFont = new Font("Serif", Font.BOLD, 28);
        Dimension buttonSize = new Dimension(300, 90);

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
            new ProfilePage("Tutor", name, email).setVisible(true);
            dispose();
        });

        findStudentBtn.addActionListener(e -> {
            new FindStudentsPage(name, email).setVisible(true);
            dispose();
        });

        viewRequestsBtn.addActionListener(e -> {
            new TutorRequestsPage(name, email).setVisible(true);
            dispose();
        });

        notificationsBtn.addActionListener(e -> {
            new TutorNotificationsPage(name, email).setVisible(true);
            dispose();
        });

        verificationBtn.addActionListener(e -> {
            new VerificationPage(name, email).setVisible(true);
            dispose();
        });

        logoutBtn.addActionListener(e -> {
            new Homepage().setVisible(true);
            dispose();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
            new TutorDashboard("tutor", "tutor@gmail.com").setVisible(true)
        );
    }
}






