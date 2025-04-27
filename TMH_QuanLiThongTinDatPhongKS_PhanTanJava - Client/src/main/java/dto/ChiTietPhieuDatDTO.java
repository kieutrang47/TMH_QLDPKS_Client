package dto;



import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class ChiTietPhieuDatDTO implements Serializable {
    private Long maChiTietPhieuDat;
    private String maPhong;
    private String tenPhong;
    private String maPhieuDat;
    private LocalTime gioNhanPhong;
    private LocalTime gioTraPhong;
    private LocalDate ngayNhanPhong;
    private LocalDate ngayTraPhong;
    private Integer soGio;
    private Boolean trangThaiChiTiet;

    public ChiTietPhieuDatDTO() {
    }

    // Full constructor
    public ChiTietPhieuDatDTO(Long maChiTietPhieuDat, String maPhong, String tenPhong, String maPhieuDat,
                              LocalTime gioNhanPhong, LocalTime gioTraPhong,
                              LocalDate ngayNhanPhong, LocalDate ngayTraPhong,
                              Integer soGio, Boolean trangThaiChiTiet) {
        this.maChiTietPhieuDat = maChiTietPhieuDat;
        this.maPhong = maPhong;
        this.tenPhong = tenPhong;
        this.maPhieuDat = maPhieuDat;
        this.gioNhanPhong = gioNhanPhong;
        this.gioTraPhong = gioTraPhong;
        this.ngayNhanPhong = ngayNhanPhong;
        this.ngayTraPhong = ngayTraPhong;
        this.soGio = soGio;
        this.trangThaiChiTiet = trangThaiChiTiet;
    }

    // Convert từ entity → DTO
    public ChiTietPhieuDatDTO(entity.ChiTietPhieuDat ct) {
        this.maChiTietPhieuDat = ct.getMaChiTietPhieuDat();
        this.maPhong = ct.getPhong().getMaPhong();
        this.tenPhong = ct.getPhong().getMaPhong();
        this.maPhieuDat = ct.getMaPhieuDat().getMaPhieuDat();
        this.gioNhanPhong = ct.getGioNhanPhong();
        this.gioTraPhong = ct.getGioTraPhong();
        this.ngayNhanPhong = ct.getNgayNhanPhong();
        this.ngayTraPhong = ct.getNgayTraPhong();
        this.soGio = ct.getSoGio();
        this.trangThaiChiTiet = ct.getTrangThaiChiTiet();
    }

    // Getters + Setters
    public Long getMaChiTietPhieuDat() { return maChiTietPhieuDat; }
    public void setMaChiTietPhieuDat(Long maChiTietPhieuDat) { this.maChiTietPhieuDat = maChiTietPhieuDat; }

    public String getMaPhong() { return maPhong; }
    public void setMaPhong(String maPhong) { this.maPhong = maPhong; }

    public String getTenPhong() { return tenPhong; }
    public void setTenPhong(String tenPhong) { this.tenPhong = tenPhong; }

    public String getMaPhieuDat() { return maPhieuDat; }
    public void setMaPhieuDat(String maPhieuDat) { this.maPhieuDat = maPhieuDat; }

    public LocalTime getGioNhanPhong() { return gioNhanPhong; }
    public void setGioNhanPhong(LocalTime gioNhanPhong) { this.gioNhanPhong = gioNhanPhong; }

    public LocalTime getGioTraPhong() { return gioTraPhong; }
    public void setGioTraPhong(LocalTime gioTraPhong) { this.gioTraPhong = gioTraPhong; }

    public LocalDate getNgayNhanPhong() { return ngayNhanPhong; }
    public void setNgayNhanPhong(LocalDate ngayNhanPhong) { this.ngayNhanPhong = ngayNhanPhong; }

    public LocalDate getNgayTraPhong() { return ngayTraPhong; }
    public void setNgayTraPhong(LocalDate ngayTraPhong) { this.ngayTraPhong = ngayTraPhong; }

    public Integer getSoGio() { return soGio; }
    public void setSoGio(Integer soGio) { this.soGio = soGio; }

    public Boolean getTrangThaiChiTiet() { return trangThaiChiTiet; }
    public void setTrangThaiChiTiet(Boolean trangThaiChiTiet) { this.trangThaiChiTiet = trangThaiChiTiet; }
}

