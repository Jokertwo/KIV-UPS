package documentListener;

import javax.swing.JButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;


/**
 * Document listener nasloucho JTextComponent a kontroluje jestli je prazdna nebo ne. (jestli uz nekdo neco zapsal nebo
 * se snazi poslat prazdnou zpravu)
 * 
 * @author Petr A15B0055K
 *
 */
public class CheckEmptyTextField implements DocumentListener {

    JButton loginButton;


    public CheckEmptyTextField(JButton loginButton) {
        this.loginButton = loginButton;
    }


    @Override
    public void insertUpdate(DocumentEvent e) {
        loginButton.setEnabled(checkDocument(e));
    }


    @Override
    public void removeUpdate(DocumentEvent e) {
        loginButton.setEnabled(checkDocument(e));
    }


    @Override
    public void changedUpdate(DocumentEvent e) {
        loginButton.setEnabled(checkDocument(e));
    }


    private boolean checkDocument(DocumentEvent e) {
        PlainDocument document = (PlainDocument) e.getDocument();
        if (document.getLength() < 1) {
            return false;
        }
        try {
            if (((document.getText(0, document.getLength())).trim()).length() > 0) {
                return true;
            }
        } catch (BadLocationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return false;
        }
        return false;
    }

}
