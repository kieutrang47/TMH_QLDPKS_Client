package entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter

@ToString(exclude = {"maNhanVien", "maKhachHang", "chiTietPhieuDats", "hoaDons"}) // tránh vòng lặp

@Entity
@Table(name = "PhieuDatPhong", schema = "TMH_QLKS")
public class PhieuDatPhong implements Serializable {
    @Id
    @Column(name = "maPhieuDat", nullable = false)
    private String maPhieuDat;

    @Column(name = "kieuDat", nullable = false)
    private Boolean kieuDat = false;

    @Column(name = "ngayDatPhong", nullable = false)
    private LocalDate ngayDatPhong;

    @Column(name = "soLuongNguoi", nullable = false)
    private Integer soLuongNguoi;

    @Column(name = "traTruoc")
    private Double traTruoc;

    // Quan hệ Many-to-One với KhachHang
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maKhachHang")
    private KhachHang maKhachHang;

    // Quan hệ Many-to-One với NhanVien
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maNhanVien")
    private NhanVien maNhanVien;

    // Quan hệ One-to-Many với ChiTietPhieuDat
    @OneToMany(mappedBy = "maPhieuDat", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChiTietPhieuDat> chiTietPhieuDats = new LinkedHashSet<>();

    // Quan hệ One-to-Many với HoaDon
    @OneToMany(mappedBy = "maPhieuDat", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HoaDon> hoaDons = new LinkedHashSet<>();

    public PhieuDatPhong() {

    }
    public PhieuDatPhong(String maPhieuDat, KhachHang maKhachHang, NhanVien maNhanVien,
                         LocalDate ngayDatPhong, Boolean kieuDat, Integer soLuongNguoi, Double traTruoc) {
        this.maPhieuDat = maPhieuDat;  // Nếu bạn muốn truyền giá trị này, nếu không có thể bỏ qua
        this.maKhachHang = maKhachHang;
        this.maNhanVien = maNhanVien;
        this.ngayDatPhong = ngayDatPhong;
        this.kieuDat = kieuDat;
        this.soLuongNguoi = soLuongNguoi;
        this.traTruoc = traTruoc;
    }


    @Override
    public String toString() {
        return this.maPhieuDat; // hoặc return mã
    }
    public double getTienPhong(ChiTietPhieuDat chiTietPhieuDat, double traTruoc) {
        double tienGio = 0;
        double tienNgay = 0;

        LocalDateTime ngayNhanPhong = LocalDateTime.of(chiTietPhieuDat.getNgayNhanPhong(), chiTietPhieuDat.getGioNhanPhong());
        LocalDateTime ngayTraPhong = LocalDateTime.of(chiTietPhieuDat.getNgayTraPhong(), chiTietPhieuDat.getGioTraPhong());
        Duration duration = Duration.between(ngayNhanPhong, ngayTraPhong);
        long soGio = duration.toHours();   // Số giờ giữa hai thời điểm
        long soNgay = duration.toDays();   // Số ngày giữa hai thời điểm
        if (soNgay == 0) soNgay = 1;       // Nếu trong cùng ngày thì vẫn tính là 1 ngày

        int loaiPhong = chiTietPhieuDat.getPhong().getLoaiPhong().getTenLoai();

        if (kieuDat == true) { // Đặt theo giờ
            switch (loaiPhong) {
                case 1:
                    if (soGio == 1) tienGio = 200000;
                    else if (soGio == 2) tienGio = 200000 * 2 * 0.8;
                    else if (soGio == 3) tienGio = 200000 * 3 * 0.7;
                    else tienGio = 200000 * soGio * 0.6;
                    break;
                case 2:
                    if (soGio == 1) tienGio = 350000;
                    else if (soGio == 2) tienGio = 350000 * 2 * 0.8;
                    else if (soGio == 3) tienGio = 350000 * 3 * 0.7;
                    else tienGio = 350000 * soGio * 0.6;
                    break;
                case 3:
                    if (soGio == 1) tienGio = 500000;
                    else if (soGio == 2) tienGio = 500000 * 2 * 0.8;
                    else if (soGio == 3) tienGio = 500000 * 3 * 0.7;
                    else tienGio = 500000 * soGio * 0.6;
                    break;
                case 4:
                    if (soGio == 1) tienGio = 300000;
                    else if (soGio == 2) tienGio = 300000 * 2 * 0.8;
                    else if (soGio == 3) tienGio = 300000 * 3 * 0.7;
                    else tienGio = 300000 * soGio * 0.6;
                    break;
                default:
                    System.out.println("Loại phòng không hợp lệ.");
                    return 0;
            }
            return tienGio - traTruoc;
        } else { // Đặt theo ngày
            switch (loaiPhong) {
                case 1:
                    tienNgay = 2400000 * soNgay;
                    break;
                case 2:
                    tienNgay = 4200000 * soNgay;
                    break;
                case 3:
                    tienNgay = 6000000 * soNgay;
                    break;
                case 4:
                    tienNgay = 3600000 * soNgay;
                    break;
                default:
                    System.out.println("Loại phòng không hợp lệ.");
                    return 0;
            }
            return tienNgay - traTruoc;
        }
    }






}