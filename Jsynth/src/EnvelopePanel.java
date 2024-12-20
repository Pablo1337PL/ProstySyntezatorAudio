
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import com.dreamfabric.DKnob;

public class EnvelopePanel extends JPanel {
    private final EnvelopeGenerator envelopeGenerator;
    private final EnvelopeVisualizer envelopeVisualizer;

    public final DKnob attackKnob;
    public final DKnob decayKnob;
    public final DKnob sustainKnob;
    public final DKnob releaseKnob;

    private final JFrame parentFrame; // Referencja do głównego okna

    public EnvelopePanel(EnvelopeGenerator envelopeGenerator, JFrame parentFrame) {
        this.envelopeGenerator = envelopeGenerator;
        this.parentFrame = parentFrame;

        setLayout(new BorderLayout());

        // Panel z knobami w układzie 2x2, mniejsze odstępy
        JPanel knobsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        knobsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        attackKnob = createKnob((float) (envelopeGenerator.getAttackTimeMs() / 1000.0f));
        decayKnob = createKnob((float) (envelopeGenerator.getDecayTimeMs() / 1000.0f));
        sustainKnob = createKnob((float)envelopeGenerator.getSustainLevel());
        releaseKnob = createKnob((float) (envelopeGenerator.getReleaseTimeMs() / 1500.0f));

        knobsPanel.add(wrapKnob("Attack (ms)", attackKnob));
        knobsPanel.add(wrapKnob("Decay (ms)", decayKnob));
        knobsPanel.add(wrapKnob("Sustain", sustainKnob));
        knobsPanel.add(wrapKnob("Release (ms)", releaseKnob));

        // Zmniejszamy panel z knobami
        knobsPanel.setPreferredSize(new Dimension(150, 150));

        add(knobsPanel, BorderLayout.WEST);

        // Wizualizacja Envelope zmniejszona lub pozostawiona tak samo
        // Jeśli chcesz ją też zmniejszyć, zmień wymiary poniżej:
        envelopeVisualizer = new EnvelopeVisualizer();
        envelopeVisualizer.setPreferredSize(new Dimension(230, 120));
        updateEnvelopeShape();
        add(envelopeVisualizer, BorderLayout.CENTER);
    }

    private DKnob createKnob(float initialVal) {
        if (initialVal < 0f) initialVal = 0f;
        if (initialVal > 1f) initialVal = 1f;

        DKnob knob = new DKnob();
        knob.setValue(initialVal);
        knob.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                applyKnobValues();
            }
        });
        return knob;
    }

    private JPanel wrapKnob(String label, DKnob knob) {
        JPanel p = new JPanel(new BorderLayout());
        // Zmniejszamy panel zawierający knob:
        p.setPreferredSize(new Dimension(80,80));

        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 10f)); // Mniejsza czcionka dla etykiet
        p.add(lbl, BorderLayout.NORTH);

        // Opcjonalnie można zmniejszyć marginesy wewnątrz panelu
        JPanel knobHolder = new JPanel(new GridBagLayout());
        knobHolder.add(knob);
        p.add(knobHolder, BorderLayout.CENTER);

        return p;
    }

    private void applyKnobValues() {
        float attackVal = attackKnob.getValue() * 2000f;
        float decayVal = decayKnob.getValue() * 2000f;
        float sustainVal = sustainKnob.getValue();
        float releaseVal = releaseKnob.getValue() * 3000f;

        envelopeGenerator.setAttack(attackVal);
        envelopeGenerator.setDecay(decayVal);
        envelopeGenerator.setSustain(sustainVal);
        envelopeGenerator.setRelease(releaseVal);

        updateEnvelopeShape();

        if (parentFrame != null) {
            parentFrame.requestFocusInWindow();
        }
    }

    private void updateEnvelopeShape() {
        double[] shape = envelopeGenerator.generateADSRShape(300, 1.0);
        envelopeVisualizer.updateShape(shape);
    }
}
