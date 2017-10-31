package GUI;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import net.miginfocom.swing.MigLayout;

public class ConnectWindow extends JFrame{

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
    }
    
    private JPanel componentPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new MigLayout());
        panel.add(labelIP,"wrap");
        panel.add(textIP,"w 100%,wrap");
        panel. add(labelPort,"wrap");
        panel.add(textPort,"w 100%,wrap");
        panel.add(infoLabel, "center,wrap,gapy 10 10");
        panel.add(initConnectB(),"w 50, h 30,center,gapy 10 10");
        return panel;
    }
    
    private JButton initConnectB(){
        connectB = new JButton("Connect");
        connectB.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                LogInWindow login = new LogInWindow();
                setVisible(false);
                login.setVisible(true);
                
            }
        });
        return connectB;
    }
    

    
    private void initFont(){
        labelIP.setFont(font);
        labelPort.setFont(font);
        connectB.setFont(font);
        textIP.setFont(font);
        textPort.setFont(font);
        infoLabel.setFont(font);
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
                new ConnectWindow();
            }
        });

    }
}
