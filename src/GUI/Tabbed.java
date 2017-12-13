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
import net.miginfocom.swing.MigLayout;


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

    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    public Tabbed(JTabbedPane tabbedPane, String addressee, Parser parser, boolean isPublicChat) {
        log.info("Inicialization of new tab for " + addressee);
        this.tabbedPane = tabbedPane;
        this.addressee = addressee;
        this.parser = parser;
        this.isPublicChat = isPublicChat;
        createChatPanel();
        close.addActionListener(new CloseTab());
        send.addActionListener(new SendMessageButton());
        forWriting.addKeyListener(new EnterActionKey(send));
        forWriting.addKeyListener(new MaxLengthAction(forWriting, Constants.MAX_MESSAGE_LENGTH));
        forReading.setLineWrap(true);
        forWriting.setLineWrap(true);
        setFont();
    }


    public String getAddressee() {
        return this.addressee;
    }


    public JButton getSend() {
        return send;
    }


    public JTextArea getForReading() {
        return this.forReading;
    }


    private void setFont() {
        Font font = new Font(Font.MONOSPACED, Font.PLAIN, 19);
        forReading.setFont(font);
        forWriting.setFont(font);
    }


    private void createChatPanel() {
        setLayout(new MigLayout());

        forReading.setEditable(false);
        add(forRsp, "w 100%, h 80%,wrap");
        add(forWsp, "w 100% ,h 20%,wrap");
        add(close, "w 100%,split");
        add(send, "w 100%");
    }

    private class CloseTab implements ActionListener {
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
                    if (tempMap.containsKey(addressee)) {
                        tabbedPane.remove(tempMap.get(addressee));
                        tempMap.remove(addressee);
                        log.info("Removing tab :" + addressee);
                    }
                } else {
                    log.warning("Can't find ChatWindow frame");
                }

            }

        }
    }

    private class SendMessageButton implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (send().equals(Constants.OK)) {
                forReading.append("You : " + forWriting.getText() + "\n");
                forWriting.setText("");
                forWriting.grabFocus();
            } else {
                forReading.append("You : {MESSAGE WASN'T SEND, TRY TO AGAIN}" + forWriting.getText() + "\n");
                forWriting.grabFocus();
            }

        }


        private String send() {
            if (isPublicChat) {
                for (int i = 0; i < 10; i++) {
                    parser.sendPublicMessage(forWriting.getText());
                }
                return parser.sendPublicMessage(forWriting.getText());

            } else {
                for (int i = 0; i < 10; i++) {
                    parser.sendPrivateMessage(addressee, forWriting.getText());
                }
                return parser.sendPrivateMessage(addressee, forWriting.getText());
            }
        }

    }
}
