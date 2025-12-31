import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FindStudentsPage extends JFrame {

    private JTextField subjectField;
    private String tutorName;
    private String tutorEmail;

    private File dataFile(String filename) {
        return new File(System.getProperty("user.dir"), filename);
    }

    private JPanel resultPanel;

    public FindStudentsPage(String name, String email) {
        this.tutorName = name;
        this.tutorEmail = email;

        setTitle("TutorSphere - Find Students");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        GradientBackground bg = new GradientBackground();
        bg.setLayout(new BorderLayout());
        setContentPane(bg);

        Color white = Color.WHITE;
        Color box = new Color(0x092635);

        //TITLE 
        JLabel title = new JLabel("Find Students by Subject", JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 46));
        title.setForeground(white);

        //SEARCH BAR
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        searchPanel.setOpaque(false);

        JLabel label = new JLabel("Enter Subject:");
        label.setFont(new Font("Serif", Font.BOLD, 26));
        label.setForeground(Color.BLACK);

        subjectField = new JTextField(20);
        subjectField.setFont(new Font("Serif", Font.PLAIN, 24));

        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Serif", Font.BOLD, 24));
        searchBtn.setBackground(box);
        searchBtn.setForeground(white);
        searchBtn.setFocusPainted(false);
        addHover(searchBtn, box, box.brighter());

        searchPanel.add(label);
        searchPanel.add(subjectField);
        searchPanel.add(searchBtn);

        // TOP WRAPPER
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(title);
        top.add(Box.createVerticalStrut(10));
        top.add(searchPanel);

        bg.add(top, BorderLayout.NORTH);

        // RESULTS PANEL
        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(resultPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        bg.add(scroll, BorderLayout.CENTER);

        // BACK BUTTON
        JButton back = new JButton("Back");
        back.setFont(new Font("Serif", Font.BOLD, 26));
        back.setBackground(box);
        back.setForeground(white);
        addHover(back, box, box.brighter());

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.add(back);

        bg.add(bottom, BorderLayout.SOUTH);

        // ACTIONS
        searchBtn.addActionListener(e -> searchStudents());
        back.addActionListener(e -> {
            new TutorDashboard(tutorName, tutorEmail).setVisible(true);
            dispose();
        });
    }

    // SEARCH STUDENTS WITH MULTI-SUBJECT SUPPORT
    private void searchStudents() {
        resultPanel.removeAll();
        String subject = subjectField.getText().trim().toLowerCase();

        if (subject.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a subject!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File file = dataFile("profiles.txt");
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "No student profiles found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                if (!parts[0].equalsIgnoreCase("Student")) continue;

                String name = parts[1].trim();
                String email = parts[2].trim();

                // MULTI SUBJECT PARSING 
                List<String> subjects = new ArrayList<>();
                String phone = "";
                String gender = "";
                String classGroup = "";

                for (int i = 4; i < parts.length; i++) {
                    String item = parts[i].trim().toLowerCase();

                    if (item.matches("\\d+")) {  
                        phone = parts[i].trim();
                        continue;
                    }
                    if (item.equals("male") || item.equals("female")) {
                        gender = parts[i].trim();
                        continue;
                    }
                    if (item.contains("-")) { 
                        classGroup = parts[i].trim();
                        continue;
                    }

                    if (!item.isEmpty())
                        subjects.add(item);
                }

                // subject match
                if (!subjects.contains(subject)) continue;

                found = true;
                resultPanel.add(createStudentCard(name, email, subjects, phone, gender, classGroup));
                resultPanel.add(Box.createVerticalStrut(10));
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading profiles!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        if (!found) {
            JLabel noMatch = new JLabel("No students found for: " + subject);
            noMatch.setFont(new Font("Serif", Font.BOLD, 26));
            noMatch.setForeground(Color.RED);
            resultPanel.add(noMatch);
        }

        resultPanel.revalidate();
        resultPanel.repaint();
    }

    // CARD UI FOR A STUDENT
    private JPanel createStudentCard(String name, String email, List<String> subjects, String phone, String gender, String classGroup) {
        JPanel card = new JPanel(new GridLayout(0, 1));
        card.setBackground(new Color(0xA1C2BD));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        card.add(new JLabel("Name: " + name));
        card.add(new JLabel("Email: " + email));
        card.add(new JLabel("Subjects: " + String.join(", ", subjects)));
        card.add(new JLabel("Phone: " + phone));
        card.add(new JLabel("Gender: " + gender));
        card.add(new JLabel("Class: " + classGroup));

        JButton req = new JButton("Send Request");
        req.setBackground(new Color(0x092635));
        req.setForeground(Color.WHITE);
        req.setFont(new Font("Serif", Font.BOLD, 20));
        addHover(req, new Color(0x092635), new Color(0x0B354A));

        req.addActionListener(e -> sendRequest(name, email, subjects));

        card.add(req);
        return card;
    }

    // SEND REQUEST
    private void sendRequest(String studentName, String email, List<String> subjects) {
    String subject = (subjects.size() > 0) ? subjects.get(0) : "Unknown";

    try (BufferedWriter w = new BufferedWriter(new FileWriter(dataFile("requests.txt"), true))) {
        w.write(studentName + "," + email + "," + tutorName + "," + tutorEmail + "," + subject + ",Pending");
        w.newLine();

        JOptionPane.showMessageDialog(this, "Request sent to " + studentName + "!");
    } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Error sending request!", "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    private void addHover(JButton btn, Color base, Color hover) {
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(hover);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(base);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new FindStudentsPage("Tutor", "tutor@gmail.com").setVisible(true));
    }
}








