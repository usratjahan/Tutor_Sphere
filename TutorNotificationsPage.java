import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TutorNotificationsPage extends JFrame {

    private String tutorName;
    private String tutorEmail;
    private JTextArea notificationsArea;

    public TutorNotificationsPage(String name, String email) {
        this.tutorName = name;
        this.tutorEmail = email;

        setTitle("TutorSphere - Notifications");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Gradient Background 
        GradientBackground background = new GradientBackground();
        background.setLayout(new BorderLayout());
        setContentPane(background);

        //Title 
        JLabel title = new JLabel("Notifications Center", JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 42));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 30, 0));
        background.add(title, BorderLayout.NORTH);

        //Text Area 
        notificationsArea = new JTextArea();
        notificationsArea.setFont(new Font("Serif", Font.PLAIN, 22));
        notificationsArea.setEditable(false);
        notificationsArea.setMargin(new Insets(20, 30, 20, 30));

        JScrollPane scroll = new JScrollPane(notificationsArea);
        scroll.setBorder(BorderFactory.createEmptyBorder(30, 100, 30, 100));
        background.add(scroll, BorderLayout.CENTER);

        //Back Button 
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Serif", Font.BOLD, 24));
        backBtn.setBackground(new Color(0x092635));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.add(backBtn);
        background.add(bottom, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> {
            new TutorDashboard(tutorName, tutorEmail).setVisible(true);
            dispose();
        });
        loadNotifications();
    }

    // Load Tutor Notifications from notifications.txt 
    private void loadNotifications() {
        File notifFile = new File("notifications.txt");
        File reqFile = new File("requests.txt");

        List<String> notifs = new ArrayList<>();

        // Read requests file for accepted/rejected requests
        if (reqFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(reqFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 6) {
                        String sName = parts[0];
                        String sEmail = parts[1];
                        String tName = parts[2];
                        String tEmail = parts[3];
                        String subject = parts[4];
                        String status = parts[5];
                        if (tEmail.equalsIgnoreCase(tutorEmail)) {
                            if (status.equalsIgnoreCase("Accepted")) {
                                notifs.add(" " + sName + " (" + sEmail + ") accepted request for " + subject + ".\nContact: Check student profile.");
                            } else if (status.equalsIgnoreCase("Rejected")) {
                                notifs.add(" " + sName + " (" + sEmail + ") rejected request for " + subject + ".");
                            } else if (status.equalsIgnoreCase("Pending")) {
                                notifs.add(" Request pending: " + sName + " (" + sEmail + ") - Subject: " + subject);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                notificationsArea.setText("Error reading requests.txt file!");
                return;
            }
        }

        // Read notifications.txt
        if (notifFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(notifFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",", 5);
                    if (parts.length >= 5 && parts[0].equalsIgnoreCase(tutorEmail)) {
                        String studentName = parts[1];
                        String studentEmail = parts[2];
                        String subject = parts[3];
                        String message = parts[4];
                        notifs.add("🔔" + studentName + " (" + studentEmail + ") " + message + " [" + subject + "]");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Display notifications
        if (notifs.isEmpty()) {
            notificationsArea.setText("No notifications available yet.");
        } else {
            StringBuilder builder = new StringBuilder("You have the following notifications:\n\n");
            for (String s : notifs) {
                builder.append("• ").append(s).append("\n\n");
            }
            notificationsArea.setText(builder.toString());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new TutorNotificationsPage("tutor", "tutor@gmail.com").setVisible(true));
    }
}




