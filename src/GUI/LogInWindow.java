package GUI;
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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
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


    public LogInWindow() {
        log.info("Open login window.");
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
        loginP.add(loginB,"center, w 60%");
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
                if (loginNameTF.getText().length() >= MAXIMUM_LENGTH_OF_NICKNAME) { // limit textfield  characters
                    e.consume();
                    infoLabel.setText(toLongNick);
                    infoLabel.setForeground(Color.RED);
                    log.info("User try type too long nickname.");
                }else{
                    infoLabel.setText(" ");
                }
            }
        });

    }
    
    private void initButton(){
        loginB.setFont(font);
        loginB.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                ChatWindow chat = new ChatWindow();
                chat.setVisible(true);
                log.info("Create and showing ChatWindow.");
                setVisible(false);       
                log.info("Hidding LogIn window");
            }
        });
    }


    public static void main(String[] args) {
        
        try {
            // Set System L&F
            UIManager.setLookAndFeel(
                "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (UnsupportedLookAndFeelException e) {
            // handle exception
        } catch (ClassNotFoundException e) {
            // handle exception
        } catch (InstantiationException e) {
            // handle exception
        } catch (IllegalAccessException e) {
            // handle exception
        }
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new LogInWindow();
            }
        });

    }

}
