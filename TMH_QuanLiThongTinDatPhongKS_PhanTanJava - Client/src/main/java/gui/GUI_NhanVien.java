package gui;

import entity.NhanVien;
import entity.TaiKhoan;
import rmi.NhanVienService;
import rmi.TaiKhoanService;
import util.DataChangeNotifier;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;

public class GUI_NhanVien extends javax.swing.JPanel {
    private static final Logger LOGGER = Logger.getLogger(GUI_NhanVien.class.getName());
    private NhanVienService nhanVienService;
    private TaiKhoanService taiKhoanService;

    public GUI_NhanVien() {
        try {
            Registry registry = LocateRegistry.getRegistry("192.168.121.163", 1099);
            nhanVienService = (NhanVienService) registry.lookup("NhanVienService");
            taiKhoanService = (TaiKhoanService) registry.lookup("TaiKhoanService");
            System.out.println("Đã kết nối tới NhanVienService và TaiKhoanService qua RMI!");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi kết nối RMI", e);
            JOptionPane.showMessageDialog(this, "Lỗi kết nối RMI: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        initComponents();
        customizeTable();
        loadDataToTable();
        loadComboBoxData();

        btnChonAnh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Chọn hình ảnh");
                fileChooser.setFileFilter(new FileNameExtensionFilter("Hình ảnh", "jpg", "jpeg", "png", "gif"));

                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String filePath = selectedFile.getAbsolutePath();
                    loadImageToLabel(filePath);
                    lblAnh.setText(filePath);
                    DataChangeNotifier.getInstance().notifyListeners();
                }
            }
        });

        txtTim.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                searchNhanVien();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                searchNhanVien();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchNhanVien();
            }

            private void searchNhanVien() {
                String timKiem = txtTim.getText().trim();
                if (timKiem.isEmpty()) {
                    loadDataToTable();
                    return;
                }

                try {
                    List<NhanVien> ketQua = null;
                    if (timKiem.matches("\\d+")) {
                        ketQua = nhanVienService.findNhanVienBySdt(timKiem);
                    } else if (timKiem.matches("NV\\d+")) {
                        NhanVien nv = nhanVienService.findNhanVienByMa(timKiem);
                        ketQua = nv != null ? List.of(nv) : List.of();
                    } else {
                        ketQua = nhanVienService.findNhanVienByName(timKiem);
                    }
                    updateTableWithData(ketQua);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Lỗi khi tìm kiếm nhân viên", e);
                    loadDataToTable();
                }
            }
        });

        tblNhanVien.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblNhanVienMouseClicked(evt);
            }
        });

        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                SwingUtilities.invokeLater(() -> {
                    adjustComponents();
                });
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {}

            @Override
            public void ancestorMoved(AncestorEvent event) {}
        });
    }

    private void tblNhanVienMouseClicked(MouseEvent evt) {
        int row = tblNhanVien.getSelectedRow();
        if (row != -1) {
            String maNhanVien = tblNhanVien.getValueAt(row, 0).toString();
            try {
                NhanVien nhanVien = nhanVienService.findNhanVienByMa(maNhanVien);
                if (nhanVien != null) {
                    txtMa.setText(nhanVien.getMaNhanVien());
                    txtTen.setText(nhanVien.getHoTenNhanVien());
                    jNgaysinh.setDate(java.sql.Date.valueOf(nhanVien.getNgaySinh()));
                    radNam.setSelected(nhanVien.getGioiTinh());
                    radNu.setSelected(!nhanVien.getGioiTinh());
                    txtDiaChi.setText(nhanVien.getDiaChi());
                    txtSdt.setText(nhanVien.getSoDienThoai());
                    cboChucVu.setSelectedItem(nhanVien.getChucVu() ? "Quản lý" : "Nhân viên");
                    cboTrangThai.setSelectedItem(nhanVien.getTrangThai() ? "Đang làm việc" : "Khóa");
                    txtEmail.setText(nhanVien.getEmail());

                    if (nhanVien.getHinhanhNV() != null && !nhanVien.getHinhanhNV().isEmpty()) {
                        loadImageToLabel(nhanVien.getHinhanhNV());
                        lblAnh.setText(nhanVien.getHinhanhNV());
                    } else {
                        lblAnh.setIcon(null);
                        lblAnh.setText("");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin chi tiết nhân viên.");
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Lỗi khi lấy thông tin nhân viên", e);
                JOptionPane.showMessageDialog(this, "Lỗi khi lấy thông tin nhân viên: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadComboBoxData() {
        cboChucVu.removeAllItems();
        cboChucVu.addItem("Nhân viên");
        cboChucVu.addItem("Quản lý");

        cboTrangThai.removeAllItems();
        cboTrangThai.addItem("Đang làm việc");
        cboTrangThai.addItem("Khóa");
    }

    private void customizeTable() {
        tblNhanVien.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        scrTable.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrTable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tblNhanVien.getTableHeader().setReorderingAllowed(false);
        tblNhanVien.getTableHeader().setResizingAllowed(true);
    }

    private void adjustComponents() {
        int panelWidth = getWidth();
        if (panelWidth > 0) {
            int tableWidth = (int) (panelWidth * 0.7);
            int formWidth = panelWidth - tableWidth;

            pTable.setPreferredSize(new Dimension(tableWidth, 0));
            pForm.setPreferredSize(new Dimension(formWidth, 0));

            int textFieldWidth = (int) (formWidth * 0.6);
            txtMa.setPreferredSize(new Dimension(textFieldWidth, txtMa.getPreferredSize().height));
            txtTen.setPreferredSize(new Dimension(textFieldWidth, txtTen.getPreferredSize().height));
            txtDiaChi.setPreferredSize(new Dimension(textFieldWidth, txtDiaChi.getPreferredSize().height));
            txtSdt.setPreferredSize(new Dimension(textFieldWidth, txtSdt.getPreferredSize().height));
            txtEmail.setPreferredSize(new Dimension(textFieldWidth, txtEmail.getPreferredSize().height));
            jNgaysinh.setPreferredSize(new Dimension(textFieldWidth, jNgaysinh.getPreferredSize().height));

            int buttonWidth = (int) (formWidth * 0.25);
            btnThem.setPreferredSize(new Dimension(buttonWidth, btnThem.getPreferredSize().height));
            btnSua.setPreferredSize(new Dimension(buttonWidth, btnSua.getPreferredSize().height));
            btnLamMoi.setPreferredSize(new Dimension(buttonWidth, btnLamMoi.getPreferredSize().height));

            revalidate();
            repaint();
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        btnGroupPhai = new javax.swing.ButtonGroup();
        pN = new javax.swing.JPanel();
        txtTim = new javax.swing.JTextField();
        txtTim.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm theo mã, tên hoặc số điện thoại nhân viên");
        txtTim.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_ICON, new FlatSVGIcon("icon/svg/tim.svg", 0.5f));
        lblTittle = new javax.swing.JLabel();
        pC = new javax.swing.JPanel();
        pTable = new javax.swing.JPanel();
        scrTable = new javax.swing.JScrollPane();
        tblNhanVien = new javax.swing.JTable();
        pForm = new javax.swing.JPanel();
        lblMa = new javax.swing.JLabel();
        lblTen = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        txtMa = new javax.swing.JTextField();
        txtMa.setEnabled(false);
        txtTen = new javax.swing.JTextField();
        btnLamMoi = new javax.swing.JButton();
        lblGioiTinh = new javax.swing.JLabel();
        lblDiaChi = new javax.swing.JLabel();
        txtDiaChi = new javax.swing.JTextField();
        jNgaysinh = new com.toedter.calendar.JDateChooser();
        jNgaysinh.setDateFormatString("dd/MM/yyyy");
        radNam = new javax.swing.JRadioButton();
        radNu = new javax.swing.JRadioButton();
        lblNgaysinh = new javax.swing.JLabel();
        lblSdt = new javax.swing.JLabel();
        txtSdt = new javax.swing.JTextField();
        lblChucVu = new javax.swing.JLabel();
        cboChucVu = new javax.swing.JComboBox<>();
        lblTrangThai = new javax.swing.JLabel();
        cboTrangThai = new javax.swing.JComboBox<>();
        txtEmail = new javax.swing.JTextField();
        lblHinhanh = new javax.swing.JLabel();
        lblAnh = new javax.swing.JLabel();
        btnChonAnh = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        btnThem = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        pN.setBackground(new java.awt.Color(255, 255, 255));
        pN.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204), 10));

        txtTim.setFont(new java.awt.Font("Times New Roman", 0, 18));
        txtTim.setToolTipText("Nhập từ khóa cần tìm");
        txtTim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTimActionPerformed(evt);
            }
        });

        lblTittle.setFont(new java.awt.Font("Gill Sans", 0, 32));
        lblTittle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTittle.setText("Quản lý nhân viên");
        lblTittle.setToolTipText("");

        javax.swing.GroupLayout pNLayout = new javax.swing.GroupLayout(pN);
        pN.setLayout(pNLayout);
        pNLayout.setHorizontalGroup(
                pNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pNLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lblTittle, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtTim, javax.swing.GroupLayout.PREFERRED_SIZE, 381, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(14, 14, 14))
        );
        pNLayout.setVerticalGroup(
                pNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pNLayout.createSequentialGroup()
                                .addGap(0, 10, Short.MAX_VALUE)
                                .addGroup(pNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblTittle, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtTim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(13, 13, 13))
        );

        add(pN, java.awt.BorderLayout.PAGE_START);

        pC.setBackground(new java.awt.Color(204, 204, 204));
        pC.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204), 10));
        pC.setLayout(new java.awt.BorderLayout());

        pTable.setBackground(new java.awt.Color(255, 255, 255));
        pTable.setLayout(new java.awt.BorderLayout());

        tblNhanVien.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                        "Mã nhân viên", "Họ tên", "Số điện thoại", "Chức vụ", "Trạng thái"
                }
        ));
        scrTable.setViewportView(tblNhanVien);

        pTable.add(scrTable, java.awt.BorderLayout.CENTER);

        pForm.setBackground(new java.awt.Color(255, 255, 255));
        pForm.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Thông tin nhân viên", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 18)));

        lblMa.setFont(new java.awt.Font("Times New Roman", 1, 16));
        lblMa.setText("Mã nhân viên");

        lblTen.setFont(new java.awt.Font("Times New Roman", 1, 16));
        lblTen.setText("Họ tên");

        lblEmail.setFont(new java.awt.Font("Times New Roman", 1, 16));
        lblEmail.setText("Email");

        txtTen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTenActionPerformed(evt);
            }
        });

        btnLamMoi.setBackground(new java.awt.Color(255, 204, 153));
        btnLamMoi.setFont(new java.awt.Font("Times New Roman", 1, 18));
        btnLamMoi.setText("Làm mới");
        btnLamMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLamMoiActionPerformed(evt);
            }
        });

        lblGioiTinh.setFont(new java.awt.Font("Times New Roman", 1, 16));
        lblGioiTinh.setText("Giới tính");

        lblDiaChi.setFont(new java.awt.Font("Times New Roman", 1, 16));
        lblDiaChi.setText("Địa chỉ");

        txtDiaChi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDiaChiActionPerformed(evt);
            }
        });

        btnGroupPhai.add(radNam);
        radNam.setText("Nam");
        radNam.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        radNam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radNamActionPerformed(evt);
            }
        });

        btnGroupPhai.add(radNu);
        radNu.setText("Nữ");
        radNu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radNuActionPerformed(evt);
            }
        });

        lblNgaysinh.setFont(new java.awt.Font("Times New Roman", 1, 16));
        lblNgaysinh.setText("Ngày sinh");

        lblSdt.setFont(new java.awt.Font("Times New Roman", 1, 16));
        lblSdt.setText("Số điện thoại");

        lblChucVu.setFont(new java.awt.Font("Times New Roman", 1, 16));
        lblChucVu.setText("Chức vụ");

        cboChucVu.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Nhân viên", "Quản lý"}));

        lblTrangThai.setFont(new java.awt.Font("Times New Roman", 1, 16));
        lblTrangThai.setText("Trạng thái");

        cboTrangThai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Đang làm việc", "Khóa"}));
        cboTrangThai.setToolTipText("");

        txtEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailActionPerformed(evt);
            }
        });

        lblHinhanh.setFont(new java.awt.Font("Times New Roman", 1, 16));
        lblHinhanh.setText("Hình ảnh");

        lblAnh.setBackground(new java.awt.Color(255, 255, 255));
        lblAnh.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnChonAnh.setText("Chọn ảnh");

        btnSua.setBackground(new java.awt.Color(204, 255, 204));
        btnSua.setFont(new java.awt.Font("Times New Roman", 1, 18));
        btnSua.setText("Sửa");
        btnSua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuaActionPerformed(evt);
            }
        });

        btnThem.setBackground(new java.awt.Color(204, 204, 255));
        btnThem.setFont(new java.awt.Font("Times New Roman", 1, 18));
        btnThem.setText("Thêm");
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pFormLayout = new javax.swing.GroupLayout(pForm);
        pForm.setLayout(pFormLayout);
        pFormLayout.setHorizontalGroup(
                pFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pFormLayout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addGroup(pFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(pFormLayout.createSequentialGroup()
                                                .addGroup(pFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblNgaysinh)
                                                        .addComponent(lblGioiTinh)
                                                        .addComponent(lblDiaChi)
                                                        .addComponent(lblSdt)
                                                        .addComponent(lblChucVu)
                                                        .addComponent(lblTrangThai)
                                                        .addComponent(lblEmail)
                                                        .addComponent(lblHinhanh))
                                                .addGap(24, 24, 24)
                                                .addGroup(pFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(txtDiaChi)
                                                        .addComponent(txtSdt)
                                                        .addComponent(txtEmail)
                                                        .addComponent(jNgaysinh)
                                                        .addGroup(pFormLayout.createSequentialGroup()
                                                                .addComponent(radNam)
                                                                .addGap(28, 28, 28)
                                                                .addComponent(radNu))
                                                        .addComponent(cboChucVu, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(cboTrangThai, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addGroup(pFormLayout.createSequentialGroup()
                                                                .addComponent(lblAnh, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(btnChonAnh))))
                                        .addGroup(pFormLayout.createSequentialGroup()
                                                .addGroup(pFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblMa)
                                                        .addComponent(lblTen))
                                                .addGap(18, 18, 18)
                                                .addGroup(pFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(txtTen)
                                                        .addComponent(txtMa)))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pFormLayout.createSequentialGroup()
                                                .addComponent(btnThem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGap(10, 10, 10)
                                                .addComponent(btnSua, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGap(10, 10, 10)
                                                .addComponent(btnLamMoi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGap(16, 16, 16))
        );
        pFormLayout.setVerticalGroup(
                pFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pFormLayout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addGroup(pFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblMa, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtMa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblTen, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtTen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(pFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jNgaysinh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblNgaysinh, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(pFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(pFormLayout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addGroup(pFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(radNam)
                                                        .addComponent(radNu)))
                                        .addGroup(pFormLayout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addComponent(lblGioiTinh, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(pFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblSdt, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtSdt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblChucVu, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cboChucVu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(pFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cboTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(pFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(pFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(pFormLayout.createSequentialGroup()
                                                .addGap(37, 37, 37)
                                                .addComponent(lblHinhanh, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(pFormLayout.createSequentialGroup()
                                                .addGap(44, 44, 44)
                                                .addComponent(btnChonAnh))
                                        .addGroup(pFormLayout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addComponent(lblAnh, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnSua, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pTable, pForm);
        splitPane.setResizeWeight(0.7);
        splitPane.setDividerSize(5);
        splitPane.setContinuousLayout(true);
        pC.add(splitPane, java.awt.BorderLayout.CENTER);

        add(pC, java.awt.BorderLayout.CENTER);
    }

    private void txtTimActionPerformed(java.awt.event.ActionEvent evt) {}

    private void txtTenActionPerformed(java.awt.event.ActionEvent evt) {}

    private void btnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {
        lamMoiTable();
    }

    private void txtDiaChiActionPerformed(java.awt.event.ActionEvent evt) {}

    private void radNamActionPerformed(java.awt.event.ActionEvent evt) {}

    private void radNuActionPerformed(java.awt.event.ActionEvent evt) {}

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {}

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {
        String maNhanVien = txtMa.getText().trim();
        String hoTenNhanVien = txtTen.getText().trim();
        LocalDate ngaySinh;

        Date selectedDate = jNgaysinh.getDate();
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

        String diaChi = txtDiaChi.getText().trim();
        String soDienThoai = txtSdt.getText().trim();
        boolean chucVu = cboChucVu.getSelectedItem().equals("Quản lý");
        boolean trangThai = cboTrangThai.getSelectedItem().equals("Đang làm việc");
        String email = txtEmail.getText().trim();
        String hinhanhNV = lblAnh.getText().trim();
        if (hinhanhNV.isEmpty()) {
            hinhanhNV = "hinhanh/nv7.png";
        }

        String emailRegex = "^[^\\s@]+@[^\\s@]+\\.[^\\s@.]+(?:\\.[^\\s@.]+)*$";
        if (!email.matches(emailRegex)) {
            JOptionPane.showMessageDialog(this, "Email không hợp lệ! Vui lòng nhập email đúng định dạng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!soDienThoai.matches("0[1-9][0-9]{8}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ! Phải có 10 chữ số và bắt đầu bằng 0.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (hoTenNhanVien.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên nhân viên không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (diaChi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Địa chỉ không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn sửa thông tin nhân viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        NhanVien nhanVien = new NhanVien();
        nhanVien.setMaNhanVien(maNhanVien);
        nhanVien.setHoTenNhanVien(hoTenNhanVien);
        nhanVien.setNgaySinh(ngaySinh);
        nhanVien.setGioiTinh(gioiTinh);
        nhanVien.setDiaChi(diaChi);
        nhanVien.setSoDienThoai(soDienThoai);
        nhanVien.setChucVu(chucVu);
        nhanVien.setTrangThai(trangThai);
        nhanVien.setEmail(email);
        nhanVien.setHinhanhNV(hinhanhNV);

        try {
            if (nhanVienService.updateNhanVien(nhanVien)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin nhân viên thành công!");
                DataChangeNotifier.getInstance().notifyListeners();
                loadDataToTable();
                lamMoiTable();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin nhân viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi cập nhật nhân viên", e);
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật nhân viên: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {
        Date selectedDate = jNgaysinh.getDate();
        LocalDate ngaySinh;
        if (selectedDate != null) {
            ngaySinh = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày sinh!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String hoTenNhanVien = txtTen.getText().trim();
        if (hoTenNhanVien.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên nhân viên không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!radNam.isSelected() && !radNu.isSelected()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn giới tính!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean gioiTinh = radNam.isSelected();

        String diaChi = txtDiaChi.getText().trim();
        String soDienThoai = txtSdt.getText().trim();
        boolean chucVu = cboChucVu.getSelectedItem().equals("Quản lý");
        boolean trangThai = cboTrangThai.getSelectedItem().equals("Đang làm việc");
        String email = txtEmail.getText().trim();
        String hinhanhNV = lblAnh.getText().trim();
        if (hinhanhNV.isEmpty()) {
            hinhanhNV = "hinhanh/nv7.png";
        }

        if (!soDienThoai.matches("0[1-9][0-9]{8}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ! Phải có 10 chữ số và bắt đầu bằng 0.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String emailRegex = "^[^\\s@]+@[^\\s@]+\\.[^\\s@.]+(?:\\.[^\\s@.]+)*$";
        if (!email.matches(emailRegex)) {
            JOptionPane.showMessageDialog(this, "Email không hợp lệ! Vui lòng nhập email đúng định dạng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (Period.between(ngaySinh, LocalDate.now()).getYears() < 18) {
            JOptionPane.showMessageDialog(this, "Nhân viên phải đủ 18 tuổi.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (diaChi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Địa chỉ không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String userName = JOptionPane.showInputDialog("Nhập tên tài khoản cho nhân viên:");
        if (userName == null || userName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên tài khoản không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (!taiKhoanService.checkAvailability(userName)) {
                JOptionPane.showMessageDialog(this, "Tên tài khoản đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi kiểm tra tên tài khoản", e);
            JOptionPane.showMessageDialog(this, "Lỗi khi kiểm tra tên tài khoản: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPasswordField matKhauField = new JPasswordField();
        int matKhauOption = JOptionPane.showConfirmDialog(this, matKhauField, "Nhập mật khẩu cho nhân viên:", JOptionPane.OK_CANCEL_OPTION);
        if (matKhauOption != JOptionPane.OK_OPTION) {
            return;
        }
        String matKhau = new String(matKhauField.getPassword());
        if (matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mật khẩu không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (matKhau.length() < 6) {
            JOptionPane.showMessageDialog(this, "Mật khẩu phải có ít nhất 6 ký tự!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPasswordField repassField = new JPasswordField();
        int repassOption = JOptionPane.showConfirmDialog(this, repassField, "Nhập lại mật khẩu:", JOptionPane.OK_CANCEL_OPTION);
        if (repassOption != JOptionPane.OK_OPTION) {
            return;
        }
        String repass = new String(repassField.getPassword());
        if (!matKhau.equals(repass)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn thêm nhân viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        NhanVien nhanVien = new NhanVien();
        nhanVien.setHoTenNhanVien(hoTenNhanVien);
        nhanVien.setNgaySinh(ngaySinh);
        nhanVien.setGioiTinh(gioiTinh);
        nhanVien.setDiaChi(diaChi);
        nhanVien.setSoDienThoai(soDienThoai);
        nhanVien.setChucVu(chucVu);
        nhanVien.setTrangThai(trangThai);
        nhanVien.setEmail(email);
        nhanVien.setHinhanhNV(hinhanhNV);

        try {
            String maNhanVienMoi = nhanVienService.addNhanVien(nhanVien);
            if (maNhanVienMoi != null) {
                NhanVien nhanVienMoi = nhanVienService.findNhanVienByMa(maNhanVienMoi);
                TaiKhoan taiKhoan = new TaiKhoan(userName, matKhau, nhanVienMoi);
                if (taiKhoanService.taoTK(taiKhoan)) {
                    JOptionPane.showMessageDialog(this, "Thêm nhân viên và tài khoản thành công!");
                    loadDataToTable();
                    lamMoiTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Tạo tài khoản thất bại! Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    nhanVienService.deleteNhanVien(maNhanVienMoi);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thất bại! Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi thêm nhân viên hoặc tài khoản", e);
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm nhân viên hoặc tài khoản: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDataToTable() {
        DefaultTableModel model = (DefaultTableModel) tblNhanVien.getModel();
        model.setRowCount(0);

        new SwingWorker<List<NhanVien>, Void>() {
            @Override
            protected List<NhanVien> doInBackground() throws Exception {
                return nhanVienService.getAllNhanVien();
            }

            @Override
            protected void done() {
                try {
                    List<NhanVien> listNhanVien = get();
                    for (NhanVien nv : listNhanVien) {
                        Object[] rowData = {
                                nv.getMaNhanVien(),
                                nv.getHoTenNhanVien(),
                                nv.getSoDienThoai(),
                                nv.getChucVu() ? "Quản lý" : "Nhân viên",
                                nv.getTrangThai() ? "Đang làm việc" : "Khóa"
                        };
                        model.addRow(rowData);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Lỗi khi tải dữ liệu nhân viên", e);
                    JOptionPane.showMessageDialog(GUI_NhanVien.this, "Lỗi tải dữ liệu: Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void updateTableWithData(List<NhanVien> danhSach) {
        DefaultTableModel model = (DefaultTableModel) tblNhanVien.getModel();
        model.setRowCount(0);
        for (NhanVien nv : danhSach) {
            Object[] row = {
                    nv.getMaNhanVien(),
                    nv.getHoTenNhanVien(),
                    nv.getSoDienThoai(),
                    nv.getChucVu() ? "Quản lý" : "Nhân viên",
                    nv.getTrangThai() ? "Đang làm việc" : "Khóa"
            };
            model.addRow(row);
        }
    }

    private void lamMoiTable() {
        txtTim.setText("");
        txtDiaChi.setText("");
        txtMa.setText("");
        txtSdt.setText("");
        txtTen.setText("");
        txtTim.requestFocus();
        btnGroupPhai.clearSelection();
        tblNhanVien.clearSelection();
        txtEmail.setText("");
        lblAnh.setIcon(null);
        lblAnh.setText("");
        cboChucVu.setSelectedIndex(-1);
        cboTrangThai.setSelectedIndex(-1);
        jNgaysinh.setDate(null);
        loadDataToTable();
    }

    private void resetFields() {
        txtMa.setText("");
        txtTen.setText("");
        txtDiaChi.setText("");
        txtSdt.setText("");
        txtEmail.setText("");
        jNgaysinh.setDate(null);
        radNam.setSelected(false);
        radNu.setSelected(false);
        cboChucVu.setSelectedIndex(0);
        cboTrangThai.setSelectedIndex(0);
        lblAnh.setText("");
        lblAnh.setIcon(new ImageIcon("hinhanh/nv7.png"));
    }

    private void loadImageToLabel(String filePath) {
        try {
            File imageFile = new File(filePath);
            if (!imageFile.exists()) {
                System.out.println("Hình ảnh không tồn tại: " + filePath);
                filePath = "hinhanh/nv7.png";
                imageFile = new File(filePath);
            }
            ImageIcon imageIcon = new ImageIcon(filePath);
            Image image = imageIcon.getImage();
            Image scaledImage = image.getScaledInstance(lblAnh.getWidth(), lblAnh.getHeight(), Image.SCALE_SMOOTH);
            lblAnh.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tải hình ảnh", e);
            lblAnh.setIcon(null);
        }
    }

    // Variables declaration
    private javax.swing.JButton btnChonAnh;
    private javax.swing.ButtonGroup btnGroupPhai;
    private javax.swing.JButton btnLamMoi;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThem;
    private javax.swing.JComboBox<String> cboChucVu;
    private javax.swing.JComboBox<String> cboTrangThai;
    private com.toedter.calendar.JDateChooser jNgaysinh;
    private javax.swing.JLabel lblAnh;
    private javax.swing.JLabel lblChucVu;
    private javax.swing.JLabel lblDiaChi;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblGioiTinh;
    private javax.swing.JLabel lblHinhanh;
    private javax.swing.JLabel lblMa;
    private javax.swing.JLabel lblNgaysinh;
    private javax.swing.JLabel lblSdt;
    private javax.swing.JLabel lblTen;
    private javax.swing.JLabel lblTittle;
    private javax.swing.JLabel lblTrangThai;
    private javax.swing.JPanel pC;
    private javax.swing.JPanel pForm;
    private javax.swing.JPanel pN;
    private javax.swing.JPanel pTable;
    private javax.swing.JRadioButton radNam;
    private javax.swing.JRadioButton radNu;
    private javax.swing.JScrollPane scrTable;
    private javax.swing.JTable tblNhanVien;
    private javax.swing.JTextField txtDiaChi;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtMa;
    private javax.swing.JTextField txtSdt;
    private javax.swing.JTextField txtTen;
    private javax.swing.JTextField txtTim;
}
