import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import net.miginfocom.swing.MigLayout;


public class MainWindow extends JFrame {

    JTabbedPane tabbedPane = new JTabbedPane();
    /**
     * 
     */
    private static final long serialVersionUID = -3880026026104218593L;


    public MainWindow() {
        setLayout(new MigLayout());
        setSize(500, 500);
        add(addTab("All"), "w 100% , h 100%");
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public JTabbedPane addTab(String name) {
        tabbedPane.add(name, new Tabbed(tabbedPane));
        tabbedPane.setSelectedIndex(0);
        return tabbedPane;
    }


    public static void main(String[] args) {
        new MainWindow();
    }

}
