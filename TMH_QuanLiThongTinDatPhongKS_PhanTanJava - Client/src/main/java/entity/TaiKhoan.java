package entity;

import jakarta.persistence.*;

import lombok.Data;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
/**
 * @author lehoann
 * @version 1.0
 * @created 14-Jan-2025 10:51:44 AM
 *
 */
@Setter
@Getter
@Entity
@Table(name = "TaiKhoan", schema = "TMH_QLKS")
public class TaiKhoan implements Serializable {
    @Id
    @Column(name = "userName", nullable = false)
    private String userName;

    @Column(name = "matKhau", nullable = false)
    private String matKhau;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maNhanVien", referencedColumnName = "maNhanVien", nullable = false)
    private NhanVien maNhanVien;

    // Constructor với tham số

    public TaiKhoan(String userName, String matKhau, NhanVien maNhanVien) {
        this.userName = userName;
        this.matKhau = matKhau;
        this.maNhanVien = maNhanVien;
    }

    public TaiKhoan() {

    }
}