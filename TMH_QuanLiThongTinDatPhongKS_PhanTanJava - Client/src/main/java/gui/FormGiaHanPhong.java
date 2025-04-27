package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import client.RMIClient;
import entity.ChiTietPhieuDat;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuDatPhong;
import entity.Phong;
import entity.TrangThaiPhong;
import rmi.ChiTietPhieuDatService;
import rmi.KhachHangService;
import rmi.PhieuDatService;
import rmi.PhongService;
import util.TaiKhoanDangNhap;

public class FormGiaHanPhong extends javax.swing.JPanel {

    private RMIClient rmiClient;
    private PhongService phongService;
    private ChiTietPhieuDatService chiTietPhieuDatService;
    private PhieuDatService phieuDatService;
    private KhachHangService khachHangService;
    private String tenPhong;
    private ChiTietPhieuDat chiTietPhieuDat;
    private PhieuDatPhong phieuDatPhong;
    private KhachHang khachHang;
    private Phong phong;
    private GUI_QuanLiDatPhong guiQuanLiDatPhong;
    private String traTruoc;
    private boolean isDataValid;

    public FormGiaHanPhong(String room, GUI_QuanLiDatPhong guiQuanLiDatPhong) {
        initComponents();
        this.guiQuanLiDatPhong = guiQuanLiDatPhong;
        tenPhong = room;
        isDataValid = false;
        xoaTrang();
        pCenter.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Khởi tạo RMI Client và các dịch vụ
        try {
            rmiClient = new RMIClient();
            phongService = rmiClient.getPhongService();
            chiTietPhieuDatService = rmiClient.getChiTietPhieuDatService();
            phieuDatService = rmiClient.getPhieuDatService();
            khachHangService = rmiClient.getKhachHangService();
        } catch (Exception e) {
            e.printStackTrace();
            mes("Lỗi kết nối RMI: " + e.getMessage());
            disableForm();
            return;
        }

        // Tải dữ liệu và kiểm tra trạng thái phòng
        try {
            isDataValid = upLoadDuLieuTTPD();
            System.out.println(">>> [DEBUG] isDataValid = " + isDataValid);
            if (!isDataValid) {
                disableForm();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mes("Lỗi tải dữ liệu: " + e.getMessage());
            disableForm();
        }

        // Sự kiện tính trả trước
        jtxtTraTruoc.getDocument().addDocumentListener(new DocumentListener() {
            private boolean isUpdating = false;

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateGiamTru();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateGiamTru();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateGiamTru();
            }

            private void updateGiamTru() {
                if (!isDataValid || isUpdating) return;
                isUpdating = true;
                SwingUtilities.invokeLater(() -> {
                    try {
                        String giaTienText = jtxtGiaTien.getText().replace(".", "").replace(",", ".");
                        BigDecimal giaTien = new BigDecimal(giaTienText);
                        String traTruocText = jtxtTraTruoc.getText().replace(".", "").replace(",", ".");
                        BigDecimal traTruoc = traTruocText.isEmpty() ? BigDecimal.ZERO : new BigDecimal(traTruocText);
                        BigDecimal giamTru = giaTien.subtract(traTruoc);

                        if (giamTru.compareTo(BigDecimal.ZERO) < 0) {
                            mes("Số tiền trả trước vượt quá giá tiền quy định!");
                            jtxtTraTruoc.setText("");
                            jtxtGiamTru.setText("");
                        } else {
                            jtxtGiamTru.setText(formatToVietnamCurrency(giamTru.toString()));
                        }
                    } catch (NumberFormatException | NullPointerException ex) {
                        mes("Lỗi định dạng tiền");
                        jtxtGiamTru.setText("");
                    } finally {
                        isUpdating = false;
                    }
                });
            }
        });

        // Sự kiện nhấn button hủy bỏ
        btnHuyBo.addActionListener(e -> SwingUtilities.getWindowAncestor(FormGiaHanPhong.this).dispose());

        // Sự kiện nhấn button gia hạn phòng
        btnDatPhong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 1. Validate dữ liệu đầu vào
                if (!isDataValid) {
                    mes("Dữ liệu không hợp lệ, không thể gia hạn phòng!");
                    return;
                }

                try {
                    // 2. Lấy nhân viên hiện tại
                    NhanVien nv = TaiKhoanDangNhap.getTaiKhoan().getMaNhanVien();

                    // 3. Lấy ChiTietPhieuDat đang active cho phòng này
                    ChiTietPhieuDat ctpd = chiTietPhieuDatService.findChiTietPhieuDatDangO(tenPhong);
                    if (ctpd == null) {
                        mes("Không tìm thấy chi tiết phiếu đặt!");
                        return;
                    }

                    // 4. Lấy thông tin hiện tại và số giờ cần gia hạn
                    LocalDate ngayCu = ctpd.getNgayTraPhong();
                    LocalTime gioCu = ctpd.getGioTraPhong();
                    LocalDateTime cu = LocalDateTime.of(ngayCu, gioCu);

                    int soGioGiaHan = Integer.parseInt(jComboBoxSoGio.getSelectedItem().toString());
                    LocalDateTime moi = cu.plusHours(soGioGiaHan);

                    LocalDate ngayTraMoi = moi.toLocalDate();
                    LocalTime gioTraMoi = moi.toLocalTime();

                    // 5. Cập nhật trên object và gọi service update
                    ctpd.setSoGio(soGioGiaHan);
                    ctpd.setNgayTraPhong(ngayTraMoi);
                    ctpd.setGioTraPhong(gioTraMoi);

                    boolean ok = chiTietPhieuDatService.updateChiTietPhieuDatPhong(
                            ctpd.getMaChiTietPhieuDat(),
                            ngayTraMoi,
                            gioTraMoi,
                            ctpd.getTrangThaiChiTiet()
                    );

                    // 6. Kiểm tra kết quả và cập nhật giao diện
                    if (ok) {
                        mes("Gia hạn phòng thành công");
                        ChiTietPhieuDat updatedCtpd = chiTietPhieuDatService.findChiTietPhieuDatDangO(tenPhong);
                        System.out.println(">>> [DEBUG] Chi tiết phiếu đặt sau gia hạn: maPhong = " + tenPhong + ", ngayTraPhong = " + updatedCtpd.getNgayTraPhong() + ", gioTraPhong = " + updatedCtpd.getGioTraPhong());
                        guiQuanLiDatPhong.updateTimerForRoom(tenPhong);
                        SwingUtilities.getWindowAncestor(FormGiaHanPhong.this).dispose();
                    } else {
                        mes("Gia hạn phòng thất bại");
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    mes("Lỗi khi gia hạn phòng: " + ex.getMessage());
                }
            }
        });



        // Sự kiện thay đổi kiểu đặt
        jComboBoxKieuDat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isDataValid || phieuDatPhong == null) {
                    mes("Dữ liệu không hợp lệ hoặc không có thông tin phiếu đặt!");
                    jComboBoxKieuDat.setEnabled(false);
                    return;
                }

                String selectedOption = (String) jComboBoxKieuDat.getSelectedItem();
                jComboBoxSoGio.setSelectedIndex(1);
                for (ActionListener al : jComboBoxSoGio.getActionListeners()) {
                    jComboBoxSoGio.removeActionListener(al);
                }

                if (phieuDatPhong.getKieuDat() && "Theo Ngày".equals(selectedOption)) {
                    jComboBoxKieuDat.setSelectedIndex(0);
                    mes("Không thể đổi sang kiểu đặt theo ngày.");
                    return;
                } else if (!phieuDatPhong.getKieuDat() && "Theo Giờ".equals(selectedOption)) {
                    jComboBoxKieuDat.setSelectedIndex(1);
                    mes("Không thể đổi sang kiểu đặt theo giờ.");
                    return;
                }

                jComboBoxKieuDat.setEnabled(false);
                String ngayHienTai = chuyenDoiFormat(LocalDate.now().toString());
                LocalTime gioHienTai = LocalTime.now();

                if ("Theo Giờ".equals(selectedOption)) {
                    xoaThuocTinh();
                    jtxtNgayNhanPhong.setText(ngayHienTai);
                    jtxtNgayTraPhong.setText(ngayHienTai);
                    jtxtNgayNhanPhong.setEnabled(false);
                    jtxtNgayTraPhong.setEnabled(false);
                    jtxtGioNhanPhong.setText(formatLocalTime(gioHienTai));
                    jtxtGioTraPhong.setText(formatLocalTime(gioHienTai.plusHours(1)));
                    jtxtGioNhanPhong.setEditable(true);
                    jtxtGioTraPhong.setEnabled(false);

                    jComboBoxSoGio.addActionListener(evt -> kiemTraVaCapNhatGioTraPhong());
                } else if ("Theo Ngày".equals(selectedOption)) {
                    xoaThuocTinh();
                    jtxtGioNhanPhong.setText("14:00");
                    jtxtGioTraPhong.setText("12:00");
                    jtxtGioNhanPhong.setEnabled(false);
                    jtxtGioTraPhong.setEnabled(false);
                    LocalDate ngayNhanPhong = gioHienTai.isAfter(LocalTime.of(14, 0)) ? LocalDate.now().plusDays(1) : LocalDate.now();
                    jtxtNgayNhanPhong.setText(chuyenDoiFormat(ngayNhanPhong.toString()));
                    jtxtNgayTraPhong.setText(chuyenDoiFormat(ngayNhanPhong.plusDays(1).toString()));
                    jtxtNgayNhanPhong.setEditable(true);
                    jtxtNgayTraPhong.setEnabled(false);

                    jComboBoxSoGio.addActionListener(evt -> kiemTraVaCapNhatNgayTraPhong());
                }
            }
        });

        // Lắng nghe thay đổi trong jtxtGioNhanPhong
        jtxtGioNhanPhong.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (isDataValid) kiemTraVaCapNhatGioTraPhong();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (isDataValid) kiemTraVaCapNhatGioTraPhong();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (isDataValid) kiemTraVaCapNhatGioTraPhong();
            }
        });

        // Lắng nghe thay đổi trong jtxtNgayNhanPhong
        jtxtNgayNhanPhong.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (isDataValid) kiemTraVaCapNhatNgayTraPhong();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (isDataValid) kiemTraVaCapNhatNgayTraPhong();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (isDataValid) kiemTraVaCapNhatNgayTraPhong();
            }
        });
    }

    private void disableForm() {
        isDataValid = false;
        jComboBoxKieuDat.setEnabled(false);
        btnDatPhong.setEnabled(false);
        jtxtTraTruoc.setEnabled(false);
        jtxtNgayNhanPhong.setEnabled(false);
        jtxtNgayTraPhong.setEnabled(false);
        jtxtGioNhanPhong.setEnabled(false);
        jtxtGioTraPhong.setEnabled(false);
        jComboBoxSoGio.setEnabled(false);
        btnHuyBo.setEnabled(true); // Chỉ cho phép nhấn Hủy bỏ
    }

    private void initComponents() {
        // Giữ nguyên mã initComponents từ mã cũ
        buttonGroupGT = new javax.swing.ButtonGroup();
        jProgressBar1 = new javax.swing.JProgressBar();
        pCenter = new javax.swing.JPanel();
        pTTKH = new javax.swing.JPanel();
        lblSoDienThoai = new javax.swing.JLabel();
        jtxtNgaySinh = new javax.swing.JTextField();
        btnTimSDT = new com.raven.datechooser.Button();
        lblNgaySinh = new javax.swing.JLabel();
        jtxtSoDienThoai = new javax.swing.JTextField();
        lblTenKhachHang = new javax.swing.JLabel();
        jtxtTenKhachHang = new javax.swing.JTextField();
        lblSoCCCD = new javax.swing.JLabel();
        jtxtSoCCCD = new javax.swing.JTextField();
        boxGT = new javax.swing.JPanel();
        lblGT = new javax.swing.JLabel();
        jradioNam = new javax.swing.JRadioButton();
        jradioNu = new javax.swing.JRadioButton();
        boxLoaiKH = new javax.swing.JPanel();
        lblLoaiKH = new javax.swing.JLabel();
        jcomboBoxLoaiKH = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        pTTPD = new javax.swing.JPanel();
        lblGiaTien = new javax.swing.JLabel();
        jtxtGiaTien = new javax.swing.JTextField();
        jtxtNgayDat = new javax.swing.JTextField();
        lblKieuDat = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jtxtTraTruoc = new javax.swing.JTextField();
        jtxtGiamTru = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        lblTraTruoc = new javax.swing.JLabel();
        jComboBoxKieuDat = new javax.swing.JComboBox<>();
        lblNgayDat = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        lblGioTraPhong = new javax.swing.JLabel();
        lblSoGio = new javax.swing.JLabel();
        jtxtNgayTraPhong = new javax.swing.JTextField();
        lblNgayNhanPhong = new javax.swing.JLabel();
        jtxtNgayNhanPhong = new javax.swing.JTextField();
        lblGioNhanPhong = new javax.swing.JLabel();
        jtxtGioNhanPhong = new javax.swing.JTextField();
        lblNgayTraPhong = new javax.swing.JLabel();
        jtxtGioTraPhong = new javax.swing.JTextField();
        lblSoLuongNguoi = new javax.swing.JLabel();
        jtxtSoLuongNguoi = new javax.swing.JTextField();
        jComboBoxSoGio = new javax.swing.JComboBox<>();
        pSouth = new javax.swing.JPanel();
        boxButton = new javax.swing.JPanel();
        btnDatPhong = new com.k33ptoo.components.KButton();
        btnHuyBo = new com.k33ptoo.components.KButton();

        setPreferredSize(new java.awt.Dimension(700, 613));
        setLayout(new java.awt.BorderLayout());

        pCenter.setBackground(new java.awt.Color(227, 227, 227));
        pCenter.setLayout(new java.awt.BorderLayout());

        pTTKH.setBackground(new java.awt.Color(227, 227, 227));
        pTTKH.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Thông Tin Khách Hàng", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14)));
        pTTKH.setMinimumSize(new java.awt.Dimension(690, 250));
        pTTKH.setPreferredSize(new java.awt.Dimension(690, 270));
        pTTKH.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblSoDienThoai.setText("Số điện thoại:");
        lblSoDienThoai.setMaximumSize(new java.awt.Dimension(76, 16));
        lblSoDienThoai.setMinimumSize(new java.awt.Dimension(76, 16));
        lblSoDienThoai.setPreferredSize(new java.awt.Dimension(76, 16));
        pTTKH.add(lblSoDienThoai, new org.netbeans.lib.awtextra.AbsoluteConstraints(38, 30, 110, 32));

        jtxtNgaySinh.setText("jTextField1");
        jtxtNgaySinh.setPreferredSize(new java.awt.Dimension(75, 30));
        jtxtNgaySinh.addActionListener(evt -> {});
        pTTKH.add(jtxtNgaySinh, new org.netbeans.lib.awtextra.AbsoluteConstraints(499, 32, 170, 32));

        btnTimSDT.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/png/add.png")));
        btnTimSDT.setToolTipText("");
        btnTimSDT.addActionListener(evt -> {});
        pTTKH.add(btnTimSDT, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 30, -1, 30));

        lblNgaySinh.setText("Ngày sinh:");
        pTTKH.add(lblNgaySinh, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 40, 90, -1));

        jtxtSoDienThoai.setText("jTextField1");
        jtxtSoDienThoai.setPreferredSize(new java.awt.Dimension(75, 30));
        jtxtSoDienThoai.addActionListener(evt -> {});
        pTTKH.add(jtxtSoDienThoai, new org.netbeans.lib.awtextra.AbsoluteConstraints(172, 31, 150, -1));

        lblTenKhachHang.setText("Tên khách hàng:");
        pTTKH.add(lblTenKhachHang, new org.netbeans.lib.awtextra.AbsoluteConstraints(38, 70, 130, 32));

        jtxtTenKhachHang.setText("jTextField2");
        jtxtTenKhachHang.setPreferredSize(new java.awt.Dimension(75, 30));
        pTTKH.add(jtxtTenKhachHang, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 70, 188, 32));

        lblSoCCCD.setText("Số CCCD:");
        pTTKH.add(lblSoCCCD, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 78, 90, -1));

        jtxtSoCCCD.setText("jTextField1");
        jtxtSoCCCD.setPreferredSize(new java.awt.Dimension(75, 30));
        jtxtSoCCCD.addActionListener(evt -> {});
        pTTKH.add(jtxtSoCCCD, new org.netbeans.lib.awtextra.AbsoluteConstraints(499, 70, 170, 32));

        boxGT.setBackground(new java.awt.Color(227, 227, 227));
        boxGT.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblGT.setText("Giới tính:");

        jradioNam.setText("Nam");
        jradioNam.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jradioNam.addActionListener(evt -> {});
        buttonGroupGT.add(jradioNam);

        jradioNu.setText("Nữ");
        jradioNu.addActionListener(evt -> {});
        buttonGroupGT.add(jradioNu);

        javax.swing.GroupLayout boxGTLayout = new javax.swing.GroupLayout(boxGT);
        boxGT.setLayout(boxGTLayout);
        boxGTLayout.setHorizontalGroup(
                boxGTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(boxGTLayout.createSequentialGroup()
                                .addComponent(lblGT, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jradioNam, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jradioNu, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
        boxGTLayout.setVerticalGroup(
                boxGTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(boxGTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblGT, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jradioNam)
                                .addComponent(jradioNu))
        );

        pTTKH.add(boxGT, new org.netbeans.lib.awtextra.AbsoluteConstraints(38, 108, 240, -1));

        boxLoaiKH.setBackground(new java.awt.Color(227, 227, 227));
        boxLoaiKH.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblLoaiKH.setText("Loại khách hàng:");

        jcomboBoxLoaiKH.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "VIP (thành viên)", "thường (vãng lai)" }));
        jcomboBoxLoaiKH.addActionListener(evt -> {});
        pTTKH.add(boxLoaiKH, new org.netbeans.lib.awtextra.AbsoluteConstraints(334, 108, 330, -1));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {},
                new String [] { "Tên khách hàng", "Số điện thoại", "Loại khách hàng", "Số CCCD" }
        ));
        jScrollPane1.setViewportView(jTable1);
        pTTKH.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, 640, 100));

        pCenter.add(pTTKH, java.awt.BorderLayout.NORTH);

        pTTPD.setBackground(new java.awt.Color(227, 227, 227));
        pTTPD.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Thông Tin Phòng Đặt", javax.swing.border.TitledBorder.LEADING, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14)));
        pTTPD.setMinimumSize(new java.awt.Dimension(0, 0));
        pTTPD.setPreferredSize(new java.awt.Dimension(671, 270));
        pTTPD.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblGiaTien.setText("Giá tiền:");
        pTTPD.add(lblGiaTien, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 30, 70, 32));

        jtxtGiaTien.setText("jTextField1");
        jtxtGiaTien.setPreferredSize(new java.awt.Dimension(70, 30));
        jtxtGiaTien.addActionListener(evt -> {});
        pTTPD.add(jtxtGiaTien, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 30, 130, -1));

        jtxtNgayDat.setText("jTextField2");
        jtxtNgayDat.setPreferredSize(new java.awt.Dimension(75, 30));
        jtxtNgayDat.addActionListener(evt -> {});
        pTTPD.add(jtxtNgayDat, new org.netbeans.lib.awtextra.AbsoluteConstraints(106, 80, 150, 32));

        lblKieuDat.setText("Kiểu đặt:");
        pTTPD.add(lblKieuDat, new org.netbeans.lib.awtextra.AbsoluteConstraints(283, 86, 100, 20));

        jPanel1.setBackground(new java.awt.Color(227, 227, 227));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jtxtTraTruoc.setText("jTextField1");
        jtxtTraTruoc.setPreferredSize(new java.awt.Dimension(70, 30));
        jtxtTraTruoc.addActionListener(evt -> {});
        jPanel1.add(jtxtTraTruoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, 130, 32));

        jtxtGiamTru.setText("jTextField1");
        jtxtGiamTru.setPreferredSize(new java.awt.Dimension(70, 30));
        jtxtGiamTru.addActionListener(evt -> {});
        jPanel1.add(jtxtGiamTru, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 10, 120, 32));

        jLabel14.setText("Giảm trừ:");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 10, 70, 30));

        lblTraTruoc.setText("Trả trước:");
        jPanel1.add(lblTraTruoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 70, 30));

        pTTPD.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 20, 420, 50));

        jComboBoxKieuDat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Theo Giờ", "Theo Ngày" }));
        pTTPD.add(jComboBoxKieuDat, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 80, 146, 32));

        lblNgayDat.setText("Ngày Đặt:");
        pTTPD.add(lblNgayDat, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 80, 32));

        jPanel6.setBackground(new java.awt.Color(227, 227, 227));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblGioTraPhong.setText("Giờ trả phòng:");
        jPanel6.add(lblGioTraPhong, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 50, 120, 32));

        lblSoGio.setText("Số giờ/ngày:");
        jPanel6.add(lblSoGio, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 90, 100, 32));

        jtxtNgayTraPhong.setText("jTextField2");
        jtxtNgayTraPhong.setPreferredSize(new java.awt.Dimension(75, 30));
        jtxtNgayTraPhong.addActionListener(evt -> {});
        jPanel6.add(jtxtNgayTraPhong, new org.netbeans.lib.awtextra.AbsoluteConstraints(166, 50, 140, 32));

        lblNgayNhanPhong.setText("Ngày nhận phòng:");
        jPanel6.add(lblNgayNhanPhong, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 160, 32));

        jtxtNgayNhanPhong.setText("jTextField2");
        jtxtNgayNhanPhong.setPreferredSize(new java.awt.Dimension(75, 30));
        jtxtNgayNhanPhong.addActionListener(evt -> {});
        jPanel6.add(jtxtNgayNhanPhong, new org.netbeans.lib.awtextra.AbsoluteConstraints(166, 10, 140, 32));

        lblGioNhanPhong.setText("Giờ nhận phòng:");
        jPanel6.add(lblGioNhanPhong, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 10, 120, 32));

        jtxtGioNhanPhong.setText("jTextField2");
        jtxtGioNhanPhong.setPreferredSize(new java.awt.Dimension(75, 30));
        jtxtGioNhanPhong.addActionListener(evt -> {});
        jPanel6.add(jtxtGioNhanPhong, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 10, 110, 32));

        lblNgayTraPhong.setText("Ngày trả phòng:");
        jPanel6.add(lblNgayTraPhong, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 160, 32));

        jtxtGioTraPhong.setText("jTextField2");
        jtxtGioTraPhong.setPreferredSize(new java.awt.Dimension(75, 30));
        jtxtGioTraPhong.addActionListener(evt -> {});
        jPanel6.add(jtxtGioTraPhong, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 50, 110, 32));

        lblSoLuongNguoi.setText("Số lượng người:");
        jPanel6.add(lblSoLuongNguoi, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 160, 32));

        jtxtSoLuongNguoi.setText("jTextField2");
        jtxtSoLuongNguoi.setPreferredSize(new java.awt.Dimension(75, 30));
        jtxtSoLuongNguoi.addActionListener(evt -> {});
        jPanel6.add(jtxtSoLuongNguoi, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 90, 80, 32));

        jComboBoxSoGio.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));
        jPanel6.add(jComboBoxSoGio, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 90, 80, 30));

        pTTPD.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 130, 570, 150));

        pCenter.add(pTTPD, java.awt.BorderLayout.CENTER);

        pSouth.setBackground(new java.awt.Color(227, 227, 227));
        pSouth.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        boxButton.setBackground(new java.awt.Color(227, 227, 227));

        btnDatPhong.setText("Gia hạn Phòng");
        btnHuyBo.setText("Hủy bỏ");
        btnHuyBo.setkEndColor(new java.awt.Color(255, 255, 0));
        btnHuyBo.setkHoverEndColor(new java.awt.Color(255, 204, 0));
        btnHuyBo.setkStartColor(new java.awt.Color(255, 51, 51));

        javax.swing.GroupLayout boxButtonLayout = new javax.swing.GroupLayout(boxButton);
        boxButton.setLayout(boxButtonLayout);
        boxButtonLayout.setHorizontalGroup(
                boxButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, boxButtonLayout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnHuyBo, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(58, 58, 58)
                                .addComponent(btnDatPhong, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
        boxButtonLayout.setVerticalGroup(
                boxButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(boxButtonLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(boxButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnDatPhong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnHuyBo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(9, Short.MAX_VALUE))
        );

        pSouth.add(boxButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 0, 310, 60));
        pCenter.add(pSouth, java.awt.BorderLayout.SOUTH);
        add(pCenter, java.awt.BorderLayout.CENTER);
    }

    public void xoaTrang() {
        jtxtGiamTru.setText("");
        jtxtGiaTien.setText("");
        jtxtGioNhanPhong.setText("");
        jtxtGioTraPhong.setText("");
        jtxtNgayDat.setText("");
        jtxtNgayNhanPhong.setText("");
        jtxtNgaySinh.setText("");
        jtxtNgayTraPhong.setText("");
        jtxtSoCCCD.setText("");
        jtxtSoDienThoai.setText("");
        jComboBoxSoGio.setSelectedIndex(0);
        jtxtSoLuongNguoi.setText("");
        jtxtTenKhachHang.setText("");
        jtxtTraTruoc.setText("");
    }

    public void mes(String x) {
        JOptionPane.showMessageDialog(this, x);
    }

    public String chuyenDoiFormat(String dateString) {
        DateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String convertedDateString = null;
        try {
            Date date = inputDateFormat.parse(dateString);
            convertedDateString = outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDateString;
    }

    public static String formatLocalTime(LocalTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(formatter);
    }

    public void xoaThuocTinh() {
        jtxtNgayNhanPhong.setEnabled(true);
        jtxtNgayTraPhong.setEnabled(true);
        jtxtGioNhanPhong.setEnabled(true);
        jtxtGioTraPhong.setEnabled(true);
        jtxtGioTraPhong.setText("");
        jtxtNgayTraPhong.setText("");
        jtxtNgayNhanPhong.setText("");
        jtxtGioNhanPhong.setText("");
        jtxtGiamTru.setText("");
        jtxtTraTruoc.setText("");
    }

    public boolean upLoadDuLieuTTPD() throws Exception {
        Phong phongKT = phongService.findPhongByMaPhong(tenPhong);
        if (phongKT == null || phongKT.getTrangThaiPhong() != TrangThaiPhong.DA_DAT) {
            mes("Phòng không tồn tại hoặc không ở trạng thái đã đặt!");
            return false;
        }

        chiTietPhieuDat = chiTietPhieuDatService.findChiTietPhieuDatDangO(tenPhong);
        if (chiTietPhieuDat == null) {
            mes("Không tìm thấy chi tiết phiếu đặt cho phòng này!");
            return false;
        }

        LocalDate today = LocalDate.now();
        LocalDate ngayNhanPhong = chiTietPhieuDat.getNgayNhanPhong();
        LocalDate ngayTraPhong = chiTietPhieuDat.getNgayTraPhong();
        if (ngayNhanPhong == null || ngayTraPhong == null || today.isBefore(ngayNhanPhong) || today.isAfter(ngayTraPhong)) {
            mes("Phòng này không đang được sử dụng vào thời điểm hiện tại!");
            return false;
        }

        String maPhieuDat = chiTietPhieuDat.getMaPhieuDat().getMaPhieuDat();
        phieuDatPhong = phieuDatService.findPhieuDatPhongByMa(maPhieuDat);
        if (phieuDatPhong == null) {
            mes("Không tìm thấy phiếu đặt cho mã: " + maPhieuDat);
            return false;
        }

        khachHang = phieuDatPhong.getMaKhachHang();
        if (khachHang == null) {
            mes("Không tìm thấy thông tin khách hàng!");
            return false;
        }

        xoaTrang();
        jtxtTenKhachHang.setText(khachHang.getTenKhachHang());
        jtxtNgaySinh.setText(chuyenDoiFormat(khachHang.getNgaySinh().toString()));
        jtxtSoCCCD.setText(khachHang.getCccd());
        jtxtSoDienThoai.setText(khachHang.getSoDienThoai());
        jtxtNgayDat.setText(chuyenDoiFormat(phieuDatPhong.getNgayDatPhong().toString()));
        jradioNam.setSelected(khachHang.getGioiTinh());
        jradioNu.setSelected(!khachHang.getGioiTinh());
        jcomboBoxLoaiKH.setSelectedIndex(khachHang.getLoaiKhachHang() ? 0 : 1);
        jComboBoxKieuDat.setSelectedIndex(phieuDatPhong.getKieuDat() ? 0 : 1);
        jtxtNgayNhanPhong.setText(chuyenDoiFormat(chiTietPhieuDat.getNgayNhanPhong().toString()));
        jtxtNgayTraPhong.setText(chuyenDoiFormat(chiTietPhieuDat.getNgayTraPhong().toString()));
        jtxtGioNhanPhong.setText(formatLocalTime(chiTietPhieuDat.getGioNhanPhong()));
        jtxtGioTraPhong.setText(formatLocalTime(chiTietPhieuDat.getGioTraPhong()));
        jtxtSoLuongNguoi.setText(Integer.toString(phieuDatPhong.getSoLuongNguoi()));
        jComboBoxSoGio.setSelectedIndex(chiTietPhieuDat.getSoGio());

        BigDecimal tienPhong = BigDecimal.valueOf(phieuDatPhong.getTienPhong(chiTietPhieuDat, 0));
        jtxtGiaTien.setText(formatToVietnamCurrency(tienPhong.toString()));
        jtxtTraTruoc.setText(formatToVietnamCurrency(BigDecimal.valueOf(phieuDatPhong.getTraTruoc() != null ? phieuDatPhong.getTraTruoc() : 0).toString()));
        jtxtGiamTru.setText(formatToVietnamCurrency(tienPhong.subtract(BigDecimal.valueOf(phieuDatPhong.getTraTruoc() != null ? phieuDatPhong.getTraTruoc() : 0)).toString()));

        jtxtNgayDat.setEnabled(false);
        jtxtTenKhachHang.setEditable(false);
        jtxtNgaySinh.setEditable(false);
        jtxtSoCCCD.setEditable(false);
        jtxtSoDienThoai.setEditable(false);
        jradioNam.setEnabled(false);
        jradioNu.setEnabled(false);
        jcomboBoxLoaiKH.setEnabled(false);
        jtxtGiaTien.setEditable(false);
        jtxtGiamTru.setEditable(false);
        jtxtTraTruoc.setEditable(false);
        jComboBoxKieuDat.setEnabled(true);
        jtxtNgayNhanPhong.setEditable(false);
        jtxtGioNhanPhong.setEditable(false);

        if (phieuDatPhong.getKieuDat()) {
            jtxtGioNhanPhong.setEnabled(false);
            jtxtGioTraPhong.setEnabled(false);
        }
        return true;
    }

    private void capNhatGioTraPhong() {
        try {
            int soGio = Integer.parseInt((String) jComboBoxSoGio.getSelectedItem());
            LocalTime gioNhanPhong = LocalTime.parse(jtxtGioNhanPhong.getText());
            LocalDate ngayNhanPhong = LocalDate.parse(jtxtNgayNhanPhong.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            LocalTime gioTraPhong = gioNhanPhong.plusHours(soGio);
            LocalDate ngayTraPhong = ngayNhanPhong;

            if (gioTraPhong.isBefore(gioNhanPhong)) {
                ngayTraPhong = ngayTraPhong.plusDays(1);
            }

            jtxtGioTraPhong.setText(gioTraPhong.toString());
            jtxtNgayTraPhong.setText(ngayTraPhong.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            calculateTotalPrice();
        } catch (Exception ex) {
            mes("Lỗi định dạng giờ: " + ex.getMessage());
        }
    }

    private void capNhatNgayTraPhong() {
        try {
            int soNgay = Integer.parseInt((String) jComboBoxSoGio.getSelectedItem());
            LocalDate ngayNhanPhong = LocalDate.parse(jtxtNgayNhanPhong.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            LocalDate ngayTraPhong = ngayNhanPhong.plusDays(soNgay);
            jtxtNgayTraPhong.setText(ngayTraPhong.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            calculateTotalPrice();
        } catch (Exception ex) {
            mes("Lỗi định dạng ngày: " + ex.getMessage());
        }
    }

    private void calculateTotalPrice() {
        if (jtxtNgayNhanPhong.getText().isEmpty() || jtxtGioNhanPhong.getText().isEmpty() ||
                jtxtNgayTraPhong.getText().isEmpty() || jtxtGioTraPhong.getText().isEmpty()) {
            return;
        }

        try {
            ArrayList<Phong> dsPhong = (ArrayList<Phong>) phongService.loadAllPhong();
            for (Phong phong : dsPhong) {
                if (tenPhong.equalsIgnoreCase(phong.getMaPhong())) {
                    this.phong = phong;

                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

                    chiTietPhieuDat = new ChiTietPhieuDat(
                            phong,
                            phieuDatPhong,
                            false,
                            LocalDate.parse(jtxtNgayNhanPhong.getText(), dateFormatter),
                            LocalTime.parse(jtxtGioNhanPhong.getText(), timeFormatter),
                            LocalDate.parse(jtxtNgayTraPhong.getText(), dateFormatter),
                            LocalTime.parse(jtxtGioTraPhong.getText(), timeFormatter),
                            Integer.parseInt(jComboBoxSoGio.getSelectedItem().toString())
                    );

                    BigDecimal tienPhong = BigDecimal.valueOf(phieuDatPhong.getTienPhong(chiTietPhieuDat, 0));
                    jtxtGiaTien.setText(formatToVietnamCurrency(tienPhong.toString()));
                    jtxtTraTruoc.setText(formatToVietnamCurrency(BigDecimal.valueOf(phieuDatPhong.getTraTruoc() != null ? phieuDatPhong.getTraTruoc() : 0).toString()));
                    jtxtGiamTru.setText(formatToVietnamCurrency(tienPhong.subtract(BigDecimal.valueOf(phieuDatPhong.getTraTruoc() != null ? phieuDatPhong.getTraTruoc() : 0)).toString()));
                }
            }
        } catch (Exception ex) {
            mes("Lỗi tính giá tiền: " + ex.getMessage());
        }
    }

    private void kiemTraVaCapNhatGioTraPhong() {
        if (jtxtGioNhanPhong.getText().length() == 5) {
            capNhatGioTraPhong();
        }
    }

    private void kiemTraVaCapNhatNgayTraPhong() {
        if (jtxtNgayNhanPhong.getText().length() == 10) {
            capNhatNgayTraPhong();
        }
    }

    public boolean kiemTra() {
        if (jtxtTenKhachHang.getText().isEmpty() ||
                jtxtSoCCCD.getText().isEmpty() ||
                jtxtNgaySinh.getText().isEmpty() ||
                jtxtSoDienThoai.getText().isEmpty() ||
                jtxtGiaTien.getText().isEmpty() ||
                jtxtNgayNhanPhong.getText().isEmpty() ||
                jtxtNgayTraPhong.getText().isEmpty() ||
                jtxtGioNhanPhong.getText().isEmpty() ||
                jtxtGioTraPhong.getText().isEmpty() ||
                jtxtSoLuongNguoi.getText().isEmpty()) {
            return false;
        }

        if (jtxtTraTruoc.getText().isEmpty()) {
            jtxtTraTruoc.setText("0");
        }
        if (jtxtGiamTru.getText().isEmpty()) {
            jtxtGiamTru.setText(jtxtGiaTien.getText());
        }

        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalDate.parse(jtxtNgayNhanPhong.getText(), dateFormatter);
            LocalDate.parse(jtxtNgayTraPhong.getText(), dateFormatter);
            LocalTime.parse(jtxtGioNhanPhong.getText(), timeFormatter);
            LocalTime.parse(jtxtGioTraPhong.getText(), timeFormatter);
        } catch (Exception e) {
            mes("Định dạng ngày hoặc giờ không hợp lệ!");
            return false;
        }

        return true;
    }

    public static String formatToVietnamCurrency(String input) {
        try {
            String numericText = input.replaceAll("[^\\d.,]", "");
            if (numericText.isEmpty()) {
                return "";
            }
            numericText = numericText.replace(",", ".");
            BigDecimal amount = new BigDecimal(numericText);
            DecimalFormatSymbols vietnamSymbols = new DecimalFormatSymbols();
            vietnamSymbols.setGroupingSeparator('.');
            vietnamSymbols.setDecimalSeparator(',');
            DecimalFormat vietnamFormat = new DecimalFormat("#,##0.00");
            vietnamFormat.setDecimalFormatSymbols(vietnamSymbols);
            return vietnamFormat.format(amount);
        } catch (NumberFormatException e) {
            return "";
        }
    }

    // Variables declaration
    private javax.swing.JPanel boxButton;
    private javax.swing.JPanel boxGT;
    private javax.swing.JPanel boxLoaiKH;
    private com.k33ptoo.components.KButton btnDatPhong;
    private com.k33ptoo.components.KButton btnHuyBo;
    private com.raven.datechooser.Button btnTimSDT;
    private javax.swing.ButtonGroup buttonGroupGT;
    private javax.swing.JComboBox<String> jComboBoxKieuDat;
    private javax.swing.JComboBox<String> jComboBoxSoGio;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JComboBox<String> jcomboBoxLoaiKH;
    private javax.swing.JRadioButton jradioNam;
    private javax.swing.JRadioButton jradioNu;
    private javax.swing.JTextField jtxtGiaTien;
    private javax.swing.JTextField jtxtGiamTru;
    private javax.swing.JTextField jtxtGioNhanPhong;
    private javax.swing.JTextField jtxtGioTraPhong;
    private javax.swing.JTextField jtxtNgayDat;
    private javax.swing.JTextField jtxtNgayNhanPhong;
    private javax.swing.JTextField jtxtNgaySinh;
    private javax.swing.JTextField jtxtNgayTraPhong;
    private javax.swing.JTextField jtxtSoCCCD;
    private javax.swing.JTextField jtxtSoDienThoai;
    private javax.swing.JTextField jtxtSoLuongNguoi;
    private javax.swing.JTextField jtxtTenKhachHang;
    private javax.swing.JTextField jtxtTraTruoc;
    private javax.swing.JLabel lblGT;
    private javax.swing.JLabel lblGiaTien;
    private javax.swing.JLabel lblGioNhanPhong;
    private javax.swing.JLabel lblGioTraPhong;
    private javax.swing.JLabel lblKieuDat;
    private javax.swing.JLabel lblLoaiKH;
    private javax.swing.JLabel lblNgayDat;
    private javax.swing.JLabel lblNgayNhanPhong;
    private javax.swing.JLabel lblNgaySinh;
    private javax.swing.JLabel lblNgayTraPhong;
    private javax.swing.JLabel lblSoCCCD;
    private javax.swing.JLabel lblSoDienThoai;
    private javax.swing.JLabel lblSoGio;
    private javax.swing.JLabel lblSoLuongNguoi;
    private javax.swing.JLabel lblTenKhachHang;
    private javax.swing.JLabel lblTraTruoc;
    private javax.swing.JPanel pCenter;
    private javax.swing.JPanel pSouth;
    private javax.swing.JPanel pTTKH;
    private javax.swing.JPanel pTTPD;
}