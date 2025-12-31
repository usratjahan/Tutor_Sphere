import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TutorRequestsPage extends JFrame {

    private String tutorName;
    private String tutorEmail;
    private JPanel requestPanel;

    public TutorRequestsPage(String name, String email) {
        this.tutorName = name;
        this.tutorEmail = email;

        setTitle("TutorSphere - Student Requests");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        GradientBackground background = new GradientBackground();
        background.setLayout(new BorderLayout());
        setContentPane(background);

        JLabel title = new JLabel("Student Requests for " + name, JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 40));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
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
        backBtn.setBackground(new Color(0x092635));
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> {
            new TutorDashboard(tutorName, tutorEmail).setVisible(true);
            dispose();
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(backBtn);
        background.add(bottomPanel, BorderLayout.SOUTH);

        loadRequests();
    }

    private void loadRequests() {
        requestPanel.removeAll();
        File file = new File("requests.txt");

        if (!file.exists()) {
            JLabel noReq = new JLabel("No student requests found!", JLabel.CENTER);
            noReq.setFont(new Font("Serif", Font.BOLD, 24));
            noReq.setForeground(Color.RED);
            requestPanel.add(noReq);
            refresh();
            return;
        }

        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length == 6 && parts[3].equalsIgnoreCase(tutorEmail)) {
                    found = true;
                    JPanel card = createRequestCard(parts);
                    requestPanel.add(card);
                    requestPanel.add(Box.createVerticalStrut(15));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!found) {
            JLabel noReq = new JLabel("No requests yet!", JLabel.CENTER);
            noReq.setFont(new Font("Serif", Font.BOLD, 24));
            noReq.setForeground(Color.RED);
            requestPanel.add(noReq);
        }

        refresh();
    }

    private JPanel createRequestCard(String[] parts) {
        String studentName = parts[0];
        String studentEmail = parts[1];
        String subject = parts[4];
        String status = parts[5];

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(0xA1C2BD));
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel info = new JLabel("<html><b>Student:</b> " + studentName + " (" + studentEmail + ")<br>"
                + "<b>Subject:</b> " + subject + "<br>"
                + "<b>Status:</b> " + status + "</html>");
        info.setFont(new Font("Serif", Font.PLAIN, 20));
        card.add(info, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);

        if (status.equalsIgnoreCase("Pending")) {

            JButton acceptBtn = new JButton("Accept");
            JButton declineBtn = new JButton("Decline");

            for (JButton b : new JButton[]{acceptBtn, declineBtn}) {
                b.setFont(new Font("Serif", Font.BOLD, 18));
                b.setBackground(new Color(0x092635));
                b.setForeground(Color.WHITE);
            }

            acceptBtn.addActionListener(e -> updateStatus(studentEmail, studentName, "Accepted", subject));
            declineBtn.addActionListener(e -> updateStatus(studentEmail, studentName, "Rejected", subject));

            btnPanel.add(acceptBtn);
            btnPanel.add(declineBtn);
        }

        card.add(btnPanel, BorderLayout.SOUTH);
        return card;
    }
    // UPDATE STATUS AND ADD NOTIFICATION
    private void updateStatus(String studentEmail, String studentName, String newStatus, String subject) {

        File file = new File("requests.txt");
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] parts = line.split(",");

                if (parts.length == 6 &&
                    parts[1].equalsIgnoreCase(studentEmail) &&
                    parts[3].equalsIgnoreCase(tutorEmail) &&
                    parts[4].equalsIgnoreCase(subject)) {

                    parts[5] = newStatus;
                    line = String.join(",", parts);

                    //notification to student
                    addStudentNotification(studentEmail, studentName, subject, newStatus);
                }

                lines.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // WRITE UPDATED FILE
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(this,
                " ✔️ Request " + newStatus.toLowerCase() + " successfully!",
                "Updated", JOptionPane.PLAIN_MESSAGE);

        loadRequests();
    }
    // FIXED NOTIFICATION FORMAT FOR STUDENTS
    private void addStudentNotification(String studentEmail, String studentName,
                                        String subject, String status) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("notifications.txt", true))) {

            String message;

            if (status.equalsIgnoreCase("Accepted")) {
                message = "Your tutoring request for " + subject + " was accepted by " + tutorName + ".";
            } else {
                message = "Your tutoring request for " + subject + " was rejected by " + tutorName + ".";
            }

            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new java.util.Date());

            // receiverEmail, senderName, senderEmail, type, message, timestamp
            writer.write(studentEmail + "," + tutorName + "," + tutorEmail
                    + ",Request," + message + "," + timestamp);
            writer.newLine();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void refresh() {
        requestPanel.revalidate();
        requestPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new TutorRequestsPage("tutor", "tutor@gmail.com").setVisible(true)
        );
    }
}



