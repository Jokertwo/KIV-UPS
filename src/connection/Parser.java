package connection;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import action.ShutdownServer;
import constants.Constants;
import gui.ChatWindow;
import gui.Tabbed;


public class Parser {

    private static final Logger LOG = Logger.getLogger(Parser.class.getName());
    private Comunicator com;
    private String name;
    private JTree users;
    private ChatWindow window;


    public Parser(String serverName, int serverPort) throws UnknownHostException, IOException {
        com = new Comunicator(serverName, serverPort, this);
    }


    private Map<String, Tabbed> getTabMap() {
        return ChatWindow.listOfOpenWidows;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }


    public void setChatWindow(ChatWindow window) {
        this.window = window;
    }


    public void setUsers(JTree users) {
        this.users = users;
    }


    public String logIn(String name) {
        return com.sendToServer(appendSize(Constants.LOG_IN + name));
    }


    public String logOut() {
        return com.sendToServer(appendSize(Constants.LOG_OUT + this.name));
    }


    public String sendPrivateMessage(String toUser, String message) {
        return com.sendToServer(appendSize(
            Constants.PRIVATE + toUser + Constants.SEPARATOR + this.name + Constants.SEPARATOR + message));
    }


    public String sendPublicMessage(String message) {
        return com.sendToServer(appendSize(Constants.ALL + this.name + Constants.SEPARATOR + message));
    }


    public void recievePrivateMessage(String message) {
        int index = message.indexOf(Constants.SEPARATOR);
        message = message.substring(index + 1, message.length());
        index = message.indexOf(Constants.SEPARATOR);
        String[] splitMessage = { message.substring(0, index), message.substring(index + 1, message.length()) };
        if (getTabMap().containsKey(splitMessage[0])) {
            getTabMap().get(splitMessage[0]).getForReading().append(splitMessage[0] + " : " + splitMessage[1] + "\n");
        } else {
            window.addTab(splitMessage[0], splitMessage[0].equals(Constants.CHAT_ALL));
            getTabMap().get(splitMessage[0]).getForReading().append(splitMessage[0] + " : " + splitMessage[1] + "\n");
        }
    }


    private void recievePublicMessage(String message) {
        int index = message.indexOf(Constants.SEPARATOR);
        String fromName = message.substring(1, index);
        message = message.substring(index + 1, message.length());
        if (getTabMap().containsKey(Constants.CHAT_ALL)) {
            getTabMap().get(Constants.CHAT_ALL).getForReading().append(fromName + " : " + message + "\n");
        } else {
            window.addTab(Constants.CHAT_ALL, true);
            getTabMap().get(Constants.CHAT_ALL).getForReading().append(fromName + " : " + message + "\n");
        }
    }


    public void parseMessage(String message) {
        int i = Character.getNumericValue(message.charAt(0));

        if (i < 0) {
            LOG.warning("Can't parse message : '" + message + "'");
            return;
        }
        switch (i) {
            case 1:
                LOG.info("Recieve public message from server.");
                recievePublicMessage(message);
                break;
            case 2:
                LOG.info("Recieve private message from server.");
                recievePrivateMessage(message);
                break;
            case 6:
                LOG.info("Recieve user list from server.");
                splitUserList(message);
                break;
            case 9:
                LOG.info("Server will be shotDown so ByeBye :)");
                new ShutdownServer(com);
                break;
            default:
                LOG.warning("Unexpected message : " + message);
                break;
        }

    }


    private String appendSize(String line) {
        StringBuilder sb = new StringBuilder();
        String a = String.valueOf(line.length() + 1 + 4);

        if (a.length() < 5) {
            for (int i = 0; i < 4 - a.length(); i++) {
                sb.append("0");
            }
            sb.append(a);
        }
        return sb.toString() + line;
    }


    private void splitUserList(String message) {
        cleanTree();
        addUser(Constants.CHAT_ALL);
        String sub = message.substring(1, message.length());
        if (sub.length() > 0) {
            String[] users = sub.split(Constants.SEPARATOR);
            for (String temp : users) {
                addUser(temp);
            }
        }
    }


    private void cleanTree() {
        DefaultTreeModel model = (DefaultTreeModel) users.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.removeAllChildren();
    }


    private void addUser(String name) {
        DefaultTreeModel model = (DefaultTreeModel) users.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.add(new DefaultMutableTreeNode(name));
        model.reload(root);
    }

}
