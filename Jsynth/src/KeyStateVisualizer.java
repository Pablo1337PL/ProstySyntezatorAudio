import javax.swing.*;
import java.awt.*;

public class KeyStateVisualizer extends JFrame {

    private JLabel keyStateLabel;

    public KeyStateVisualizer() {
        setTitle("Key State Visualizer");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        keyStateLabel = new JLabel("H NOT PRESSED", SwingConstants.CENTER);
        keyStateLabel.setFont(new Font("Arial", Font.BOLD, 24));

        add(keyStateLabel, BorderLayout.CENTER);

        setVisible(true);
    }

    public void updateKeyState(boolean isPressed) {
        if (isPressed) {
            keyStateLabel.setText("H PRESSED");
            keyStateLabel.setForeground(Color.GREEN);
        } else {
            keyStateLabel.setText("H NOT PRESSED");
            keyStateLabel.setForeground(Color.RED);
        }
    }
}
