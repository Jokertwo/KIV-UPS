import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import net.miginfocom.swing.MigLayout;


public class Tabbed extends JPanel {

    private JTextArea forReading = new JTextArea();
    private JTextArea forWriting = new JTextArea();
    private JTextArea users = new JTextArea();
    private JButton send = new JButton("Send");
    private JButton close = new JButton("Close");
    private JTabbedPane tabbedPane;
    private JScrollPane forRsp = new JScrollPane(forReading);
    private JScrollPane forWsp = new JScrollPane(forWriting);
    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    public Tabbed(JTabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane;
        createChatPanel();
        close.addActionListener(new CloseTab(this));
        send.addActionListener(new SendMessageButton());
        forWriting.addKeyListener(new SendMessageKey());
        forReading.setLineWrap(true);
        forWriting.setLineWrap(true);
    }


    private void createChatPanel() {
        setLayout(new MigLayout());
//        forReading.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
//        forWriting.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        users.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        forReading.setEditable(false);
        add(forRsp, "w 80%, h 80%,split");
        add(users, " w 40%,wrap,h 80%");
        add(forWsp, "w 100% ,h 20%,wrap");
        add(close, "w 100%,split");
        add(send, "w 100%");
    }

    private class CloseTab implements ActionListener {
        JPanel panel;


        public CloseTab(JPanel panel) {
            this.panel = panel;
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            tabbedPane.remove(panel);
        }
    }

    private class SendMessageKey implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
        }


        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                e.consume();
                send.doClick();
            }
        }


        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                e.consume();
            }

        }

    }

    private class SendMessageButton implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            forReading.append(forWriting.getText() + "\n");
            forWriting.setText("");
            forWriting.grabFocus();

        }

    }
}
