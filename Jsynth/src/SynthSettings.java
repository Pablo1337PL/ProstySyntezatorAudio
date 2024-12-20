

import java.io.Serializable;

public class SynthSettings implements Serializable {
    private static final long serialVersionUID = 1L;

    // Parametry globalne
    public boolean sineEnabled;
    public boolean sawEnabled;
    public boolean squareEnabled;
    public boolean triangleEnabled;

    public int sineOctaveOffset;
    public int sawOctaveOffset;
    public int squareOctaveOffset;
    public int triangleOctaveOffset;

    public double lowPassCutoff;
    public double highPassCutoff;

    public double attackMs;
    public double decayMs;
    public double sustainLevel;
    public double releaseMs;

    // Konstruktor domyślny
    public SynthSettings() {}

    // Konstruktor kopiujący aktualne ustawienia z SoundGenerator
    public SynthSettings(SoundGenerator generator) {
        this.sineEnabled = generator.getOscillatorStates()[0];
        this.sawEnabled = generator.getOscillatorStates()[1];
        this.squareEnabled = generator.getOscillatorStates()[2];
        this.triangleEnabled = generator.getOscillatorStates()[3];

        this.sineOctaveOffset = generator.getOscillatorOctaveOffsets()[0];
        this.sawOctaveOffset = generator.getOscillatorOctaveOffsets()[1];
        this.squareOctaveOffset = generator.getOscillatorOctaveOffsets()[2];
        this.triangleOctaveOffset = generator.getOscillatorOctaveOffsets()[3];

        this.lowPassCutoff = generator.getFilterCutoffs()[0];
        this.highPassCutoff = generator.getFilterCutoffs()[1];

        this.attackMs = generator.envelope.getAttackTimeMs();
        this.decayMs = generator.envelope.getDecayTimeMs();
        this.sustainLevel = generator.envelope.getSustainLevel();
        this.releaseMs = generator.envelope.getReleaseTimeMs();
    }

    // Metoda przywracająca ustawienia do SoundGenerator
    public void applyTo(SoundGenerator generator, SynthApp app) {
        generator.setSineEnabled(this.sineEnabled);
        generator.setSawEnabled(this.sawEnabled);
        generator.setSquareEnabled(this.squareEnabled);
        generator.setTriangleEnabled(this.triangleEnabled);

        generator.setSineOctave(this.sineOctaveOffset);
        generator.setSawOctave(this.sawOctaveOffset);
        generator.setSquareOctave(this.squareOctaveOffset);
        generator.setTriangleOctave(this.triangleOctaveOffset);

        generator.setLowPassCutoff(this.lowPassCutoff);
        generator.setHighPassCutoff(this.highPassCutoff);

        generator.envelope.setAttack(this.attackMs);
        generator.envelope.setDecay(this.decayMs);
        generator.envelope.setSustain(this.sustainLevel);
        generator.envelope.setRelease(this.releaseMs);

        // Aktualizacja GUI w SynthApp
        app.sineCheckbox.setSelected(this.sineEnabled);
        app.sawCheckbox.setSelected(this.sawEnabled);
        app.squareCheckbox.setSelected(this.squareEnabled);
        app.triangleCheckbox.setSelected(this.triangleEnabled);

        app.sineSpinner.setValue(this.sineOctaveOffset);
        app.sawSpinner.setValue(this.sawOctaveOffset);
        app.squareSpinner.setValue(this.squareOctaveOffset);
        app.triangleSpinner.setValue(this.triangleOctaveOffset);

        app.lowPassKnob.setValue((float) this.lowPassCutoff/4980 -20);
        app.highPassKnob.setValue((float) this.highPassCutoff/4980 -20);

        if (app.envelopePanel != null) {
            app.envelopePanel.attackKnob.setValue((float) (int) this.attackMs /2000);
            app.envelopePanel.decayKnob.setValue((float) (int) this.decayMs /2000);
            app.envelopePanel.sustainKnob.setValue((float) (this.sustainLevel));
            app.envelopePanel.releaseKnob.setValue((float) (int) this.releaseMs /3000);
        }



    }



}