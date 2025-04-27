package gui;

import entity.KhachHang;
import entity.TaiKhoan;
import rmi.KhachHangService;
import util.ApplicationContext;
import util.TaiKhoanDangNhap;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GUI_KhachHang extends javax.swing.JPanel {
    private static final Logger LOGGER = Logger.getLogger(GUI_KhachHang.class.getName());
    private KhachHangService khachHangService;
    private boolean isDataLoaded = false; // Biến kiểm tra xem dữ liệu đã được tìm kiếm hay chưa

    public GUI_KhachHang() {
        ApplicationContext appContext = ApplicationContext.getInstance();
        if (!appContext.isRmiInitialized()) {
            JOptionPane.showMessageDialog(this, "Không thể khởi tạo vì kết nối RMI thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            setEnabled(false);
            return;
        }
        khachHangService = appContext.getKhachHangService();
        LOGGER.info("Đã lấy KhachHangService từ ApplicationContext");
        initComponents();
        initializeTable();
        enableInputFields(); // Kích hoạt các trường nhập liệu ngay từ đầu
    }

    private void initializeTable() {
        DefaultTableModel model = (DefaultTableModel) tblKhachHang.getModel();
        model.setRowCount(0); // Xóa các hàng cũ trong bảng
        model.addRow(new Object[]{"", "", "", "Vui lòng tìm kiếm", "", "", ""});
        disableButtons(); // Vô hiệu hóa các nút khi chưa tìm kiếm
    }

    private void loadDataToTable(List<KhachHang> listKhachHang) {
        DefaultTableModel model = (DefaultTableModel) tblKhachHang.getModel();
        model.setRowCount(0); // Xóa các hàng cũ trong bảng

        for (KhachHang kh : listKhachHang) {
            Object[] rowData = {
                    kh.getMaKhachHang(),
                    kh.getTenKhachHang(),
                    kh.getNgaySinh(),
                    kh.getGioiTinh() != null && kh.getGioiTinh() ? "Nam" : "Nữ",
                    kh.getCccd(),
                    kh.getSoDienThoai(),
                    kh.getLoaiKhachHang() != null && kh.getLoaiKhachHang() ? "Khách lẻ" : "Khách quen"
            };
            model.addRow(rowData);
        }
        isDataLoaded = !listKhachHang.isEmpty();
        updateButtonState(); // Cập nhật trạng thái các nút sau khi load dữ liệu
        LOGGER.info("Đã tải dữ liệu khách hàng thành công. Số lượng: " + listKhachHang.size());
    }

    private void disableButtons() {
        btnCapNhat.setEnabled(false);
        btnLamMoi.setEnabled(false);
        // Nút Thêm luôn được kích hoạt để cho phép thêm khách hàng mới
        btnThem2.setEnabled(true);
    }

    private void enableInputFields() {
        txtMa.setEnabled(false); // Mã khách hàng không cho chỉnh sửa
        txtTen.setEnabled(true);
        txtCCCD.setEnabled(true);
        txtSodt.setEnabled(true);
        jDateNgaySinh.setEnabled(true);
        radNam.setEnabled(true);
        radNu.setEnabled(true);
        radVIP.setEnabled(true);
        radTHUONG.setEnabled(true);
    }

    private void updateButtonState() {
        if (isDataLoaded) {
            btnCapNhat.setEnabled(true);
            btnLamMoi.setEnabled(true);
        } else {
            disableButtons();
        }
    }

    private boolean hasPermission() {
        TaiKhoan taiKhoan = TaiKhoanDangNhap.getTaiKhoan();
        if (taiKhoan == null || taiKhoan.getMaNhanVien() == null) {
            JOptionPane.showMessageDialog(this, "Bạn chưa đăng nhập!", "Lỗi quyền truy cập", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        // Nhân viên và quản lý đều có quyền thêm/sửa khách hàng (theo logic MainForm)
        return true;
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        btnGioiTinh = new javax.swing.ButtonGroup();
        btnLoaiKhach = new javax.swing.ButtonGroup();
        pN = new javax.swing.JPanel();
        lblTaiKhoan = new javax.swing.JLabel();
        pC = new javax.swing.JPanel();
        pTimsua = new javax.swing.JPanel();
        txtTim = new javax.swing.JTextField();
        sctable = new javax.swing.JScrollPane();
        tblKhachHang = new javax.swing.JTable();
        pButton = new javax.swing.JPanel();
        btnCapNhat = new com.k33ptoo.components.KButton();
        btnLamMoi = new com.k33ptoo.components.KButton();
        btnThem2 = new com.k33ptoo.components.KButton();
        pTTinKH = new javax.swing.JPanel();
        lblMa = new javax.swing.JLabel();
        lblTen = new javax.swing.JLabel();
        lblPhai = new javax.swing.JLabel();
        lblCCCD = new javax.swing.JLabel();
        lblNgaysinh = new javax.swing.JLabel();
        lblSodt = new javax.swing.JLabel();
        lblLoaiKH = new javax.swing.JLabel();
        txtMa = new javax.swing.JTextField();
        txtTen = new javax.swing.JTextField();
        txtCCCD = new javax.swing.JTextField();
        txtSodt = new javax.swing.JTextField();
        radNam = new javax.swing.JRadioButton();
        radNu = new javax.swing.JRadioButton();
        radVIP = new javax.swing.JRadioButton();
        radTHUONG = new javax.swing.JRadioButton();
        jDateNgaySinh = new com.toedter.calendar.JDateChooser();
        btntTimKiem = new javax.swing.JButton();
        lblTim = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        pN.setBackground(new java.awt.Color(120, 170, 204));

        lblTaiKhoan.setFont(new java.awt.Font("Segoe UI", 1, 18));
        lblTaiKhoan.setForeground(new java.awt.Color(255, 255, 255));
        lblTaiKhoan.setText("QUẢN LÝ KHÁCH HÀNG");

        javax.swing.GroupLayout pNLayout = new javax.swing.GroupLayout(pN);
        pN.setLayout(pNLayout);
        pNLayout.setHorizontalGroup(
                pNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pNLayout.createSequentialGroup()
                                .addGap(500, 500, 500)
                                .addComponent(lblTaiKhoan)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pNLayout.setVerticalGroup(
                pNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pNLayout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(lblTaiKhoan)
                                .addContainerGap(16, Short.MAX_VALUE))
        );

        add(pN, java.awt.BorderLayout.PAGE_START);

        pC.setBackground(new java.awt.Color(255, 255, 255));

        txtTim.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTimFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTimFocusLost(evt);
            }
        });
        txtTim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTimActionPerformed(evt);
            }
        });

        sctable.setBorder(javax.swing.BorderFactory.createTitledBorder("Danh sách khách hàng"));

        tblKhachHang.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                        "Mã khách hàng", "Tên khách hàng", "Ngày Sinh", "Giới Tính", "CCCD", "Số điện thoại", "Loại khách hàng"
                }
        ) {
            boolean[] canEdit = new boolean[]{
                    false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        tblKhachHang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblKhachHangMouseClicked(evt);
            }
        });
        sctable.setViewportView(tblKhachHang);

        pButton.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnCapNhat.setText("Cập nhật");
        btnCapNhat.setkBackGroundColor(new java.awt.Color(255, 153, 0));
        btnCapNhat.setkEndColor(new java.awt.Color(255, 153, 0));
        btnCapNhat.setkHoverColor(new java.awt.Color(255, 153, 0));
        btnCapNhat.setkHoverEndColor(new java.awt.Color(255, 204, 0));
        btnCapNhat.setkHoverForeGround(new java.awt.Color(0, 0, 0));
        btnCapNhat.setkHoverStartColor(new java.awt.Color(255, 153, 0));
        btnCapNhat.setkSelectedColor(new java.awt.Color(255, 153, 0));
        btnCapNhat.setkStartColor(new java.awt.Color(255, 153, 0));
        btnCapNhat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCapNhatActionPerformed(evt);
            }
        });
        pButton.add(btnCapNhat, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 0, 150, 40));

        btnLamMoi.setText("Làm mới");
        btnLamMoi.setkBackGroundColor(new java.awt.Color(0, 153, 255));
        btnLamMoi.setkEndColor(new java.awt.Color(0, 153, 255));
        btnLamMoi.setkHoverEndColor(new java.awt.Color(0, 204, 255));
        btnLamMoi.setkHoverForeGround(new java.awt.Color(0, 0, 0));
        btnLamMoi.setkHoverStartColor(new java.awt.Color(0, 204, 255));
        btnLamMoi.setkSelectedColor(new java.awt.Color(0, 153, 255));
        btnLamMoi.setkStartColor(new java.awt.Color(0, 153, 255));
        btnLamMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLamMoiActionPerformed(evt);
            }
        });
        pButton.add(btnLamMoi, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 0, 140, 40));

        btnThem2.setText("Thêm");
        btnThem2.setkBackGroundColor(new java.awt.Color(255, 0, 0));
        btnThem2.setkEndColor(new java.awt.Color(255, 0, 0));
        btnThem2.setkHoverEndColor(new java.awt.Color(153, 153, 255));
        btnThem2.setkHoverForeGround(new java.awt.Color(0, 0, 0));
        btnThem2.setkHoverStartColor(new java.awt.Color(255, 0, 0));
        btnThem2.setkSelectedColor(new java.awt.Color(255, 0, 0));
        btnThem2.setkStartColor(new java.awt.Color(255, 0, 0));
        btnThem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThem2ActionPerformed(evt);
            }
        });
        pButton.add(btnThem2, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 0, 150, 40));

        pTTinKH.setBackground(new java.awt.Color(255, 255, 255));
        pTTinKH.setBorder(javax.swing.BorderFactory.createTitledBorder("Thông tin khách hàng"));
        pTTinKH.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblMa.setText("Mã khách hàng:");
        pTTinKH.add(lblMa, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, -1, -1));

        lblTen.setText("Tên khách hàng:");
        pTTinKH.add(lblTen, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 90, -1, -1));

        lblPhai.setText("Giới tính:");
        pTTinKH.add(lblPhai, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 150, -1, -1));

        lblCCCD.setText("CCCD: ");
        pTTinKH.add(lblCCCD, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 40, -1, -1));

        lblNgaysinh.setText("Ngày sinh: ");
        pTTinKH.add(lblNgaysinh, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 110, -1, -1));

        lblSodt.setText("Số điện thoại:");
        pTTinKH.add(lblSodt, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 180, -1, -1));

        lblLoaiKH.setText("Loại khách hàng:");
        pTTinKH.add(lblLoaiKH, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 200, -1, 30));
        pTTinKH.add(txtMa, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 40, 260, 30));
        pTTinKH.add(txtTen, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 90, 260, 30));

        txtCCCD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCCCDActionPerformed(evt);
            }
        });
        pTTinKH.add(txtCCCD, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 40, 290, 30));

        txtSodt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSodtActionPerformed(evt);
            }
        });
        pTTinKH.add(txtSodt, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 170, 290, 30));

        btnGioiTinh.add(radNam);
        radNam.setText("Nam");
        pTTinKH.add(radNam, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 140, 100, 40));

        btnGioiTinh.add(radNu);
        radNu.setText("Nữ");
        radNu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radNuActionPerformed(evt);
            }
        });
        pTTinKH.add(radNu, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 140, 100, 40));

        btnLoaiKhach.add(radVIP);
        radVIP.setText("VIP (Khách Quen)");
        radVIP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radVIPActionPerformed(evt);
            }
        });
        pTTinKH.add(radVIP, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 210, 190, -1));

        btnLoaiKhach.add(radTHUONG);
        radTHUONG.setText("Thường (Khách Lẻ)");
        pTTinKH.add(radTHUONG, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 240, 190, 20));

        jDateNgaySinh.setDateFormatString("dd/MM/yyyy");
        pTTinKH.add(jDateNgaySinh, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 100, 290, 30));

        btntTimKiem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/tabCustom/find.png")));
        btntTimKiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btntTimKiemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pTimsuaLayout = new javax.swing.GroupLayout(pTimsua);
        pTimsua.setLayout(pTimsuaLayout);
        pTimsuaLayout.setHorizontalGroup(
                pTimsuaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pTimsuaLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pTimsuaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(pTimsuaLayout.createSequentialGroup()
                                                .addComponent(txtTim, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btntTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addComponent(sctable, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(pTTinKH, javax.swing.GroupLayout.DEFAULT_SIZE, 1490, Short.MAX_VALUE))
                                .addContainerGap())
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pTimsuaLayout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(pButton, javax.swing.GroupLayout.PREFERRED_SIZE, 715, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(21, 21, 21))
        );
        pTimsuaLayout.setVerticalGroup(
                pTimsuaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pTimsuaLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pTimsuaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(btntTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtTim, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(pTTinKH, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                                .addGap(15, 15, 15)
                                .addComponent(pButton, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sctable, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        lblTim.setText("Nhập số điện thoại khách hàng để tìm kiếm");

        javax.swing.GroupLayout pCLayout = new javax.swing.GroupLayout(pC);
        pC.setLayout(pCLayout);
        pCLayout.setHorizontalGroup(
                pCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pCLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(pTimsua, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(pCLayout.createSequentialGroup()
                                                .addGap(8, 8, 8)
                                                .addComponent(lblTim, javax.swing.GroupLayout.PREFERRED_SIZE, 348, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        pCLayout.setVerticalGroup(
                pCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pCLayout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(lblTim, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pTimsua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(28, Short.MAX_VALUE))
        );

        add(pC, java.awt.BorderLayout.CENTER);
    }

    private void txtTimActionPerformed(java.awt.event.ActionEvent evt) {
        btntTimKiemActionPerformed(evt);
    }

    private void tblKhachHangMouseClicked(java.awt.event.MouseEvent evt) {
        if (!isDataLoaded) {
            return; // Không cho phép chọn hàng nếu chưa tìm kiếm
        }

        int row = tblKhachHang.getSelectedRow();
        if (row != -1) {
            txtMa.setText(tblKhachHang.getValueAt(row, 0).toString());
            txtTen.setText(tblKhachHang.getValueAt(row, 1).toString());

            Object dateValue = tblKhachHang.getValueAt(row, 2);
            try {
                if (dateValue instanceof LocalDate) {
                    LocalDate localDate = (LocalDate) dateValue;
                    jDateNgaySinh.setDate(java.sql.Date.valueOf(localDate));
                } else if (dateValue instanceof String) {
                    LocalDate localDate = LocalDate.parse((String) dateValue);
                    jDateNgaySinh.setDate(java.sql.Date.valueOf(localDate));
                } else {
                    jDateNgaySinh.setDate(null);
                    LOGGER.warning("Ngày sinh không hợp lệ tại hàng " + row);
                }
            } catch (Exception e) {
                jDateNgaySinh.setDate(null);
                LOGGER.log(Level.WARNING, "Lỗi khi parse ngày sinh tại hàng " + row, e);
            }

            String gioiTinh = tblKhachHang.getValueAt(row, 3).toString();
            radNam.setSelected(gioiTinh.equals("Nam"));
            radNu.setSelected(gioiTinh.equals("Nữ"));

            txtCCCD.setText(tblKhachHang.getValueAt(row, 4).toString());
            txtSodt.setText(tblKhachHang.getValueAt(row, 5).toString());

            String loaiKhachHang = tblKhachHang.getValueAt(row, 6).toString();
            radVIP.setSelected(loaiKhachHang.equals("Khách quen"));
            radTHUONG.setSelected(loaiKhachHang.equals("Khách lẻ"));
        }
    }

    private void btnCapNhatActionPerformed(java.awt.event.ActionEvent evt) {
        if (!hasPermission()) {
            return;
        }
        if (!isDataLoaded) {
            JOptionPane.showMessageDialog(this, "Vui lòng tìm kiếm khách hàng trước khi cập nhật!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maKhachHang = txtMa.getText().trim();
        String tenKhachHang = txtTen.getText().trim();
        LocalDate ngaySinh;

        Date selectedDate = jDateNgaySinh.getDate();
        if (selectedDate != null) {
            ngaySinh = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày sinh!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!radNam.isSelected() && !radNu.isSelected()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn giới tính!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean gioiTinh = radNam.isSelected();

        String cccd = txtCCCD.getText().trim();
        String soDienThoai = txtSodt.getText().trim();

        if (!radVIP.isSelected() && !radTHUONG.isSelected()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại khách hàng!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean loaiKhachHang = radTHUONG.isSelected();

        if (tenKhachHang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên khách hàng không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cccd.isEmpty() || cccd.length() != 12 || !cccd.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "CCCD phải gồm 12 chữ số!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (soDienThoai.isEmpty() || !soDienThoai.matches("0[1-9][0-9]{8}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn sửa thông tin khách hàng này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            khachHangService.capNhatTTKhachHang(maKhachHang, tenKhachHang, ngaySinh, gioiTinh, cccd, soDienThoai, loaiKhachHang);
            JOptionPane.showMessageDialog(this, "Sửa thông tin khách hàng thành công!");
            loadDataToTable(khachHangService.findKhachHangBySoDienThoai(soDienThoai));
            refresh();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi sửa thông tin khách hàng", e);
            JOptionPane.showMessageDialog(this, "Sửa thông tin khách hàng thất bại: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {
        refresh();
        initializeTable(); // Quay lại trạng thái ban đầu
    }

    private void refresh() {
        txtMa.setText("");
        txtTen.setText("");
        txtCCCD.setText("");
        txtSodt.setText("");
        txtTim.setText("");
        txtTim.requestFocus();
        jDateNgaySinh.setDate(null);
        btnGioiTinh.clearSelection();
        btnLoaiKhach.clearSelection();
    }

    private void btnThem2ActionPerformed(java.awt.event.ActionEvent evt) {
        if (!hasPermission()) {
            return;
        }

        Date selectedDate = jDateNgaySinh.getDate();
        LocalDate ngaySinh;

        if (selectedDate != null) {
            ngaySinh = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày sinh!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tenKhachHang = txtTen.getText().trim();
        String cccd = txtCCCD.getText().trim();
        String soDienThoai = txtSodt.getText().trim();

        if (!radNam.isSelected() && !radNu.isSelected()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn giới tính!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean gioiTinh = radNam.isSelected();

        if (!radVIP.isSelected() && !radTHUONG.isSelected()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại khách hàng!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean loaiKhachHang = radTHUONG.isSelected();

        if (tenKhachHang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên khách hàng không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cccd.isEmpty() || cccd.length() != 12 || !cccd.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "CCCD phải gồm 12 chữ số!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (soDienThoai.isEmpty() || !soDienThoai.matches("0[1-9][0-9]{8}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải có 10 chữ số và bắt đầu bằng 0!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn thêm khách hàng này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            khachHangService.themKhachHang(tenKhachHang, gioiTinh, cccd, ngaySinh, soDienThoai, loaiKhachHang);
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!");
            loadDataToTable(khachHangService.findKhachHangBySoDienThoai(soDienThoai));
            refresh();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm khách hàng", e);
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thất bại: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void txtSodtActionPerformed(java.awt.event.ActionEvent evt) {
        // Không cần xử lý gì khi nhấn Enter trên txtSodt
    }

    private void btntTimKiemActionPerformed(java.awt.event.ActionEvent evt) {
        if (!hasPermission()) {
            return;
        }

        String sdt = txtTim.getText().trim();

        if (sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại để tìm kiếm.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!sdt.matches("0[1-9][0-9]{8}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ! Phải có 10 chữ số và bắt đầu bằng 0.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            List<KhachHang> listKhachHang = khachHangService.findKhachHangBySoDienThoai(sdt);
            loadDataToTable(listKhachHang);

            if (listKhachHang.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng với số điện thoại: " + sdt);
            } else {
                JOptionPane.showMessageDialog(this, "Đã tìm thấy " + listKhachHang.size() + " khách hàng.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm kiếm khách hàng", e);
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm khách hàng: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void txtCCCDActionPerformed(java.awt.event.ActionEvent evt) {
        // Không cần xử lý gì khi nhấn Enter trên txtCCCD
    }

    private void txtTimFocusGained(java.awt.event.FocusEvent evt) {
        if (txtTim.getText().equals("Tìm số điện thoại khách hàng")) {
            txtTim.setText("");
            txtTim.setForeground(new Color(0, 0, 0));
        }
    }

    private void txtTimFocusLost(java.awt.event.FocusEvent evt) {
        if (txtTim.getText().isEmpty()) {
            txtTim.setText("Tìm số điện thoại khách hàng");
            txtTim.setForeground(new Color(153, 153, 153));
        }
    }

    private void radNuActionPerformed(java.awt.event.ActionEvent evt) {
        // Không cần xử lý gì khi chọn radNu
    }

    private void radVIPActionPerformed(java.awt.event.ActionEvent evt) {
        // Không cần xử lý gì khi chọn radVIP
    }

    // Variables declaration - do not modify
    private com.k33ptoo.components.KButton btnCapNhat;
    private javax.swing.ButtonGroup btnGioiTinh;
    private com.k33ptoo.components.KButton btnLamMoi;
    private javax.swing.ButtonGroup btnLoaiKhach;
    private com.k33ptoo.components.KButton btnThem2;
    private javax.swing.JButton btntTimKiem;
    private com.toedter.calendar.JDateChooser jDateNgaySinh;
    private javax.swing.JLabel lblCCCD;
    private javax.swing.JLabel lblLoaiKH;
    private javax.swing.JLabel lblMa;
    private javax.swing.JLabel lblNgaysinh;
    private javax.swing.JLabel lblPhai;
    private javax.swing.JLabel lblSodt;
    private javax.swing.JLabel lblTaiKhoan;
    private javax.swing.JLabel lblTen;
    private javax.swing.JLabel lblTim;
    private javax.swing.JPanel pButton;
    private javax.swing.JPanel pC;
    private javax.swing.JPanel pN;
    private javax.swing.JPanel pTTinKH;
    private javax.swing.JPanel pTimsua;
    private javax.swing.JRadioButton radNam;
    private javax.swing.JRadioButton radNu;
    private javax.swing.JRadioButton radTHUONG;
    private javax.swing.JRadioButton radVIP;
    private javax.swing.JScrollPane sctable;
    private javax.swing.JTable tblKhachHang;
    private javax.swing.JTextField txtCCCD;
    private javax.swing.JTextField txtMa;
    private javax.swing.JTextField txtSodt;
    private javax.swing.JTextField txtTen;
    private javax.swing.JTextField txtTim;
    // End of variables declaration
}