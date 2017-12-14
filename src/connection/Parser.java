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


/**
 * Mezi kus mezi GUI a komunikaci se serverem naformatuje zpravu do potrebneho tvaru nebo naopak zpracuje prijatou
 * zpravu
 * 
 * @author Petr A15B0055K
 *
 */
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


    /**
     * Prihlaseni na server tvar zpravy >> 4name <<
     * 
     * @param name
     *            jmeno pod kterym se prihlasuji
     * @return vraci notifikaci
     */
    public String logIn(String name) {
        return com.sendToServer(appendSize(Constants.LOG_IN + name));
    }


    /**
     * Odhlaseni ze serveru tvar zpravy >> 5name <<
     * 
     * @return vraci notifikaci
     */
    public String logOut() {
        return com.sendToServer(appendSize(Constants.LOG_OUT + this.name));
    }


    /**
     * Posle soukromou zpravu danemu uzivateli, tvar zpravy >> 2toUser;name;message <<
     * 
     * @param toUser
     *            komu se ma zprava poslat
     * @param message
     *            zprava ktera se ma poslat
     * @return vraci notifikaci
     */
    public String sendPrivateMessage(String toUser, String message) {
        return com.sendToServer(appendSize(
            Constants.PRIVATE + toUser + Constants.SEPARATOR + this.name + Constants.SEPARATOR + message));
    }


    /**
     * Posle verejnou zpravu vsem uzivatelum, tvar zpravy >> 1name;message
     * 
     * @param message
     *            zprava ktera se ma poslat
     * @return vraci notifikaci
     */
    public String sendPublicMessage(String message) {
        return com.sendToServer(appendSize(Constants.ALL + this.name + Constants.SEPARATOR + message));
    }


    /**
     * Zpracuje soukromou zpravu. Rozparsuje ji a preda gui
     * 
     * @param message
     *            zprava prijata ze serveru
     */
    public void recievePrivateMessage(String message) {
        int index = message.indexOf(Constants.SEPARATOR);
        message = message.substring(index + 1, message.length());
        index = message.indexOf(Constants.SEPARATOR);
        String[] splitMessage = { message.substring(0, index), message.substring(index + 1, message.length()) };
        if (getTabMap().containsKey(splitMessage[0])) {
            getTabMap().get(splitMessage[0]).appendText(splitMessage[0] + " : " + splitMessage[1] + "\n");
        } else {
            window.addTab(splitMessage[0], splitMessage[0].equals(Constants.CHAT_ALL));
            getTabMap().get(splitMessage[0]).appendText(splitMessage[0] + " : " + splitMessage[1] + "\n");
        }
    }


    /**
     * Zpracuje verejnou/public zpravu. Rozparsuje ji a preda gui.
     * 
     * @param message
     *            zprava prijata ze serveru
     */
    private void recievePublicMessage(String message) {
        int index = message.indexOf(Constants.SEPARATOR);
        String fromName = message.substring(1, index);
        message = message.substring(index + 1, message.length());
        if (getTabMap().containsKey(Constants.CHAT_ALL)) {
            getTabMap().get(Constants.CHAT_ALL).appendText(fromName + " : " + message + "\n");
        } else {
            window.addTab(Constants.CHAT_ALL, true);
            getTabMap().get(Constants.CHAT_ALL).appendText(fromName + " : " + message + "\n");
        }
    }


    /**
     * Identifikuje druh zpravy prijate ze serveru
     * 
     * @param message
     *            zprava prijata ze serveru
     */
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


    /**
     * Spocita jak je zprava velka a pripoji tuto informaci pred zpravu. Pripojuje vzdy 4-mistne cislo. Pokud je cislo
     * mensi doplni nuly
     * 
     * @param message
     *            zprava ktera se ma poslat na server
     * @return vraci puvodni ypravu obohacenou o infomaci o tom jak je velka/dlouha
     */
    private String appendSize(String message) {
        StringBuilder sb = new StringBuilder();
        String a = String.valueOf(message.length() + 1 + 4);

        if (a.length() < 5) {
            for (int i = 0; i < 4 - a.length(); i++) {
                sb.append("0");
            }
            sb.append(a);
        }
        return sb.toString() + message;
    }


    /**
     * Zpracuje zpravu obsahujici seznam aktualne pripojenych clientu. A sestavy strom kde jsou clienti ulozeni
     * 
     * @param message
     */
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


    /**
     * Vycisti strom / vymaze ho
     */
    private void cleanTree() {
        DefaultTreeModel model = (DefaultTreeModel) users.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.removeAllChildren();
    }


    /**
     * Prida clienta do stromu
     * 
     * @param name
     *            jmeno clienta
     */
    private void addUser(String name) {
        DefaultTreeModel model = (DefaultTreeModel) users.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.add(new DefaultMutableTreeNode(name));
        model.reload(root);
    }

}
