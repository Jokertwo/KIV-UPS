package connection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;


/**
 * Trida ktera vytvori spojeni se serverem
 * 
 * Jedna se o konzumenta
 * 
 * Navrhovy vzor producent - konzument (producentem je trida Reciever)
 * 
 * @author Petr A15B0055K
 *
 */
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
        // spousti vlakno ktere prijima zpravy ze serveru (producent)
        listen = new Reciever(streamIn, parser);
        listen.start();
        LOG.info("Listener(thread) begun listen.");

    }


    /**
     * Posle zpravu na server
     * 
     * @param message
     *            zprava ktera se ma poslat
     * @return vraci notifikaci
     */
    public String sendToServer(String message) {
        try {
            if (message.length() < 1023) {
                // write to buffer
                streamOut.write(message + "\n");
                // send to server
                streamOut.flush();
                LOG.info("Send to server : '" + message + "'");
                // wait for notification
                return listen.getNotification();
            } else {
                LOG.info("Too long message : " + message);
            }
        } catch (IOException ioe) {
            LOG.warning("Sending error: " + ioe.getMessage());
        }
        return "ToLong";
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