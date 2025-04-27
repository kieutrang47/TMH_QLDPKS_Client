package util;

import rmi.NhanVienService;
import rmi.TaiKhoanService;
import rmi.PhongService;
import rmi.ChiTietPhieuDatService;
import rmi.PhieuDatService;
import rmi.KhachHangService;

public class ApplicationContext {
    private static ApplicationContext instance;
    private TaiKhoanService taiKhoanService;
    private NhanVienService nhanVienService;
    private PhongService phongService;
    private ChiTietPhieuDatService chiTietPhieuDatService;
    private PhieuDatService phieuDatService;
    private KhachHangService khachHangService;
    private boolean isRmiInitialized = false;

    private ApplicationContext() {
        System.out.println("ApplicationContext: Khởi tạo instance mới");
    }

    public static ApplicationContext getInstance() {
        if (instance == null) {
            System.out.println("ApplicationContext: Tạo instance mới (singleton)");
            instance = new ApplicationContext();
        }
        return instance;
    }

    public void setTaiKhoanService(TaiKhoanService service) {
        System.out.println("ApplicationContext: Lưu TaiKhoanService = " + service);
        this.taiKhoanService = service;
    }

    public TaiKhoanService getTaiKhoanService() {
        System.out.println("ApplicationContext: Truy xuất TaiKhoanService = " + taiKhoanService);
        return taiKhoanService;
    }

    public void setNhanVienService(NhanVienService service) {
        System.out.println("ApplicationContext: Lưu NhanVienService = " + service);
        this.nhanVienService = service;
    }

    public NhanVienService getNhanVienService() {
        System.out.println("ApplicationContext: Truy xuất NhanVienService = " + nhanVienService);
        return nhanVienService;
    }

    public void setPhongService(PhongService service) {
        System.out.println("ApplicationContext: Lưu PhongService = " + service);
        this.phongService = service;
    }

    public PhongService getPhongService() {
        System.out.println("ApplicationContext: Truy xuất PhongService = " + phongService);
        return phongService;
    }

    public void setChiTietPhieuDatService(ChiTietPhieuDatService service) {
        System.out.println("ApplicationContext: Lưu ChiTietPhieuDatService = " + service);
        this.chiTietPhieuDatService = service;
    }

    public ChiTietPhieuDatService getChiTietPhieuDatService() {
        System.out.println("ApplicationContext: Truy xuất ChiTietPhieuDatService = " + chiTietPhieuDatService);
        return chiTietPhieuDatService;
    }

    public void setPhieuDatService(PhieuDatService service) {
        System.out.println("ApplicationContext: Lưu PhieuDatService = " + service);
        this.phieuDatService = service;
    }

    public PhieuDatService getPhieuDatService() {
        System.out.println("ApplicationContext: Truy xuất PhieuDatService = " + phieuDatService);
        return phieuDatService;
    }

    public void setKhachHangService(KhachHangService service) {
        System.out.println("ApplicationContext: Lưu KhachHangService = " + service);
        this.khachHangService = service;
    }

    public KhachHangService getKhachHangService() {
        System.out.println("ApplicationContext: Truy xuất KhachHangService = " + khachHangService);
        return khachHangService;
    }

    public void setRmiInitialized(boolean initialized) {
        System.out.println("ApplicationContext: Lưu isRmiInitialized = " + initialized);
        this.isRmiInitialized = initialized;
    }

    public boolean isRmiInitialized() {
        System.out.println("ApplicationContext: Truy xuất isRmiInitialized = " + isRmiInitialized);
        return isRmiInitialized;
    }

    // Thêm phương thức để reset nếu cần (dùng cẩn thận)
    public void reset() {
        System.out.println("ApplicationContext: Reset trạng thái");
        this.taiKhoanService = null;
        this.nhanVienService = null;
        this.phongService = null;
        this.chiTietPhieuDatService = null;
        this.phieuDatService = null;
        this.khachHangService = null;
        this.isRmiInitialized = false;
    }
}