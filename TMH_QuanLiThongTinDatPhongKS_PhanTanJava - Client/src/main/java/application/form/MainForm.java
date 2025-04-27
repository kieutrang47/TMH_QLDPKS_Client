package application.form;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.UIScale;

import application.Application;
import entity.NhanVien;
import gui.*;
import menu.Menu;
import menu.MenuAction;
import util.TaiKhoanDangNhap;

public class MainForm extends JLayeredPane {
    private static NhanVien currentNhanVien;
    private NhanVien nhanVien;
    // Lưu trữ instance của các panel để tái sử dụng
    private GUI_TK guiTK;
    private GUI_KhachHang guiKhachHang;
    private GUI_NhanVien guiNhanVien;
    private GUI_DoiMatKhau guiDoiMatKhau; // Thêm instance cho GUI_DoiMatKhau

    public MainForm() {
        currentNhanVien = new NhanVien();
        currentNhanVien.setHoTenNhanVien("Mặc định");
        currentNhanVien.setChucVu(true);
        init();
    }

    public void onLoginSuccess(NhanVien loggedInNhanVien) {
        this.setCurrentNhanVien(loggedInNhanVien); // Cập nhật ngay sau khi đăng nhập thành công
        // Làm mới dữ liệu cho GUI_TK nếu đã được khởi tạo
        if (guiTK != null) {
            guiTK.loadNhanVienData();
        }
        // Làm mới dữ liệu cho GUI_DoiMatKhau nếu đã được khởi tạo
        if (guiDoiMatKhau != null) {
            guiDoiMatKhau.loadNhanVienData();
        }
    }

    private void init() {
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setLayout(new MainFormLayout());
        menu = new Menu();
        panelBody = new JPanel(new BorderLayout());
        initMenuArrowIcon();
        menuButton.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:$Menu.button.background;"
                + "arc:999;"
                + "focusWidth:0;"
                + "borderWidth:0");
        menuButton.addActionListener((ActionEvent e) -> {
            setMenuFull(!menu.isMenuFull());
        });
        initMenuEvent();
        setLayer(menuButton, JLayeredPane.POPUP_LAYER);
        add(menuButton);
        add(menu);
        add(panelBody);
    }

    private void initMenuEvent() {
        menu.addMenuEvent((index, subIndex, action) -> {
            try {
                if (index==0){
                    showForm(new GUI_QuanLiDatPhong());
                    System.out.println("Hiển thị GUI_Phong để kiểm tra RMI");
                }
                else if (index == 1 && subIndex == 1) { // Quản lý khách hàng
                    if (guiKhachHang == null) {
                        guiKhachHang = new GUI_KhachHang();
                    }
                    showForm(guiKhachHang);
                    System.out.println("Hiển thị GUI_KhachHang để kiểm tra RMI");
                }
                else if (index == 1 && subIndex == 2) { // Quản lý phòng
                    showForm(new GUI_Phong());
                    System.out.println("Hiển thị GUI_Phong để kiểm tra RMI");
                }
                else if (index == 1 && subIndex == 3) { // Quản lý nhân viên
                    if (guiNhanVien == null) {
                        guiNhanVien = new GUI_NhanVien();
                    }
                    showForm(guiNhanVien);
                    System.out.println("Hiển thị GUI_NhanVien để kiểm tra RMI");
                }
                else if (index == 3) { // Quản lý hóa đơn
                    showForm(new GUI_HoaDon1());
                    System.out.println("Hiển thị GUI_HoaDon để kiểm tra RMI");
                }
                else if (index == 4 && subIndex == 1) { // Quản lý tài khoản
                    if (guiTK == null) {
                        guiTK = new GUI_TK();
                    }
                    showForm(guiTK);
                    // Làm mới dữ liệu mỗi lần hiển thị
                    guiTK.loadNhanVienData();
                    System.out.println("Hiển thị GUI_TaiKhoan để kiểm tra RMI");
                }
                else if (index == 4 && subIndex == 2) { // Đổi mật khẩu
                    if (guiDoiMatKhau == null) {
                        guiDoiMatKhau = new GUI_DoiMatKhau();
                    }
                    showForm(guiDoiMatKhau);
                    // Làm mới dữ liệu mỗi lần hiển thị
                    guiDoiMatKhau.loadNhanVienData();
                    System.out.println("Hiển thị GUI_DoiMatKhau để kiểm tra RMI");
                }
                else if (index == 4 && subIndex == 3) { // Hỗ trợ (Mở file PDF)
                    openHelpPDF();
                    System.out.println("Mở file PDF hỗ trợ");
                }
                else if (index == 4 && subIndex == 4) { // Thoát chương trình
//                    Application.logout();
                    TaiKhoanDangNhap.clear();
                    Application.logout();
//                    System.exit(0);
                    System.out.println("Thoát chương trình!");
                } else {
                    JOptionPane.showMessageDialog(this, "Chưa hỗ trợ mục này!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                    action.cancel();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi mở GUI: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                action.cancel();
            }
        });
    }

    @Override
    public void applyComponentOrientation(ComponentOrientation o) {
        super.applyComponentOrientation(o);
        initMenuArrowIcon();
    }

    private void initMenuArrowIcon() {
        if (menuButton == null) {
            menuButton = new JButton();
        }
        String icon = (getComponentOrientation().isLeftToRight()) ? "menu_left.svg" : "menu_right.svg";
        menuButton.setIcon(new FlatSVGIcon("icon/svg/" + icon, 0.8f));
//        System.out.println("Đường dẫn icon: " + iconPath);
//        String icon = (getComponentOrientation().isLeftToRight()) ? "menu_left.svg" : "menu_right.svg";
//        String iconPath = "icon/svg/" + icon;
//        System.out.println("Đường dẫn icon: " + iconPath);

// Kiểm tra xem file có tồn tại trong classpath không
//        if (MainForm.class.getResource(iconPath) == null) {
//            System.err.println("Không tìm thấy file icon: " + iconPath);
//            menuButton.setText(">"); // Thay thế tạm thời nếu không tìm thấy icon
//        } else {
//            System.out.println("Tìm thấy file icon: " + iconPath);
//            menuButton.setIcon(new FlatSVGIcon(iconPath, 0.8f));
//        }
    }

    public static NhanVien getCurrentNhanVien() {
        return currentNhanVien;
    }

    public static void setCurrentNhanVien(NhanVien nhanVien) {
        if (nhanVien == null) {
            System.err.println("Đối tượng NhanVien truyền vào là null!");
            return;
        }
        try {
            currentNhanVien = nhanVien;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Log thông tin khi gán
        System.out.println("Gán nhân viên mới: " + nhanVien.getHoTenNhanVien());
        System.out.println("Gán chức vụ: " + nhanVien.getChucVu());
    }

    private boolean checkAccess(int index, int subIndex) {
        if (currentNhanVien == null) {
            System.err.println("Lỗi: Nhân viên hiện tại là null.");
            return false;
        }
        boolean isNhanVien = currentNhanVien.getChucVu();

        System.out.println("Truy cập quyền hạn:");
        System.out.println(" - Nhân viên hiện tại: " + currentNhanVien.getHoTenNhanVien());
        System.out.println(" - Chức vụ: " + currentNhanVien.getChucVu());
        System.out.println(" - Index: " + index + ", SubIndex: " + subIndex);

        if (index == 3 || index == 4) {
            // Mục tra cứu và cài đặt, cả hai chức vụ đều được phép
            return true;
        }

        if (isNhanVien) { // Nhân viên (true)
            switch (index) {
                case 0: // Đặt phòng
                    return true;
                case 1: // Quản lý
                    return (subIndex == 1); // khách hàng
                case 2: // Thống kê
                    return (subIndex == 2); // Thống kê hóa đơn theo ngày
                default:
                    return false;
            }
        } else { // Quản lý (false)
            switch (index) {
                case 1: // Quản lý
                    return (subIndex == 2 || subIndex == 3); // Quản lý phòng & nhân viên
                case 2: // Thống kê
                    return (subIndex == 1 || subIndex == 3); // Thống kê theo năm & doanh thu
                default:
                    return false;
            }
        }
    }
    private void openHelpPDF() {
        try {
            Path outDir = Paths.get("file");
            if (!Files.exists(outDir)) {
                Files.createDirectories(outDir);
            }

            // Ghi đè file hoadon.pdf cố định
            Path pdfPath = outDir.resolve("HDSD.pdf");
            File pdfFile = pdfPath.toFile(); // ✅ Đúng cách chuyển Path sang File

            if (!pdfFile.exists()) {
                JOptionPane.showMessageDialog(null, "File hướng dẫn không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            } else {
                JOptionPane.showMessageDialog(null, "Máy tính không hỗ trợ mở file PDF!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Không thể mở file PDF: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    private void showAccessDeniedMessage() {
        JOptionPane.showMessageDialog(this,
                "Bạn không có quyền truy cập vào mục này! Vui lòng kiểm tra lại chức năng được phân quyền.",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
    }

    private void setMenuFull(boolean full) {
        String icon;
        if (getComponentOrientation().isLeftToRight()) {
            icon = (full) ? "menu_left.svg" : "menu_right.svg";
        } else {
            icon = (full) ? "menu_right.svg" : "menu_left.svg";
        }
        menuButton.setIcon(new FlatSVGIcon("icon/svg/" + icon, 0.8f));

        menu.setMenuFull(full);
        revalidate();
    }

    public void hideMenu() {
        menu.hideMenuItem();
    }

    public void showForm(Component component) {
        panelBody.removeAll();
        panelBody.add(component);
        panelBody.repaint();
        panelBody.revalidate();
    }

    public void setSelectedMenu(int index, int subIndex) {
        menu.setSelectedMenu(index, subIndex);
    }

    private Menu menu;
    private JPanel panelBody;
    private JButton menuButton;

    private class MainFormLayout implements LayoutManager {
        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return new Dimension(5, 5);
            }
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return new Dimension(0, 0);
            }
        }

        @Override
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                boolean ltr = parent.getComponentOrientation().isLeftToRight();
                Insets insets = UIScale.scale(parent.getInsets());
                int x = insets.left;
                int y = insets.top;
                int width = parent.getWidth() - (insets.left + insets.right);
                int height = parent.getHeight() - (insets.top + insets.bottom);
                int menuWidth = UIScale.scale(menu.isMenuFull() ? menu.getMenuMaxWidth() : menu.getMenuMinWidth());
                int menuX = ltr ? x : x + width - menuWidth;
                menu.setBounds(menuX, y, menuWidth, height);
                int menuButtonWidth = menuButton.getPreferredSize().width;
                int menuButtonHeight = menuButton.getPreferredSize().height;
                int menubX;
                if (ltr) {
                    menubX = (int) (x + menuWidth - (menuButtonWidth * (menu.isMenuFull() ? 0.5f : 0.3f)));
                } else {
                    menubX = (int) (menuX - (menuButtonWidth * (menu.isMenuFull() ? 0.5f : 0.7f)));
                }
                menuButton.setBounds(menubX, UIScale.scale(30), menuButtonWidth, menuButtonHeight);
                int gap = UIScale.scale(5);
                int bodyWidth = width - menuWidth - gap;
                int bodyHeight = height;
                int bodyx = ltr ? (x + menuWidth + gap) : x;
                int bodyy = y;
                panelBody.setBounds(bodyx, bodyy, bodyWidth, bodyHeight);
            }
        }
    }
}