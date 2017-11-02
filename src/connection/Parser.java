package connection;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import gui.ChatWindow;
import gui.Tabbed;
import main.Main;


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


    public void setChatWindow(ChatWindow window) {
        this.window = window;
    }


    public void setUsers(JTree users) {
        this.users = users;
    }


    public String logIn(String name) {
        return com.sendToServer(Main.codes.get("logIn") + name);
    }


    public String logOut(String name) {
        return com.sendToServer(Main.codes.get("logOut") + name);
    }


    public String sendPrivateMessage(String toUser, String message) {
        return com.sendToServer(
            Main.codes.get("private") + toUser + Main.codes.get("sep") + this.name + Main.codes.get("sep") + message);
    }


    public String sendPublicMessage(String message) {
        return com.sendToServer(Main.codes.get("all") + this.name + Main.codes.get("sep") + message);
    }


    public void recievePrivateMessage(String message) {
        int index = message.indexOf(Main.codes.get("sep"));
        message = message.substring(index + 1, message.length());
        index = message.indexOf(Main.codes.get("sep"));
        String[] splitMessage = { message.substring(0, index), message.substring(index + 1, message.length()) };
        if (getTabMap().containsKey(splitMessage[0])) {
            getTabMap().get(splitMessage[0]).getForReading().append(splitMessage[0] + " : " + splitMessage[1] + "\n");
        } else {
            window.addTab(splitMessage[0]);
            getTabMap().get(splitMessage[0]).getForReading().append(splitMessage[0] + " : " + splitMessage[1] + "\n");
        }
    }


    private void recievePublicMessage(String message) {
        int index = message.indexOf(Main.codes.get("sep"));
        String fromName = message.substring(1, index);
        message = message.substring(index + 1, message.length());
        if (getTabMap().containsKey("All")) {
            getTabMap().get("All").getForReading().append(fromName + " : " + message + "\n");
        } else {
            window.addTab("All");
            getTabMap().get("All").getForReading().append(fromName + " : " + message + "\n");
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
                recievePublicMessage(message);
                break;
            case 2:
                recievePrivateMessage(message);
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                splitUserList(message);
                break;
            case 7:
                break;
            case 8:
                break;
        }

    }


    private void splitUserList(String message) {
        String sub = message.substring(1, message.length());
        String[] users = sub.split(Main.codes.get("sep"));
        cleanTree();
        addUser("All");
        for (String temp : users) {
            addUser(temp);
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
