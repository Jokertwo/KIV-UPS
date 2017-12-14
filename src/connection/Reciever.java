package connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Logger;
import constants.Constants;


public class Reciever extends Thread {

    private static final Logger LOG = Logger.getLogger(Reciever.class.getName());

    private Vector<String> messages = new Vector<>();
    private BufferedReader streamIn;
    private Parser parser;
    String temp;


    public Reciever(BufferedReader streamIn, Parser parser) {
        this.streamIn = streamIn;
        this.parser = parser;
    }


    @Override
    public void run() {
        while (true) {
            try {
                storeNotification();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    private synchronized String getFromServer() {
        try {
            if (streamIn.ready()) {
                return streamIn.readLine();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


    private synchronized void storeNotification() throws InterruptedException {
        temp = getFromServer();
        if (temp != null) {
            LOG.info("Recieve from serever : '" + temp + "'");
            if (temp.charAt(0) == Constants.ERROR.toCharArray()[0] || temp.equals(Constants.OK)) {
                messages.add(temp);
                notify();
            } else {
                parser.parseMessage(temp);
                if (temp.equals(Constants.SHUTDOWN)) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }


    public synchronized String getNotification() {
        int counter = 0;
        try {
            while (messages.size() < 1) {
                counter++;
                if (counter == 20) {
                    break;
                }
                wait(100);
            }
            if (messages.size() > 0) {
                String temp = messages.firstElement();
                messages.remove(temp);
                return temp;
            } else {
                LOG.warning("Waiting for notityfication was to long. Return ERROR");
                return Constants.ERROR;
            }

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Constants.ERROR;
    }

}
