package application.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

import com.formdev.flatlaf.FlatClientProperties;
import application.Application;
import entity.NhanVien;
import entity.TaiKhoan;
import net.miginfocom.swing.MigLayout;
import rmi.NhanVienService;
import rmi.TaiKhoanService;
import rmi.PhongService;
import rmi.ChiTietPhieuDatService;
import rmi.PhieuDatService;
import rmi.KhachHangService;
//import session.TaiKhoanDangNhap;
import util.ApplicationContext;
import util.DataChangeNotifier;
import util.EmailSender;
import util.PasswordResetManager;
import util.PasswordUtils;
import util.TaiKhoanDangNhap;

public class LoginForm extends javax.swing.JPanel {
    private static final Logger LOGGER = Logger.getLogger(LoginForm.class.getName());
    private JWindow splashScreen;
    private TaiKhoan taiKhoanHT;
    private static LoginForm instance;
    private TaiKhoanService taiKhoanService;
    private NhanVienService nhanVienService;
    private PhongService phongService;
    private ChiTietPhieuDatService chiTietPhieuDatService;
    private PhieuDatService phieuDatService;
    private KhachHangService khachHangService;
    private boolean isRmiInitialized = false;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000;

    private int loginAttempts = 0;
    private Timer lockTimer;
    private static final int MAX_ATTEMPTS = 5;

    public LoginForm() {
        // Kết nối RMI và lưu tất cả các service cần thiết
        int retries = 0;
        while (retries < MAX_RETRIES && !isRmiInitialized) {
            try {
                Registry registry = LocateRegistry.getRegistry("192.168.121.163", 1099);
                taiKhoanService = (TaiKhoanService) registry.lookup("TaiKhoanService");
                nhanVienService = (NhanVienService) registry.lookup("NhanVienService");
                phongService = (PhongService) registry.lookup("PhongService");
                chiTietPhieuDatService = (ChiTietPhieuDatService) registry.lookup("ChiTietPhieuDatService");
                phieuDatService = (PhieuDatService) registry.lookup("PhieuDatService");
                khachHangService = (KhachHangService) registry.lookup("KhachHangService");
                System.out.println("Đã kết nối tới tất cả các service qua RMI!");
                isRmiInitialized = true;

                // Lưu vào ApplicationContext
                ApplicationContext appContext = ApplicationContext.getInstance();
                appContext.setTaiKhoanService(taiKhoanService);
                appContext.setNhanVienService(nhanVienService);
                appContext.setPhongService(phongService);
                appContext.setChiTietPhieuDatService(chiTietPhieuDatService);
                appContext.setPhieuDatService(phieuDatService);
                appContext.setKhachHangService(khachHangService);
                appContext.setRmiInitialized(isRmiInitialized);
                break;
            } catch (Exception e) {
                retries++;
                LOGGER.log(Level.WARNING, "Thử kết nối RMI lần " + retries + " thất bại", e);
                if (retries < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        if (!isRmiInitialized) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối RMI sau " + MAX_RETRIES + " lần thử. Vui lòng kiểm tra RMI Server!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            ApplicationContext.getInstance().setRmiInitialized(isRmiInitialized);
            setEnabled(false);
            return;
        }

        initComponents();
        init();
    }

    public static LoginForm getInstance() {
        if (instance == null) {
            instance = new LoginForm();
        }
        return instance;
    }

    public void showSplashScreen(Runnable onSplashFinished) {
        createSplashScreen();
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Thread.sleep(3000);
                return null;
            }

            @Override
            protected void done() {
                splashScreen.setVisible(false);
                splashScreen.dispose();
                SwingUtilities.invokeLater(onSplashFinished);
            }
        };
        splashScreen.setVisible(true);
        worker.execute();
    }

    public void createSplashScreen() {
        splashScreen = new JWindow();
        splashScreen.getContentPane().setBackground(new Color(191, 185, 165));

        JLabel imageLabel = new JLabel(new ImageIcon(getClass().getResource("/icon/logo/loading.jpeg")), SwingConstants.CENTER);
        splashScreen.getContentPane().add(imageLabel, BorderLayout.CENTER);

        ImageIcon originalIcon = (ImageIcon) imageLabel.getIcon();
        Image originalImage = originalIcon.getImage();
        int newWidth = 400;
        int newHeight = 400;
        Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        imageLabel.setIcon(scaledIcon);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        splashScreen.getContentPane().add(progressBar, BorderLayout.SOUTH);

        splashScreen.setBounds(450, 150, 400, 400);
        splashScreen.setVisible(true);

        new Thread(() -> {
            int value = 0;
            while (value <= 100) {
                progressBar.setValue(value);
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                value++;
            }
        }).start();
    }

    private void init() {
        setLayout(new MigLayout("al center center"));
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:$h1.font");
        txtPass.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true; showCapsLock:true");
        cmdLogin.putClientProperty(FlatClientProperties.STYLE, "borderWidth:0; focusWidth:0");
        txtUser.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tên đăng nhập");
        txtPass.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Mật khẩu");
        txtUser.addActionListener(e -> txtPass.requestFocus());
        txtPass.addActionListener(e -> cmdLogin.doClick());

        lbForgotPassword.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openForgotPasswordDialog();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                lbForgotPassword.setForeground(Color.BLUE);
                lbForgotPassword.setText("<html><u>Quên mật khẩu?</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                lbForgotPassword.setForeground(Color.BLACK);
                lbForgotPassword.setText("Quên mật khẩu?");
            }
        });
    }

    private void openForgotPasswordDialog() {
        if (!isRmiInitialized || taiKhoanService == null) {
            JOptionPane.showMessageDialog(this, "Không thể khôi phục mật khẩu vì kết nối RMI thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(1, 2));
        JTextField emailField = new JTextField(10);
        panel.add(new JLabel("Nhập Email của bạn:"));
        panel.add(emailField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Khôi phục mật khẩu", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String email = emailField.getText().trim();
            if (!email.isEmpty()) {
                try {
                    if (taiKhoanService.kiemTraEmailTonTai(email)) {
                        String newPassword = generateRandomPassword();
                        System.out.println("Mật khẩu ngẫu nhiên: " + newPassword);

                        String hashedPassword = PasswordUtils.hashPassword(newPassword);

                        if (taiKhoanService.capNhatMatKhauTheoEmail(email, hashedPassword)) {
                            String subject = "Thông báo cấp lại mật khẩu - TheMoonHotel";
                            String content = "Kính gửi Quý nhân viên của chuỗi khách sạn TheMoonHotel,\n\n" +
                                    "Chúng tôi xin thông báo rằng yêu cầu cấp lại mật khẩu tài khoản của bạn đã được thực hiện thành công.\n\n" +
                                    "Mật khẩu mới của bạn là: " + newPassword + "\n\n" +
                                    "Vui lòng lưu ý rằng mật khẩu này rất quan trọng. Để bảo mật tài khoản của bạn, hãy thực hiện theo các bước sau:\n" +
                                    "1. Đăng nhập vào tài khoản của bạn bằng mật khẩu mới.\n" +
                                    "2. Thay đổi mật khẩu mới ngay sau khi đăng nhập lần đầu để đảm bảo an toàn.\n\n" +
                                    "Nếu bạn cần hỗ trợ, vui lòng liên hệ:\n" +
                                    "Hotline: 1900 88 88 99\n" +
                                    "Email: support@themoonhotel.vn\n\n" +
                                    "Chúng tôi cảm ơn sự hợp tác của bạn và mong bạn giữ tài khoản của mình an toàn.\n\n" +
                                    "Trân trọng,\nTheMoonHotel Team";
                            EmailSender.sendEmail(email, subject, content);

                            JOptionPane.showMessageDialog(this,
                                    "Mật khẩu mới của bạn đã được gửi qua email! Vui lòng kiểm tra email.",
                                    "Thành công",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Không thể cập nhật mật khẩu!",
                                    "Lỗi",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Email này chưa được đăng ký trước đó!",
                                "Thông báo",
                                JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Lỗi khi khôi phục mật khẩu", e);
                    JOptionPane.showMessageDialog(this,
                            "Lỗi hệ thống: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Email không được để trống!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private boolean validateInput(String username, String password) {
        if (username.length() < 3) {
            JOptionPane.showMessageDialog(this,
                    "Tên đăng nhập phải có ít nhất 3 ký tự!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "Mật khẩu phải có ít nhất 6 ký tự!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!username.matches("[a-zA-Z0-9_]+")) {
            JOptionPane.showMessageDialog(this,
                    "Tên đăng nhập chỉ được chứa chữ cái, số và dấu gạch dưới!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void cmdLoginActionPerformed(java.awt.event.ActionEvent evt) {
        // Kiểm tra trạng thái RMI từ ApplicationContext
        isRmiInitialized = ApplicationContext.getInstance().isRmiInitialized();
        taiKhoanService = ApplicationContext.getInstance().getTaiKhoanService();
        nhanVienService = ApplicationContext.getInstance().getNhanVienService();
        phongService = ApplicationContext.getInstance().getPhongService();
        chiTietPhieuDatService = ApplicationContext.getInstance().getChiTietPhieuDatService();
        phieuDatService = ApplicationContext.getInstance().getPhieuDatService();
        khachHangService = ApplicationContext.getInstance().getKhachHangService();

        if (!isRmiInitialized || taiKhoanService == null || nhanVienService == null ||
                phongService == null || chiTietPhieuDatService == null || phieuDatService == null || khachHangService == null) {
            System.out.println("LoginForm: RMI chưa được khởi tạo, thử kết nối lại...");
            int retries = 0;
            while (retries < MAX_RETRIES && !isRmiInitialized) {
                try {
                    Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                    taiKhoanService = (TaiKhoanService) registry.lookup("TaiKhoanService");
                    nhanVienService = (NhanVienService) registry.lookup("NhanVienService");
                    phongService = (PhongService) registry.lookup("PhongService");
                    chiTietPhieuDatService = (ChiTietPhieuDatService) registry.lookup("ChiTietPhieuDatService");
                    phieuDatService = (PhieuDatService) registry.lookup("PhieuDatService");
                    khachHangService = (KhachHangService) registry.lookup("KhachHangService");
                    System.out.println("LoginForm: Đã kết nối lại tất cả các service qua RMI!");
                    isRmiInitialized = true;

                    // Lưu lại vào ApplicationContext
                    ApplicationContext appContext = ApplicationContext.getInstance();
                    appContext.setTaiKhoanService(taiKhoanService);
                    appContext.setNhanVienService(nhanVienService);
                    appContext.setPhongService(phongService);
                    appContext.setChiTietPhieuDatService(chiTietPhieuDatService);
                    appContext.setPhieuDatService(phieuDatService);
                    appContext.setKhachHangService(khachHangService);
                    appContext.setRmiInitialized(isRmiInitialized);
                    break;
                } catch (Exception e) {
                    retries++;
                    LOGGER.log(Level.WARNING, "LoginForm: Thử kết nối RMI lần " + retries + " thất bại", e);
                    if (retries < MAX_RETRIES) {
                        try {
                            Thread.sleep(RETRY_DELAY_MS);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }

            if (!isRmiInitialized) {
                JOptionPane.showMessageDialog(this, "Không thể đăng nhập vì kết nối RMI thất bại sau " + MAX_RETRIES + " lần thử!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        String username = txtUser.getText().trim();
        String password = new String(txtPass.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên tài khoản và mật khẩu không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            resetLogin();
            return;
        }

        if (!validateInput(username, password)) {
            return;
        }

        try {
            System.out.println("Kiểm tra mật khẩu...");
            String hashedPasswordFromDb = taiKhoanService.getHashedPassword(username);

            if (hashedPasswordFromDb == null) {
                JOptionPane.showMessageDialog(this, "Tên tài khoản không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String email = taiKhoanService.getEmailByUsername(username);

            if (PasswordResetManager.getInstance().isValidResetPassword(email, hashedPasswordFromDb)) {
                int option = JOptionPane.showConfirmDialog(this,
                        "Bạn đang sử dụng mật khẩu tạm thời. Vui lòng đổi mật khẩu mới.",
                        "Đổi mật khẩu",
                        JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    showChangePasswordDialog(username);
                } else {
                    return;
                }
            } else if (PasswordUtils.checkPassword(password, hashedPasswordFromDb)) {
                taiKhoanHT = taiKhoanService.layTKTheoUsername(username);
//                TaiKhoanDangNhap.taiKhoan = taiKhoanHT;
                TaiKhoanDangNhap.setTaiKhoan(taiKhoanHT);

                System.out.println("Đăng nhập thành công với tài khoản: " + username);
                NhanVien currentNhanVien = taiKhoanHT.getMaNhanVien();
                if (currentNhanVien != null) {
                    System.out.println("Tên nhân viên: " + currentNhanVien.getHoTenNhanVien());
                    System.out.println("Chức vụ: " + (currentNhanVien.getChucVu() ? "quản lý" : "nhân viên"));

                    MainForm mainForm = Application.getInstance().getMainForm();
                    mainForm.setCurrentNhanVien(currentNhanVien);
                    System.out.println("Đang hiển thị giao diện cho " + (currentNhanVien.getChucVu() ? "quản lý" : "nhân viên"));
                }

                // Thông báo sự thay đổi dữ liệu để GUI_TK làm mới
                DataChangeNotifier.getInstance().notifyListeners();

                Application.login();

                javax.swing.JFrame topFrame = (javax.swing.JFrame) javax.swing.SwingUtilities.getWindowAncestor(this);
                if (topFrame != null) {
                    topFrame.dispose();
                }
            } else {
                loginAttempts++;
                JOptionPane.showMessageDialog(this,
                        "Tên tài khoản hoặc mật khẩu không đúng!\nCòn " + (MAX_ATTEMPTS - loginAttempts) + " lần thử",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);

                if (loginAttempts >= MAX_ATTEMPTS) {
                    lockAccount();
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi xác thực tài khoản", e);
            JOptionPane.showMessageDialog(this, "Lỗi khi xác thực tài khoản: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showChangePasswordDialog(String username) {
        if (!isRmiInitialized || taiKhoanService == null) {
            JOptionPane.showMessageDialog(this, "Không thể đổi mật khẩu vì kết nối RMI thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        JPasswordField newPass = new JPasswordField();
        JPasswordField confirmPass = new JPasswordField();

        panel.add(new JLabel("Mật khẩu mới:"));
        panel.add(newPass);
        panel.add(new JLabel("Xác nhận mật khẩu:"));
        panel.add(confirmPass);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Đổi mật khẩu", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String newPassword = new String(newPass.getPassword());
            String confirmPassword = new String(confirmPass.getPassword());

            if (newPassword.equals(confirmPassword)) {
                try {
                    String hashedNewPassword = PasswordUtils.hashPassword(newPassword);
                    taiKhoanService.updatePassword(username, hashedNewPassword);

                    String email = taiKhoanService.getEmailByUsername(username);
                    PasswordResetManager.getInstance().removeResetRequest(email);

                    JOptionPane.showMessageDialog(this,
                            "Đổi mật khẩu thành công. Vui lòng đăng nhập lại.",
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);

                    resetLogin();
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật mật khẩu", e);
                    JOptionPane.showMessageDialog(this,
                            "Lỗi khi cập nhật mật khẩu: " + e.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Mật khẩu xác nhận không khớp!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                showChangePasswordDialog(username);
            }
        }
    }

    private void lockAccount() {
        lockTimer = new Timer(180000, e -> {
            loginAttempts = 0;
            lockTimer.stop();
        });
        lockTimer.start();
        JOptionPane.showMessageDialog(this,
                "Tài khoản bị khóa tạm thời trong 3 phút. Vui lòng thử lại sau.",
                "Khóa tài khoản",
                JOptionPane.WARNING_MESSAGE);
    }

    private void resetLogin() {
        txtUser.setText("");
        txtPass.setText("");
    }

    // Variables declaration - do not modify
    private javax.swing.JButton cmdLogin;
    private javax.swing.JLabel lbPass;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JLabel lbUser;
    private javax.swing.JLabel lbForgotPassword;
    private application.form.PanelLogin panelLogin1;
    private javax.swing.JPasswordField txtPass;
    private javax.swing.JTextField txtUser;
    private JLabel lbAnimation;
    // End of variables declaration

    @SuppressWarnings("unchecked")
    private void initComponents() {
        panelLogin1 = new application.form.PanelLogin();
        lbTitle = new javax.swing.JLabel();
        lbUser = new javax.swing.JLabel();
        txtUser = new javax.swing.JTextField();
        lbPass = new javax.swing.JLabel();
        txtPass = new javax.swing.JPasswordField();
        cmdLogin = new javax.swing.JButton();
        lbForgotPassword = new javax.swing.JLabel();

        lbAnimation = new javax.swing.JLabel();
        lbAnimation.setBackground(new Color(247, 247, 246));

        URL imgURL = getClass().getResource("/icon/banner/Business Analysis.gif");
        if (imgURL != null) {
            javax.swing.ImageIcon icon = new javax.swing.ImageIcon(imgURL);
            lbAnimation.setIcon(icon);
            lbAnimation.setPreferredSize(new java.awt.Dimension(icon.getIconWidth(), icon.getIconHeight()));
            lbAnimation.setLayout(null);
        } else {
            lbAnimation.setText("Hình ảnh động không tìm thấy.");
        }

        lbTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbTitle.setText("Đăng nhập");

        panelLogin1.add(lbTitle);

        panelLogin1.setBackground(new Color(238, 237, 238));
        this.setBackground(new Color(254, 254, 254));

        lbUser.setText("Tên đăng nhập");
        lbUser.setPreferredSize(new java.awt.Dimension(100, 25));
        panelLogin1.add(lbUser);
        panelLogin1.add(txtUser);

        lbPass.setText("Mật khẩu");
        lbPass.setPreferredSize(new java.awt.Dimension(100, 25));
        panelLogin1.add(lbPass);
        panelLogin1.add(txtPass);

        cmdLogin.setText("Đăng nhập");
        cmdLogin.setPreferredSize(new java.awt.Dimension(200, 40));
        cmdLogin.setBackground(new Color(0, 123, 255));
        cmdLogin.setForeground(Color.white);
        cmdLogin.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        cmdLogin.addActionListener(this::cmdLoginActionPerformed);
        panelLogin1.add(cmdLogin);

        lbForgotPassword.setText("Quên mật khẩu?");
        lbForgotPassword.setForeground(Color.DARK_GRAY);
        lbForgotPassword.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        panelLogin1.add(lbForgotPassword);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(50, Short.MAX_VALUE)
                                .addComponent(lbAnimation)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(panelLogin1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(50, 50, 50))
        );

        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap(50, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lbAnimation)
                                        .addComponent(panelLogin1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(50, Short.MAX_VALUE))
        );
    }
}