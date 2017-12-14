package main;

import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import gui.ConnectWindow;

public class Main {
        
    public static boolean TEST = false;
    public static int NUMBER = 0;
    
    public static void main(String[] args) {
        if(args.length > 0){
            try{
            NUMBER = Integer.parseInt(args[0]);
            TEST = true;
            }catch(NumberFormatException e){
                Logger.getLogger(Main.class.getName()).warning("Wrong format of arguments '"+args[0]+"' ,start standart version");
            }
        }
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
