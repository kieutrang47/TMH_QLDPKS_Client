package gui;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import client.RMIClient;

import entity.*;

import gui.SharedData.PhieuDatInfo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import rmi.*;

import util.JpaUtil;
import util.TaiKhoanDangNhap;


public class FormTraPhong extends javax.swing.JPanel {

    /**
     * Creates new form FormDatPhong
     */

    private RMIClient rmiClient;
    private PhieuDatService phieuDatService;
    private ChiTietPhieuDatService chiTietPhieuDatService;
    private KhachHangService khachHangService;
    private PhongService phongService;
    private HoaDonService hoaDonService;
    private DoAnThucUongService doAnThucUongService;
    private ChiTietHoaDonService chiTietHoaDonService;

//    private PhieuDatPhong_DAO phieuDatPhong_DAO = new PhieuDatPhong_DAO();
//    private ChiTietPhieuDat_DAO ctPhieuDatPhong_DAO = new ChiTietPhieuDat_DAO();
//    private KhachHang_DAO khachHang_DAO = new KhachHang_DAO();
//    private Phong_DAO phong_DAO = new Phong_DAO();
//    private HoaDon_DAO hoaDon_Dao = new HoaDon_DAO();
//    private DoAnThucUong_DAO doAnThucUong_Dao = new DoAnThucUong_DAO();
//    private ChiTietHoaDon_DAO chiTietHoaDon_DAO = new ChiTietHoaDon_DAO();

    private String tenPhong;
    private ChiTietPhieuDat chiTietPhieuDat;
    private PhieuDatPhong phieuDatPhong;
    private ArrayList<KhachHang> dsKH;
    private KhachHang khachHang;
    private String maKhachHang;
    private Phong phong;
    private GUI_QuanLiDatPhong guiQuanLiDatPhong;
    private double totalAmount = 0;

    private static final DecimalFormat df = new DecimalFormat("#,###");


    private HoaDon hoaDon;
    private ChiTietHoaDon chiTietHoaDon;
    private NhanVien nhanVien;
    private ArrayList<Phong> dsPhong;
    private String luuTTTien;
    private String luuTien;
    private BigDecimal giaTienPhong;
    private ArrayList<DoAnThucUong> dsDoAnThucUong;
    private List<Map<String, Object>> danhSachDichVu;
    private FormThongTinHoaDon formTTHoaDon;
    private static String filepath = "src/file/hoadon.pdf";
    private BigDecimal traTruoc;
    public FormTraPhong(String room, GUI_QuanLiDatPhong quiQuanLiDatPhong, FormThongTinHoaDon formTTHoaDon) throws SQLException {

        initComponents();
        // Khởi tạo RMIClient và các service
        try {
            rmiClient = new RMIClient();
            phieuDatService = rmiClient.getPhieuDatService();
            chiTietPhieuDatService = rmiClient.getChiTietPhieuDatService();
            khachHangService = rmiClient.getKhachHangService();
            phongService = rmiClient.getPhongService();
            hoaDonService = rmiClient.getHoaDonService();
            doAnThucUongService = rmiClient.getDoAnThucUongService();
            chiTietHoaDonService = rmiClient.getChiTietHoaDonService();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối RMI: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        this.guiQuanLiDatPhong = guiQuanLiDatPhong;
        tenPhong = room;
        this.guiQuanLiDatPhong = quiQuanLiDatPhong;
        this.formTTHoaDon = formTTHoaDon;
        xoaTrang();
        upLoadDuLieuTTPD();

        setComboBoxData(jComboBoxSP);
//        addFormatListener(jtxtGiaTien);
//        addFormatListener(jtxtTraTruoc);
//        addFormatListener(jtxtGiamTru);
        pCenter.setBorder(new EmptyBorder(10, 20, 10, 20)); // Thụt trên/dưới/trái/phải

        btnHuyBo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                SwingUtilities.getWindowAncestor(FormTraPhong.this).dispose();

            }
        });
        // chỉnh tốc độ cuốn thanh jscroll
//        jScrollPane1.getVerticalScrollBar().setUnitIncrement(48);  // Tăng tốc độ cuộn theo chiều dọc
        // sự kiện cho dịch vụ
        btnThemSanPham.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                // Lấy sản phẩm và số lượng đã chọn
                String selectedProduct = (String) jComboBoxSP.getSelectedItem();
                String quantityStr = (String) jComboBoxSLSP.getSelectedItem();

                // Kiểm tra nếu số lượng không phải là "Tùy chọn"
                if (!quantityStr.equals("Tùy chọn")) {
                    int quantity = Integer.parseInt(quantityStr);

                    // Trích xuất tên sản phẩm và giá tiền từ chuỗi
                    String[] productParts = selectedProduct.split("\\(");
                    String productName = productParts[0].trim();
                    String priceStr = productParts[1].replace(")", "").replace(",", "").replace("đ", "").trim();
                    double price = Double.parseDouble(priceStr);

                    // Tính tổng tiền của sản phẩm vừa thêm
                    double total = price * quantity;
                    totalAmount += total; // Cộng vào tổng tiền

                    // Thêm dòng mới vào bảng
                    DefaultTableModel model = (DefaultTableModel) jtableSP.getModel();
                    model.addRow(new Object[]{productName, quantity, total});
                }
            }
        });
        btnXoaSanPham.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = jtableSP.getSelectedRow();
                if (selectedRow != -1) {
                    DefaultTableModel model = (DefaultTableModel) jtableSP.getModel();

                    // Lấy giá trị tiền của sản phẩm trong dòng đã chọn
                    double productTotal = (double) model.getValueAt(selectedRow, 2);

                    // Trừ giá trị khỏi tổng tiền
                    totalAmount -= productTotal;

                    // Xóa dòng khỏi bảng
                    model.removeRow(selectedRow);
                }
            }
        });
        btnTraPhong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EntityManager em = null;
                EntityTransaction tr = null;
                try {
                    // Kiểm tra trạng thái phòng (1 = Đang ở)
                    Phong phong = phongService.findPhongByMaPhong(tenPhong);
                    if (phong == null || phong.getTrangThaiPhong().getMaTrangThai() != 1) {
                        mes("Phòng phải ở trạng thái đã nhận để trả!");
                        return;
                    }

                    // Tạo cờ kiểm tra
                    boolean kiemTra = false;

                    // Kiểm tra nhân viên
                    NhanVien nv = TaiKhoanDangNhap.getTaiKhoan().getMaNhanVien();
                    if (nv == null || nv.getMaNhanVien() == null) {
                        mes("Nhân viên không hợp lệ!");
                        return;
                    }

                    // Kiểm tra khách hàng
                    if (khachHang == null || khachHang.getMaKhachHang() == null) {
                        mes("Khách hàng không hợp lệ!");
                        return;
                    }

                    // Kiểm tra phiếu đặt phòng
                    if (phieuDatPhong == null || phieuDatPhong.getMaPhieuDat() == null) {
                        mes("Phiếu đặt phòng không hợp lệ!");
                        return;
                    }

                    // Kiểm tra xem các thực thể có tồn tại trong cơ sở dữ liệu không
                    em = JpaUtil.getEntityManager();
                    try {
                        // Kiểm tra NhanVien
                        NhanVien existingNv = em.find(NhanVien.class, nv.getMaNhanVien());
                        if (existingNv == null) {
                            mes("Nhân viên không tồn tại trong cơ sở dữ liệu: " + nv.getMaNhanVien());
                            return;
                        }
                        System.out.println("Nhân viên tồn tại: maNhanVien=" + nv.getMaNhanVien());

                        // Kiểm tra KhachHang
                        KhachHang existingKh = em.find(KhachHang.class, khachHang.getMaKhachHang());
                        if (existingKh == null) {
                            mes("Khách hàng không tồn tại trong cơ sở dữ liệu: " + khachHang.getMaKhachHang());
                            return;
                        }
                        System.out.println("Khách hàng tồn tại: maKhachHang=" + khachHang.getMaKhachHang());

                        // Kiểm tra PhieuDatPhong
                        PhieuDatPhong existingPdp = em.find(PhieuDatPhong.class, phieuDatPhong.getMaPhieuDat());
                        if (existingPdp == null) {
                            mes("Phiếu đặt phòng không tồn tại trong cơ sở dữ liệu: " + phieuDatPhong.getMaPhieuDat());
                            return;
                        }
                        // Kiểm tra KhachHang trong PhieuDatPhong
                        if (existingPdp.getMaKhachHang() != null) {
                            KhachHang pdpKh = em.find(KhachHang.class, existingPdp.getMaKhachHang().getMaKhachHang());
                            if (pdpKh == null) {
                                mes("Khách hàng trong Phiếu đặt phòng không tồn tại: " + existingPdp.getMaKhachHang().getMaKhachHang());
                                return;
                            }
                            System.out.println("Khách hàng trong Phiếu đặt phòng tồn tại: " + pdpKh.getMaKhachHang());
                        }
                        // Kiểm tra NhanVien trong PhieuDatPhong
                        if (existingPdp.getMaNhanVien() != null) {
                            NhanVien pdpNv = em.find(NhanVien.class, existingPdp.getMaNhanVien().getMaNhanVien());
                            if (pdpNv == null) {
                                mes("Nhân viên trong Phiếu đặt phòng không tồn tại: " + existingPdp.getMaNhanVien().getMaNhanVien());
                                return;
                            }
                            System.out.println("Nhân viên trong Phiếu đặt phòng tồn tại: " + pdpNv.getMaNhanVien());
                        }
                        // Kiểm tra ChiTietPhieuDat và Phong
                        if (existingPdp.getChiTietPhieuDats() != null && !existingPdp.getChiTietPhieuDats().isEmpty()) {
                            for (ChiTietPhieuDat ctpd : existingPdp.getChiTietPhieuDats()) {
                                if (ctpd.getPhong() != null) {
                                    Phong pdpPhong = em.find(Phong.class, ctpd.getPhong().getMaPhong());
                                    if (pdpPhong == null) {
                                        mes("Phòng trong ChiTietPhieuDat không tồn tại: " + ctpd.getPhong().getMaPhong());
                                        return;
                                    }
                                    // Kiểm tra loaiPhong và trangThaiPhong trong Phong
                                    try {
                                        LoaiPhong loaiPhong = pdpPhong.getLoaiPhong();
                                        if (loaiPhong == null) {
                                            mes("Loại phòng của Phòng không hợp lệ (null): " + pdpPhong.getMaPhong());
                                            return;
                                        }
                                        System.out.println("LoaiPhong in Phong: " + loaiPhong);
                                    } catch (Exception ex) {
                                        mes("Lỗi khi ánh xạ LoaiPhong cho Phòng: " + pdpPhong.getMaPhong() + ", lỗi: " + ex.getMessage());
                                        return;
                                    }
                                    try {
                                        TrangThaiPhong trangThaiPhong = pdpPhong.getTrangThaiPhong();
                                        if (trangThaiPhong == null) {
                                            mes("Trạng thái phòng của Phòng không hợp lệ (null): " + pdpPhong.getMaPhong());
                                            return;
                                        }
                                        System.out.println("TrangThaiPhong in Phong: " + trangThaiPhong);
                                    } catch (Exception ex) {
                                        mes("Lỗi khi ánh xạ TrangThaiPhong cho Phòng: " + pdpPhong.getMaPhong() + ", lỗi: " + ex.getMessage());
                                        return;
                                    }
                                    System.out.println("Phòng trong ChiTietPhieuDat tồn tại: " + pdpPhong.getMaPhong());
                                } else {
                                    mes("Phòng trong ChiTietPhieuDat không được để trống!");
                                    return;
                                }
                            }
                        } else {
                            mes("Phiếu đặt phòng không có chi tiết phiếu đặt!");
                            return;
                        }
                        System.out.println("Phiếu đặt phòng tồn tại: maPhieuDat=" + phieuDatPhong.getMaPhieuDat());
                    } finally {
                        if (em != null && em.isOpen()) {
                            em.close();
                        }
                    }

                    // Tạo hóa đơn
                    hoaDon = new HoaDon();
                    try {
                        hoaDon.setNgayLapHoaDon(LocalDate.parse(jtxtNgayLapHoaDon.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    } catch (DateTimeParseException ex) {
                        mes("Ngày lập hóa đơn không hợp lệ: " + jtxtNgayLapHoaDon.getText());
                        return;
                    }
                    hoaDon.setThue(0.08);
                    hoaDon.setMaNhanVien(nv);
                    hoaDon.setMaKhachHang(khachHang);
                    hoaDon.setMaPhieuDat(phieuDatPhong);

                    // Lưu hóa đơn vào cơ sở dữ liệu bằng HoaDon_DAO.themHoaDon

                    try {
                        hoaDon = rmiClient.themHoaDon(hoaDon);
                        if (hoaDon == null || hoaDon.getMaHoaDon() == null) {
                            mes("Không thể tạo hóa đơn! Vui lòng kiểm tra dữ liệu đầu vào và cơ sở dữ liệu.");
                            return;
                        }
                        System.out.println("Hóa đơn được tạo: maHoaDon=" + hoaDon.getMaHoaDon());
                    } catch (Exception ex) {
                        mes("Lỗi khi tạo hóa đơn: " + ex.getMessage());
                        return;
                    } finally {
                        if (em != null && em.isOpen()) {
                            em.close();
                        }
                    }

                    // Xử lý ghi chú và giá tiền
                    String ghiChu = jTextAreaGhiChu.getText().isEmpty() ? "Không có" : jTextAreaGhiChu.getText();
                    if (jtxtGiaTien.getText().isEmpty()) {
                        jtxtGiaTien.setText("0");
                    }

                    // Tải danh sách đồ ăn thức uống
                    dsDoAnThucUong = (ArrayList<DoAnThucUong>) doAnThucUongService.getAllDoAnThucUong();
                    if (dsDoAnThucUong == null || dsDoAnThucUong.isEmpty()) {
                        mes("Không tải được danh sách đồ ăn thức uống!");
                        return;
                    }

                    // Tìm một đồ ăn mặc định nếu không có dịch vụ
                    DoAnThucUong doAnTam = dsDoAnThucUong.stream()
                            .filter(sp -> sp.getTenSanPham().equalsIgnoreCase("Không có"))
                            .findFirst()
                            .orElse(dsDoAnThucUong.get(0));

                    // Kiểm tra chiTietPhieuDat trước khi tạo ChiTietHoaDon
                    if (chiTietPhieuDat == null) {
                        mes("Chi tiết phiếu đặt không hợp lệ!");
                        return;
                    }
                    Phong chiTietPhong = chiTietPhieuDat.getPhong();
                    if (chiTietPhong == null) {
                        mes("Phòng trong chi tiết phiếu đặt không hợp lệ!");
                        return;
                    }

                    // Tạo chi tiết hóa đơn
                    if (jtableSP.getRowCount() == 0) {
                        chiTietHoaDon = new ChiTietHoaDon();
                        chiTietHoaDon.setMaPhong(chiTietPhong);
                        chiTietHoaDon.setMaHoaDon(hoaDon);
                        chiTietHoaDon.setMaDoAnThucUong(doAnTam);
                        chiTietHoaDon.setSoLuong(1); // Đặt soLuong = 1 thay vì 0 để hợp lệ
                        try {
                            chiTietHoaDon.setSoGio(Integer.parseInt(jtxtSoGio.getText()));
                        } catch (NumberFormatException ex) {
                            mes("Số giờ không hợp lệ: " + jtxtSoGio.getText());
                            return;
                        }
                        chiTietHoaDon.setSoPhongDat(1);
                        try {
                            chiTietHoaDon.setPhuThu(Double.parseDouble(jtxtGiaTien.getText()));
                        } catch (NumberFormatException ex) {
                            mes("Phụ thu không hợp lệ: " + jtxtGiaTien.getText());
                            return;
                        }
                        chiTietHoaDon.setGhiChu(ghiChu);
                        kiemTra = chiTietHoaDonService.createChiTietHoaDon(chiTietHoaDon);
                    } else {
                        for (int i = 0; i < jtableSP.getRowCount(); i++) {
                            String tenSanPham = jtableSP.getValueAt(i, 0).toString();
                            tenSanPham = tenSanPham.split(" \\(")[0];
                            for (DoAnThucUong doAnThucUong : dsDoAnThucUong) {
                                if (tenSanPham.equalsIgnoreCase(doAnThucUong.getTenSanPham())) {
                                    chiTietHoaDon = new ChiTietHoaDon();
                                    chiTietHoaDon.setMaPhong(chiTietPhong);
                                    chiTietHoaDon.setMaHoaDon(hoaDon);
                                    chiTietHoaDon.setMaDoAnThucUong(doAnThucUong);
                                    try {
                                        chiTietHoaDon.setSoLuong(Integer.parseInt(jtableSP.getValueAt(i, 1).toString()));
                                    } catch (NumberFormatException ex) {
                                        mes("Số lượng không hợp lệ tại dòng " + (i + 1));
                                        return;
                                    }
                                    try {
                                        chiTietHoaDon.setSoGio(Integer.parseInt(jtxtSoGio.getText()));
                                    } catch (NumberFormatException ex) {
                                        mes("Số giờ không hợp lệ: " + jtxtSoGio.getText());
                                        return;
                                    }
                                    chiTietHoaDon.setSoPhongDat(1);
                                    try {
                                        chiTietHoaDon.setPhuThu(Double.parseDouble(jtxtGiaTien.getText()));
                                    } catch (NumberFormatException ex) {
                                        mes("Phụ thu không hợp lệ: " + jtxtGiaTien.getText());
                                        return;
                                    }
                                    chiTietHoaDon.setGhiChu(ghiChu);
                                    boolean created = chiTietHoaDonService.createChiTietHoaDon(chiTietHoaDon);
                                    kiemTra = kiemTra && created;
                                }
                            }
                        }
                    }

                    // Kiểm tra kết quả tạo chi tiết hóa đơn
                    if (!kiemTra) {
                        mes("Trả phòng không thành công do lỗi tạo chi tiết hóa đơn!");
                        return;
                    }

                    // Cập nhật trạng thái phòng thành ĐANG_BẢO_TRÌ
                    boolean phongUpdated = phongService.updatePhong(tenPhong, null, TrangThaiPhong.DANG_BAO_TRI);
                    if (!phongUpdated) {
                        mes("Trả phòng thành công!");

                        return;
                    }

                    // Load lại danh sách phòng và cập nhật giao diện
                    dsPhong = (ArrayList<Phong>) phongService.loadAllPhong();
                    if (dsPhong == null) {
                        mes("Không tải được danh sách phòng!");
                        return;
                    }
                    guiQuanLiDatPhong.xoaDuLieuPhong();
                    guiQuanLiDatPhong.upLoadDataJpanel(dsPhong);

                    mes("Trả phòng thành công!");

                    // Đóng JFrame
                    SwingUtilities.getWindowAncestor(FormTraPhong.this).dispose();

                    // In hóa đơn nếu được chọn
                    if (jComboInHoaDon.getSelectedItem().equals("Chọn")) {
                        String maHoaDon = hoaDon.getMaHoaDon();
                        Map<String, Object> thongTin = hoaDonService.loadThongTinHoaDon(maHoaDon);
                        List<Map<String, Object>> danhSachDichVu = hoaDonService.loadDichVuHoaDon(maHoaDon);

                        File pdfFile = new File("src/file/hoadon.pdf");
                        if (pdfFile.exists()) {
                            Desktop.getDesktop().open(pdfFile);
                        } else {
                            mes("Không tìm thấy file PDF để xem trước.");
                        }

                        formTTHoaDon.exportInvoiceToPDF(thongTin, danhSachDichVu, filepath); // Tạo PDF
                        formTTHoaDon.printPreview();
                    }
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                    mes("Lỗi khi trả phòng: " + ex.getMessage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mes("Lỗi không xác định: " + ex.getMessage());
                } finally {
                    if (em != null && em.isOpen()) {
                        em.close();
                    }
                }
            }
        });

        jtableSP.getModel().addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                // TODO Auto-generated method stub
                calculateTotalPrice();
            }
        });

    }
    //
//	/**
//     * This method is called from within the constructor to initialize the form.
//     * WARNING: Do NOT modify this code. The content of this method is always
//     * regenerated by the Form Editor.
//     */
//    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        buttonGroupGT = new javax.swing.ButtonGroup();
        jProgressBar1 = new javax.swing.JProgressBar();
        jPanel2 = new javax.swing.JPanel();
        pCenter = new javax.swing.JPanel();
        pTTKHvsDV = new javax.swing.JPanel();
        pTTKH = new javax.swing.JPanel();
        lblLoaiKH = new javax.swing.JLabel();
        lblGiaTien = new javax.swing.JLabel();
        lblTenKhachHang = new javax.swing.JLabel();
        lblSoCCCD = new javax.swing.JLabel();
        lblTTSoCCCD = new javax.swing.JLabel();
        lblTTTenKH = new javax.swing.JLabel();
        lblTTMaHD = new javax.swing.JLabel();
        lblTTLoaiKH = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        lblGioTraPhong1 = new javax.swing.JLabel();
        jtxtSoLuongN = new javax.swing.JTextField();
        jtxtNgayTraPhong = new javax.swing.JTextField();
        lblNgayNhanPhong = new javax.swing.JLabel();
        jtxtNgayNhanPhong = new javax.swing.JTextField();
        lblGioNhanPhong1 = new javax.swing.JLabel();
        jtxtGioNhanPhong = new javax.swing.JTextField();
        lblNgayTraPhong = new javax.swing.JLabel();
        jtxtGioTraPhong = new javax.swing.JTextField();
        lblSoLuongNguoi = new javax.swing.JLabel();
        lblSoGio = new javax.swing.JLabel();
        jtxtSoGio = new javax.swing.JTextField();
        lblMaHD = new javax.swing.JLabel();
        lblLoaiPhong = new javax.swing.JLabel();
        lblTTLoaiP = new javax.swing.JLabel();
        lblTTGiaTien = new javax.swing.JLabel();
        pTTDV = new javax.swing.JPanel();
        lblThemSanPham = new javax.swing.JLabel();
        btnThemSanPham = new com.raven.datechooser.Button();
        lblSoLuongSP = new javax.swing.JLabel();
        jComboBoxSLSP = new javax.swing.JComboBox<>();
        jScrSP = new javax.swing.JScrollPane();
        jtableSP = new javax.swing.JTable();
        jComboBoxSP = new javax.swing.JComboBox<>();
        btnXoaSanPham = new com.raven.datechooser.Button();
        pThanhToan = new javax.swing.JPanel();
        lblPhuThu = new javax.swing.JLabel();
        jtxtGiaTien = new javax.swing.JTextField();
        jComboBoxPPT = new javax.swing.JComboBox<>();
        lblThanhToan = new javax.swing.JLabel();
        lblTienTe = new javax.swing.JLabel();
        lblPhuongThucTra = new javax.swing.JLabel();
        lblTTThanhToan = new javax.swing.JLabel();
        lblGhiChu = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaGhiChu = new javax.swing.JTextArea();
        lblTraTruoc = new javax.swing.JLabel();
        lblTTTraTruoc = new javax.swing.JLabel();
        lblTienTe1 = new javax.swing.JLabel();
        lblTongTien = new javax.swing.JLabel();
        lblTTTongTien = new javax.swing.JLabel();
        lblTienTe2 = new javax.swing.JLabel();
        pSouth = new javax.swing.JPanel();
        boxButton = new javax.swing.JPanel();
        btnTraPhong = new com.k33ptoo.components.KButton();
        btnHuyBo = new com.k33ptoo.components.KButton();
        jComboInHoaDon = new javax.swing.JComboBox<>();
        lblKieuIn = new javax.swing.JLabel();
        lblNgayLapHoaDon = new javax.swing.JLabel();
        jtxtNgayLapHoaDon = new javax.swing.JTextField();

        setPreferredSize(new java.awt.Dimension(700, 613));
        setLayout(new java.awt.BorderLayout());

        jPanel2.setMinimumSize(new java.awt.Dimension(640, 680));
        jPanel2.setPreferredSize(new java.awt.Dimension(640, 680));
        jPanel2.setLayout(new java.awt.BorderLayout());

        pCenter.setBackground(new java.awt.Color(227, 227, 227));
        pCenter.setPreferredSize(new java.awt.Dimension(671, 729));
        pCenter.setLayout(new java.awt.BorderLayout());

        pTTKHvsDV.setPreferredSize(new java.awt.Dimension(671, 430));
        pTTKHvsDV.setLayout(new java.awt.BorderLayout());

        pTTKH.setBackground(new java.awt.Color(227, 227, 227));
        pTTKH.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Thông Tin Hóa Đơn", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        pTTKH.setPreferredSize(new java.awt.Dimension(671, 280));
        pTTKH.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblLoaiKH.setText("Loại Khách Hàng:");
        pTTKH.add(lblLoaiKH, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 102, 140, 40));

        lblGiaTien.setText("Giá Tiền:");
        pTTKH.add(lblGiaTien, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 40, 110, -1));

        lblTenKhachHang.setText("Tên khách hàng:");
        pTTKH.add(lblTenKhachHang, new org.netbeans.lib.awtextra.AbsoluteConstraints(38, 70, 140, 32));

        lblSoCCCD.setText("Số CCCD:");
        pTTKH.add(lblSoCCCD, new org.netbeans.lib.awtextra.AbsoluteConstraints(383, 106, 100, 30));

        lblTTSoCCCD.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTTSoCCCD.setText("jLabel1");
        lblTTSoCCCD.setMaximumSize(new java.awt.Dimension(75, 30));
        lblTTSoCCCD.setPreferredSize(new java.awt.Dimension(75, 30));
        pTTKH.add(lblTTSoCCCD, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 100, 170, 40));

        lblTTTenKH.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTTTenKH.setText("Nguyễn Trịnh Phương Tuấn");
        lblTTTenKH.setMaximumSize(new java.awt.Dimension(75, 30));
        pTTKH.add(lblTTTenKH, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 70, 200, 30));

        lblTTMaHD.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTTMaHD.setText("jLabel1");
        lblTTMaHD.setMaximumSize(new java.awt.Dimension(75, 30));
        lblTTMaHD.setPreferredSize(new java.awt.Dimension(75, 30));
        pTTKH.add(lblTTMaHD, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 30, 190, -1));

        lblTTLoaiKH.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTTLoaiKH.setText("VIP");
        lblTTLoaiKH.setMaximumSize(new java.awt.Dimension(75, 30));
        lblTTLoaiKH.setMinimumSize(new java.awt.Dimension(39, 0));
        lblTTLoaiKH.setPreferredSize(new java.awt.Dimension(75, 30));
        pTTKH.add(lblTTLoaiKH, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 100, 150, 40));

        jPanel7.setBackground(new java.awt.Color(227, 227, 227));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblGioTraPhong1.setText("Giờ trả phòng:");
        jPanel7.add(lblGioTraPhong1, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 50, 120, 32));

        jtxtSoLuongN.setText("jTextField2");
        jtxtSoLuongN.setPreferredSize(new java.awt.Dimension(75, 30));
        jtxtSoLuongN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtSoLuongNActionPerformed(evt);
            }
        });
        jPanel7.add(jtxtSoLuongN, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 90, 90, 32));

        jtxtNgayTraPhong.setText("jTextField2");
        jtxtNgayTraPhong.setPreferredSize(new java.awt.Dimension(75, 30));
        jtxtNgayTraPhong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtNgayTraPhongActionPerformed(evt);
            }
        });
        jPanel7.add(jtxtNgayTraPhong, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 50, 140, 32));

        lblNgayNhanPhong.setText("Ngày nhận phòng:");
        jPanel7.add(lblNgayNhanPhong, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 150, 32));

        jtxtNgayNhanPhong.setText("jTextField2");
        jtxtNgayNhanPhong.setPreferredSize(new java.awt.Dimension(75, 30));
        jtxtNgayNhanPhong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtNgayNhanPhongActionPerformed(evt);
            }
        });
        jPanel7.add(jtxtNgayNhanPhong, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 10, 140, 32));

        lblGioNhanPhong1.setText("Giờ nhận phòng:");
        jPanel7.add(lblGioNhanPhong1, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 10, 120, 32));

        jtxtGioNhanPhong.setText("jTextField2");
        jtxtGioNhanPhong.setPreferredSize(new java.awt.Dimension(75, 30));
        jtxtGioNhanPhong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtGioNhanPhongActionPerformed(evt);
            }
        });
        jPanel7.add(jtxtGioNhanPhong, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 10, 110, 32));

        lblNgayTraPhong.setText("Ngày trả phòng:");
        jPanel7.add(lblNgayTraPhong, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 150, 32));

        jtxtGioTraPhong.setText("jTextField2");
        jtxtGioTraPhong.setPreferredSize(new java.awt.Dimension(75, 30));
        jtxtGioTraPhong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtGioTraPhongActionPerformed(evt);
            }
        });
        jPanel7.add(jtxtGioTraPhong, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 50, 110, 32));

        lblSoLuongNguoi.setText("Số lượng người:");
        jPanel7.add(lblSoLuongNguoi, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 150, 32));

        lblSoGio.setText("Số giờ/ngày:");
        jPanel7.add(lblSoGio, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 90, 100, 32));

        jtxtSoGio.setText("jTextField2");
        jtxtSoGio.setPreferredSize(new java.awt.Dimension(75, 30));
        jtxtSoGio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtSoGioActionPerformed(evt);
            }
        });
        jPanel7.add(jtxtSoGio, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 90, 110, 32));

        pTTKH.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, 590, 130));

        lblMaHD.setText("Mã hóa đơn:");
        pTTKH.add(lblMaHD, new org.netbeans.lib.awtextra.AbsoluteConstraints(38, 30, 140, 32));

        lblLoaiPhong.setText("Loại phòng:");
        pTTKH.add(lblLoaiPhong, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 80, 110, -1));

        lblTTLoaiP.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTTLoaiP.setText("jLabel1");
        lblTTLoaiP.setMaximumSize(new java.awt.Dimension(75, 30));
        lblTTLoaiP.setPreferredSize(new java.awt.Dimension(75, 30));
        pTTKH.add(lblTTLoaiP, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 70, 170, -1));

        lblTTGiaTien.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTTGiaTien.setText("jLabel1");
        lblTTGiaTien.setMaximumSize(new java.awt.Dimension(75, 30));
        lblTTGiaTien.setPreferredSize(new java.awt.Dimension(75, 30));
        pTTKH.add(lblTTGiaTien, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 30, 160, -1));

        pTTKHvsDV.add(pTTKH, java.awt.BorderLayout.NORTH);

        pTTDV.setBackground(new java.awt.Color(227, 227, 227));
        pTTDV.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Thêm Dịch Vụ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        pTTDV.setPreferredSize(new java.awt.Dimension(671, 155));
        pTTDV.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblThemSanPham.setText("Thêm:");
        pTTDV.add(lblThemSanPham, new org.netbeans.lib.awtextra.AbsoluteConstraints(38, 30, 50, 32));

        btnThemSanPham.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/png/add.png"))); // NOI18N
        btnThemSanPham.setToolTipText("");
        btnThemSanPham.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemSanPhamActionPerformed(evt);
            }
        });
        pTTDV.add(btnThemSanPham, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 30, -1, 30));

        lblSoLuongSP.setText("Số lượng:");
        pTTDV.add(lblSoLuongSP, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 30, 80, 32));

        jComboBoxSLSP.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tùy chọn", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        pTTDV.add(jComboBoxSLSP, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 30, 140, 30));

        jtableSP.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {

                },
                new String [] {
                        "Tên sản phẩm", "Số lượng", "Thành tiền"
                }
        ) {
            Class[] types = new Class [] {
                    java.lang.String.class, java.lang.Integer.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrSP.setViewportView(jtableSP);
        if (jtableSP.getColumnModel().getColumnCount() > 0) {
            jtableSP.getColumnModel().getColumn(0).setResizable(false);
            jtableSP.getColumnModel().getColumn(0).setPreferredWidth(120);
            jtableSP.getColumnModel().getColumn(1).setResizable(false);
            jtableSP.getColumnModel().getColumn(1).setPreferredWidth(30);
            jtableSP.getColumnModel().getColumn(2).setResizable(false);
            jtableSP.getColumnModel().getColumn(2).setPreferredWidth(120);
        }

        pTTDV.add(jScrSP, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 600, 80));

        jComboBoxSP.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nước suối (13.000)", "Nước chanh (20.000)", "Nước dừa (25.000)", "Nước cam (20.000)", "Nước trà (11.000)", "String (18.000)", "Nước soda (13.000)", "Nước ép táo (13.000)", "Nước khoáng (13.000)", "Nước dưa hấu (12.000)", "Nước mật ong (14.000)", "Nước gừng (13.000)", "Nước thảo mộc (12.000)", "Nước bí đao (13.000)", "Nước mía (10.000)", "Snack Poca (25.000)", "Kẹo dẻo (18.000)", "Bánh Oreo (15.000)", "Mì Hảo Hảo (8.000)", "Mì Indomie (15.000)", " " }));
        pTTDV.add(jComboBoxSP, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 30, 190, 30));

        btnXoaSanPham.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/png/remove.png"))); // NOI18N
        btnXoaSanPham.setToolTipText("");
        btnXoaSanPham.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaSanPhamActionPerformed(evt);
            }
        });
        pTTDV.add(btnXoaSanPham, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 30, -1, 30));

        pTTKHvsDV.add(pTTDV, java.awt.BorderLayout.SOUTH);

        pCenter.add(pTTKHvsDV, java.awt.BorderLayout.NORTH);

        pThanhToan.setBackground(new java.awt.Color(227, 227, 227));
        pThanhToan.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "Thanh Toán", javax.swing.border.TitledBorder.LEADING, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        pThanhToan.setMinimumSize(new java.awt.Dimension(620, 40));
        pThanhToan.setPreferredSize(new java.awt.Dimension(671, 40));
        pThanhToan.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblPhuThu.setText("Phụ thu:");
        pThanhToan.add(lblPhuThu, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 60, 32));

        jtxtGiaTien.setText("jTextField1");
        jtxtGiaTien.setPreferredSize(new java.awt.Dimension(70, 30));
        jtxtGiaTien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtGiaTienActionPerformed(evt);
            }
        });
        pThanhToan.add(jtxtGiaTien, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 30, 130, -1));

        jComboBoxPPT.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tiền Mặt", "Ngân Hàng" }));
        jComboBoxPPT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxPPTActionPerformed(evt);
            }
        });
        pThanhToan.add(jComboBoxPPT, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 140, 140, 32));

        lblThanhToan.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblThanhToan.setForeground(new java.awt.Color(255, 0, 0));
        lblThanhToan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/png/cash computer.png"))); // NOI18N
        lblThanhToan.setText("Thanh Toán:");
        pThanhToan.add(lblThanhToan, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 110, 140, 32));

        lblTienTe.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTienTe.setForeground(new java.awt.Color(255, 0, 51));
        lblTienTe.setText("VNĐ");
        pThanhToan.add(lblTienTe, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 110, -1, 30));

        lblPhuongThucTra.setText("Phương thức trả:");
        pThanhToan.add(lblPhuongThucTra, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, 120, 30));

        lblTTThanhToan.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTTThanhToan.setText("20.000.000");
        lblTTThanhToan.setMaximumSize(new java.awt.Dimension(75, 30));
        lblTTThanhToan.setPreferredSize(new java.awt.Dimension(75, 30));
        pThanhToan.add(lblTTThanhToan, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 110, 90, -1));

        lblGhiChu.setText("ghi chú:");
        pThanhToan.add(lblGhiChu, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 70, 60, 32));

        jTextAreaGhiChu.setColumns(20);
        jTextAreaGhiChu.setRows(5);
        jScrollPane1.setViewportView(jTextAreaGhiChu);

        pThanhToan.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 70, 240, 60));

        lblTraTruoc.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTraTruoc.setForeground(new java.awt.Color(255, 0, 0));
        lblTraTruoc.setText("Trả Trước:");
        pThanhToan.add(lblTraTruoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 70, 100, 32));

        lblTTTraTruoc.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTTTraTruoc.setText("20.000.000");
        lblTTTraTruoc.setMaximumSize(new java.awt.Dimension(75, 30));
        lblTTTraTruoc.setPreferredSize(new java.awt.Dimension(75, 30));
        pThanhToan.add(lblTTTraTruoc, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 70, 90, -1));

        lblTienTe1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTienTe1.setForeground(new java.awt.Color(255, 0, 51));
        lblTienTe1.setText("VNĐ");
        pThanhToan.add(lblTienTe1, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 70, -1, 30));

        lblTongTien.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTongTien.setForeground(new java.awt.Color(255, 0, 0));
        lblTongTien.setText("Tổng tiền:");
        pThanhToan.add(lblTongTien, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 30, 100, 32));

        lblTTTongTien.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTTTongTien.setText("20.000.000");
        lblTTTongTien.setMaximumSize(new java.awt.Dimension(75, 30));
        lblTTTongTien.setPreferredSize(new java.awt.Dimension(75, 30));
        pThanhToan.add(lblTTTongTien, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 30, 90, -1));

        lblTienTe2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTienTe2.setForeground(new java.awt.Color(255, 0, 51));
        lblTienTe2.setText("VNĐ");
        pThanhToan.add(lblTienTe2, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 30, -1, 30));

        pCenter.add(pThanhToan, java.awt.BorderLayout.CENTER);

        pSouth.setBackground(new java.awt.Color(227, 227, 227));
        pSouth.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        boxButton.setBackground(new java.awt.Color(227, 227, 227));

        btnTraPhong.setText("Trả Phòng");
        btnTraPhong.setkHoverForeGround(new java.awt.Color(204, 255, 204));
        btnTraPhong.setkHoverStartColor(new java.awt.Color(102, 255, 102));
        btnTraPhong.setkSelectedColor(new java.awt.Color(0, 255, 51));
        btnTraPhong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTraPhongActionPerformed(evt);
            }
        });

        btnHuyBo.setText("Hủy bỏ");
        btnHuyBo.setkEndColor(new java.awt.Color(255, 255, 0));
        btnHuyBo.setkHoverEndColor(new java.awt.Color(255, 204, 0));
        btnHuyBo.setkHoverForeGround(new java.awt.Color(255, 102, 102));
        btnHuyBo.setkHoverStartColor(new java.awt.Color(255, 51, 51));
        btnHuyBo.setkSelectedColor(new java.awt.Color(255, 51, 0));
        btnHuyBo.setkStartColor(new java.awt.Color(255, 51, 51));

        javax.swing.GroupLayout boxButtonLayout = new javax.swing.GroupLayout(boxButton);
        boxButton.setLayout(boxButtonLayout);
        boxButtonLayout.setHorizontalGroup(
                boxButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, boxButtonLayout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(btnHuyBo, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                                .addComponent(btnTraPhong, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
        boxButtonLayout.setVerticalGroup(
                boxButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(boxButtonLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(boxButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnHuyBo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnTraPhong, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pSouth.add(boxButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 0, 290, 60));

        jComboInHoaDon.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Không chọn", "Chọn" }));
        jComboInHoaDon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboInHoaDonActionPerformed(evt);
            }
        });
        pSouth.add(jComboInHoaDon, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, -1, 30));

        lblKieuIn.setBackground(new java.awt.Color(204, 204, 204));
        lblKieuIn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblKieuIn.setForeground(new java.awt.Color(255, 51, 0));
        lblKieuIn.setText("Print:");
        pSouth.add(lblKieuIn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 50, 30));

        lblNgayLapHoaDon.setText("Ngày lập:");
        pSouth.add(lblNgayLapHoaDon, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 10, 70, 32));

        jtxtNgayLapHoaDon.setText("jTextField2");
        jtxtNgayLapHoaDon.setPreferredSize(new java.awt.Dimension(75, 30));
        jtxtNgayLapHoaDon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtNgayLapHoaDonActionPerformed(evt);
            }
        });
        pSouth.add(jtxtNgayLapHoaDon, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 10, 110, 32));

        pCenter.add(pSouth, java.awt.BorderLayout.SOUTH);

        jPanel2.add(pCenter, java.awt.BorderLayout.CENTER);

        add(jPanel2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>

    private void jtxtGiaTienActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void btnThemSanPhamActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void btnTraPhongActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void jComboInHoaDonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void jtxtGioTraPhongActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void jtxtGioNhanPhongActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void jtxtNgayNhanPhongActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void jtxtNgayTraPhongActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void jtxtSoLuongNActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void jtxtSoGioActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void jtxtNgayLapHoaDonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void btnXoaSanPhamActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void jComboBoxPPTActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }
    // Phương thức tính tiền
    // Biến thành viên để lưu giá trị tiền dịch vụ
    private BigDecimal giaTienDichVu = BigDecimal.ZERO;
    private void calculateTotalPrice() {
        giaTienPhong = BigDecimal.valueOf(Double.parseDouble(luuTien));
        jtxtGiaTien.setEditable(true);
        jtxtGiaTien.setEnabled(true);

        // Lắng nghe thay đổi trên `jtxtGiaTien` (phụ thu) và chỉ cập nhật phụ thu
        jtxtGiaTien.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                // Chỉ tính lại phụ thu khi thay đổi trong `jtxtGiaTien`
                refreshTotalPrice();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                // Chỉ tính lại phụ thu khi thay đổi trong `jtxtGiaTien`
                refreshTotalPrice();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Chỉ tính lại phụ thu khi thay đổi trong `jtxtGiaTien`
                refreshTotalPrice();
            }
        });

        // Lắng nghe thay đổi trong bảng dịch vụ (chỉ tính lại tiền dịch vụ)
        jtableSP.getModel().addTableModelListener(e -> {
            // Cập nhật lại tiền dịch vụ khi có thay đổi trong bảng
            refreshTotalPrice();
        });

        // Khởi tạo giá trị tổng ban đầu
        refreshTotalPrice();
    }

    private void refreshTotalPrice() {

        try {
            // Tính tiền phụ thu (dựa trên `jtxtGiaTien`)
            BigDecimal giaTienThem = calculateSurcharge();


            // Tính tiền dịch vụ từ bảng `jtableSP`
            giaTienDichVu = BigDecimal.valueOf(calculateServiceTotal());



            // Tính thuế trên tiền phòng
            BigDecimal tienThue = giaTienPhong.multiply(BigDecimal.valueOf(0.08));
            BigDecimal tongTienPhongSauThue = giaTienPhong.add(tienThue);

            // Tổng thanh toán = Tiền phòng (sau thuế) + Dịch vụ + Phụ thu
            BigDecimal tongThanhToan = tongTienPhongSauThue.add(giaTienDichVu).add(giaTienThem).subtract(traTruoc);

            // Tổng tiền thuế = Tiền phòng + Dịch vụ + Phụ thu
            BigDecimal tongTienChuaThue = giaTienPhong.add(tienThue).add(giaTienDichVu).add(giaTienThem);

            // Cập nhật giao diện
            lblTTTongTien.setText(formatToVietnamCurrency(tongTienChuaThue.toPlainString())); // Tổng tiền chưa thuế
            lblTTThanhToan.setText(formatToVietnamCurrency(tongThanhToan.toPlainString())); // Tổng thanh toán

            // Lưu giá trị thanh toán
            luuTTTien = tongThanhToan.toPlainString();
        } catch (NumberFormatException ex) {
            mes("Vui lòng nhập số hợp lệ.");
        }

    }

    // Hàm tính tiền phụ thu (dựa vào `jtxtGiaTien`)
    private BigDecimal calculateSurcharge() {
        try {
            String inputText = jtxtGiaTien.getText().trim(); // Lấy giá trị từ `jtxtGiaTien`
            return inputText.isEmpty() ? BigDecimal.ZERO : new BigDecimal(inputText);
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO; // Trả về 0 nếu giá trị không hợp lệ
        }
    }

    // Hàm tính tổng tiền từ bảng dịch vụ (`jtableSP`)
    private double calculateServiceTotal() {
        double tongTienDichVu = 0;
        for (int i = 0; i < jtableSP.getRowCount(); i++) {
            Object value = jtableSP.getValueAt(i, 2); // Lấy giá trị từ cột "Thành tiền"
            if (value instanceof Number) {
                tongTienDichVu += ((Number) value).doubleValue();
            } else if (value instanceof String) {
                try {
                    tongTienDichVu += Double.parseDouble((String) value);
                } catch (NumberFormatException ex) {
                    // Bỏ qua giá trị không hợp lệ
                }
            }
        }
        return tongTienDichVu;
    }


    public void xoaTrang() {
//    	jtxtGiamTru.setText("");
        jtxtGiaTien.setText("");
        jtxtGioNhanPhong.setText("");
        jtxtGioTraPhong.setText("");
        jtxtNgayLapHoaDon.setText("");
        jtxtNgayNhanPhong.setText("");
        lblTTMaHD.setText("");
        jtxtNgayTraPhong.setText("");
        lblTTSoCCCD.setText("");
        lblTTGiaTien.setText("");
        lblTTTenKH.setText("");
        lblTTLoaiKH.setText("");
        lblTTLoaiP.setText("");
//    	jtxtTraTruoc.setText("");
        jtxtSoLuongN.setText("");
        jtxtSoGio.setText("");
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
        jtxtNgayLapHoaDon.setEditable(false);
        jtxtGiaTien.setEditable(true);
    }
    public void upLoadDuLieuTTPD() {
        try {
            Phong phongKT = phongService.findPhongByMaPhong(tenPhong);
            if (phongKT == null) {
                mes("Không tìm thấy phòng với mã: " + tenPhong);
                return;
            }

            xoaThuocTinh();
            jtxtNgayNhanPhong.setEditable(false);
            jtxtNgayTraPhong.setEditable(false);
            jtxtGioNhanPhong.setEditable(false);
            jtxtGioTraPhong.setEditable(false);
            jtxtSoLuongN.setEnabled(false);
            jtxtSoGio.setEnabled(false);

            if (phongKT.getTrangThaiPhong().getMaTrangThai() == 1) { // 1 = ĐANG Ở
                chiTietPhieuDat = chiTietPhieuDatService.findChiTietPhieuDatDangO(tenPhong);

                if (chiTietPhieuDat != null) {
                    // Đã có chi tiết phiếu đặt
                    String maPhieuDat = chiTietPhieuDat.getMaPhieuDat().getMaPhieuDat();
                    phieuDatPhong = phieuDatService.findPhieuDatPhongByMa(maPhieuDat);
                    if (phieuDatPhong == null) {
                        mes("Không tìm thấy phiếu đặt với mã: " + maPhieuDat);
                        return;
                    }

                    khachHang = phieuDatPhong.getMaKhachHang();
                    nhanVien = phieuDatPhong.getMaNhanVien();

                    lblTTTenKH.setText(khachHang.getTenKhachHang());
                    lblTTSoCCCD.setText(khachHang.getCccd());
                    lblTTLoaiKH.setText(khachHang.getLoaiKhachHang() ? "Thường (Vãng lai)" : "VIP (Thành viên)");

                    int loaiPhong = chiTietPhieuDat.getPhong().getLoaiPhong().getTenLoai();
                    switch (loaiPhong) {
                        case 1: lblTTLoaiP.setText("Một giường đơn"); break;
                        case 2: lblTTLoaiP.setText("Hai giường đơn"); break;
                        case 3: lblTTLoaiP.setText("Một giường đơn, Một giường đôi"); break;
                        case 4: lblTTLoaiP.setText("Hai giường đôi"); break;
                        default: lblTTLoaiP.setText("Không xác định"); break;
                    }

                    jtxtNgayNhanPhong.setText(chuyenDoiFormat(chiTietPhieuDat.getNgayNhanPhong().toString()));
                    jtxtNgayTraPhong.setText(chuyenDoiFormat(chiTietPhieuDat.getNgayTraPhong().toString()));
                    jtxtGioNhanPhong.setText(formatLocalTime(chiTietPhieuDat.getGioNhanPhong()));
                    jtxtGioTraPhong.setText(formatLocalTime(chiTietPhieuDat.getGioTraPhong()));
                    jtxtSoLuongN.setText(String.valueOf(phieuDatPhong.getSoLuongNguoi()));
                    jtxtSoGio.setText(String.valueOf(chiTietPhieuDat.getSoGio()));

                    BigDecimal tienPhong = BigDecimal.valueOf(phieuDatPhong.getTienPhong(chiTietPhieuDat, 0));
                    luuTien = luuTTTien = tienPhong.toPlainString();
                    lblTTGiaTien.setText(formatToVietnamCurrency(tienPhong.toPlainString()));

                    traTruoc = BigDecimal.valueOf(phieuDatPhong.getTraTruoc());
                    lblTTTraTruoc.setText(formatToVietnamCurrency(traTruoc.toPlainString()));

                    BigDecimal tongTien = tienPhong.multiply(BigDecimal.valueOf(1.08)); // thuế 8%
                    lblTTTongTien.setText(formatToVietnamCurrency(tongTien.toPlainString()));
                    lblTTThanhToan.setText(formatToVietnamCurrency(tongTien.subtract(traTruoc).toPlainString()));

                    jtxtNgayLapHoaDon.setText(chuyenDoiFormat(LocalDate.now().toString()));

                } else {
                    // Phòng đã quá giờ, không còn chi tiết phiếu đặt
                    mes("Phòng đã quá giờ trả phòng!!");

                    int confirm = JOptionPane.showConfirmDialog(this,
                            "Phòng đã quá giờ trả phòng. Bạn có muốn chuyển sang trạng thái BẢO TRÌ không?",
                            "Xác nhận", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        phongService.updatePhong(tenPhong, null, TrangThaiPhong.DANG_BAO_TRI);
                        guiQuanLiDatPhong.xoaDuLieuPhong();
                        dsPhong = (ArrayList<Phong>) phongService.loadAllPhong();
                        guiQuanLiDatPhong.upLoadDataJpanel(dsPhong);
                        mes("Phòng đã được chuyển sang trạng thái BẢO TRÌ.");
                    }
                }

            } else {
                mes("Vui lòng chọn phòng đã có khách Check-In.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mes("Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    public void setComboBoxData(javax.swing.JComboBox<String> jComboBoxSP) {
        try {
            // Tải danh sách đồ ăn thức uống từ DoAnThucUongService
            List<DoAnThucUong> dsSP = doAnThucUongService.getAllDoAnThucUong();
            if (dsSP == null) {
                JOptionPane.showMessageDialog(this, "Không tải được danh sách sản phẩm.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ArrayList<String> items = new ArrayList<>();
            jComboBoxSP.removeAllItems();

            // Định dạng cho giá bán
            DecimalFormat df = new DecimalFormat("#,###");

            for (DoAnThucUong sp : dsSP) {
                // Định dạng "Tên sản phẩm (giá bán)" với giá bán đã được định dạng
                String item = sp.getTenSanPham() + " (" + df.format(sp.getGiaBan()) + ")";
                items.add(item);
            }

            // Thiết lập dữ liệu vào JComboBox
            jComboBoxSP.setModel(new DefaultComboBoxModel<>(items.toArray(new String[0])));
        } catch (RemoteException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách sản phẩm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    public String formatCurrency(double value) {
        return df.format(value);
    }

    //	public String formatCurrency(String value) {
//	    // Xóa dấu phẩy để chuyển đổi về số
//	    String sanitizedValue = value.replace(",", "").trim();
//	    try {
//	        double parsedValue = Double.parseDouble(sanitizedValue);
//	        return df.format(parsedValue);
//	    } catch (NumberFormatException e) {
//	        return value; // Nếu không thể phân tích, trả lại chuỗi ban đầu
//	    }
//	}
//
//	public void addFormatListener(JTextField textField) {
//	    // Định dạng khi người dùng rời khỏi trường nhập liệu
//	    textField.addFocusListener(new FocusAdapter() {
//	        @Override
//	        public void focusLost(FocusEvent e) {
//	            textField.setText(formatCurrency(textField.getText()));
//	        }
//	    });
//	}
    public List<DoAnThucUong> layDanhSachSanPham(JTable jtable) throws RemoteException {
        List<DoAnThucUong> danhSachSanPhamTrongTable = new ArrayList<>();
        ArrayList<DoAnThucUong> dsSP = (ArrayList<DoAnThucUong>) doAnThucUongService.getAllDoAnThucUong();

        for (int i = 0; i < jtable.getRowCount(); i++) {
            String tenSanPham = jtable.getValueAt(i, 0).toString();
            for (DoAnThucUong doAnThucUong : dsSP) {
                if(tenSanPham.equalsIgnoreCase(doAnThucUong.getTenSanPham()))
                    danhSachSanPhamTrongTable.add(doAnThucUong);
            }
        }
        return danhSachSanPhamTrongTable;
    }
    public static String formatToVietnamCurrency(String input) {
        try {
            // Loại bỏ các ký tự không phải số hoặc dấu thập phân
            String numericText = input.replaceAll("[^\\d.]", "");

            // Kiểm tra nếu chuỗi rỗng hoặc không hợp lệ
            if (numericText.isEmpty()) {
                return "";
            }

            // Chuyển chuỗi thành số
            double amount = Double.parseDouble(numericText);

            // Tạo định dạng số
            DecimalFormatSymbols vietnamSymbols = new DecimalFormatSymbols();
            vietnamSymbols.setGroupingSeparator('.'); // Dấu phân cách nghìn
            vietnamSymbols.setDecimalSeparator(','); // Dấu phân cách phần thập phân

            // Định dạng mà không có ký hiệu ₫
            DecimalFormat vietnamFormat = new DecimalFormat("#,##0.00");
            vietnamFormat.setDecimalFormatSymbols(vietnamSymbols);

            // Trả về chuỗi đã định dạng
            return vietnamFormat.format(amount);
        } catch (NumberFormatException e) {
            // Xử lý lỗi định dạng
            return "";
        }
    }
    public String convertCurrencyString(String currency) {
        // Loại bỏ các dấu phân cách nhóm (chấm) và dấu thập phân (phẩy)
        String result = currency.replace(".", "").replace(",", "");
        return result;
    }
    // Variables declaration - do not modify
    private javax.swing.JPanel boxButton;
    private com.k33ptoo.components.KButton btnHuyBo;
    private com.raven.datechooser.Button btnThemSanPham;
    private com.k33ptoo.components.KButton btnTraPhong;
    private com.raven.datechooser.Button btnXoaSanPham;
    private javax.swing.ButtonGroup buttonGroupGT;
    private javax.swing.JComboBox<String> jComboBoxPPT;
    private javax.swing.JComboBox<String> jComboBoxSLSP;
    private javax.swing.JComboBox<String> jComboBoxSP;
    private javax.swing.JComboBox<String> jComboInHoaDon;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrSP;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaGhiChu;
    private javax.swing.JTable jtableSP;
    private javax.swing.JTextField jtxtGiaTien;
    private javax.swing.JTextField jtxtGioNhanPhong;
    private javax.swing.JTextField jtxtGioTraPhong;
    private javax.swing.JTextField jtxtNgayLapHoaDon;
    private javax.swing.JTextField jtxtNgayNhanPhong;
    private javax.swing.JTextField jtxtNgayTraPhong;
    private javax.swing.JTextField jtxtSoGio;
    private javax.swing.JTextField jtxtSoLuongN;
    private javax.swing.JLabel lblGhiChu;
    private javax.swing.JLabel lblGiaTien;
    private javax.swing.JLabel lblGioNhanPhong1;
    private javax.swing.JLabel lblGioTraPhong1;
    private javax.swing.JLabel lblKieuIn;
    private javax.swing.JLabel lblLoaiKH;
    private javax.swing.JLabel lblLoaiPhong;
    private javax.swing.JLabel lblMaHD;
    private javax.swing.JLabel lblNgayLapHoaDon;
    private javax.swing.JLabel lblNgayNhanPhong;
    private javax.swing.JLabel lblNgayTraPhong;
    private javax.swing.JLabel lblPhuThu;
    private javax.swing.JLabel lblPhuongThucTra;
    private javax.swing.JLabel lblSoCCCD;
    private javax.swing.JLabel lblSoGio;
    private javax.swing.JLabel lblSoLuongNguoi;
    private javax.swing.JLabel lblSoLuongSP;
    private javax.swing.JLabel lblTTGiaTien;
    private javax.swing.JLabel lblTTLoaiKH;
    private javax.swing.JLabel lblTTLoaiP;
    private javax.swing.JLabel lblTTMaHD;
    private javax.swing.JLabel lblTTSoCCCD;
    private javax.swing.JLabel lblTTTenKH;
    private javax.swing.JLabel lblTTThanhToan;
    private javax.swing.JLabel lblTTTongTien;
    private javax.swing.JLabel lblTTTraTruoc;
    private javax.swing.JLabel lblTenKhachHang;
    private javax.swing.JLabel lblThanhToan;
    private javax.swing.JLabel lblThemSanPham;
    private javax.swing.JLabel lblTienTe;
    private javax.swing.JLabel lblTienTe1;
    private javax.swing.JLabel lblTienTe2;
    private javax.swing.JLabel lblTongTien;
    private javax.swing.JLabel lblTraTruoc;
    private javax.swing.JPanel pCenter;
    private javax.swing.JPanel pSouth;
    private javax.swing.JPanel pTTDV;
    private javax.swing.JPanel pTTKH;
    private javax.swing.JPanel pTTKHvsDV;
    private javax.swing.JPanel pThanhToan;
    // End of variables declaration


}
