import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class RegisterPage extends JFrame {

    private JTextField nameField, emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;

    public RegisterPage() {
        setTitle("TutorSphere - Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Gradient Background
        GradientBackground background = new GradientBackground();
        background.setLayout(new BorderLayout());
        setContentPane(background);

        // Colors
        Color whiteText = new Color(0xFFFFFF);
        Color boxColor = new Color(0x092635);
        Color blackText = new Color(0x000000);

        // Title
        JLabel title = new JLabel("Register as Tutor or Student", JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 48));
        title.setForeground(whiteText);
        title.setBorder(BorderFactory.createEmptyBorder(60, 0, 40, 0));
        background.add(title, BorderLayout.NORTH);

        //Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Serif", Font.PLAIN, 26);
        Font fieldFont = new Font("Serif", Font.PLAIN, 22);

        // Full Name
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(labelFont);
        nameLabel.setForeground(blackText);
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);

        nameField = new JTextField(20);
        nameField.setFont(fieldFont);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(nameField, gbc);

        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        emailLabel.setForeground(blackText);
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(emailLabel, gbc);

        emailField = new JTextField(20);
        emailField.setFont(fieldFont);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(emailField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(labelFont);
        passwordLabel.setForeground(blackText);
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(fieldFont);
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(passwordField, gbc);

        // Role Selection
        JLabel roleLabel = new JLabel("Register As:");
        roleLabel.setFont(labelFont);
        roleLabel.setForeground(blackText);
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(roleLabel, gbc);

        String[] roles = {"Tutor", "Student", "Admin"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(fieldFont);
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(roleComboBox, gbc);

        background.add(formPanel, BorderLayout.CENTER);

        // Buttons 
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 80, 40));
        buttonPanel.setOpaque(false);

        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("Back");

        JButton[] buttons = {registerBtn, backBtn};
        Font buttonFont = new Font("Serif", Font.BOLD, 28);
        Dimension buttonSize = new Dimension(250, 80);

        for (JButton btn : buttons) {
            btn.setFont(buttonFont);
            btn.setPreferredSize(buttonSize);
            btn.setBackground(boxColor);
            btn.setForeground(whiteText);
            btn.setFocusPainted(false);
            addHoverEffect(btn, boxColor, boxColor.brighter());
        }

        buttonPanel.add(registerBtn);
        buttonPanel.add(backBtn);
        background.add(buttonPanel, BorderLayout.SOUTH);

        // Button Actions 
        registerBtn.addActionListener(e -> registerUser());
        backBtn.addActionListener(e -> {
            new Homepage().setVisible(true);
            dispose();
        });
    }

    // Register Logic 
    private void registerUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim().toLowerCase();
        String password = new String(passwordField.getPassword()).trim();
        String role = (String) roleComboBox.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File file = new File("users.txt");

        try {
            // Check email and name against existing entries
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length >= 3) {
                            String existingName = parts[1].trim();
                            String existingEmail = parts[2].trim().toLowerCase();

                            if (existingEmail.equals(email)) {
                                JOptionPane.showMessageDialog(this, "This email is already registered!", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            if (existingName.equalsIgnoreCase(name)) {
                                JOptionPane.showMessageDialog(this, "This name is already registered!", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                    }
                }
            }

            // Write new user data 
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(role + "," + name + "," + email + "," + password);
                writer.newLine();
            }

            JLabel messageLabel = new JLabel();
            messageLabel.setFont(new Font("Serif", Font.BOLD, 18));
            messageLabel.setText("<html><span style='color:#006400;'>✔</span> <span style='color:#000000;'> Registration successful!</span></html>");

            JOptionPane.showMessageDialog(
                this,
                messageLabel,
                "Success",
                JOptionPane.PLAIN_MESSAGE
            );

            // Redirect to login page
            new LoginPage().setVisible(true);
            dispose();

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving user data!", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void addHoverEffect(JButton button, Color base, Color hover) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { button.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent evt) { button.setBackground(base); }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterPage().setVisible(true));
    }
}


