package gui;

import entity.NhanVien;
import entity.TaiKhoan;
import util.TaiKhoanDangNhap;
import rmi.NhanVienService;
import rmi.TaiKhoanService;
import util.ApplicationContext;
import util.DataChangeNotifier;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GUI_TK extends javax.swing.JPanel {
    private static final Logger LOGGER = Logger.getLogger(GUI_TK.class.getName());
    private NhanVienService nhanVienService;
    private TaiKhoanService taiKhoanService;
    private boolean isRmiInitialized = false;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000;

    public GUI_TK(TaiKhoan taiKhoanHT) {
        taiKhoanService = ApplicationContext.getInstance().getTaiKhoanService();
        nhanVienService = ApplicationContext.getInstance().getNhanVienService();
        isRmiInitialized = ApplicationContext.getInstance().isRmiInitialized();

        if (!isRmiInitialized || taiKhoanService == null || nhanVienService == null) {
            System.out.println("GUI_TK: ApplicationContext không có service, thử kết nối RMI...");
            int retries = 0;
            while (retries < MAX_RETRIES && !isRmiInitialized) {
                try {
                    Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                    taiKhoanService = (TaiKhoanService) registry.lookup("TaiKhoanService");
                    nhanVienService = (NhanVienService) registry.lookup("NhanVienService");
                    System.out.println("GUI_TK: Đã kết nối tới NhanVienService và TaiKhoanService qua RMI!");
                    isRmiInitialized = true;

                    ApplicationContext.getInstance().setTaiKhoanService(taiKhoanService);
                    ApplicationContext.getInstance().setNhanVienService(nhanVienService);
                    ApplicationContext.getInstance().setRmiInitialized(isRmiInitialized);
                    break;
                } catch (Exception e) {
                    retries++;
                    LOGGER.log(Level.WARNING, "GUI_TK: Thử kết nối RMI lần " + retries + " thất bại", e);
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
            System.out.println("GUI_TK: Sử dụng TaiKhoanService và NhanVienService từ ApplicationContext");
        }

        initComponents();
        setFieldsEditable(false);
        DataChangeNotifier.getInstance().addListener(() -> {
            System.out.println("DataChangeNotifier: Dữ liệu thay đổi, tải lại thông tin...");
            loadNhanVienData();
        });

        loadNhanVienData();
    }

    public GUI_TK() {
        initComponents();
        setFieldsEditable(false);
    }

    public void loadNhanVienData() {
        System.out.println("loadNhanVienData: Bắt đầu tải dữ liệu...");

        isRmiInitialized = ApplicationContext.getInstance().isRmiInitialized();
        nhanVienService = ApplicationContext.getInstance().getNhanVienService();
        taiKhoanService = ApplicationContext.getInstance().getTaiKhoanService();

        System.out.println("loadNhanVienData: isRmiInitialized = " + isRmiInitialized);
        System.out.println("loadNhanVienData: nhanVienService = " + nhanVienService);
        System.out.println("loadNhanVienData: taiKhoanService = " + taiKhoanService);

        if (!isRmiInitialized || nhanVienService == null || taiKhoanService == null) {
            System.out.println("loadNhanVienData: RMI chưa được khởi tạo, thử kết nối lại...");
            int retries = 0;
            while (retries < MAX_RETRIES && !isRmiInitialized) {
                try {
                    Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                    nhanVienService = (NhanVienService) registry.lookup("NhanVienService");
                    taiKhoanService = (TaiKhoanService) registry.lookup("TaiKhoanService");
                    System.out.println("loadNhanVienData: Đã kết nối lại NhanVienService và TaiKhoanService qua RMI!");
                    isRmiInitialized = true;

                    ApplicationContext.getInstance().setNhanVienService(nhanVienService);
                    ApplicationContext.getInstance().setTaiKhoanService(taiKhoanService);
                    ApplicationContext.getInstance().setRmiInitialized(isRmiInitialized);
                    break;
                } catch (Exception e) {
                    retries++;
                    LOGGER.log(Level.WARNING, "loadNhanVienData: Thử kết nối RMI lần " + retries + " thất bại", e);
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
                JOptionPane.showMessageDialog(this, "Không thể tải dữ liệu vì kết nối RMI thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            System.out.println("loadNhanVienData: TaiKhoanDangNhap.getTaiKhoan = " + TaiKhoanDangNhap.getTaiKhoan());
            if (TaiKhoanDangNhap.getTaiKhoan() == null) {
                JOptionPane.showMessageDialog(this, "Chưa đăng nhập tài khoản!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String maNhanVien = TaiKhoanDangNhap.getTaiKhoan().getMaNhanVien().getMaNhanVien();
            System.out.println("loadNhanVienData: Tìm nhân viên với mã: " + maNhanVien);
            NhanVien nhanVien = nhanVienService.findNhanVienByMa(maNhanVien);
            System.out.println("loadNhanVienData: Kết quả tìm nhân viên: " + nhanVien);

            if (nhanVien != null) {
                txtMa.setText(nhanVien.getMaNhanVien());
                txtTen.setText(nhanVien.getHoTenNhanVien());
                txtNgaysinh.setText(nhanVien.getNgaySinh() != null ? nhanVien.getNgaySinh().toString() : "Không rõ");
                txtPhai.setText(nhanVien.getGioiTinh() ? "Nam" : "Nữ");
                txtDiaChi.setText(nhanVien.getDiaChi());
                txtSdt.setText(nhanVien.getSoDienThoai());
                txtChucVu.setText(nhanVien.getChucVu() ? "Quản lý" : "Nhân viên");
                txtTTrang.setText(nhanVien.getTrangThai() ? "Đang làm việc" : "Khóa");

                String hinhAnh = nhanVien.getHinhanhNV();
                if (hinhAnh != null && !hinhAnh.isEmpty()) {
                    displayScaledImage(hinhAnh);
                } else {
                    displayScaledImage("src/hinhanh/minke.png");
                }
            } else {
                System.out.println("loadNhanVienData: Không tìm thấy thông tin nhân viên!");
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin nhân viên.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tải dữ liệu nhân viên", e);
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu nhân viên: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayScaledImage(String imagePath) {
        try {
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                imagePath = "src/hinhanh/minke.png";
                imageFile = new File(imagePath);
                if (!imageFile.exists()) {
                    throw new IOException("Không tìm thấy tệp hình ảnh mặc định: " + imagePath);
                }
            }

            ImageIcon icon = new ImageIcon(imagePath);
            Image image = icon.getImage();

            int labelWidth = lblAnh.getWidth();
            int labelHeight = lblAnh.getHeight();

            if (labelWidth <= 0 || labelHeight <= 0) {
                labelWidth = 198;
                labelHeight = 184;
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

    private void setFieldsEditable(boolean editable) {
        txtMa.setEditable(editable);
        txtTen.setEditable(editable);
        txtNgaysinh.setEditable(editable);
        txtPhai.setEditable(editable);
        txtDiaChi.setEditable(editable);
        txtSdt.setEditable(editable);
        txtChucVu.setEditable(editable);
        txtTTrang.setEditable(editable);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        pN = new javax.swing.JPanel();
        lblTieude = new javax.swing.JLabel();
        pThongTin = new javax.swing.JPanel();
        lblAnh = new javax.swing.JLabel();
        lblAnh.setPreferredSize(new Dimension(198, 184)); // Giữ kích thước ban đầu để khớp với khung
        lblAnh.setMinimumSize(new Dimension(198, 184));
        lblAnh.setMaximumSize(new Dimension(198, 184));
        lblAnh.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAnh.setVerticalAlignment(javax.swing.SwingConstants.CENTER);

        lblMa = new javax.swing.JLabel();
        lblTen = new javax.swing.JLabel();
        lblNgaysinh = new javax.swing.JLabel();
        lblPhai = new javax.swing.JLabel();
        lblDiaChi = new javax.swing.JLabel();
        lblSdt = new javax.swing.JLabel();
        lblChucVu = new javax.swing.JLabel();
        lblTTrang = new javax.swing.JLabel();
        btnCapNhatAnh = new javax.swing.JButton();
        txtMa = new javax.swing.JTextField();
        txtTen = new javax.swing.JTextField();
        txtNgaysinh = new javax.swing.JTextField();
        txtPhai = new javax.swing.JTextField();
        txtDiaChi = new javax.swing.JTextField();
        txtSdt = new javax.swing.JTextField();
        txtChucVu = new javax.swing.JTextField();
        txtTTrang = new javax.swing.JTextField();

        setLayout(new java.awt.BorderLayout());

        pN.setBackground(new java.awt.Color(153, 193, 233));
        pN.setForeground(new java.awt.Color(255, 255, 255));
        pN.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTieude.setFont(new java.awt.Font("Helvetica Neue", 0, 28));
        lblTieude.setForeground(new java.awt.Color(51, 51, 51));
        lblTieude.setText("Tài khoản");
        pN.add(lblTieude, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        pThongTin.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Hồ sơ cá nhân", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Helvetica Neue", 0, 14)));
        pThongTin.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblAnh.setBorder(javax.swing.BorderFactory.createTitledBorder("Avatar"));
        pThongTin.add(lblAnh, new org.netbeans.lib.awtextra.AbsoluteConstraints(58, 45, 198, 184));

        lblMa.setFont(new java.awt.Font("Helvetica Neue", 0, 18));
        lblMa.setText("Mã nhân viên: ");
        pThongTin.add(lblMa, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 50, 127, -1));

        lblTen.setFont(new java.awt.Font("Helvetica Neue", 0, 18));
        lblTen.setText("Họ và tên:");
        pThongTin.add(lblTen, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 110, 127, -1));

        lblNgaysinh.setFont(new java.awt.Font("Helvetica Neue", 0, 18));
        lblNgaysinh.setText("Ngày sinh: ");
        pThongTin.add(lblNgaysinh, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 170, 127, -1));

        lblPhai.setFont(new java.awt.Font("Helvetica Neue", 0, 18));
        lblPhai.setText("Giới tính:");
        pThongTin.add(lblPhai, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 230, 127, -1));

        lblDiaChi.setFont(new java.awt.Font("Helvetica Neue", 0, 18));
        lblDiaChi.setText("Địa chỉ:");
        pThongTin.add(lblDiaChi, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 290, 127, -1));

        lblSdt.setFont(new java.awt.Font("Helvetica Neue", 0, 18));
        lblSdt.setText("Số điện thoại:");
        pThongTin.add(lblSdt, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 350, 127, -1));

        lblChucVu.setFont(new java.awt.Font("Helvetica Neue", 0, 18));
        lblChucVu.setText("Chức vụ:");
        pThongTin.add(lblChucVu, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 410, 127, -1));

        lblTTrang.setFont(new java.awt.Font("Helvetica Neue", 0, 18));
        lblTTrang.setText("Tình trạng làm việc:");
        pThongTin.add(lblTTrang, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 470, -1, -1));

        btnCapNhatAnh.setBackground(new java.awt.Color(153, 153, 153));
        btnCapNhatAnh.setText("Thay đổi ảnh đại diện");
        btnCapNhatAnh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCapNhatAnhActionPerformed(evt);
            }
        });
        pThongTin.add(btnCapNhatAnh, new org.netbeans.lib.awtextra.AbsoluteConstraints(73, 259, 173, 30));

        txtMa.setFont(new java.awt.Font("Helvetica Neue", 0, 16));
        txtMa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaActionPerformed(evt);
            }
        });
        pThongTin.add(txtMa, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 50, 300, -1));

        txtTen.setFont(new java.awt.Font("Helvetica Neue", 0, 16));
        txtTen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTenActionPerformed(evt);
            }
        });
        pThongTin.add(txtTen, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 110, 300, -1));

        txtNgaysinh.setFont(new java.awt.Font("Helvetica Neue", 0, 16));
        txtNgaysinh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNgaysinhActionPerformed(evt);
            }
        });
        pThongTin.add(txtNgaysinh, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 170, 300, -1));

        txtPhai.setFont(new java.awt.Font("Helvetica Neue", 0, 16));
        txtPhai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPhaiActionPerformed(evt);
            }
        });
        pThongTin.add(txtPhai, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 230, 300, -1));

        txtDiaChi.setFont(new java.awt.Font("Helvetica Neue", 0, 16));
        txtDiaChi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDiaChiActionPerformed(evt);
            }
        });
        pThongTin.add(txtDiaChi, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 290, 300, -1));

        txtSdt.setFont(new java.awt.Font("Helvetica Neue", 0, 16));
        txtSdt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSdtActionPerformed(evt);
            }
        });
        pThongTin.add(txtSdt, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 350, 300, -1));

        txtChucVu.setFont(new java.awt.Font("Helvetica Neue", 0, 16));
        txtChucVu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtChucVuActionPerformed(evt);
            }
        });
        pThongTin.add(txtChucVu, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 410, 300, -1));

        txtTTrang.setFont(new java.awt.Font("Helvetica Neue", 0, 16));
        txtTTrang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTTrangActionPerformed(evt);
            }
        });
        pThongTin.add(txtTTrang, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 470, 300, -1));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(pN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(17, 17, 17)
                                                .addComponent(pThongTin, javax.swing.GroupLayout.DEFAULT_SIZE, 987, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(pN, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(pThongTin, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE))
        );
        add(jPanel1, java.awt.BorderLayout.CENTER);
    }

    private void btnCapNhatAnhActionPerformed(java.awt.event.ActionEvent evt) {
        isRmiInitialized = ApplicationContext.getInstance().isRmiInitialized();
        nhanVienService = ApplicationContext.getInstance().getNhanVienService();

        if (!isRmiInitialized || nhanVienService == null) {
            JOptionPane.showMessageDialog(this, "Không thể thực hiện vì kết nối RMI thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn ảnh đại diện");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png"));

        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String imagePath = selectedFile.getAbsolutePath();

            String maNhanVien = txtMa.getText().trim();
            if (maNhanVien.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mã nhân viên không được để trống!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                NhanVien nhanVien = nhanVienService.findNhanVienByMa(maNhanVien);
                if (nhanVien == null) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên với mã: " + maNhanVien, "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                nhanVien.setHinhanhNV(imagePath);
                boolean result = nhanVienService.updateNhanVien(nhanVien);

                if (result) {
                    JOptionPane.showMessageDialog(this, "Cập nhật ảnh đại diện thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    displayScaledImage(imagePath);
                    DataChangeNotifier.getInstance().notifyListeners();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật ảnh đại diện thất bại. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật ảnh đại diện", e);
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật ảnh đại diện: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void txtMaActionPerformed(java.awt.event.ActionEvent evt) {}
    private void txtTenActionPerformed(java.awt.event.ActionEvent evt) {}
    private void txtNgaysinhActionPerformed(java.awt.event.ActionEvent evt) {}
    private void txtPhaiActionPerformed(java.awt.event.ActionEvent evt) {}
    private void txtDiaChiActionPerformed(java.awt.event.ActionEvent evt) {}
    private void txtSdtActionPerformed(java.awt.event.ActionEvent evt) {}
    private void txtChucVuActionPerformed(java.awt.event.ActionEvent evt) {}
    private void txtTTrangActionPerformed(java.awt.event.ActionEvent evt) {}

    private javax.swing.JButton btnCapNhatAnh;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblAnh;
    private javax.swing.JLabel lblChucVu;
    private javax.swing.JLabel lblDiaChi;
    private javax.swing.JLabel lblMa;
    private javax.swing.JLabel lblNgaysinh;
    private javax.swing.JLabel lblPhai;
    private javax.swing.JLabel lblSdt;
    private javax.swing.JLabel lblTTrang;
    private javax.swing.JLabel lblTieude;
    private javax.swing.JLabel lblTen;
    private javax.swing.JPanel pN;
    private javax.swing.JPanel pThongTin;
    private javax.swing.JTextField txtChucVu;
    private javax.swing.JTextField txtDiaChi;
    private javax.swing.JTextField txtMa;
    private javax.swing.JTextField txtNgaysinh;
    private javax.swing.JTextField txtPhai;
    private javax.swing.JTextField txtSdt;
    private javax.swing.JTextField txtTTrang;
    private javax.swing.JTextField txtTen;
}