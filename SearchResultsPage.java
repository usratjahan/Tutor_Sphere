import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.List;

public class SearchResultsPage extends JFrame {

    private List<String[]> tutorList;
    private String studentName, studentEmail, subjectQuery;

    public SearchResultsPage(List<String[]> tutorList, String studentName, String studentEmail, String subjectQuery) {
        this.tutorList = tutorList;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.subjectQuery = subjectQuery;

        setTitle("Tutors Matching: " + subjectQuery);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        GradientBackground background = new GradientBackground();
        background.setLayout(new BorderLayout());
        setContentPane(background);

        Color whiteText = new Color(0xFF, 0xFF, 0xFF);
        Color boxColor = new Color(0x09, 0x26, 0x35);

        JLabel title = new JLabel("Tutors Found for \"" + subjectQuery + "\"", JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 46));
        title.setForeground(whiteText);
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 30, 0));
        background.add(title, BorderLayout.NORTH);

        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setOpaque(false);

        for (String[] tutor : tutorList) {
            JPanel card = createTutorCard(tutor);
            resultsPanel.add(card);
            resultsPanel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        background.add(scrollPane, BorderLayout.CENTER);

        JButton backBtn = new JButton("Back to Search");
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
            new MatchmakingPage(studentName, studentEmail).setVisible(true);
            dispose();
        });
    }

    private JPanel createTutorCard(String[] tutor) {
        JPanel card = new JPanel(new GridLayout(0, 1));
        card.setOpaque(true);
        card.setBackground(new Color(0xA1C2BD));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        int n = tutor.length;
        for (int i = 0; i < n; i++) {
            tutor[i] = tutor[i].trim();
        }

        String name = n > 1 ? tutor[1] : "Unknown";
        String email = n > 2 ? tutor[2] : "Unknown";

        String phone = n >= 7 ? tutor[n - 3] : "N/A";
        String gender = n >= 7 ? tutor[n - 2] : "N/A";
        String cls = n >= 7 ? tutor[n - 1] : "N/A";

        StringBuilder subjectsList = new StringBuilder();
        if (n > 7) {
            for (int i = 4; i < n - 3; i++) {
                if (subjectsList.length() > 0) subjectsList.append(", ");
                subjectsList.append(tutor[i]);
            }
        }
        String subjects = subjectsList.length() == 0 ? "N/A" : subjectsList.toString();

        JLabel nameLbl = new JLabel("Tutor: " + name);
        JLabel emailLbl = new JLabel("Email: " + email);
        JLabel subjectLbl = new JLabel("Subjects: " + subjects);
        JLabel phoneLbl = new JLabel("Phone: " + phone);
        JLabel genderLbl = new JLabel("Gender: " + gender);
        JLabel classLbl = new JLabel("Class / Group: " + cls);

        Font infoFont = new Font("Serif", Font.PLAIN, 20);
        nameLbl.setFont(infoFont);
        emailLbl.setFont(infoFont);
        subjectLbl.setFont(infoFont);
        phoneLbl.setFont(infoFont);
        genderLbl.setFont(infoFont);
        classLbl.setFont(infoFont);

        card.add(nameLbl);
        card.add(emailLbl);
        card.add(subjectLbl);
        card.add(phoneLbl);
        card.add(genderLbl);
        card.add(classLbl);

        JButton requestBtn = new JButton("Send Request");
        requestBtn.setFont(new Font("Serif", Font.BOLD, 20));
        requestBtn.setBackground(new Color(0x092635));
        requestBtn.setForeground(Color.WHITE);
        requestBtn.setFocusPainted(false);
        addHoverEffect(requestBtn, new Color(0x092635), new Color(0x123A55));
        card.add(requestBtn);

        requestBtn.addActionListener(e -> sendRequest(name, email, subjectQuery));
        return card;
    }

    private void sendRequest(String tutorName, String tutorEmail, String subject) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("requests.txt", true))) {
            writer.write(studentName + "," + studentEmail + "," + tutorName + "," + tutorEmail + "," + subject + ",Pending");
            writer.newLine();
            JOptionPane.showMessageDialog(this, " ✔️ Request sent to " + tutorName + " successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, " ❌ Error sending request!", "Error", JOptionPane.PLAIN_MESSAGE);
        }
    }

    private void addHoverEffect(JButton button, Color base, Color hover) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { button.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent evt) { button.setBackground(base); }
        });
    }
}

