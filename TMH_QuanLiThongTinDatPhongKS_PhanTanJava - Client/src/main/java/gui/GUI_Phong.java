/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;


import client.RMIClient;


import entity.LoaiPhong;
import entity.Phong;
import entity.TrangThaiPhong;
import rmi.PhongService;


public class GUI_Phong extends javax.swing.JPanel {
    private RMIClient rmiClient;
    private PhongService phongService;
	private Phong phong = new Phong();

	JComboBox<LoaiPhong> cboLoaiPhong = new JComboBox<>(LoaiPhong.values());
	JComboBox<TrangThaiPhong> cboTrangThai = new JComboBox<>(TrangThaiPhong.values());
    /**
     * Creates new form GUI_NhanVien
     */
//    public GUI_Phong() {
//        initComponents();
//    }

	public GUI_Phong() {


	    try {
            rmiClient = new RMIClient();
            phongService = rmiClient.getPhongService();
            System.out.println("Connected to RMI server successfully!");

	    } catch (Exception e) {
	        // Xử lý ngoại lệ nếu không thể kết nối
	        e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to RMI server: " + e.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
	    }


	    initComponents(); // Khởi tạo các thành phần giao diện

	}


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pC = new javax.swing.JPanel();
        b2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablePhong = new javax.swing.JTable();
        txtTimKiem = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        pTTPhong = new javax.swing.JPanel();
        lblmaPhong = new javax.swing.JLabel();
        lblSoPhong = new javax.swing.JLabel();
        lblLoaiPhong = new javax.swing.JLabel();
        lblhaPhong = new javax.swing.JLabel();
        lblTrangThaiPhong = new javax.swing.JLabel();
        txtSoGiuong = new javax.swing.JTextField();
        txtMaPhong = new javax.swing.JTextField();
        cboLoaiPhong = new JComboBox<>();
        cboTrangThai = new JComboBox<>();
        lblANH = new javax.swing.JLabel();
        btnAnh = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        btnLamMoi = new javax.swing.JButton();
        btnCapNhat = new javax.swing.JButton();
        lblGiaPhong = new javax.swing.JLabel();
        txtSoPhong = new javax.swing.JTextField();
        lblSoGiuong1 = new javax.swing.JLabel();
        txtGiaPhong = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        btnTimKiem = new javax.swing.JButton();


        txtMaPhong.setEnabled(false);
        txtSoGiuong.setEnabled(false);
        txtGiaPhong.setEnabled(false);



        setLayout(new java.awt.BorderLayout());

        pC.setLayout(new javax.swing.BoxLayout(pC, javax.swing.BoxLayout.LINE_AXIS));

        b2.setBackground(new Color(255, 255, 255));

        tablePhong.setModel(new DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Mã phòng", "Số phòng", "Số giường", "Loại phòng", "Giá Phòng", "Hình ảnh phòng", "Trạng thái phòng"
            }
        ));
        tablePhong.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablePhongMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablePhong);

        txtTimKiem.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtTimKiemFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtTimKiemFocusLost(evt);
            }
        });
        txtTimKiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                txtTimKiemActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel1.setText("Tìm kiếm: ");

        pTTPhong.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Thông Tin Phòng", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 16))); // NOI18N

        lblmaPhong.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblmaPhong.setText("Mã Phòng");

        lblSoPhong.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSoPhong.setText("Số Phòng");

        lblLoaiPhong.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblLoaiPhong.setText("Loại Phòng");

        lblhaPhong.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblhaPhong.setText("Hinh anh Phong");

        lblTrangThaiPhong.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblTrangThaiPhong.setText("Trạng Thái Phòng");

        txtSoGiuong.setPreferredSize(new java.awt.Dimension(75, 30));
        txtSoGiuong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                txtSoGiuongActionPerformed(evt);
            }
        });

        txtMaPhong.setPreferredSize(new java.awt.Dimension(75, 30));
        txtMaPhong.setEnabled(false);
        cboLoaiPhong.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
//        cboLoaiPhong.setModel(new javax.swing.DefaultComboBoxModel<>(new LoaiPhong[] { "MOT_GIUONG_DON", "HAI_GIUONG_DON", "MOT_DON_MOT_DOI", "MOT_GIUONG_DOI" }));
//
//        cboLoaiPhong.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                cboLoaiPhongActionPerformed(evt);
//            }
//        });

        cboLoaiPhong.setModel(new javax.swing.DefaultComboBoxModel<>(LoaiPhong.values())); // Sử dụng values() để lấy tất cả giá trị của enum

        cboLoaiPhong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cboLoaiPhongActionPerformed(evt);
            }
        });

        cboTrangThai.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cboTrangThai.setModel(new javax.swing.DefaultComboBoxModel<>(TrangThaiPhong.values()));
        cboTrangThai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cboTrangThaiActionPerformed(evt);
            }
        });

        lblANH.setBackground(new Color(255, 255, 255));
        lblANH.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnAnh.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnAnh.setText("Chọn ảnh");
        btnAnh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnAnhActionPerformed(evt);
            }
        });

        btnLamMoi.setText("Làm mới");
        btnLamMoi.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
//        btnLamMoi.setkEndColor(new Color(51, 153, 255));
//        btnLamMoi.setkHoverColor(new Color(204, 204, 204));
//        btnLamMoi.setkHoverEndColor(new Color(204, 204, 204));
//        btnLamMoi.setkHoverForeGround(new Color(0, 153, 153));
//        btnLamMoi.setkHoverStartColor(new Color(204, 204, 204));
//        btnLamMoi.setkPressedColor(new Color(204, 204, 204));
//        btnLamMoi.setkSelectedColor(new Color(204, 204, 204));
//        btnLamMoi.setkStartColor(new Color(0, 153, 204));
        btnLamMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnLamMoiActionPerformed(evt);
            }
        });

        btnCapNhat.setText("Cập nhật");
        btnCapNhat.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
//        btnCapNhat.setkBackGroundColor(new Color(255, 102, 102));
//        btnCapNhat.setkBorderRadius(15);
//        btnCapNhat.setkEndColor(new Color(255, 102, 102));
//        btnCapNhat.setkHoverColor(new Color(204, 204, 204));
//        btnCapNhat.setkHoverEndColor(new Color(255, 153, 153));
//        btnCapNhat.setkHoverForeGround(new Color(0, 153, 153));
//        btnCapNhat.setkHoverStartColor(new Color(204, 204, 204));
//        btnCapNhat.setkPressedColor(new Color(204, 204, 204));
//        btnCapNhat.setkSelectedColor(new Color(204, 204, 204));
//        btnCapNhat.setkStartColor(new Color(255, 102, 102));
        btnCapNhat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnCapNhatActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCapNhat, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnLamMoi, javax.swing.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnCapNhat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        lblGiaPhong.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblGiaPhong.setText("Giá Phòng");

        txtSoPhong.setPreferredSize(new java.awt.Dimension(75, 30));
        txtSoPhong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                txtSoPhongActionPerformed(evt);
            }
        });

        lblSoGiuong1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblSoGiuong1.setText("Số Giường");

        txtGiaPhong.setPreferredSize(new java.awt.Dimension(75, 30));
        txtGiaPhong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                txtGiaPhongActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pTTPhongLayout = new javax.swing.GroupLayout(pTTPhong);
        pTTPhong.setLayout(pTTPhongLayout);
        pTTPhongLayout.setHorizontalGroup(
            pTTPhongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pTTPhongLayout.createSequentialGroup()
                .addGap(117, 117, 117)
                .addGroup(pTTPhongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(pTTPhongLayout.createSequentialGroup()
                        .addGroup(pTTPhongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pTTPhongLayout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(lblmaPhong, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblhaPhong, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pTTPhongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pTTPhongLayout.createSequentialGroup()
                                .addComponent(lblANH, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(31, 31, 31)
                                .addComponent(btnAnh))
                            .addComponent(txtMaPhong, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pTTPhongLayout.createSequentialGroup()
                        .addComponent(lblLoaiPhong, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cboLoaiPhong, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(69, 69, 69)
                .addGroup(pTTPhongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTrangThaiPhong, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pTTPhongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lblGiaPhong, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblSoGiuong1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblSoPhong, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(36, 36, 36)
                .addGroup(pTTPhongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtGiaPhong, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtSoGiuong, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtSoPhong, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboTrangThai, 0, 313, Short.MAX_VALUE))
                .addContainerGap(214, Short.MAX_VALUE))
        );
        pTTPhongLayout.setVerticalGroup(
            pTTPhongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pTTPhongLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pTTPhongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMaPhong, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblmaPhong, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSoPhong, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtSoPhong, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pTTPhongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pTTPhongLayout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(pTTPhongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblLoaiPhong, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboLoaiPhong, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblGiaPhong, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtGiaPhong, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(72, 72, 72)
                        .addComponent(btnAnh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 129, Short.MAX_VALUE))
                    .addGroup(pTTPhongLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 131, Short.MAX_VALUE)
                        .addGroup(pTTPhongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pTTPhongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblhaPhong, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblANH, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pTTPhongLayout.createSequentialGroup()
                                .addGroup(pTTPhongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lblSoGiuong1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtSoGiuong, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(42, 42, 42)
                                .addGroup(pTTPhongLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(cboTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblTrangThaiPhong, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(12, 12, 12)))
                        .addGap(42, 42, 42)))
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2.setBackground(new Color(120, 170, 204));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 47, Short.MAX_VALUE)
        );

        // Trong initComponents(), thay dòng:
// btnTimKiem.setIcon(new ImageIcon(getClass().getResource("/icon/tabCustom/find.png")));
        btnTimKiem.setText("Tìm");
        URL iconURL = getClass().getResource("/icon/tabCustom/find.png");
        if (iconURL != null) {
            btnTimKiem.setIcon(new ImageIcon(iconURL));
        } else {
            System.err.println("Không tìm thấy icon: /icon/tabCustom/find.png");
        }
        btnTimKiem.addActionListener(e -> btnTimKiemActionPerformed(e)); // NOI18N
        btnTimKiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnTimKiemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout b2Layout = new javax.swing.GroupLayout(b2);
        b2.setLayout(b2Layout);
        b2Layout.setHorizontalGroup(
            b2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, b2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51))
            .addComponent(jScrollPane1)
            .addGroup(b2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(b2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(pTTPhong, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        b2Layout.setVerticalGroup(
            b2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(b2Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addGroup(b2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(b2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pTTPhong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(11, Short.MAX_VALUE))
        );

        pC.add(b2);

        add(pC, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

//    private void btnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {
//        // TODO add your handling code here:
//
//
//    }

//    LAM MOI
    private void btnLamMoiActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnLamMoiActionPerformed
        // Xóa nội dung trong các JTextField
        DefaultTableModel model = (DefaultTableModel) tablePhong.getModel();
        model.setRowCount(0); // Xóa tất cả các hàng khỏi bảng
        txtMaPhong.setText("");
        txtSoPhong.setText("");
        txtSoGiuong.setText("");
        txtGiaPhong.setText("");

        // Đặt lại hình ảnh mặc định cho JLabel (nếu có hình ảnh mặc định)
        lblANH.setIcon(null); // Hoặc đặt một biểu tượng mặc định nếu muốn

        // Đặt JComboBox của loại phòng về mục đầu tiên hoặc mặc định
        if (cboLoaiPhong.getItemCount() > 0) {
            cboLoaiPhong.setSelectedIndex(0);
        }

        // Đặt JComboBox của trạng thái phòng về mục đầu tiên hoặc mặc định
        if (cboTrangThai.getItemCount() > 0) {
            cboTrangThai.setSelectedIndex(0);
        }

        // Thông báo cho người dùng nếu cần
        JOptionPane.showMessageDialog(this, "Đã làm mới các trường dữ liệu.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnLamMoiActionPerformed

    private void btnAnhVActionPerformed(ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn hình ảnh");

        // Chỉ cho phép chọn tệp hình ảnh
        fileChooser.setFileFilter(new FileNameExtensionFilter("Hình ảnh", "jpg", "jpeg", "png", "gif"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();

            // Hiển thị hình ảnh lên JLabel
            loadImageToLabel(filePath);
        }
    }

    public double getGiaPhongTheoLoai(LoaiPhong loaiPhong) {
        double giaPhong;
        switch (loaiPhong) {
            case MOT_GIUONG_DON:
                giaPhong = 200000; // Giá cho loại phòng một giường đơn
                break;
            case HAI_GIUONG_DON:
                giaPhong = 350000; // Giá cho loại phòng hai giường đơn
                break;
            case MOT_DON_MOT_DOI:
                giaPhong = 500000; // Giá cho loại phòng một đơn một đôi
                break;
            case MOT_GIUONG_DOI:
                giaPhong = 300000; // Giá cho loại phòng một giường đôi
                break;
            default:
                throw new IllegalArgumentException("Loại phòng không hợp lệ: " + loaiPhong);
        }
        return giaPhong;
    }

    // Phương thức lấy số giường theo loại phòng
    public int getSoGiuongTheoLoai(LoaiPhong loaiPhong) {
        int soGiuong;
        switch (loaiPhong) {
            case MOT_GIUONG_DON:
                soGiuong = 1; // Số giường cho loại phòng một giường đơn
                break;
            case HAI_GIUONG_DON:
                soGiuong = 2; // Số giường cho loại phòng hai giường đơn
                break;
            case MOT_DON_MOT_DOI:
                soGiuong = 2; // Số giường cho loại phòng một đơn một đôi
                break;
            case MOT_GIUONG_DOI:
                soGiuong = 1; // Số giường cho loại phòng một giường đôi
                break;
            default:
                throw new IllegalArgumentException("Loại phòng không hợp lệ: " + loaiPhong);
        }
        return soGiuong;
    }


    private String getHinhAnhPhong() {
        String hinhAnhPhong = lblANH.getText();
        return (hinhAnhPhong == null || hinhAnhPhong.isEmpty()) ? "/path/to/default/image.png" : hinhAnhPhong;
    }

    private boolean confirmUpdate() {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn cập nhật thông tin phòng này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        return (confirm == JOptionPane.YES_OPTION);
    }

    private void loadTablePhong() {
        DefaultTableModel model = (DefaultTableModel) tablePhong.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ

        try {
            // Lấy tất cả phòng thông qua RMI
            List<Phong> danhSachPhong = rmiClient.getPhongByTrangThai(null);

            if (danhSachPhong != null) {
                for (Phong phong : danhSachPhong) {
                    Object[] rowData = {
                            phong.getMaPhong(),
                            phong.getSoPhong(),
                            phong.getSoGiuong(),
                            phong.getLoaiPhong(),
                            phong.getGiaPhong(),
                            phong.getHinhAnhPhong(),
                            phong.getTrangThaiPhong()
                    };
                    model.addRow(rowData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu phòng: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnCapNhatActionPerformed(java.awt.event.ActionEvent evt) {
        // Lấy dữ liệu từ giao diện
        String maPhong = txtMaPhong.getText();
        LoaiPhong loaiPhong = (LoaiPhong) cboLoaiPhong.getSelectedItem();
        double giaPhong = getGiaPhongTheoLoai(loaiPhong);
        TrangThaiPhong trangThai = (TrangThaiPhong) cboTrangThai.getSelectedItem();

        if (!confirmUpdate()) return;

        try {
            // Gọi RMI để cập nhật
            boolean success = rmiClient.capNhatPhong(maPhong, giaPhong, trangThai);

            if (success) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                loadTablePhong(); // Tải lại dữ liệu
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void btnTimKiemActionPerformed(java.awt.event.ActionEvent evt) {
        String tuKhoa = txtTimKiem.getText().trim();

        if (tuKhoa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tablePhong.getModel();
        model.setRowCount(0);

        try {
            // Tìm theo trạng thái nếu từ khóa là tên trạng thái
            try {
                TrangThaiPhong trangThai = TrangThaiPhong.valueOf(tuKhoa.toUpperCase());
                List<Phong> dsTheoTrangThai = rmiClient.getPhongByTrangThai(trangThai);
                addRoomsToTable(model, dsTheoTrangThai);
                return;
            } catch (IllegalArgumentException e) {
                // Không phải trạng thái hợp lệ, tiếp tục tìm kiếm khác
            }

            // Tìm theo loại phòng nếu từ khóa là tên loại phòng
            try {
                LoaiPhong loaiPhong = LoaiPhong.valueOf(tuKhoa.toUpperCase());
                List<Phong> dsTheoLoai = rmiClient.getPhongByLoaiPhong(loaiPhong);
                addRoomsToTable(model, dsTheoLoai);
                return;
            } catch (IllegalArgumentException e) {
                // Không phải loại phòng hợp lệ, tiếp tục tìm kiếm khác
            }
            try {

                Phong phong = rmiClient.getPhongByMaPhong(tuKhoa);
                if (phong != null) {
                    List<Phong> dsTheoMaPhong = new ArrayList<>();
                    dsTheoMaPhong.add(phong);
                    addRoomsToTable(model, dsTheoMaPhong);
                    return;
                }

            } catch (IllegalArgumentException e) {
                // Không phải loại phòng hợp lệ, tiếp tục tìm kiếm khác
            }
            // Tìm theo số điện thoại
            List<Phong> dsTheoSDT = rmiClient.getPhongBySoDienThoai(tuKhoa);
            if (dsTheoSDT != null && !dsTheoSDT.isEmpty()) {
                addRoomsToTable(model, dsTheoSDT);
                return;
            }

            // Nếu không tìm thấy gì
            JOptionPane.showMessageDialog(this, "Không tìm thấy phòng phù hợp");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addRoomsToTable(DefaultTableModel model, List<Phong> danhSachPhong) {
        if (danhSachPhong != null) {
            for (Phong phong : danhSachPhong) {
                Object[] rowData = {
                        phong.getMaPhong(),
                        phong.getSoPhong(),
                        phong.getSoGiuong(),
                        phong.getLoaiPhong(),
                        phong.getGiaPhong(),
                        phong.getHinhAnhPhong(),
                        phong.getTrangThaiPhong()
                };
                model.addRow(rowData);
            }
        }
    }
    private void tablePhongMouseClicked(java.awt.event.MouseEvent evt) {
        int row = tablePhong.getSelectedRow();

        if (row >= 0) {
            // Lấy dữ liệu từ bảng
            String maPhong = tablePhong.getValueAt(row, 0).toString();
            String soPhong = tablePhong.getValueAt(row, 1).toString();
            int soGiuong = Integer.parseInt(tablePhong.getValueAt(row, 2).toString());
            LoaiPhong loaiPhong = (LoaiPhong) tablePhong.getValueAt(row, 3);
            double giaPhong = Double.parseDouble(tablePhong.getValueAt(row, 4).toString());
            String hinhAnh = tablePhong.getValueAt(row, 5).toString();
            TrangThaiPhong trangThai = (TrangThaiPhong) tablePhong.getValueAt(row, 6);

            // Hiển thị lên form
            txtMaPhong.setText(maPhong);
            txtSoPhong.setText(soPhong);
            txtSoGiuong.setText(String.valueOf(soGiuong));
            cboLoaiPhong.setSelectedItem(loaiPhong);
            txtGiaPhong.setText(String.valueOf(giaPhong));
            cboTrangThai.setSelectedItem(trangThai);

            // Hiển thị hình ảnh nếu có
            if (hinhAnh != null && !hinhAnh.isEmpty()) {
                loadImageToLabel(hinhAnh);
            }
        }
    }
    // Phương thức để xóa các trường nhập
    private void clearFields() {
        txtMaPhong.setText("");
        txtSoPhong.setText("");
        txtSoGiuong.setText("");
        cboLoaiPhong.setSelectedIndex(0); // Đặt lại về mục đầu tiên
        txtGiaPhong.setText("");
        lblANH.setText(""); // Giả sử lblANH là nơi hiển thị hình ảnh
    }

    // Phương thức để tải dữ liệu phòng (thường sẽ gọi loadDuLieuTuSQL)
    private void loadRoomData() {
        // Thực hiện của bạn để tải và hiển thị dữ liệu phòng
    }

   private void txtTimKiemActionPerformed(ActionEvent evt) {//GEN-FIRST:event_txtTimKiemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTimKiemActionPerformed




    private void txtSoGiuongActionPerformed(ActionEvent evt) {//GEN-FIRST:event_txtSoGiuongActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSoGiuongActionPerformed

    private void cboLoaiPhongActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cboLoaiPhongActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboLoaiPhongActionPerformed

    private void cboTrangThaiActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cboTrangThaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboTrangThaiActionPerformed

    private void txtTimKiemFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTimKiemFocusGained
        // TODO add your handling code here:
        if(txtTimKiem.getText().equals("Nhập Mã hoặc Số phòng")){
            txtTimKiem.setText("");
            txtTimKiem.setForeground(new Color(153,153,153));
        }
    }//GEN-LAST:event_txtTimKiemFocusGained

    //DUA VAO LOAI PHONG DE CHECK SO GIUONG VA GIA PHONNG




    private void txtTimKiemFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtTimKiemFocusLost
        // TODO add your handling code here:
        if(txtTimKiem.getText().equals("")){
            txtTimKiem.setText("Nhập Mã hoặc Số Phòng");
            txtTimKiem.setForeground(new Color(153,153,153));
        }
    }//GEN-LAST:event_txtTimKiemFocusLost


    private void txtSoPhongActionPerformed(ActionEvent evt) {//GEN-FIRST:event_txtSoPhongActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSoPhongActionPerformed

    private void txtGiaPhongActionPerformed(ActionEvent evt) {//GEN-FIRST:event_txtGiaPhongActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtGiaPhongActionPerformed

//    private void btnAnhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnhActionPerformed
//        // TODO add your handling code here:
//    }//GEN-LAST:event_btnAnhActionPerformed

    private void btnAnhActionPerformed(ActionEvent evt) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn hình ảnh");

        // Chỉ cho phép chọn tệp hình ảnh
        fileChooser.setFileFilter(new FileNameExtensionFilter("Hình ảnh", "jpg", "jpeg", "png", "gif"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();

            // Hiển thị hình ảnh lên JLabel
            loadImageToLabel(filePath);
        }
    }

    private void loadImageToLabel(String filePath) {
        // Tạo một đối tượng ImageIcon từ đường dẫn hình ảnh
        ImageIcon imageIcon = new ImageIcon(filePath);

        // Tùy chỉnh kích thước của hình ảnh (nếu cần)
        Image image = imageIcon.getImage(); // Chuyển đổi ImageIcon thành Image
        Image scaledImage = image.getScaledInstance(lblANH.getWidth(), lblANH.getHeight(), Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(scaledImage); // Tạo lại ImageIcon từ hình đã được thay đổi kích thước

        // Đặt hình ảnh vào JLabel
        lblANH.setIcon(imageIcon);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel b2;
    private javax.swing.JButton btnAnh;
    private javax.swing.JButton btnCapNhat;
    private javax.swing.JButton btnLamMoi;
    private javax.swing.JButton btnTimKiem;


    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblANH;
    private javax.swing.JLabel lblGiaPhong;
    private javax.swing.JLabel lblLoaiPhong;
    private javax.swing.JLabel lblSoGiuong1;
    private javax.swing.JLabel lblSoPhong;
    private javax.swing.JLabel lblTrangThaiPhong;
    private javax.swing.JLabel lblhaPhong;
    private javax.swing.JLabel lblmaPhong;
    private javax.swing.JPanel pC;
    private javax.swing.JPanel pTTPhong;
    private javax.swing.JTable tablePhong;
    private javax.swing.JTextField txtGiaPhong;
    private javax.swing.JTextField txtMaPhong;
    private javax.swing.JTextField txtSoGiuong;
    private javax.swing.JTextField txtSoPhong;
    private javax.swing.JTextField txtTimKiem;
    // End of variables declaration//GEN-END:variables
}
