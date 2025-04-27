package gui;

import javax.swing.*;

import entity.NhanVien;
import entity.TaiKhoan;
import rmi.TaiKhoanService;
import util.ApplicationContext;
import util.PasswordUtils;
import util.TaiKhoanDangNhap;
import com.formdev.flatlaf.FlatClientProperties;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GUI_DoiMatKhau extends javax.swing.JPanel {
    private static final Logger LOGGER = Logger.getLogger(GUI_DoiMatKhau.class.getName());
    private TaiKhoanService taiKhoanService;
    private boolean isRmiInitialized = false;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000;

    class ImagePanel extends JPanel {
        private Image backgroundImage;

        public ImagePanel(String imagePath) {
            try {
                backgroundImage = new ImageIcon(imagePath).getImage();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Không thể tải ảnh nền: " + imagePath, e);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    private ImagePanel jPanel1;

    public GUI_DoiMatKhau() {
        taiKhoanService = ApplicationContext.getInstance().getTaiKhoanService();
        isRmiInitialized = ApplicationContext.getInstance().isRmiInitialized();

        if (!isRmiInitialized || taiKhoanService == null) {
            System.out.println("GUI_DoiMatKhau: ApplicationContext không có service, thử kết nối RMI...");
            int retries = 0;
            while (retries < MAX_RETRIES && !isRmiInitialized) {
                try {
                    Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                    taiKhoanService = (TaiKhoanService) registry.lookup("TaiKhoanService");
                    System.out.println("GUI_DoiMatKhau: Đã kết nối tới TaiKhoanService qua RMI!");
                    isRmiInitialized = true;
                    ApplicationContext.getInstance().setTaiKhoanService(taiKhoanService);
                    ApplicationContext.getInstance().setRmiInitialized(isRmiInitialized);
                    break;
                } catch (Exception e) {
                    retries++;
                    LOGGER.log(Level.WARNING, "GUI_DoiMatKhau: Thử kết nối RMI lần " + retries + " thất bại", e);
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
                setEnabled(false);
                return;
            }
        } else {
            System.out.println("GUI_DoiMatKhau: Sử dụng TaiKhoanService từ ApplicationContext");
        }

        jPanel1 = new ImagePanel("src//icon//tabCustom//tk.jpg");
        initComponents();
        SwingUtilities.invokeLater(() -> loadNhanVienData());

        btnYes.addActionListener(e -> {
            try {
                changePassword();
            } catch (Exception e1) {
                LOGGER.log(Level.SEVERE, "Lỗi khi đổi mật khẩu", e1);
                JOptionPane.showMessageDialog(this, "Lỗi khi đổi mật khẩu: " + e1.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void changePassword() throws Exception {
        isRmiInitialized = ApplicationContext.getInstance().isRmiInitialized();
        taiKhoanService = ApplicationContext.getInstance().getTaiKhoanService();

        if (!isRmiInitialized || taiKhoanService == null) {
            System.out.println("GUI_DoiMatKhau: RMI chưa được khởi tạo, thử kết nối lại...");
            int retries = 0;
            while (retries < MAX_RETRIES && !isRmiInitialized) {
                try {
                    Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                    taiKhoanService = (TaiKhoanService) registry.lookup("TaiKhoanService");
                    System.out.println("GUI_DoiMatKhau: Đã kết nối lại TaiKhoanService qua RMI!");
                    isRmiInitialized = true;
                    ApplicationContext.getInstance().setTaiKhoanService(taiKhoanService);
                    ApplicationContext.getInstance().setRmiInitialized(isRmiInitialized);
                    break;
                } catch (Exception e) {
                    retries++;
                    LOGGER.log(Level.WARNING, "GUI_DoiMatKhau: Thử kết nối RMI lần " + retries + " thất bại", e);
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
                JOptionPane.showMessageDialog(this, "Không thể đổi mật khẩu vì kết nối RMI thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (TaiKhoanDangNhap.getTaiKhoan() == null) {
            JOptionPane.showMessageDialog(this, "Bạn chưa đăng nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String currentPassword = new String(txtCurrentPass.getPassword());
        String newPassword = new String(txtNewPass.getPassword());
        String confirmPassword = new String(txtRePass.getPassword());

        if (!validatePassword(currentPassword, newPassword, confirmPassword)) {
            clearFields();
            return;
        }

        String hashedNewPassword = PasswordUtils.hashPassword(newPassword);
        TaiKhoanDangNhap.getTaiKhoan().setMatKhau(hashedNewPassword);

        try {
            String username = TaiKhoanDangNhap.getTaiKhoan().getUserName();
            taiKhoanService.updatePassword(username, hashedNewPassword);
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật mật khẩu qua RMI! Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            throw e;
        }

        clearFields();
    }

    private void clearFields() {
        txtCurrentPass.setText("");
        txtNewPass.setText("");
        txtRePass.setText("");
    }

    private boolean validatePassword(String currentPassword, String newPassword, String confirmPassword) {
        if (!PasswordUtils.checkPassword(currentPassword, TaiKhoanDangNhap.getTaiKhoan().getMatKhau())) {
            JOptionPane.showMessageDialog(this, "Mật khẩu hiện tại không đúng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới và xác nhận không trùng khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (newPassword.length() < 6) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới phải có ít nhất 6 ký tự!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void initKeyListeners() {
        ActionListener moveToNextField = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == txtCurrentPass) {
                    txtNewPass.requestFocus();
                } else if (e.getSource() == txtNewPass) {
                    txtRePass.requestFocus();
                } else if (e.getSource() == txtRePass) {
                    btnYes.doClick();
                }
            }
        };

        txtCurrentPass.addActionListener(moveToNextField);
        txtNewPass.addActionListener(moveToNextField);
        txtRePass.addActionListener(moveToNextField);
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            initKeyListeners();
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        pMK = new javax.swing.JPanel();
        lblNewMK = new javax.swing.JLabel();
        lblAnh = new javax.swing.JLabel();
        lblAnh.setPreferredSize(new Dimension(186, 191)); // Giữ kích thước ban đầu để khớp với khung
        lblAnh.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAnh.setVerticalAlignment(javax.swing.SwingConstants.CENTER);

        lblRepass = new javax.swing.JLabel();
        lblCurrentPass = new javax.swing.JLabel();
        txtCurrentPass = new javax.swing.JPasswordField();
        txtNewPass = new javax.swing.JPasswordField();
        txtRePass = new javax.swing.JPasswordField();

        btnYes = new com.k33ptoo.components.KButton();

        txtCurrentPass.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true");
        txtNewPass.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true");
        txtRePass.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true");

        setLayout(new java.awt.BorderLayout());
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.setBackground(new java.awt.Color(136, 183, 215));

        pMK.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Đổi mật khẩu", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Helvetica Neue", 0, 18)));

        lblNewMK.setText("Mật khẩu mới");
        lblAnh.setBorder(javax.swing.BorderFactory.createTitledBorder("Avatar"));
        lblRepass.setText("Nhập lại mật khẩu");
        lblCurrentPass.setText("Mật khẩu hiện tại");

        btnYes.setText("Xác nhận");
        btnYes.setFont(new java.awt.Font("Helvetica Neue", 0, 16));
        btnYes.setInheritsPopupMenu(true);
        btnYes.setkBackGroundColor(new java.awt.Color(0, 102, 255));
        btnYes.setkEndColor(new java.awt.Color(0, 204, 255));
        btnYes.setkHoverEndColor(new java.awt.Color(255, 204, 204));
        btnYes.setkHoverForeGround(new java.awt.Color(0, 51, 102));
        btnYes.setkHoverStartColor(new java.awt.Color(150, 218, 218));
        btnYes.setkPressedColor(new java.awt.Color(130, 151, 174));
        btnYes.setkSelectedColor(new java.awt.Color(58, 88, 114));
        btnYes.setkStartColor(new java.awt.Color(0, 51, 102));

        javax.swing.GroupLayout pMKLayout = new javax.swing.GroupLayout(pMK);
        pMK.setLayout(pMKLayout);
        pMKLayout.setHorizontalGroup(
                pMKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pMKLayout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnYes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28))
                        .addGroup(pMKLayout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addGroup(pMKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(txtNewPass, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(pMKLayout.createSequentialGroup()
                                                .addComponent(lblAnh, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGroup(pMKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(pMKLayout.createSequentialGroup()
                                                                .addGap(28, 28, 28)
                                                                .addGroup(pMKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                        .addComponent(lblCurrentPass, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(lblNewMK, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(lblRepass, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                        .addGroup(pMKLayout.createSequentialGroup()
                                                                .addGap(68, 68, 68)
                                                                .addComponent(txtRePass, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pMKLayout.createSequentialGroup()
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(txtCurrentPass, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                .addContainerGap(133, Short.MAX_VALUE))
        );
        pMKLayout.setVerticalGroup(
                pMKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pMKLayout.createSequentialGroup()
                                .addGroup(pMKLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(pMKLayout.createSequentialGroup()
                                                .addGap(27, 27, 27)
                                                .addComponent(lblAnh, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(pMKLayout.createSequentialGroup()
                                                .addGap(57, 57, 57)
                                                .addComponent(lblCurrentPass, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(txtCurrentPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(26, 26, 26)
                                                .addComponent(lblNewMK, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(txtNewPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(28, 28, 28)
                                .addComponent(lblRepass, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtRePass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 138, Short.MAX_VALUE)
                                .addComponent(btnYes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(35, 35, 35))
        );

        jPanel1.add(pMK, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 40, -1, -1));
        add(jPanel1, java.awt.BorderLayout.CENTER);
    }

    public void loadNhanVienData() {
        NhanVien nhanVien = TaiKhoanDangNhap.getTaiKhoan() != null ? TaiKhoanDangNhap.getTaiKhoan().getMaNhanVien() : null;

        if (nhanVien != null) {
            String hinhAnh = nhanVien.getHinhanhNV();
            if (hinhAnh != null && !hinhAnh.isEmpty()) {
                displayScaledImage(hinhAnh);
            } else {
                displayScaledImage("src/hinhanh/minke.png");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin nhân viên.", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void displayScaledImage(String imagePath) {
        try {
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                throw new IOException("Không tìm thấy tệp hình ảnh: " + imagePath);
            }

            ImageIcon icon = new ImageIcon(imagePath);
            Image image = icon.getImage();

            int labelWidth = lblAnh.getWidth();
            int labelHeight = lblAnh.getHeight();

            if (labelWidth <= 0 || labelHeight <= 0) {
                labelWidth = 186;
                labelHeight = 191;
            }

            // Lấp đầy khung mà vẫn giữ tỷ lệ khung hình
            double imageAspectRatio = (double) image.getWidth(null) / image.getHeight(null);
            double labelAspectRatio = (double) labelWidth / labelHeight;

            int newWidth, newHeight;
            if (imageAspectRatio > labelAspectRatio) {
                // Ảnh rộng hơn khung -> Giữ chiều cao, điều chỉnh chiều rộng
                newHeight = labelHeight;
                newWidth = (int) (labelHeight * imageAspectRatio);
            } else {
                // Ảnh cao hơn khung -> Giữ chiều rộng, điều chỉnh chiều cao
                newWidth = labelWidth;
                newHeight = (int) (labelWidth / imageAspectRatio);
            }

            Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            lblAnh.setIcon(scaledIcon);
        } catch (IOException e) {
            lblAnh.setIcon(null);
            LOGGER.log(Level.WARNING, "Lỗi khi tải hình ảnh: " + e.getMessage(), e);
        }
    }

    private com.k33ptoo.components.KButton btnYes;
    private javax.swing.JPanel pMK;
    private javax.swing.JLabel lblAnh;
    private javax.swing.JLabel lblCurrentPass;
    private javax.swing.JLabel lblNewMK;
    private javax.swing.JPasswordField txtCurrentPass;
    private javax.swing.JPasswordField txtNewPass;
    private javax.swing.JPasswordField txtRePass;
    private javax.swing.JLabel lblRepass;
}