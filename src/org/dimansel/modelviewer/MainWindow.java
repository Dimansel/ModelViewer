package org.dimansel.modelviewer;

import org.dimansel.projection3d.Keyboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class MainWindow extends JFrame implements KeyEventDispatcher, ActionListener {
    private final int WIDTH = 1200;
    private final int HEIGHT = 700;

    private Screen screen;
    private Robot robot;
    private Point prevPoint = new Point(); //storing previous mouse position
    private long lastFPS;
    private int fps;

    private MainWindow() {
        //INITIALIZATION
        screen = new Screen(WIDTH, HEIGHT);
        lastFPS = System.currentTimeMillis();

        //ADDING LISTENERS
        KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        kfm.addKeyEventDispatcher(this);
        Timer timer = new Timer(10, this);
        timer.start();

        //WINDOW CONFIGURATION
        setSize(WIDTH, HEIGHT);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - WIDTH) / 2, (screenSize.height - HEIGHT) / 2);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Model Viewer");
        add(screen);
        setVisible(true);
        try { robot = new Robot(); } catch(AWTException e) {e.printStackTrace();}
    }

    private void render() {
        handleMouse(); //rotate camera
        screen.cam.processKeyboard(0.1); //moving camera
        screen.projectVertices();
        screen.repaint();
        updateFPS();
    }

    private void setCursor() {
        if (screen.mouseGrabbed) {
            BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                    cursorImg, new Point(0, 0), "blank cursor");
            getContentPane().setCursor(blankCursor);
        } else {
            getContentPane().setCursor(Cursor.getDefaultCursor());
        }
    }

    private void handleMouse() {
        Point p = new Point(WIDTH/2, HEIGHT/2);
        SwingUtilities.convertPointToScreen(p, screen);
        Point e = MouseInfo.getPointerInfo().getLocation();

        if ((!e.equals(p) && screen.mouseGrabbed)) {
            screen.cam.processMouse(e.x - prevPoint.x, e.y - prevPoint.y);
            prevPoint = e;
        }

        if (screen.mouseGrabbed) {
            robot.mouseMove(p.x, p.y);
            prevPoint = p;
        }
    }

    private void grabMouse() {
        Point p = new Point(WIDTH/2, HEIGHT/2);
        SwingUtilities.convertPointToScreen(p, screen);
        prevPoint = p;
        robot.mouseMove(p.x, p.y);
        screen.mouseGrabbed = !screen.mouseGrabbed;
    }

    private void updateFPS() {
        if (System.currentTimeMillis() - lastFPS > 1000) {
            setTitle("FPS: " + String.valueOf(fps));
            fps = 0;
            lastFPS += 1000;
        }
        fps++;
    }

    /**
     * EVENTS
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            Keyboard.addKey(e.getKeyCode());
            if (e.getKeyCode() == KeyEvent.VK_G) {
                grabMouse();
                setCursor();
            }
        }
        if (e.getID() == KeyEvent.KEY_RELEASED) Keyboard.removeKey(e.getKeyCode());

        return false;
    }

    /**
     * Timer tick
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        render();
    }

    public static void main(String[] args) {
        new MainWindow();
    }
}
