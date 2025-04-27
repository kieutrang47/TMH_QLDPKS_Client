package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "DoAnThucUong", schema = "TMH_QLKS")
public class DoAnThucUong implements Serializable {
    @Id
    @Column(name = "maSanPham", nullable = false, length = 10)
    private String maSanPham;

    @Column(name = "giaBan", nullable = false)
    private Double giaBan;

    @Column(name = "giaNhap", nullable = false)
    private Double giaNhap;

    @Column(name = "hanSuDung", nullable = false)
    private LocalDate hanSuDung;

    @Column(name = "loaiSanPham", nullable = false)
    private Boolean loaiSanPham = false;

    @Column(name = "ngaySanXuat", nullable = false)
    private LocalDate ngaySanXuat;

    @Column(name = "soLuong", nullable = false)
    private Integer soLuong;

    @Column(name = "tenSanPham", nullable = false)
    private String tenSanPham;

    @OneToMany(mappedBy = "maDoAnThucUong")
    private Set<ChiTietHoaDon> chiTietHoaDons = new LinkedHashSet<>();

}