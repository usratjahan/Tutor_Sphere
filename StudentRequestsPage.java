import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StudentRequestsPage extends JFrame {

    private String studentName;
    private String studentEmail;
    private JPanel requestPanel;

    public StudentRequestsPage(String name, String email) {
        this.studentName = name;
        this.studentEmail = email;

        setTitle("TutorSphere - My Tutor Requests");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        GradientBackground background = new GradientBackground();
        background.setLayout(new BorderLayout());
        setContentPane(background);

        Color whiteText = new Color(0xFFFFFF);
        Color boxColor = new Color(0x092635);

        JLabel title = new JLabel("Tutor Requests for " + name, JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 46));
        title.setForeground(whiteText);
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 30, 0));
        background.add(title, BorderLayout.NORTH);

        requestPanel = new JPanel();
        requestPanel.setLayout(new BoxLayout(requestPanel, BoxLayout.Y_AXIS));
        requestPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(requestPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        background.add(scrollPane, BorderLayout.CENTER);

        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Serif", Font.BOLD, 24));
        backBtn.setBackground(boxColor);
        backBtn.setForeground(whiteText);
        backBtn.setFocusPainted(false);
        addHoverEffect(backBtn, boxColor, boxColor.brighter());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.add(backBtn);
        background.add(bottomPanel, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> {
            new StudentDashboard(studentName, studentEmail).setVisible(true);
            dispose();
        });

        loadRequests();
    }

    //Load Requests for This Student
    private void loadRequests() {
        requestPanel.removeAll();

        File file = new File("requests.txt");
        if (!file.exists()) {
            JLabel noReq = new JLabel("No requests found!", JLabel.CENTER);
            noReq.setFont(new Font("Serif", Font.BOLD, 26));
            noReq.setForeground(Color.RED);
            requestPanel.add(noReq);
            refreshPanel();
            return;
        }

        boolean hasRequests = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 5) continue; 
                for (int i = 0; i < parts.length; i++) parts[i] = parts[i].trim();

                String sEmail = parts[1];
                String tName, tEmail, subject, status;

                if (parts.length >= 6) {
                    tName = parts[2];
                    tEmail = parts[3];
                    subject = parts[4];
                    status = parts[5];
                } else if (isStatus(parts[4])) {
                    // Format: sName, sEmail, tEmail, subject, status
                    tEmail = parts[2];
                    subject = parts[3];
                    status = parts[4];
                    String lookedUp = findNameByEmail(tEmail);
                    tName = lookedUp != null ? lookedUp : "(tutor)";
                } else {
                    // Format: sName, sEmail, tName, tEmail, subject
                    tName = parts[2];
                    tEmail = parts[3];
                    subject = parts[4];
                    status = "Pending";
                }

                if (sEmail.equalsIgnoreCase(studentEmail)) {
                    hasRequests = true;
                    JPanel card = createRequestCard(tName, tEmail, subject, status);
                    requestPanel.add(card);
                    requestPanel.add(Box.createVerticalStrut(10));
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading requests file!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        if (!hasRequests) {
            JLabel noReq = new JLabel("No tutor requests yet!", JLabel.CENTER);
            noReq.setFont(new Font("Serif", Font.BOLD, 26));
            noReq.setForeground(Color.RED);
            requestPanel.add(noReq);
        }

        refreshPanel();
    }

    //status word 
    private boolean isStatus(String s) {
        return s.equalsIgnoreCase("Pending") ||
               s.equalsIgnoreCase("Accepted") ||
               s.equalsIgnoreCase("Rejected");
    }

    //find tutor name by email from users.txt 
    private String findNameByEmail(String email) {
        File usersFile = new File("users.txt");
        if (!usersFile.exists()) return null;
        try (BufferedReader br = new BufferedReader(new FileReader(usersFile))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                String[] p = ln.split(",");
                if (p.length >= 3 && p[2].trim().equalsIgnoreCase(email.trim())) {
                    return p[1].trim(); 
                }
            }
        } catch (IOException ignored) {}
        return null;
    }

    //Create Each Request Card 
    private JPanel createRequestCard(String tutorName, String tutorEmail, String subject, String status) {
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(true);
        card.setBackground(new Color(0xA1C2BD));
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel info = new JLabel("<html><b>Tutor:</b> " + tutorName + " (" + tutorEmail + ")<br>"
                + "<b>Subject:</b> " + subject + "<br>"
                + "<b>Status:</b> " + status + "</html>");
        info.setFont(new Font("Serif", Font.PLAIN, 20));
        card.add(info, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);

        if (status.equalsIgnoreCase("Pending")) {
            JButton acceptBtn = new JButton("Accept");
            JButton declineBtn = new JButton("Reject");

            for (JButton b : new JButton[]{acceptBtn, declineBtn}) {
                b.setFont(new Font("Serif", Font.BOLD, 18));
                b.setBackground(new Color(0x092635));
                b.setForeground(Color.WHITE);
                b.setFocusPainted(false);
            }

            addHoverEffect(acceptBtn, new Color(0x092635), new Color(0x0B3245));
            addHoverEffect(declineBtn, new Color(0x092635), new Color(0x0B3245));

            acceptBtn.addActionListener(e -> updateRequestStatus(tutorEmail, subject, "Accepted"));
            declineBtn.addActionListener(e -> updateRequestStatus(tutorEmail, subject, "Rejected"));

            btnPanel.add(acceptBtn);
            btnPanel.add(declineBtn);
        }

        card.add(btnPanel, BorderLayout.SOUTH);
        return card;
    }

    private void updateRequestStatus(String tutorEmail, String subject, String newStatus) {
        File file = new File("requests.txt");
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 5) continue;
                for (int i = 0; i < parts.length; i++) parts[i] = parts[i].trim();

                String sName = parts[0];
                String sEmail = parts[1];
                String tName = "";
                String tEmail = "";
                String subj = "";
                String status = "Pending";

                if (parts.length >= 6) {
                    tName = parts[2]; tEmail = parts[3]; subj = parts[4]; status = parts[5];
                } else if (isStatus(parts[4])) {
                    tEmail = parts[2]; subj = parts[3]; status = parts[4];
                    String lookedUp = findNameByEmail(tEmail);
                    tName = lookedUp != null ? lookedUp : "(tutor)";
                } else {
                    tName = parts[2]; tEmail = parts[3]; subj = parts[4];
                }

                if (sEmail.equalsIgnoreCase(studentEmail)
                        && tEmail.equalsIgnoreCase(tutorEmail)
                        && subj.equalsIgnoreCase(subject)) {
                    line = String.join(",", sName, sEmail, tName, tEmail, subj, newStatus);
                    addTutorNotification(tutorEmail, studentName, studentEmail, subject, newStatus);
                }
                lines.add(line);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading requests file!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String l : lines) {
                writer.write(l);
                writer.newLine();
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error writing requests file!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "You have " + newStatus.toLowerCase() + " this request.",
                "Status Updated", JOptionPane.INFORMATION_MESSAGE);
        loadRequests();
    }

    private void addTutorNotification(String tutorEmail, String studentName, String studentEmail, String subject, String status) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("notifications.txt", true))) {
            String message = status.equalsIgnoreCase("Accepted")
                    ? " ✔️ accepted your tutoring request for " + subject + "."
                    : " ❌ rejected your tutoring request for " + subject + ".";
            writer.write(tutorEmail + "," + studentName + "," + studentEmail + "," + subject + "," + message);
            writer.newLine();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving tutor notification!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshPanel() {
        requestPanel.revalidate();
        requestPanel.repaint();
    }

    private void addHoverEffect(JButton button, Color base, Color hover) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { button.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent evt) { button.setBackground(base); }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new StudentRequestsPage("student", "student@gmail.com").setVisible(true));
    }
}







