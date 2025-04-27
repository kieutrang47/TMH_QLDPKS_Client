package dto;

import entity.HoaDon;

import java.io.Serializable;
import java.time.LocalDate;

public class HoaDonDTO implements Serializable {
    private String maHoaDon;
    private LocalDate ngayLapHoaDon;
    private double thue;
    private String maNhanVien;
    private String maKhachHang;
    private String maPhieuDat;
    private String tenNhanVien;
    private String tenKhachHang;


    public HoaDonDTO(HoaDon hd) {
        this.maHoaDon = hd.getMaHoaDon();
        this.ngayLapHoaDon = hd.getNgayLapHoaDon();
        this.thue = hd.getThue();

        if (hd.getMaNhanVien() != null) {
            this.maNhanVien = hd.getMaNhanVien().getMaNhanVien();
            this.tenNhanVien = hd.getMaNhanVien().getHoTenNhanVien(); // THÊM
        }

        if (hd.getMaKhachHang() != null) {
            this.maKhachHang = hd.getMaKhachHang().getMaKhachHang();
            this.tenKhachHang = hd.getMaKhachHang().getTenKhachHang(); // THÊM
        }

        if (hd.getMaPhieuDat() != null) {
            this.maPhieuDat = hd.getMaPhieuDat().getMaPhieuDat();
        }
    }

    // Getters
    public String getMaHoaDon() { return maHoaDon; }
    public LocalDate getNgayLapHoaDon() { return ngayLapHoaDon; }
    public double getThue() { return thue; }
    public String getMaNhanVien() { return maNhanVien; }
    public String getMaKhachHang() { return maKhachHang; }
    public String getMaPhieuDat() { return maPhieuDat; }
    public String getTenNhanVien() { return tenNhanVien; }
    public String getTenKhachHang() { return tenKhachHang; }

}

