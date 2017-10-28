import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Logger;


public class Comunicator {
    private static final Logger LOG = Logger.getLogger(Comunicator.class.getName());
    private Socket socket = null;
    private Scanner console = null;
    private BufferedWriter streamOut = null;
    private BufferedReader streamIn = null;


    public Comunicator(String serverName, int serverPort) {
        LOG.info("Establishing connection. Please wait ...");
        try {
            socket = new Socket(serverName, serverPort);
            LOG.info("Connected: " + socket);
            start();
            listenToServer();
        } catch (UnknownHostException uhe) {
            LOG.warning("Host unknown: " + uhe.getMessage());
        } catch (IOException ioe) {
            LOG.warning("Unexpected exception: " + ioe.getMessage());
        }
    }


    public void writeToServer(String line) {
        try {
            streamOut.write(line + "\n");
            streamOut.flush();
        } catch (IOException ioe) {
            LOG.warning("Sending error: " + ioe.getMessage());
        }

    }


    public void listenToServer() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        if (streamIn.ready()) {
                            System.out.println(streamIn.readLine());
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
        console = new Scanner(System.in);
        streamOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        streamIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }


    public void stop() {
        try {
            if (console != null)
                console.close();
            if (streamOut != null)
                streamOut.close();
            if (socket != null)
                socket.close();
        } catch (IOException ioe) {
            LOG.warning("Error closing ...");
        }
    }

}