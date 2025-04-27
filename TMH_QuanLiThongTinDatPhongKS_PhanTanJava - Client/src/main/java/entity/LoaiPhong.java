package entity;

import java.io.Serializable;

/**
 * @author lehoann
 * @version 1.0
 * @created 14-Jan-2025 10:51:44 AM
 *
 */
public enum LoaiPhong implements Serializable {
	MOT_GIUONG_DON(1),
	HAI_GIUONG_DON(2),
	MOT_DON_MOT_DOI(3),
	MOT_GIUONG_DOI(4);

	private final int tenLoai;

    private LoaiPhong(int tenLoai) {
        this.tenLoai = tenLoai;
    }

    public int getTenLoai() {
        return tenLoai;
    }
 // Phương thức chuyển từ int sang enum
    public static LoaiPhong fromInt(int tenLoai) {
        for (LoaiPhong loaiPhong : LoaiPhong.values()) {
            if (loaiPhong.getTenLoai() == tenLoai) {
                return loaiPhong;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy loại phòng với giá trị: " + tenLoai);
    }
	

}