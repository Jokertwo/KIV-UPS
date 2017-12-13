package connection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;


public class Comunicator {
    private static final Logger LOG = Logger.getLogger(Comunicator.class.getName());
    private Socket socket = null;

    private BufferedWriter streamOut;
    private BufferedReader streamIn;
    Reciever listen;


    public Comunicator(String serverName, int serverPort, Parser parser) throws UnknownHostException, IOException {

        LOG.info("Establishing connection. Please wait ...");
        socket = new Socket(serverName, serverPort);
        LOG.info("Connected: " + socket);

        start();
        listen = new Reciever(streamIn, parser);
        listen.start();
        LOG.info("Listener(thread) begun listen.");

    }


    public String sendToServer(String line) {
        try {
            if (line.length() < 1023) {
                // write to buffer
                streamOut.write(line + "\n");
                // send to server
                streamOut.flush();
                LOG.info("Send to server : '" + line + "'");
                // wait for notification
                return listen.getNotification();
            } else {
                LOG.info("Too long message : " + line);
            }
        } catch (IOException ioe) {
            LOG.warning("Sending error: " + ioe.getMessage());
        }
        return null;
    }


    public void start() throws IOException {
        streamOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        streamIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }


    public void stop() {
        LOG.info("Closing stream");
        try {
            if (streamOut != null)
                streamOut.close();
            if (socket != null)
                socket.close();
        } catch (IOException ioe) {
            LOG.warning("Error closing ...");
        }
    }

}