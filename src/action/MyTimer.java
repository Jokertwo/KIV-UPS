package action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.Timer;


/**
 * Timer ktery po urcite dobe schova label
 * 
 * @author Petr
 *
 */
public class MyTimer {
    private static final int DEFAULT_TIME = 4000;


    public MyTimer(JLabel infoLabel) {
        runTimer(infoLabel, DEFAULT_TIME);
    }


    public MyTimer(JLabel infoLabel, int time) {
        runTimer(infoLabel, time);
    }


    private void runTimer(JLabel infoLabel, int time) {
        Timer t = new Timer(time, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                infoLabel.setText(" ");
            }
        });
        t.setRepeats(false);
        t.start();
    }

}
