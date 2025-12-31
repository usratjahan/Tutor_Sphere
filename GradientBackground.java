import javax.swing.*;
import java.awt.*;

public class GradientBackground extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int width = getWidth();
        int height = getHeight();

        Color topColor = new Color(0x708993);
        Color middleColor = new Color(0xA1C2BD);
        Color bottomColor = new Color(0xE7F2EF);

        GradientPaint gp1 = new GradientPaint(0, 0, topColor, 0, height / 3, middleColor);
        GradientPaint gp2 = new GradientPaint(0, height / 3, middleColor, 0, height, bottomColor);

        g2.setPaint(gp1);
        g2.fillRect(0, 0, width, height / 2);

        g2.setPaint(gp2);
        g2.fillRect(0, height / 3, width, height);
    }
}

