package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import action.EnterActionKey;
import connection.Parser;
import documentFilters.SemicolonsFilter;
import main.Main;
import net.miginfocom.swing.MigLayout;


public class LogInWindow extends JFrame {

    private static final Logger log = Logger.getLogger(LogInWindow.class.getName());

    private final String toLongNick = "Maximum length of nick is 8.";
    private static final long serialVersionUID = 1L;
    private final int MAXIMUM_LENGTH_OF_NICKNAME = 8;
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
        loginNameTF.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if (loginNameTF.getText().length() >= MAXIMUM_LENGTH_OF_NICKNAME) { // limit textfield characters
                    e.consume();
                    infoLabel.setText(toLongNick);
                    infoLabel.setForeground(Color.RED);
                    log.info("User try type too long nickname.");
                } else {
                    infoLabel.setText(" ");
                }
            }
        });
        loginNameTF.addKeyListener(new EnterActionKey(loginB));
        AbstractDocument doc = (AbstractDocument)loginNameTF.getDocument();
        doc.setDocumentFilter(new SemicolonsFilter());
    }


    private void initButton() {
        loginB.setFont(font);
        loginB.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String userName = loginNameTF.getText();
                String responseFromServer = parser.logIn(userName);
                if (responseFromServer.equals(Main.codes.get("ok"))) {
                    ChatWindow chat = new ChatWindow(parser);
                    chat.setVisible(true);
                    log.info("Create and showing ChatWindow.");
                    setVisible(false);
                    log.info("Hidding LogIn window");
                    parser.setName(userName);
                }
                else if(responseFromServer.equals(Main.codes.get("error"))){
                    infoLabel.setForeground(Color.RED);
                    infoLabel.setText("The name is already in use...");
                }
            }
        });
    }

}
