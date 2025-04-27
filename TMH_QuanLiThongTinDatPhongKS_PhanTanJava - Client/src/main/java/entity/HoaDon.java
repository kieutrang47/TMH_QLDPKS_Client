package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "HoaDon", schema = "TMH_QLKS")
public class HoaDon implements Serializable {
    @Id
    @Column(name = "maHoaDon", nullable = false)
    private String maHoaDon;

    @Column(name = "ngayLapHoaDon", nullable = false)
    private LocalDate ngayLapHoaDon;

    @Column(name = "thue", nullable = false)
    private Double thue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maKhachHang")
    private entity.KhachHang maKhachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maNhanVien")
    private entity.NhanVien maNhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maPhieuDat")
    private entity.PhieuDatPhong maPhieuDat;

    // Quan hệ 1-N: 1 HoaDon có nhiều ChiTietHoaDon
    @OneToMany(mappedBy = "maHoaDon", fetch = FetchType.LAZY)
    private Set<ChiTietHoaDon> chiTietHoaDons;
}
