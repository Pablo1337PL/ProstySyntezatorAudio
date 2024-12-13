public class EnvelopeGenerator {
    private double attackTimeMs = 100.0;
    private double decayTimeMs = 200.0;
    private double sustainLevel = 1;
    private double releaseTimeMs = 100.0;

    private boolean noteOn = false;
    private boolean noteOff = false;
    private long noteOnSample = 0;
    private long noteOffSample = 0;
    private double ampAtRelease = 0.0; // amplituda w momencie przejścia do release

    private double sampleRate = 44100.0;

    public void setSampleRate(double rate) {
        this.sampleRate = rate;
    }

    public void setAttack(double ms) {
        this.attackTimeMs = ms;
    }

    public void setDecay(double ms) {
        this.decayTimeMs = ms;
    }

    public void setSustain(double level) {
        this.sustainLevel = level;
    }

    public void setRelease(double ms) {
        this.releaseTimeMs = ms;
    }

    public double getAttackTimeMs() {return attackTimeMs;}
    public double getDecayTimeMs() {return decayTimeMs;}
    public double getSustainLevel() {return sustainLevel;}
    public double getReleaseTimeMs() {return releaseTimeMs;}

    public void noteOn(long currentSample) {
        this.noteOn = true;
        this.noteOff = false;
        this.noteOnSample = currentSample;
    }

    public void noteOff(long currentSample) {
        // Najpierw obliczamy aktualną amplitudę przy currentSample (moment puszczenia klawisza)
        double currentAmp = getAmplitude(currentSample);

        this.noteOff = true;
        this.noteOffSample = currentSample;
        this.ampAtRelease = currentAmp; // zapamiętujemy amplitudę w momencie startu release
    }

    public double getAmplitude(long currentSample) {
        if (!noteOn) {
            return 0.0;
        }

        double t = (currentSample - noteOnSample) / sampleRate;
        double A = attackTimeMs / 1000.0;
        double D = decayTimeMs / 1000.0;
        double R = releaseTimeMs / 1000.0;

        if (!noteOff) {
            // Fazy: Attack -> Decay -> Sustain
            if (t < A) {
                // Attack od 0 do 1
                return t / A;
            } else if (t < A + D) {
                // Decay od 1 do sustainLevel
                double decayProgress = (t - A) / D;
                return 1.0 - (1.0 - sustainLevel) * decayProgress;
            } else {
                // Sustain
                return sustainLevel;
            }
        } else {
            // Fazę release liczymy od noteOffSample
            double toff = (currentSample - noteOffSample) / sampleRate;
            if (toff < 0) toff = 0; // bezpieczeństwo

            // W fazie release schodzimy z ampAtRelease do 0 w czasie R
            double rel = ampAtRelease * (1.0 - (toff / R));
            if (rel < 0.0) rel = 0.0;
            return rel;
        }
    }

    public double[] generateADSRShape(int length, double maxTimeSec) {
        double[] shape = new double[length];
        double dt = maxTimeSec / length;

        double A = attackTimeMs/1000.0;
        double D = decayTimeMs/1000.0;
        double R = releaseTimeMs/1000.0;
        double sustainTime = (A + D) * 2;
        double totalTime = A + D + sustainTime + R;

        for (int i=0; i<length; i++) {
            double time = i * dt;
            double amp;
            if (time < A) {
                amp = (time / A);
            } else if (time < A + D) {
                double decayProgress = (time - A) / D;
                amp = 1.0 - (1.0 - sustainLevel)*decayProgress;
            } else if (time < A + D + sustainTime) {
                amp = sustainLevel;
            } else {
                double tr = (time - (A+D+sustainTime));
                if (tr < R) {
                    double relProgress = tr / R;
                    amp = sustainLevel * (1.0 - relProgress);
                    if (amp < 0.0) amp = 0.0;
                } else {
                    amp = 0.0;
                }
            }
            shape[i] = amp;
        }

        return shape;
    }
}