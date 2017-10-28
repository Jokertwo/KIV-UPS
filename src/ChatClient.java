import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class ChatClient {
    private Socket socket = null;
    private Scanner console = null;
    private BufferedWriter streamOut = null;
    private BufferedReader streamIn = null;


    public ChatClient(String serverName, int serverPort) {
        System.out.println("Establishing connection. Please wait ...");
        try {
            socket = new Socket(serverName, serverPort);
            System.out.println("Connected: " + socket);
            start();
            listenToServer();
            writeToServer();
        } catch (UnknownHostException uhe) {
            System.out.println("Host unknown: " + uhe.getMessage());
        } catch (IOException ioe) {
            System.out.println("Unexpected exception: " + ioe.getMessage());
        }      
        stop();
    }
    public void writeToServer(){
        String line = "";
        while (!line.equals(".bye")) {
            try {
                line = console.nextLine();
                streamOut.write(line + "\n");
                streamOut.flush();
            } catch (IOException ioe) {
                System.out.println("Sending error: " + ioe.getMessage());
            }
        }
    }
    public void listenToServer(){
        Thread thread = new Thread(new Runnable() {
            
            @Override
            public void run() {
                while(true){
                    try {
                        if(streamIn.ready()){
                            System.out.println(streamIn.readLine());
                        }
                        Thread.sleep(10);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }catch(InterruptedException e){
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
            System.out.println("Error closing ...");
        }
    }


    public static void main(String args[]) {
        ChatClient client;

        client = new ChatClient("192.168.56.101", Integer.parseInt("8882"));
    }
}