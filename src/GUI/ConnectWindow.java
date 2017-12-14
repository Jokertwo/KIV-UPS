package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;
import action.EnterActionKey;
import action.MyTimer;
import connection.Parser;
import documentFilters.IntegerFilter;
import net.miginfocom.swing.MigLayout;


/**
 * Uvidni okno aplikace kde jsou pole nakonfigurovani kam se ma client pripojit
 * 
 * @author Petr A15B0055K
 *
 */
public class ConnectWindow extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = -6197662685806093187L;

    private final static Logger LOG = Logger.getLogger(ConnectWindow.class.getName());

    private JLabel labelIP = new JLabel("IP adress :");
    private JTextField textIP = new JTextField();

    private JLabel labelPort = new JLabel("Port number :");
    private JTextField textPort = new JTextField();

    private JLabel infoLabel = new JLabel(" ");

    private JButton connectB;

    private Font font = new Font(Font.MONOSPACED, Font.PLAIN, 20);


    public ConnectWindow() {
        setTitle("Connecting to server");
        setSize(350, 350);
        add(componentPanel());

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initFont();
        addDocumentFilter(textIP);
        addDocumentFilter(textPort);

        dummy();
    }


    private void addDocumentFilter(JTextField text) {
        AbstractDocument document = (AbstractDocument) text.getDocument();
        document.setDocumentFilter(new IntegerFilter());
    }


    private JPanel componentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new MigLayout());
        panel.add(labelIP, "wrap");
        panel.add(textIP, "w 100%,wrap");
        panel.add(labelPort, "wrap");
        panel.add(textPort, "w 100%,wrap");
        panel.add(infoLabel, "center,wrap,gapy 10 10");
        panel.add(initConnectB(), "w 50, h 30,center,gapy 10 10");

        textIP.addKeyListener(new EnterActionKey(connectB));
        textPort.addKeyListener(new EnterActionKey(connectB));
        return panel;
    }


    private void unableToConnet() {
        infoLabel.setForeground(Color.RED);
        infoLabel.setText("Unable connect to server!");
        new MyTimer(infoLabel, 2000);
    }


    private JButton initConnectB() {
        connectB = new JButton("Connect");
        connectB.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    Parser parser = new Parser(textIP.getText(), Integer.parseInt(textPort.getText()));
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            LogInWindow login = new LogInWindow(parser);
                            setVisible(false);
                            login.setVisible(true);
                        }
                    });

                } catch (UnknownHostException uhe) {
                    LOG.warning("Host unknown: " + uhe.getMessage());
                    unableToConnet();
                } catch (IOException ioe) {
                    LOG.warning("Unexpected exception: " + ioe.getMessage());
                    unableToConnet();
                }
            }
        });
        return connectB;
    }


    private void dummy() {
        textIP.setText("192.168.56.101");
        textPort.setText("8882");
    }


    private void initFont() {
        labelIP.setFont(font);
        labelPort.setFont(font);
        connectB.setFont(font);
        textIP.setFont(font);
        textPort.setFont(font);
        infoLabel.setFont(font);
    }
}
