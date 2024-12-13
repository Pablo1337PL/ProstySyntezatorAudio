import javax.swing.*;
import java.awt.*;

public class EnvelopeVisualizer extends JPanel {

    private double[] shape = new double[0];
    private Color lineColor = Color.ORANGE;

    public EnvelopeVisualizer() {
        setBackground(Color.BLACK);
    }

    public void updateShape(double[] newShape) {
        this.shape = newShape.clone();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (shape.length == 0) return;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(lineColor);

        int width = getWidth();
        int height = getHeight();
        int midY = height;

        int[] xPoints = new int[shape.length];
        int[] yPoints = new int[shape.length];

        for (int i=0; i<shape.length; i++) {
            xPoints[i] = (int)((double)i / (shape.length - 1) * width);
            // amplituda od 0 do 1, rysujemy od dołu do góry
            yPoints[i] = (int)(height - shape[i] * height*0.9);
        }

        for (int i = 1; i < xPoints.length; i++) {
            g2d.drawLine(xPoints[i-1], yPoints[i-1], xPoints[i], yPoints[i]);
        }
    }
}
