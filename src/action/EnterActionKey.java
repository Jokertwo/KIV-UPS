package action;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;

public class EnterActionKey implements KeyListener {

    JButton btn;
    
    public EnterActionKey(JButton btn) {
        // TODO Auto-generated constructor stub
        this.btn = btn;
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            e.consume();
            btn.doClick();
        }
    }


    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            e.consume();
        }

    }

}