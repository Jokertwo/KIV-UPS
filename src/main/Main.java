package main;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import gui.ConnectWindow;

public class Main {
    
    public static Map<String,String> codes;
    
    public static Map<String,String> initMap(){
        Map<String,String> temp = new HashMap<>();
        temp.put("all", "1");
        temp.put("private", "2");
        temp.put("ping", "3");
        temp.put("logIn", "4");
        temp.put("logOut", "5");
        temp.put("conUsers", "6");
        temp.put("ok", "7");
        temp.put("error", "8");
        temp.put("separ", ";");
        
        return Collections.unmodifiableMap(temp);
    }
    
    
    
    public static void main(String[] args) {
        codes = initMap();
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
