package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;
import action.EnterActionKey;
import action.MaxLengthAction;
import connection.Parser;
import constants.Constants;
import documentFilters.SemicolonsFilter;
import documentListener.CheckEmptyTextField;
import net.miginfocom.swing.MigLayout;


public class LogInWindow extends JFrame {

    private static final Logger log = Logger.getLogger(LogInWindow.class.getName());

    private final String toLongNick = "Maximum length of nick is 8.";
    private static final long serialVersionUID = 1L;
    private static final int MAXIMUM_LENGTH_OF_NICKNAME = 8;
    private final String loginS = "LogIn";

    private JLabel loginNameL = new JLabel("Login name :");
    private JTextField loginNameTF = new JTextField();
    private JLabel infoLabel = new JLabel(" ");
    private JButton loginB = new JButton(loginS);

    private Font font = new Font(Font.MONOSPACED, Font.PLAIN, 20);

    private final Parser parser;


    public LogInWindow(Parser parser) {
        log.info("Open login window.");
        this.parser = parser;
        add(panelLogin());
        initLabels();
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Login to server");
        initTextField();
        setLocationRelativeTo(null);
        initButton();
    }


    private JPanel panelLogin() {
        JPanel loginP = new JPanel();
        loginP.setLayout(new MigLayout());
        loginP.add(loginNameL, "wrap, gapy 20 0");
        loginP.add(loginNameTF, "w 100% , wrap");
        loginP.add(infoLabel, "center, wrap , gapy 50 50");
        loginP.add(loginB, "center, w 60%");
        return loginP;
    }


    private void initLabels() {
        loginNameL.setFont(font);
        infoLabel.setFont(font);
    }


    private void initTextField() {
        loginNameTF.setFont(font);
        loginNameTF.setToolTipText("Write here your nickname.");
        loginNameTF.addKeyListener(new MaxLengthLoginAction(loginNameTF, MAXIMUM_LENGTH_OF_NICKNAME));
        loginNameTF.addKeyListener(new EnterActionKey(loginB));
        loginNameTF.getDocument().addDocumentListener(new CheckEmptyTextField(loginB));
        ((AbstractDocument) loginNameTF.getDocument()).setDocumentFilter(new SemicolonsFilter());
    }


    private void initButton() {
        loginB.setEnabled(false);
        loginB.setFont(font);
        loginB.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String userName = loginNameTF.getText();
                String responseFromServer = parser.logIn(userName);
                if (responseFromServer.equals(Constants.OK)) {
                    parser.setName(userName);
                    log.info("Create and showing ChatWindow.");
                    ChatWindow chat = new ChatWindow(parser);
                    chat.setVisible(true);
                    setVisible(false);
                    log.info("Hidding LogIn window");
                } else {
                    handleError(responseFromServer);
                }
            }
        });
    }


    private void handleError(String response) {
        infoLabel.setForeground(Color.RED);
        if (response.equals(Constants.ERROR)) {
            log.warning("Unexpected default error here!");
            return;
        }
        if (response.charAt(1) == Constants.ERROR_MAX_USERS) {
            infoLabel.setText("Max capacity of server has been reached.");
            log.info("Cant connect to server. Maximum capacity of server has been reached.");
        }
        if (response.charAt(1) == Constants.ERROR_USER_EXIST) {
            infoLabel.setText("The name is already in use...");
            log.info("Cant connect to server because the name/nick is already is use");
        }
        else{
            log.warning("Cant parse response from server : " + response);
            return;
        }
        hideText();
    }


    private void hideText() {
        Timer t = new Timer(4000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                infoLabel.setText(" ");
            }
        });
        t.setRepeats(false);
        t.start();
    }

    private class MaxLengthLoginAction extends MaxLengthAction {

        public MaxLengthLoginAction(JTextComponent component, int maxLength) {
            super(component, maxLength);
        }


        @Override
        public void keyTyped(KeyEvent e) {
            if (component.getText().length() >= maxLength) { // limit textfield characters
                component.setText(component.getText().substring(0, component.getText().length() - 1));
                infoLabel.setText(toLongNick);
                infoLabel.setForeground(Color.RED);
                log.info("User try type too long nickname.");
                hideText();
            }
        }

    }

}
