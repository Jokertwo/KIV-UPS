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

    private BufferedWriter streamOut = null;
    private BufferedReader streamIn = null;
    private final Parser parser;


    public Comunicator(String serverName, int serverPort,Parser parser) throws UnknownHostException, IOException {

        LOG.info("Establishing connection. Please wait ...");
        
            socket = new Socket(serverName, serverPort);
            this.parser = parser;
            LOG.info("Connected: " + socket);
            start();
            listenToServer();
        
    }
   


    public String sendToServer(String line) {
        try {
            if(line.length() < 1024){
                LOG.info("Sending to server : " + line);
            streamOut.write(line + "\n");
            streamOut.flush();
            return streamIn.readLine();
            }
            else{
                LOG.info("Too long message : " + line);
            }
        } catch (IOException ioe) {
            LOG.warning("Sending error: " + ioe.getMessage());
        }
        return null;
    }


    public void listenToServer() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        if (streamIn.ready()) {
                            parser.parseMessage(streamIn.readLine());
                        }
                        Thread.sleep(10);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        });
        thread.start();

    }

    public void start() throws IOException {
        streamOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        streamIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }


    public void stop() {
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