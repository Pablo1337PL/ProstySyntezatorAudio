
import com.dreamfabric.DKnob;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class SynthApp {

    private SoundGenerator soundGenerator;
    private WaveVisualizer waveVisualizer;



    //do wczytywania i zapisu rzeczy poniewaz jest to spektakularnie wyjątkowo upierdliwe
    JCheckBox sineCheckbox;
    JSpinner sineSpinner;

    JCheckBox sawCheckbox;
    JSpinner sawSpinner;

    JCheckBox squareCheckbox;
    JSpinner squareSpinner;

    JCheckBox triangleCheckbox;
    JSpinner triangleSpinner;

    DKnob lowPassKnob;
    DKnob highPassKnob;

    EnvelopePanel envelopePanel;


    private Map<Integer, Double> keyToFrequencyMap = new HashMap<>();
    private boolean isKeyPressed = false;

    public SynthApp() {
        soundGenerator = new SoundGenerator();
        waveVisualizer = new WaveVisualizer();
        
        //stare mapowanie
//        keyToFrequencyMap.put(KeyEvent.VK_A, 440.00);
//        keyToFrequencyMap.put(KeyEvent.VK_W, 466.16);
//        keyToFrequencyMap.put(KeyEvent.VK_S, 493.88);
//        keyToFrequencyMap.put(KeyEvent.VK_D, 523.25);
//        keyToFrequencyMap.put(KeyEvent.VK_R, 554.37);
//        keyToFrequencyMap.put(KeyEvent.VK_F, 587.33);
//        keyToFrequencyMap.put(KeyEvent.VK_T, 622.25);
//        keyToFrequencyMap.put(KeyEvent.VK_G, 659.25);
//        keyToFrequencyMap.put(KeyEvent.VK_H, 698.46);
//        keyToFrequencyMap.put(KeyEvent.VK_U, 739.99);
//        keyToFrequencyMap.put(KeyEvent.VK_J, 783.99);
//        keyToFrequencyMap.put(KeyEvent.VK_I, 830.61);
//        keyToFrequencyMap.put(KeyEvent.VK_K, 880.00);
        
        keyToFrequencyMap.put(KeyEvent.VK_A, 440.00); //KeyEvent.VK_A 0
        keyToFrequencyMap.put(KeyEvent.VK_W, 466.16); //KeyEvent.VK_W 1
        keyToFrequencyMap.put(KeyEvent.VK_S, 493.88); //KeyEvent.VK_S 2
        keyToFrequencyMap.put(KeyEvent.VK_E, 523.25); //KeyEvent.VK_D 3
        keyToFrequencyMap.put(KeyEvent.VK_D, 554.37); //KeyEvent.VK_R 4
        keyToFrequencyMap.put(KeyEvent.VK_F, 587.33); //KeyEvent.VK_F 5
        keyToFrequencyMap.put(KeyEvent.VK_T, 622.25); //KeyEvent.VK_T 6
        keyToFrequencyMap.put(KeyEvent.VK_G, 659.25); //KeyEvent.VK_G 7
        keyToFrequencyMap.put(KeyEvent.VK_Y, 698.46); //KeyEvent.VK_H 8
        keyToFrequencyMap.put(KeyEvent.VK_H, 739.99); //KeyEvent.VK_U 9
        keyToFrequencyMap.put(KeyEvent.VK_U, 783.99); //KeyEvent.VK_J 10
        keyToFrequencyMap.put(KeyEvent.VK_J, 830.61); //KeyEvent.VK_I 11
        keyToFrequencyMap.put(KeyEvent.VK_K, 880.00); //KeyEvent.VK_I .12
        
    }

    public void run() {

        JFrame frame = new JFrame("Synthesizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 500);
        frame.setLayout(new BorderLayout());

        JPanel oscPanel = createOscPanel(frame);
        JPanel filterPanel = createFilterPanel(frame);
        JPanel visualizationPanel = createVisualizationPanel();
        frame.setJMenuBar(createMenuBar());

        // Tworzymy panel wschodni i umieszczamy w nim ADSR u góry, FX na dole
        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new BorderLayout());

        envelopePanel = new EnvelopePanel(soundGenerator.getEnvelopeGenerator(), frame);

        JPanel fxPanel = createFXPanel(frame);

        eastPanel.add(envelopePanel, BorderLayout.CENTER);
        eastPanel.add(filterPanel, BorderLayout.SOUTH);

        frame.add(oscPanel, BorderLayout.WEST);

        frame.add(visualizationPanel, BorderLayout.CENTER);
        frame.add(eastPanel, BorderLayout.EAST);
        //frame.add(fxPanel, BorderLayout.SOUTH);
        
        PianoPanel pianoPanel = new PianoPanel();
        frame.add(pianoPanel, BorderLayout.SOUTH);



        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Teraz zawsze pobieramy próbki, aby envelope mogło się zaktualizować
                double[] samples = soundGenerator.getCurrentSamples(300);

                if (isKeyPressed) {
                    waveVisualizer.updateWave(samples, true);
                } else {
                    waveVisualizer.updateWave(samples, false);
                }
            }
        }, 0, 50);

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (keyToFrequencyMap.containsKey(e.getKeyCode())) {
                    if (!isKeyPressed) {
                        double freq = keyToFrequencyMap.get(e.getKeyCode());
                        soundGenerator.setBaseFrequency(freq);
                        soundGenerator.startSound();
                        isKeyPressed = true;
                    }
                }
                
                pianoPanel.pressKey(e.getKeyCode());
                //panel pianina
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (keyToFrequencyMap.containsKey(e.getKeyCode())) {
                    soundGenerator.stopSound();
                    isKeyPressed = false;
                }
                pianoPanel.releaseKey(e.getKeyCode());
                //panel pianina

            }
        });
        
        //added mouse Listener, gets the key code from the piano class
        //not working atm
        frame.addMouseListener(new MouseAdapter() {
            @Override
			public void mousePressed(MouseEvent e) {
            	int keyCode = pianoPanel.mouseClick(e.getPoint());
				if (keyToFrequencyMap.containsKey(keyCode)) {
					if (!isKeyPressed) {
						double freq = keyToFrequencyMap.get(keyCode);
						soundGenerator.setBaseFrequency(freq);
						soundGenerator.startSound();
						isKeyPressed = true;
					}
				}
			}
            
            @Override
			public void mouseReleased(MouseEvent e) {
            	int keyCode = pianoPanel.mouseReleased(e.getPoint());
				if (keyToFrequencyMap.containsKey(keyCode)) {
					soundGenerator.stopSound();
					isKeyPressed = false;
				}
			}
        });
        

        frame.setFocusable(true);
        frame.requestFocusInWindow();
        frame.setVisible(true);
    }

    private JPanel createOscillatorControl(String label, JCheckBox checkBox, JSpinner spinner, ActionListener checkboxListener, ChangeListener spinnerListener, JFrame frame) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Ustawienia dla checkbox
        checkBox.setText(label);
        checkBox.addActionListener(e -> {
            checkboxListener.actionPerformed(e);
            frame.requestFocusInWindow();
        });

        // Ustawienia dla spinnera
        spinner.setPreferredSize(new Dimension(50, 20));
        spinner.addChangeListener(e -> {
            spinnerListener.stateChanged(e);
            frame.requestFocusInWindow();
        });

        panel.add(checkBox);
        panel.add(spinner);
        return panel;
    }


    private JPanel createOscPanel(JFrame frame) {
        JPanel oscPanel = new JPanel();
        oscPanel.setLayout(new GridLayout(4, 1, 10, 10));
        oscPanel.setBorder(BorderFactory.createTitledBorder("OSC"));

        // Tworzenie checkboxów i spinnerów dla każdego oscylatora
        sineCheckbox = new JCheckBox();
        sineSpinner = new JSpinner(new SpinnerNumberModel(0, -2, 2, 1));
        oscPanel.add(createOscillatorControl("Sine", sineCheckbox, sineSpinner,
                e -> soundGenerator.setSineEnabled(sineCheckbox.isSelected()),
                e -> soundGenerator.setSineOctave((int) sineSpinner.getValue()),
                frame));

        sawCheckbox = new JCheckBox();
        sawSpinner = new JSpinner(new SpinnerNumberModel(0, -2, 2, 1));
        oscPanel.add(createOscillatorControl("Sawtooth", sawCheckbox, sawSpinner,
                e -> soundGenerator.setSawEnabled(sawCheckbox.isSelected()),
                e -> soundGenerator.setSawOctave((int) sawSpinner.getValue()),
                frame));

        squareCheckbox = new JCheckBox();
        squareSpinner = new JSpinner(new SpinnerNumberModel(0, -2, 2, 1));
        oscPanel.add(createOscillatorControl("Square", squareCheckbox, squareSpinner,
                e -> soundGenerator.setSquareEnabled(squareCheckbox.isSelected()),
                e -> soundGenerator.setSquareOctave((int) squareSpinner.getValue()),
                frame));

        triangleCheckbox = new JCheckBox();
        triangleSpinner = new JSpinner(new SpinnerNumberModel(0, -2, 2, 1));
        oscPanel.add(createOscillatorControl("Triangle", triangleCheckbox, triangleSpinner,
                e -> soundGenerator.setTriangleEnabled(triangleCheckbox.isSelected()),
                e -> soundGenerator.setTriangleOctave((int) triangleSpinner.getValue()),
                frame));

        // Ustawienie nieedytowalnych pól w spinnerach
        for (Component component : oscPanel.getComponents()) {
            if (component instanceof JPanel panel) {
                for (Component subComponent : panel.getComponents()) {
                    if (subComponent instanceof JSpinner spinner) {
                        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
                        editor.getTextField().setEditable(false);
                    }
                }
            }
        }

        return oscPanel;
    }


    private JPanel createFilterPanel(JFrame frame) {
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new GridLayout(2, 1, 10, 10));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filters"));

        // Low-Pass Knob
        JLabel lpfLabel = new JLabel("Low-Pass:");
        lowPassKnob = new DKnob();
        lowPassKnob.setValue(1.0f);
        lowPassKnob.addChangeListener(e -> {
            double cutoff = 20 + (lowPassKnob.getValue() * 3980); // Map 0-1 to 20-4000 Hz
            soundGenerator.setLowPassCutoff(cutoff);
            frame.requestFocusInWindow();
        });

        JPanel lpfPanel = new JPanel(new BorderLayout());
        lpfPanel.add(lpfLabel, BorderLayout.NORTH);
        lpfPanel.add(lowPassKnob, BorderLayout.CENTER);

        // High-Pass Knob
        JLabel hpfLabel = new JLabel("High-Pass:");
        highPassKnob = new DKnob();
        highPassKnob.addChangeListener(e -> {
            double cutoff = 20 + (highPassKnob.getValue() * 3980); // Map 0-1 to 20-4000 Hz
            soundGenerator.setHighPassCutoff(cutoff);
            frame.requestFocusInWindow();
        });

        JPanel hpfPanel = new JPanel(new BorderLayout());
        hpfPanel.add(hpfLabel, BorderLayout.NORTH);
        hpfPanel.add(highPassKnob, BorderLayout.CENTER);

        filterPanel.add(lpfPanel);
        filterPanel.add(hpfPanel);

        return filterPanel;
    }


    private JPanel createVisualizationPanel() {
        JPanel visualizationPanel = new JPanel(new BorderLayout());
        visualizationPanel.setPreferredSize(new Dimension(300, 150));
        visualizationPanel.setBorder(BorderFactory.createTitledBorder("Waveform"));
        visualizationPanel.add(waveVisualizer, BorderLayout.CENTER);
        return visualizationPanel;
    }

    private JPanel createFXPanel(JFrame frame) {
        JPanel fxPanel = new JPanel();
        fxPanel.setPreferredSize(new Dimension(100, 150));
        fxPanel.setBorder(BorderFactory.createTitledBorder("FX"));
        fxPanel.setBackground(Color.LIGHT_GRAY);

        // Możesz tu dodać przyciski do efektów
        return fxPanel;
    }





    //stary panel zapisu
//    private JMenuBar createMenuBar() {
//        JMenuBar menuBar = new JMenuBar();
//        JMenu fileMenu = new JMenu("File");
//
//        JMenuItem saveItem = new JMenuItem("Save Preset");
//        saveItem.addActionListener(e -> savePreset());
//
//        JMenuItem loadItem = new JMenuItem("Load Preset");
//        loadItem.addActionListener(e -> loadPreset());
//
//        JMenuItem randomizeItem = new JMenuItem("Randomize Preset");
//        randomizeItem.addActionListener(e -> randomizePreset());
//
//        fileMenu.add(saveItem);
//        fileMenu.add(loadItem);
//        fileMenu.add(randomizeItem);
//
//        menuBar.add(fileMenu);
//        return menuBar;
//    }
    
/////////////////////////////////////////////////////////////////////////////////////////

    private ArrayList<File> presetFiles = new ArrayList<>();
    private int currentPresetIndex = -1;
    private JLabel presetNameLabel;
    private JPanel presetPanel;
    
	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");

		JMenuItem saveItem = new JMenuItem("Save Preset");
		saveItem.addActionListener(e -> savePreset());

		JMenuItem loadItem = new JMenuItem("Load Preset");
		loadItem.addActionListener(e -> loadPreset());

		JMenuItem randomizeItem = new JMenuItem("Randomize Preset");
		randomizeItem.addActionListener(e -> randomizePreset());

		fileMenu.add(saveItem);
		fileMenu.add(loadItem);
		fileMenu.add(randomizeItem);

		menuBar.add(fileMenu);

		// Presets panel (added to menu bar)
		presetPanel = new JPanel();
	    presetPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	    
	    updatePresetList(); //daje presety z folderu presets do listy
	    
	    
	    
	    JButton prevButton = new JButton("Prev");
	    prevButton.addActionListener(e -> loadPreviousPreset());
	    presetPanel.add(prevButton);
	    
	    JButton nextButton = new JButton("Next");
	    nextButton.addActionListener(e -> loadNextPreset());
	    presetPanel.add(nextButton);

	    // Preset name label
	    presetNameLabel = new JLabel("No preset selected"); // Default text
	    presetPanel.add(presetNameLabel);

	    // Add click listener to the panel
	    presetPanel.addMouseListener(new java.awt.event.MouseAdapter() {
	        @Override
	        public void mouseClicked(java.awt.event.MouseEvent e) {
	            reloadCurrentPreset(); // Reload the displayed preset when the panel is clicked
	        }
	    });

	    menuBar.add(presetPanel);

	    return menuBar;
	}

    private void loadNextPreset() {
        if (presetFiles.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No presets available");
            return;
        }
        currentPresetIndex = (currentPresetIndex + 1) % presetFiles.size();
        updatePresetLabel();
        loadPresetFromFile(presetFiles.get(currentPresetIndex));
    }

    private void loadPreviousPreset() {
        if (presetFiles.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No presets available");
            return;
        }
        currentPresetIndex = (currentPresetIndex - 1 + presetFiles.size()) % presetFiles.size();
        updatePresetLabel();
        loadPresetFromFile(presetFiles.get(currentPresetIndex));
    }

    private void updatePresetLabel() {
        if (currentPresetIndex >= 0 && currentPresetIndex < presetFiles.size()) {
            String presetName = presetFiles.get(currentPresetIndex).getName();
            SwingUtilities.invokeLater(() -> presetNameLabel.setText(presetName));
        }
    }
    
    private void reloadCurrentPreset() {
        if (currentPresetIndex >= 0 && currentPresetIndex < presetFiles.size()) {
            File currentPresetFile = presetFiles.get(currentPresetIndex);
            loadPresetFromFile(currentPresetFile); // Reload preset
//            JOptionPane.showMessageDialog(
//                null,
//                "Preset reloaded: " + currentPresetFile.getName(),
//                "Preset Reloaded",
//                JOptionPane.INFORMATION_MESSAGE
//            );
        } else {
            JOptionPane.showMessageDialog(
                null,
                "No preset selected to reload.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Dynamic preset loading
    private void loadPresetFromFile(File presetFile) {
        try {
            SynthSettings settings = PresetManager.loadSettings(presetFile);
            settings.applyTo(soundGenerator, this);
//            JOptionPane.showMessageDialog(null, "Preset loaded: " + presetFile.getName());
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Error loading preset: " + ex.getMessage());
        }
    }

    private void updatePresetList() {
        File presetsDir = new File("./presets"); // Directory containing presets
        presetFiles.clear(); // Clear the existing list

        if (presetsDir.exists() && presetsDir.isDirectory()) {
            File[] files = presetsDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) { // Only add files (ignore subdirectories)
                        presetFiles.add(file);
                    }
                }
            }
        } else {
            System.out.println("Presets directory not found or is not a directory!");
        }

        // Reset current preset index if needed
        if (presetFiles.isEmpty()) {
            currentPresetIndex = -1;
        } else if (currentPresetIndex >= presetFiles.size()) {
            currentPresetIndex = 0; // Reset to the first preset if the index is invalid
        }
    }


    
/////////////////////////////////////////////////////////////////////////////////////////



    //serializacja
    private void savePreset() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Preset");
        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                SynthSettings settings = new SynthSettings(soundGenerator);
                PresetManager.saveSettings(settings, file);
                JOptionPane.showMessageDialog(null, "Preset saved successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error saving preset: " + ex.getMessage());
            }
        }
        
        //nowe
        updatePresetList();
    }
    private void loadPreset() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Preset");
        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                SynthSettings settings = PresetManager.loadSettings(file);
                settings.applyTo(soundGenerator, this);
                JOptionPane.showMessageDialog(null, "Preset loaded successfully!");
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "Error loading preset: " + ex.getMessage());
            }
        }
    }

    //opcja randomowy preset
    private void randomizePreset() {
        // Wszystkie oscylatory i odpowiadające im checkboxy
        JCheckBox[] oscillators = {sineCheckbox, sawCheckbox, squareCheckbox, triangleCheckbox};
        JSpinner[] octaveSpinners = {sineSpinner, sawSpinner, squareSpinner, triangleSpinner};

        // Resetuj wszystkie oscylatory na wyłączone
        for (JCheckBox checkbox : oscillators) {
            checkbox.setSelected(false);
        }

        // Losowanie dwóch unikalnych oscylatorów
        int firstOsc = (int) (Math.random() * oscillators.length);
        int secondOsc;
        do {
            secondOsc = (int) (Math.random() * oscillators.length);
        } while (secondOsc == firstOsc);

        // Aktywacja losowych oscylatorów
        oscillators[firstOsc].setSelected(true);
        oscillators[secondOsc].setSelected(true);

        // Ustawienie losowych wartości oktaw dla aktywnych oscylatorów
        octaveSpinners[firstOsc].setValue((int) (Math.random() * 5 - 2)); // Zakres od -2 do 2
        octaveSpinners[secondOsc].setValue((int) (Math.random() * 5 - 2));

        // Losowe wartości filtrów
        lowPassKnob.setValue((float) (Math.random())); // Zakres 20-5000
        highPassKnob.setValue((float) (Math.random()));

        // Losowe wartości ADSR
        if (envelopePanel != null) {
            envelopePanel.attackKnob.setValue((float) Math.random() /5);
            envelopePanel.decayKnob.setValue((float) Math.random() );
            envelopePanel.sustainKnob.setValue((float) (Math.random()));
            envelopePanel.releaseKnob.setValue((float) Math.random()/5 );
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SynthApp().run());
    }

}

