import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import net.miginfocom.swing.MigLayout;


public class ChatWindow extends JFrame {

    private JTabbedPane tabbedPane = new JTabbedPane();
    private JTree users;
    private Map<String,Tabbed> listOfOpenWidows = new HashMap<>();

    /**
     * 
     */
    private static final long serialVersionUID = -3880026026104218593L;


    public ChatWindow() {
        initTree();
        addUser("All");
        setLayout(new MigLayout());
        setSize(600, 500);
        add(tabbedPane, "w 80% , h 100%");
        add(users, " w 20%, h 100%");
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        addUser("Petr");
    }
    
    public Map<String,Tabbed> getListOfOpenWindows(){
        return listOfOpenWidows;
    }


    public void addTab(String name) {
        Tabbed tab = new Tabbed(tabbedPane,name);
        tabbedPane.add(name, tab);
        int count = tabbedPane.getTabCount();
        count = (count > 0) ? count - 1 : count;
        tabbedPane.setSelectedIndex(count);
        listOfOpenWidows.put(name,tab);
    }


    public void addUser(String name) {
        DefaultTreeModel model = (DefaultTreeModel) users.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.add(new DefaultMutableTreeNode(name));
        model.reload(root);
    }


    private void initTree() {

        // create the root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("");

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
    }


    public static void main(String[] args) {
     
        try {
            // Set System L&F
            UIManager.setLookAndFeel(
                "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (UnsupportedLookAndFeelException e) {
            // handle exception
        } catch (ClassNotFoundException e) {
            // handle exception
        } catch (InstantiationException e) {
            // handle exception
        } catch (IllegalAccessException e) {
            // handle exception
        }
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new ChatWindow();
            }
        });

    }

}
