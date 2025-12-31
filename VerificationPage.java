import javax.swing.*;
import java.awt.*;
import java.io.*;

public class VerificationPage extends JFrame {

    private JTextField nameField, emailField, filePathField;
    private JButton chooseFileButton, uploadButton, backButton;
    private String name, email;

    private File dataFile(String filename) {
        return new File(System.getProperty("user.dir"), filename);
    }

    public VerificationPage(String name, String email) {
        this.name = name;
        this.email = email;

        setTitle("TutorSphere - Tutor Verification");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        GradientBackground background = new GradientBackground();
        background.setLayout(new BorderLayout());
        setContentPane(background);

        // Colors
        Color whiteText = Color.WHITE;
        Color boxColor = new Color(0x092635);
        Color blackText = Color.BLACK;

        // Title
        JLabel title = new JLabel("Tutor Verification - Upload HSC Certificate", JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 42));
        title.setForeground(whiteText);
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
        background.add(title, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 10, 20, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Serif", Font.PLAIN, 24);
        Font fieldFont = new Font("Serif", Font.PLAIN, 20);

        int row = 0;

        // NAME FIELD
        addField(formPanel, gbc, "Name:", labelFont, fieldFont,
                nameField = new JTextField(name), row++);
        nameField.setEditable(false);

        // EMAIL FIELD
        addField(formPanel, gbc, "Email:", labelFont, fieldFont,
                emailField = new JTextField(email), row++);
        emailField.setEditable(false);

        // FILE INPUT
        JLabel fileLabel = new JLabel("HSC Certificate:");
        fileLabel.setFont(labelFont);
        fileLabel.setForeground(blackText);

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(fileLabel, gbc);

        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filePanel.setOpaque(false);

        filePathField = new JTextField();
        filePathField.setPreferredSize(new Dimension(300, 35));
        filePathField.setFont(fieldFont);
        filePathField.setEditable(false);

        chooseFileButton = new JButton("Choose File");
        chooseFileButton.setFont(new Font("Serif", Font.BOLD, 18));
        chooseFileButton.setBackground(boxColor);
        chooseFileButton.setForeground(whiteText);
        chooseFileButton.setFocusPainted(false);
        addHoverEffect(chooseFileButton, boxColor, boxColor.brighter());

        chooseFileButton.addActionListener(e -> chooseFile());

        filePanel.add(filePathField);
        filePanel.add(chooseFileButton);

        gbc.gridx = 1;
        formPanel.add(filePanel, gbc);
        row++;

        background.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        uploadButton = new JButton("Upload");
        backButton = new JButton("Back");

        JButton[] buttons = {uploadButton, backButton};
        Font buttonFont = new Font("Serif", Font.BOLD, 24);
        Dimension buttonSize = new Dimension(200, 60);

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

        // Upload logic
        uploadButton.addActionListener(e -> uploadCertificate());

        // Back logic
        backButton.addActionListener(e -> {
            new TutorDashboard(name, email).setVisible(true);
            dispose();
        });
    }

    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            filePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void uploadCertificate() {
        String filePath = filePathField.getText().trim();

        if (filePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please choose a file!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File verifyFile = dataFile("verifications.txt");

        // Prevent duplicate submissions
        if (isAlreadySubmitted()) {
            JOptionPane.showMessageDialog(this,
                    "You already submitted your certificate.\nWait for admin approval.",
                    "Duplicate Submission",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(verifyFile, true))) {
            writer.write("Tutor," + name + "," + email + "," + filePath + ",Pending");
            writer.newLine();

            JOptionPane.showMessageDialog(this,
                    " ✔️ Certificate uploaded successfully!\nStatus: Pending Admin Approval.",
                    "Success",
                    JOptionPane.PLAIN_MESSAGE);

            new TutorDashboard(name, email).setVisible(true);
            dispose();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error saving verification data!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private boolean isAlreadySubmitted() {
        File file = dataFile("verifications.txt");
        if (!file.exists()) return false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length >= 5 && parts[2].trim().equalsIgnoreCase(email)) {

                    String status = parts[4].trim();

                    // rejected can resubmit
                    return !status.equalsIgnoreCase("Rejected"); 
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // UI helper
    private void addField(JPanel panel, GridBagConstraints gbc, String labelText,
                          Font labelFont, Font fieldFont, JTextField field, int row) {

        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        label.setForeground(Color.BLACK);

        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(label, gbc);

        field.setFont(fieldFont);
        field.setPreferredSize(new Dimension(300, 35));

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    // Hover
    private void addHoverEffect(JButton button, Color base, Color hover) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { button.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent evt) { button.setBackground(base); }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VerificationPage("person", "person@gmail.com").setVisible(true));
    }
}


