/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;


import application.form.MainForm;


import client.RMIClient;
import entity.*;
import rmi.ChiTietPhieuDatService;
import rmi.PhongService;
import util.TaiKhoanDangNhap;


public class FormDatPhong extends javax.swing.JPanel {

    /**
     * Creates new form FormDatPhong
     */
//	private PhieuDatPhong_DAO phieuDatPhong_DAO = new PhieuDatPhong_DAO();
//	private ChiTietPhieuDat_DAO ctPhieuDatPhong_DAO = new ChiTietPhieuDat_DAO();
//	private KhachHang_DAO khachHang_DAO = new KhachHang_DAO();
//	private Phong_DAO phong_DAO = new Phong_DAO();

    private RMIClient rmiClient;
    private PhongService phongService;
    private ChiTietPhieuDatService chiTietPhieuDatService;

	private String tenPhong;
	private ChiTietPhieuDat chiTietPhieuDat;
	private PhieuDatPhong phieuDatPhong;
	private ArrayList<KhachHang> dsKH;
	private KhachHang khachHang1;
    private NhanVien nhanVien;
	private Phong phong;
	private GUI_QuanLiDatPhong guiQuanLiDatPhong;
//	private FormNhanPhong FormNhanPhong;
	// chuyển dữ liệu sang form nhận phòng
    private String maPhieuDat;
    private String maPhong;



	
	// nhân viên (do chưa có lớp tài khoản nên nhân viên chưa làm)
	
	
    public FormDatPhong(String room, MainForm mainForm, GUI_QuanLiDatPhong guiQuanLiDatPhong) throws SQLException {
        initComponents();
        rmiClient = new RMIClient();
        phongService = rmiClient.getPhongService();
        chiTietPhieuDatService= rmiClient.getChiTietPhieuDatService();
        this.guiQuanLiDatPhong = guiQuanLiDatPhong;
        xoaTrang();
        pCenter.setBorder(new EmptyBorder(10, 20, 10, 20)); // Thụt trên/dưới/trái/phải
        tenPhong = room;
      
        upLoadDuLieuTTPD();
        // sự kiện tính trả trước
        jtxtTraTruoc.getDocument().addDocumentListener(new DocumentListener() {
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
                SwingUtilities.invokeLater(() -> {
                    try {
                        // Lấy giá trị từ jtxtGiaTien và chuyển đổi thành BigDecimal
                        String giaTienText = jtxtGiaTien.getText().replace(".", "").replace(",", ".");
                        BigDecimal giaTien = new BigDecimal(giaTienText);

                        // Lấy giá trị từ jtxtTraTruoc và chuyển đổi thành BigDecimal
                        String traTruocText = jtxtTraTruoc.getText().replace(".", "").replace(",", ".");
                        BigDecimal traTruoc = new BigDecimal(traTruocText);

                        // Tính toán giá trị giảm trừ
                        BigDecimal giamTru = giaTien.subtract(traTruoc);

                        // Kiểm tra nếu số tiền trả trước vượt quá giá tiền
                        if (giamTru.compareTo(BigDecimal.ZERO) < 0) {
                            mes("Số tiền trả trước vượt quá giá tiền quy định!!");
                            jtxtTraTruoc.setText(""); // Đặt lại trường trả trước
                            jtxtGiamTru.setText("");  // Đặt lại trường giảm trừ
                        } else {
                            // Định dạng và hiển thị kết quả giảm trừ
                            jtxtGiamTru.setText(formatToVietnamCurrency(giamTru.toString()));
                        }
                    } catch (NumberFormatException | NullPointerException ex) {
                        // Xử lý lỗi định dạng hoặc dữ liệu trống
                        mes("Lỗi định dạng tiền");
                        jtxtGiamTru.setText("0"); // Đặt lại trường giảm trừ nếu có lỗi
                    }
                });
            }
        });
      // sự kiện nhấn button hủy bỏ
        btnHuyBo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				SwingUtilities.getWindowAncestor(FormDatPhong.this).dispose();
				
			}
		});
        // sự kiện tìm kiếm khách hàng
        btnTimSDT.addActionListener(new ActionListener() {
            private DefaultTableModel model;

			@Override
            public void actionPerformed(ActionEvent e) {
                dsKH = null;

                try {
                    dsKH = (ArrayList<KhachHang>) rmiClient.getKhachHangBySoDienThoai(jtxtSoDienThoai.getText());
//                    System.out.println(jtxtSoDienThoai.getText());
//                    System.out.println("Kết quả tìm kiếm: " + dsKH); // Kiểm tra kết quả
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                // Lấy mô hình của bảng
                 model = (DefaultTableModel) jTable1.getModel();
                model.setRowCount(0); // Xóa tất cả các hàng trong bảng
                
                // Thêm từng khách hàng vào bảng
                if (dsKH != null && !dsKH.isEmpty()) {
                    for (KhachHang kh : dsKH) {
                        model.addRow(new Object[] {
                            kh.getTenKhachHang(),
                            kh.getSoDienThoai(),
                            kh.getLoaiKhachHang() ? "Vãng lai" : "Thành viên",
                            kh.getCccd()
                        });
                    }
                    String message = MessageFormat.format("Tìm thấy {0} kết quả:", dsKH.size());
                    mes(message);
                    
                } else {
//                    JOptionPane.showMessageDialog(btnDatPhong, "Không tìm thấy");
                    int option = JOptionPane.showOptionDialog(btnDatPhong,
                            "Không tìm thấy",
                            "Thông báo",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE,
                            null,
                            new Object[]{"Tiếp tục", "Kết thúc"},
                            null);

                    // Xử lý lựa chọn
                    if (option == JOptionPane.YES_OPTION) {
						// Nếu chọn "Tiếp tục", chuyển đến GUI_Customer
                        try {
							mainForm.showForm(new GUI_KhachHang());
							SwingUtilities.getWindowAncestor(FormDatPhong.this).dispose();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} // Gọi phương thức từ MainForm
                    } else if (option == JOptionPane.NO_OPTION) {
                    	SwingUtilities.getWindowAncestor(FormDatPhong.this).dispose();
                    }

                }
            }
        });
        jTable1.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				int row = jTable1.getSelectedRow();
				if(row==-1) {
					mes("click vào bảng Khách hàng");
				}
				else {
					String soCCCD = jTable1.getValueAt(row,3).toString();
					for (KhachHang khachHang : dsKH) {
						if(khachHang.getCccd().equalsIgnoreCase(soCCCD)) {
							jtxtSoDienThoai.setText(khachHang.getSoDienThoai());
							jtxtNgaySinh.setText(chuyenDoiFormat(khachHang.getNgaySinh().toString()));
							jtxtTenKhachHang.setText(khachHang.getTenKhachHang());
							jtxtSoCCCD.setText(khachHang.getCccd());
							khachHang1 = khachHang;
							if(khachHang.getGioiTinh()==true)
								jradioNam.setSelected(true);
							
							else {
								jradioNu.setSelected(true);
							}
							String loaiKH="";
							if(khachHang.getLoaiKhachHang()==false)
								loaiKH = "VIP (thành viên)";
							else 
								loaiKH = "thường (vãng lai)";
							jcomboBoxLoaiKH.setSelectedItem(loaiKH);
							
						}
					}
				}
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
        btnDatPhong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Tạo đối tượng phòng từ RMI
                    Phong phong1 = rmiClient.getPhongByMaPhong(room);

                    // Kiểm tra trạng thái phòng (== 2 cho Trống)
                    if (phong1 != null && phong1.getTrangThaiPhong().getMaTrangThai() == 2) {
                        if (!kiemTra()) {
                            mes("Vui lòng nhập đầy đủ các thông tin!!");
                            return;
                        }

                        // Kiểm tra xem phòng có bị đặt trước không
                        String ngayNhanPhongText = jtxtNgayNhanPhong.getText();
                        String gioTraPhongText = jtxtGioTraPhong.getText();
                        if (!kiemTraDsPhongDaDatbyPhong(room, gioTraPhongText, ngayNhanPhongText)) {
                            mes("Phòng này đã được đặt trước trong khoảng thời gian sắp tới!!");
                            return;
                        }

                        // Lấy dữ liệu từ các trường nhập
                        String soLuongNguoiText = jtxtSoLuongNguoi.getText();
                        String ngayDatText = jtxtNgayDat.getText();
                        String gioNhanPhongText = jtxtGioNhanPhong.getText();
                        String ngayTraPhongText = jtxtNgayTraPhong.getText();
                        String soGioText = jComboBoxSoGio.getSelectedItem().toString();

                        // Lấy thông tin nhân viên và khách hàng
                        NhanVien nv = TaiKhoanDangNhap.getTaiKhoan().getMaNhanVien();
                        double traTruoc = Double.parseDouble(jtxtTraTruoc.getText());

                        // Tạo PhieuDatPhong
                        PhieuDatPhong phieuDatPhong = new PhieuDatPhong(
                                null, khachHang1, nhanVien,
                                LocalDate.parse(ngayDatText, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                jComboBoxKieuDat.getSelectedIndex() == 0 ? true : false,
                                Integer.parseInt(soLuongNguoiText), traTruoc
                        );

                        // Gọi hàm tạo PhieuDatPhong qua RMI
                        PhieuDatPhong phieuDatPhongTao = rmiClient.createPhieuDatPhong(phieuDatPhong);
                        if (phieuDatPhongTao != null) {
                            String maPhieuDat = phieuDatPhongTao.getMaPhieuDat();
                            phieuDatPhong = phieuDatPhongTao;

                            // Tạo ChiTietPhieuDat
                            ChiTietPhieuDat chiTietPhieuDat = new ChiTietPhieuDat(
                                    phong1, phieuDatPhong, true,
                                    LocalDate.parse(ngayNhanPhongText, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                    LocalTime.parse(gioNhanPhongText, DateTimeFormatter.ofPattern("HH:mm")),
                                    LocalDate.parse(ngayTraPhongText, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                    LocalTime.parse(gioTraPhongText, DateTimeFormatter.ofPattern("HH:mm")),
                                    Integer.parseInt(soGioText));

                            // Kiểm tra ràng buộc ngày nhận phòng
                            if (!kiemTraRangBuoc(chiTietPhieuDat)) {
                                mes("Ngày nhận phòng phải sau ngày hiện hành!!");
                                jtxtNgayNhanPhong.requestFocus();
                                return;
                            }

                            // Gọi hàm tạo ChiTietPhieuDat qua RMI
                            if (rmiClient.createCTPhieuDat(chiTietPhieuDat)) {
                                mes("Đặt phòng thành công");

                                // Lưu thông tin phiếu đặt
                                SharedData.addPhieuDatInfo(maPhieuDat, Double.valueOf(jtxtTraTruoc.getText()), khachHang1.getMaKhachHang(), tenPhong);
                                System.out.println("Số lượng phần tử trong danh sách: " + SharedData.getPhieuDatList().size());

                                // Cập nhật giao diện
                                ArrayList<Phong> dsPhong = (ArrayList<Phong>) rmiClient.loadTatCaPhong();
                                SwingUtilities.getWindowAncestor(FormDatPhong.this).dispose();
                                guiQuanLiDatPhong.xoaDuLieuPhong();
                                guiQuanLiDatPhong.upLoadDataJpanel(dsPhong);
                            }
                        } else {
                            System.err.println("Lỗi: createPhieuDatPhong trả về null!!"); // GHI LOG
                            System.err.println("Dữ liệu phiếu gửi đi: " + phieuDatPhong); // Coi kỹ object có đúng chưa
                            throw new RemoteException("Không thể tạo phiếu đặt!");

                        }
                    } else {
                        mes("Phòng phải ở trạng thái Trống!!");
                    }
                } catch (NumberFormatException ex) {
                    mes("Vui lòng nhập số hợp lệ cho các trường yêu cầu.");
                } catch (RemoteException ex) {
                    mes("Lỗi khi đặt phòng: " + ex.getMessage());
                } catch (Exception ex) {
                    mes("Lỗi không xác định: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

    }
    // Giả định bạn có một phương thức để lấy thông tin nhân viên hiện tại
    private entity.NhanVien getNhanVienByCurrentUser() {
        entity.NhanVien nhanVien = new entity.NhanVien();
        nhanVien.setMaNhanVien("NV2501002");
        nhanVien.setHoTenNhanVien("Huynh Le Hoan");
        return nhanVien;
    }
//
//	/**
//     * This method is called from within the constructor to initialize the form.
//     * WARNING: Do NOT modify this code. The content of this method is always
//     * regenerated by the Form Editor.
//     */
//    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupGT = new javax.swing.ButtonGroup();
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

        setLayout(new java.awt.BorderLayout());

        pCenter.setBackground(new java.awt.Color(227, 227, 227));
        pCenter.setLayout(new java.awt.BorderLayout());

        pTTKH.setBackground(new java.awt.Color(227, 227, 227));
        pTTKH.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Thông Tin Khách Hàng", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
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
        jtxtNgaySinh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtNgaySinhActionPerformed(evt);
            }
        });
        pTTKH.add(jtxtNgaySinh, new org.netbeans.lib.awtextra.AbsoluteConstraints(499, 32, 170, 32));

        btnTimSDT.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/png/add.png"))); // NOI18N
        btnTimSDT.setToolTipText("");
        btnTimSDT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTimSDTActionPerformed(evt);
            }
        });
        pTTKH.add(btnTimSDT, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 30, -1, 30));

        lblNgaySinh.setText("Ngày sinh:");
        pTTKH.add(lblNgaySinh, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 40, 90, -1));

        jtxtSoDienThoai.setText("jTextField1");
        jtxtSoDienThoai.setPreferredSize(new java.awt.Dimension(75, 30));
        jtxtSoDienThoai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtSoDienThoaiActionPerformed(evt);
            }
        });
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
        jtxtSoCCCD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtSoCCCDActionPerformed(evt);
            }
        });
        pTTKH.add(jtxtSoCCCD, new org.netbeans.lib.awtextra.AbsoluteConstraints(499, 70, 170, 32));

        boxGT.setBackground(new java.awt.Color(227, 227, 227));
        boxGT.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblGT.setText("Giới tính:");

        buttonGroupGT.add(jradioNam);
        jradioNam.setText("Nam");
        jradioNam.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jradioNam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jradioNamActionPerformed(evt);
            }
        });

        buttonGroupGT.add(jradioNu);
        jradioNu.setText("Nữ");
        jradioNu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jradioNuActionPerformed(evt);
            }
        });

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
        jcomboBoxLoaiKH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcomboBoxLoaiKHActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout boxLoaiKHLayout = new javax.swing.GroupLayout(boxLoaiKH);
        boxLoaiKH.setLayout(boxLoaiKHLayout);
        boxLoaiKHLayout.setHorizontalGroup(
            boxLoaiKHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(boxLoaiKHLayout.createSequentialGroup()
                .addComponent(lblLoaiKH, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcomboBoxLoaiKH, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        boxLoaiKHLayout.setVerticalGroup(
            boxLoaiKHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(boxLoaiKHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lblLoaiKH, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jcomboBoxLoaiKH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pTTKH.add(boxLoaiKH, new org.netbeans.lib.awtextra.AbsoluteConstraints(334, 108, 330, -1));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tên khách hàng", "Số điện thoại", "Loại khách hàng", "Số CCCD"
            }
        ));
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
        }

        pTTKH.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, 640, 100));

        pCenter.add(pTTKH, java.awt.BorderLayout.NORTH);

        pTTPD.setBackground(new java.awt.Color(227, 227, 227));
        pTTPD.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Thông Tin Phòng Đặt", javax.swing.border.TitledBorder.LEADING, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        pTTPD.setMinimumSize(new java.awt.Dimension(0, 0));
        pTTPD.setPreferredSize(new java.awt.Dimension(671, 270));
        pTTPD.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblGiaTien.setText("Giá tiền:");
        pTTPD.add(lblGiaTien, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 30, 70, 32));

        jtxtGiaTien.setText("jTextField1");
        jtxtGiaTien.setPreferredSize(new java.awt.Dimension(70, 30));
        jtxtGiaTien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtGiaTienActionPerformed(evt);
            }
        });
        pTTPD.add(jtxtGiaTien, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 30, 130, -1));

        jtxtNgayDat.setText("jTextField2");
        jtxtNgayDat.setPreferredSize(new java.awt.Dimension(75, 30));
        jtxtNgayDat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtNgayDatActionPerformed(evt);
            }
        });
        pTTPD.add(jtxtNgayDat, new org.netbeans.lib.awtextra.AbsoluteConstraints(106, 80, 150, 32));

        lblKieuDat.setText("Kiểu đặt:");
        pTTPD.add(lblKieuDat, new org.netbeans.lib.awtextra.AbsoluteConstraints(283, 86, 100, 20));

        jPanel1.setBackground(new java.awt.Color(227, 227, 227));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jtxtTraTruoc.setText("jTextField1");
        jtxtTraTruoc.setPreferredSize(new java.awt.Dimension(70, 30));
        jtxtTraTruoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtTraTruocActionPerformed(evt);
            }
        });
        jPanel1.add(jtxtTraTruoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, 130, 32));

        jtxtGiamTru.setText("jTextField1");
        jtxtGiamTru.setPreferredSize(new java.awt.Dimension(70, 30));
        jtxtGiamTru.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtGiamTruActionPerformed(evt);
            }
        });
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
        jtxtNgayTraPhong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtNgayTraPhongActionPerformed(evt);
            }
        });
        jPanel6.add(jtxtNgayTraPhong, new org.netbeans.lib.awtextra.AbsoluteConstraints(166, 50, 140, 32));

        lblNgayNhanPhong.setText("Ngày nhận phòng:");
        jPanel6.add(lblNgayNhanPhong, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 160, 32));

        jtxtNgayNhanPhong.setText("jTextField2");
        jtxtNgayNhanPhong.setPreferredSize(new java.awt.Dimension(75, 30));
        jtxtNgayNhanPhong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtNgayNhanPhongActionPerformed(evt);
            }
        });
        jPanel6.add(jtxtNgayNhanPhong, new org.netbeans.lib.awtextra.AbsoluteConstraints(166, 10, 140, 32));

        lblGioNhanPhong.setText("Giờ nhận phòng:");
        jPanel6.add(lblGioNhanPhong, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 10, 120, 32));

        jtxtGioNhanPhong.setText("jTextField2");
        jtxtGioNhanPhong.setPreferredSize(new java.awt.Dimension(75, 30));
        jtxtGioNhanPhong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtGioNhanPhongActionPerformed(evt);
            }
        });
        jPanel6.add(jtxtGioNhanPhong, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 10, 110, 32));

        lblNgayTraPhong.setText("Ngày trả phòng:");
        jPanel6.add(lblNgayTraPhong, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 160, 32));

        jtxtGioTraPhong.setText("jTextField2");
        jtxtGioTraPhong.setPreferredSize(new java.awt.Dimension(75, 30));
        jtxtGioTraPhong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtGioTraPhongActionPerformed(evt);
            }
        });
        jPanel6.add(jtxtGioTraPhong, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 50, 110, 32));

        lblSoLuongNguoi.setText("Số lượng người:");
        jPanel6.add(lblSoLuongNguoi, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 160, 32));

        jtxtSoLuongNguoi.setText("jTextField2");
        jtxtSoLuongNguoi.setPreferredSize(new java.awt.Dimension(75, 30));
        jtxtSoLuongNguoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtSoLuongNguoiActionPerformed(evt);
            }
        });
        jPanel6.add(jtxtSoLuongNguoi, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 90, 80, 32));

        jComboBoxSoGio.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));
        jPanel6.add(jComboBoxSoGio, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 90, 80, 30));

        pTTPD.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 130, 570, 150));

        pCenter.add(pTTPD, java.awt.BorderLayout.CENTER);

        pSouth.setBackground(new java.awt.Color(227, 227, 227));
        pSouth.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        boxButton.setBackground(new java.awt.Color(227, 227, 227));

        btnDatPhong.setText("Đặt phòng");

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
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtNgaySinhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtNgaySinhActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtNgaySinhActionPerformed

    private void jtxtSoDienThoaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtSoDienThoaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtSoDienThoaiActionPerformed

    private void jtxtSoCCCDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtSoCCCDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtSoCCCDActionPerformed

    private void jradioNuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jradioNuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jradioNuActionPerformed

    private void jradioNamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jradioNamActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jradioNamActionPerformed

    private void jcomboBoxLoaiKHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcomboBoxLoaiKHActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jcomboBoxLoaiKHActionPerformed

    private void jtxtGiaTienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtGiaTienActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtGiaTienActionPerformed

    private void btnTimSDTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimSDTActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTimSDTActionPerformed

    private void jtxtTraTruocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtTraTruocActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtTraTruocActionPerformed

    private void jtxtGiamTruActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtGiamTruActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtGiamTruActionPerformed

    private void jtxtNgayDatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtNgayDatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtNgayDatActionPerformed

    private void jtxtNgayTraPhongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtNgayTraPhongActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtNgayTraPhongActionPerformed

    private void jtxtNgayNhanPhongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtNgayNhanPhongActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtNgayNhanPhongActionPerformed

    private void jtxtGioNhanPhongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtGioNhanPhongActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtGioNhanPhongActionPerformed

    private void jtxtGioTraPhongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtGioTraPhongActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtGioTraPhongActionPerformed

    private void jtxtSoLuongNguoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtSoLuongNguoiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtSoLuongNguoiActionPerformed

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
    	
    	jtxtSoLuongNguoi.setText("");
    	jtxtTenKhachHang.setText("");
    	jtxtTraTruoc.setText("");
    }
	public void mes(String x) {
		JOptionPane.showMessageDialog(this,x);
	}
	public String chuyenDoiFormat(String dateString) {
		 DateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	     DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	     String convertedDateString=null;
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
	public void upLoadDuLieuTTPD() {
		
		String ngayHienTai = chuyenDoiFormat(LocalDate.now().toString());
		LocalTime giohienTai = LocalTime.now();
		jtxtNgayDat.setText(ngayHienTai);
		//set các thuộc tính enabled
		jtxtNgayDat.setEnabled(false);
		jtxtTenKhachHang.setEditable(false);
		jtxtNgaySinh.setEditable(false);
		jtxtSoCCCD.setEditable(false);
		jradioNam.setEnabled(false);
		jradioNu.setEnabled(false);
		jcomboBoxLoaiKH.setEditable(false);
		jtxtGiaTien.setEditable(false);
		jtxtGiamTru.setEditable(false);
		
		jComboBoxKieuDat.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        String selectedOption = (String) jComboBoxKieuDat.getSelectedItem();
		        jComboBoxSoGio.setSelectedIndex(1);  // Đặt mặc định số giờ là 3
		        for (ActionListener al : jComboBoxSoGio.getActionListeners()) {
		            jComboBoxSoGio.removeActionListener(al);
		        }
		        if ("Theo Giờ".equals(selectedOption)) {
		            xoaThuocTinh();
		            jtxtNgayNhanPhong.setText(ngayHienTai);
		            jtxtNgayTraPhong.setText(ngayHienTai);
		            jtxtNgayNhanPhong.setEnabled(false);
		            jtxtNgayTraPhong.setEnabled(false);
		            jtxtGioNhanPhong.setText(formatLocalTime(giohienTai));
		            jtxtGioTraPhong.setText(formatLocalTime(giohienTai.plusHours(1))); 
		            jtxtGioNhanPhong.setEditable(true); 
		            jtxtGioTraPhong.setEnabled(false);

		            // Lắng nghe thay đổi trong jComboBoxSoGio
		            jComboBoxSoGio.addActionListener(new ActionListener() {
		                @Override
		                public void actionPerformed(ActionEvent e) {
		                	kiemTraVaCapNhatGioTraPhong();
		                }
		            });

		           
		        }
		        // Các đoạn code khác cho "Theo Ngày"...
		        else if ("Theo Ngày".equals(selectedOption)) {
		            xoaThuocTinh();
		            jtxtGioNhanPhong.setText("14:00");
		            jtxtGioTraPhong.setText("12:00");
		            jtxtGioNhanPhong.setEnabled(false);
		            jtxtGioTraPhong.setEnabled(false);
		            // Kiểm tra giờ hiện tại
		            LocalTime gioHienTai = LocalTime.now();
		            LocalDate ngayNhanPhong = LocalDate.now();
		            // Nếu giờ hiện tại lớn hơn 14h, đặt ngày nhận phòng là ngày mai
		            if (gioHienTai.isAfter(LocalTime.of(14, 0))) {
		            	ngayNhanPhong = LocalDate.now().plusDays(1);
		                jtxtNgayNhanPhong.setText(chuyenDoiFormat(ngayNhanPhong.toString()));
		            } else {
		                jtxtNgayNhanPhong.setText(chuyenDoiFormat(LocalDate.now().toString()));
		            }
		            jtxtNgayTraPhong.setText(chuyenDoiFormat(ngayNhanPhong.plusDays(1).toString()));
		            jtxtNgayNhanPhong.setEditable(true); 
		            jtxtNgayTraPhong.setEnabled(false);

		            // Lắng nghe thay đổi trong jComboBoxSoGio
		            jComboBoxSoGio.addActionListener(new ActionListener() {
		                @Override
		                public void actionPerformed(ActionEvent e) {
		                	kiemTraVaCapNhatNgayTraPhong();
		                }
		            });
		           
		        }
		    }
		});
		 // Lắng nghe thay đổi trong jtxtGioNhanPhong
        jtxtGioNhanPhong.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
            	kiemTraVaCapNhatGioTraPhong();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
            	kiemTraVaCapNhatGioTraPhong();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            	kiemTraVaCapNhatGioTraPhong();
            }
        });
        // Lắng nghe thay đổi trong jtxtNgayNhanPhong
        jtxtNgayNhanPhong.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
            	kiemTraVaCapNhatNgayTraPhong();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
            	kiemTraVaCapNhatNgayTraPhong();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            	kiemTraVaCapNhatNgayTraPhong();
            }
        });

		// lắng nghe jtxtSoLuongNguoi
        jtxtSoLuongNguoi.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				kiemTraSoLuongNguoi();
				
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				kiemTraSoLuongNguoi();
				
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				kiemTraSoLuongNguoi();
				
			}
		});
		
		
	}
	private void capNhatGioTraPhong() {
	    try {
	        int soGio = Integer.parseInt((String) jComboBoxSoGio.getSelectedItem());
	        LocalTime gioNhanPhong = LocalTime.parse(jtxtGioNhanPhong.getText());
	        LocalDate ngayNhanPhong = LocalDate.parse(jtxtNgayNhanPhong.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	        
	        // Tính giờ trả phòng
	        LocalTime gioTraPhong = gioNhanPhong.plusHours(soGio);
	        LocalDate ngayTraPhong = ngayNhanPhong;
	        
	        // Kiểm tra nếu giờ trả phòng vượt quá 23:59
	        if (gioTraPhong.isBefore(gioNhanPhong)) {
	            ngayTraPhong = ngayTraPhong.plusDays(1); // Tăng ngày lên 1 nếu qua ngày hôm sau
	        }
	        
	        // Cập nhật lại giờ và ngày trả phòng
	        jtxtGioTraPhong.setText(gioTraPhong.toString());
	        jtxtNgayTraPhong.setText(ngayTraPhong.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
	    } catch (Exception ex) {
	        // Xử lý ngoại lệ nếu cần
	    }
	}

	private void capNhatNgayTraPhong() {
	    try {
	        int soNgay = Integer.parseInt((String) jComboBoxSoGio.getSelectedItem());
	        LocalDate ngayNhanPhong = LocalDate.parse(jtxtNgayNhanPhong.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	        LocalDate ngayTraPhong = ngayNhanPhong.plusDays(soNgay);
	        jtxtNgayTraPhong.setText(ngayTraPhong.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
	    } catch (Exception ex) {
	        // Xử lý ngoại lệ nếu cần
	    }
	}

	
	// Phương thức tính tiền
// Sửa hàm calculateTotalPrice để gọi getTienPhong
    private void calculateTotalPrice() {
        // Kiểm tra dữ liệu có đầy đủ không
        if (jtxtNgayNhanPhong.getText().isEmpty() || jtxtGioNhanPhong.getText().isEmpty() ||
                jtxtNgayTraPhong.getText().isEmpty() || jtxtGioTraPhong.getText().isEmpty()) {
            return; // Thoát nếu dữ liệu chưa đầy đủ
        }

        // Kiểm tra và tải danh sách phòng từ cơ sở dữ liệu
        ArrayList<Phong> dsPhong = (ArrayList<Phong>) rmiClient.loadTatCaPhong();
        for (Phong phong : dsPhong) {
            if (tenPhong.equalsIgnoreCase(phong.getMaPhong())) {
                this.phong = phong;

                // Định dạng ngày và giờ
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

                // Tạo đối tượng ChiTietPhieuDat
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

                // Tạo đối tượng PhieuDatPhong
                phieuDatPhong = new PhieuDatPhong(
                        "",
                        null,
                        null,
                        LocalDate.parse(jtxtNgayDat.getText(), dateFormatter),
                        "Theo Giờ".equals(jComboBoxKieuDat.getSelectedItem().toString()),
                        1, 0.0
                );

                // Tính tiền phòng và hiển thị
                BigDecimal tienPhong = BigDecimal.valueOf(phieuDatPhong.getTienPhong(chiTietPhieuDat, 0));
                jtxtGiaTien.setText(formatToVietnamCurrency(tienPhong.toPlainString()));
            }
        }
    }


	
	

	// Hàm kiểm tra và cập nhật giờ trả phòng
	private void kiemTraVaCapNhatGioTraPhong() {
	    // Kiểm tra nếu jtxtGioNhanPhong có đủ 5 ký tự
	    if (jtxtGioNhanPhong.getText().length() == 5) {
	        capNhatGioTraPhong();
	        calculateTotalPrice();
	    }
	}

	// Hàm kiểm tra và cập nhật ngày trả phòng
	private void kiemTraVaCapNhatNgayTraPhong() {
	    // Kiểm tra nếu jtxtNgayNhanPhong có đủ 10 ký tự
	    if (jtxtNgayNhanPhong.getText().length() == 10) {
	        capNhatNgayTraPhong();
	        calculateTotalPrice();
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
	        jtxtSoLuongNguoi.getText().isEmpty() || 
	       jComboBoxSoGio.getSelectedIndex()==0) {
	        return false;
	    }

	    // Thêm điều kiện kiểm tra jtxtTraTruoc và jtxtGiamTien
	    if (jtxtTraTruoc.getText().isEmpty()) {
	        jtxtTraTruoc.setText("0");
	    }
	    if (jtxtGiamTru.getText().isEmpty()) {
	        jtxtGiamTru.setText(jtxtGiaTien.getText());
	    }

	    return true;
	}
	public static String formatToVietnamCurrency(String input) {
	    try {
	        // Loại bỏ các ký tự không phải số hoặc dấu thập phân
	        String numericText = input.replaceAll("[^\\d.,]", "");

	        // Kiểm tra nếu chuỗi rỗng hoặc không hợp lệ
	        if (numericText.isEmpty()) {
	            return "";
	        }

	        // Thay dấu ',' (phần thập phân ở Việt Nam) thành '.'
	        numericText = numericText.replace(",", ".");

	        // Chuyển chuỗi thành BigDecimal
	        BigDecimal amount = new BigDecimal(numericText);

	        // Tạo định dạng số
	        DecimalFormatSymbols vietnamSymbols = new DecimalFormatSymbols();
	        vietnamSymbols.setGroupingSeparator('.'); // Dấu phân cách hàng nghìn
	        vietnamSymbols.setDecimalSeparator(','); // Dấu phân cách phần thập phân

	        // Định dạng số tiền với phần thập phân
	        DecimalFormat vietnamFormat = new DecimalFormat("#,##0.00");
	        vietnamFormat.setDecimalFormatSymbols(vietnamSymbols);

	        // Trả về chuỗi đã định dạng
	        return vietnamFormat.format(amount);
	    } catch (NumberFormatException e) {
	        // Xử lý lỗi định dạng
	        return "";
	    }
	}
    public boolean kiemTraDsPhongDaDatbyPhong(String maPhong, String gioTraPhong, String ngayNhanPhong) {
        ArrayList<ChiTietPhieuDat> chiTietPhieuDat = (ArrayList<ChiTietPhieuDat>) rmiClient.timPhongDaDat();

        // Add null check here
        if (chiTietPhieuDat == null) {
            System.err.println("Không thể lấy danh sách phòng đã đặt");
            return true; // Return true to avoid booking conflicts or handle this case as needed
        }

        LocalTime gioTra = LocalTime.parse(gioTraPhong, DateTimeFormatter.ofPattern("HH:mm"));
        LocalDate ngayNhan = LocalDate.parse(ngayNhanPhong, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        for (ChiTietPhieuDat chiTietPhieuDat2 : chiTietPhieuDat) {
            if (maPhong.equals(chiTietPhieuDat2.getPhong().getMaPhong()) && ngayNhan.equals(chiTietPhieuDat2.getNgayNhanPhong())) {
                LocalTime gioNhanPhong = chiTietPhieuDat2.getGioNhanPhong();
                LocalTime gioTraPhong1 = chiTietPhieuDat2.getGioTraPhong();

                // Kiểm tra nếu giờ trả của khách mới (gioTra) trùng hoặc trước giờ nhận phòng của khách cũ
                if (gioTra.isBefore(gioNhanPhong) || gioTra.equals(gioNhanPhong)) {
                    return false;
                }

                // Kiểm tra nếu giờ nhận của khách mới trùng hoặc trước giờ trả phòng của khách cũ
                if (gioTra.isBefore(gioTraPhong1) || gioTra.equals(gioTraPhong1)) {
                    return false;
                }
            }
        }
        return true;
    }
	public void kiemTraSoLuongNguoi() {
		if(jtxtSoLuongNguoi.getText().length() >0) {
			if(!jtxtSoLuongNguoi.getText().matches("^[0-9]")) {
				mes("Vui lòng nhập số");
				
			}else {
				Phong phong = rmiClient.getPhongByMaPhong(tenPhong);
				int soLuongNguoi = Integer.parseInt(jtxtSoLuongNguoi.getText());
				switch (phong.getLoaiPhong().getTenLoai()) {
				case 1: {
					if(soLuongNguoi >2) {
						mes("Số lượng người không phù hợp");
					}
					break;
				}
				case 2: {
					if(soLuongNguoi >3) {
						mes("Số lượng người không phù hợp");
						
					}
					break;
				}
				case 3: {
					if(soLuongNguoi >5) {
						mes("Số lượng người không phù hợp");
						
					}
					break;
				}
				case 4: {
					if(soLuongNguoi >2) {
						mes("Số lượng người không phù hợp");
						
					}
					break;
				}
			
				
		}
			}
		}
		
	}
	public boolean kiemTraRangBuoc(ChiTietPhieuDat chiTietPhieuDat) {
		LocalDate ngayNhanPhong = chiTietPhieuDat.getNgayNhanPhong();
		if(ngayNhanPhong.isBefore(LocalDate.now())) {
			return false;
		}else
			return true;
	}





    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    // End of variables declaration//GEN-END:variables
    
    
}
