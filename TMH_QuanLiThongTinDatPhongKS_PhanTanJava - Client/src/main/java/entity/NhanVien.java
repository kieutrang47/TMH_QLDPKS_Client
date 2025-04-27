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

@ToString(exclude = "phieuDatPhongs")


@Entity
@Table(name = "NhanVien", schema = "TMH_QLKS")
public class NhanVien implements Serializable {
    @Id
    @Column(name = "maNhanVien", nullable = false)
    private String maNhanVien;

    @Column(name = "chucVu", nullable = false)
    private Boolean chucVu = false;

    @Column(name = "diaChi", nullable = false)
    private String diaChi;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "gioiTinh", nullable = false)
    private Boolean gioiTinh = false;

    @Column(name = "hinhanhNV", nullable = false)
    private String hinhanhNV;

    @Column(name = "hoTenNhanVien", nullable = false)
    private String hoTenNhanVien;

    @Column(name = "ngaySinh", nullable = false)
    private LocalDate ngaySinh;

    @Column(name = "soDienThoai", nullable = false)
    private String soDienThoai;

    @Column(name = "trangThai", nullable = false)
    private Boolean trangThai = false;



    @OneToMany(mappedBy = "maNhanVien", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PhieuDatPhong> phieuDatPhongs = new LinkedHashSet<>();

    @Override
    public String toString() {
        return this.hoTenNhanVien; // hoặc return mã
    }


}