package com.dreamfabric;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class DKnob extends JComponent {
    private final static float START = 225;
    private final static float LENGTH = 270;
    private final static float PI = (float) 3.1415;
    private final static float START_ANG = (START/360)*PI*2;
    private final static float LENGTH_ANG = (LENGTH/360)*PI*2;

    private ChangeEvent changeEvent = null;
    private EventListenerList listenerList = new EventListenerList();

    private int size;
    private int middle;
    private float val; // 0..1
    private double lastAng;
    private int dragType = ROUND;

    // Zakres wartości wyświetlanych i interpretowanych
    private float minVal = 0.0f;
    private float maxVal = 1.0f;

    // Czy mysz jest wciśnięta
    private boolean mouseDown = false;
    private int mouseX, mouseY;

    public final static int SIMPLE = 1;
    public final static int ROUND  = 2;

    private final float precisionStep = 0.0025f;

    public DKnob() {
        setPreferredSize(new Dimension(60, 60));
        dragType = ROUND;
        // Add mouse wheel listener for precision scrolling
        addMouseWheelListener(e -> {
            int rotation = e.getWheelRotation();
            if (rotation > 0) {
                setValue(val + precisionStep); // Scroll down decreases value
            } else if (rotation < 0) {
                setValue(val - precisionStep); // Scroll up increases value
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                mouseDown = true;
                int xpos = middle - me.getX();
                int ypos = middle - me.getY();
                lastAng = Math.atan2(xpos, ypos);
                requestFocusInWindow();
            }

            public void mouseReleased(MouseEvent me) {
                mouseDown = false;
                repaint(); // odśwież, aby zniknął tekst po zwolnieniu myszy
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent me) {
                if (dragType == ROUND) {
                    int xpos = middle - me.getX();
                    int ypos = middle - me.getY();
                    double ang = Math.atan2(xpos, ypos);
                    double diff = lastAng - ang;
                    setValue((float) (getValue() + (diff / LENGTH_ANG)));
                    lastAng = ang;

                    // Zapamiętujemy pozycję myszy by wyświetlić tekst w pobliżu
                    mouseX = me.getX();
                    mouseY = me.getY();
                    repaint();
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int k = e.getKeyCode();
                if (k == KeyEvent.VK_RIGHT) {
                    setValue(getValue() + 0.01f);
                } else if (k == KeyEvent.VK_LEFT) {
                    setValue(getValue() - 0.01f);
                }
            }
        });

        setFocusable(true);
    }

    /**
     * Ustawia zakres wartości reprezentowanych przez 0..1 gałki.
     * np. setRange(0, 1000) dla 0-1000ms
     */
    public void setRange(float min, float max) {
        this.minVal = min;
        this.maxVal = max;
        repaint();
    }

    public void setDragType(int type) {
        dragType = type;
    }
    public int getDragType() {
        return dragType;
    }

    public float getValue() {
        return val;
    }

    /**
     * Ustawia wartość gałki (0..1). Należy pamiętać, że wyświetlana
     * wartość będzie skalowana do minVal..maxVal.
     */
    public void setValue(float val) {
        if (val < 0) val = 0;
        if (val > 1) val = 1;
        this.val = val;
        repaint();
        fireChangeEvent();
    }

    public void addChangeListener(ChangeListener cl) {
        listenerList.add(ChangeListener.class, cl);
    }

    public void removeChangeListener(ChangeListener cl) {
        listenerList.remove(ChangeListener.class, cl);
    }

    protected void fireChangeEvent() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null)
                    changeEvent = new ChangeEvent(this);
                ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
            }
        }
    }

    @Override
    public boolean isFocusTraversable() {
        return true;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();
        size = Math.min(width, height) - 4;
        middle = 2 + size/2;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Rysujemy gałkę
        g2.setColor(Color.WHITE);
        g2.fillOval(2, 2, size, size);

        g2.setColor(Color.GRAY);
        g2.drawOval(2, 2, size, size);

        float ang = START_ANG - val * LENGTH_ANG;

        int x = 2 + size/2 + (int)((size/2 - 5) * Math.cos(ang));
        int y = 2 + size/2 - (int)((size/2 - 5) * Math.sin(ang));

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3f));
        g2.drawLine(2 + size/2, 2 + size/2, x, y);

        if (hasFocus()) {
            g2.setColor(new Color(0x8080FF));
            g2.setStroke(new BasicStroke(1f));
            g2.drawOval(0, 0, size+4, size+4);
        }

        // Jeśli mysz wciśnięta, wyświetlamy wartość w przeskalowanym zakresie
        if (mouseDown) {
            float scaledValue = minVal + val * (maxVal - minVal);

            // Ustal formatowanie - jeżeli sustain 0..1 to np. dwie cyfry po przecinku,
            // jeżeli ms to może być bez przecinka lub też z przecinkami.
            // Dla ogólności użyjemy "%.2f", ale można dostosować w zależności od zakresu.
            String text = String.format("%.2f", scaledValue);

            FontMetrics fm = g2.getFontMetrics();
            int tw = fm.stringWidth(text);
            int th = fm.getAscent();

            // Tło dla wartości
            int tx = mouseX - tw/2;
            int ty = mouseY - 10; // powyżej kursora
            g2.setColor(new Color(0,0,0,180));
            g2.fillRect(tx - 2, ty - th, tw + 4, th + 2);
            g2.setColor(Color.WHITE);
            g2.drawString(text, tx, ty);
        }
    }
}
