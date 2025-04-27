package client;

import dto.HoaDonDTO;
import entity.*;
import rmi.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;

public class RMIClient {
    private static final Logger LOGGER = Logger.getLogger(RMIClient.class.getName());
    private PhongService phongService;
    private PhieuDatService phieuDatService;
    private HoaDonService hoaDonService;
    private ChiTietHoaDonService chiTietHoaDonService;
    private ChiTietPhieuDatService chiTietPhieuDatService;
    private DoAnThucUongService doAnThucUongService;
    private KhachHangService khachHangService;
    private NhanVienService nhanVienService;

    public RMIClient() {
        try {
            Registry registry = LocateRegistry.getRegistry("192.168.121.163", 1099);
            phongService = (PhongService) registry.lookup("PhongService");
            phieuDatService = (PhieuDatService) registry.lookup("PhieuDatService");
            hoaDonService = (HoaDonService) registry.lookup("HoaDonService");
            chiTietHoaDonService = (ChiTietHoaDonService) registry.lookup("ChiTietHoaDonService");
            chiTietPhieuDatService = (ChiTietPhieuDatService) registry.lookup("ChiTietPhieuDatService");
            doAnThucUongService = (DoAnThucUongService) registry.lookup("DoAnThucUongService");
            khachHangService = (KhachHangService) registry.lookup("KhachHangService");
            nhanVienService = (NhanVienService) registry.lookup("NhanVienService");
            LOGGER.info("Kết nối RMI Server thành công!");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi kết nối RMI Server: " + e.getMessage(), e);
        }
    }


    // PHÒNG
    public PhongService getPhongService() {
        return phongService;
    }
    public HoaDonService getHoaDonService() {
        return hoaDonService;
    }
    public ChiTietPhieuDatService getChiTietPhieuDatService() {
        return chiTietPhieuDatService;
    }
    public KhachHangService getKhachHangService() {
        return khachHangService;
    }

    public PhieuDatService getPhieuDatService() {
        return phieuDatService;
    }

    public ChiTietHoaDonService getChiTietHoaDonService() {
        return chiTietHoaDonService;
    }

    public DoAnThucUongService getDoAnThucUongService() {
        return doAnThucUongService;
    }

    public List<Phong> loadTatCaPhong() {
        try {
            System.out.println("Client: Gọi server.layTatCaPhong");
            return phongService.loadAllPhong(); // Gọi đến RMI Server
        } catch (RemoteException e) {
            System.err.println("Client: Lỗi khi gọi server.layTatCaPhong - " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // Tránh null pointer
        }
    }
    public Phong getPhongByMaPhong(String maPhong) {
        try {
            return phongService.findPhongByMaPhong(maPhong);
        } catch (Exception e) {
            System.err.println("Lỗi gọi findPhongByMaPhong: " + e.getMessage());
            return null;
        }
    }

    public List<Phong> getPhongByTrangThai(TrangThaiPhong trangThai) {
        if (phongService == null) {
            LOGGER.severe("PhongService is null. Không thể gọi phương thức từ xa.");
            return null;
        }
        try {
            return phongService.findPhongByTrangThai(trangThai);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi gọi findPhongByTrangThai: " + e.getMessage(), e);
            return null;
        }
    }
    public List<Phong> timPhongTheoTTPhong(LoaiPhong loaiPhong, TrangThaiPhong trangThai, int soNgay, String ngayNhan) {
        try {
            return phongService.timPhongTheoTTPhong(loaiPhong, trangThai, ngayNhan, soNgay);
        } catch (Exception e) {
            System.err.println("Lỗi gọi timPhongTheoTTPhong: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Phong> getPhongByLoaiPhong(LoaiPhong loaiPhong) {
        if (phongService == null) {
            LOGGER.severe("PhongService is null. Không thể gọi phương thức từ xa.");
            return null;
        }
        try {
            return phongService.findPhongByLoaiPhong(loaiPhong);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi gọi findPhongByLoaiPhong: " + e.getMessage(), e);
            return null;
        }
    }
    public List<Phong> getPhongByLoaiVaTrangThai(LoaiPhong loaiPhong, TrangThaiPhong trangThai) {
        try {
            return phongService.findPhongByLoaiVaTrangThai(loaiPhong, trangThai);
        } catch (Exception e) {
            System.err.println("Lỗi gọi findPhongByLoaiVaTrangThai: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean capNhatPhong(String maPhong, Double giaPhongMoi, TrangThaiPhong trangThaiMoi) {
        if (phongService == null) {
            LOGGER.severe("PhongService is null. Không thể gọi phương thức từ xa.");
            return false;
        }
        try {
            return phongService.updatePhong(maPhong, giaPhongMoi, trangThaiMoi);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi gọi updatePhong: " + e.getMessage(), e);
            return false;
        }
    }

    public List<Phong> getPhongTrongThoiGian(LocalDate batDau, LocalDate ketThuc) {
        if (phongService == null) {
            LOGGER.severe("PhongService is null. Không thể gọi phương thức từ xa.");
            return null;
        }
        try {
            return phongService.findPhongTrongThoiGianDat(batDau, ketThuc);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi gọi findPhongTrongThoiGianDat: " + e.getMessage(), e);
            return null;
        }
    }

    public List<Phong> getPhongBySoDienThoai(String soDienThoai) {
        if (phongService == null) {
            LOGGER.severe("PhongService is null. Không thể gọi phương thức từ xa.");
            return null;
        }
        try {
            return phongService.findPhongBySoDienThoaiKhachHang(soDienThoai);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi gọi findPhongBySoDienThoaiKhachHang: " + e.getMessage(), e);
            return null;
        }
    }


    // ĐỒ ĂN/THỨC UỐNG
    public DoAnThucUong findDoAnThucUongByMa(String maSanPham) {
        if (doAnThucUongService == null) {
            LOGGER.severe("DoAnThucUongService is null. Không thể gọi phương thức từ xa.");
            return null;
        }
        try {
            return doAnThucUongService.findDoAnThucUongByMa(maSanPham);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi gọi findDoAnThucUongByMa: " + e.getMessage(), e);
            return null;
        }
    }

    public List<DoAnThucUong> getAllDoAnThucUong() {
        if (doAnThucUongService == null) {
            LOGGER.severe("DoAnThucUongService is null. Không thể gọi phương thức từ xa.");
            return null;
        }
        try {
            return doAnThucUongService.getAllDoAnThucUong();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi gọi getAllDoAnThucUong: " + e.getMessage(), e);
            return null;
        }
    }
    public HoaDon themHoaDon(HoaDon hoaDon) {
        try {
            return hoaDonService.themHoaDon(hoaDon);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    // CHI TIẾT HÓA ĐƠN
    public boolean createRandomChiTietHoaDon(int quantity) {
        if (chiTietHoaDonService == null) {
            LOGGER.severe("ChiTietHoaDonService is null. Không thể gọi phương thức từ xa.");
            return false;
        }
        try {
            return chiTietHoaDonService.createRandomChiTietHoaDon(quantity);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi gọi createRandomChiTietHoaDon: " + e.getMessage(), e);
            return false;
        }
    }

    public List<ChiTietHoaDon> getAllChiTietHoaDon() {
        if (chiTietHoaDonService == null) {
            LOGGER.severe("ChiTietHoaDonService is null. Không thể gọi phương thức từ xa.");
            return null;
        }
        try {
            return chiTietHoaDonService.getAllChiTietHoaDon();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi gọi getAllChiTietHoaDon: " + e.getMessage(), e);
            return null;
        }
    }


    // HÓA ĐƠN
    //het main
    public List<Map<String, Object>> loadDichVuHoaDon(String maHoaDon) throws RemoteException {
        List<Map<String, Object>> danhSachDichVu = new ArrayList<>();

        // Sử dụng hoaDonService để gọi phương thức từ server
        try {
            danhSachDichVu = hoaDonService.loadDichVuHoaDon(maHoaDon);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Lỗi khi gọi phương thức loadDichVuHoaDon", e);
        }

        return danhSachDichVu;
    }


    // Lấy danh sách tất cả hóa đơn

    public List<HoaDon> getAllHoaDon() {
        if (hoaDonService == null) {
            LOGGER.severe("HoaDonService is null. Không thể gọi phương thức từ xa.");
            return null;
        }
        try {
            return hoaDonService.getAllHoaDon();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi gọi getAllHoaDon: " + e.getMessage(), e);
            return null;
        }
    }

    public HoaDon getHoaDonByMa(String ma) {
        if (hoaDonService == null) {
            LOGGER.severe("HoaDonService is null. Không thể gọi phương thức từ xa.");
            return null;
        }
        try {
            return hoaDonService.findHoaDonByMa(ma);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi gọi findHoaDonByMa: " + e.getMessage(), e);
            return null;
        }
    }

    public boolean taoHoaDonNgauNhien(int soDot) {
        if (hoaDonService == null) {
            LOGGER.severe("HoaDonService is null. Không thể gọi phương thức từ xa.");
            return false;
        }
        try {
            return hoaDonService.createRandomHoaDon(soDot);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi gọi createRandomHoaDon: " + e.getMessage(), e);
            return false;
        }
    }

    public double tinhTongTienHoaDon(String maHoaDon) {
        if (hoaDonService == null) {
            LOGGER.severe("HoaDonService is null. Không thể gọi phương thức từ xa.");
            return -1;
        }
        try {
            return hoaDonService.calculateTotalAmount(maHoaDon);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi gọi calculateTotalAmount: " + e.getMessage(), e);
            return -1;
        }
    }

    public List<HoaDon> getHoaDonTheoNgay(LocalDate ngay) {
        if (hoaDonService == null) {
            LOGGER.severe("HoaDonService is null. Không thể gọi phương thức từ xa.");
            return null;
        }
        try {
            return hoaDonService.findHoaDonByNgayLap(ngay);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi gọi findHoaDonByNgayLap: " + e.getMessage(), e);
            return null;
        }
    }

    public List<HoaDonDTO> timKiemHoaDonTheoTuKhoa(String tuKhoa) {
        try {
            System.out.println("Client: Gọi timKiemHoaDonTheoTuKhoa với từ khóa: [" + tuKhoa + "]");

            // Gọi service để lấy danh sách hóa đơn DTO
            List<HoaDonDTO> result = hoaDonService.timKiemHoaDonTheoTuKhoa(tuKhoa);
            System.out.println("Client: Nhận được " + (result != null ? result.size() : 0) + " kết quả");

            return result != null ? result : new ArrayList<>(); // Trả về danh sách kết quả hoặc danh sách rỗng
        } catch (Exception e) {
            System.err.println("Client: Lỗi gọi timKiemHoaDonTheoTuKhoa: " + e.getMessage());
            e.printStackTrace(); // Log stack trace đầy đủ
            return new ArrayList<>(); // Trả danh sách rỗng thay vì null
        }
    }

    public Map<String, Object> loadThongTinHoaDon(String maHoaDon) {
        try {
            return hoaDonService.loadThongTinHoaDon(maHoaDon);
        } catch (Exception e) {
            System.err.println("Lỗi gọi loadThongTinHoaDon: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    public List<HoaDonDTO> timKiemHoaDonTheoNgay(LocalDate from, LocalDate to) {
        try {
            return hoaDonService.findHoaDonByNgayLapBetween(from, to);
        } catch (Exception e) {
            System.err.println("Lỗi gọi findHoaDonByNgayLapBetween: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


// ========== KHÁCH HÀNG ==========

    // KHÁCH HÀNG
    public List<KhachHang> getKhachHangBySoDienThoai(String soDienThoai) {
        if (khachHangService == null) {
            LOGGER.severe("KhachHangService is null. Không thể gọi phương thức từ xa.");
            return null;
        }
        try {
            return khachHangService.findKhachHangBySoDienThoai(soDienThoai);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi gọi findKhachHangBySoDienThoai: " + e.getMessage(), e);
            return null;
        }
    }

    public boolean createRandomKhachHang(int quantity) {
        if (khachHangService == null) {
            LOGGER.severe("KhachHangService is null. Không thể gọi phương thức từ xa.");
            return false;
        }
        if (quantity <= 0) {
            LOGGER.warning("Số lượng khách hàng phải lớn hơn 0.");
            return false;
        }
        try {
            khachHangService.createRandomKhachHang(quantity);
            LOGGER.info("Tạo " + quantity + " khách hàng ngẫu nhiên thành công.");
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi gọi createRandomKhachHang: " + e.getMessage(), e);
            return false;
        }
    }

    public boolean capNhatKhachHang(String maKhachHang, String tenKhachHang, boolean gioiTinh, String cccd, LocalDate ngaySinh, String soDienThoai, boolean loaiKhachHang) {
        if (khachHangService == null) {
            LOGGER.severe("KhachHangService is null. Không thể gọi phương thức từ xa.");
            return false;
        }
        if (ngaySinh == null) {
            LOGGER.warning("Ngày sinh không được null.");
            return false;
        }
        if (soDienThoai == null || !soDienThoai.matches("0[1-9][0-9]{8}")) {
            LOGGER.warning("Số điện thoại không hợp lệ.");
            return false;
        }
        if (cccd == null || cccd.length() != 12 || !cccd.matches("\\d+")) {
            LOGGER.warning("CCCD phải gồm 12 chữ số.");
            return false;
        }
        try {
            khachHangService.capNhatTTKhachHang(maKhachHang, tenKhachHang, ngaySinh, gioiTinh, cccd, soDienThoai, loaiKhachHang);
            LOGGER.info("Cập nhật khách hàng " + maKhachHang + " thành công.");
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi gọi capNhatTTKhachHang: " + e.getMessage(), e);
            return false;
        }
    }

    // NHÂN VIÊN
    public String addNhanVien(NhanVien nhanVien) {
        if (nhanVienService == null) {
            LOGGER.severe("NhanVienService is null. Không thể gọi phương thức từ xa.");
            return null;
        }
        try {
            return nhanVienService.addNhanVien(nhanVien);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi gọi addNhanVien: " + e.getMessage(), e);
            return null;
        }
    }

    public boolean updateNhanVien(NhanVien nhanVien) {
        if (nhanVienService == null) {
            LOGGER.severe("NhanVienService is null. Không thể gọi phương thức từ xa.");
            return false;
        }
        try {
            return nhanVienService.updateNhanVien(nhanVien);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi gọi updateNhanVien: " + e.getMessage(), e);
            return false;
        }
    }

    public boolean deleteNhanVien(String maNhanVien) {
        if (nhanVienService == null) {
            LOGGER.severe("NhanVienService is null. Không thể gọi phương thức từ xa.");
            return false;
        }
        try {
            return nhanVienService.deleteNhanVien(maNhanVien);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi gọi deleteNhanVien: " + e.getMessage(), e);
            return false;
        }
    }

    public NhanVien findNhanVienByMa(String maNhanVien) {
        if (nhanVienService == null) {
            LOGGER.severe("NhanVienService is null. Không thể gọi phương thức từ xa.");
            return null;
        }
        try {
            return nhanVienService.findNhanVienByMa(maNhanVien);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi gọi findNhanVienByMa: " + e.getMessage(), e);
            return null;
        }
    }

    public List<NhanVien> getAllNhanVien() {
        if (nhanVienService == null) {
            LOGGER.severe("NhanVienService is null. Không thể gọi phương thức từ xa.");
            return null;
        }
        try {
            return nhanVienService.getAllNhanVien();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi gọi getAllNhanVien: " + e.getMessage(), e);
            return null;
        }
    }

    public List<NhanVien> findNhanVienByName(String name) {
        if (nhanVienService == null) {
            LOGGER.severe("NhanVienService is null. Không thể gọi phương thức từ xa.");
            return null;
        }
        try {
            return nhanVienService.findNhanVienByName(name);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi gọi findNhanVienByName: " + e.getMessage(), e);
            return null;
        }
    }

    public List<NhanVien> findNhanVienBySdt(String sdt) {
        if (nhanVienService == null) {
            LOGGER.severe("NhanVienService is null. Không thể gọi phương thức từ xa.");
            return null;
        }
        try {
            return nhanVienService.findNhanVienBySdt(sdt);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi gọi findNhanVienBySdt: " + e.getMessage(), e);
            return null;
        }
    }
    //---------------Chi tiet phieu dat----------
    public ChiTietPhieuDat getChiTietPhieuDatDangO(String maPhong) {
        try {
            return chiTietPhieuDatService.findChiTietPhieuDatDangO(maPhong);
        } catch (Exception e) {
            System.err.println("Lỗi khi gọi findChiTietPhieuDatDangO: " + e.getMessage());
            return null;
        }
    }
    public boolean createCTPhieuDat(ChiTietPhieuDat ctpd) {
        try {
            return chiTietPhieuDatService.createCTPhieuDat(ctpd);
        } catch (Exception e) {
            System.err.println("Lỗi gọi createCTPhieuDat: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public List<ChiTietPhieuDat> timPhongDaDat() {
        try {
            return chiTietPhieuDatService.findPhongDaDat();
        } catch (Exception e) {
            System.err.println("Lỗi khi tìm phòng đã đặt: " + e.getMessage());
            return null;
        }
    }
    //------------phieu dat ------------
    public PhieuDatPhong createPhieuDatPhong(PhieuDatPhong pd) {
        try {
            // Gọi phương thức createPhieuDatPhong từ service và nhận lại đối tượng PhieuDatPhong
            PhieuDatPhong phieuDatPhong = phieuDatService.createPhieuDatPhong(pd);

            // Kiểm tra nếu phieuDatPhong không null, tức là đã tạo thành công
            if (phieuDatPhong != null) {
                return phieuDatPhong; // Trả về đối tượng PhieuDatPhong đã tạo
            } else {
                return null; // Trả về null nếu có lỗi
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            return null; // Trả về null nếu có lỗi
        }
    }

    public PhieuDatPhong layPhieuDatTheoMaKhachHangVaNgayDat(String maKH, String ngayDat) {
        try {
            return phieuDatService.layPhieuDatTheoMaKhachHangVaNgayDat(maKH, ngayDat);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }


//    public boolean deletePhieuDatPhongByMa(String maPhieuDat) {
//        try {
//            return phieuDatService.deletePhieuDatPhongByMa(maPhieuDat);
//        } catch (RemoteException e) {
//            System.err.println("Lỗi gọi deletePhieuDatPhongByMa: " + e.getMessage());
//            return false;
//        }
//    }
    public PhieuDatPhong layPhieuDatTheoMa(String maPhieuDat) {
        try {
            return phieuDatService.getPhieuDatPhongByMa(maPhieuDat);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void main(String[] args) {
        RMIClient client = new RMIClient();

        // Test NHÂN VIÊN
        LOGGER.info("=== TEST NHÂN VIÊN ===");
        // Thêm nhân viên mới
        NhanVien nhanVien = new NhanVien();
        nhanVien.setHoTenNhanVien("Nguyen Van Test");
        nhanVien.setDiaChi("123 Duong Test, TP HCM");
        nhanVien.setSoDienThoai("0987654321");
        nhanVien.setEmail("test@gmail.com");
        nhanVien.setGioiTinh(true);
        nhanVien.setNgaySinh(LocalDate.of(1990, 1, 1));
        nhanVien.setChucVu(false);
        nhanVien.setTrangThai(true);
        nhanVien.setHinhanhNV("avatar_test.jpg");
        String maNhanVien = client.addNhanVien(nhanVien);
        if (maNhanVien != null) {
            LOGGER.info("Thêm nhân viên thành công, mã: " + maNhanVien);
        } else {
            LOGGER.info("Thêm nhân viên thất bại!");
        }

        // Cập nhật nhân viên
        nhanVien.setMaNhanVien(maNhanVien);
        nhanVien.setHoTenNhanVien("Nguyen Van Test Updated");
        boolean updateResult = client.updateNhanVien(nhanVien);
        LOGGER.info("Cập nhật nhân viên: " + (updateResult ? "Thành công" : "Thất bại"));

        // Tìm nhân viên theo mã
        NhanVien nhanVienByMa = client.findNhanVienByMa(maNhanVien);
        if (nhanVienByMa != null) {
            LOGGER.info("Nhân viên tìm thấy: " + nhanVienByMa);
        } else {
            LOGGER.info("Không tìm thấy nhân viên với mã " + maNhanVien);
        }

        // Tìm nhân viên theo tên
        List<NhanVien> nhanVienByName = client.findNhanVienByName("Test");
        if (nhanVienByName != null && !nhanVienByName.isEmpty()) {
            LOGGER.info("Danh sách nhân viên theo tên 'Test':");
            for (NhanVien nv : nhanVienByName) {
                LOGGER.info(" - " + nv);
            }
        } else {
            LOGGER.info("Không tìm thấy nhân viên với tên Test");
        }

        // Tìm nhân viên theo số điện thoại
        List<NhanVien> nhanVienBySdt = client.findNhanVienBySdt("0987654321");
        if (nhanVienBySdt != null && !nhanVienBySdt.isEmpty()) {
            LOGGER.info("Danh sách nhân viên theo số điện thoại '0987654321':");
            for (NhanVien nv : nhanVienBySdt) {
                LOGGER.info(" - " + nv);
            }
        } else {
            LOGGER.info("Không tìm thấy nhân viên với số điện thoại 0987654321");
        }

        // Xem tất cả nhân viên
        List<NhanVien> allNhanVien = client.getAllNhanVien();
        if (allNhanVien != null && !allNhanVien.isEmpty()) {
            LOGGER.info("Danh sách tất cả nhân viên:");
            for (NhanVien nv : allNhanVien) {
                LOGGER.info(" - " + nv);
            }
        } else {
            LOGGER.info("Danh sách nhân viên trống hoặc lỗi khi lấy dữ liệu.");
        }

        // Xóa nhân viên
        boolean deleteResult = client.deleteNhanVien(maNhanVien);
        LOGGER.info("Xóa nhân viên: " + (deleteResult ? "Thành công" : "Thất bại"));

        // Test KHÁCH HÀNG
        LOGGER.info("\n=== TEST KHÁCH HÀNG ===");
        // Tạo khách hàng ngẫu nhiên
        boolean createKhachHangResult = client.createRandomKhachHang(5);
        LOGGER.info("Tạo 5 khách hàng ngẫu nhiên: " + (createKhachHangResult ? "Thành công" : "Thất bại"));

        // Cập nhật khách hàng
        boolean updateKhachHangResult = client.capNhatKhachHang(
                "KH001", "Nguyen Van A", true, "123456789012",
                LocalDate.of(1990, 1, 1), "0987654321", false
        );
        LOGGER.info("Cập nhật khách hàng KH001: " + (updateKhachHangResult ? "Thành công" : "Thất bại"));

        // Tìm khách hàng theo số điện thoại
        List<KhachHang> khachHangList = client.getKhachHangBySoDienThoai("0987654321");
        if (khachHangList != null && !khachHangList.isEmpty()) {
            LOGGER.info("Danh sách khách hàng với số điện thoại 0987654321:");
            for (KhachHang kh : khachHangList) {
                LOGGER.info(" - " + kh);
            }
        } else {
            LOGGER.info("Không tìm thấy khách hàng với số điện thoại 0987654321");
        }

        // Test PHÒNG
        LOGGER.info("\n=== TEST PHÒNG ===");
        // Xem phòng trống
        List<Phong> danhSachPhongTrong = client.getPhongByTrangThai(TrangThaiPhong.TRONG);
        if (danhSachPhongTrong != null && !danhSachPhongTrong.isEmpty()) {
            LOGGER.info("Danh sách phòng trống:");
            for (Phong phong : danhSachPhongTrong) {
                LOGGER.info(" - Mã phòng: " + phong.getMaPhong() + ", Số phòng: " + phong.getSoPhong());
            }
        } else {
            LOGGER.info("Không có phòng trống hoặc lỗi khi lấy dữ liệu.");
        }

        // Cập nhật phòng
        boolean updatePhongResult = client.capNhatPhong("PA01", 350000.0, TrangThaiPhong.TRONG);
        LOGGER.info("Cập nhật phòng PA01: " + (updatePhongResult ? "Thành công" : "Thất bại"));

        // Tìm phòng theo loại
        List<Phong> danhSachLoaiA = client.getPhongByLoaiPhong(LoaiPhong.MOT_GIUONG_DON);
        if (danhSachLoaiA != null && !danhSachLoaiA.isEmpty()) {
            LOGGER.info("Danh sách phòng loại một giường đơn:");
            for (Phong phong : danhSachLoaiA) {
                LOGGER.info(" - " + phong.getMaPhong());
            }
        } else {
            LOGGER.info("Không tìm thấy phòng loại một giường đơn.");
        }

        // Test HÓA ĐƠN
        LOGGER.info("\n=== TEST HÓA ĐƠN ===");
        // Tạo hóa đơn ngẫu nhiên
        boolean taoHoaDonResult = client.taoHoaDonNgauNhien(3);
        LOGGER.info("Tạo 3 hóa đơn ngẫu nhiên: " + (taoHoaDonResult ? "Thành công" : "Thất bại"));

        // Tính tổng tiền hóa đơn
        double tongTien = client.tinhTongTienHoaDon("HD001");
        if (tongTien >= 0) {
            LOGGER.info("Tổng tiền hóa đơn HD001: " + tongTien);
        } else {
            LOGGER.info("Không tính được tổng tiền cho hóa đơn HD001");
        }

        // Xem tất cả hóa đơn
        List<HoaDon> danhSachHoaDon = client.getAllHoaDon();
        if (danhSachHoaDon != null && !danhSachHoaDon.isEmpty()) {
            LOGGER.info("Danh sách hóa đơn:");
            for (HoaDon hd : danhSachHoaDon) {
                double tong = client.tinhTongTienHoaDon(hd.getMaHoaDon());
                LOGGER.info("- Mã hóa đơn: " + hd.getMaHoaDon() + ", Ngày lập: " + hd.getNgayLapHoaDon() + ", Tổng tiền: " + tong);
            }
        } else {
            LOGGER.info("Không có hóa đơn nào.");
        }

        // Test ĐỒ ĂN/THỨC UỐNG
        LOGGER.info("\n=== TEST ĐỒ ĂN/THỨC UỐNG ===");
        // Tìm đồ ăn/thức uống theo mã
        DoAnThucUong sp = client.findDoAnThucUongByMa("DV31012368");
        if (sp != null) {
            LOGGER.info("Tìm thấy sản phẩm: " + sp.getMaSanPham() + ", Tên: " + sp.getTenSanPham() + ", Giá: " + sp.getGiaBan());
        } else {
            LOGGER.info("Không tìm thấy đồ ăn/thức uống với mã DV31012368");
        }

        // Xem tất cả đồ ăn/thức uống
        List<DoAnThucUong> doAnThucUongs = client.getAllDoAnThucUong();
        if (doAnThucUongs != null && !doAnThucUongs.isEmpty()) {
            LOGGER.info("Danh sách đồ ăn/thức uống:");
            for (DoAnThucUong datu : doAnThucUongs) {
                LOGGER.info(" - Mã: " + datu.getMaSanPham() + ", Tên: " + datu.getTenSanPham() + ", Giá: " + datu.getGiaBan());
            }
        } else {
            LOGGER.info("Không có đồ ăn/thức uống nào.");
        }

        // Test CHI TIẾT HÓA ĐƠN
        LOGGER.info("\n=== TEST CHI TIẾT HÓA ĐƠN ===");
        // Tạo chi tiết hóa đơn ngẫu nhiên
        boolean taoChiTietHoaDonResult = client.createRandomChiTietHoaDon(1);
        LOGGER.info("Tạo chi tiết hóa đơn ngẫu nhiên: " + (taoChiTietHoaDonResult ? "Thành công" : "Thất bại"));

        // Xem tất cả chi tiết hóa đơn
        List<ChiTietHoaDon> chiTietHoaDons = client.getAllChiTietHoaDon();
        if (chiTietHoaDons != null && !chiTietHoaDons.isEmpty()) {
            LOGGER.info("Danh sách chi tiết hóa đơn:");
            for (ChiTietHoaDon cthd : chiTietHoaDons) {
                LOGGER.info(" - Mã hóa đơn: " + (cthd.getMaHoaDon() != null ? cthd.getMaHoaDon().getMaHoaDon() : "N/A") +
                        ", Mã phòng: " + (cthd.getMaPhong() != null ? cthd.getMaPhong().getMaPhong() : "N/A") +
                        ", Số lượng: " + cthd.getSoLuong());
            }
        } else {
            LOGGER.info("Không có chi tiết hóa đơn nào.");
        }
    }

}


