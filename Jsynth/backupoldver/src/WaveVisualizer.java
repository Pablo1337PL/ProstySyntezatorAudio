import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class WaveVisualizer extends JPanel {

    private double[] currentSamples; // Przechowywane próbki dźwięku
    private boolean isActive = false; // Flaga wskazująca, czy wizualizacja jest aktywna

    public WaveVisualizer() {
        setBackground(Color.BLACK);
        currentSamples = new double[0]; // Początkowo brak próbek
    }

    public void updateWave(double[] samples, boolean active) {
        this.currentSamples = Arrays.copyOf(samples, samples.length); // Kopiowanie próbek
        this.isActive = active;
        repaint(); // Odświeżanie panelu
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.GREEN);

        if (isActive && currentSamples.length > 0) {
            int width = getWidth();
            int height = getHeight();
            int midY = height / 2;

            // Skalowanie wartości próbek do wysokości panelu
            int[] xPoints = new int[currentSamples.length];
            int[] yPoints = new int[currentSamples.length];

            for (int i = 0; i < currentSamples.length; i++) {
                xPoints[i] = (int) ((double) i / currentSamples.length * width);
                yPoints[i] = midY - (int) (currentSamples[i] * midY); // Próbki skalowane do połowy wysokości
            }

            // Rysowanie fali
            for (int i = 1; i < xPoints.length; i++) {
                g2d.drawLine(xPoints[i - 1], yPoints[i - 1], xPoints[i], yPoints[i]);
            }
        }
    }
}
