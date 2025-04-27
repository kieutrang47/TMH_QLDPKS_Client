package util;

import entity.TaiKhoan;
import java.util.logging.Logger;

public class TaiKhoanDangNhap {
    private static final Logger LOGGER = Logger.getLogger(TaiKhoanDangNhap.class.getName());
    private static final ThreadLocal<TaiKhoan> taiKhoanHolder = new ThreadLocal<>();

    private TaiKhoanDangNhap() {
        // Ngăn khởi tạo
    }

    public static void setTaiKhoan(TaiKhoan taiKhoan) {
        if (taiKhoan == null) {
            LOGGER.warning("Cố gắng gán tài khoản null vào TaiKhoanDangNhap");
            return;
        }
        taiKhoanHolder.set(taiKhoan);
        LOGGER.info("Đã gán tài khoản: " + taiKhoan.getUserName() + " cho thread " + Thread.currentThread().getName());
    }

    public static TaiKhoan getTaiKhoan() {
        return taiKhoanHolder.get();
    }

    public static void clear() {
        TaiKhoan taiKhoan = taiKhoanHolder.get();
        if (taiKhoan != null) {
            LOGGER.info("Xóa tài khoản: " + taiKhoan.getUserName() + " khỏi thread " + Thread.currentThread().getName());
            taiKhoanHolder.remove();
        }
    }
}