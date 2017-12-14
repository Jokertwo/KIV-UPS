package gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import action.EnterActionKey;
import action.MaxLengthAction;
import connection.Parser;
import constants.Constants;
import main.Main;
import net.miginfocom.swing.MigLayout;


/**
 * Reprezentuje jednu zalozku chatu
 * 
 * @author Petr A15B0055K
 *
 */
public class Tabbed extends JPanel {

    private static final Logger log = Logger.getLogger(Tabbed.class.getName());

    private JTextArea forReading = new JTextArea();
    private JTextArea forWriting = new JTextArea();

    private JButton send = new JButton("Send");
    private JButton close = new JButton("Close");
    private JTabbedPane tabbedPane;
    private JScrollPane forRsp = new JScrollPane(forReading);
    private JScrollPane forWsp = new JScrollPane(forWriting);
    private String addressee;

    private final Parser parser;

    private final boolean isPublicChat;

    private static final long serialVersionUID = 1L;


    public Tabbed(JTabbedPane tabbedPane, String addressee, Parser parser, boolean isPublicChat) {
        log.info("Inicialization of new tab for " + addressee);
        this.tabbedPane = tabbedPane;
        this.addressee = addressee;
        this.parser = parser;
        this.isPublicChat = isPublicChat;
        createChatPanel();
        close.addActionListener(new CloseTab(this));
        send.addActionListener(new SendMessageButton());
        forWriting.addKeyListener(new EnterActionKey(send));
        forWriting.addKeyListener(new MaxLengthAction(forWriting, Constants.MAX_MESSAGE_LENGTH));
        forReading.setLineWrap(true);
        forWriting.setLineWrap(true);
        setFont();

    }


    /**
     * Pripoji text do textarea urcene pro cteni
     * 
     * @param text
     *            zprava ze serveru
     */
    public void appendText(String text) {
        forReading.append(text);
        forRsp.getVerticalScrollBar().setValue(forRsp.getVerticalScrollBar().getMaximum());
    }


    /**
     * Jmeno adresata
     * 
     * @return jmenu komu se bodou posilat zpravy
     */
    public String getAddressee() {
        return this.addressee;
    }


    /**
     * Vraci tlacitko pro odeslani zpravy
     * 
     * @return
     */
    public JButton getSend() {
        return send;
    }


    /**
     * Nastavuje font
     */
    private void setFont() {
        Font font = new Font(Font.MONOSPACED, Font.PLAIN, 19);
        forReading.setFont(font);
        forWriting.setFont(font);
    }


    /**
     * Vyvori panel se vsemi potrebnymi prvky a umisti je do okna
     */
    private void createChatPanel() {
        setLayout(new MigLayout());

        forReading.setEditable(false);
        add(forRsp, "w 100%, h 80%,wrap");
        add(forWsp, "w 100% ,h 20%,wrap");
        add(close, "w 100%,split");
        add(send, "w 100%");
    }

    /**
     * akce pro zavreni zalozky s chatem
     * 
     * @author Petr A15B0055K
     *
     */
    private class CloseTab implements ActionListener {
        private Tabbed tab;


        public CloseTab(Tabbed tab) {
            this.tab = tab;
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            int n = JOptionPane.showConfirmDialog(
                null,
                "If you close the window, you lost hole communication!",
                "Warning",
                JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                ChatWindow frame = (ChatWindow) SwingUtilities.getAncestorOfClass(ChatWindow.class, tabbedPane);
                if (frame != null) {
                    Map<String, Tabbed> tempMap = frame.getListOfOpenWindows();
                    tempMap.remove(addressee);
                    if (tempMap.containsKey(addressee)) {
                        tabbedPane.remove(tab);
                        log.info("Removing tab :" + addressee);
                    }
                } else {
                    log.warning("Can't find ChatWindow frame");
                }

            }

        }
    }

    /**
     * Akce tlacitka pro poslani zpravy
     * 
     * @author Petr A15B0055K
     *
     */
    private class SendMessageButton implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (send().equals(Constants.OK)) {
                appendText("You : " + forWriting.getText() + "\n");
                forWriting.setText("");
                forWriting.grabFocus();
            } else {
                forReading.append("You : {MESSAGE WASN'T SEND, TRY TO AGAIN}" + forWriting.getText() + "\n");
                forWriting.grabFocus();
            }

        }


        /**
         * Posle zpravu for cykly v podmince IF probehnou pouze v testovacim modu ktery se spousti spustenim teto
         * aplikace a argumentem(cislem)
         * 
         * @return
         */
        private String send() {
            if (isPublicChat) {
                if (Main.TEST) {
                    for (int i = 0; i < Main.NUMBER; i++) {
                        parser.sendPublicMessage(forWriting.getText() + i);
                    }
                }
                return parser.sendPublicMessage(forWriting.getText());
            } else {
                if (Main.TEST) {
                    for (int i = 0; i < Main.NUMBER; i++) {
                        parser.sendPrivateMessage(addressee, forWriting.getText() + i);
                    }
                }
                return parser.sendPrivateMessage(addressee, forWriting.getText());
            }
        }

    }
}
