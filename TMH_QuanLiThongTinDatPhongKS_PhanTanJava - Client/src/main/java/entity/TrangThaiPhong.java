package entity;

import java.io.Serializable;

/**
 * @author lehoann
 * @version 1.0
 * @created 14-Jan-2025 10:51:44 AM
 *
 */
public enum TrangThaiPhong implements Serializable {
    DA_DAT(1),
    TRONG(2),
    DANG_BAO_TRI(3),
    DA_DAT_TRUOC(4);

    private final int maTrangThai;

    private TrangThaiPhong(int maTrangThai) {
        this.maTrangThai = maTrangThai;
    }

    public int getMaTrangThai() {
        return maTrangThai;
    }

    // Phương thức chuyển từ int sang enum
    public static TrangThaiPhong fromInt(int maTrangThai) {
        for (TrangThaiPhong trangThai : TrangThaiPhong.values()) {
            if (trangThai.getMaTrangThai() == maTrangThai) {
                return trangThai;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy trạng thái phòng với mã: " + maTrangThai);
    }
}