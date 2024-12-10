import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Timer;
import java.util.TimerTask;

public class SynthApp {

    private SoundGenerator soundGenerator;
    private boolean isHPressed = false;
    private WaveVisualizer waveVisualizer;

    public SynthApp() {
        soundGenerator = new SoundGenerator();
        waveVisualizer = new WaveVisualizer();
    }

    public void run() {
        JFrame frame = new JFrame("Synthesizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500); // Zmniejszone okno
        frame.setLayout(new BorderLayout());

        // Główne panele
        JPanel oscPanel = createOscPanel(frame);
        JPanel filterPanel = createFilterPanel(frame);
        JPanel visualizationPanel = createVisualizationPanel();
        JPanel fxPanel = createFXPanel();

        // Dodanie paneli do głównego okna
        frame.add(oscPanel, BorderLayout.WEST);
        frame.add(filterPanel, BorderLayout.SOUTH);
        frame.add(visualizationPanel, BorderLayout.CENTER);
        frame.add(fxPanel, BorderLayout.EAST);

        // Timer do aktualizacji wizualizacji
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isHPressed) {
                    double[] samples = soundGenerator.getCurrentSamples(200);
                    waveVisualizer.updateWave(samples, true);
                } else {
                    waveVisualizer.updateWave(new double[0], false);
                }
            }
        }, 0, 50);

        // Obsługa klawisza H
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_H && !isHPressed) {
                    isHPressed = true;
                    soundGenerator.startSound();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_H) {
                    isHPressed = false;
                    soundGenerator.stopSound();
                }
            }
        });

        frame.setFocusable(true);
        frame.requestFocusInWindow();
        frame.setVisible(true);
    }
    private JPanel createOscillatorControl(String label, ActionListener checkboxListener, ChangeListener spinnerListener, JFrame frame) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCheckBox checkBox = new JCheckBox(label);
        checkBox.addActionListener(e -> {
            checkboxListener.actionPerformed(e);
            frame.requestFocusInWindow(); // Wymuszenie fokusu
        });

        JSpinner octaveSpinner = new JSpinner(new SpinnerNumberModel(0, -2, 2, 1));
        octaveSpinner.setPreferredSize(new Dimension(50, 20));
        octaveSpinner.addChangeListener(e -> {
            spinnerListener.stateChanged(e);
            frame.requestFocusInWindow(); // Wymuszenie fokusu
        });

        panel.add(checkBox);
        panel.add(octaveSpinner);
        return panel;
    }
    private JPanel createOscPanel(JFrame frame) {
        JPanel oscPanel = new JPanel();
        oscPanel.setLayout(new GridLayout(4, 1, 10, 10));
        oscPanel.setBorder(BorderFactory.createTitledBorder("OSC"));

        oscPanel.add(createOscillatorControl("Sine",
                e -> soundGenerator.setSineEnabled(((JCheckBox) e.getSource()).isSelected()),
                e -> soundGenerator.setSineOctave((int) ((JSpinner) e.getSource()).getValue()),
                frame));

        oscPanel.add(createOscillatorControl("Sawtooth",
                e -> soundGenerator.setSawEnabled(((JCheckBox) e.getSource()).isSelected()),
                e -> soundGenerator.setSawOctave((int) ((JSpinner) e.getSource()).getValue()),
                frame));

        oscPanel.add(createOscillatorControl("Square",
                e -> soundGenerator.setSquareEnabled(((JCheckBox) e.getSource()).isSelected()),
                e -> soundGenerator.setSquareOctave((int) ((JSpinner) e.getSource()).getValue()),
                frame));

        oscPanel.add(createOscillatorControl("Triangle",
                e -> soundGenerator.setTriangleEnabled(((JCheckBox) e.getSource()).isSelected()),
                e -> soundGenerator.setTriangleOctave((int) ((JSpinner) e.getSource()).getValue()),
                frame));

        // Zmodyfikowanie spinnerów w każdym oscylatorze, aby nie były edytowalne
        for (Component component : oscPanel.getComponents()) {
            if (component instanceof JPanel panel) {
                for (Component subComponent : panel.getComponents()) {
                    if (subComponent instanceof JSpinner spinner) {
                        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
                        editor.getTextField().setEditable(false); // Wyłączanie edytowalności
                    }
                }
            }
        }

        return oscPanel;
    }


    private JPanel createFilterPanel(JFrame frame) {
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new GridLayout(2, 1, 10, 10)); // Zmieniono na prosty układ
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filters"));

        // LPF Suwak
        JLabel lpfLabel = new JLabel("Low-Pass:");
        JSlider lpfSlider = new JSlider(20, 5000, 5000);
        lpfSlider.addChangeListener(e -> {
            soundGenerator.setLowPassCutoff(lpfSlider.getValue());
            frame.requestFocusInWindow();
        });

        JPanel lpfPanel = new JPanel(new BorderLayout());
        lpfPanel.add(lpfLabel, BorderLayout.NORTH);
        lpfPanel.add(lpfSlider, BorderLayout.CENTER);

        // HPF Suwak
        JLabel hpfLabel = new JLabel("High-Pass:");
        JSlider hpfSlider = new JSlider(20, 5000, 20);
        hpfSlider.addChangeListener(e -> {
            soundGenerator.setHighPassCutoff(hpfSlider.getValue());
            frame.requestFocusInWindow();
        });

        JPanel hpfPanel = new JPanel(new BorderLayout());
        hpfPanel.add(hpfLabel, BorderLayout.NORTH);
        hpfPanel.add(hpfSlider, BorderLayout.CENTER);

        // Dodanie paneli do głównego panelu filtrów
        filterPanel.add(lpfPanel);
        filterPanel.add(hpfPanel);

        return filterPanel;
    }


    private JPanel createVisualizationPanel() {
        JPanel visualizationPanel = new JPanel(new BorderLayout());
        visualizationPanel.setPreferredSize(new Dimension(400, 150)); // Dopasowano rozmiar
        visualizationPanel.setBorder(BorderFactory.createTitledBorder("Waveform"));
        visualizationPanel.add(waveVisualizer, BorderLayout.CENTER);
        return visualizationPanel;
    }

    private JPanel createFXPanel() {
        JPanel fxPanel = new JPanel();
        fxPanel.setPreferredSize(new Dimension(100, 150)); // Dopasowano rozmiar
        fxPanel.setBorder(BorderFactory.createTitledBorder("FX"));
        fxPanel.setBackground(Color.LIGHT_GRAY);
        return fxPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SynthApp().run());
    }
}
