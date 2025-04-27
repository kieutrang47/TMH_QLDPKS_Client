package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Table(name = "Phong", schema = "TMH_QLKS")
public class Phong implements Serializable {
    @Id
    @Column(name = "maPhong", nullable = false)
    private String maPhong;

    @Column(name = "giaPhong", nullable = false)
    private Double giaPhong;

    @Column(name = "hinhAnhPhong", nullable = false)
    private String hinhAnhPhong;

    @Enumerated(EnumType.STRING)
    @Column(name = "loaiPhong", nullable = false)
    private LoaiPhong loaiPhong;

    @Column(name = "soGiuong", nullable = false)
    private Integer soGiuong;

    @Column(name = "soPhong", nullable = false)
    private String soPhong;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangThaiPhong", nullable = false)
    private TrangThaiPhong trangThaiPhong;

    // Quan hệ 1-N: 1 Phong có nhiều ChiTietHoaDon
    @OneToMany(mappedBy = "maPhong", fetch = FetchType.LAZY)
    private Set<ChiTietHoaDon> chiTietHoaDons = new LinkedHashSet<>();

    // Quan hệ 1-N: 1 Phong có nhiều ChiTietPhieuDat
    @OneToMany(mappedBy = "phong", fetch = FetchType.LAZY)
    private Set<ChiTietPhieuDat> chiTietPhieuDats = new LinkedHashSet<>();

    @PrePersist
    public void generateMaPhong() {
        if (this.maPhong == null || this.maPhong.isEmpty()) {
            this.maPhong = "P" + soPhong;
        }
    }

    public void setLoaiPhong(LoaiPhong loaiPhong) {
        this.loaiPhong = loaiPhong;
        switch (loaiPhong) {
            case MOT_GIUONG_DON:
                this.soGiuong = 1;
                this.giaPhong = 200000.0;
                break;
            case MOT_GIUONG_DOI:
                this.soGiuong = 1;
                this.giaPhong = 300000.0;
                break;
            case HAI_GIUONG_DON:
                this.soGiuong = 2;
                this.giaPhong = 350000.0;
                break;
            case MOT_DON_MOT_DOI:
                this.soGiuong = 2;
                this.giaPhong = 500000.0;
                break;
            default:
                throw new IllegalArgumentException("Loại phòng không hợp lệ!");
        }
    }
}