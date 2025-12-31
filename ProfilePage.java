import javax.swing.*;
import java.awt.*;
import java.io.*;

public class ProfilePage extends JFrame {

    private JTextField nameField, emailField, ageField, subjectField, phoneField, genderField, classField;
    private JTextArea addressArea;
    private String role;

    private File dataFile(String filename) {
        return new File(System.getProperty("user.dir"), filename);
    }

    public ProfilePage(String role, String name, String email) {
        this.role = role;

        setTitle("TutorSphere - " + role + " Profile");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        GradientBackground bg = new GradientBackground();
        bg.setLayout(new BorderLayout());
        setContentPane(bg);

        Color white = Color.WHITE;
        Color box = new Color(0x092635);
        Color black = Color.BLACK;

        // Title
        JLabel title = new JLabel(role + " Profile Information", JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 46));
        title.setForeground(white);
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        bg.add(title, BorderLayout.NORTH);

        // Center panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        bg.add(centerPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Serif", Font.PLAIN, 24);
        Font fieldFont = new Font("Serif", Font.PLAIN, 20);
        Dimension fieldSize = new Dimension(260, 35);

        int row = 0;
        addField(centerPanel, gbc, "Name:", labelFont, fieldFont, nameField = new JTextField(name), fieldSize, row++);
        nameField.setEditable(false);

        addField(centerPanel, gbc, "Email:", labelFont, fieldFont, emailField = new JTextField(email), fieldSize, row++);
        emailField.setEditable(false);

        addField(centerPanel, gbc, "Age:", labelFont, fieldFont, ageField = new JTextField(), fieldSize, row++);
        addField(centerPanel, gbc, "Subject:", labelFont, fieldFont, subjectField = new JTextField(), fieldSize, row++);
        addField(centerPanel, gbc, "Phone:", labelFont, fieldFont, phoneField = new JTextField(), fieldSize, row++);

        // Gender Field
        addField(centerPanel, gbc, "Gender:", labelFont, fieldFont, genderField = new JTextField(), fieldSize, row++);

        // Class Field
        if (role.equalsIgnoreCase("Student")) {
            addField(centerPanel, gbc, "Class (Grade + Group):", labelFont, fieldFont,
                    classField = new JTextField(), fieldSize, row++);
        }

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 30));
        btnPanel.setOpaque(false);

        JButton saveBtn = new JButton("Save Profile");
        JButton backBtn = new JButton("Back");
        JButton[] btns = {saveBtn, backBtn};

        for (JButton b : btns) {
            b.setFont(new Font("Serif", Font.BOLD, 22));
            b.setPreferredSize(new Dimension(180, 55)); 
            b.setBackground(box);
            b.setForeground(white);
            b.setFocusPainted(false);
            addHover(b, box, box.brighter());
            btnPanel.add(b);
        }

        bg.add(btnPanel, BorderLayout.SOUTH);

        // Button actions
        saveBtn.addActionListener(e -> saveProfile());
        backBtn.addActionListener(e -> {
            if (role.equalsIgnoreCase("Tutor"))
                new TutorDashboard(name, email).setVisible(true);
            else
                new StudentDashboard(name, email).setVisible(true);
            dispose();
        });

        loadProfile(email);
    }

    // Save Profile
    private void saveProfile() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String age = ageField.getText().trim();
        String subject = subjectField.getText().trim();
        String phone = phoneField.getText().trim();
        String gender = genderField.getText().trim();
        String studentClass = (classField != null) ? classField.getText().trim() : "";

        if (age.isEmpty() || subject.isEmpty() || phone.isEmpty() || gender.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try { Integer.parseInt(age); }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Age must be a number!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File file = dataFile("profiles.txt");
        File tmp = dataFile("temp_profiles.txt");
        boolean updated = false;

        try {
            if (!file.exists()) file.createNewFile();

            try (BufferedReader r = new BufferedReader(new FileReader(file));
                 BufferedWriter w = new BufferedWriter(new FileWriter(tmp))) {

                String line;
                while ((line = r.readLine()) != null) {
                    String[] p = line.split(",");
                    if (p.length >= 3 && p[2].equalsIgnoreCase(email)) {
                        w.write(role + "," + name + "," + email + "," + age + "," + subject + "," +
                                phone + "," + gender + "," + studentClass);
                        w.newLine();
                        updated = true;
                    } else {
                        w.write(line);
                        w.newLine();
                    }
                }

                if (!updated) {
                    w.write(role + "," + name + "," + email + "," + age + "," + subject + "," +
                            phone + "," + gender + "," + studentClass);
                    w.newLine();
                }
            }

            file.delete();
            tmp.renameTo(file);

            JOptionPane.showMessageDialog(this,
               "<html><span style='color:green;font-weight:bold;'>✔</span> " +
                (updated ? "Profile Updated!" : "Profile Created!") + "</html>",
          "Success", JOptionPane.PLAIN_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving profile!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Load Profile
    private void loadProfile(String email) {
        File file = dataFile("profiles.txt");
        if (!file.exists()) return;

        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 7 && p[2].equalsIgnoreCase(email)) {
                    ageField.setText(p[3]);
                    subjectField.setText(p[4]);
                    phoneField.setText(p[5]);
                    genderField.setText(p[6]);
                    if (classField != null && p.length >= 8) classField.setText(p[7]);
                    break;
                }
            }
        } catch (Exception ignored) {}
    }

    private void addField(JPanel panel, GridBagConstraints gbc,
                          String text, Font lf, Font ff, JTextField field, Dimension size, int row) {

        JLabel label = new JLabel(text);
        label.setFont(lf);
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(label, gbc);

        field.setFont(ff);
        field.setPreferredSize(size);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(field, gbc);
    }

    private void addHover(JButton b, Color base, Color hover) {
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { b.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent evt) { b.setBackground(base); }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new ProfilePage("Student", "Student", "student@gmail.com").setVisible(true));
    }
}

