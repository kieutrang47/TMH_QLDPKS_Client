package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@ToString(exclude = {"hoaDons", "phieuDatPhongs"})
@Entity
@Table(name = "KhachHang", schema = "TMH_QLKS")
public class KhachHang implements Serializable {
    @Id
    @Column(name = "maKhachHang", nullable = false)
    private String maKhachHang;

    @Column(name = "CCCD", nullable = false)
    private String cccd;

    /**
     * Giới tính: true = Nam, false = Nữ
     */
    @Column(name = "gioiTinh", nullable = false)
    private Boolean gioiTinh = false;

    /**
     * Loại khách hàng: true = Khách lẻ, false = Khách quen
     */
    @Column(name = "loaiKhachHang", nullable = false)
    private Boolean loaiKhachHang = false;

    @Column(name = "ngaySinh", nullable = false)
    private LocalDate ngaySinh;

    @Column(name = "soDienThoai", nullable = false)
    private String soDienThoai;

    @Column(name = "tenKhachHang", nullable = false)
    private String tenKhachHang;

    @Transient
    @OneToMany(mappedBy = "maKhachHang")
    private Set<HoaDon> hoaDons = new LinkedHashSet<>();

    @Transient
    @OneToMany(mappedBy = "maKhachHang", cascade = CascadeType.ALL, orphanRemoval = true)

    private Set<PhieuDatPhong> phieuDatPhongs = new LinkedHashSet<>();

    @Override
    public String toString() {
        return this.tenKhachHang; // hoặc return mã
    }

}