import javax.swing.*;
import java.awt.*;

public abstract class DashboardBase extends JFrame {

    protected String name;
    protected String email;
    protected GradientBackground background;
    protected Color whiteText = new Color(0xFF, 0xFF, 0xFF);
    protected Color boxColor = new Color(0x09, 0x26, 0x35);
    protected Color blackText = new Color(0x00, 0x00, 0x00);

    private boolean showFooter;  

    public DashboardBase(String title, String name, String email, boolean showFooter) {
        this.name = name;
        this.email = email;
        this.showFooter = showFooter;

        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        background = new GradientBackground();
        background.setLayout(new BorderLayout());
        setContentPane(background);

        setupHeader(title);

        // ONLY ADD FOOTER IF TRUE
        if (showFooter) {
            setupCommonButtons();
        }

        setupContent();
    }

    private void setupHeader(String titleText) {
        JLabel title = new JLabel(titleText, JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 50));
        title.setForeground(whiteText);
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        background.add(title, BorderLayout.NORTH);

        JLabel welcome = new JLabel("Welcome, " + name + " (" + email + ")", JLabel.CENTER);
        welcome.setFont(new Font("Serif", Font.PLAIN, 24));
        welcome.setForeground(blackText);
        background.add(welcome, BorderLayout.BEFORE_FIRST_LINE);
    }

    private void setupCommonButtons() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 60, 20));
        footer.setOpaque(false);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("Serif", Font.BOLD, 26));
        logoutBtn.setBackground(boxColor);
        logoutBtn.setForeground(whiteText);
        logoutBtn.setFocusPainted(false);
        addHoverEffect(logoutBtn, boxColor, boxColor.brighter());

        logoutBtn.addActionListener(e -> {
            new Homepage().setVisible(true);
            dispose();
        });

        footer.add(logoutBtn);
        background.add(footer, BorderLayout.SOUTH);
    }

    protected void addHoverEffect(JButton button, Color base, Color hover) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hover);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(base);
            }
        });
    }

    protected abstract void setupContent();
}

