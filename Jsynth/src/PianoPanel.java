import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class PianoPanel extends JPanel {
	private class Tile extends JButton{
		private boolean isBlack;
		private int keyCode;
		public Tile(boolean isBlack) {
			super();
			this.isBlack = isBlack;
		}
		public boolean getIsBlack() {
			return isBlack;
		}
		public int getKeyCode() {
			return keyCode;
		}
		public void setKeyCode(int keyCode) {
			this.keyCode = keyCode;
		}
	}
	
    private static final Map<Integer, Double> keyToFrequencyMap = new HashMap<>();
    private static final Map<Integer, Tile> keyToButtonMap = new HashMap<>();
    
    
    static {
        keyToFrequencyMap.put(KeyEvent.VK_A, 440.00);
        keyToFrequencyMap.put(KeyEvent.VK_W, 466.16);
        keyToFrequencyMap.put(KeyEvent.VK_S, 493.88);
        keyToFrequencyMap.put(KeyEvent.VK_D, 523.25);
        keyToFrequencyMap.put(KeyEvent.VK_R, 554.37);
        keyToFrequencyMap.put(KeyEvent.VK_F, 587.33);
        keyToFrequencyMap.put(KeyEvent.VK_T, 622.25);
        keyToFrequencyMap.put(KeyEvent.VK_G, 659.25);
        keyToFrequencyMap.put(KeyEvent.VK_H, 698.46);
        keyToFrequencyMap.put(KeyEvent.VK_U, 739.99);
        keyToFrequencyMap.put(KeyEvent.VK_J, 783.99);
        keyToFrequencyMap.put(KeyEvent.VK_I, 830.61);
        keyToFrequencyMap.put(KeyEvent.VK_K, 880.00);
    }

    public PianoPanel() {
        this.setLayout(new GridBagLayout()); // Use GridBagLayout to center components
        JLayeredPane pianoKeysPane = createPianoKeysPane();
        this.add(pianoKeysPane); // Add the pianoKeysPane directly to the panel
    }
    
	public void pressKey(int keyCode) {
		Tile keyButton = keyToButtonMap.get(keyCode);
		if (keyButton != null) {
			keyButton.setBackground(keyButton.getIsBlack() ? Color.DARK_GRAY : Color.YELLOW); // Highlight key
		}
	}
	
	public void releaseKey(int keyCode) {
		Tile keyButton = keyToButtonMap.get(keyCode);
		if (keyButton != null) {
			keyButton.setBackground(keyButton.getIsBlack() ? Color.BLACK : Color.WHITE); // Reset key color
		}
	}
	
	public int mouseClick(Point mousePoint) {
		Component component = getComponentAt(mousePoint);
		if (component instanceof Tile) {
			Tile keyButton = (Tile) component;
			keyButton.setBackground(keyButton.getIsBlack() ? Color.DARK_GRAY : Color.YELLOW); // Highlight key
			return keyButton.getKeyCode();
		}
		return -1; // No key was clicked
	}
	
	public int mouseReleased(Point mousePoint) {
		Component component = getComponentAt(mousePoint);
		if (component instanceof Tile) {
			Tile keyButton = (Tile) component;
			keyButton.setBackground(keyButton.getIsBlack() ? Color.BLACK : Color.WHITE); // Reset key color
			return keyButton.getKeyCode();
		}
		return -1; // No key was released
	}
	
    
    private JLayeredPane createPianoKeysPane() {
        JLayeredPane pianoKeysPane = new JLayeredPane();
        int pianoWidth = 400;
        int pianoHeight = 200;
        pianoKeysPane.setPreferredSize(new Dimension(pianoWidth, pianoHeight));

        // Create white keys
        int whiteKeyWidth = 50;
        int whiteKeyHeight = 150;
        int[] whiteKeyOrder = {
            KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D, KeyEvent.VK_F, KeyEvent.VK_G, KeyEvent.VK_H, KeyEvent.VK_J, KeyEvent.VK_K
        };
        int xPosition = 0;
        for (int key : whiteKeyOrder) {
            Tile whiteKey = new Tile(false);
            whiteKey.setKeyCode(key);
            whiteKey.setBounds(xPosition, 0, whiteKeyWidth, whiteKeyHeight);
            whiteKey.setBackground(Color.WHITE);
            whiteKey.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            whiteKey.setFocusable(false);
            keyToButtonMap.put(key, whiteKey);
            pianoKeysPane.add(whiteKey, JLayeredPane.DEFAULT_LAYER); // Add white keys to the default layer
            xPosition += whiteKeyWidth;
        }

        // Create black keys
        int blackKeyWidth = 30;
        int blackKeyHeight = 100;
        int[] blackKeyOrder = {
            KeyEvent.VK_W, KeyEvent.VK_R, KeyEvent.VK_T, KeyEvent.VK_U, KeyEvent.VK_I
        };
        int[] blackKeyOffsets = {35, 85, 185, 235, 285}; // Relative x-positions for black keys
        for (int i = 0; i < blackKeyOrder.length; i++) {
            Tile blackKey = new Tile(true);
            blackKey.setKeyCode(blackKeyOrder[i]);
            blackKey.setBounds(blackKeyOffsets[i], 0, blackKeyWidth, blackKeyHeight);
            blackKey.setBackground(Color.BLACK);
            blackKey.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            blackKey.setFocusable(false);
            keyToButtonMap.put(blackKeyOrder[i], blackKey);
            pianoKeysPane.add(blackKey, JLayeredPane.PALETTE_LAYER); // Add black keys to the palette layer (on top of white keys)
        }

        // Add key listener to handle key events
//        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
//            if (e.getID() == KeyEvent.KEY_PRESSED) {
//                Tile keyButton = keyToButtonMap.get(e.getKeyCode());
//                if (keyButton != null) {
//                    keyButton.setBackground(keyButton.getIsBlack() ? Color.DARK_GRAY : Color.YELLOW); // Highlight key
//                }
//            } else if (e.getID() == KeyEvent.KEY_RELEASED) {
//                Tile keyButton = keyToButtonMap.get(e.getKeyCode());
//                if (keyButton != null) {
//                    keyButton.setBackground(keyButton.getIsBlack() ? Color.BLACK : Color.WHITE); // Reset key color
//                }
//            }
//            return false;
//        });
      
        
//		MouseAdapter mouseAdapter = new MouseAdapter() {
//			@Override
//			public void mousePressed(MouseEvent e) {
//				Component component = pianoKeysPane.getComponentAt(e.getPoint());
//				if (component instanceof Tile) {
//					Tile keyButton = (Tile) component;
//					keyButton.setBackground(keyButton.getIsBlack() ? Color.DARK_GRAY : Color.YELLOW); // Highlight key
//					
//				}
//			}
//
//			@Override
//			public void mouseReleased(MouseEvent e) {
//				Component component = pianoKeysPane.getComponentAt(e.getPoint());
//				if (component instanceof Tile) {
//					Tile keyButton = (Tile) component;
//					keyButton.setBackground(keyButton.getIsBlack() ? Color.BLACK : Color.WHITE); // Reset key color
//				}
//			}
//		};

        return pianoKeysPane;
    }
}
