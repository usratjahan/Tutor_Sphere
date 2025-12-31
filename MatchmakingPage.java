import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MatchmakingPage extends JFrame {

    private String studentName;
    private String studentEmail;
    private JTextField subjectField;
    private JPanel resultsPanel;

    public MatchmakingPage(String name, String email) {
        this.studentName = name;
        this.studentEmail = email;

        setTitle("TutorSphere - Find Tutors");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        GradientBackground background = new GradientBackground();
        background.setLayout(new BorderLayout());
        setContentPane(background);

        Color whiteText = new Color(0xFFFFFF);
        Color boxColor = new Color(0x092635);

        JLabel title = new JLabel("Find Your Perfect Tutor", JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 46));
        title.setForeground(whiteText);
        title.setBorder(BorderFactory.createEmptyBorder(50, 0, 30, 0));
        background.add(title, BorderLayout.NORTH);

        // Search Bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        searchPanel.setOpaque(false);

        JLabel label = new JLabel("Enter Subject:");
        label.setFont(new Font("Serif", Font.PLAIN, 24));
        label.setForeground(Color.BLACK);

        subjectField = new JTextField(20);
        subjectField.setFont(new Font("Serif", Font.PLAIN, 22));

        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Serif", Font.BOLD, 22));
        searchBtn.setBackground(boxColor);
        searchBtn.setForeground(whiteText);
        searchBtn.setFocusPainted(false);

        addHoverEffect(searchBtn, boxColor, boxColor.brighter());
        searchPanel.add(label);
        searchPanel.add(subjectField);
        searchPanel.add(searchBtn);

        background.add(searchPanel, BorderLayout.NORTH);

        // Results Panel
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(resultsPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        background.add(scroll, BorderLayout.CENTER);

        // Back Button
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Serif", Font.BOLD, 24));
        backBtn.setBackground(boxColor);
        backBtn.setForeground(whiteText);
        backBtn.setFocusPainted(false);
        addHoverEffect(backBtn, boxColor, boxColor.brighter());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(backBtn);
        background.add(bottomPanel, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> {
            new StudentDashboard(studentName, studentEmail).setVisible(true);
            dispose();
        });

        searchBtn.addActionListener(e -> searchTutors());
    }

    private void searchTutors() {
        String query = subjectField.getText().trim().toLowerCase();

        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a subject!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        resultsPanel.removeAll();
        List<String[]> tutors = findTutors(query);

        if (tutors.isEmpty()) {
            JLabel noResults = new JLabel("No tutors found for subject: " + query);
            noResults.setFont(new Font("Serif", Font.BOLD, 26));
            noResults.setForeground(Color.RED);
            resultsPanel.add(noResults);
        } else {
            for (String[] tutor : tutors) {
                resultsPanel.add(createTutorCard(tutor));
                resultsPanel.add(Box.createVerticalStrut(10));
            }
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    //FIXED SEARCH LOGIC
    private List<String[]> findTutors(String subjectQueryLower) {
        List<String[]> tutors = new ArrayList<>();
        File file = new File("profiles.txt");

        if (!file.exists()) return tutors;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");

                // trim values
                for (int i = 0; i < parts.length; i++)
                    parts[i] = parts[i].trim();

                if (!parts[0].equalsIgnoreCase("Tutor"))
                    continue;

                int n = parts.length;

                if (n < 7) continue; 
                boolean match = false;
                for (int i = 4; i <= n - 3; i++) {
                    String sub = parts[i].toLowerCase();
                    if (sub.contains(subjectQueryLower)) {
                        match = true;
                        break;
                    }
                }

                if (match)
                    tutors.add(parts);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return tutors;
    }

    //FIXED CARD UI
    private JPanel createTutorCard(String[] t) {
        JPanel card = new JPanel(new GridLayout(0, 1));
        card.setBackground(new Color(0xA1C2BD));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String name = t[1];
        String email = t[2];

        int n = t.length;
        List<String> subs = new ArrayList<>();

        for (int i = 4; i <= n - 3; i++)
            subs.add(t[i]);

        String phone = t[n - 2];
        String gender = t[n - 1];

        card.add(new JLabel("Name: " + name));
        card.add(new JLabel("Email: " + email));
        card.add(new JLabel("Subjects: " + subs));
        card.add(new JLabel("Phone: " + phone));
        card.add(new JLabel("Gender: " + gender));

        JButton requestBtn = new JButton("Send Request");
        requestBtn.setFont(new Font("Serif", Font.BOLD, 20));
        requestBtn.setBackground(new Color(0x092635));
        requestBtn.setForeground(Color.WHITE);
        requestBtn.setFocusPainted(false);

        addHoverEffect(requestBtn, new Color(0x092635), new Color(0x0B3A57));

        requestBtn.addActionListener(e -> sendRequest(name, email, subs.get(0)));

        card.add(requestBtn);

        return card;
    }

    private void sendRequest(String tutorName, String tutorEmail, String subject) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("requests.txt", true))) {
            writer.write(studentName + "," + studentEmail + "," + tutorName + "," + tutorEmail + "," + subject + ",Pending");
            writer.newLine();

            JOptionPane.showMessageDialog(this,
                    " ✔️ Request sent to " + tutorName + "!",
                    "Success", JOptionPane.PLAIN_MESSAGE);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error sending request!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void addHoverEffect(JButton button, Color base, Color hover) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { button.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent evt) { button.setBackground(base); }
        });
    }
}











