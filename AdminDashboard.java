import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdminDashboard extends DashboardBase {

    private JPanel displayPanel;
    private JLabel statsLabel;

    public AdminDashboard(String name, String email) {
        super("TutorSphere - Admin Dashboard", name, email, true);
    }

    @Override
    protected void setupContent() {

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));
        background.add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 6, 15, 10));
        buttonPanel.setOpaque(false);

        String[] btnNames = {
                "View All Users", "Add User", "Edit User",
                "Delete User", "Verify Tutors", "Refresh"
        };

        JButton[] buttons = new JButton[btnNames.length];
        Font buttonFont = new Font("Serif", Font.BOLD, 20);

        for (int i = 0; i < btnNames.length; i++) {
            buttons[i] = new JButton(btnNames[i]);
            buttons[i].setFont(buttonFont);
            buttons[i].setBackground(boxColor);
            buttons[i].setForeground(whiteText);
            buttons[i].setFocusPainted(false);
            addHoverEffect(buttons[i], boxColor, boxColor.brighter());
            buttonPanel.add(buttons[i]);
        }

        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        displayPanel = new JPanel(new BorderLayout());
        displayPanel.setOpaque(false);
        JScrollPane scroller = new JScrollPane(displayPanel);
        scroller.setBorder(null);
        mainPanel.add(scroller, BorderLayout.CENTER);

        statsLabel = new JLabel("Platform Stats: --", JLabel.CENTER);
        statsLabel.setFont(new Font("Serif", Font.BOLD, 22));
        statsLabel.setForeground(blackText);
        mainPanel.add(statsLabel, BorderLayout.SOUTH);

        buttons[0].addActionListener(e -> viewAllUsers());
        buttons[1].addActionListener(e -> addUser());
        buttons[2].addActionListener(e -> editUser());
        buttons[3].addActionListener(e -> deleteUser());
        buttons[4].addActionListener(e -> verifyTutors());
        buttons[5].addActionListener(e -> refreshDashboard());

        viewAllUsers();
        showStats();
    }

    // VIEW USERS 
    private void viewAllUsers() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        displayPanel.removeAll();

        File file = new File("users.txt");
        if (!file.exists()) {
            JLabel noUsers = new JLabel("No users found!");
            noUsers.setFont(new Font("Serif", Font.PLAIN, 20));
            container.add(noUsers);
        } else {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                boolean any = false;

                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;

                    String[] p = line.split(",", -1);
                    // role,name,email,password
                    String role = p.length > 0 ? p[0].trim() : "Unknown";
                    String name = p.length > 1 ? p[1].trim() : "Unknown";
                    String email = p.length > 2 ? p[2].trim() : "Unknown";
                    String password = p.length > 3 ? p[3] : "";

                    JPanel card = new JPanel(new BorderLayout(8, 8));
                    card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
                    card.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
                    card.setBackground(new Color(255, 255, 255, 200));
                    JPanel info = new JPanel();
                    info.setOpaque(false);
                    info.setLayout(new GridLayout(4, 1, 4, 4));

                    Font labelFont = new Font("Serif", Font.PLAIN, 18);
                    JLabel roleLbl = new JLabel("Role: " + role);
                    roleLbl.setFont(labelFont);
                    JLabel nameLbl = new JLabel("Name: " + name);
                    nameLbl.setFont(labelFont);
                    JLabel emailLbl = new JLabel("Email: " + email);
                    emailLbl.setFont(labelFont);
                    JLabel passLbl = new JLabel("Password: " + maskPassword(password));
                    passLbl.setFont(labelFont);

                    info.add(roleLbl);
                    info.add(nameLbl);
                    info.add(emailLbl);
                    info.add(passLbl);

                    card.add(info, BorderLayout.CENTER);

                    JPanel right = new JPanel();
                    right.setOpaque(false);
                    right.setPreferredSize(new Dimension(120, 10));
                    card.add(right, BorderLayout.EAST);
                    container.add(Box.createVerticalStrut(8));
                    container.add(card);

                    any = true;
                }

                if (!any) {
                    JLabel noUsers = new JLabel("No users found!");
                    noUsers.setFont(new Font("Serif", Font.PLAIN, 20));
                    container.add(noUsers);
                }

            } catch (IOException ex) {
                JLabel err = new JLabel("Error reading users.txt");
                err.setFont(new Font("Serif", Font.PLAIN, 20));
                container.add(err);
            }
        }
        displayPanel.add(container, BorderLayout.NORTH);
        displayPanel.revalidate();
        displayPanel.repaint();
        showStats();
    }

    //password using '*' 
    private String maskPassword(String pwd) {
        if (pwd == null || pwd.length() == 0) return "******";
        int len = pwd.length();
        try {
            return "*".repeat(len);
        } catch (NoSuchMethodError | Exception e) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len; i++) sb.append('*');
            return sb.toString();
        }
    }

    // ADD USER 
    private void addUser() {
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField passField = new JTextField();
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Tutor", "Student", "Admin"});

        Object[] fields = {
                "Name:", nameField,
                "Email:", emailField,
                "Password:", passField,
                "Role:", roleBox
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Add New User", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String newName = nameField.getText().trim();
            String newEmail = emailField.getText().trim().toLowerCase(); 
            String newPass = passField.getText().trim();
            String newRole = (String) roleBox.getSelectedItem();

            if (newName.isEmpty() || newEmail.isEmpty() || newPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            File file = new File("users.txt");
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().isEmpty()) continue;
                        String[] parts = line.split(",", -1);
                        if (parts.length >= 3) {
                            String existingEmail = parts[2].trim().toLowerCase();
                            if (existingEmail.equals(newEmail)) {
                                JOptionPane.showMessageDialog(this, "This email is already registered! Please use another email.", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error reading users file!", "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                    return;
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(newRole + "," + newName + "," + newEmail + "," + newPass);
                writer.newLine();
                JOptionPane.showMessageDialog(this, "User added successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error adding user!", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }

            refreshDashboard();
        }
    }

    // EDIT USER
    private void editUser() {
        String emailToEdit = JOptionPane.showInputDialog(this, "Enter email of user to edit:");
        if (emailToEdit == null || emailToEdit.trim().isEmpty()) return;
        emailToEdit = emailToEdit.trim();

        File file = new File("users.txt");
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "Users file missing!");
            return;
        }

        File temp = new File("temp_users.txt");
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             BufferedWriter writer = new BufferedWriter(new FileWriter(temp))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    writer.write(line);
                    writer.newLine();
                    continue;
                }

                String[] p = line.split(",", -1);

                // Match email case-insensitively
                if (p.length >= 4 && p[2].trim().equalsIgnoreCase(emailToEdit)) {
                    JTextField nameField = new JTextField(p[1].trim());
                    JTextField passField = new JTextField(p[3].trim());
                    JComboBox<String> roleBox = new JComboBox<>(new String[]{"Tutor", "Student", "Admin"});
                    roleBox.setSelectedItem(p[0].trim());

                    Object[] fields = {
                            "Name:", nameField,
                            "Password:", passField,
                            "Role:", roleBox
                    };

                    int result = JOptionPane.showConfirmDialog(this, fields, "Edit User", JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.OK_OPTION) {
                        String newName = nameField.getText().trim();
                        String newPass = passField.getText().trim();
                        String newRole = (String) roleBox.getSelectedItem();
                        String normalizedEmail = p[2].trim().toLowerCase();

                        writer.write(newRole + "," + newName + "," + normalizedEmail + "," + newPass);
                        writer.newLine();
                        updated = true;
                        continue; 
                    } else {
                        writer.write(line);
                        writer.newLine();
                        continue;
                    }
                }

                writer.write(line);
                writer.newLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error editing users file!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!file.delete()) {
            System.err.println("Warning: could not delete original users file.");
        }
        if (!temp.renameTo(file)) {
            System.err.println("Warning: could not rename temp file to users.txt");
        }

        JOptionPane.showMessageDialog(this, updated ? "User updated!" : "User not found or update cancelled!");
        refreshDashboard();
    }

    // DELETE USER
    private void deleteUser() {
        String emailToDelete = JOptionPane.showInputDialog(this, "Enter email of user to delete:");
        if (emailToDelete == null || emailToDelete.trim().isEmpty()) return;
        emailToDelete = emailToDelete.trim();

        File file = new File("users.txt");
        File temp = new File("temp.txt");

        boolean deleted = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             BufferedWriter writer = new BufferedWriter(new FileWriter(temp))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    writer.write(line);
                    writer.newLine();
                    continue;
                }

                String[] p = line.split(",", -1);
                if (p.length >= 3 && p[2].trim().equalsIgnoreCase(emailToDelete)) {
                    deleted = true;
                    continue; 
                }
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Replace files
        if (!file.delete()) {
            System.err.println("Warning: could not delete original users file.");
        }
        if (!temp.renameTo(file)) {
            System.err.println("Warning: could not rename temp file to users.txt");
        }

        JOptionPane.showMessageDialog(this, deleted ? "User deleted!" : "User not found!");
        refreshDashboard();
    }

    // TUTOR VERIFICATION 
    private void verifyTutors() {

        displayPanel.removeAll();
        displayPanel.revalidate();
        displayPanel.repaint();

        File file = new File("verifications.txt");
        if (!file.exists()) {
            displayPanel.add(new JLabel("No tutor verification requests found!"));
            displayPanel.revalidate();
            displayPanel.repaint();
            return;
        }

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] p = line.split(",", -1);

                if (p.length >= 5 && p[0].trim().equalsIgnoreCase("Tutor")) {
                    found = true;

                    String tName = p[1].trim();
                    String tEmail = p[2].trim();
                    String cert = p[3].trim();
                    String status = p[4].trim();

                    JPanel card = new JPanel();
                    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                    card.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                    card.setBackground(Color.WHITE);

                    JLabel lbl = new JLabel(
                            "<html><b>Name:</b> " + tName +
                                    "<br><b>Email:</b> " + tEmail +
                                    "<br><b>Certificate:</b> " + cert +
                                    "<br><b>Status:</b> " + status + "</html>"
                    );
                    lbl.setFont(new Font("Serif", Font.PLAIN, 18));

                    if (status.equalsIgnoreCase("Pending")) {
                        JButton approve = new JButton("Approve");
                        approve.setBackground(new Color(0x2E8B57));
                        approve.setForeground(Color.WHITE);

                        JButton reject = new JButton("Reject");
                        reject.setBackground(new Color(0x8B0000));
                        reject.setForeground(Color.WHITE);

                        approve.addActionListener(e -> updateTutorStatus(tName, tEmail, cert, "Approved"));
                        reject.addActionListener(e -> updateTutorStatus(tName, tEmail, cert, "Rejected"));

                        JPanel btnRow = new JPanel();
                        btnRow.add(approve);
                        btnRow.add(reject);

                        card.add(lbl);
                        card.add(btnRow);
                    } else {
                        card.add(lbl);
                    }

                    card.add(Box.createVerticalStrut(10));
                    container.add(card);
                }
            }

            if (!found)
                displayPanel.add(new JLabel("No tutor verification requests found!"));
            else
                displayPanel.add(container, BorderLayout.NORTH);

        } catch (IOException ex) {
            displayPanel.add(new JLabel("Error reading verifications.txt"));
            ex.printStackTrace();
        }

        displayPanel.revalidate();
        displayPanel.repaint();
    }

    // UPDATE TUTOR STATUS,SEND NOTIFICATION
    private void updateTutorStatus(String name, String email, String cert, String status) {

        File file = new File("verifications.txt");
        File temp = new File("ver_temp.txt");

        try (BufferedReader r = new BufferedReader(new FileReader(file));
             BufferedWriter w = new BufferedWriter(new FileWriter(temp))) {

            String line;
            while ((line = r.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    w.write(line);
                    w.newLine();
                    continue;
                }

                String[] p = line.split(",", -1);

                if (p.length >= 5 && p[2].trim().equalsIgnoreCase(email)) {
                    w.write("Tutor," + name + "," + email + "," + cert + "," + status);
                    w.newLine();

                    addTutorVerificationNotification(name, email, status);

                } else {
                    w.write(line);
                    w.newLine();
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (!file.delete()) {
            System.err.println("Warning: could not delete verifications.txt");
        }
        if (!temp.renameTo(file)) {
            System.err.println("Warning: could not rename ver_temp.txt");
        }

        JOptionPane.showMessageDialog(this, name + " has been " + status + "!");
        verifyTutors();
    }

    // SEND NOTIFICATION TO TUTOR
    private void addTutorVerificationNotification(String tutorName, String tutorEmail, String status) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("notifications.txt", true))) {

            String message;
            if (status.equalsIgnoreCase("Approved")) {
                message = "Your tutor verification has been approved! You are now a verified tutor.";
            } else {
                message = "Your tutor verification has been rejected. Please resubmit proper documents.";
            }

            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            //tutorEmail, tutorName, sender, subject, message, timestamp
            writer.write(tutorEmail + "," + tutorName + ",ADMIN,Verification," + message + "," + timestamp);
            writer.newLine();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // REFRESH DASHBOARD
    private void refreshDashboard() {
        viewAllUsers();
        showStats();
    }

    // SHOW PLATFORM STATS
    private void showStats() {
        int tutors = 0, students = 0, admins = 0;

        File file = new File("users.txt");
        if (!file.exists()) {
            statsLabel.setText("Platform Stats: No users yet.");
            return;
        }

        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",", -1);
                String role = p.length > 0 ? p[0].trim() : "";
                if (role.equalsIgnoreCase("Tutor")) tutors++;
                else if (role.equalsIgnoreCase("Student")) students++;
                else if (role.equalsIgnoreCase("Admin")) admins++;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        statsLabel.setText(
                "Platform Stats: Tutors = " + tutors +
                        " | Students = " + students +
                        " | Admins = " + admins +
                        " | Total = " + (tutors + students + admins)
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new AdminDashboard("Admin", "admin@tutorsphere.com").setVisible(true)
        );
    }
}



