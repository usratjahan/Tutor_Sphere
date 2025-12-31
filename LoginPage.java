import javax.swing.*;
import java.awt.*;
import java.io.*;

public class LoginPage extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginPage() {
        setTitle("TutorSphere - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Background
        GradientBackground background = new GradientBackground();

        background.setLayout(new BorderLayout());
        setContentPane(background);

        // Colors
        Color whiteText = new Color(0xFFFFFF);
        Color boxColor = new Color(0x092635);
        Color blackText = new Color(0x000000);

        // Title
        JLabel title = new JLabel("Welcome Back! Please Login", JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 48));
        title.setForeground(whiteText);
        title.setBorder(BorderFactory.createEmptyBorder(60, 0, 40, 0));
        background.add(title, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 10, 20, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Serif", Font.PLAIN, 26);
        Font fieldFont = new Font("Serif", Font.PLAIN, 22);

        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        emailLabel.setForeground(blackText);
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(emailLabel, gbc);

        emailField = new JTextField(20);
        emailField.setFont(fieldFont);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(emailField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(labelFont);
        passwordLabel.setForeground(blackText);
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(fieldFont);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(passwordField, gbc);

        background.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 80, 40));

        JButton loginBtn = new JButton("Login");
        JButton backBtn = new JButton("Back");

        JButton[] buttons = {loginBtn, backBtn};
        Font buttonFont = new Font("Serif", Font.BOLD, 28);
        Dimension buttonSize = new Dimension(250, 80);

        for (JButton btn : buttons) {
            btn.setFont(buttonFont);
            btn.setPreferredSize(buttonSize);
            btn.setBackground(boxColor);
            btn.setForeground(whiteText);
            btn.setFocusPainted(false);
            addHoverEffect(btn, boxColor, boxColor.brighter());
            buttonPanel.add(btn);
        }

        background.add(buttonPanel, BorderLayout.SOUTH);

        // Actions
        loginBtn.addActionListener(e -> loginUser());
        backBtn.addActionListener(e -> {
            new Homepage().setVisible(true);
            dispose();
        });
    }

    // LOGIN 
    private void loginUser() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both email and password!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File file = new File("users.txt");
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "No registered users found. Please register first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String role = parts[0];
                    String name = parts[1];
                    String fileEmail = parts[2];
                    String filePassword = parts[3];
                                      
                        if (email.equalsIgnoreCase(fileEmail) && password.equals(filePassword)) {
                        found = true;
                        
                        JLabel messageLabel = new JLabel();
                        messageLabel.setFont(new Font("Serif", Font.BOLD, 18));

                       // Green checkmark 
                        messageLabel.setText("<html><span style='color:#006400;'>✔</span> <span style='color:#000000;'>Login Successful as " + role + ".</span></html>");

                        JOptionPane.showMessageDialog(
                            this,
                            messageLabel,
                     "Success",
                            JOptionPane.PLAIN_MESSAGE
                     );
                        openDashboard(role, name, email);
                        dispose();
                        break;
                    }
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading user data!", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "Invalid email or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    // OPEN DASHBOARD BY ROLE 
    private void openDashboard(String role, String name, String email) {
        switch (role.toLowerCase()) {
            case "admin":
                new AdminDashboard(name, email).setVisible(true);
                break;
            case "tutor":
                new TutorDashboard(name, email).setVisible(true);
                break;
            case "student":
                new StudentDashboard(name, email).setVisible(true);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Unknown role: " + role, "Error", JOptionPane.ERROR_MESSAGE);
                new Homepage().setVisible(true);
                break;
        }
    }

    // Hover effect for buttons
    private void addHoverEffect(JButton button, Color base, Color hover) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { button.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent evt) { button.setBackground(base); }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}
