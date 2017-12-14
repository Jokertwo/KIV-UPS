package documentFilters;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;


/**
 * 
 * Dokument filter zakazujici zapsat do JTextComponent neco jineho nez integer
 * 
 * @author Petr A15B0055K
 *
 */
public class IntegerFilter extends DocumentFilter {

    @Override
    public void insertString(FilterBypass fb,
                             int offset,
                             String text,
                             AttributeSet attr)
        throws BadLocationException {
        super.insertString(fb, offset, checkIt(text), attr);
    }


    @Override
    public void replace(FilterBypass fb,
                        int offset,
                        int length,
                        String text,
                        AttributeSet attrs)
        throws BadLocationException {
        super.replace(fb, offset, length, checkIt(text), attrs);
    }


    @Override
    public void remove(FilterBypass fb, int offset, int length)
        throws BadLocationException {
        super.remove(fb, offset, length);
    }


    private String checkIt(String text) {
        StringBuilder builder = new StringBuilder();

        for (Character temp : text.toCharArray()) {
            if ((temp.equals('.')) || Character.isDigit(temp)) {
                builder.append(temp);
            }
        }
        return builder.toString();

    }

}
