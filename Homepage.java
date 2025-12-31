import javax.swing.*;
import java.awt.*;

public class Homepage extends JFrame {

    public Homepage() {
        setTitle("TutorSphere - Peer Tutoring Matchmaking Platform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Full screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;
        setSize(width, height);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Gradient background
        GradientBackground background = new GradientBackground();

        background.setLayout(new BorderLayout());
        setContentPane(background);

        // Colors
        Color whiteText = new Color(0xFF, 0xFF, 0xFF); 
        Color blackQuote = new Color(0x00, 0x00, 0x00); 

        // Title
        JLabel title = new JLabel("Welcome to TutorSphere", JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 50));
        title.setForeground(whiteText);
        title.setBorder(BorderFactory.createEmptyBorder(50, 0, 20, 0));
        background.add(title, BorderLayout.NORTH);

        // Quote
        JLabel quote = new JLabel(
            "<html><div style='text-align: center;'>“Education is the most powerful weapon which you can use to change the world.”<br>– Nelson Mandela</div></html>", 
            JLabel.CENTER
        );
        quote.setFont(new Font("Serif", Font.ITALIC, 30));
        quote.setForeground(blackQuote); 
        quote.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        background.add(quote, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridLayout(1, 3, 50, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JButton registerBtn = new JButton("Register");
        JButton loginBtn = new JButton("Login");
        JButton exitBtn = new JButton("Exit");

        // Button style
        Font buttonFont = new Font("Serif", Font.BOLD, 30);
        Dimension buttonSize = new Dimension(250, 80);
        JButton[] buttons = {registerBtn, loginBtn, exitBtn};
        Color boxColor = new Color(0x09, 0x26, 0x35); 

        for (JButton btn : buttons) {
            btn.setFont(buttonFont);
            btn.setPreferredSize(buttonSize);
            btn.setBackground(boxColor); 
            btn.setForeground(whiteText); 
            btn.setFocusPainted(false);
        }

        // Hover effect
        addHoverEffect(registerBtn, boxColor, boxColor.brighter());
        addHoverEffect(loginBtn, boxColor, boxColor.brighter());
        addHoverEffect(exitBtn, boxColor, boxColor.brighter());

        buttonPanel.add(registerBtn);
        buttonPanel.add(loginBtn);
        buttonPanel.add(exitBtn);
        background.add(buttonPanel, BorderLayout.SOUTH);

        // Button Actions
        registerBtn.addActionListener(e -> {
            RegisterPage registerPage = new RegisterPage();
            registerPage.setVisible(true);
            dispose();
        });

        loginBtn.addActionListener(e -> {
            LoginPage loginPage = new LoginPage();
            loginPage.setVisible(true);
            dispose();
        });

        exitBtn.addActionListener(e -> System.exit(0));
    }

    private void addHoverEffect(JButton button, Color base, Color hover) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hover);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(base);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Homepage().setVisible(true));
    }
}






