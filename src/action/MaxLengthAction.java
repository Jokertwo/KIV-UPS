package action;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Logger;
import javax.swing.text.JTextComponent;


/**
 * Nedovoli do JTextComponent zapsat vice znaku nez kolik je preddefinovano
 * 
 * @author Petr
 *
 */
public class MaxLengthAction implements KeyListener {

    protected JTextComponent component;
    protected int maxLength;
    Logger log = Logger.getLogger(MaxLengthAction.class.getName());


    public MaxLengthAction(JTextComponent component, int maxLength) {
        this.component = component;
        this.maxLength = maxLength;
    }


    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub

    }


    @Override
    public void keyPressed(KeyEvent e) {
        if (component.getText().length() >= maxLength) {
            component.setText(component.getText().substring(0, component.getText().length() - 1));

        }

    }


    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }

}
