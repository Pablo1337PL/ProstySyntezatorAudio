import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.*;

public class SoundGenerator {
    private Synthesizer synth;
    private SineOscillator sineOscillator;
    private SawtoothOscillator sawOscillator;
    private SquareOscillator squareOscillator;
    private TriangleOscillator triangleOscillator;
    private FilterLowPass lowPassFilter;
    private FilterHighPass highPassFilter;
    private LineOut lineOut;

    private boolean sineEnabled = false;
    private boolean sawEnabled = false;
    private boolean squareEnabled = false;
    private boolean triangleEnabled = false;

    private double baseFrequency = 440.0; // A4
    private int sineOctaveOffset = 0;
    private int sawOctaveOffset = 0;
    private int squareOctaveOffset = 0;
    private int triangleOctaveOffset = 0;

    public SoundGenerator() {
        synth = JSyn.createSynthesizer();

        // Tworzenie oscylatorów i filtrów
        sineOscillator = new SineOscillator();
        sawOscillator = new SawtoothOscillator();
        squareOscillator = new SquareOscillator();
        triangleOscillator = new TriangleOscillator();
        lowPassFilter = new FilterLowPass();
        highPassFilter = new FilterHighPass();
        lineOut = new LineOut();

        // Dodawanie komponentów do syntezatora
        synth.add(sineOscillator);
        synth.add(sawOscillator);
        synth.add(squareOscillator);
        synth.add(triangleOscillator);
        synth.add(lowPassFilter);
        synth.add(highPassFilter);
        synth.add(lineOut);

        // Połączenie oscylatorów i filtrów
        sineOscillator.output.connect(lowPassFilter.input);
        sawOscillator.output.connect(lowPassFilter.input);
        squareOscillator.output.connect(lowPassFilter.input);
        triangleOscillator.output.connect(lowPassFilter.input);
        lowPassFilter.output.connect(highPassFilter.input);
        highPassFilter.output.connect(lineOut.input);

        // Domyślne ustawienia
        sineOscillator.amplitude.set(0.0);
        sawOscillator.amplitude.set(0.0);
        squareOscillator.amplitude.set(0.0);
        triangleOscillator.amplitude.set(0.0);
        lowPassFilter.frequency.set(5000.0); // Domyślne odcięcie LPF
        highPassFilter.frequency.set(20.0);  // Domyślne odcięcie HPF

        synth.start();
        lineOut.start();
    }

    public void setSineEnabled(boolean enabled) {
        sineEnabled = enabled;
    }

    public void setSawEnabled(boolean enabled) {
        sawEnabled = enabled;
    }

    public void setSquareEnabled(boolean enabled) {
        squareEnabled = enabled;
    }

    public void setTriangleEnabled(boolean enabled) {
        triangleEnabled = enabled;
    }

    public void setSineOctave(int offset) {
        sineOctaveOffset = offset;
        sineOscillator.frequency.set(baseFrequency * Math.pow(2, sineOctaveOffset));
    }

    public void setSawOctave(int offset) {
        sawOctaveOffset = offset;
        sawOscillator.frequency.set(baseFrequency * Math.pow(2, sawOctaveOffset));
    }

    public void setSquareOctave(int offset) {
        squareOctaveOffset = offset;
        squareOscillator.frequency.set(baseFrequency * Math.pow(2, squareOctaveOffset));
    }

    public void setTriangleOctave(int offset) {
        triangleOctaveOffset = offset;
        triangleOscillator.frequency.set(baseFrequency * Math.pow(2, triangleOctaveOffset));
    }

    public void setLowPassCutoff(double cutoff) {
        lowPassFilter.frequency.set(cutoff);
    }

    public void setHighPassCutoff(double cutoff) {
        highPassFilter.frequency.set(cutoff);
    }

    public void startSound() {
        if (sineEnabled) {
            sineOscillator.amplitude.set(0.5);
        }
        if (sawEnabled) {
            sawOscillator.amplitude.set(0.5);
        }
        if (squareEnabled) {
            squareOscillator.amplitude.set(0.5);
        }
        if (triangleEnabled) {
            triangleOscillator.amplitude.set(0.5);
        }
    }

    public void stopSound() {
        sineOscillator.amplitude.set(0.0);
        sawOscillator.amplitude.set(0.0);
        squareOscillator.amplitude.set(0.0);
        triangleOscillator.amplitude.set(0.0);
    }

































    public double[] getCurrentSamples(int sampleCount) {
        double[] samples = new double[sampleCount];
        double sineFreq = sineEnabled ? sineOscillator.frequency.get() : 0.0;
        double sawFreq = sawEnabled ? sawOscillator.frequency.get() : 0.0;
        double squareFreq = squareEnabled ? squareOscillator.frequency.get() : 0.0;
        double triangleFreq = triangleEnabled ? triangleOscillator.frequency.get() : 0.0;

        // Filtry LPF i HPF - przechowywanie poprzednich wartości
        double[] lpfState = {0.0}; // Stan filtra LPF
        double[] hpfState = {0.0, 0.0}; // Stan filtra HPF

        double sampleRate = 44100.0;

        for (int i = 0; i < sampleCount; i++) {
            double sineWave = sineEnabled ? Math.sin(2 * Math.PI * sineFreq * i / sampleRate) : 0.0;
            double sawWave = sawEnabled ? 2.0 * (i * sawFreq / sampleRate % 1.0) - 1.0 : 0.0;
            double squareWave = squareEnabled ? (Math.sin(2 * Math.PI * squareFreq * i / sampleRate) >= 0 ? 1.0 : -1.0) : 0.0;
            double triangleWave = triangleEnabled ? Math.abs((2.0 * (i * triangleFreq / sampleRate % 1.0)) - 1.0) : 0.0;

            // Sumowanie fal
            double sampleValue = (sineWave + sawWave + squareWave + triangleWave) / 4.0;

            // Zastosowanie filtra LPF
            sampleValue = applyLowPassFilter(sampleValue, lpfState, lowPassFilter.frequency.get());

            // Zastosowanie filtra HPF
            sampleValue = applyHighPassFilter(sampleValue, hpfState, highPassFilter.frequency.get());

            // Zapisanie przetworzonej próbki
            samples[i] = sampleValue;
        }

        return samples;
    }

    private double applyLowPassFilter(double input, double[] state, double cutoffFreq) {
        double rc = 1.0 / (cutoffFreq * 2 * Math.PI);
        double dt = 1.0 / 44100.0; // Stała częstotliwość próbkowania
        double alpha = dt / (rc + dt);

        state[0] = state[0] + alpha * (input - state[0]);
        return state[0];
    }

    private double applyHighPassFilter(double input, double[] state, double cutoffFreq) {
        double rc = 1.0 / (cutoffFreq * 2 * Math.PI);
        double dt = 1.0 / 44100.0; // Stała częstotliwość próbkowania
        double alpha = rc / (rc + dt);

        double filtered = alpha * (state[1] + input - state[0]);
        state[0] = input;
        state[1] = filtered;
        return filtered;
    }

}
