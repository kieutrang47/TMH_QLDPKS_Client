package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "ChiTietHoaDon", schema = "TMH_QLKS")

public class ChiTietHoaDon implements Serializable {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maHoaDon", nullable = false)
    private HoaDon maHoaDon;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maPhong", nullable = false)
    private Phong maPhong;

    @Column(name = "ghiChu")
    private String ghiChu;

    @Column(name = "phuThu")
    private Double phuThu;

    @Column(name = "soGio", nullable = false)
    private Integer soGio;

    @Column(name = "soLuong", nullable = false)
    private Integer soLuong;

    @Column(name = "soPhongDat", nullable = false)
    private Integer soPhongDat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maDoAnThucUong")
    private DoAnThucUong maDoAnThucUong;

//    public double tinhTienThuePhong(List<ChiTietPhieuDat> chiTietPhieuDatList, double traTruoc) {
//        double totalTienPhong = 0;
//        for (ChiTietPhieuDat chiTietPhieuDat : chiTietPhieuDatList) {
//            totalTienPhong += maHoaDon.getMaPhieuDat().getTienPhong(chiTietPhieuDat, traTruoc); // Tính tiền thuê phòng cho từng chi tiết
//        }
//        return totalTienPhong + phuThu;
//    }
}
