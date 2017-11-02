package gui;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import connection.Parser;
import net.miginfocom.swing.MigLayout;


public class ChatWindow extends JFrame {

    private JTabbedPane tabbedPane = new JTabbedPane();
    private JTree users;
    public static Map<String,Tabbed> listOfOpenWidows = new HashMap<>();

    
    private static final Logger log = Logger.getLogger(ChatWindow.class.getName());
    
    /**
     * 
     */
    private static final long serialVersionUID = -3880026026104218593L;
    private final Parser parser;

    public ChatWindow(Parser parser) {
        this.parser = parser;
        setTitle("Chat");
        initTree();
        parser.setUsers(users);
        parser.setChatWindow(this);
        setLayout(new MigLayout());
        setSize(600, 500);
        add(tabbedPane, "w 80% , h 100%");
        add(users, " w 20%, h 100%");       
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
//        addTab("All");
    }
    
    public Map<String,Tabbed> getListOfOpenWindows(){
        return listOfOpenWidows;
    }


    public void addTab(String addressee) {
        Tabbed tab = new Tabbed(tabbedPane,addressee,parser);
        tabbedPane.add(addressee, tab);
        int count = tabbedPane.getTabCount();
        count = (count > 0) ? count - 1 : count;
        tabbedPane.setSelectedIndex(count);
        listOfOpenWidows.put(addressee,tab);
        log.info("Open new tab for user : " + addressee);
    }

    private void initTree() {

        // create the root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("");
        root.add(new DefaultMutableTreeNode("All"));
        users = new JTree(root);
        users.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 19));
        users.setRootVisible(false);

        users.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) users.getLastSelectedPathComponent();
                    if (node == null)
                        return;
                    String name = node.getUserObject().toString();

                    if (!listOfOpenWidows.containsKey(name)) {
                        addTab(name);
                    }
                }
            }
        });
        log.info("Inicialization tree with users");
    }


    

}
