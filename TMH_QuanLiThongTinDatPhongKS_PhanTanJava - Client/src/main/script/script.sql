select * from chitietphieudat ;

select * from phong;
SELECT *
FROM Phong
WHERE maPhong = 'P205' AND trangThaiPhong = 'TRONG';
select * from khachhang;
SELECT *
FROM ChiTietPhieuDat
WHERE maPhong = 'P205';

SELECT *
FROM PhieuDatPhong
WHERE maPhieuDat IN (
    SELECT maPhieuDat
    FROM ChiTietPhieuDat
    WHERE maPhong = 'P205'
);
select * from khachhang where maKhachHang="KH0013";


------
SELECT * FROM chitietphieudat;

SELECT * FROM HoaDon;
SELECT * FROM ChiTietHoaDon;

DESCRIBE HoaDon;
SELECT * FROM HoaDon WHERE maHoaDon LIKE '%HD051224455%';

-----
-- 0
SET foreign_key_checks = 0;
-- 1. Thêm cột maChiTietPhieuDat
ALTER TABLE ChiTietPhieuDat
    ADD COLUMN maChiTietPhieuDat INT AUTO_INCREMENT PRIMARY KEY FIRST;


-- 2. Bỏ ràng buộc khóa chính trên ma_phong
ALTER TABLE chitietphieudat
    DROP PRIMARY KEY;

ALTER TABLE ChiTietPhieuDat
    DROP PRIMARY KEY;
DESCRIBE ChiTietPhieuDat;
DESCRIBE phong;


-- 3. Đặt maChiTietPhieuDat làm khóa chính
ALTER TABLE chitietphieudat
    ADD PRIMARY KEY (maChiTietPhieuDat);

-- 4. Đảm bảo ma_phong là khóa ngoại
ALTER TABLE ChiTietPhieuDat
    ADD CONSTRAINT fk_maPhong
        FOREIGN KEY (maPhong) REFERENCES phong(maPhong);
-- 5
SET foreign_key_checks = 1;
-- 6
SHOW FULL COLUMNS FROM ChiTietPhieuDat;
SHOW CREATE TABLE phong;