package action;

import javax.swing.JOptionPane;
import connection.Comunicator;
import gui.ChatWindow;
import gui.Tabbed;


/**
 * Akce pri ukonceni serveru -> pokusi se odhlasit a uzavrit pripojeni
 * 
 * @author Petr A15B0055K
 *
 */
public class ShutdownServer {
    Comunicator com;


    public ShutdownServer(Comunicator com) {
        this.com = com;
        shutDown();
    }


    private void shutDown() {
        com.stop();
        disableButton();
        showDialog();
    }


    private void disableButton() {
        for (Tabbed tab : ChatWindow.listOfOpenWidows.values()) {
            tab.getSend().setEnabled(false);
        }
    }


    private void showDialog() {
        JOptionPane.showMessageDialog(null,
            "Server is now offline",
            "Server shutdown",
            JOptionPane.WARNING_MESSAGE);
    }

}
