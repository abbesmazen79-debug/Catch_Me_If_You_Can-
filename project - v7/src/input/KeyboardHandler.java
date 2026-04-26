package input;

import java.awt.event.*;

public class KeyboardHandler implements KeyListener {

    public boolean up, down, left, right;
    public boolean escape, enter, espace;

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP,    KeyEvent.VK_Z -> up     = true;
            case KeyEvent.VK_DOWN,  KeyEvent.VK_S -> down   = true;
            case KeyEvent.VK_LEFT,  KeyEvent.VK_Q -> left   = true;
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> right  = true;
            case KeyEvent.VK_ESCAPE               -> escape = true;
            case KeyEvent.VK_ENTER                -> enter  = true;
            case KeyEvent.VK_SPACE                -> espace = true;  //dashing
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP,    KeyEvent.VK_Z -> up     = false;
            case KeyEvent.VK_DOWN,  KeyEvent.VK_S -> down   = false;
            case KeyEvent.VK_LEFT,  KeyEvent.VK_Q -> left   = false;
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> right  = false;
            case KeyEvent.VK_ESCAPE               -> escape = false;
            case KeyEvent.VK_ENTER                -> enter  = false;
            case KeyEvent.VK_SPACE                -> espace = false;
        }
    }

    @Override public void keyTyped(KeyEvent e) {}

    public void reset() {
        up = down = left = right = escape = enter = espace = false;
    }
}