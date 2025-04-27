/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.Timer;

import application.form.MainForm;


import client.RMIClient;
import entity.ChiTietPhieuDat;
import entity.LoaiPhong;
import entity.PhieuDatPhong;
import entity.Phong;
import entity.TrangThaiPhong;
import rmi.ChiTietPhieuDatService;
import rmi.PhieuDatService;
import rmi.PhongService;

public class GUI_QuanLiDatPhong extends javax.swing.JPanel {

    private JPanel roomPanel;
    private int secondsElapsed=0;

    private RMIClient rmiClient;
    private PhongService phongService;
    private ChiTietPhieuDatService chiTietPhieuDatService;
    private PhieuDatService phieuDatService;
    private Phong phong = new Phong();


	private static MainForm mainForm;
	private boolean nhanGiaTri;
	private String ten;
	private FormNhanPhong formNhanPhong;

    private ArrayList<Phong> dsPhong;
    private ArrayList<Phong> dsPhongYC = new ArrayList<>();

    private Map<String, Timer> timers = new HashMap<>();



    public GUI_QuanLiDatPhong() throws Exception {
    	this.mainForm = mainForm;
    	this.formNhanPhong = formNhanPhong;
    	
    initComponents();
 // kết nối sql
        try {
            rmiClient = new RMIClient();
            phongService = rmiClient.getPhongService();
            chiTietPhieuDatService= rmiClient.getChiTietPhieuDatService();
            phieuDatService= rmiClient.getPhieuDatService();
            System.out.println("Connected to RMI server successfully!");


            dsPhong = (ArrayList<Phong>) rmiClient.loadTatCaPhong();

            System.out.println("Số phòng load được: " + dsPhong.size());
            xoaDuLieuPhong();
            upLoadDataJpanel(dsPhong);
        } catch (Exception e) {
            // Xử lý ngoại lệ nếu không thể kết nối
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to RMI server: " + e.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
// 		kiemTraPDQuaThoiGian();
 		upLoadDataJpanel(dsPhong);
// 		SharedData.loadPhieuDatFromDatabase();
 		btnLamMoi.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				xoaDuLieuPhong();
				
				try {
					dsPhong = (ArrayList<Phong>) rmiClient.loadTatCaPhong();
                    System.out.println("Số phòng load được: " + dsPhong.size());

                    dsPhongYC = dsPhong;
					upLoadDataJpanel(dsPhong);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
         // o tim kiem theo tu khoa
        btnTimKiem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tuKhoa = jtxtTimKiemMaPhong.getText().trim();

                if (tuKhoa.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Vui lòng nhập từ khóa tìm kiếm");
                    return;
                }

                try {
                    // Tìm theo trạng thái phòng
                    try {
                        TrangThaiPhong trangThai = TrangThaiPhong.valueOf(tuKhoa.toUpperCase());
                        ArrayList<Phong> ds = new ArrayList<>(rmiClient.getPhongByTrangThai(trangThai));
                        xoaDuLieuPhong();
                        upLoadDataJpanel(ds);
                        return;
                    } catch (IllegalArgumentException ex) {
                        // Không phải trạng thái hợp lệ
                    }

                    // Tìm theo loại phòng
                    try {
                        LoaiPhong loaiPhong = LoaiPhong.valueOf(tuKhoa.toUpperCase());
                        ArrayList<Phong> ds = new ArrayList<>(rmiClient.getPhongByLoaiPhong(loaiPhong));
                        xoaDuLieuPhong();
                        upLoadDataJpanel(ds);
                        return;
                    } catch (IllegalArgumentException ex) {
                        // Không phải loại phòng hợp lệ
                    }

                    // Tìm theo mã phòng
                    try {
                        Phong phong = rmiClient.getPhongByMaPhong(tuKhoa);
                        if (phong != null) {
                            ArrayList<Phong> ds = new ArrayList<>();
                            ds.add(phong);
                            xoaDuLieuPhong();
                            upLoadDataJpanel(ds);
                            return;
                        }
                    } catch (Exception ex) {
                        // Không tìm được theo mã phòng
                    }

                    // Tìm theo số điện thoại
                    List<Phong> dsSDT = rmiClient.getPhongBySoDienThoai(tuKhoa);
                    if (dsSDT != null && !dsSDT.isEmpty()) {
                        ArrayList<Phong> ds = new ArrayList<>(dsSDT);
                        xoaDuLieuPhong();
                        upLoadDataJpanel(ds);
                        return;
                    }

                    JOptionPane.showMessageDialog(null, "Không tìm thấy phòng phù hợp");

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Lỗi khi tìm kiếm: " + ex.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnCreateHD.addActionListener(new ActionListener() {
 		    @Override
 		    public void actionPerformed(ActionEvent e) {
 		        // Tạo JFrame chứa JPanel
// 		        JFrame frame = new JFrame("Trả Phòng Khách Đoàn");
// 		        FormTraPhongKhachDoan formTraPhongKhachDoan = null;
// 		       FormThongTinHoaDon formTTHoaDon = new FormThongTinHoaDon();
//				try {
//					formTraPhongKhachDoan = new FormTraPhongKhachDoan(GUI_QuanLiDatPhong.this,formTTHoaDon);
//				} catch (SQLException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				} // Giả sử class này là JPanel
// 		        frame.setContentPane(formTraPhongKhachDoan); // Đặt JPanel vào JFrame
// 		        frame.setSize(1160, 720); // Kích thước của JFrame
// 		       frame.setLocationRelativeTo(null);
// 		        frame.setResizable(false);
// 		        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
// 		        frame.setVisible(true); // Hiển thị JFrame
 		    }
 		});
 		btnCreateKH.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
//				JFrame frame = new JFrame("Đặt Phòng Khách Đoàn");
				
//				FormDatPhongKhachDoan formDatPhongKhachDoan = null;
//				try {
//					formDatPhongKhachDoan = new FormDatPhongKhachDoan(dsPhongYC, mainForm, GUI_QuanLiDatPhong.this);
//				} catch (SQLException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				} // Giả sử class này là JPanel
//				frame.setContentPane(formDatPhongKhachDoan); // Đặt JPanel vào JFrame
//				frame.setSize(1160, 650); // Kích thước của JFrame
//       frame.setLocationRelativeTo(null);
//				frame.setResizable(false);
//				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//				frame.setVisible(true); // Hiển thị JFrame
			}
		});
 		btnCheckIn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
//				JFrame frame = new JFrame("Nhận Phòng Khách Đoàn");
				
//				FormNhanPhongKhachDoan formNhanPhongKhachDoan = null;
//				try {
//					formNhanPhongKhachDoan = new FormNhanPhongKhachDoan(dsPhongYC ,mainForm, GUI_QuanLiDatPhong.this);
//				} catch (SQLException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				} // Giả sử class này là JPanel
//				frame.setContentPane(formNhanPhongKhachDoan); // Đặt JPanel vào JFrame
//				frame.setSize(1160, 650); // Kích thước của JFrame
//       frame.setLocationRelativeTo(null);
//				frame.setResizable(false);
//				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//				frame.setVisible(true); // Hiển thị JFrame
			}
		});
// tim kiem theo ngay, combobox..., so ngay
 		btnTimKiemTTPhong.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				int tinhTrangPhong = jcomboBoxTTP.getSelectedIndex();
				int loaiPhong = jComboBoxLoaiPhong.getSelectedIndex();
				int soNgay= -1;
				if(!jtxtTimKiemSoNgay.getText().trim().isEmpty())
				soNgay = Integer.parseInt(jtxtTimKiemSoNgay.getText());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Định dạng ngày mong muốn
				Date selectedDate = jDateChooserNgayNhan.getDate();  // Lấy ngày từ JDateChooser

				// Kiểm tra nếu ngày không bị null
				String ngayNhan = (selectedDate != null) ? sdf.format(selectedDate) : null;

//				System.out.println(ngayNhan);
				if(tinhTrangPhong!=0 && (loaiPhong==0 && soNgay==-1)){
					ArrayList<Phong> dsPhongTheoTTP = (ArrayList<Phong>) rmiClient.getPhongByTrangThai(TrangThaiPhong.fromInt(tinhTrangPhong));
					dsPhongYC = dsPhongTheoTTP;
					if(dsPhongTheoTTP==null)
						mes("Ko tìm thấy Tình Trạng Phòng mà bạn yêu cầu!!");
					else {
						xoaDuLieuPhong();
						try {
							upLoadDataJpanel(dsPhongTheoTTP);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						String temp = Integer.toString(dsPhongTheoTTP.size());
						mes("Tìm thấy "+ temp+" Kết quả");
					}
					
				}
				else if(loaiPhong!=0 && (tinhTrangPhong==0 && soNgay==-1)){
					ArrayList<Phong> dsPhongTheoLP = (ArrayList<Phong>) rmiClient.getPhongByLoaiPhong(LoaiPhong.fromInt(loaiPhong));
					dsPhongYC = dsPhongTheoLP;
					if(dsPhongTheoLP==null)
						mes("Ko tìm thấy Loại Phòng mà bạn yêu cầu!!");
					else {
						xoaDuLieuPhong();
						try {
							upLoadDataJpanel(dsPhongTheoLP);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						String temp = Integer.toString(dsPhongTheoLP.size());
						mes("Tìm thấy "+ temp+" Kết quả");
					}
				}else if(loaiPhong!=0 && (tinhTrangPhong!=0 && soNgay==-1)) {
					ArrayList<Phong> dsPhongTheoLPvaTTP = (ArrayList<Phong>) rmiClient.getPhongByLoaiVaTrangThai(LoaiPhong.fromInt(loaiPhong), TrangThaiPhong.fromInt(tinhTrangPhong));
					dsPhongYC = dsPhongTheoLPvaTTP;
					if(dsPhongTheoLPvaTTP==null)
						mes("Ko tìm thấy Thông Tin Phòng mà bạn yêu cầu!!");
					else {
						xoaDuLieuPhong();
						try {
							upLoadDataJpanel(dsPhongTheoLPvaTTP);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						String temp = Integer.toString(dsPhongTheoLPvaTTP.size());
						mes("Tìm thấy "+ temp+" Kết quả");
					}
				}else if(loaiPhong!=0 && tinhTrangPhong!=0 && soNgay!=-1) {
					if(ngayNhan==null)
						mes("Chọn ngày nhận!!");
					ArrayList<Phong> dsPhongTTPhongYC = (ArrayList<Phong>) rmiClient.timPhongTheoTTPhong(LoaiPhong.fromInt(loaiPhong),
							TrangThaiPhong.fromInt(tinhTrangPhong), soNgay, ngayNhan);
					dsPhongYC = dsPhongTTPhongYC;
					if(dsPhongTTPhongYC==null)
						mes("Ko tìm thấy Thông Tin Phòng mà bạn yêu cầu!!");
					else {
						xoaDuLieuPhong();
						try {
							upLoadDataJpanel(dsPhongTTPhongYC);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						String temp = Integer.toString(dsPhongTTPhongYC.size());
						mes("Tìm thấy "+ temp+" Kết quả");
					}
					
				}else {
					xoaDuLieuPhong();
					try {
						upLoadDataJpanel(dsPhong);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String temp = Integer.toString(dsPhong.size());
					mes("Tìm thấy "+ temp+" Kết quả");
					
				}
				// trường hợp đã điền 2 phiếu đó
				
					
				
				
			}
		});
// 		checkDKDatPhong(nhanGiaTri,ten);

 }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pGUI = new javax.swing.JPanel();
        Pheader = new javax.swing.JPanel();
        pN = new javax.swing.JPanel();
        pC = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        pBox1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        jPanel2 = new javax.swing.JPanel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        jLabel3 = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        jPanel1 = new javax.swing.JPanel();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        jLabel5 = new javax.swing.JLabel();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        jPanel4 = new javax.swing.JPanel();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        jLabel7 = new javax.swing.JLabel();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        jLabel6 = new javax.swing.JLabel();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        jLabel8 = new javax.swing.JLabel();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        jDateChooserNgayNhan = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        jtxtTimKiemSoNgay = new javax.swing.JTextField();
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        btnTimKiemTTPhong = new javax.swing.JButton();
        pBox2 = new javax.swing.JPanel();
        btnCreateKH = new javax.swing.JButton();
        filler17 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(10, 32767));
        btnCheckIn = new javax.swing.JButton();
        filler12 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(30, 0), new java.awt.Dimension(10, 32767));
        btnCreateHD = new javax.swing.JButton();
        filler13 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(70, 0), new java.awt.Dimension(160, 32767));
        jcomboBoxTTP = new javax.swing.JComboBox<>();
        filler14 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        jComboBoxLoaiPhong = new javax.swing.JComboBox<>();
        filler15 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        jtxtTimKiemMaPhong = new javax.swing.JTextField();
        btnTimKiem = new javax.swing.JButton();
        filler16 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(10, 0), new java.awt.Dimension(10, 32767));
        btnLamMoi = new javax.swing.JButton();
        pS = new javax.swing.JPanel();
        lblDsCacPhong = new javax.swing.JLabel();
        pDanhSachPhong = new javax.swing.JPanel();
        lblTang1 = new javax.swing.JPanel();
        pT1 = new javax.swing.JPanel();
        jtxtTang1 = new javax.swing.JLabel();
        lblTang2 = new javax.swing.JPanel();
        pT2 = new javax.swing.JPanel();
        jtxtTang2 = new javax.swing.JLabel();
        lblTang3 = new javax.swing.JPanel();
        pT3 = new javax.swing.JPanel();
        jtxtTang3 = new javax.swing.JLabel();
        lblTang4 = new javax.swing.JPanel();
        pT4 = new javax.swing.JPanel();
        jtxtTang4 = new javax.swing.JLabel();
        lblTang5 = new javax.swing.JPanel();
        pT5 = new javax.swing.JPanel();
        jtxtTang5 = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(700, 626));
        setPreferredSize(new java.awt.Dimension(700, 626));
        setLayout(new java.awt.GridLayout(1, 0));

        pGUI.setMinimumSize(new java.awt.Dimension(700, 609));
        pGUI.setPreferredSize(new java.awt.Dimension(700, 691));
        pGUI.setLayout(new java.awt.BorderLayout());

        Pheader.setMinimumSize(new java.awt.Dimension(700, 161));
        Pheader.setLayout(new java.awt.BorderLayout());

        pN.setBackground(new java.awt.Color(255, 255, 255));
        pN.setPreferredSize(new java.awt.Dimension(700, 30));

        javax.swing.GroupLayout pNLayout = new javax.swing.GroupLayout(pN);
        pN.setLayout(pNLayout);
        pNLayout.setHorizontalGroup(
            pNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1138, Short.MAX_VALUE)
        );
        pNLayout.setVerticalGroup(
            pNLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        Pheader.add(pN, java.awt.BorderLayout.PAGE_START);

        pC.setBackground(new java.awt.Color(227, 227, 227));
        pC.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 15, true));
        pC.setMaximumSize(new java.awt.Dimension(1068, 161));
        pC.setMinimumSize(new java.awt.Dimension(1068, 161));
        pC.setPreferredSize(new java.awt.Dimension(700, 161));
        pC.setLayout(new java.awt.BorderLayout());

        jPanel3.setBackground(new java.awt.Color(227, 227, 227));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.Y_AXIS));

        pBox1.setBackground(new java.awt.Color(227, 227, 227));
        pBox1.setMaximumSize(new java.awt.Dimension(1210, 60));
        pBox1.setMinimumSize(new java.awt.Dimension(1024, 60));
        pBox1.setPreferredSize(new java.awt.Dimension(1024, 60));
        pBox1.setLayout(new javax.swing.BoxLayout(pBox1, javax.swing.BoxLayout.LINE_AXIS));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel2.setText("Đã ở:");
        pBox1.add(jLabel2);
        pBox1.add(filler1);

        jPanel2.setBackground(new java.awt.Color(255, 127, 80));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setMaximumSize(new java.awt.Dimension(50, 28));
        jPanel2.setPreferredSize(new java.awt.Dimension(50, 50));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        pBox1.add(jPanel2);
        pBox1.add(filler2);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel3.setText("Trống:");
        pBox1.add(jLabel3);
        pBox1.add(filler3);

        jPanel1.setBackground(new java.awt.Color(91, 181, 111));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setMaximumSize(new java.awt.Dimension(50, 28));
        jPanel1.setPreferredSize(new java.awt.Dimension(50, 28));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        pBox1.add(jPanel1);
        pBox1.add(filler4);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel5.setText("Đã Đặt:");
        pBox1.add(jLabel5);
        pBox1.add(filler5);

        jPanel4.setBackground(new java.awt.Color(255, 165, 0));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.setMaximumSize(new java.awt.Dimension(50, 28));
        jPanel4.setPreferredSize(new java.awt.Dimension(50, 28));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        pBox1.add(jPanel4);
        pBox1.add(filler6);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel7.setText("Đang bảo trì:");
        pBox1.add(jLabel7);
        pBox1.add(filler7);

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/png/clean.png"))); // NOI18N
        jLabel6.setToolTipText("");
        jLabel6.setMaximumSize(new java.awt.Dimension(50, 28));
        jLabel6.setMinimumSize(new java.awt.Dimension(50, 28));
        jLabel6.setPreferredSize(new java.awt.Dimension(50, 28));
        pBox1.add(jLabel6);
        pBox1.add(filler9);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel8.setText("Ngày nhận:");
        pBox1.add(jLabel8);
        pBox1.add(filler8);

        jDateChooserNgayNhan.setDateFormatString("dd/MM/yyyy");
        jDateChooserNgayNhan.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jDateChooserNgayNhan.setMaxSelectableDate(new java.util.Date(253370743266000L));
        jDateChooserNgayNhan.setMaximumSize(new java.awt.Dimension(200, 35));
        jDateChooserNgayNhan.setMinimumSize(new java.awt.Dimension(200, 35));
        jDateChooserNgayNhan.setPreferredSize(new java.awt.Dimension(200, 35));
        pBox1.add(jDateChooserNgayNhan);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel4.setText("Số ngày:");
        pBox1.add(jLabel4);
        pBox1.add(filler10);

        jtxtTimKiemSoNgay.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jtxtTimKiemSoNgay.setMaximumSize(new java.awt.Dimension(60, 35));
        jtxtTimKiemSoNgay.setMinimumSize(new java.awt.Dimension(60, 35));
        jtxtTimKiemSoNgay.setPreferredSize(new java.awt.Dimension(60, 35));
        jtxtTimKiemSoNgay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtTimKiemSoNgayActionPerformed(evt);
            }
        });
        pBox1.add(jtxtTimKiemSoNgay);
        pBox1.add(filler11);

        btnTimKiemTTPhong.setBackground(new java.awt.Color(102, 204, 0));
        btnTimKiemTTPhong.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnTimKiemTTPhong.setForeground(new java.awt.Color(255, 255, 255));
        btnTimKiemTTPhong.setText("Tìm kiếm");
        pBox1.add(btnTimKiemTTPhong);

        jPanel3.add(pBox1);

        pBox2.setBackground(new java.awt.Color(227, 227, 227));
        pBox2.setMaximumSize(new java.awt.Dimension(1210, 43));
        pBox2.setPreferredSize(new java.awt.Dimension(1018, 43));
        pBox2.setLayout(new javax.swing.BoxLayout(pBox2, javax.swing.BoxLayout.X_AXIS));

        btnCreateKH.setBackground(new java.awt.Color(141, 171, 241));
        btnCreateKH.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnCreateKH.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/png/people.png"))); // NOI18N
        btnCreateKH.setText("Booking");
        btnCreateKH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateKHActionPerformed(evt);
            }
        });
        pBox2.add(btnCreateKH);
        pBox2.add(filler17);

        btnCheckIn.setBackground(new java.awt.Color(102, 153, 0));
        btnCheckIn.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnCheckIn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/png/people.png"))); // NOI18N
        btnCheckIn.setText("Checkin");
        btnCheckIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckInActionPerformed(evt);
            }
        });
        pBox2.add(btnCheckIn);
        pBox2.add(filler12);

        btnCreateHD.setBackground(new java.awt.Color(251, 120, 108));
        btnCreateHD.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnCreateHD.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/png/hoaDon.png"))); // NOI18N
        btnCreateHD.setText("Checkout");
        btnCreateHD.setActionCommand("btnCreateHD");
        btnCreateHD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateHDActionPerformed(evt);
            }
        });
        pBox2.add(btnCreateHD);
        pBox2.add(filler13);

        jcomboBoxTTP.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jcomboBoxTTP.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tình Trạng Phòng", "Đã ở", "Trống", "Đang Bảo Trì", "Đã Đặt Trước" }));
        jcomboBoxTTP.setMaximumSize(new java.awt.Dimension(205, 43));
        jcomboBoxTTP.setMinimumSize(new java.awt.Dimension(205, 43));
        jcomboBoxTTP.setPreferredSize(new java.awt.Dimension(205, 43));
        jcomboBoxTTP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcomboBoxTTPActionPerformed(evt);
            }
        });
        pBox2.add(jcomboBoxTTP);
        pBox2.add(filler14);

        jComboBoxLoaiPhong.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jComboBoxLoaiPhong.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Loại phòng", "1 giường đơn", "2 giường đơn", "1 đôi 1 đơn", "1 giường đôi" }));
        jComboBoxLoaiPhong.setMaximumSize(new java.awt.Dimension(157, 43));
        jComboBoxLoaiPhong.setMinimumSize(new java.awt.Dimension(157, 43));
        jComboBoxLoaiPhong.setPreferredSize(new java.awt.Dimension(157, 43));
        pBox2.add(jComboBoxLoaiPhong);
        pBox2.add(filler15);

        jtxtTimKiemMaPhong.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jtxtTimKiemMaPhong.setMaximumSize(new java.awt.Dimension(140, 43));
        jtxtTimKiemMaPhong.setMinimumSize(new java.awt.Dimension(140, 43));
        jtxtTimKiemMaPhong.setPreferredSize(new java.awt.Dimension(140, 43));
        jtxtTimKiemMaPhong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtTimKiemMaPhongActionPerformed(evt);
            }
        });
        pBox2.add(jtxtTimKiemMaPhong);

        btnTimKiem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/png/search.png"))); // NOI18N
        btnTimKiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTimKiemActionPerformed(evt);
            }
        });
        pBox2.add(btnTimKiem);
        pBox2.add(filler16);

        btnLamMoi.setBackground(new java.awt.Color(255, 51, 51));
        btnLamMoi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnLamMoi.setForeground(new java.awt.Color(255, 255, 255));
        btnLamMoi.setText("Làm mới");
        pBox2.add(btnLamMoi);

        jPanel3.add(pBox2);

        pC.add(jPanel3, java.awt.BorderLayout.CENTER);

        Pheader.add(pC, java.awt.BorderLayout.CENTER);

        pGUI.add(Pheader, java.awt.BorderLayout.NORTH);

        pS.setPreferredSize(new java.awt.Dimension(700, 480));
        pS.setLayout(new java.awt.BorderLayout());

        lblDsCacPhong.setFont(new java.awt.Font("Times New Roman", 1, 15)); // NOI18N
        lblDsCacPhong.setText("Danh Sách Các Phòng");
        pS.add(lblDsCacPhong, java.awt.BorderLayout.NORTH);

        pDanhSachPhong.setLayout(new javax.swing.BoxLayout(pDanhSachPhong, javax.swing.BoxLayout.Y_AXIS));

        lblTang1.setPreferredSize(new java.awt.Dimension(1068, 80));
        lblTang1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        pT1.setBackground(new java.awt.Color(255, 255, 255));
        pT1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        pT1.setPreferredSize(new java.awt.Dimension(150, 128));

        jtxtTang1.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jtxtTang1.setText("Tầng 1");

        javax.swing.GroupLayout pT1Layout = new javax.swing.GroupLayout(pT1);
        pT1.setLayout(pT1Layout);
        pT1Layout.setHorizontalGroup(
            pT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pT1Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jtxtTang1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
        );
        pT1Layout.setVerticalGroup(
            pT1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pT1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jtxtTang1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblTang1.add(pT1);

        pDanhSachPhong.add(lblTang1);

        lblTang2.setPreferredSize(new java.awt.Dimension(1274, 80));
        lblTang2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        pT2.setBackground(new java.awt.Color(255, 255, 255));
        pT2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        pT2.setPreferredSize(new java.awt.Dimension(150, 128));

        jtxtTang2.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jtxtTang2.setText("Tầng 2");

        javax.swing.GroupLayout pT2Layout = new javax.swing.GroupLayout(pT2);
        pT2.setLayout(pT2Layout);
        pT2Layout.setHorizontalGroup(
            pT2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pT2Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jtxtTang2, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(51, Short.MAX_VALUE))
        );
        pT2Layout.setVerticalGroup(
            pT2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pT2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jtxtTang2, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblTang2.add(pT2);

        pDanhSachPhong.add(lblTang2);

        lblTang3.setPreferredSize(new java.awt.Dimension(1274, 80));
        lblTang3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        pT3.setBackground(new java.awt.Color(255, 255, 255));
        pT3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        pT3.setPreferredSize(new java.awt.Dimension(150, 128));

        jtxtTang3.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jtxtTang3.setText("Tầng 3");

        javax.swing.GroupLayout pT3Layout = new javax.swing.GroupLayout(pT3);
        pT3.setLayout(pT3Layout);
        pT3Layout.setHorizontalGroup(
            pT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pT3Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jtxtTang3, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(51, Short.MAX_VALUE))
        );
        pT3Layout.setVerticalGroup(
            pT3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pT3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jtxtTang3, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblTang3.add(pT3);

        pDanhSachPhong.add(lblTang3);

        lblTang4.setPreferredSize(new java.awt.Dimension(1274, 80));
        lblTang4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        pT4.setBackground(new java.awt.Color(255, 255, 255));
        pT4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        pT4.setPreferredSize(new java.awt.Dimension(150, 128));

        jtxtTang4.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jtxtTang4.setText("Tầng 4");

        javax.swing.GroupLayout pT4Layout = new javax.swing.GroupLayout(pT4);
        pT4.setLayout(pT4Layout);
        pT4Layout.setHorizontalGroup(
            pT4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pT4Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jtxtTang4, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(51, Short.MAX_VALUE))
        );
        pT4Layout.setVerticalGroup(
            pT4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pT4Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jtxtTang4, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(65, Short.MAX_VALUE))
        );

        lblTang4.add(pT4);

        pDanhSachPhong.add(lblTang4);

        lblTang5.setPreferredSize(new java.awt.Dimension(1274, 80));
        lblTang5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        pT5.setBackground(new java.awt.Color(255, 255, 255));
        pT5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        pT5.setPreferredSize(new java.awt.Dimension(150, 128));

        jtxtTang5.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jtxtTang5.setText("Tầng 5");

        javax.swing.GroupLayout pT5Layout = new javax.swing.GroupLayout(pT5);
        pT5.setLayout(pT5Layout);
        pT5Layout.setHorizontalGroup(
            pT5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pT5Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jtxtTang5, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(50, Short.MAX_VALUE))
        );
        pT5Layout.setVerticalGroup(
            pT5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pT5Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jtxtTang5, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblTang5.add(pT5);

        pDanhSachPhong.add(lblTang5);

        pS.add(pDanhSachPhong, java.awt.BorderLayout.CENTER);

        pGUI.add(pS, java.awt.BorderLayout.CENTER);

        add(pGUI);
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtTimKiemSoNgayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtTimKiemSoNgayActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtTimKiemSoNgayActionPerformed

    private void btnTimKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimKiemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTimKiemActionPerformed

    private void jcomboBoxTTPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcomboBoxTTPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jcomboBoxTTPActionPerformed

    private void btnCreateHDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateHDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCreateHDActionPerformed

    private void btnCreateKHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateKHActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCreateKHActionPerformed

    private void jtxtTimKiemMaPhongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtTimKiemMaPhongActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtTimKiemMaPhongActionPerformed

    private void btnCheckInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckInActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCheckInActionPerformed

   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Pheader;
    private javax.swing.JButton btnCheckIn;
    private javax.swing.JButton btnCreateHD;
    private javax.swing.JButton btnCreateKH;
    private javax.swing.JButton btnLamMoi;
    private javax.swing.JButton btnTimKiem;
    private javax.swing.JButton btnTimKiemTTPhong;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler12;
    private javax.swing.Box.Filler filler13;
    private javax.swing.Box.Filler filler14;
    private javax.swing.Box.Filler filler15;
    private javax.swing.Box.Filler filler16;
    private javax.swing.Box.Filler filler17;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JComboBox<String> jComboBoxLoaiPhong;
    private com.toedter.calendar.JDateChooser jDateChooserNgayNhan;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JComboBox<String> jcomboBoxTTP;
    private javax.swing.JLabel jtxtTang1;
    private javax.swing.JLabel jtxtTang2;
    private javax.swing.JLabel jtxtTang3;
    private javax.swing.JLabel jtxtTang4;
    private javax.swing.JLabel jtxtTang5;
    private javax.swing.JTextField jtxtTimKiemMaPhong;
    private javax.swing.JTextField jtxtTimKiemSoNgay;
    private javax.swing.JLabel lblDsCacPhong;
    private javax.swing.JPanel lblTang1;
    private javax.swing.JPanel lblTang2;
    private javax.swing.JPanel lblTang3;
    private javax.swing.JPanel lblTang4;
    private javax.swing.JPanel lblTang5;
    private javax.swing.JPanel pBox1;
    private javax.swing.JPanel pBox2;
    private javax.swing.JPanel pC;
    private javax.swing.JPanel pDanhSachPhong;
    private javax.swing.JPanel pGUI;
    private javax.swing.JPanel pN;
    private javax.swing.JPanel pS;
    private javax.swing.JPanel pT1;
    private javax.swing.JPanel pT2;
    private javax.swing.JPanel pT3;
    private javax.swing.JPanel pT4;
    private javax.swing.JPanel pT5;
    // End of variables declaration//GEN-END:variables



    private Color rgba(int i, int i0, int i1, int i2) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
	public void mes(String x) {
		JOptionPane.showMessageDialog(this,x);
	}
    private void openNewFrame(String option, String room) throws Exception {
    	
    	FormThongTinHoaDon formThongTinHoaDon = new FormThongTinHoaDon();
        JFrame frame = RoomOptionFrameFactory.createFrame(option, room, mainForm,this, formThongTinHoaDon);
        if(frame==null) {
        	return;
        }
        frame.setVisible(true); // Hiển thị frame đã được cấu hình
    }
//    public void upLoadDataJpanel(ArrayList<Phong> dsphong) throws Exception {
//
//    	ArrayList<Phong> dsPhong = dsphong;
//
//        for (Phong phong : dsPhong) {
//        	String maPhong = phong.getMaPhong();
////            kiemTraThoiGianPhongHopLe(phong);
//            int loaiPhongInt = phong.getLoaiPhong().getTenLoai();
//            String loaiPhong;
//
//            // Đặt tên loại phòng
//            if (loaiPhongInt == 1) {
//                loaiPhong = "Một Đơn";
//            } else if (loaiPhongInt == 2) {
//                loaiPhong = "Hai Đơn";
//            } else if (loaiPhongInt == 3) {
//                loaiPhong = "1 Đơn 1 Đôi";
//            } else {
//                loaiPhong = "Một Đôi";
//            }
//
//            int tang = Integer.parseInt(phong.getSoPhong().substring(0, 1));
//
//            // Tạo JPanel mới cho từng phòng
//            JPanel roomPanel = new JPanel();
//
//            roomPanel.setLayout(new java.awt.BorderLayout());
//            roomPanel.setPreferredSize(new java.awt.Dimension(160, 128));
//            roomPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
//
//            // Kiểm tra trạng thái phòng
//            int trangThaiPhongInt = phong.getTrangThaiPhong().getMaTrangThai();
//
//            // Header cho JPanel
//            JPanel pHeader = new JPanel();
////            pHeader.setBackground(new java.awt.Color(165, 218, 192));
//            pHeader.setLayout(new java.awt.GridLayout(1, 0));
//
//            JLabel lblImg = new JLabel();
//            lblImg.setFont(new java.awt.Font("Times New Roman", 1, 18));
//            lblImg.setPreferredSize(new java.awt.Dimension(60, 60)); // Kích thước cố định cho JLabel chứa ảnh
//            lblImg.setMinimumSize(new java.awt.Dimension(60, 60)); // Đảm bảo không thay đổi kích thước tối thiểu
//            lblImg.setMaximumSize(new java.awt.Dimension(60, 60)); // Đảm bảo không thay đổi kích thước tối đa
//
//            // Cập nhật icon dựa trên trạng thái
//            switch (trangThaiPhongInt) {
//                case 1:
//                	lblImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/png/nha.png"))); // Icon cho phòng đã đặt
//                    break;
//                case 2:
//                	lblImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/png/nha.png"))); // Icon cho phòng trống
//                    break;
//                case 3:
//                	lblImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/png/clean.png"))); // Icon cho phòng đang bảo trì
//                    break;
//                case 4:
//                	lblImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/png/nha.png"))); // Icon cho phòng đã đặt trước
//                    break;
//            }
//            pHeader.add(lblImg);
//
//            JLabel lblMaPhong = new JLabel();
//            lblMaPhong.setFont(new java.awt.Font("Segoe UI", 1, 18));
//            lblMaPhong.setText(maPhong);
//            pHeader.add(lblMaPhong);
//
//            roomPanel.add(pHeader, java.awt.BorderLayout.NORTH);
//
//            // Footer cho JPanel
//            JPanel pFooter = new JPanel();
////            pFooter.setBackground(new java.awt.Color(165, 218, 192));
//            pFooter.setLayout(new java.awt.FlowLayout(FlowLayout.LEFT));
//            switch (trangThaiPhongInt) {
//            case 1: // DA_DAT
//                roomPanel.setBackground(new java.awt.Color(255, 127, 80)); // Đổi màu nền sang đỏ
//                pHeader.setBackground(new java.awt.Color(255, 127, 80));
//                pFooter.setBackground(new java.awt.Color(255, 127, 80));
//                break;
//            case 2: // TRONG
//                roomPanel.setBackground(new java.awt.Color(91, 181, 111)); // Giữ nguyên màu
//                pHeader.setBackground(new java.awt.Color(91, 181, 111));
//                pFooter.setBackground(new java.awt.Color(91, 181, 111));
//                break;
//            case 3: // DANG_BAO_TRI
//                roomPanel.setBackground(new java.awt.Color(91, 181, 111)); // Đổi màu nền sang vàng
//                pHeader.setBackground(new java.awt.Color(91, 181, 111));
//                pFooter.setBackground(new java.awt.Color(91, 181, 111));
//                break;
//            case 4: // DA_DAT_TRUOC
//                roomPanel.setBackground(new java.awt.Color(255, 165, 0)); // Giữ nguyên màu
//                pHeader.setBackground(new java.awt.Color(255, 165, 0));
//                pFooter.setBackground(new java.awt.Color(255, 165, 0));
//                break;
//        }
//            JLabel lblLoaiPhong = new JLabel();
//            lblLoaiPhong.setText(loaiPhong);
//            pFooter.add(lblLoaiPhong);
//            pFooter.add(Box.createHorizontalStrut(10));
//
//             // Đảm bảo nhập khẩu này
//
//            JLabel lblGioDat = new JLabel();
//            lblGioDat.setFont(new java.awt.Font("Segoe UI", 0, 14));
//            pFooter.add(lblGioDat);
//
//            ChiTietPhieuDat chiTietPhieuDat = rmiClient.getChiTietPhieuDatDangO(maPhong);
//
//            if (chiTietPhieuDat != null && trangThaiPhongInt == 1) {
//            	 PhieuDatPhong phieuDatPhong = chiTietPhieuDat.getMaPhieuDat();
//                // Kiểm tra kiểu đặt (theo giờ hoặc theo ngày)
//                boolean kieuDat = phieuDatPhong.getKieuDat(); // Giả sử có phương thức getKieuDat() trả về kiểu đặt
//                Timer timer = new Timer(1000, new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        // Lấy ngày giờ trả phòng từ chi tiết phiếu đặt
//                        LocalDateTime checkOutDateTime = LocalDateTime.of(
//                            chiTietPhieuDat.getNgayTraPhong(),
//                            chiTietPhieuDat.getGioTraPhong()
//                        );
//
//                        // Lấy thời gian hiện tại
//                        LocalDateTime currentDateTime = LocalDateTime.now();
//
//                        // Tính thời gian còn lại
//                        long minutesRemaining = ChronoUnit.MINUTES.between(currentDateTime, checkOutDateTime);
//                        long hoursRemaining = minutesRemaining / 60;
//                        long remainingMinutes = minutesRemaining % 60;
//
//
//                        // Kiểm tra kiểu đặt
//                        SwingUtilities.invokeLater(() -> {
//                            if (minutesRemaining >= 0) {
//                                if (kieuDat == true) {
//                                    // Đặt theo giờ: Cập nhật đếm ngược giờ
//                                    lblGioDat.setText(String.format("%02d:%02d Giờ", hoursRemaining, remainingMinutes));
//                                } else if (kieuDat == false ) {
//                                	long totalDays = hoursRemaining / 24; // Mỗi 22 giờ là 1 ngày
//                                    long extraHours = hoursRemaining % 24; // Số giờ lẻ sau khi tính ngày
//
//                                    if (totalDays > 0) {
//                                        // Nếu tổng giờ vượt quá 22h => hiển thị số ngày
//                                        lblGioDat.setText(String.format("%d Ngày", totalDays + (extraHours > 0 ? 1 : 0)));
//                                    } else {
//                                        // Nếu tổng giờ dưới 22h => hiển thị giống "theo giờ"
//                                        lblGioDat.setText(String.format("%02d:%02d Giờ", hoursRemaining, remainingMinutes));
//                                    }
//                                }
//                                lblGioDat.revalidate();
//                                lblGioDat.repaint();
//                            } else {
//                                // Khi thời gian kết thúc, dừng Timer và cập nhật giao diện
//                                ((Timer) e.getSource()).stop();
//                                lblGioDat.setText("Trả phòng");
//                                lblGioDat.revalidate();
//                                lblGioDat.repaint();
//                                System.out.println("Thời gian đã hết, trả phòng!");
//                            }
//                        });
//                    }
//                });
//                timer.start();
//            } else {
////                System.out.println("Không tìm thấy chi tiết phiếu đặt hoặc trạng thái phòng không hợp lệ.");
//            }
//
//
//
//
//            // Biến lưu thời gian (giây) đã trôi qua
////            final int[] secondsElapsed = {0};
////
////            // Tạo Timer để tăng thời gian
////            Timer timer = new Timer(1000, new ActionListener() {
////                @Override
////                public void actionPerformed(ActionEvent e) {
////                    secondsElapsed[0]++; // Tăng thời gian lên 1 giây
////                    updateLabel(lblGioDat, secondsElapsed[0]); // Cập nhật label
////                }
////            });
////            roomPanel.putClientProperty("timer", timer);
////             Chỉ bắt đầu Timer khi trạng thái phòng là "Đã đặt"
////            if (trangThaiPhongInt == 1) {
////                timer.stop(); // Ban đầu, dừng Timer
////            }
//
//
//
//            roomPanel.add(pFooter, java.awt.BorderLayout.CENTER);
//
//            // Thêm phòng vào panel của tầng tương ứng
//            switch (tang) {
//                case 1:
//                    lblTang1.add(roomPanel);
//                    break;
//                case 2:
//                    lblTang2.add(roomPanel);
//                    break;
//                case 3:
//                    lblTang3.add(roomPanel);
//                    break;
//                case 4:
//                    lblTang4.add(roomPanel);
//                    break;
//                case 5:
//                    lblTang5.add(roomPanel);
//                    break;
//            }
//
//            // Tạo và thêm menu popup vào mỗi panel phòng
//            JPopupMenu menu = createRoomPopupMenu(maPhong);
//            roomPanel.addMouseListener(new MouseAdapter() {
//                @Override
//                public void mouseClicked(MouseEvent e) {
//                    if (SwingUtilities.isRightMouseButton(e)) {
//                        menu.show(roomPanel, e.getX(), e.getY());
//                    }
//                }
//            });
//        }
//
//        // Cập nhật lại các panel của từng tầng để hiển thị các thay đổi
//        lblTang1.revalidate();
//        lblTang1.repaint();
//        lblTang2.revalidate();
//        lblTang2.repaint();
//        lblTang3.revalidate();
//        lblTang3.repaint();
//        lblTang4.revalidate();
//        lblTang4.repaint();
//        lblTang5.revalidate();
//        lblTang5.repaint();
//    }
public void upLoadDataJpanel(ArrayList<Phong> dsphong) throws Exception {
    ArrayList<Phong> dsPhong = dsphong;

    // Dừng tất cả timer hiện tại
    for (Timer timer : timers.values()) {
        timer.stop();
    }
    timers.clear();

    for (Phong phong : dsPhong) {
        String maPhong = phong.getMaPhong();
        int loaiPhongInt = phong.getLoaiPhong().getTenLoai();
        String loaiPhong;

        if (loaiPhongInt == 1) {
            loaiPhong = "Một Đơn";
        } else if (loaiPhongInt == 2) {
            loaiPhong = "Hai Đơn";
        } else if (loaiPhongInt == 3) {
            loaiPhong = "1 Đơn 1 Đôi";
        } else {
            loaiPhong = "Một Đôi";
        }

        int tang = Integer.parseInt(phong.getSoPhong().substring(0, 1));

        JPanel roomPanel = new JPanel();
        roomPanel.setLayout(new java.awt.BorderLayout());
        roomPanel.setPreferredSize(new java.awt.Dimension(160, 128));
        roomPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));

        int trangThaiPhongInt = phong.getTrangThaiPhong().getMaTrangThai();

        JPanel pHeader = new JPanel();
        pHeader.setLayout(new java.awt.GridLayout(1, 0));

        JLabel lblImg = new JLabel();
        lblImg.setFont(new java.awt.Font("Times New Roman", 1, 18));
        lblImg.setPreferredSize(new java.awt.Dimension(60, 60));
        lblImg.setMinimumSize(new java.awt.Dimension(60, 60));
        lblImg.setMaximumSize(new java.awt.Dimension(60, 60));

        switch (trangThaiPhongInt) {
            case 1:
                lblImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/png/nha.png")));
                break;
            case 2:
                lblImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/png/nha.png")));
                break;
            case 3:
                lblImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/png/clean.png")));
                break;
            case 4:
                lblImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/png/nha.png")));
                break;
        }
        pHeader.add(lblImg);

        JLabel lblMaPhong = new JLabel();
        lblMaPhong.setFont(new java.awt.Font("Segoe UI", 1, 18));
        lblMaPhong.setText(maPhong);
        pHeader.add(lblMaPhong);

        roomPanel.add(pHeader, java.awt.BorderLayout.NORTH);

        JPanel pFooter = new JPanel();
        pFooter.setLayout(new java.awt.FlowLayout(FlowLayout.LEFT));
        switch (trangThaiPhongInt) {
            case 1:
                roomPanel.setBackground(new java.awt.Color(255, 127, 80));
                pHeader.setBackground(new java.awt.Color(255, 127, 80));
                pFooter.setBackground(new java.awt.Color(255, 127, 80));
                break;
            case 2:
                roomPanel.setBackground(new java.awt.Color(91, 181, 111));
                pHeader.setBackground(new java.awt.Color(91, 181, 111));
                pFooter.setBackground(new java.awt.Color(91, 181, 111));
                break;
            case 3:
                roomPanel.setBackground(new java.awt.Color(91, 181, 111));
                pHeader.setBackground(new java.awt.Color(91, 181, 111));
                pFooter.setBackground(new java.awt.Color(91, 181, 111));
                break;
            case 4:
                roomPanel.setBackground(new java.awt.Color(255, 165, 0));
                pHeader.setBackground(new java.awt.Color(255, 165, 0));
                pFooter.setBackground(new java.awt.Color(255, 165, 0));
                break;
        }

        JLabel lblLoaiPhong = new JLabel();
        lblLoaiPhong.setText(loaiPhong);
        pFooter.add(lblLoaiPhong);
        pFooter.add(Box.createHorizontalStrut(10));

        JLabel lblGioDat = new JLabel();
        lblGioDat.setFont(new java.awt.Font("Segoe UI", 0, 14));
        pFooter.add(lblGioDat);

        ChiTietPhieuDat chiTietPhieuDat = rmiClient.getChiTietPhieuDatDangO(maPhong);

        if (chiTietPhieuDat != null && trangThaiPhongInt == 1) {
            PhieuDatPhong phieuDatPhong = chiTietPhieuDat.getMaPhieuDat();
            boolean kieuDat = phieuDatPhong.getKieuDat();
            // Log để kiểm tra dữ liệu
            System.out.println(">>> [DEBUG] Phòng " + maPhong + ": ngayTraPhong = " + chiTietPhieuDat.getNgayTraPhong() + ", gioTraPhong = " + chiTietPhieuDat.getGioTraPhong());
            Timer timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        // Không lấy lại ChiTietPhieuDat mỗi giây để tránh gọi RMI quá nhiều
                        LocalDateTime checkOutDateTime = LocalDateTime.of(
                                chiTietPhieuDat.getNgayTraPhong(),
                                chiTietPhieuDat.getGioTraPhong()
                        );
                        LocalDateTime currentDateTime = LocalDateTime.now();
                        long minutesRemaining = ChronoUnit.MINUTES.between(currentDateTime, checkOutDateTime);
                        long hoursRemaining = minutesRemaining / 60;
                        long remainingMinutes = minutesRemaining % 60;

                        SwingUtilities.invokeLater(() -> {
                            if (minutesRemaining >= 0) {
                                if (kieuDat) {
                                    lblGioDat.setText(String.format("%02d:%02d Giờ", hoursRemaining, remainingMinutes));
                                } else {
                                    long totalDays = hoursRemaining / 24;
                                    long extraHours = hoursRemaining % 24;
                                    if (totalDays > 0) {
                                        lblGioDat.setText(String.format("%d Ngày", totalDays + (extraHours > 0 ? 1 : 0)));
                                    } else {
                                        lblGioDat.setText(String.format("%02d:%02d Giờ", hoursRemaining, remainingMinutes));
                                    }
                                }
                                lblGioDat.revalidate();
                                lblGioDat.repaint();
                            } else {
                                ((Timer) e.getSource()).stop();
                                lblGioDat.setText("Trả phòng");
                                lblGioDat.revalidate();
                                lblGioDat.repaint();
                                System.out.println("Thời gian đã hết, trả phòng cho phòng " + maPhong + "!");
                            }
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        ((Timer) e.getSource()).stop();
                        lblGioDat.setText("Lỗi");
                        System.out.println(">>> [ERROR] Lỗi timer phòng " + maPhong + ": " + ex.getMessage());
                    }
                }
            });
            timers.put(maPhong, timer);
            timer.start();
            System.out.println(">>> [DEBUG] Timer started for room: " + maPhong);
        } else {

            System.out.println(">>> [DEBUG] Phòng " + maPhong + ": Không có ChiTietPhieuDat hoặc không ở trạng thái DA_DAT");
        }

        roomPanel.add(pFooter, java.awt.BorderLayout.CENTER);

        switch (tang) {
            case 1:
                lblTang1.add(roomPanel);
                break;
            case 2:
                lblTang2.add(roomPanel);
                break;
            case 3:
                lblTang3.add(roomPanel);
                break;
            case 4:
                lblTang4.add(roomPanel);
                break;
            case 5:
                lblTang5.add(roomPanel);
                break;
        }

        JPopupMenu menu = createRoomPopupMenu(maPhong);
        roomPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    menu.show(roomPanel, e.getX(), e.getY());
                }
            }
        });
    }

    lblTang1.revalidate();
    lblTang1.repaint();
    lblTang2.revalidate();
    lblTang2.repaint();
    lblTang3.revalidate();
    lblTang3.repaint();
    lblTang4.revalidate();
    lblTang4.repaint();
    lblTang5.revalidate();
    lblTang5.repaint();
}
    private JPopupMenu createRoomPopupMenu(String roomCode) {
        JPopupMenu menu = new JPopupMenu();
        String[] actions = {"Đặt phòng", "Nhận phòng", "Hủy phòng", "Trả phòng", "Đổi phòng", "Gia hạn phòng", "Cập nhật trạng thái phòng"};
        for (String action : actions) {
            JMenuItem menuItem = new JMenuItem(action);
            menuItem.addActionListener(e -> {
				try {
                    System.out.println(">>> [DEBUG] Action được chọn: [" + action + "]");

                    if (action.equals("Hủy phòng")) {
	                    // Hiển thị hộp thoại xác nhận hủy phòng
	                    int confirmed = JOptionPane.showConfirmDialog(null,
	                        "Bạn có chắc chắn muốn hủy phòng " + roomCode + " không?",
	                        "Xác nhận Hủy Phòng",
	                        JOptionPane.YES_NO_OPTION);

	                    if (confirmed == JOptionPane.YES_OPTION) {
	                        // Thực hiện hủy phòng
	                        huyPhong(roomCode);
	                    }
                    } else if (action.equals("Cập nhật trạng thái phòng")) {
                        Phong phong = rmiClient.getPhongByMaPhong(roomCode);

                        if (phong == null) {
                            JOptionPane.showMessageDialog(this, "Không tìm thấy phòng để cập nhật.");
                            return;
                        }

                        if (phong.getTrangThaiPhong().getMaTrangThai() != 3) {
                            JOptionPane.showMessageDialog(this, "Chức năng chỉ áp dụng cho phòng đang bảo trì!");
                        } else {
                            // Chuyển trạng thái từ BẢO_TRÌ (3) về TRỐNG
                            boolean result = rmiClient.capNhatPhong(
                                    phong.getMaPhong(),
                                    phong.getGiaPhong(), // giữ nguyên giá hiện tại
                                    TrangThaiPhong.TRONG
                            );

                            if (result) {
                                JOptionPane.showMessageDialog(this, "Cập nhật trạng thái thành công.");
                                xoaDuLieuPhong();
                                dsPhong = (ArrayList<Phong>) rmiClient.loadTatCaPhong();
                                upLoadDataJpanel(dsPhong);
                            } else {
                                JOptionPane.showMessageDialog(this, "Cập nhật thất bại.");
                            }
                        }
	                }
                    else if (action.equals("Gia hạn phòng")) {
                        // Kiểm tra trạng thái phòng trước khi gia hạn
                        Phong phong = rmiClient.getPhongByMaPhong(roomCode);
                        System.out.println("Trạng thái phòng: " + phong.getTrangThaiPhong().getMaTrangThai());

                        if (phong == null) {
                            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin phòng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        if (phong.getTrangThaiPhong().getMaTrangThai() != 1) { // Chỉ cho phép gia hạn nếu phòng đang "Đã ở"
                            JOptionPane.showMessageDialog(this, "Chỉ có thể gia hạn cho phòng đang được sử dụng (Đã ở)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        // Mở FormGiaHanPhong
                        System.out.println(">>> Đã qua bước kiểm tra, mở form gia hạn phòng!");
                        FormGiaHanPhong formGiaHanPhong = new FormGiaHanPhong(roomCode, this);
                        JFrame frame = new JFrame("Gia hạn phòng");
                        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        frame.add(formGiaHanPhong);
                        frame.pack();
                        frame.setLocationRelativeTo(null); // Căn giữa màn hình
                        frame.setVisible(true);
                    }
                    else {
	                    openNewFrame(action, roomCode);
	                }
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            });
            menu.add(menuItem);
        }
        return menu;
    
    }
    public void updateTimerForRoom(String maPhong) throws Exception {
        // Tìm và dừng timer cũ nếu có
        Timer oldTimer = timers.get(maPhong);
        if (oldTimer != null) {
            oldTimer.stop();
            timers.remove(maPhong);
        }

        // Tìm panel phòng
        JPanel targetPanel = null;
        JPanel[] tangPanels = {lblTang1, lblTang2, lblTang3, lblTang4, lblTang5};
        for (JPanel tang : tangPanels) {
            for (Component comp : tang.getComponents()) {
                if (comp instanceof JPanel && !(comp instanceof JPanel && ((JPanel) comp).getComponentCount() == 1 && ((JPanel) comp).getComponent(0) instanceof JLabel)) {
                    JPanel roomPanel = (JPanel) comp;
                    JPanel pHeader = (JPanel) roomPanel.getComponent(0);
                    JLabel lblMaPhong = (JLabel) pHeader.getComponent(1);
                    if (lblMaPhong.getText().equals(maPhong)) {
                        targetPanel = roomPanel;
                        break;
                    }
                }
            }
            if (targetPanel != null) break;
        }

        if (targetPanel == null) {
            System.out.println(">>> [DEBUG] Không tìm thấy panel cho phòng " + maPhong);
            return;
        }

        // Lấy lblGioDat
        JPanel pFooter = (JPanel) targetPanel.getComponent(1);
        JLabel lblGioDat = (JLabel) pFooter.getComponent(2); // Giả sử lblGioDat là component thứ 3

        // Lấy ChiTietPhieuDat mới
        ChiTietPhieuDat chiTietPhieuDat = rmiClient.getChiTietPhieuDatDangO(maPhong);
        if (chiTietPhieuDat != null && phongService.findPhongByMaPhong(maPhong).getTrangThaiPhong().getMaTrangThai() == 1) {
            PhieuDatPhong phieuDatPhong = chiTietPhieuDat.getMaPhieuDat();
            boolean kieuDat = phieuDatPhong.getKieuDat();
            LocalDateTime checkOutDateTime = LocalDateTime.of(
                    chiTietPhieuDat.getNgayTraPhong(),
                    chiTietPhieuDat.getGioTraPhong()
            );
            LocalDateTime currentDateTime = LocalDateTime.now();
            System.out.println(">>> [DEBUG] Cập nhật timer phòng " + maPhong + ": ngayTraPhong = " + chiTietPhieuDat.getNgayTraPhong() + ", gioTraPhong = " + chiTietPhieuDat.getGioTraPhong() + ", checkOutDateTime = " + checkOutDateTime + ", currentDateTime = " + currentDateTime);

            // Kiểm tra thời gian hợp lệ
            if (checkOutDateTime.isBefore(currentDateTime)) {
                System.out.println(">>> [DEBUG] Thời gian trả phòng đã qua cho phòng " + maPhong);
                lblGioDat.setText("Trả phòng");
            } else {
                Timer timer = new Timer(1000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            LocalDateTime checkOutDateTime = LocalDateTime.of(
                                    chiTietPhieuDat.getNgayTraPhong(),
                                    chiTietPhieuDat.getGioTraPhong()
                            );
                            LocalDateTime currentDateTime = LocalDateTime.now();
                            long minutesRemaining = ChronoUnit.MINUTES.between(currentDateTime, checkOutDateTime);
                            long hoursRemaining = minutesRemaining / 60;
                            long remainingMinutes = minutesRemaining % 60;

                            SwingUtilities.invokeLater(() -> {
                                if (minutesRemaining >= 0) {
                                    if (kieuDat) {
                                        lblGioDat.setText(String.format("%02d:%02d Giờ", hoursRemaining, remainingMinutes));
                                    } else {
                                        long totalDays = hoursRemaining / 24;
                                        long extraHours = hoursRemaining % 24;
                                        if (totalDays > 0) {
                                            lblGioDat.setText(String.format("%d Ngày", totalDays + (extraHours > 0 ? 1 : 0)));
                                        } else {
                                            lblGioDat.setText(String.format("%02d:%02d Giờ", hoursRemaining, remainingMinutes));
                                        }
                                    }
                                    lblGioDat.revalidate();
                                    lblGioDat.repaint();
                                } else {
                                    ((Timer) e.getSource()).stop();
                                    lblGioDat.setText("Trả phòng");
                                    lblGioDat.revalidate();
                                    lblGioDat.repaint();
                                    System.out.println("Thời gian đã hết, trả phòng cho phòng " + maPhong + "!");
                                }
                            });
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            ((Timer) e.getSource()).stop();
                            lblGioDat.setText("Lỗi");
                            System.out.println(">>> [ERROR] Lỗi timer phòng " + maPhong + ": " + ex.getMessage());
                        }
                    }
                });
                timers.put(maPhong, timer);
                timer.start();
                System.out.println(">>> [DEBUG] Timer cập nhật cho phòng: " + maPhong);
            }
        } else {
            if (phongService.findPhongByMaPhong(maPhong).getTrangThaiPhong().getMaTrangThai() == 1) {
                System.out.println(">>> [ERROR] Phòng " + maPhong + " ở trạng thái DA_DAT nhưng không có ChiTietPhieuDat!");
                lblGioDat.setText("Lỗi dữ liệu");
            } else {
                lblGioDat.setText("Trống");
            }
        }
    }
//    public void xoaDuLieuPhong() {
//	     lblTang1.removeAll();
//	     lblTang2.removeAll();
//	     lblTang3.removeAll();
//	     lblTang4.removeAll();
//	     lblTang5.removeAll();
//	     lblTang1.add(pT1);
//	     lblTang2.add(pT2);
//	     lblTang3.add(pT3);
//	     lblTang4.add(pT4);
//	     lblTang5.add(pT5);
//    }
public void xoaDuLieuPhong() {
    // Dừng tất cả timer trước khi xóa panel
    for (Timer timer : timers.values()) {
        timer.stop();
    }
    timers.clear();

    lblTang1.removeAll();
    lblTang2.removeAll();
    lblTang3.removeAll();
    lblTang4.removeAll();
    lblTang5.removeAll();
    lblTang1.add(pT1);
    lblTang2.add(pT2);
    lblTang3.add(pT3);
    lblTang4.add(pT4);
    lblTang5.add(pT5);
}

//    public void huyPhong(String room)  {
//    	Phong phong = rmiClient.getPhongByMaPhong(room);
//    	int dem=0;
//    	if(phong.getTrangThaiPhong().getMaTrangThai()==4) {
//    		ArrayList<ChiTietPhieuDat> dsCTPD = chiTietPhieuDatPhong_Dao.timPhongTheoTrangThaiDaDat();
//    		for (ChiTietPhieuDat chiTietPhieuDat : dsCTPD) {
//				if(phong.getMaPhong().equalsIgnoreCase(chiTietPhieuDat.getPhong().getMaPhong())) {
//	        		SharedData.deletePhieuDatInfo(chiTietPhieuDat.getPhieuDatPhong().getMaPhieuDat());
//	        		phieuDatPhong_Dao.deletePhieuDatPhong(chiTietPhieuDat.getPhieuDatPhong().getMaPhieuDat());
//	        		JOptionPane.showMessageDialog(this,"Hủy phòng thành công");
//	        		phong.setTrangThaiPhong(TrangThaiPhong.TRONG);
//	        		phong_dao.updatePhong(phong);
//	        		dem++;
//	        		xoaDuLieuPhong();
//	        		dsPhong = (ArrayList<Phong>) phong_dao.loadDuLieuTuSQL();
//	        		upLoadDataJpanel(dsPhong);
//	        		return;
//				}
//
//			}
//    		if(dem==0) {
//
//        		JOptionPane.showMessageDialog(this,"Không tìm thấy Phiếu Đặt phòng do hết thời gian quy định");
//        		phong.setTrangThaiPhong(TrangThaiPhong.TRONG);
//        		phong_dao.updatePhong(phong);
//        		ArrayList<Phong> dsPhong = (ArrayList<Phong>) phong_dao.loadDuLieuTuSQL();
//        		xoaDuLieuPhong();
//        		upLoadDataJpanel(dsPhong);
//			}
//    	}else
//    		JOptionPane.showMessageDialog(this,"Phòng phải đang trạng thái Đã đặt trước");
//
//
//
//
//    }
public void huyPhong(String room) throws Exception {
    // Khởi tạo RMIClient

    Phong phong = phongService.findPhongByMaPhong(room);
    if (phong == null) {
        JOptionPane.showMessageDialog(null, "Phòng không tồn tại hoặc dịch vụ RMI chưa được khởi tạo");
        return;
    }

    int dem = 0;
    if (phong.getTrangThaiPhong().getMaTrangThai() == 4) { // DA_DAT_TRUOC
        List<ChiTietPhieuDat> dsCTPD = chiTietPhieuDatService.findPhongTheoTrangThaiDaDat();
        if (dsCTPD == null) {
            dsCTPD = new ArrayList<>();
        }
        for (ChiTietPhieuDat chiTietPhieuDat : dsCTPD) {
            if (phong.getMaPhong().equalsIgnoreCase(chiTietPhieuDat.getPhong().getMaPhong())) {
                // Xóa phiếu đặt
                boolean deleted = true;//phieuDatService.deletePhieuDatPhongByMa(chiTietPhieuDat.getMaPhieuDat().getMaPhieuDat());
                if (deleted) {
                    JOptionPane.showMessageDialog(null, "Hủy phòng thành công");
                    phong.setTrangThaiPhong(TrangThaiPhong.TRONG);
                    rmiClient.capNhatPhong(phong.getMaPhong(), phong.getGiaPhong(), TrangThaiPhong.TRONG);
                    dem++;
                    xoaDuLieuPhong();
                    dsPhong = (ArrayList<Phong>) rmiClient.loadTatCaPhong();
                    upLoadDataJpanel(dsPhong);
                    return;
                } else {
                    JOptionPane.showMessageDialog(null, "Lỗi khi xóa phiếu đặt");
                    return;
                }
            }
        }
        if (dem == 0) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy Phiếu Đặt phòng do hết thời gian quy định");
            phong.setTrangThaiPhong(TrangThaiPhong.TRONG);
            rmiClient.capNhatPhong(phong.getMaPhong(), phong.getGiaPhong(), TrangThaiPhong.TRONG);
            xoaDuLieuPhong();
            dsPhong = (ArrayList<Phong>) rmiClient.loadTatCaPhong();
            upLoadDataJpanel(dsPhong);
        }
    } else {
        JOptionPane.showMessageDialog(null, "Phòng phải đang trạng thái Đã đặt trước");
    }
}

//    public void kiemTraThoiGianPhongHopLe(Phong phong) throws RemoteException {
//        if (phong == null) {
//            return;
//        }
//
//
//        // Kiểm tra phòng hôm nay đã bắt đầu đặt phòng trước
//        List<ChiTietPhieuDat> dsCTPD = rmiClient.getChiTietPhieuDatService().findPhongTrongThoiGianDat();
//        if (dsCTPD == null) {
//            dsCTPD = new ArrayList<>();
//        }
//        for (ChiTietPhieuDat chiTietPhieuDat : dsCTPD) {
//            if (phong.getMaPhong().equalsIgnoreCase(chiTietPhieuDat.getPhong().getMaPhong())) {
//                phong.setTrangThaiPhong(TrangThaiPhong.DA_DAT_TRUOC);
//                rmiClient.capNhatPhong(phong.getMaPhong(), phong.getGiaPhong(), TrangThaiPhong.DA_DAT_TRUOC);
//                System.out.println("Tìm phòng đặt trước và cập nhật thành công");
//            }
//        }
//
//        // Kiểm tra phòng đã quá quy định đặt phòng trước
//        List<ChiTietPhieuDat> dsCTPD2 = rmiClient.getChiTietPhieuDatService().findPhongTheoTrangThaiDaDat();
//        if (dsCTPD2 == null) {
//            dsCTPD2 = new ArrayList<>();
//        }
//        for (ChiTietPhieuDat chiTietPhieuDat : dsCTPD2) {
//            if (phong.getMaPhong().equalsIgnoreCase(chiTietPhieuDat.getPhong().getMaPhong())) {
//                phong.setTrangThaiPhong(TrangThaiPhong.TRONG);
//                rmiClient.capNhatPhong(phong.getMaPhong(), phong.getGiaPhong(), TrangThaiPhong.TRONG);
//                rmiClient.deletePhieuDatPhongByMa(chiTietPhieuDat.getMaPhieuDat().getMaPhieuDat());
//                System.out.println("Thành công, cập nhật lại phòng và phiếu đặt");
//            }
//        }
//    }

    	
    	
    }
//    public void startRoomTimer(String roomCode) {
//        // Duyệt qua tất cả các roomPanel
//        for (Component comp : lblTang1.getComponents()) {
//            if (comp instanceof JPanel) {
//                JPanel roomPanel = (JPanel) comp;
//
//                // Kiểm tra và tìm pHeader (phần chứa JLabel mã phòng)
//                Component headerComp = roomPanel.getComponent(0); // Lấy component đầu tiên (pHeader)
//                if (headerComp instanceof JPanel) {
//                    JPanel pHeader = (JPanel) headerComp;
//
//                    // Tìm JLabel chứa mã phòng trong pHeader
//                    Component labelComp = pHeader.getComponent(1); // Lấy component thứ hai (JLabel mã phòng)
//                    if (labelComp instanceof JLabel) {
//                        JLabel lblMaPhong = (JLabel) labelComp;
//
//                        // So sánh mã phòng
//                        if (lblMaPhong.getText().equals(roomCode)) {
//                            Timer timer = (Timer) roomPanel.getClientProperty("timer"); // Lấy Timer gán vào JPanel
//                            if (timer != null) {
//                                timer.start(); // Khởi động Timer
//                                System.out.println("Timer started for room: " + roomCode);
//                            } else {
//                                System.out.println("Timer not found for room: " + roomCode);
//                            }
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private void updateLabel(JLabel label, int secondsElapsed) {
//        int hours = secondsElapsed / 3600;
//        int minutes = (secondsElapsed % 3600) / 60;
//        int seconds = secondsElapsed % 60;
//        label.setText(String.format("%02d:%02d:%02d Giờ", hours, minutes, seconds));
//        System.out.println("Updated time: " + label.getText()); // Thêm debug để kiểm tra thời gian
//    }



    

