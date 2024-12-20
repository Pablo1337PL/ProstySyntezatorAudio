
import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.FilterHighPass;
import com.jsyn.unitgen.FilterLowPass;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.Multiply;
import com.jsyn.unitgen.SawtoothOscillator;
import com.jsyn.unitgen.SineOscillator;
import com.jsyn.unitgen.SquareOscillator;
import com.jsyn.unitgen.TriangleOscillator;


//damping miedzy nutami potrzeba zrobic, bo jak zmieniasz nute to zbyt gwaltownie to sie dzieje i slychac jest popping
//dodac jakies efekty do dzwieku, delay, reverb, chorus, flanger
//dodac mozliwosc zmiany amplitudy dla kazdego oscylatora (glosnosc)










public class SoundGenerator {


    private final Synthesizer synth;
    private final SineOscillator sineOscillator;
    private final SawtoothOscillator sawOscillator;
    private final SquareOscillator squareOscillator;
    private final TriangleOscillator triangleOscillator;
    private final FilterLowPass lowPassFilter;
    private final FilterHighPass highPassFilter;
    private final LineOut lineOut;
    private final Multiply ampUnit;

    private boolean sineEnabled = false;
    private boolean sawEnabled = false;
    private boolean squareEnabled = false;
    private boolean triangleEnabled = false;

    private double baseFrequency = 440.0;
    private int sineOctaveOffset = 0;
    private int sawOctaveOffset = 0;
    private int squareOctaveOffset = 0;
    private int triangleOctaveOffset = 0;

    EnvelopeGenerator envelope;
    private long currentSampleIndex = 0;
    private final double sampleRate = 44100.0;


    public SoundGenerator() {
        synth = JSyn.createSynthesizer();

        //test delayu








        sineOscillator = new SineOscillator();
        sawOscillator = new SawtoothOscillator();
        squareOscillator = new SquareOscillator();
        triangleOscillator = new TriangleOscillator();
        lowPassFilter = new FilterLowPass();
        highPassFilter = new FilterHighPass();
        lineOut = new LineOut();
        ampUnit = new Multiply();

        synth.add(sineOscillator);
        synth.add(sawOscillator);
        synth.add(squareOscillator);
        synth.add(triangleOscillator);
        synth.add(lowPassFilter);
        synth.add(highPassFilter);
        synth.add(ampUnit);
        synth.add(lineOut);

        // Łączenie
        sineOscillator.output.connect(lowPassFilter.input);
        sawOscillator.output.connect(lowPassFilter.input);
        squareOscillator.output.connect(lowPassFilter.input);
        triangleOscillator.output.connect(lowPassFilter.input);

        lowPassFilter.output.connect(highPassFilter.input);
        highPassFilter.output.connect(ampUnit.inputA);
        ampUnit.output.connect(lineOut.input);

        //oscylatory wyłączone na starcie
        sineOscillator.amplitude.set(0.0);
        sawOscillator.amplitude.set(0.0);
        squareOscillator.amplitude.set(0.0);
        triangleOscillator.amplitude.set(0.0);

        lowPassFilter.frequency.set(5000.0);
        highPassFilter.frequency.set(20.0);

        synth.start();
        lineOut.start();

        envelope = new EnvelopeGenerator();
        envelope.setSampleRate(sampleRate);
    }

    public void setSineEnabled(boolean enabled) {
        sineEnabled = enabled;
        sineOscillator.amplitude.set(enabled ? 0.4 : 0.0);
    }

    public void setSawEnabled(boolean enabled) {
        sawEnabled = enabled;
        sawOscillator.amplitude.set(enabled ? 0.4 : 0.0);
    }

    public void setSquareEnabled(boolean enabled) {
        squareEnabled = enabled;
        squareOscillator.amplitude.set(enabled ? 0.4 : 0.0);
    }

    public void setTriangleEnabled(boolean enabled) {
        triangleEnabled = enabled;
        triangleOscillator.amplitude.set(enabled ? 0.4 : 0.0);
    }

    public void setSineOctave(int offset) {
        sineOctaveOffset = offset;
        updateOscillatorFrequencies();
    }

    public void setSawOctave(int offset) {
        sawOctaveOffset = offset;
        updateOscillatorFrequencies();
    }

    public void setSquareOctave(int offset) {
        squareOctaveOffset = offset;
        updateOscillatorFrequencies();
    }

    public void setTriangleOctave(int offset) {
        triangleOctaveOffset = offset;
        updateOscillatorFrequencies();
    }

    public void setLowPassCutoff(double cutoff) {
        lowPassFilter.frequency.set(cutoff);
    }

    public void setHighPassCutoff(double cutoff) {
        highPassFilter.frequency.set(cutoff);
    }

    public void setBaseFrequency(double freq) {
        this.baseFrequency = freq;
        updateOscillatorFrequencies();
    }

    private void updateOscillatorFrequencies() {
        double sineFreq = baseFrequency * Math.pow(2, sineOctaveOffset);
        double sawFreq = baseFrequency * Math.pow(2, sawOctaveOffset);
        double sqFreq = baseFrequency * Math.pow(2, squareOctaveOffset);
        double triFreq = baseFrequency * Math.pow(2, triangleOctaveOffset);

        sineOscillator.frequency.set(sineFreq);
        sawOscillator.frequency.set(sawFreq);
        squareOscillator.frequency.set(sqFreq);
        triangleOscillator.frequency.set(triFreq);
    }

    public void startSound() {
        noteOn();
    }

    public void stopSound() {
        noteOff();
    }

    private void noteOn() {
        envelope.noteOn(currentSampleIndex);
    }

    private void noteOff() {
        envelope.noteOff(currentSampleIndex);
    }

    public double[] getCurrentSamples(int sampleCount) {
        double[] samples = new double[sampleCount];

        double[] lpfState = {0.0};
        double[] hpfState = {0.0, 0.0};

        // Pobieramy aktualną amplitudę z obwiedni
        double currentAmp = envelope.getAmplitude(currentSampleIndex);

        // Skalujemy obwiednię do max 0.7, aby uniknąć przesterowania
        double scaledAmp = currentAmp * 1;
        ampUnit.inputB.set(scaledAmp);

        // Dalej generujemy próbki do wizualizacji jak wcześniej
        double sineFreq = sineEnabled ? sineOscillator.frequency.get() : 0.0;
        double sawFreq = sawEnabled ? sawOscillator.frequency.get() : 0.0;
        double squareFreq = squareEnabled ? squareOscillator.frequency.get() : 0.0;
        double triangleFreq = triangleEnabled ? triangleOscillator.frequency.get() : 0.0;

        for (int i = 0; i < sampleCount; i++) {
            long samplePosition = currentSampleIndex + i;
            double sineWave = sineEnabled ? Math.sin(2 * Math.PI * sineFreq * samplePosition / sampleRate) : 0.0;

            double sawWave = 0.0;
            if (sawEnabled) {
                double phase = (samplePosition * sawFreq / sampleRate) % 1.0;
                sawWave = 2.0 * phase - 1.0;
            }

            double squareWave = 0.0;
            if (squareEnabled) {
                double val = Math.sin(2 * Math.PI * squareFreq * samplePosition / sampleRate);
                squareWave = val >= 0 ? 1.0 : -1.0;
            }

            double triangleWave = 0.0;
            if (triangleEnabled) {
                double phase = (samplePosition * triangleFreq / sampleRate) % 1.0;
                triangleWave = 2.0 * (phase < 0.5 ? phase : 1 - phase) - 1.0;
            }

            double sampleValue = (sineWave + sawWave + squareWave + triangleWave) / 4.0;

            sampleValue = applyLowPassFilter(sampleValue, lpfState, lowPassFilter.frequency.get());
            sampleValue = applyHighPassFilter(sampleValue, hpfState, highPassFilter.frequency.get());

            // Również w wizualizacji używamy skalowanej amplitudy
            double visAmp = envelope.getAmplitude(samplePosition) * 0.7;
            sampleValue *= visAmp;

            samples[i] = sampleValue;
        }

        currentSampleIndex += sampleCount;
        return samples;
    }


    private double applyLowPassFilter(double input, double[] state, double cutoffFreq) {
        double rc = 1.0 / (cutoffFreq * 2 * Math.PI);
        double dt = 1.0 / sampleRate;
        double alpha = dt / (rc + dt);

        state[0] = state[0] + alpha * (input - state[0]);
        return state[0];
    }

    private double applyHighPassFilter(double input, double[] state, double cutoffFreq) {
        double rc = 1.0 / (cutoffFreq * 2 * Math.PI);
        double dt = 1.0 / sampleRate;
        double alpha = rc / (rc + dt);

        double filtered = alpha * (state[1] + input - state[0]);
        state[0] = input;
        state[1] = filtered;
        return filtered;
    }

    public EnvelopeGenerator getEnvelopeGenerator() {
        return envelope;
    }


    //boolean array
    public boolean[] getOscillatorStates() {
        return new boolean[]{sineEnabled, sawEnabled, squareEnabled, triangleEnabled};
    }

    //octave settings array
    public int[] getOscillatorOctaveOffsets() {
        return new int[]{sineOctaveOffset, sawOctaveOffset, squareOctaveOffset, triangleOctaveOffset};
    }
    //filter settings
    public double[] getFilterCutoffs() {
        return new double[]{lowPassFilter.frequency.get(), highPassFilter.frequency.get()};
    }
}
