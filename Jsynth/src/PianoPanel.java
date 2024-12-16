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
    private static final Map<Integer, Tile> keyToButtonMap = new HashMap<>();
    
    public PianoPanel() {
    	//createFreqMap();
        this.setLayout(new GridBagLayout()); // Use GridBagLayout to center components
        JLayeredPane pianoKeysPane = createPianoKeysPane();
        this.add(pianoKeysPane); // Add the pianoKeysPane directly to the panel
    }
    
	public void pressKey(int keyCode) {
		Tile keyButton = keyToButtonMap.get(keyCode);
		if (keyButton != null) {
			keyButton.setBackground(keyButton.getIsBlack() ? Color.BLUE : Color.CYAN); // Highlight key
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
			keyButton.setBackground(keyButton.getIsBlack() ? Color.BLUE : Color.CYAN); // Highlight key
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
        int pianoWidth = 1000;
        int pianoHeight = 200;
        pianoKeysPane.setPreferredSize(new Dimension(pianoWidth, pianoHeight));

        // Create white keys
        int iloscOktaw = 6; // 1, 2, ..., 6
        final int srodkowaOktawa = 2; //0, 1, 2, ..., iloscOktaw -1
        int whiteKeyWidth = 20; // mod 4 = 0
        int whiteKeyHeight = 150;
        int blackKeyWidth = whiteKeyWidth / 2;
        int blackKeyHeight = 100;
        
        boolean[] isBlack = {false, true, false, true, false, false, true, false, true, false, true, false};
        
        int xLastPosition=iloscOktaw*12*whiteKeyWidth;
        int xPosition=pianoWidth-2*xLastPosition/3;
        
        for(int k =0; k<iloscOktaw;++k) {
        	for(int i=0;i<12;++i) {
	        	Tile key = new Tile(isBlack[i]);
	        	key.setKeyCode(1200*k+i);
	        	
	        	if(isBlack[i]) {
	        		key.setBounds(xPosition-blackKeyWidth/2, 0, blackKeyWidth, blackKeyHeight);
	        	}
	        	else {
	        		key.setBounds(xPosition, 0, whiteKeyWidth, whiteKeyHeight);
	        		xPosition += whiteKeyWidth;
	        	}
	        	
	        	key.setBackground(key.getIsBlack() ? Color.BLACK : Color.WHITE);
	            key.setBorder(BorderFactory.createLineBorder(key.getIsBlack() ? Color.DARK_GRAY : Color.BLACK));
	            key.setFocusable(false);
	            
	            switch(key.getKeyCode()) {
				case 0 + srodkowaOktawa*1200:
					keyToButtonMap.put(KeyEvent.VK_A, key);
					break;
				case 1 + srodkowaOktawa*1200:
					keyToButtonMap.put(KeyEvent.VK_W, key);
				    break;
			    case 2 + srodkowaOktawa*1200:
			    	keyToButtonMap.put(KeyEvent.VK_S, key);
				    break;
			    case 3 + srodkowaOktawa*1200:
			    	keyToButtonMap.put(KeyEvent.VK_E, key);
			    	break;
			    case 4 + srodkowaOktawa*1200:
			    	keyToButtonMap.put(KeyEvent.VK_D, key);
			    	break;
			    case 5 + srodkowaOktawa*1200:
			    	keyToButtonMap.put(KeyEvent.VK_F, key);
			    	break;
			    case 6 + srodkowaOktawa*1200:
			    	keyToButtonMap.put(KeyEvent.VK_T, key);
			    	break;
			    case 7 + srodkowaOktawa*1200:
			    	keyToButtonMap.put(KeyEvent.VK_G, key);
			    	break;
			    case 8 + srodkowaOktawa*1200:
			    	keyToButtonMap.put(KeyEvent.VK_Y, key);
			    	break;
			    case 9 + srodkowaOktawa*1200:
			    	keyToButtonMap.put(KeyEvent.VK_H, key);
			        break;
			    case 10 + srodkowaOktawa*1200:
			    	keyToButtonMap.put(KeyEvent.VK_U, key);
			    	break;
			    case 11 + srodkowaOktawa*1200:
			    	keyToButtonMap.put(KeyEvent.VK_J, key);
			    	break;
			    case 0 + (srodkowaOktawa+1)*1200: //kolejna oktawa
			    	keyToButtonMap.put(KeyEvent.VK_K, key);
			    	break;
			    default:
			    	keyToButtonMap.put(1200*k+i, key);
			    	break;
	            }
	            
	            pianoKeysPane.add(key, key.getIsBlack() ? JLayeredPane.PALETTE_LAYER : JLayeredPane.DEFAULT_LAYER); // Add tiles
	        }
        }
        
        return pianoKeysPane;
    }
}
