package entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "ChiTietPhieuDat", schema = "TMH_QLKS")
public class ChiTietPhieuDat implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maChiTietPhieuDat", nullable = false)
    private Long maChiTietPhieuDat; // Khóa chính mới, tự tăng

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "maPhong", nullable = false)
    private Phong phong; // maPhong giờ là khóa ngoại

    @Column(name = "gioNhanPhong", nullable = false)
    private LocalTime gioNhanPhong;

    @Column(name = "gioTraPhong", nullable = false)
    private LocalTime gioTraPhong;

    @Column(name = "ngayNhanPhong", nullable = false)
    private LocalDate ngayNhanPhong;

    @Column(name = "ngayTraPhong", nullable = false)
    private LocalDate ngayTraPhong;

    @Column(name = "soGio", nullable = false)
    private Integer soGio;

    @Column(name = "trangThaiChiTiet", nullable = false)
    private Boolean trangThaiChiTiet = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "maPhieuDat", nullable = false)
    private PhieuDatPhong maPhieuDat;

    public ChiTietPhieuDat() {
    }

    public ChiTietPhieuDat(Phong phong, PhieuDatPhong maPhieuDat, Boolean trangThaiChiTiet,
                           LocalDate ngayNhanPhong, LocalTime gioNhanPhong,
                           LocalDate ngayTraPhong, LocalTime gioTraPhong, Integer soGio) {
        this.phong = phong;
        this.maPhieuDat = maPhieuDat;
        this.trangThaiChiTiet = trangThaiChiTiet;
        this.ngayNhanPhong = ngayNhanPhong;
        this.gioNhanPhong = gioNhanPhong;
        this.ngayTraPhong = ngayTraPhong;
        this.gioTraPhong = gioTraPhong;
        this.soGio = soGio;
    }
}