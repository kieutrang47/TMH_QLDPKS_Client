package application;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import application.form.LoginForm;
import application.form.MainForm;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import raven.toast.Notifications;
import util.PasswordResetManager;
public class Application extends javax.swing.JFrame {
    private static Application instance;
    public static Application app;
    private MainForm mainForm;
    private final LoginForm loginForm;
    
    public Application() {
        initComponents();
        setSize(new Dimension(1366, 768));
        setLocationRelativeTo(null);
        mainForm = new MainForm();
        loginForm = LoginForm.getInstance();
//        getRootPane().putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, true);
        Notifications.getInstance().setJFrame(this);
    }
    
    public static Application getInstance() {
        if (instance == null) {
            instance = new Application();
        }
        return instance;
    }
 // Phương thức này sẽ giúp bạn lấy đối tượng MainForm
    public static MainForm getMainForm() {
        return app.mainForm;
    }
    // Phương thức để thiết lập MainForm
    public static void setMainForm(MainForm newMainForm) {
        app.mainForm = newMainForm;
    }

    public static void showForm(Component component) {
        component.applyComponentOrientation(app.getComponentOrientation());
        app.mainForm.showForm(component);
    }

    public static void login() {
        FlatAnimatedLafChange.showSnapshot();
        app.setContentPane(app.mainForm);
        app.mainForm.applyComponentOrientation(app.getComponentOrientation());
        app.mainForm.setSelectedMenu(0, 0);
        app.mainForm.hideMenu();
        SwingUtilities.updateComponentTreeUI(app.mainForm);
        app.revalidate();
        app.repaint();
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

    public static void logout() {
        FlatAnimatedLafChange.showSnapshot();
        app.setContentPane(app.loginForm);
        app.loginForm.applyComponentOrientation(app.getComponentOrientation());
        SwingUtilities.updateComponentTreeUI(app.loginForm);
        app.revalidate();
        app.repaint();
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }
    

    public static void setSelectedMenu(int index, int subIndex) {
        app.mainForm.setSelectedMenu(index, subIndex);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        pack();
    }

    public static void main(String args[]) {
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("theme");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 16));
       // FlatMacDarkLaf.setup();
        FlatMacLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            app = new Application();
            app.setVisible(false); // Không hiển thị JFrame chính ngay lúc đầu
            // Hiển thị màn hình splash trước
            LoginForm.getInstance().showSplashScreen(() -> {
                // Khi màn hình splash kết thúc, hiển thị JFrame chính
                app.setVisible(true);
                app.setContentPane(app.loginForm);
                app.revalidate();
                app.repaint();
            });
        });
    }
    
}



